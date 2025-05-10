package com.example.wx_smart_community.service.impl;

import com.example.wx_smart_community.service.WxPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class WxPayServiceImpl implements WxPayService {

    @Override
    public Map<String, Object> createOrder(Map<String, Object> params) {
        try {
            log.info("创建微信支付订单，参数：{}", params);
            
            // 构建支付参数
            Map<String, String> payParams = new HashMap<>();
            payParams.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
            payParams.put("nonceStr", UUID.randomUUID().toString().replaceAll("-", "").substring(0, 32));
            payParams.put("package", "prepay_id=wx" + System.currentTimeMillis());
            payParams.put("signType", "MD5");
            payParams.put("paySign", "dummy_sign");

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", payParams);
            log.info("微信支付参数生成成功：{}", result);
            return result;
        } catch (Exception e) {
            log.error("创建微信支付订单失败", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "创建支付订单失败：" + e.getMessage());
            return error;
        }
    }

    @Override
    public Map<String, String> parsePayNotify(String xmlData) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(xmlData)));
            NodeList nodeList = doc.getElementsByTagName("xml").item(0).getChildNodes();

            Map<String, String> result = new HashMap<>();
            for (int i = 0; i < nodeList.getLength(); i++) {
                result.put(nodeList.item(i).getNodeName(), nodeList.item(i).getTextContent());
            }
            return result;
        } catch (Exception e) {
            log.error("解析支付回调XML失败", e);
            throw new RuntimeException("解析支付回调XML失败", e);
        }
    }
} 