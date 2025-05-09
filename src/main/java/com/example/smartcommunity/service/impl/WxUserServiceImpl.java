package com.example.smartcommunity.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.smartcommunity.mapper.WxUserMapper;
import com.example.smartcommunity.model.WxUser;
import com.example.smartcommunity.service.WxUserService;
import org.springframework.stereotype.Service;

@Service
public class WxUserServiceImpl extends ServiceImpl<WxUserMapper, WxUser> implements WxUserService {

    @Override
    public WxUser findByOpenid(String openid) {
        QueryWrapper<WxUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid);
        return getOne(queryWrapper);
    }

    @Override
    public WxUser saveOrUpdate(WxUser wxUser) {
        if (wxUser.getId() == null) {
            save(wxUser);
        } else {
            updateById(wxUser);
        }
        return wxUser;
    }

    @Override
    public WxUser findByUserId(Integer userId) {
        QueryWrapper<WxUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return getOne(queryWrapper);
    }

    @Override
    public boolean updateWxUser(WxUser wxUser) {
        return updateById(wxUser);
    }
} 