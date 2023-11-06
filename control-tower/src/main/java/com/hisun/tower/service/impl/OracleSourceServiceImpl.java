package com.hisun.tower.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hisun.tower.entity.OracleSource;
import com.hisun.tower.mapper.OracleSourceMapper;
import com.hisun.tower.service.IOracleSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OracleSourceServiceImpl extends ServiceImpl<OracleSourceMapper, OracleSource> implements IOracleSourceService {
}
