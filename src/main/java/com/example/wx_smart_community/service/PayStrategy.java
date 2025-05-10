package com.example.wx_smart_community.service;

import java.util.Map;

public interface PayStrategy {
    Map<String, Object> pay(Map<String, Object> params);
    String getPayType();
} 