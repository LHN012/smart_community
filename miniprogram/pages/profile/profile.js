// pages/profile/profile.js
const app = getApp()

Page({

  /**
   * 页面的初始数据
   */
  data: {
    userInfo: null,
    isWxBound: false,
    wxUserInfo: null,
    loginType: '' // 'normal' 或 'wx'
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    this.loadUserInfo();
    this.checkWxBinding();
  },

  onShow() {
    // 每次显示页面时重新加载用户信息
    this.loadUserInfo();
    this.checkWxBinding();
  },

  // 检查token是否过期
  isTokenExpired(token) {
    if (!token) return true;
    
    try {
      // 检查是否是JWT token（包含两个点）
      if (token.split('.').length === 3) {
        // 移除Bearer前缀
        const tokenWithoutBearer = token.replace('Bearer ', '');
        
        // 解析JWT token
        const base64Url = tokenWithoutBearer.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
          return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        const payload = JSON.parse(jsonPayload);
        const expirationTime = payload.exp * 1000; // 转换为毫秒
        const currentTime = Date.now();

        console.log('JWT Token解析结果:', payload);
        console.log('Token过期时间:', new Date(expirationTime));
        console.log('当前时间:', new Date(currentTime));
        console.log('是否过期:', currentTime >= expirationTime);

        return currentTime >= expirationTime;
      } else {
        // 非JWT token，检查是否存在于会话中
        console.log('非JWT token，检查会话状态');
        return false; // 假设非JWT token不会过期
      }
    } catch (error) {
      console.error('解析token失败:', error);
      return false; // 如果解析失败，认为token有效
    }
  },

  // 加载用户信息
  loadUserInfo() {
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo');
    console.log('从Storage获取的token:', token);
    console.log('从Storage获取的userInfo:', userInfo);
    
    if (!token || !userInfo) {
      console.log('未找到登录信息，跳转到登录页');
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      wx.redirectTo({
        url: '/pages/login/login'
      });
      return;
    }

    // 检查token是否过期
    if (this.isTokenExpired(token)) {
      console.log('Token已过期，跳转到登录页');
      wx.showToast({
        title: '登录已过期，请重新登录',
        icon: 'none'
      });
      // 清除登录信息
      wx.removeStorageSync('token');
      wx.removeStorageSync('userInfo');
      // 跳转到登录页
      setTimeout(() => {
        wx.redirectTo({
          url: '/pages/login/login'
        });
      }, 1500);
      return;
    }

    // 先设置本地存储的用户信息
    this.setData({
      userInfo: userInfo
    });

    // 确保token格式正确
    const authHeader = token.startsWith('Bearer ') ? token : `Bearer ${token}`;
    console.log('发送的Authorization header:', authHeader);

    // 判断登录类型
    const isWxLogin = userInfo.openid != null;
    this.setData({ loginType: isWxLogin ? 'wx' : 'normal' });

    // 根据登录类型选择不同的API
    const apiUrl = isWxLogin ? 
      'http://localhost:8080/api/wx/user/info' : 
      'http://localhost:8080/api/user/info';

    // 准备请求参数
    const requestData = isWxLogin ? { openid: userInfo.openid } : {};

    // 发送请求
    wx.request({
      url: apiUrl,
      method: 'GET',
      header: {
        'Authorization': authHeader,
        'content-type': 'application/json'
      },
      data: requestData,
      success: (res) => {
        console.log('获取用户信息响应:', res);
        if (res.statusCode === 200 && res.data) {
          // 更新本地存储和页面数据
          const updatedUserInfo = { ...userInfo, ...res.data };
          wx.setStorageSync('userInfo', updatedUserInfo);
          this.setData({
            userInfo: updatedUserInfo
          });
        } else if (res.statusCode === 401 || res.statusCode === 403) {
          console.log('Token验证失败，状态码:', res.statusCode);
          console.log('Token内容:', token);
          console.log('响应头:', res.header);
          
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
            wx.redirectTo({
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

    // 确保token格式正确
    const authHeader = token.startsWith('Bearer ') ? token : `Bearer ${token}`;

    // 检查是否已绑定
    wx.request({
      url: `http://localhost:8080/api/wx/user/info?userId=${userInfo.userId}`,
      method: 'GET',
      header: {
        'Authorization': authHeader,
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
      wx.redirectTo({
        url: '/pages/login/login'
      });
      return;
    }

    // 确保token格式正确
    const authHeader = token.startsWith('Bearer ') ? token : `Bearer ${token}`;

    wx.login({
      success: (res) => {
        if (res.code) {
          console.log('获取到微信登录code:', res.code);
          wx.request({
            url: 'http://localhost:8080/api/wx/bind',
            method: 'POST',
            header: {
              'Authorization': authHeader,
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