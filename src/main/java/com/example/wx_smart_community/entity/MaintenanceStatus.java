package com.example.wx_smart_community.entity;

public enum MaintenanceStatus {
    PENDING("待处理"),
    PROCESSING("处理中"),
    COMPLETED("已完成"),
    CANCELLED("已取消");

    private final String description;

    MaintenanceStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 