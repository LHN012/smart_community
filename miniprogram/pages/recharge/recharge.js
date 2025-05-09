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
    showPaymentOptions: false // 是否显示支付方式选择
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
    this.loadUserInfo();
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
        if (res.statusCode === 200 && res.data) {
          const houses = res.data;
          this.setData({ houses });
          
          // 默认选择第一个房屋
          if (houses.length > 0) {
            this.selectHouse(houses[0]);
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
  selectHouse(house) {
    this.setData({
      currentHouse: house,
      balance: house.balance
    });
  },

  // 房屋选择器变化
  onHouseChange(e) {
    const index = e.detail.value;
    const house = this.data.houses[index];
    this.selectHouse(house);
  },

  // 输入充值金额
  onAmountInput(e) {
    this.setData({
      rechargeAmount: e.detail.value
    });
  },

  // 显示支付方式选择
  showPayment() {
    if (!this.data.rechargeAmount || isNaN(this.data.rechargeAmount) || this.data.rechargeAmount <= 0) {
      wx.showToast({
        title: '请输入有效金额',
        icon: 'none'
      });
      return;
    }
    this.setData({
      showPaymentOptions: true
    });
  },

  // 选择支付方式
  async choosePayment(e) {
    const method = e.currentTarget.dataset.method;
    const amount = parseFloat(this.data.rechargeAmount);
    
    try {
      // 调用支付接口
      const payResult = await this.callPaymentAPI(method, amount);
      
      if (payResult.success) {
        // 支付成功，记录充值
        await this.recordRecharge(method, amount);
        
        wx.showToast({
          title: '充值成功',
          icon: 'success'
        });
        
        // 刷新房屋信息
        this.loadUserHouses();
      } else {
        wx.showToast({
          title: payResult.message || '支付失败',
          icon: 'none'
        });
      }
    } catch (error) {
      console.error('支付过程出错:', error);
      wx.showToast({
        title: '支付失败',
        icon: 'none'
      });
    }
    
    this.setData({
      showPaymentOptions: false,
      rechargeAmount: ''
    });
  },

  // 调用支付接口
  callPaymentAPI(method, amount) {
    return new Promise((resolve, reject) => {
      if (method === '微信') {
        wx.requestPayment({
          timeStamp: '',
          nonceStr: '',
          package: '',
          signType: 'MD5',
          paySign: '',
          success: (res) => {
            resolve({ success: true });
          },
          fail: (err) => {
            resolve({ success: false, message: err.errMsg });
          }
        });
      } else if (method === '支付宝') {
        // 调用支付宝支付接口
        wx.request({
          url: 'http://localhost:8080/api/payment/alipay',
          method: 'POST',
          data: {
            amount: amount,
            houseId: this.data.currentHouse.house_id
          },
          success: (res) => {
            if (res.statusCode === 200) {
              resolve({ success: true });
            } else {
              resolve({ success: false, message: res.data.message });
            }
          },
          fail: (err) => {
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

    return new Promise((resolve, reject) => {
      wx.request({
        url: 'http://localhost:8080/api/recharge/record',
        method: 'POST',
        header: {
          'Authorization': `Bearer ${token}`,
          'content-type': 'application/json'
        },
        data: {
          houseId: this.data.currentHouse.house_id,
          userId: userInfo.userId,
          rechargeAmount: amount,
          rechargeChannel: '线上',
          paymentMethod: method
        },
        success: (res) => {
          if (res.statusCode === 200) {
            resolve();
          } else {
            reject(new Error(res.data.message));
          }
        },
        fail: (err) => {
          reject(err);
        }
      });
    });
  }
})