package com.example.smart_community.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.smart_community.entity.Units;
import com.example.smart_community.mapper.UnitsMapper;
import com.example.smart_community.service.UnitsService;
import org.springframework.stereotype.Service;

@Service
public class UnitsServiceImpl extends ServiceImpl<UnitsMapper, Units> implements UnitsService {
}    