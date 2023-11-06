package com.hisun.tower.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hisun.tower.entity.AlarmInstance;
import com.hisun.tower.mapper.AlarmInstanceMapper;
import com.hisun.tower.service.IAlarmGroupService;
import com.hisun.tower.service.IAlarmInstanceService;
import com.hisun.tower.util.EnumUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.email.EmailPopulatingBuilder;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class AlarmInstanceServiceImpl extends ServiceImpl<AlarmInstanceMapper, AlarmInstance> implements IAlarmInstanceService {

    private static final Function<Integer, TransportStrategy> DEFAULT_SERVE_PORT = EnumUtils.lookupMap(TransportStrategy.class, TransportStrategy::getDefaultServerPort);

    @Override
    public boolean testAlert(AlarmInstance alarmInstance) {

        Map<String, Object> params;
        JacksonJsonParser jacksonJsonParser = new JacksonJsonParser();
        params = jacksonJsonParser.parseMap(alarmInstance.getParams());

        int port = Integer.parseInt((String) params.get("serverPort"));
        TransportStrategy transportStrategy = DEFAULT_SERVE_PORT.apply(port);


        try (Mailer mailer = MailerBuilder
                .withSMTPServer((String) params.get("serverHost"), port, (String) params.get("user"), (String) params.get("password"))
                .withTransportStrategy(transportStrategy)
                .withSessionTimeout(10 * 1000)
                .buildMailer()) {
            mailer.testConnection();

        } catch (Exception e) {
            log.error("test connection failed", e);
            return false;
        }


        return true;
    }

    @Override
    public void sendEmail(String alarmId, String sceneName, String jobId, String alarm) {
        AlarmInstance alarmInstance = getById(alarmId);
        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setTemplateMode(TemplateMode.HTML); // HTML5 option was deprecated in 3.0.0
        templateEngine.setTemplateResolver(resolver);
        Context ct = new Context();
        ct.setVariable("name", sceneName);
        ct.setVariable("url", "http://10.9.75.236:18081/#/job/" + jobId +"/overview");
        ct.setVariable("alarm", alarm);
        String html = templateEngine.process("mail.html", ct);


        Map<String, Object> params;
        JacksonJsonParser jacksonJsonParser = new JacksonJsonParser();
        params = jacksonJsonParser.parseMap(alarmInstance.getParams());

        String sender = (String) params.get("sender");
        String user = (String) params.get("user");
        String password = (String) params.get("password");
        String receivers = (String) params.get("receivers");
        String receiverCcs = (String) params.get("receiverCcs");
        String serverHost = (String) params.get("serverHost");
        int serverPort = Integer.parseInt((String) params.get("serverPort"));
        TransportStrategy transportStrategy = DEFAULT_SERVE_PORT.apply(serverPort);


        Email serverLevelOverrides = EmailBuilder.startingBlank()
                .from(sender, user)
                .buildEmail();

        EmailPopulatingBuilder emailPopulatingBuilder = EmailBuilder.startingBlank()
                .to(receivers)
                .withSubject("场景【" + alarmInstance.getName() + "】：核对异常!")
//                .withPlainText("Please view this email in a modern email client!")
                .withHTMLText(html);

        if (StringUtils.isNotBlank(receiverCcs)) {
            emailPopulatingBuilder.cc(receiverCcs);
        }

        Email email = emailPopulatingBuilder.buildEmail();


        try (Mailer mailer = MailerBuilder
                .withSMTPServer(serverHost, serverPort, user, password)
                .withTransportStrategy(transportStrategy)
                .withEmailOverrides(serverLevelOverrides)
                .withSessionTimeout(10 * 1000)
                .buildMailer()) {
            mailer.sendMail(email);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
