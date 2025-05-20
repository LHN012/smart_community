// pages/payment/payment.js
const app = getApp()

Page({

  /**
   * 页面的初始数据
   */
  data: {
    currentTab: 'recharge', // 默认显示充值记录
    currentHouseIndex: 0,
    currentMonth: '',
    houses: [], // 用户绑定的所有房屋
    rechargeRecords: [], // 充值记录
    paymentRecords: [], // 缴纳记录
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    // 获取用户信息
    const userInfo = wx.getStorageSync('userInfo')
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

  // 加载用户绑定的房屋列表
  loadUserHouses(userId) {
    wx.showLoading({
      title: '加载中...',
    })

    wx.request({
      url: `${app.globalData.baseUrl}/api/user/houses`,
      data: {
        userId: userId
      },
      success: (res) => {
        if (res.data && Array.isArray(res.data)) {
          const houses = res.data || []
          this.setData({ 
            houses,
            currentHouseIndex: 0,
            currentMonth: '' // 清空月份选择
          }, () => {
            // 加载充值记录
            this.loadRechargeRecords()
            // 加载缴纳记录
            this.loadPaymentRecords()
          })
        } else {
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

  // 切换标签页
  switchTab(e) {
    const tab = e.currentTarget.dataset.tab
    this.setData({ currentTab: tab })
  },

  // 房屋选择变化
  onHouseChange(e) {
    const index = e.detail.value
    this.setData({
      currentHouseIndex: index
    })
  },

  // 月份选择变化
  onMonthChange(e) {
    this.setData({
      currentMonth: e.detail.value
    })
  },

  // 加载充值记录
  loadRechargeRecords() {
    const { houses, currentHouseIndex, currentMonth } = this.data
    if (!houses.length) return

    const houseId = houses[currentHouseIndex].houseId

    wx.showLoading({
      title: '加载中...',
    })

    wx.request({
      url: `${app.globalData.baseUrl}/api/recharge/records`,
      data: {
        houseId,
        month: currentMonth
      },
      success: (res) => {
        if (res.data && Array.isArray(res.data)) {
          // 处理日期格式
          const records = res.data.map(item => ({
            ...item,
            rechargeTime: item.rechargeTime.split('T')[0] + ' ' + item.rechargeTime.split('T')[1].substring(0, 8)
          }))
          this.setData({
            rechargeRecords: records
          })
        } else {
          wx.showToast({
            title: '加载失败',
            icon: 'none'
          })
        }
      },
      fail: (err) => {
        console.error('加载充值记录失败:', err)
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

  // 加载缴纳记录
  loadPaymentRecords() {
    const { houses, currentHouseIndex, currentMonth } = this.data
    if (!houses.length) return

    const houseId = houses[currentHouseIndex].houseId

    wx.showLoading({
      title: '加载中...',
    })

    wx.request({
      url: `${app.globalData.baseUrl}/api/payment/records/list`,
      data: {
        houseId,
        month: currentMonth
      },
      success: (res) => {
        if (res.data && Array.isArray(res.data)) {
          // 处理日期格式
          const records = res.data.map(item => ({
            ...item,
            createDate: item.createDate.split('T')[0] + ' ' + item.createDate.split('T')[1].substring(0, 8)
          }))
          this.setData({
            paymentRecords: records
          })
        } else {
          wx.showToast({
            title: '加载失败',
            icon: 'none'
          })
        }
      },
      fail: (err) => {
        console.error('加载缴纳记录失败:', err)
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

  // 查询充值记录
  queryRechargeRecords() {
    this.loadRechargeRecords()
  },

  // 查询缴纳记录
  queryPaymentRecords() {
    this.loadPaymentRecords()
  },

  // 重置充值记录查询
  resetRechargeRecords() {
    this.setData({
      currentMonth: ''
    }, () => {
      this.loadRechargeRecords()
    })
  },

  // 重置缴纳记录查询
  resetPaymentRecords() {
    this.setData({
      currentMonth: ''
    }, () => {
      this.loadPaymentRecords()
    })
  }
})