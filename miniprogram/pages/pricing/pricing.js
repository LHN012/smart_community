const app = getApp();

Page({
  data: {
    currentTab: 'current',
    currentPricingList: [],
    historyPricingList: [],
    filteredHistoryList: [],
    searchDate: '',
    types: ['水', '电', '燃气', '物业'],
    loading: false,
    // 定义单位映射
    unitMap: {
      '水': '元/立方米',
      '电': '元/千瓦时',
      '燃气': '元/立方米',
      '物业': '元/月'
    }
  },

  onLoad: function() {
    this.loadCurrentPricingData();
    this.loadHistoryPricingData();
  },

  // 格式化日期
  formatDate: function(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
  },

  switchTab: function(e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({ 
      currentTab: tab,
      searchDate: '',
      filteredHistoryList: this.data.historyPricingList
    });
  },

  onDateChange: function(e) {
    this.setData({ searchDate: e.detail.value });
  },

  resetSearch: function() {
    this.setData({
      searchDate: '',
      filteredHistoryList: this.data.historyPricingList
    });
  },

  searchHistory: function() {
    const { searchDate, historyPricingList } = this.data;
    if (!searchDate) {
      wx.showToast({
        title: '请选择日期',
        icon: 'none'
      });
      return;
    }

    const searchDateObj = new Date(searchDate);
    const filteredList = historyPricingList.filter(group => {
      const effectiveDate = new Date(group.effectiveDate);
      const endDate = group.endDate ? new Date(group.endDate) : null;
      return searchDateObj >= effectiveDate && (!endDate || searchDateObj <= endDate);
    });

    if (filteredList.length === 0) {
      wx.showToast({
        title: '未找到相关记录',
        icon: 'none'
      });
    }

    this.setData({ filteredHistoryList: filteredList });
  },

  loadCurrentPricingData: function() {
    if (!app || !app.globalData) {
      console.error('app.globalData未定义');
      wx.showToast({
        title: '系统配置错误',
        icon: 'none'
      });
      return;
    }

    this.setData({ loading: true });
    const { types, unitMap } = this.data;
    const currentPricingList = [];
    
    // 并行请求所有类型的当前价格设置
    const promises = types.map(type => {
      return new Promise((resolve, reject) => {
        wx.request({
          url: `${app.globalData.baseUrl}/api/price-settings/${type}`,
          method: 'GET',
          success: (res) => {
            if (res.statusCode === 200 && res.data) {
              resolve({
                type,
                effectiveDate: this.formatDate(res.data[0]?.effectiveDate),
                unit: unitMap[type],
                items: res.data.map(item => ({
                  id: item.priceId,
                  level: item.level,
                  price: item.price,
                  startUsage: item.startUsage,
                  endUsage: item.endUsage,
                  paymentCycle: item.paymentCycle
                }))
              });
            } else {
              console.warn(`获取${type}价格设置失败:`, res);
              resolve(null);
            }
          },
          fail: (error) => {
            console.error(`获取${type}价格设置请求失败:`, error);
            reject(error);
          }
        });
      });
    });

    Promise.all(promises)
      .then(results => {
        const validResults = results.filter(result => result !== null);
        this.setData({ 
          currentPricingList: validResults,
          loading: false
        });
      })
      .catch(error => {
        console.error('加载当前价格设置失败:', error);
        this.setData({ loading: false });
        wx.showToast({
          title: '加载失败，请重试',
          icon: 'none',
          duration: 2000
        });
      });
  },

  loadHistoryPricingData: function() {
    if (!app || !app.globalData) {
      console.error('app.globalData未定义');
      wx.showToast({
        title: '系统配置错误',
        icon: 'none'
      });
      return;
    }

    this.setData({ loading: true });
    const { types, unitMap } = this.data;
    const historyPricingList = [];
    
    // 并行请求所有类型的历史价格设置
    const promises = types.map(type => {
      return new Promise((resolve, reject) => {
        wx.request({
          url: `${app.globalData.baseUrl}/api/price-settings/${type}/history`,
          method: 'GET',
          success: (res) => {
            if (res.statusCode === 200 && res.data) {
              resolve({
                type,
                effectiveDate: this.formatDate(res.data[0]?.effectiveDate),
                endDate: this.formatDate(res.data[0]?.endDate),
                unit: unitMap[type],
                items: res.data.map(item => ({
                  id: item.priceId,
                  level: item.level,
                  price: item.price,
                  startUsage: item.startUsage,
                  endUsage: item.endUsage,
                  paymentCycle: item.paymentCycle
                }))
              });
            } else {
              console.warn(`获取${type}历史价格设置失败:`, res);
              resolve(null);
            }
          },
          fail: (error) => {
            console.error(`获取${type}历史价格设置请求失败:`, error);
            reject(error);
          }
        });
      });
    });

    Promise.all(promises)
      .then(results => {
        const validResults = results.filter(result => result !== null);
        this.setData({ 
          historyPricingList: validResults,
          filteredHistoryList: validResults,
          loading: false
        });
      })
      .catch(error => {
        console.error('加载历史价格设置失败:', error);
        this.setData({ loading: false });
        wx.showToast({
          title: '加载失败，请重试',
          icon: 'none',
          duration: 2000
        });
      });
  }
}); 