// pages/notice/notice.js
const app = getApp()

Page({

  /**
   * 页面的初始数据
   */
  data: {
    typeOptions: [
      { label: '全部通知', value: 'all', initial: '全' },
      { label: '紧急通知', value: 'urgent', initial: '急' },
      { label: '普通通知', value: 'normal', initial: '普' },
      { label: '活动通知', value: 'activity', initial: '活' },
      { label: '维护通知', value: 'maintenance', initial: '维' },
      { label: '缴费通知', value: 'payment', initial: '缴' }
    ],
    // 添加中文类型映射
    typeMapping: {
      '公告': 'normal',
      '活动通知': 'activity',
      '紧急通知': 'urgent',
      '维护通知': 'maintenance',
      '缴费通知': 'payment'
    },
    typeIndex: 0,
    currentType: 'all',
    timeRange: 'current',
    notices: [],
    hasUnread: false,
    showDetail: false,
    currentNotice: null,
    userId: null,
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    // 从本地存储获取userInfo
    const userInfo = wx.getStorageSync('userInfo')
    if (!userInfo || !userInfo.userId) {
      wx.showToast({
        title: '请先登录',
        icon: 'none',
        success: () => {
          setTimeout(() => {
            wx.redirectTo({
              url: '/pages/login/login'
            })
          }, 1500)
        }
      })
      return
    }
    this.setData({ userId: parseInt(userInfo.userId) })
    this.loadNotices()
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
    this.checkUnreadNotices()
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

  // 切换通知类型
  switchType(e) {
    const index = e.detail.value;
    const type = this.data.typeOptions[index].value;
    this.setData({
      typeIndex: index,
      currentType: type
    });
    this.loadNotices();
  },

  // 切换时间范围
  switchTimeRange(e) {
    const range = e.currentTarget.dataset.range;
    this.setData({
      timeRange: range
    });
    this.loadNotices();
  },

  // 格式化时间
  formatTime(date) {
    const now = new Date()
    const target = new Date(date)
    const diff = now - target

    // 如果是今天
    if (target.toDateString() === now.toDateString()) {
      const hours = target.getHours().toString().padStart(2, '0')
      const minutes = target.getMinutes().toString().padStart(2, '0')
      return `今天 ${hours}:${minutes}`
    }

    // 如果是昨天
    const yesterday = new Date(now)
    yesterday.setDate(yesterday.getDate() - 1)
    if (target.toDateString() === yesterday.toDateString()) {
      const hours = target.getHours().toString().padStart(2, '0')
      const minutes = target.getMinutes().toString().padStart(2, '0')
      return `昨天 ${hours}:${minutes}`
    }

    // 其他日期
    const year = target.getFullYear()
    const month = (target.getMonth() + 1).toString().padStart(2, '0')
    const day = target.getDate().toString().padStart(2, '0')
    const hours = target.getHours().toString().padStart(2, '0')
    const minutes = target.getMinutes().toString().padStart(2, '0')
    
    // 如果是今年
    if (year === now.getFullYear()) {
      return `${month}-${day} ${hours}:${minutes}`
    }
    
    // 不是今年
    return `${year}-${month}-${day} ${hours}:${minutes}`
  },

  // 加载通知列表
  loadNotices() {
    const { currentType, timeRange, userId } = this.data
    if (!userId) return
    
    wx.request({
      url: `${app.globalData.baseUrl}/api/notice/list`,
      method: 'GET',
      data: {
        userId: parseInt(userId),
        type: currentType,
        timeRange
      },
      success: (res) => {
        if (res.data.code === 0) {
          const notices = res.data.data.map(notice => {
            const isRead = notice.isRead === true || notice.is_read === true || 
                         notice.isRead === 1 || notice.is_read === 1;
            
            let notificationType = notice.notificationType || notice.type || 'normal';
            const mappedType = this.data.typeMapping[notificationType] || 'normal';
            const typeOption = this.data.typeOptions.find(opt => opt.value === mappedType);
            
            return {
              ...notice,
              isRead,
              time: this.formatTime(notice.sendTime),
              notificationType: mappedType,
              typeInitial: typeOption ? typeOption.initial : '通'
            };
          });
          
          // 检查是否有未读通知
          const hasUnread = notices.some(notice => !notice.isRead);
          console.log('未读状态:', hasUnread);
          
          this.setData({
            notices,
            hasUnread
          });
        }
      }
    })
  },

  // 显示通知详情
  showNoticeDetail(e) {
    const noticeId = e.currentTarget.dataset.id
    const { userId } = this.data
    
    // 先标记为已读
    this.markAsRead(noticeId)
    
    // 然后获取详情
    wx.request({
      url: `${app.globalData.baseUrl}/api/notice/detail`,
      method: 'GET',
      data: { 
        noticeId: parseInt(noticeId),
        userId: parseInt(userId)
      },
      success: (res) => {
        if (res.data.code === 0) {
          const notice = res.data.data
          if (notice) {
            this.setData({
              currentNotice: {
                ...notice,
                time: this.formatTime(notice.sendTime)
              },
              showDetail: true
            })
          } else {
            wx.showToast({
              title: '获取通知详情失败',
              icon: 'none'
            })
          }
        } else {
          wx.showToast({
            title: res.data.msg || '获取通知详情失败',
            icon: 'none'
          })
        }
      },
      fail: (err) => {
        console.error('获取通知详情失败:', err)
        wx.showToast({
          title: '获取通知详情失败，请稍后重试',
          icon: 'none'
        })
      }
    })
  },

  // 关闭详情弹窗
  closeDetail() {
    this.setData({
      showDetail: false,
      currentNotice: null
    })
    // 刷新列表
    this.loadNotices()
  },

  // 标记单条通知为已读
  markAsRead(noticeId) {
    const { userId } = this.data
    if (!userId || !noticeId) return

    // 立即更新本地状态
    const notices = this.data.notices.map(notice => {
      if (notice.id === noticeId) {
        return { ...notice, isRead: true }
      }
      return notice
    })
    
    // 更新未读状态
    const hasUnread = notices.some(notice => !notice.isRead)
    
    this.setData({
      notices,
      hasUnread
    })

    // 发送请求到服务器
    wx.request({
      url: `${app.globalData.baseUrl}/api/notice/read`,
      method: 'POST',
      header: {
        'content-type': 'application/json'
      },
      data: {
        noticeId: parseInt(noticeId),
        userId: parseInt(userId)
      },
      success: (res) => {
        if (res.data.code === 0) {
          this.checkUnreadNotices()
        } else {
          console.error('标记已读失败:', res.data)
          // 如果失败，恢复未读状态
          const notices = this.data.notices.map(notice => {
            if (notice.id === noticeId) {
              return { ...notice, isRead: false }
            }
            return notice
          })
          this.setData({
            notices,
            hasUnread: notices.some(notice => !notice.isRead)
          })
          wx.showToast({
            title: '操作失败',
            icon: 'none'
          })
        }
      },
      fail: (err) => {
        console.error('标记已读请求失败:', err)
        // 如果失败，恢复未读状态
        const notices = this.data.notices.map(notice => {
          if (notice.id === noticeId) {
            return { ...notice, isRead: false }
          }
          return notice
        })
        this.setData({
          notices,
          hasUnread: notices.some(notice => !notice.isRead)
        })
        wx.showToast({
          title: '网络错误',
          icon: 'none'
        })
      }
    })
  },

  // 一键标记所有为已读
  markAllAsRead() {
    const { userId } = this.data
    if (!userId) return

    wx.request({
      url: `${app.globalData.baseUrl}/api/notice/readAll?userId=${userId}`,
      method: 'POST',
      success: (res) => {
        if (res.data.code === 0) {
          // 更新本地通知列表中的所有通知为已读
          const notices = this.data.notices.map(notice => ({
            ...notice,
            isRead: true
          }))
          this.setData({
            notices,
            hasUnread: false
          })
          this.checkUnreadNotices()
          wx.showToast({
            title: '已全部标记为已读',
            icon: 'success'
          })
        }
      }
    })
  },

  // 检查是否有未读通知
  checkUnreadNotices() {
    const { userId } = this.data
    if (!userId) return

    wx.request({
      url: `${app.globalData.baseUrl}/api/notice/unread/count`,
      method: 'GET',
      data: {
        userId: parseInt(userId)
      },
      success: (res) => {
        if (res.data.code === 0) {
          const hasUnread = res.data.data > 0
          console.log('未读数量:', res.data.data, '未读状态:', hasUnread);
          this.setData({ hasUnread })
          
          wx.hideTabBarRedDot({
            index: 1,
            complete: () => {
              if (hasUnread) {
                wx.showTabBarRedDot({
                  index: 1,
                  fail: (err) => {
                    console.error('显示小红点失败:', err)
                    setTimeout(() => {
                      wx.showTabBarRedDot({
                        index: 1
                      })
                    }, 100)
                  }
                })
              }
            }
          })
        }
      }
    })
  }
})