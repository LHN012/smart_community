package com.example.wx_smart_community.mqtt;

import com.example.wx_smart_community.service.GasMeterDataCollectionService;
import com.example.wx_smart_community.service.WaterMeterDataCollectionService;
import com.example.wx_smart_community.service.ElectricMeterDataCollectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

@Component
public class DefaultMqttMessageHandler implements MessageHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(DefaultMqttMessageHandler.class);
    
    @Autowired
    private GasMeterDataCollectionService gasMeterDataService;
    
    @Autowired
    private WaterMeterDataCollectionService waterMeterDataService;
    
    @Autowired
    private ElectricMeterDataCollectionService electricMeterDataService;
    
    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC, String.class);
        Object payload = message.getPayload();
        String payloadStr;
        
        if (payload instanceof byte[]) {
            payloadStr = new String((byte[]) payload);
        } else if (payload instanceof String) {
            payloadStr = (String) payload;
        } else {
            payloadStr = payload.toString();
        }
        
        logger.info("收到MQTT消息 - 主题: {}", topic);
        logger.debug("消息内容: {}", payloadStr);
        
        if (topic != null && topic.startsWith("device/")) {
            String[] topicParts = topic.split("/");
            if (topicParts.length >= 3) {
                String deviceId = topicParts[1];
                String messageType = topicParts[2];
                
                if ("data".equals(messageType)) {
                    // 根据设备ID前缀判断设备类型
                    if (deviceId.startsWith("GAS-")) {
                        gasMeterDataService.handleDeviceReport(deviceId, payloadStr);
                    } else if (deviceId.startsWith("WATER-")) {
                        waterMeterDataService.handleDeviceReport(deviceId, payloadStr);
                    } else if (deviceId.startsWith("ELEC-")) {
                        electricMeterDataService.handleDeviceReport(deviceId, payloadStr);
                    } else {
                        logger.warn("未知的设备类型: {}", deviceId);
                    }
                } else if ("status".equals(messageType)) {
                    // TODO: 处理设备状态消息
                    logger.debug("收到设备状态消息: {}", payloadStr);
                } else {
                    logger.warn("未知的消息类型: {}", messageType);
                }
            }
        }
    }
} 