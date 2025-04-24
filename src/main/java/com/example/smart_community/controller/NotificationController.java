package com.example.smart_community.controller;

import com.example.smart_community.common.Result;
import com.example.smart_community.entity.Notifications;
import com.example.smart_community.entity.NotificationReceiver;
import com.example.smart_community.entity.Users;
import com.example.smart_community.mapper.NotificationsMapper;
import com.example.smart_community.mapper.NotificationReceiverMapper;
import com.example.smart_community.mapper.UsersMapper;
import com.example.smart_community.security.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import javax.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Base64;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private NotificationsMapper notificationsMapper;

    @Autowired
    private NotificationReceiverMapper notificationReceiverMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping
    public Result<List<Map<String, Object>>> getNotifications() {
        try {
            List<Map<String, Object>> notifications = notificationsMapper.selectNotificationsWithSenderAndReceiver();
            return Result.success(notifications);
        } catch (Exception e) {
            logger.error("获取通知列表失败", e);
            return Result.error("获取通知列表失败：" + e.getMessage());
        }
    }

    @PostMapping
    public Result<String> createNotification(@RequestBody Map<String, Object> notificationData) {
        try {
            logger.info("收到创建通知请求，数据：{}", notificationData);
            
            // 获取当前用户ID
            Integer senderId = getCurrentUserId();
            if (senderId == null) {
                logger.error("未获取到当前用户信息");
                return Result.error("未获取到当前用户信息");
            }
            
            // 验证必要字段
            if (notificationData.get("title") == null || notificationData.get("content") == null || 
                notificationData.get("notificationType") == null || notificationData.get("receiverType") == null) {
                logger.error("缺少必要字段");
                return Result.error("请填写完整信息");
            }
            
            // 创建通知
            Notifications notification = new Notifications();
            notification.setNotificationType((String) notificationData.get("notificationType"));
            notification.setTitle((String) notificationData.get("title"));
            notification.setContent((String) notificationData.get("content"));
            notification.setSenderId(senderId);
            notification.setReceiverType((String) notificationData.get("receiverType"));
            
            // 设置接收者ID
            String receiverType = notification.getReceiverType();
            if ("特定房间业主".equals(receiverType)) {
                // 特定房间业主
                Object roomIdObj = notificationData.get("roomId");
                if (roomIdObj == null) {
                    logger.error("特定房间业主未选择具体房间");
                    return Result.error("请选择具体房间");
                }
                try {
                    Integer roomId = Integer.valueOf(roomIdObj.toString());
                    notification.setReceiverId(roomId);
                } catch (NumberFormatException e) {
                    logger.error("房间ID格式错误", e);
                    return Result.error("房间ID格式错误");
                }
            } else {
                // 全体业主或物业员工，receiverId设为null
                notification.setReceiverId(null);
            }
            
            notification.setSendTime(LocalDateTime.now());
            
            logger.info("准备保存通知：{}", notification);
            
            // 保存通知
            int result = notificationsMapper.insert(notification);
            if (result <= 0) {
                logger.error("保存通知失败");
                return Result.error("保存通知失败");
            }
            
            logger.info("通知保存成功，ID：{}", notification.getId());
            
            // 创建通知接收记录
            if ("全体业主".equals(receiverType)) {
                // 全体业主
                List<Users> users = usersMapper.selectUsersByRole(1);
                for (Users user : users) {
                    createNotificationReceiver(notification.getId(), user.getUserId());
                }
            } else if ("物业员工".equals(receiverType)) {
                // 物业员工
                List<Users> users = usersMapper.selectUsersByRole(2);
                for (Users user : users) {
                    createNotificationReceiver(notification.getId(), user.getUserId());
                }
            } else if ("特定房间业主".equals(receiverType)) {
                // 特定房间业主
                List<Users> users = usersMapper.selectUsersByHouseId(notification.getReceiverId());
                for (Users user : users) {
                    createNotificationReceiver(notification.getId(), user.getUserId());
                }
            } else {
                logger.error("未知的接收类型: {}", receiverType);
                return Result.error("未知的接收类型");
            }
            
            logger.info("通知发布成功");
            return Result.success("通知发布成功");
        } catch (Exception e) {
            logger.error("通知发布失败", e);
            return Result.error("通知发布失败：" + e.getMessage());
        }
    }

    private void createNotificationReceiver(Integer notificationId, Integer userId) {
        try {
            NotificationReceiver receiver = new NotificationReceiver();
            receiver.setNotificationId(notificationId);
            receiver.setReceiverUserId(userId);
            receiver.setIsRead(false);
            notificationReceiverMapper.insert(receiver);
        } catch (Exception e) {
            logger.error("创建通知接收记录失败", e);
            throw e;
        }
    }

    private Integer getCurrentUserId() {
        try {
            // 从请求头中获取token
            String token = request.getHeader("Authorization");
            logger.info("获取到的Authorization头: {}", token);
            
            if (token == null || !token.startsWith("Bearer ")) {
                logger.error("未获取到有效的token");
                return null;
            }
            
            // 使用JwtTokenUtil解析token获取用户ID
            String jwtToken = token.substring(7); // 去掉"Bearer "前缀
            Integer userId = jwtTokenUtil.getUserIdFromToken(jwtToken);
            
            if (userId == null) {
                logger.error("从token中解析用户ID失败");
                return null;
            }
            
            logger.info("解析出的用户ID: {}", userId);
            return userId;
        } catch (Exception e) {
            logger.error("获取当前用户ID失败", e);
            return null;
        }
    }
} 