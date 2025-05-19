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
    // 检查是否是tabBar页面
    const tabBarPages = ['/pages/home/home', '/pages/notice/notice', '/pages/profile/profile']
    
    if (tabBarPages.includes(url)) {
      wx.switchTab({ 
        url,
        fail: (err) => {
          console.error('switchTab失败:', err)
          wx.showToast({
            title: '页面跳转失败',
            icon: 'none'
          })
        }
      })
    } else {
      wx.navigateTo({ 
        url,
        fail: (err) => {
          console.error('navigateTo失败:', err)
          wx.showToast({
            title: '页面跳转失败',
            icon: 'none'
          })
        }
      })
    }
  }
}) 