import paho.mqtt.client as mqtt
import json
import time
import random
from datetime import datetime
import threading

# netstat -ano | findstr 1883

class BaseMeterSimulator:
    def __init__(self, device_id, device_number, broker="localhost", port=1883):
        self.device_id = device_id
        self.device_number = device_number
        
        # 使用新的API方式创建MQTT客户端
        self.client = mqtt.Client(
            callback_api_version=mqtt.CallbackAPIVersion.VERSION2,
            client_id=f"meter_{device_number}"
        )
        
        self.broker = broker
        self.port = port
        self.total_volume = 0.0
        self.connected = False
        
        # 连接回调
        self.client.on_connect = self.on_connect
        self.client.on_message = self.on_message
        self.client.on_disconnect = self.on_disconnect
    
    def on_connect(self, client, userdata, flags, reason_code, properties):
        if reason_code == 0:
            self.connected = True
            print(f"设备 {self.device_number} 已连接到MQTT服务器")
            # 订阅控制主题
            control_topic = f"device/{self.device_id}/control"
            self.client.subscribe(control_topic)
        else:
            self.connected = False
            print(f"设备 {self.device_number} 连接失败，返回码：{reason_code}")
    
    def on_disconnect(self, client, userdata, reason_code, properties=None, v1_rc=None):
        self.connected = False
        print(f"设备 {self.device_number} 已断开连接，原因码：{reason_code}")

    def on_message(self, client, userdata, msg, properties=None):
        try:
            payload = json.loads(msg.payload.decode())
            command = payload.get("command")
            print(f"设备 {self.device_number} 收到命令: {command}")
        except Exception as e:
            print(f"处理命令出错: {e}")

    def connect(self):
        try:
            print(f"设备 {self.device_number} 正在连接到MQTT服务器...")
            self.client.connect(self.broker, self.port, 60)
            self.client.loop_start()
        except Exception as e:
            print(f"设备 {self.device_number} 连接失败: {str(e)}")
            raise

    def generate_data(self):
        raise NotImplementedError("子类必须实现此方法")
    
    def send_data(self):
        if not self.connected:
            print(f"设备 {self.device_number} 未连接，无法发送数据")
            return
            
        topic = f"device/{self.device_id}/data"
        data = self.generate_data()
        try:
            self.client.publish(topic, json.dumps(data))
            print(f"设备 {self.device_number} 发送数据: {data}")
        except Exception as e:
            print(f"设备 {self.device_number} 发送数据失败: {e}")
    
    def run(self, interval=60):
        while True:
            try:
                self.send_data()
                time.sleep(interval)
            except KeyboardInterrupt:
                print(f"\n设备 {self.device_number} 正在停止...")
                self.client.loop_stop()
                self.client.disconnect()
                break
            except Exception as e:
                print(f"设备 {self.device_number} 运行错误: {e}")
                time.sleep(5)  # 发生错误时等待5秒后重试

class GasMeterSimulator(BaseMeterSimulator):
    def __init__(self, device_id, device_number, broker="localhost", port=1883):
        super().__init__(device_id, device_number, broker, port)
        self.total_volume = 0.0
    
    def generate_data(self):
        flow_rate = random.uniform(0.1, 2.0)  # 流量 (m³/h)
        self.total_volume += flow_rate / 60  # 累计用量 (m³)，由于是每分钟发送一次，所以除以60
        pressure = random.uniform(2.0, 4.0)    # 压力 (kPa)
        temperature = random.uniform(15, 25)    # 温度 (°C)
        battery_level = random.uniform(80, 100) # 电池电量 (%)
        signal_strength = random.randint(-100, -50)  # 信号强度 (dBm)
        
        # 随机生成异常状态
        is_abnormal = random.random() < 0.05  # 5%概率产生异常数据
        
        return {
            "timestamp": datetime.now().isoformat(),
            "data": {
                "flow_rate": round(flow_rate, 3),
                "total_volume": round(self.total_volume, 3),
                "pressure": round(pressure, 2),
                "temperature": round(temperature, 1),
                "battery_level": round(battery_level, 2),
                "signal_strength": signal_strength,
                "is_abnormal": is_abnormal
            }
        }

class WaterMeterSimulator(BaseMeterSimulator):
    def __init__(self, device_id, device_number, broker="localhost", port=1883):
        super().__init__(device_id, device_number, broker, port)
        self.total_volume = 0.0
    
    def generate_data(self):
        flow_rate = random.uniform(0.05, 1.0)  # 流量 (m³/h)
        self.total_volume += flow_rate / 60  # 累计用量 (m³)
        pressure = random.uniform(200, 400)   # 水压 (kPa)
        temperature = random.uniform(10, 30)  # 水温 (°C)
        battery_level = random.uniform(80, 100) # 电池电量 (%)
        signal_strength = random.randint(-100, -50)  # 信号强度 (dBm)
        
        return {
            "timestamp": datetime.now().isoformat(),
            "data": {
                "flow_rate": round(flow_rate, 4),
                "total_volume": round(self.total_volume, 3),
                "pressure": round(pressure, 2),
                "temperature": round(temperature, 2),
                "battery_level": round(battery_level, 2),
                "signal_strength": signal_strength
            }
        }

class ElectricMeterSimulator(BaseMeterSimulator):
    def __init__(self, device_id, device_number, broker="localhost", port=1883):
        super().__init__(device_id, device_number, broker, port)
        self.total_energy = 0.0
    
    def generate_data(self):
        current_power = random.uniform(0.5, 5.0)  # 当前功率 (kW)
        self.total_energy += current_power / 60  # 累计电量 (kWh)
        voltage = random.uniform(220, 240)      # 电压 (V)
        current = current_power * 1000 / voltage  # 电流 (A)
        power_factor = random.uniform(0.85, 0.98)  # 功率因数
        signal_strength = random.randint(-100, -50)  # 信号强度 (dBm)
        
        return {
            "timestamp": datetime.now().isoformat(),
            "data": {
                "current_power": round(current_power, 3),
                "total_energy": round(self.total_energy, 3),
                "voltage": round(voltage, 2),
                "current": round(current, 3),
                "power_factor": round(power_factor, 3),
                "signal_strength": signal_strength
            }
        }

def run_device(device):
    try:
        device.connect()
        device.run(interval=10)  # 每10秒发送一次数据
    except KeyboardInterrupt:
        print(f"设备 {device.device_number} 停止运行")
        device.client.loop_stop()
    except Exception as e:
        print(f"设备 {device.device_number} 运行出错: {e}")

if __name__ == "__main__":
    # 创建模拟设备，使用与数据库中相同的设备ID
    devices = [
        GasMeterSimulator("GAS-2025001", "GAS-2025001"),    # 1号燃气表
        GasMeterSimulator("GAS-2025002", "GAS-2025002"),    # 2号燃气表
        WaterMeterSimulator("WATER-2025001", "WATER-2025001"), # 3号水表
        ElectricMeterSimulator("ELEC-2025001", "ELEC-2025001") # 4号电表
    ]
    
    print("模拟器启动中，每10秒将发送一次数据...")
    
    # 为每个设备创建一个线程
    threads = []
    for device in devices:
        thread = threading.Thread(target=run_device, args=(device,))
        threads.append(thread)
        thread.start()
        print(f"设备 {device.device_number} 开始发送数据...")
    
    try:
        # 等待所有线程结束
        for thread in threads:
            thread.join()
    except KeyboardInterrupt:
        print("\n正在停止所有设备...") 