package com.example.wx_smart_community.service;

import java.util.Map;

public interface WxPayService {
    Map<String, Object> createOrder(Map<String, Object> params);
    Map<String, String> parsePayNotify(String xmlData);
} 