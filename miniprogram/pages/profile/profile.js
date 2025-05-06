// pages/profile/profile.js
const app = getApp()

Page({

  /**
   * 页面的初始数据
   */
  data: {
    userInfo: null,
    isWxBound: false,
    wxUserInfo: null
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.loadUserInfo();
    this.checkWxBinding();
  },

  onShow() {
    // 每次显示页面时重新检查绑定状态
    this.checkWxBinding();
  },

  // 加载用户信息
  loadUserInfo() {
    const token = wx.getStorageSync('token');
    console.log('从Storage获取的token:', token);
    
    if (!token) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      wx.navigateTo({
        url: '/pages/login/login'
      });
      return;
    }

    const authHeader = `Bearer ${token}`;
    console.log('发送的Authorization header:', authHeader);

    wx.request({
      url: 'http://localhost:8080/api/user/info',
      method: 'GET',
      header: {
        'Authorization': authHeader,
        'content-type': 'application/json',
        'Accept': 'application/json'
      },
      withCredentials: true,
      success: (res) => {
        console.log('获取用户信息响应:', res);
        if (res.statusCode === 200) {
          this.setData({
            userInfo: res.data
          });
        } else if (res.statusCode === 401 || res.statusCode === 403) {
          console.log('Token验证失败，状态码:', res.statusCode);
          
          // 检查token是否过期
          if (res.statusCode === 401) {
            wx.showToast({
              title: '登录已过期，请重新登录',
              icon: 'none'
            });
          } else {
            wx.showToast({
              title: '无权限访问',
              icon: 'none'
            });
          }
          
          // 清除登录信息
          wx.removeStorageSync('token');
          wx.removeStorageSync('userInfo');
          
          // 跳转到登录页
          setTimeout(() => {
            wx.navigateTo({
              url: '/pages/login/login'
            });
          }, 1500);
        } else {
          console.error('未知错误:', res);
          wx.showToast({
            title: '获取用户信息失败',
            icon: 'none'
          });
        }
      },
      fail: (err) => {
        console.error('请求失败:', err);
        wx.showToast({
          title: '网络错误',
          icon: 'none'
        });
      }
    });
  },

  // 检查微信绑定状态
  checkWxBinding() {
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo');
    
    if (!token || !userInfo || !userInfo.userId) {
      this.setData({
        isWxBound: false,
        wxUserInfo: null
      });
      return;
    }

    // 如果已经有openid，说明已经绑定过微信
    if (userInfo.openid) {
      this.setData({
        isWxBound: true,
        wxUserInfo: userInfo
      });
      return;
    }

    // 否则检查是否已绑定
    wx.request({
      url: `http://localhost:8080/api/wx/user/info?userId=${userInfo.userId}`,
      method: 'GET',
      header: {
        'Authorization': `Bearer ${token}`,
        'content-type': 'application/json'
      },
      success: (res) => {
        console.log('检查微信绑定状态响应:', res);
        if (res.statusCode === 200 && res.data) {
          this.setData({
            isWxBound: true,
            wxUserInfo: res.data
          });
          // 更新本地存储的用户信息
          const updatedUserInfo = { ...userInfo, ...res.data };
          wx.setStorageSync('userInfo', updatedUserInfo);
        } else {
          this.setData({
            isWxBound: false,
            wxUserInfo: null
          });
        }
      },
      fail: (err) => {
        console.error('检查微信绑定状态失败:', err);
        this.setData({
          isWxBound: false,
          wxUserInfo: null
        });
      }
    });
  },

  // 绑定微信账号
  bindWxAccount() {
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo');
    
    if (!token || !userInfo || !userInfo.userId) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      wx.navigateTo({
        url: '/pages/login/login'
      });
      return;
    }

    wx.login({
      success: (res) => {
        if (res.code) {
          console.log('获取到微信登录code:', res.code);
          wx.request({
            url: 'http://localhost:8080/api/wx/bind',
            method: 'POST',
            header: {
              'Authorization': `Bearer ${token}`,
              'content-type': 'application/json'
            },
            data: {
              code: res.code,
              userId: userInfo.userId
            },
            success: (response) => {
              console.log('绑定微信响应:', response);
              if (response.statusCode === 200) {
                wx.showToast({
                  title: '绑定成功',
                  icon: 'success'
                });
                // 重新检查绑定状态
                this.checkWxBinding();
              } else {
                wx.showToast({
                  title: response.data || '绑定失败',
                  icon: 'none'
                });
              }
            },
            fail: (err) => {
              console.error('绑定微信失败:', err);
              wx.showToast({
                title: '网络错误',
                icon: 'none'
              });
            }
          });
        } else {
          wx.showToast({
            title: '获取微信登录凭证失败',
            icon: 'none'
          });
        }
      },
      fail: (err) => {
        console.error('微信登录失败:', err);
        wx.showToast({
          title: '微信登录失败',
          icon: 'none'
        });
      }
    });
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {

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

  }
})