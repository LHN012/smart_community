package com.example.wx_smart_community.entity;

import lombok.Data;

@Data
public class House {
    private Integer houseId;
    private String houseNumber;
    private String building;
    private String unit;
    private String floor;
    private String room;
    private Integer userId;
    private String status;
    private String createTime;
    private String updateTime;
} 