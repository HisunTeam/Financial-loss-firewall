package com.hisun.tower.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hisun.tower.common.vo.Result;
import com.hisun.tower.entity.AlarmInstance;

public interface IAlarmInstanceService extends IService<AlarmInstance> {
    boolean testAlert(AlarmInstance alarmInstance);

    void sendEmail(String alarmId, String sceneName, String jobId, String alarm);
}
