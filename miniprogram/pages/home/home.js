Page({
  data: {
    userInfo: null
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
  },

  navigateTo(e) {
    const url = e.currentTarget.dataset.url
    if (url === '/pages/profile/profile') {
      wx.switchTab({ url })
    } else {
      wx.navigateTo({ url })
    }
  }
}) 