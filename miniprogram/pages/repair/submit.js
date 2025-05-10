const app = getApp()

Page({
  data: {
    userInfo: null,
    location: '',
    description: ''
  },

  onLoad() {
    const userInfo = wx.getStorageSync('userInfo')
    if (userInfo) {
      this.setData({
        userInfo: userInfo
      })
    }
  },

  submitRequest() {
    if (!this.data.userInfo) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      })
      return
    }

    if (!this.data.description) {
      wx.showToast({
        title: '请填写故障描述',
        icon: 'none'
      })
      return
    }

    if (!this.data.location) {
      wx.showToast({
        title: '请填写故障位置',
        icon: 'none'
      })
      return
    }

    wx.showLoading({
      title: '提交中...'
    })

    wx.request({
      url: 'http://localhost:8080/api/maintenance-requests',
      method: 'POST',
      data: {
        userId: this.data.userInfo.userId,
        location: this.data.location,
        description: this.data.description,
        status: '待处理'
      },
      success: (res) => {
        wx.hideLoading()
        if (res.statusCode === 200) {
          wx.showToast({
            title: '提交成功',
            icon: 'success'
          })
          setTimeout(() => {
            wx.navigateBack()
          }, 1500)
        } else {
          wx.showToast({
            title: '提交失败',
            icon: 'none'
          })
        }
      },
      fail: () => {
        wx.hideLoading()
        wx.showToast({
          title: '网络错误',
          icon: 'none'
        })
      }
    })
  }
}) 