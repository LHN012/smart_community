package com.example.wx_smart_community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.wx_smart_community.entity.WxUser;
import com.example.wx_smart_community.mapper.WxUserMapper;
import com.example.wx_smart_community.service.WxUserService;
import com.example.wx_smart_community.config.WxProperties;
import com.example.wx_smart_community.model.WxLoginResponse;
import com.example.wx_smart_community.model.WxUserInfo;
import com.example.wx_smart_community.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class WxUserServiceImpl implements WxUserService {

    @Autowired
    private WxUserMapper wxUserMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WxProperties wxProperties;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public WxLoginResponse login(String code) {
        log.info("开始处理微信登录，code: {}", code);
        
        try {
            // 获取微信用户openid
            String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                wxProperties.getAppid(),
                wxProperties.getSecret(),
                code
            );
            
            log.info("请求微信接口获取openid，URL: {}", url);
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            String responseBody = responseEntity.getBody();
            log.info("微信接口返回原始数据：{}", responseBody);
            
            Map<String, Object> response = objectMapper.readValue(responseBody, Map.class);
            log.info("微信接口返回解析后数据：{}", response);
            
            if (response == null || response.get("openid") == null) {
                String errorMsg = response != null ? (String) response.get("errmsg") : "未知错误";
                log.error("获取openid失败：{}", errorMsg);
                throw new RuntimeException("获取openid失败：" + errorMsg);
            }
            
            String openid = (String) response.get("openid");
            log.info("成功获取openid: {}", openid);
            
            // 生成token
            String token = jwtUtil.generateToken(openid);
            log.info("生成token成功");
            
            // 获取用户信息
            WxUserInfo userInfo = getUserInfo(openid, null);
            if (userInfo == null) {
                userInfo = new WxUserInfo();
                userInfo.setOpenid(openid);
            }
            
            return new WxLoginResponse(token, userInfo);
        } catch (Exception e) {
            log.error("微信登录处理失败", e);
            throw new RuntimeException("微信登录失败：" + e.getMessage());
        }
    }

    @Override
    public WxUserInfo getUserInfo(String openid, String userId) {
        log.info("获取用户信息，openid: {}, userId: {}", openid, userId);
        
        WxUser wxUser = null;
        if (openid != null) {
            wxUser = wxUserMapper.selectOne(new QueryWrapper<WxUser>().eq("openid", openid));
        } else if (userId != null) {
            wxUser = wxUserMapper.selectOne(new QueryWrapper<WxUser>().eq("user_id", userId));
        }
        
        if (wxUser == null) {
            return null;
        }
        
        WxUserInfo userInfo = new WxUserInfo();
        userInfo.setOpenid(wxUser.getOpenid());
        userInfo.setNickname(wxUser.getNickname());
        userInfo.setAvatarUrl(wxUser.getAvatarUrl());
        userInfo.setGender(String.valueOf(wxUser.getGender()));
        userInfo.setCountry(wxUser.getCountry());
        userInfo.setProvince(wxUser.getProvince());
        userInfo.setCity(wxUser.getCity());
        userInfo.setLanguage(wxUser.getLanguage());
        userInfo.setUserId(wxUser.getUserId());
        
        return userInfo;
    }

    @Override
    public WxUserInfo bindWechat(String code, String userId) {
        log.info("绑定微信账号，code: {}, userId: {}", code, userId);
        
        // 获取openid
        String url = String.format(
            "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
            wxProperties.getAppid(),
            wxProperties.getSecret(),
            code
        );
        
        log.info("请求微信接口获取openid，URL: {}", url);
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        log.info("微信接口返回数据：{}", response);
        
        if (response == null || response.get("openid") == null) {
            String errorMsg = response != null ? response.get("errmsg").toString() : "未知错误";
            log.error("获取openid失败：{}", errorMsg);
            throw new RuntimeException("获取openid失败：" + errorMsg);
        }
        
        String openid = response.get("openid").toString();
        log.info("成功获取openid: {}", openid);
        
        // 保存绑定关系到数据库
        WxUser wxUser = new WxUser();
        wxUser.setOpenid(openid);
        wxUser.setUserId(Long.parseLong(userId));
        wxUserMapper.insert(wxUser);
        
        return getUserInfo(openid, null);
    }

    @Override
    public WxUser getUserByOpenid(String openid) {
        return wxUserMapper.selectOne(new QueryWrapper<WxUser>().eq("openid", openid));
    }

    @Override
    public WxUser getUserByUserId(Long userId) {
        return wxUserMapper.selectOne(new QueryWrapper<WxUser>().eq("user_id", userId));
    }

    @Override
    public void updateUserInfo(WxUser wxUser) {
        wxUserMapper.updateById(wxUser);
    }
} 