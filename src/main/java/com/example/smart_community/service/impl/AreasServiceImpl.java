package com.example.smart_community.service.impl;

/**
 * @Author:LHN
 * @CreateTime:2025/4/18 14:46
 */
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.smart_community.entity.Areas;
import com.example.smart_community.mapper.AreasMapper;
import com.example.smart_community.service.AreasService;
import org.springframework.stereotype.Service;

@Service
public class AreasServiceImpl extends ServiceImpl<AreasMapper, Areas> implements AreasService {
}