package com.example.wx_smart_community.entity;

import lombok.Data;

@Data
public class Device {
    private Integer deviceId;
    private String type;
    private String deviceNumber;
    private Integer houseId;
    private String installLocation;
    private String status;
    private String lastCheckTime;
    private String model;
    private String manufacturer;
    private String installTime;
} 