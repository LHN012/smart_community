package com.example.wx_smart_community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.wx_smart_community.entity.WxUser;
import com.example.wx_smart_community.entity.User;
import com.example.wx_smart_community.mapper.WxUserMapper;
import com.example.wx_smart_community.mapper.UserMapper;
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
public class WxUserServiceImpl extends ServiceImpl<WxUserMapper, WxUser> implements WxUserService {

    @Autowired
    private WxUserMapper wxUserMapper;
    
    @Autowired
    private UserMapper userMapper;

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
            
            if (responseBody == null) {
                throw new RuntimeException("微信接口返回数据为空");
            }
            
            Map<String, Object> response = objectMapper.readValue(responseBody, Map.class);
            log.info("微信接口返回解析后数据：{}", response);
            
            if (response == null) {
                throw new RuntimeException("解析微信接口返回数据失败");
            }
            
            if (response.get("errcode") != null) {
                String errorMsg = String.format("微信接口返回错误：%s - %s", 
                    response.get("errcode"), 
                    response.get("errmsg"));
                log.error(errorMsg);
                throw new RuntimeException(errorMsg);
            }
            
            String openid = (String) response.get("openid");
            String sessionKey = (String) response.get("session_key");
            
            if (openid == null || sessionKey == null) {
                throw new RuntimeException("获取openid或session_key失败");
            }
            
            log.info("成功获取openid: {}, sessionKey: {}", openid, sessionKey);
            
            // 生成token
            String token = jwtUtil.generateToken(openid);
            log.info("生成token成功");
            
            // 获取或创建用户信息
            WxUser wxUser = findByOpenid(openid);
            if (wxUser == null) {
                wxUser = new WxUser();
                wxUser.setOpenid(openid);
                wxUser.setSessionKey(sessionKey);
                save(wxUser);
                log.info("创建新微信用户成功");
            } else {
                wxUser.setSessionKey(sessionKey);
                updateById(wxUser);
                log.info("更新微信用户sessionKey成功");
            }
            
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
            wxUser = wxUserMapper.selectOne(new QueryWrapper<WxUser>().eq("user_id", Integer.parseInt(userId)));
        }
        
        if (wxUser == null) {
            log.warn("未找到微信用户信息");
            return null;
        }
        
        // 获取关联的用户信息
        User user = null;
        if (wxUser.getUserId() != null) {
            user = userMapper.selectById(wxUser.getUserId());
        }
        
        WxUserInfo userInfo = new WxUserInfo();
        userInfo.setOpenid(wxUser.getOpenid());
        userInfo.setNickname(wxUser.getNickname());
        userInfo.setUserId(wxUser.getUserId());
        
        // 如果有关联的用户信息，补充用户信息
        if (user != null) {
            userInfo.setRealName(user.getRealName());
            userInfo.setPhone(user.getPhoneNumber());
            userInfo.setEmail(user.getEmail());
        }
        
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
        wxUser.setUserId(Integer.parseInt(userId));
        wxUserMapper.insert(wxUser);
        
        return getUserInfo(openid, null);
    }

    @Override
    public void updateUserInfo(WxUser wxUser) {
        log.info("更新微信用户信息：{}", wxUser);
        if (wxUser.getId() == null && wxUser.getOpenid() == null) {
            throw new RuntimeException("id和openid不能同时为空");
        }
        
        WxUser existingUser = null;
        if (wxUser.getId() != null) {
            existingUser = getById(wxUser.getId());
        } else {
            existingUser = findByOpenid(wxUser.getOpenid());
        }
        
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 更新用户信息
        updateById(wxUser);
    }

    @Override
    public WxUser findByOpenid(String openid) {
        QueryWrapper<WxUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid);
        return getOne(queryWrapper);
    }

    @Override
    public boolean saveOrUpdate(WxUser wxUser) {
        return super.saveOrUpdate(wxUser);
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