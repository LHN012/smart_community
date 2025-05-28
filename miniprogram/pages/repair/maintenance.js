Page({
  data: {
    requests: [],
    userInfo: null
  },

  onLoad() {
    const userInfo = wx.getStorageSync('userInfo')
    if (userInfo) {
      console.log('用户信息：', userInfo)
      this.setData({ userInfo })
      this.loadRequests()
    } else {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      })
    }
  },

  onShow() {
    this.loadRequests()
  },

  navigateToSubmit() {
    wx.navigateTo({
      url: '/pages/repair/submit'
    })
  },

  loadRequests() {
    if (!this.data.userInfo || !this.data.userInfo.userId) {
      wx.showToast({
        title: '用户信息不完整',
        icon: 'none'
      })
      return
    }

    wx.showLoading({
      title: '加载中...',
      mask: true
    })

    const userId = this.data.userInfo.userId
    console.log('正在获取用户报修列表，用户ID：', userId)

    const token = wx.getStorageSync('token')
    if (!token) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      })
      return
    }

    wx.request({
      url: `http://localhost:8080/api/maintenance-requests/user/${userId}`,
      method: 'GET',
      header: {
        'Authorization': token.startsWith('Bearer ') ? token : `Bearer ${token}`,
        'content-type': 'application/json'
      },
      success: (res) => {
        console.log('请求响应：', res)
        if (res.statusCode === 200) {
          this.setData({
            requests: res.data
          })
        } else {
          const errorMsg = res.data || '加载失败'
          console.error('请求失败：', errorMsg)
          wx.showToast({
            title: errorMsg,
            icon: 'none',
            duration: 2000
          })
        }
      },
      fail: (err) => {
        console.error('请求失败：', err)
        wx.showToast({
          title: '网络错误',
          icon: 'none',
          duration: 2000
        })
      },
      complete: () => {
        wx.hideLoading()
      }
    })
  },

  onPullDownRefresh() {
    this.loadRequests()
    wx.stopPullDownRefresh()
  }
}) 