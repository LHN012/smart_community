Page({
  data: {
    username: '',
    password: '',
    loading: false,
    wxLoginCode: '', // 存储微信登录code
    isAgree: false // 是否同意协议
  },

  handleInput(e) {
    const { field } = e.currentTarget.dataset;
    this.setData({
      [field]: e.detail.value
    });
  },

  handleAgreementChange(e) {
    this.setData({
      isAgree: e.detail.value.length > 0
    });
  },

  showServiceAgreement() {
    wx.navigateTo({
      url: '/pages/agreement/service'
    });
  },

  showPrivacyAgreement() {
    wx.navigateTo({
      url: '/pages/agreement/privacy'
    });
  },

  onLoad() {
    // 检查是否已经登录
    const token = wx.getStorageSync('token');
    if (token) {
      this.navigateToHome();
    }
  },

  handleLogin() {
    const { username, password, isAgree } = this.data;
    
    if (!username || !password) {
      wx.showToast({
        title: '请输入账号和密码',
        icon: 'none'
      });
      return;
    }

    if (!isAgree) {
      wx.showToast({
        title: '请阅读并同意用户协议',
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
        username: username,
        password: password
      },
      success: (res) => {
        if (res.statusCode === 200) {
          const { token, user } = res.data;
          if (!token || !user) {
            wx.showToast({
              title: '登录数据不完整',
              icon: 'none'
            });
            return;
          }
          
          // 确保token格式正确
          const formattedToken = token.startsWith('Bearer ') ? token : `Bearer ${token}`;
          
          // 保存token和用户信息
          wx.setStorageSync('token', formattedToken);
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

  handleWechatLogin() {
    if (!this.data.isAgree) {
      wx.showToast({
        title: '请阅读并同意用户协议',
        icon: 'none'
      });
      return;
    }

    this.setData({ loading: true });
    
    // 先获取用户信息
    wx.getUserProfile({
      desc: '用于完善用户资料',
      success: (profileRes) => {
        // 获取到用户信息后，再进行微信登录
        this.wxLogin(profileRes.userInfo);
      },
      fail: (err) => {
        console.error('获取用户信息失败:', err);
        wx.showToast({
          title: '需要授权才能登录',
          icon: 'none'
        });
        this.setData({ loading: false });
      }
    });
  },

  // 执行微信登录
  wxLogin(userInfo) {
    wx.login({
      success: (res) => {
        if (res.code) {
          // 发送code到后端
          wx.request({
            url: 'http://localhost:8080/api/wx/login',
            method: 'POST',
            header: {
              'content-type': 'application/json'
            },
            data: {
              code: res.code,
              userInfo: userInfo
            },
            success: (response) => {
              if (response.statusCode === 200 && response.data) {
                const { token, user } = response.data;
                if (token && user) {
                  // 确保token格式正确
                  const formattedToken = token.startsWith('Bearer ') ? token : `Bearer ${token}`;
                  
                  // 合并用户信息
                  const updatedUser = {
                    ...user,
                    nickname: userInfo.nickName,
                    avatarUrl: userInfo.avatarUrl,
                    username: user.username || userInfo.nickName // 确保username字段存在
                  };
                  
                  console.log('登录成功，用户信息：', updatedUser); // 添加日志
                  
                  // 保存token和用户信息
                  wx.setStorageSync('token', formattedToken);
                  wx.setStorageSync('userInfo', updatedUser);
                  
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
              console.error('微信登录请求失败:', err);
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
          console.error('获取微信登录code失败');
          wx.showToast({
            title: '微信登录失败',
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
}); 