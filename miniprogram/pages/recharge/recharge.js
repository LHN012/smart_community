// pages/recharge/recharge.js
const app = getApp()

Page({

  /**
   * 页面的初始数据
   */
  data: {
    userInfo: null,
    houses: [], // 用户绑定的所有房屋
    currentHouse: null, // 当前选中的房屋
    balance: 0, // 当前房屋余额
    rechargeAmount: '', // 充值金额
    showPaymentOptions: false, // 是否显示支付方式选择
    isProcessing: false, // 防止重复点击
    isFirstLoad: true // 标记是否是首次加载
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.loadUserInfo();
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
    // 只在首次加载时调用loadUserInfo
    if (this.data.isFirstLoad) {
      this.loadUserInfo();
      this.setData({
        isFirstLoad: false
      });
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

  // 加载用户信息
  loadUserInfo() {
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo');
    
    if (!token || !userInfo) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      wx.navigateTo({
        url: '/pages/login/login'
      });
      return;
    }

    this.setData({ userInfo });
    this.loadUserHouses();
  },

  // 加载用户绑定的房屋
  loadUserHouses() {
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo');

    wx.request({
      url: `http://localhost:8080/api/user/houses?userId=${userInfo.userId}`,
      method: 'GET',
      header: {
        'Authorization': `Bearer ${token}`,
        'content-type': 'application/json'
      },
      success: (res) => {
        console.log('获取房屋信息响应:', res.data);
        if (res.statusCode === 200 && res.data) {
          const houses = res.data;
          this.setData({ houses });
          
          // 默认选择第一个房屋
          if (houses.length > 0) {
            this.selectHouse(houses[0]);
          } else {
            wx.showToast({
              title: '未绑定房屋',
              icon: 'none'
            });
          }
        }
      },
      fail: (err) => {
        console.error('获取房屋信息失败:', err);
        wx.showToast({
          title: '获取房屋信息失败',
          icon: 'none'
        });
      }
    });
  },

  // 选择房屋
  async selectHouse(house) {
    console.log('选择房屋:', house);
    if (!house || !house.houseId) {
      console.error('房屋信息不完整:', house);
      return;
    }

    // 重新获取房屋信息以确保余额是最新的
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo');

    try {
      const res = await new Promise((resolve, reject) => {
        wx.request({
          url: `http://localhost:8080/api/user/houses?userId=${userInfo.userId}`,
          method: 'GET',
          header: {
            'Authorization': `Bearer ${token}`,
            'content-type': 'application/json'
          },
          success: resolve,
          fail: reject
        });
      });

      if (res.statusCode === 200 && res.data) {
        const houses = res.data;
        const updatedHouse = houses.find(h => h.houseId === house.houseId);
        
        if (updatedHouse) {
          this.setData({
            currentHouse: updatedHouse,
            balance: updatedHouse.balance || 0,
            houses: houses // 同时更新房屋列表
          });
        } else {
          this.setData({
            currentHouse: house,
            balance: house.balance || 0
          });
        }
      }
    } catch (error) {
      console.error('获取房屋信息失败:', error);
      // 如果获取失败，使用传入的房屋信息
      this.setData({
        currentHouse: house,
        balance: house.balance || 0
      });
    }
  },

  // 房屋选择器变化
  async onHouseChange(e) {
    const index = e.detail.value;
    const house = this.data.houses[index];
    console.log('房屋选择器变化，选中的房屋:', house);
    await this.selectHouse(house);
  },

  // 输入充值金额
  onAmountInput(e) {
    this.setData({
      rechargeAmount: e.detail.value
    });
  },

  // 显示支付方式选择
  showPayment() {
    if (!this.data.currentHouse || !this.data.currentHouse.houseId) {
      wx.showToast({
        title: '请先选择房屋',
        icon: 'none'
      });
      return;
    }

    if (!this.data.rechargeAmount || isNaN(this.data.rechargeAmount) || this.data.rechargeAmount <= 0) {
      wx.showToast({
        title: '请输入有效金额',
        icon: 'none'
      });
      return;
    }

    console.log('显示支付方式选择，当前房屋:', this.data.currentHouse);
    this.setData({
      showPaymentOptions: true
    });
  },

  // 选择支付方式
  async choosePayment(e) {
    const method = e.currentTarget.dataset.method;
    const amount = parseFloat(this.data.rechargeAmount);
    
    if (!this.data.currentHouse || !this.data.currentHouse.houseId) {
      wx.showToast({
        title: '请先选择房屋',
        icon: 'none'
      });
      return;
    }

    // 防止重复点击
    if (this.isProcessing) {
      console.log('正在处理支付，请勿重复点击');
      return;
    }
    
    this.setData({ isProcessing: true });
    
    try {
      console.log('开始支付，当前房屋:', this.data.currentHouse);
      // 调用支付接口
      const payResult = await this.callPaymentAPI(method, amount);
      
      if (payResult.success) {
        wx.showToast({
          title: '充值成功',
          icon: 'success'
        });
        
        // 重新加载房屋信息以获取最新余额
        await this.refreshCurrentHouse();
      } else {
        wx.showToast({
          title: payResult.message || '支付失败',
          icon: 'none'
        });
      }
    } catch (error) {
      console.error('支付过程出错:', error);
      wx.showToast({
        title: error.message || '支付失败',
        icon: 'none'
      });
    } finally {
      this.setData({
        isProcessing: false,
        showPaymentOptions: false,
        rechargeAmount: ''
      });
    }
  },

  // 刷新当前房屋信息
  async refreshCurrentHouse() {
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo');
    const currentHouseId = this.data.currentHouse.houseId;

    try {
      const res = await new Promise((resolve, reject) => {
        wx.request({
          url: `http://localhost:8080/api/user/houses?userId=${userInfo.userId}`,
          method: 'GET',
          header: {
            'Authorization': `Bearer ${token}`,
            'content-type': 'application/json'
          },
          success: resolve,
          fail: reject
        });
      });

      if (res.statusCode === 200 && res.data) {
        const houses = res.data;
        const updatedHouse = houses.find(h => h.houseId === currentHouseId);
        
        if (updatedHouse) {
          this.setData({
            currentHouse: updatedHouse,
            balance: updatedHouse.balance || 0,
            houses: houses // 同时更新房屋列表
          });
        }
      }
    } catch (error) {
      console.error('刷新房屋信息失败:', error);
    }
  },

  // 调用支付接口
  callPaymentAPI(method, amount) {
    return new Promise((resolve, reject) => {
      const token = wx.getStorageSync('token');
      const userInfo = wx.getStorageSync('userInfo');
      
      if (!token || !userInfo) {
        reject(new Error('未登录'));
        return;
      }

      // 检查是否选择了房屋
      if (!this.data.currentHouse || !this.data.currentHouse.houseId) {
        reject(new Error('请先选择房屋'));
        return;
      }

      const requestData = {
        userId: userInfo.userId,
        houseId: this.data.currentHouse.houseId,
        amount: amount,
        description: '物业费充值'
      };

      console.log('支付请求参数:', requestData);

      if (method === '微信') {
        // 先调用后端创建订单
        wx.request({
          url: 'http://localhost:8080/api/payment/wechat',
          method: 'POST',
          header: {
            'Authorization': `Bearer ${token}`,
            'content-type': 'application/json'
          },
          data: requestData,
          success: (res) => {
            console.log('微信支付创建订单响应:', res.data);
            if (res.statusCode === 200 && res.data.success && res.data.data) {
              const payParams = res.data.data;
              
              // 检查支付参数完整性
              if (!payParams.timeStamp || !payParams.nonceStr || 
                  !payParams.package || !payParams.paySign) {
                console.error('支付参数不完整:', payParams);
                resolve({ 
                  success: false, 
                  message: '支付参数不完整' 
                });
                return;
              }

              // 调用微信支付
              wx.requestPayment({
                timeStamp: payParams.timeStamp,
                nonceStr: payParams.nonceStr,
                package: payParams.package,
                signType: payParams.signType || 'MD5',
                paySign: payParams.paySign,
                success: (payRes) => {
                  console.log('微信支付成功:', payRes);
                  // 支付成功后记录充值
                  this.recordRecharge(method, amount).then(() => {
                    resolve({ success: true });
                  }).catch(err => {
                    console.error('记录充值失败:', err);
                    resolve({ success: false, message: '支付成功但记录失败' });
                  });
                },
                fail: (err) => {
                  console.error('微信支付失败:', err);
                  if (err.errMsg.includes('no permission')) {
                    wx.showModal({
                      title: '提示',
                      content: '当前环境不支持微信支付，是否继续测试？',
                      success: (res) => {
                        if (res.confirm) {
                          // 模拟支付成功
                          console.log('用户选择继续测试，模拟支付成功');
                          // 支付成功后记录充值
                          this.recordRecharge(method, amount).then(() => {
                            resolve({ success: true });
                          }).catch(err => {
                            console.error('记录充值失败:', err);
                            resolve({ success: false, message: '支付成功但记录失败' });
                          });
                        } else {
                          resolve({ 
                            success: false, 
                            message: '用户取消支付' 
                          });
                        }
                      }
                    });
                  } else {
                    resolve({ 
                      success: false, 
                      message: err.errMsg || '支付失败' 
                    });
                  }
                }
              });
            } else {
              console.error('创建微信支付订单失败:', res.data);
              resolve({ 
                success: false, 
                message: res.data.message || '创建支付订单失败' 
              });
            }
          },
          fail: (err) => {
            console.error('请求微信支付接口失败:', err);
            resolve({ success: false, message: '网络错误' });
          }
        });
      } else if (method === '支付宝') {
        // 调用支付宝支付接口
        wx.request({
          url: 'http://localhost:8080/api/payment/alipay',
          method: 'POST',
          header: {
            'Authorization': `Bearer ${token}`,
            'content-type': 'application/json'
          },
          data: requestData,
          success: (res) => {
            console.log('支付宝支付响应:', res.data);
            if (res.statusCode === 200 && res.data.success) {
              // 支付宝支付成功后，直接返回成功，不需要再次记录充值
              resolve({ success: true, record: res.data.record });
            } else {
              console.error('支付宝支付失败:', res.data);
              resolve({ success: false, message: res.data.message || '支付失败' });
            }
          },
          fail: (err) => {
            console.error('请求支付宝接口失败:', err);
            resolve({ success: false, message: '网络错误' });
          }
        });
      }
    });
  },

  // 记录充值
  recordRecharge(method, amount) {
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo');

    if (!token || !userInfo) {
      return Promise.reject(new Error('未登录'));
    }

    // 检查是否选择了房屋
    if (!this.data.currentHouse || !this.data.currentHouse.houseId) {
      return Promise.reject(new Error('请先选择房屋'));
    }

    const requestData = {
      houseId: this.data.currentHouse.houseId,
      userId: userInfo.userId,
      amount: amount,
      payType: method,
      rechargeChannel: '线上',
      description: '物业费充值'
    };

    console.log('记录充值请求参数:', requestData);

    return new Promise((resolve, reject) => {
      wx.request({
        url: 'http://localhost:8080/api/recharge/record',
        method: 'POST',
        header: {
          'Authorization': `Bearer ${token}`,
          'content-type': 'application/json'
        },
        data: requestData,
        success: (res) => {
          console.log('记录充值响应:', res.data);
          if (res.statusCode === 200) {
            resolve(res.data);
          } else {
            reject(new Error(res.data.message || '记录充值失败'));
          }
        },
        fail: (err) => {
          console.error('请求记录充值接口失败:', err);
          reject(err);
        }
      });
    });
  }
})