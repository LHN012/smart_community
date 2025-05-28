const app = getApp()

// 天气API配置
const WEATHER_CONFIG = {
  id: '88888888', // 替换成您的接口盒子ID
  key: '88888888', // 替换成您的接口盒子KEY
  sheng: '山东', // 省份名称
  place: '淄博' // 详细地点
}

// 虚拟天气数据
const MOCK_WEATHER = {
  temperature: '23',
  weather: '晴'
}

// 虚拟余额数据
const MOCK_BALANCE = '1,234.56'

Page({
  data: {
    userInfo: null,
    weather: {
      temperature: '--',
      weather: '未知'
    },
    currentTime: '',
    currentDate: '',
    stats: {
      unreadNotices: 0, // 未读通知数
      pendingRepairs: 0, // 未处理报修数
      balance: '0.00' // 房屋余额
    },
    notice: {
      content: '暂无公告'
    },
    weatherRetryCount: 0, // 添加重试计数器
    maxRetries: 3, // 最大重试次数
    retryInterval: 900000, // 重试间隔改为15分钟（900000毫秒）
    useMockData: true // 默认使用虚拟数据
  },

  onLoad() {
    const userInfo = wx.getStorageSync('userInfo')
    if (!userInfo) {
      wx.redirectTo({
        url: '/pages/login/login'
      })
      return
    }
    this.setData({ userInfo })
    this.updateTime()
    this.getWeather()
    this.getStats()
    this.getNotice()
    
    // 每秒更新一次时间
    setInterval(() => {
      this.updateTime()
    }, 1000)

    // 每15分钟更新一次天气
    setInterval(() => {
      this.getWeather()
    }, 900000)
  },

  // 更新时间
  updateTime: function() {
    const now = new Date()
    // 格式化时间
    const time = now.toLocaleTimeString('zh-CN', {
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: false
    })
    // 格式化日期
    const date = now.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    })
    
    this.setData({
      currentTime: time,
      currentDate: date
    })
  },

  // 获取天气信息
  getWeather: function() {
    // 如果使用虚拟数据，直接返回
    if (this.data.useMockData) {
      this.setData({
        weather: MOCK_WEATHER
      })
      return
    }

    // 如果超过最大重试次数，则使用虚拟数据
    if (this.data.weatherRetryCount >= this.data.maxRetries) {
      console.log('天气API重试次数已达上限，使用虚拟数据')
      this.setData({
        weather: MOCK_WEATHER,
        useMockData: true
      })
      return
    }

    wx.request({
      url: 'https://cn.apihz.cn/api/tianqi/tqyb.php',
      data: {
        id: WEATHER_CONFIG.id,
        key: WEATHER_CONFIG.key,
        sheng: WEATHER_CONFIG.sheng,
        place: WEATHER_CONFIG.place
      },
      success: (res) => {
        if (res.data && res.data.code === 200) {
          const weatherData = res.data.data
          this.setData({
            weather: {
              temperature: weatherData.temperature,
              weather: weatherData.weather
            },
            weatherRetryCount: 0, // 重置重试计数
            useMockData: false // 使用真实数据
          })
        } else {
          console.error('获取天气信息失败:', res.data)
          
          // 如果是频次限制错误，等待后重试
          if (res.data && res.data.code === 400 && res.data.msg.includes('调用频次过快')) {
            this.setData({
              weatherRetryCount: this.data.weatherRetryCount + 1,
              weather: MOCK_WEATHER, // 在重试期间显示虚拟数据
              useMockData: true
            })
            
            // 显示重试提示
            wx.showToast({
              title: `正在重试获取天气(${this.data.weatherRetryCount}/${this.data.maxRetries})`,
              icon: 'none',
              duration: 3000
            })
            
            // 等待指定时间后重试
            setTimeout(() => {
              this.getWeather()
            }, this.data.retryInterval)
          } else {
            // 其他错误时使用虚拟数据
            this.setData({
              weather: MOCK_WEATHER,
              useMockData: true
            })
            wx.showToast({
              title: '使用模拟天气数据',
              icon: 'none',
              duration: 3000
            })
          }
        }
      },
      fail: (err) => {
        console.error('请求天气API失败:', err)
        // 网络错误时使用虚拟数据
        this.setData({
          weather: MOCK_WEATHER,
          useMockData: true
        })
        wx.showToast({
          title: '使用模拟天气数据',
          icon: 'none',
          duration: 3000
        })
      }
    })
  },

  // 获取统计数据
  getStats: function() {
    // 获取未读通知数
    wx.request({
      url: 'YOUR_API_URL/notices/unread/count',
      success: (res) => {
        if (res.data) {
          this.setData({
            'stats.unreadNotices': res.data.count || 0
          })
        }
      }
    })

    // 获取未处理报修数
    wx.request({
      url: 'YOUR_API_URL/repairs/pending/count',
      success: (res) => {
        if (res.data) {
          this.setData({
            'stats.pendingRepairs': res.data.count || 0
          })
        }
      }
    })

    // 余额使用虚拟数据
    this.setData({
      'stats.balance': MOCK_BALANCE
    })
  },

  // 获取公告
  getNotice: function() {
    // 这里替换成您的实际API
    wx.request({
      url: 'YOUR_API_URL/notice',
      success: (res) => {
        if (res.data) {
          this.setData({
            notice: res.data
          })
        }
      }
    })
  },

  // 页面跳转
  navigateTo(e) {
    const url = e.currentTarget.dataset.url;
    console.log('准备跳转到:', url);
    
    // 检查是否是tabBar页面
    const tabBarPages = ['/pages/home/home', '/pages/notice/notice', '/pages/profile/profile'];
    
    if (tabBarPages.includes(url)) {
      wx.switchTab({
        url: url,
        success: () => {
          console.log('跳转成功');
        },
        fail: (err) => {
          console.error('跳转失败:', err);
          wx.showToast({
            title: '页面跳转失败',
            icon: 'none'
          });
        }
      });
    } else {
      wx.navigateTo({
        url: url,
        success: () => {
          console.log('跳转成功');
        },
        fail: (err) => {
          console.error('跳转失败:', err);
          wx.showToast({
            title: '页面跳转失败',
            icon: 'none'
          });
        }
      });
    }
  }
}) 