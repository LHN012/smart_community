package com.example.wx_smart_community.service;

import com.example.wx_smart_community.entity.Notice;
import com.example.wx_smart_community.mapper.NoticeMapper;
import com.example.wx_smart_community.mapper.NoticeReceiverMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class NoticeService {
    private static final Logger logger = LoggerFactory.getLogger(NoticeService.class);

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private NoticeReceiverMapper noticeReceiverMapper;

    public List<Notice> getNoticeList(Integer userId, String type, String timeRange) {
        Calendar cal = Calendar.getInstance();
        Date startTime;

        if ("history".equals(timeRange)) {
            cal.add(Calendar.MONTH, -1);
            startTime = cal.getTime();
        } else {
            cal.add(Calendar.MONTH, -1);
            startTime = cal.getTime();
        }

        // 转换通知类型
        String noticeType = "全部";
        switch (type) {
            case "urgent":
                noticeType = "紧急通知";
                break;
            case "normal":
                noticeType = "公告";
                break;
            case "activity":
                noticeType = "活动通知";
                break;
            case "maintenance":
                noticeType = "报修通知";
                break;
            case "payment":
                noticeType = "缴费通知";
                break;
        }

        return noticeMapper.getNoticeList(userId, noticeType, startTime);
    }

    public Notice getNoticeDetail(Integer noticeId, Integer userId) {
        try {
            if (noticeId == null || userId == null) {
                logger.error("通知ID或用户ID为空");
                return null;
            }
            Notice notice = noticeMapper.selectById(noticeId, userId);
            if (notice == null) {
                logger.error("未找到通知详情，noticeId: {}, userId: {}", noticeId, userId);
                return null;
            }
            return notice;
        } catch (Exception e) {
            logger.error("获取通知详情失败: {}, noticeId: {}, userId: {}", e.getMessage(), noticeId, userId);
            return null;
        }
    }

    public void markAsRead(Integer userId, Integer noticeId) {
        noticeReceiverMapper.markAsRead(userId, noticeId);
    }

    public void markAllAsRead(Integer userId) {
        noticeReceiverMapper.markAllAsRead(userId);
    }

    public int getUnreadCount(Integer userId) {
        return noticeMapper.getUnreadCount(userId);
    }
} 