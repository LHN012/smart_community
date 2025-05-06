Page({
  data: {
    username: '',
    password: '',
    loading: false
  },

  onUsernameInput(e) {
    this.setData({
      username: e.detail.value
    })
  },

  onPasswordInput(e) {
    this.setData({
      password: e.detail.value
    })
  },

  onLoad() {
    // 检查是否已经登录
    const token = wx.getStorageSync('token');
    if (token) {
      this.navigateToHome();
    }
  },

  handleLogin() {
    if (!this.data.username || !this.data.password) {
      wx.showToast({
        title: '请输入用户名和密码',
        icon: 'none'
      });
      return;
    }

    this.setData({ loading: true });
    
    wx.request({
      url: 'http://localhost:8080/api/user/login',
      method: 'POST',
      header: {
        'content-type': 'application/json'
      },
      data: {
        username: this.data.username,
        password: this.data.password
      },
      success: (res) => {
        if (res.statusCode === 200) {
          const { token, user } = res.data;
          // 保存token和用户信息
          wx.setStorageSync('token', token);
          wx.setStorageSync('userInfo', user);
          this.navigateToHome();
        } else {
          wx.showToast({
            title: res.data || '登录失败',
            icon: 'none'
          });
        }
      },
      fail: () => {
        wx.showToast({
          title: '网络错误',
          icon: 'none'
        });
      },
      complete: () => {
        this.setData({ loading: false });
      }
    });
  },

  // 微信一键登录
  handleWechatLogin() {
    this.setData({ loading: true });
    
    wx.login({
      success: (res) => {
        if (res.code) {
          console.log('获取到微信登录code:', res.code);
          // 发送code到后端
          wx.request({
            url: 'http://localhost:8080/api/wx/login',
            method: 'POST',
            header: {
              'content-type': 'application/json'
            },
            data: {
              code: res.code
            },
            success: (response) => {
              console.log('微信登录响应:', response);
              if (response.statusCode === 200 && response.data) {
                const { token, user } = response.data;
                if (token && user) {
                  // 保存token和用户信息
                  wx.setStorageSync('token', token);
                  wx.setStorageSync('userInfo', user);
                  wx.showToast({
                    title: '登录成功',
                    icon: 'success',
                    duration: 1500,
                    complete: () => {
                      setTimeout(() => {
                        this.navigateToHome();
                      }, 1500);
                    }
                  });
                } else {
                  wx.showToast({
                    title: '登录数据不完整',
                    icon: 'none'
                  });
                }
              } else {
                wx.showToast({
                  title: response.data || '登录失败',
                  icon: 'none'
                });
              }
            },
            fail: (err) => {
              console.error('微信登录失败:', err);
              wx.showToast({
                title: '网络错误',
                icon: 'none'
              });
            },
            complete: () => {
              this.setData({ loading: false });
            }
          });
        } else {
          wx.showToast({
            title: '获取微信登录凭证失败',
            icon: 'none'
          });
          this.setData({ loading: false });
        }
      },
      fail: (err) => {
        console.error('微信登录失败:', err);
        wx.showToast({
          title: '微信登录失败',
          icon: 'none'
        });
        this.setData({ loading: false });
      }
    });
  },

  // 跳转到首页
  navigateToHome() {
    console.log('准备跳转到首页');
    wx.switchTab({
      url: '/pages/home/home',
      success: () => {
        console.log('跳转成功');
      },
      fail: (err) => {
        console.error('跳转失败:', err);
        // 如果switchTab失败，尝试使用redirectTo
        wx.redirectTo({
          url: '/pages/home/home',
          fail: (err) => {
            console.error('redirectTo也失败了:', err);
            wx.showToast({
              title: '页面跳转失败',
              icon: 'none'
            });
          }
        });
      }
    });
  }
}) 