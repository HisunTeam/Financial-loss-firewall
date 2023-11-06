package com.hisun.tower.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hisun.tower.entity.AlarmGroup;
import com.hisun.tower.entity.OracleSource;
import com.hisun.tower.entity.Rule;
import com.hisun.tower.entity.Scene;
import com.hisun.tower.mapper.SceneMapper;
import com.hisun.tower.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.JobID;
import org.apache.flink.client.deployment.StandaloneClusterId;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.PackagedProgramUtils;
import org.apache.flink.client.program.rest.RestClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class SceneServiceImpl extends ServiceImpl<SceneMapper, Scene> implements ISceneService {
    @Value("${tower.scene.flink-jar:/opt/finance-guard-0.0.1-SNAPSHOT-all.jar}")
    private String flinkJarFile;
    private final IRuleService iRuleService;
    private final IAlarmGroupService iAlarmGroupService;
    private final IAlarmInstanceService iAlarmInstanceService;
    private final IOracleSourceService iOracleSourceService;

    public SceneServiceImpl(IRuleService iRuleService, IAlarmGroupService iAlarmGroupService, IAlarmInstanceService iAlarmInstanceService, IOracleSourceService iOracleSourceService) {
        this.iRuleService = iRuleService;
        this.iAlarmGroupService = iAlarmGroupService;
        this.iAlarmInstanceService = iAlarmInstanceService;
        this.iOracleSourceService = iOracleSourceService;
    }

    @Override
    public void publish(Scene scene) {

        StringJoiner stringJoiner = new StringJoiner(" ");

        LambdaQueryWrapper<Rule> ruleLambdaQueryWrapper = Wrappers.lambdaQuery();
        ruleLambdaQueryWrapper.eq(Rule::getName, scene.getRuleName());
        Rule rule = iRuleService.getOne(ruleLambdaQueryWrapper);
        String firstType = rule.getFirstSourceType();
        if (StringUtils.equalsIgnoreCase(firstType, "oracle")) {
            String firstSource = rule.getFirstSource();
            LambdaQueryWrapper<OracleSource> oracleSourceLambdaQueryWrapper = Wrappers.lambdaQuery();
            oracleSourceLambdaQueryWrapper.eq(OracleSource::getName, firstSource);
            OracleSource oracleSource = iOracleSourceService.getOne(oracleSourceLambdaQueryWrapper);
            String hostname = oracleSource.getHostname();
            int port = oracleSource.getPort();
            String database = oracleSource.getDatabase();
            String schema = oracleSource.getSchema();
            String table = StringUtils.joinWith(".", schema, oracleSource.getTable());
            String username = oracleSource.getUsername();
            String password = oracleSource.getPassword();

            String keys = rule.getFirstKeys();
            String fields = rule.getFirstColumns();
            String conditions = rule.getFirstConditions();


            stringJoiner.add("--first-source-type").add(firstType)
                    .add("--first-hostname").add(hostname)
                    .add("--first-port").add(String.valueOf(port))
                    .add("--first-database").add(database)
                    .add("--first-schema-list").add(schema)
                    .add("--first-table-list").add(table)
                    .add("--first-username").add(username)
                    .add("--first-password").add(password)
                    .add("--first-keys").add(keys)
                    .add("--first-fields").add(fields)
                    .add("--first-table").add(table);

            if (StringUtils.isNotBlank(conditions)) {
                stringJoiner.add("--first-conditions").add(conditions);
            }

        } else {
            // TODO
        }

        String secondType = rule.getFirstSourceType();
        if (StringUtils.equalsIgnoreCase(secondType, "oracle")) {
            String secondSource = rule.getSecondSource();
            LambdaQueryWrapper<OracleSource> oracleSourceLambdaQueryWrapper = Wrappers.lambdaQuery();
            oracleSourceLambdaQueryWrapper.eq(OracleSource::getName, secondSource);
            OracleSource oracleSource = iOracleSourceService.getOne(oracleSourceLambdaQueryWrapper);
            String hostname = oracleSource.getHostname();
            int port = oracleSource.getPort();
            String database = oracleSource.getDatabase();
            String schema = oracleSource.getSchema();
            String table = StringUtils.joinWith(".", schema, oracleSource.getTable());
            String username = oracleSource.getUsername();
            String password = oracleSource.getPassword();

            String keys = rule.getSecondKeys();
            String fields = rule.getSecondColumns();
            String conditions = rule.getSecondConditions();

            stringJoiner.add("--second-source-type").add(firstType)
                    .add("--second-hostname").add(hostname)
                    .add("--second-port").add(String.valueOf(port))
                    .add("--second-database").add(database)
                    .add("--second-schema-list").add(schema)
                    .add("--second-table-list").add(table)
                    .add("--second-username").add(username)
                    .add("--second-password").add(password)
                    .add("--second-keys").add(keys)
                    .add("--second-fields").add(fields)
                    .add("--second-table").add(table);
            ;

            if (StringUtils.isNotBlank(conditions)) {
                stringJoiner.add("--second-conditions").add(conditions);
            }
        } else {
            // TODO
        }
        // TODO 从表读取监控组
        // TODO 场景更新表状态

        stringJoiner.add("--sink-type").add("elasticsearch7")
                .add("--es-hostname").add("10.9.75.236")
                .add("--es-port").add("9200")
                .add("--es-scheme").add("http");
        stringJoiner.add("--scene-id").add(scene.getId());
        String[] args = stringJoiner.toString().split(" ");

        //Application Mode not supported by standalone deployments.

        Configuration configuration = new Configuration();
        configuration.setString(JobManagerOptions.ADDRESS, "192.168.78.97");
//        configuration.setString("classloader.resolve-order","parent-first");
        // 解决loader constraint violation
//        configuration.setString("classloader.parent-first-patterns.additional", "org.apache");
        configuration.set(CoreOptions.ALWAYS_PARENT_FIRST_LOADER_PATTERNS_ADDITIONAL, List.of("org.apache.http.HttpHost"));


        File jarFile = new File(flinkJarFile);
        JobGraph jobGraph = null;
        JobID jobID = null;
        try (RestClusterClient<StandaloneClusterId> restClusterClient = new RestClusterClient<>(configuration, StandaloneClusterId.getInstance());
             PackagedProgram packagedProgram = PackagedProgram.newBuilder()
                     .setConfiguration(configuration)
                     .setEntryPointClassName("com.hisun.guard.DataStreamJob")
                     .setArguments(args)
                     .setJarFile(jarFile)
                     .build()) {
            jobGraph = PackagedProgramUtils.createJobGraph(packagedProgram, configuration, 1, true);
            CompletableFuture<JobID> result = restClusterClient.submitJob(jobGraph);
            jobID = result.get();
            log.info("任务编号 ---> {}", jobID.toHexString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        LambdaUpdateWrapper<Scene> sceneLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        sceneLambdaUpdateWrapper.set(Scene::getPublishStatus, "1");
        sceneLambdaUpdateWrapper.set(Scene::getJobId, jobID.toHexString());
        sceneLambdaUpdateWrapper.eq(Scene::getId, scene.getId());
        update(null, sceneLambdaUpdateWrapper);

        log.info("场景[{}]发布成功，任务编号:{}", scene.getName(), jobID);
    }

    @Override
    public void sendEmail(String sceneId, String alarm) {
        Scene scene = getById(sceneId);
        String alarmGroupName = scene.getAlarmGroupName();

        LambdaQueryWrapper<AlarmGroup> alarmGroupLambdaQueryWrapper = new LambdaQueryWrapper<>();
        alarmGroupLambdaQueryWrapper.eq(AlarmGroup::getName, alarmGroupName);
        AlarmGroup alarmGroup = iAlarmGroupService.getOne(alarmGroupLambdaQueryWrapper);
        String[] alarmInstanceIds = alarmGroup.getAlarmInstanceIds().split(",");
        for (String id : alarmInstanceIds) {
            iAlarmInstanceService.sendEmail(id, scene.getName(), scene.getJobId(), alarm);
        }
    }
}
