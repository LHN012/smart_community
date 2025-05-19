package com.example.wx_smart_community.controller;

import com.example.wx_smart_community.entity.Notice;
import com.example.wx_smart_community.service.NoticeService;
import com.example.wx_smart_community.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notice")
public class NoticeController {
    @Autowired
    private NoticeService noticeService;

    @GetMapping("/list")
    public Result<List<Notice>> getNoticeList(
            @RequestParam Integer userId,
            @RequestParam(defaultValue = "all") String type,
            @RequestParam(defaultValue = "current") String timeRange) {
        List<Notice> notices = noticeService.getNoticeList(userId, type, timeRange);
        return Result.success(notices);
    }

    @GetMapping("/detail")
    public Result<Notice> getNoticeDetail(@RequestParam Integer noticeId, @RequestParam Integer userId) {
        Notice notice = noticeService.getNoticeDetail(noticeId, userId);
        if (notice == null) {
            return Result.error("获取通知详情失败");
        }
        return Result.success(notice);
    }

    @PostMapping("/read")
    public Result<Void> markAsRead(@RequestBody Map<String, Integer> params) {
        Integer userId = params.get("userId");
        Integer noticeId = params.get("noticeId");
        if (userId == null || noticeId == null) {
            return Result.error("参数错误");
        }
        noticeService.markAsRead(userId, noticeId);
        return Result.success();
    }

    @PostMapping("/readAll")
    public Result<Void> markAllAsRead(@RequestParam Integer userId) {
        noticeService.markAllAsRead(userId);
        return Result.success();
    }

    @GetMapping("/unread/count")
    public Result<Integer> getUnreadCount(@RequestParam Integer userId) {
        int count = noticeService.getUnreadCount(userId);
        return Result.success(count);
    }
} 