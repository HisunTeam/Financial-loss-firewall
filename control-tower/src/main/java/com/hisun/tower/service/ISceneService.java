package com.hisun.tower.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hisun.tower.entity.Scene;

public interface ISceneService extends IService<Scene> {
    void publish(Scene scene);

    void sendEmail(String sceneId, String alarm);
}
