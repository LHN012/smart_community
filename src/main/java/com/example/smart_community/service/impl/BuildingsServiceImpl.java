package com.example.smart_community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.smart_community.entity.Buildings;
import com.example.smart_community.mapper.BuildingsMapper;
import com.example.smart_community.service.BuildingsService;
import org.springframework.stereotype.Service;

@Service
public class BuildingsServiceImpl extends ServiceImpl<BuildingsMapper, Buildings> implements BuildingsService {
}    