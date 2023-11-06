package com.hisun.tower.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hisun.tower.entity.SourceType;
import com.hisun.tower.mapper.SourceTypeMapper;
import com.hisun.tower.service.ISourceTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SourceTypeServiceImpl extends ServiceImpl<SourceTypeMapper, SourceType> implements ISourceTypeService {
    private final SourceTypeMapper sourceTypeMapper;

    public SourceTypeServiceImpl(SourceTypeMapper sourceTypeMapper) {
        this.sourceTypeMapper = sourceTypeMapper;
    }
}
