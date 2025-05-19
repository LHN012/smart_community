// pages/usage/usage.js
const app = getApp()
// 暂时注释掉echarts引入，先实现基本功能
// const echarts = require('../../ec-canvas/echarts')

Page({

  /**
   * 页面的初始数据
   */
  data: {
    electricEc: {
      onInit: initChart
    },
    waterEc: {
      onInit: initChart
    },
    gasEc: {
      onInit: initChart
    },
    currentTab: 'realtime',
    currentMonth: '',
    historicalData: [],
    deviceIds: {
      electric: '',
      water: '',
      gas: ''
    },
    houseId: null,
    timer: null,
    houses: [], // 用户绑定的所有房屋
    currentHouseIndex: 0, // 当前选中的房屋索引
    realtimeData: {
      electric: null,
      water: null,
      gas: null
    }
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    console.log('页面加载开始')
    // 获取用户信息
    const userInfo = wx.getStorageSync('userInfo')
    console.log('获取到的用户信息:', userInfo)
    
    if (!userInfo || !userInfo.userId) {
      wx.showToast({
        title: '请先登录',
        icon: 'none',
        duration: 2000,
        complete: () => {
          setTimeout(() => {
            wx.navigateTo({
              url: '/pages/login/login'
            })
          }, 2000)
        }
      })
      return
    }

    // 加载用户绑定的房屋信息
    this.loadUserHouses(userInfo.userId)

    // 设置当前月份
    const now = new Date()
    const currentMonth = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
    this.setData({ currentMonth })
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    console.log('页面显示')
    // 如果已有房屋ID，重新加载数据
    if (this.data.houseId) {
      console.log('重新加载数据，houseId:', this.data.houseId)
      this.loadHistoricalData()
      this.loadRealtimeData()
    }
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {
    // 清除定时器
    if (this.data.timer) {
      clearInterval(this.data.timer)
    }
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {

  },

  // 加载用户绑定的房屋列表
  loadUserHouses(userId) {
    console.log('开始加载用户房屋列表，userId:', userId)

    wx.showLoading({
        title: '加载中...',
    })

    wx.request({
        url: `${app.globalData.baseUrl}/api/user/houses`,
        data: {
            userId: userId
        },
        success: (res) => {
            console.log('房屋列表接口返回:', res.data)
            if (res.data && Array.isArray(res.data)) {
                const houses = res.data || []
                console.log('处理前的房屋列表:', houses)
                
                // 为每个房屋初始化实时数据对象
                const processedHouses = houses.map(house => ({
                    ...house,
                    realtimeData: {
                        water: null,
                        electric: null,
                        gas: null
                    }
                }))
                
                console.log('处理后的房屋列表:', processedHouses)
                
                this.setData({ houses: processedHouses }, () => {
                    console.log('房屋列表已更新到视图:', this.data.houses)
                    // 加载每个房屋的设备信息
                    processedHouses.forEach(house => {
                        this.loadHouseDevices(house)
                    })
                })
            } else {
                console.error('获取房屋列表失败: 返回数据格式不正确')
                wx.showToast({
                    title: '加载失败',
                    icon: 'none'
                })
            }
        },
        fail: (err) => {
            console.error('加载房屋列表失败:', err)
            wx.showToast({
                title: '网络错误',
                icon: 'none'
            })
        },
        complete: () => {
            wx.hideLoading()
        }
    })
  },

  // 加载房屋设备信息
  loadHouseDevices(house) {
    console.log('开始加载房屋设备信息，house:', house)

    wx.request({
      url: `${app.globalData.baseUrl}/api/devices/house/${house.houseId}`,
      success: (res) => {
        console.log('设备信息接口返回:', res.data)
        if (res.data.code === 0) {
          const devices = res.data.data || []
          console.log('获取到的设备列表:', devices)
          
          // 获取当前月份
          const now = new Date()
          const currentMonth = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
          
          // 加载每个设备的实时数据
          devices.forEach(device => {
            this.loadDeviceLatestData(house, device, currentMonth)
          })
        } else {
          console.error('获取设备信息失败:', res.data.msg)
        }
      },
      fail: (err) => {
        console.error('加载设备信息失败:', err)
      }
    })
  },

  // 加载设备最新数据
  loadDeviceLatestData(house, device, month) {
    console.log('开始加载设备最新数据，house:', house, 'device:', device, 'month:', month)
    
    const type = device.type === '水' ? 'water' : 
                device.type === '电' ? 'electric' : 'gas'
    
    wx.request({
      url: `${app.globalData.baseUrl}/api/meter/${type}/latest`,
      data: {
        deviceId: device.deviceNumber,
        month: month
      },
      success: (res) => {
        console.log('设备数据接口返回:', res.data)
        if (res.data.code === 0 && res.data.data) {
          const data = res.data.data
          // 更新房屋的实时数据
          const houses = this.data.houses
          const houseIndex = houses.findIndex(h => h.houseId === house.houseId)
          if (houseIndex !== -1) {
            houses[houseIndex].realtimeData[type] = data
            this.setData({ houses }, () => {
              console.log('设备数据已更新到视图:', this.data.houses[houseIndex])
            })
          }
        } else {
          console.error('获取设备数据失败:', res.data.msg)
        }
      },
      fail: (err) => {
        console.error('加载设备数据失败:', err)
      }
    })
  },

  // 切换房屋
  switchHouse(e) {
    const index = e.currentTarget.dataset.index;
    if (index === this.data.currentHouseIndex) return;

    console.log('切换房屋，index:', index);

    this.setData({ 
      currentHouseIndex: index,
      houseId: this.data.houses[index].houseId,
      historicalData: [], // 清空历史数据
      realtimeData: {
        electric: null,
        water: null,
        gas: null
      }
    }, () => {
      console.log('房屋切换完成，新的houseId:', this.data.houseId);
      // 加载新房屋的设备信息
      this.loadHouseDevices(this.data.houses[index]);
    });
  },

  // 加载实时数据
  loadRealtimeData() {
    const { deviceIds } = this.data
    if (!deviceIds.electric && !deviceIds.water && !deviceIds.gas) return

    // 获取当前月份
    const now = new Date()
    const currentMonth = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`

    // 更新电表数据
    if (deviceIds.electric) {
      this.updateMeterData('electric', deviceIds.electric, currentMonth)
    }

    // 更新水表数据
    if (deviceIds.water) {
      this.updateMeterData('water', deviceIds.water, currentMonth)
    }

    // 更新燃气表数据
    if (deviceIds.gas) {
      this.updateMeterData('gas', deviceIds.gas, currentMonth)
    }
  },

  // 更新单个表的数据
  updateMeterData(type, deviceId, month) {
    wx.request({
      url: `${app.globalData.baseUrl}/api/meter/${type}/latest`,
      data: { 
        deviceId,
        month
      },
      success: (res) => {
        if (res.data.code === 0) {
          const data = res.data.data;
          if (data) {
            // 更新实时数据
            const realtimeData = { ...this.data.realtimeData };
            realtimeData[type] = {
              value: type === 'electric' ? data.totalEnergy :
                     type === 'water' ? data.totalVolume :
                     data.totalVolume,
              unit: type === 'electric' ? 'kWh' : 'm³',
              timestamp: data.timestamp
            };
            this.setData({ realtimeData });

            // 暂时注释掉图表更新
            /*
            const ecComponent = this.selectComponent(`#${type}Ec`);
            if (ecComponent) {
              ecComponent.init((canvas, width, height, dpr) => {
                const chart = echarts.init(canvas, null, {
                  width: width,
                  height: height,
                  devicePixelRatio: dpr
                });
                
                const option = {
                  title: {
                    text: `${type === 'electric' ? '电' : type === 'water' ? '水' : '燃气'}表本月累计用量`,
                    left: 'center'
                  },
                  tooltip: {
                    trigger: 'axis',
                    formatter: function(params) {
                      const data = params[0];
                      const value = type === 'electric' ? data.value[1].toFixed(3) + 'kWh' :
                                  type === 'water' ? data.value[1].toFixed(3) + 'm³' :
                                  data.value[1].toFixed(3) + 'm³';
                      return `${data.axisValue}<br/>${value}`;
                    }
                  },
                  xAxis: {
                    type: 'time',
                    splitLine: {
                      show: false
                    }
                  },
                  yAxis: {
                    type: 'value',
                    splitLine: {
                      show: true
                    },
                    axisLabel: {
                      formatter: function(value) {
                        return value.toFixed(3);
                      }
                    }
                  },
                  series: [{
                    name: type === 'electric' ? '累计电量' : type === 'water' ? '累计水量' : '累计气量',
                    type: 'line',
                    data: [[new Date(data.timestamp), 
                      type === 'electric' ? data.totalEnergy :
                      type === 'water' ? data.totalVolume :
                      data.totalVolume]],
                    smooth: true,
                    showSymbol: false,
                    areaStyle: {
                      opacity: 0.1
                    }
                  }]
                };
                
                chart.setOption(option);
                return chart;
              });
            }
            */
          }
        }
      }
    });
  },

  // 加载历史用量数据
  loadHistoricalData() {
    const { houseId } = this.data;
    if (!houseId) {
      console.log('houseId为空，无法加载历史数据');
      return;
    }

    console.log('开始加载历史数据，houseId:', houseId);

    wx.showLoading({
      title: '加载中...',
    });

    // 获取当前月份
    const now = new Date();
    const currentMonth = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;
    this.setData({ currentMonth });

    wx.request({
      url: `${app.globalData.baseUrl}/api/usage/historical`,
      data: { 
        houseId,
        month: currentMonth
      },
      success: (res) => {
        console.log('历史数据接口返回:', res.data);
        if (res.data.code === 0) {
          const historicalData = res.data.data || [];
          console.log('处理后的历史数据:', historicalData);
          
          if (historicalData.length === 0) {
            wx.showToast({
              title: '暂无历史数据',
              icon: 'none'
            });
          }
          
          // 按月份降序排序
          historicalData.sort((a, b) => {
            return new Date(b.month) - new Date(a.month);
          });
          
          this.setData({ 
            historicalData,
            currentTab: 'historical' // 切换到历史用量标签
          }, () => {
            console.log('历史数据已更新到视图:', this.data.historicalData);
          });
        } else {
          wx.showToast({
            title: res.data.msg || '加载失败',
            icon: 'none'
          });
        }
      },
      fail: (err) => {
        console.error('加载历史数据失败:', err);
        wx.showToast({
          title: '网络错误',
          icon: 'none'
        });
      },
      complete: () => {
        wx.hideLoading();
      }
    });
  },

  // 按月份查询历史数据
  queryHistoricalData() {
    const { houseId, currentMonth } = this.data;
    if (!houseId) {
      wx.showToast({
        title: '请先选择房屋',
        icon: 'none'
      });
      return;
    }

    if (!currentMonth) {
      wx.showToast({
        title: '请选择月份',
        icon: 'none'
      });
      return;
    }

    console.log('开始查询历史数据，houseId:', houseId, 'currentMonth:', currentMonth);

    wx.showLoading({
      title: '查询中...',
    });

    wx.request({
      url: `${app.globalData.baseUrl}/api/usage/historical`,
      data: {
        houseId,
        month: currentMonth
      },
      success: (res) => {
        console.log('查询历史数据接口返回:', res.data);
        if (res.data.code === 0) {
          const historicalData = res.data.data || [];
          console.log('处理后的历史数据:', historicalData);
          
          if (historicalData.length === 0) {
            wx.showToast({
              title: '该月份暂无数据',
              icon: 'none'
            });
          }
          
          // 按月份降序排序
          historicalData.sort((a, b) => {
            return new Date(b.month) - new Date(a.month);
          });
          
          this.setData({ historicalData }, () => {
            console.log('历史数据已更新到视图:', this.data.historicalData);
          });
        } else {
          wx.showToast({
            title: res.data.msg || '查询失败',
            icon: 'none'
          });
        }
      },
      fail: (err) => {
        console.error('查询历史数据失败:', err);
        wx.showToast({
          title: '网络错误',
          icon: 'none'
        });
      },
      complete: () => {
        wx.hideLoading();
      }
    });
  },

  // 重置历史数据查询
  resetHistoricalData() {
    const { houseId } = this.data;
    if (!houseId) {
      wx.showToast({
        title: '请先选择房屋',
        icon: 'none'
      });
      return;
    }

    // 重置月份选择
    const now = new Date();
    const currentMonth = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`;
    this.setData({ currentMonth });

    // 重新加载所有历史数据
    this.loadHistoricalData();
  },

  // 切换标签页
  switchTab(e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({ currentTab: tab });
  },

  // 月份选择器变化
  onMonthChange(e) {
    this.setData({
      currentMonth: e.detail.value
    });
  }
})

// 初始化图表
function initChart(canvas, width, height, dpr) {
  // 暂时返回空图表
  return {
    setOption: function() {}
  };
}