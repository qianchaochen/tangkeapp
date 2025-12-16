# UniApp Frontend - WeChat Mini Program Login Implementation

This document covers the implementation of WeChat authentication on the UniApp frontend side.

## Directory Structure

```
uni_modules/
├── wechat-login/
│   ├── utils/
│   │   ├── wechatService.js
│   │   ├── tokenManager.js
│   │   └── apiClient.js
│   ├── pages/
│   │   ├── login.vue
│   │   └── profile.vue
│   ├── store/
│   │   └── auth.js
│   └── middleware/
│       └── authInterceptor.js
```

## 1. Token Manager (tokenManager.js)

Handles storing and retrieving JWT tokens from local storage.

```javascript
// stores tokens and manages expiration
const TokenManager = {
  TOKEN_KEY: 'wechat_auth_token',
  CUSTOMER_ID_KEY: 'wechat_customer_id',
  OPENID_KEY: 'wechat_openid',
  
  setToken(token, customerId, openid) {
    uni.setStorageSync(this.TOKEN_KEY, token);
    uni.setStorageSync(this.CUSTOMER_ID_KEY, customerId);
    uni.setStorageSync(this.OPENID_KEY, openid);
  },
  
  getToken() {
    return uni.getStorageSync(this.TOKEN_KEY);
  },
  
  getCustomerId() {
    return uni.getStorageSync(this.CUSTOMER_ID_KEY);
  },
  
  getOpenid() {
    return uni.getStorageSync(this.OPENID_KEY);
  },
  
  clearToken() {
    uni.removeStorageSync(this.TOKEN_KEY);
    uni.removeStorageSync(this.CUSTOMER_ID_KEY);
    uni.removeStorageSync(this.OPENID_KEY);
  },
  
  isTokenAvailable() {
    return !!this.getToken();
  }
};

export default TokenManager;
```

## 2. WeChat Service (wechatService.js)

Handles WeChat Mini Program login and profile retrieval.

```javascript
// Handles WeChat login and session management
import TokenManager from './tokenManager.js';

const WechatService = {
  // Call WeChat native login
  async wxLogin() {
    return new Promise((resolve, reject) => {
      wx.login({
        success: (res) => {
          if (res.code) {
            resolve(res.code);
          } else {
            reject(new Error('Failed to get code from WeChat: ' + res.errMsg));
          }
        },
        fail: (err) => {
          reject(err);
        }
      });
    });
  },
  
  // H5 fallback - redirect to WeChat OAuth
  async h5Login() {
    // This would redirect to WeChat OAuth endpoint
    // For H5, you need to register and setup OAuth
    const redirectUri = encodeURIComponent(`${window.location.origin}/callback`);
    const state = Math.random().toString(36).substring(7);
    
    window.location.href = 
      `https://open.weixin.qq.com/connect/oauth2/authorize?` +
      `appid=${process.env.VUE_APP_WECHAT_WEB_APPID}&` +
      `redirect_uri=${redirectUri}&` +
      `response_type=code&` +
      `scope=snsapi_userinfo&` +
      `state=${state}#wechat_redirect`;
  },
  
  // Check if running in WeChat Mini Program environment
  isInMiniProgram() {
    try {
      return typeof wx !== 'undefined' && wx.getSystemInfo;
    } catch (e) {
      return false;
    }
  }
};

export default WechatService;
```

## 3. API Client (apiClient.js)

Handles HTTP requests with automatic token injection.

```javascript
// API client with authentication headers
import TokenManager from './tokenManager.js';

const API_BASE_URL = process.env.VUE_APP_API_BASE_URL || 'https://api.example.com';

const apiClient = {
  async request(method, url, data = null, headers = {}) {
    const token = TokenManager.getToken();
    const finalHeaders = {
      'Content-Type': 'application/json',
      ...headers
    };
    
    if (token) {
      finalHeaders['Authorization'] = `Bearer ${token}`;
    }
    
    try {
      const response = await uni.request({
        url: `${API_BASE_URL}${url}`,
        method: method,
        data: data,
        header: finalHeaders
      });
      
      if (response[1].statusCode === 401) {
        // Token expired or invalid
        TokenManager.clearToken();
        uni.navigateTo({ url: '/pages/login' });
        throw new Error('Unauthorized');
      }
      
      return response[1];
    } catch (error) {
      throw error;
    }
  },
  
  get(url, headers) {
    return this.request('GET', url, null, headers);
  },
  
  post(url, data, headers) {
    return this.request('POST', url, data, headers);
  },
  
  put(url, data, headers) {
    return this.request('PUT', url, data, headers);
  },
  
  delete(url, headers) {
    return this.request('DELETE', url, null, headers);
  }
};

export default apiClient;
```

## 4. Login Page (login.vue)

Main login page using WeChat Mini Program login.

```vue
<template>
  <view class="login-container">
    <view class="login-card">
      <view class="title">登录</view>
      
      <button 
        @click="handleWechatLogin" 
        :loading="isLoading"
        :disabled="isLoading"
        class="btn-primary"
      >
        {{ isLoading ? '登录中...' : '微信登录' }}
      </button>
      
      <view v-if="error" class="error-message">{{ error }}</view>
    </view>
  </view>
</template>

<script>
import WechatService from '@/uni_modules/wechat-login/utils/wechatService.js';
import TokenManager from '@/uni_modules/wechat-login/utils/tokenManager.js';
import apiClient from '@/uni_modules/wechat-login/utils/apiClient.js';

export default {
  data() {
    return {
      isLoading: false,
      error: ''
    };
  },
  
  onLoad() {
    // Check if already logged in
    if (TokenManager.isTokenAvailable()) {
      uni.navigateTo({ url: '/pages/index' });
    }
  },
  
  methods: {
    async handleWechatLogin() {
      try {
        this.isLoading = true;
        this.error = '';
        
        // Get code from WeChat
        let code;
        if (WechatService.isInMiniProgram()) {
          code = await WechatService.wxLogin();
        } else {
          // H5 fallback
          await WechatService.h5Login();
          return;
        }
        
        // Call backend login
        const response = await apiClient.post('/api/auth/wechat/login', {
          code: code,
          source: 'weixin'
        });
        
        if (response.statusCode === 200 && response.data) {
          const loginData = response.data;
          
          // Save token
          TokenManager.setToken(
            loginData.token,
            loginData.customerId,
            loginData.openid
          );
          
          // Store auth state
          this.$store.commit('auth/setAuthenticated', true);
          this.$store.commit('auth/setCustomer', {
            id: loginData.customerId,
            name: loginData.name,
            phone: loginData.phone,
            source: loginData.source
          });
          
          // If new customer, go to profile completion
          if (loginData.isNewCustomer) {
            uni.navigateTo({ url: '/pages/profile' });
          } else {
            uni.navigateTo({ url: '/pages/index' });
          }
        } else {
          this.error = response.data?.error || 'Login failed';
        }
      } catch (err) {
        this.error = err.message || 'WeChat login failed';
        console.error('Login error:', err);
      } finally {
        this.isLoading = false;
      }
    }
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  background: white;
  border-radius: 10px;
  padding: 40px;
  width: 90%;
  max-width: 300px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}

.title {
  font-size: 28px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 40px;
  color: #333;
}

.btn-primary {
  width: 100%;
  height: 50px;
  background: #09bb07;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  margin-bottom: 20px;
}

.error-message {
  color: #f56c6c;
  text-align: center;
  font-size: 14px;
  margin-top: 10px;
}
</style>
```

## 5. Profile Completion Page (profile.vue)

Page for completing profile information after first login.

```vue
<template>
  <view class="profile-container">
    <view class="profile-card">
      <view class="title">完成个人资料</view>
      
      <input-field 
        v-model="form.name"
        label="姓名"
        placeholder="请输入您的姓名"
      />
      
      <input-field 
        v-model="form.phone"
        label="手机号码"
        placeholder="请输入您的手机号码"
        type="tel"
      />
      
      <button 
        @click="handleSubmit"
        :loading="isLoading"
        :disabled="isLoading || !form.name"
        class="btn-primary"
      >
        {{ isLoading ? '保存中...' : '保存并继续' }}
      </button>
      
      <view v-if="error" class="error-message">{{ error }}</view>
    </view>
  </view>
</template>

<script>
import apiClient from '@/uni_modules/wechat-login/utils/apiClient.js';
import TokenManager from '@/uni_modules/wechat-login/utils/tokenManager.js';

export default {
  data() {
    return {
      form: {
        name: '',
        phone: '',
        source: 'weixin'
      },
      isLoading: false,
      error: ''
    };
  },
  
  onLoad() {
    // Ensure user is authenticated
    if (!TokenManager.isTokenAvailable()) {
      uni.navigateTo({ url: '/pages/login' });
    }
  },
  
  methods: {
    async handleSubmit() {
      try {
        if (!this.form.name) {
          this.error = 'Please enter your name';
          return;
        }
        
        this.isLoading = true;
        this.error = '';
        
        const response = await apiClient.post('/api/auth/profile/complete', this.form);
        
        if (response.statusCode === 200) {
          // Update store
          this.$store.commit('auth/setCustomer', {
            id: response.data.id,
            name: response.data.name,
            phone: response.data.phone,
            source: response.data.source
          });
          
          // Navigate to home
          uni.navigateTo({ url: '/pages/index' });
        } else {
          this.error = response.data?.error || 'Failed to save profile';
        }
      } catch (err) {
        this.error = err.message || 'Error saving profile';
        console.error('Profile save error:', err);
      } finally {
        this.isLoading = false;
      }
    }
  }
};
</script>

<style scoped>
.profile-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.profile-card {
  background: white;
  border-radius: 10px;
  padding: 40px;
  width: 100%;
  max-width: 300px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}

.title {
  font-size: 24px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.btn-primary {
  width: 100%;
  height: 50px;
  background: #09bb07;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  margin-top: 20px;
}

.error-message {
  color: #f56c6c;
  text-align: center;
  font-size: 14px;
  margin-top: 10px;
}
</style>
```

## 6. Vuex Store (auth.js)

Store for managing authentication state.

```javascript
// Vuex store for authentication state
import TokenManager from '@/uni_modules/wechat-login/utils/tokenManager.js';

const state = {
  isAuthenticated: TokenManager.isTokenAvailable(),
  customer: {
    id: TokenManager.getCustomerId(),
    name: '',
    phone: '',
    source: 'weixin'
  }
};

const mutations = {
  setAuthenticated(state, value) {
    state.isAuthenticated = value;
  },
  
  setCustomer(state, customer) {
    state.customer = {
      ...state.customer,
      ...customer
    };
  },
  
  logout(state) {
    state.isAuthenticated = false;
    state.customer = {
      id: null,
      name: '',
      phone: '',
      source: 'weixin'
    };
    TokenManager.clearToken();
  }
};

const actions = {
  async refreshAuthOnLaunch({ commit }) {
    // Called when app launches to restore auth state
    if (TokenManager.isTokenAvailable()) {
      commit('setAuthenticated', true);
    }
  },
  
  logout({ commit }) {
    commit('logout');
    uni.navigateTo({ url: '/pages/login' });
  }
};

export default {
  namespaced: true,
  state,
  mutations,
  actions
};
```

## 7. App.vue Launch Handler

Initialize authentication on app launch.

```vue
<script>
export default {
  onLaunch() {
    // Auto-refresh auth state on app launch
    this.$store.dispatch('auth/refreshAuthOnLaunch');
  }
};
</script>
```

## 8. Environment Configuration

Create `.env.development` and `.env.production` files:

**.env.development**
```
VUE_APP_API_BASE_URL=http://localhost:8080
VUE_APP_WECHAT_WEB_APPID=wx00000000
```

**.env.production**
```
VUE_APP_API_BASE_URL=https://api.example.com
VUE_APP_WECHAT_WEB_APPID=your_web_appid
```

## Testing Flow

1. **Start the backend** - Ensure Spring Boot app is running on port 8080
2. **Open login page** - Navigate to `/pages/login`
3. **Click WeChat Login** - This triggers `wx.login()` in Mini Program
4. **Backend creates customer** - A new customer record is created in the database
5. **Redirect to profile** - If new customer, show profile completion page
6. **Complete profile** - Enter name, phone, source
7. **Stored in database** - Customer profile is updated
8. **Navigate to app** - User is logged in and can access protected pages
9. **Token in headers** - All API calls include `Authorization: Bearer <token>`

## Features

✅ WeChat Mini Program native login  
✅ H5 fallback with OAuth  
✅ JWT token management  
✅ Automatic token injection in requests  
✅ Profile completion workflow  
✅ Persistent authentication state  
✅ Auto-refresh on app launch  
✅ Logout functionality  
✅ Error handling and user feedback  

## Next Steps

1. Create input components for the forms
2. Implement proper validation
3. Add loading states and error handling
4. Setup API interceptors for request/response handling
5. Implement token refresh logic for expired tokens
6. Add analytics and tracking
7. Setup proper error boundaries

---

# Wallet Recharge UI (UniApp) - Phase 3

This section adds a simple balance/recharge UI and triggers WeChat Pay / Alipay flows.

## API Endpoints Used

- `GET /api/wallet/balance`
- `GET /api/wallet/promotions`
- `POST /api/wallet/recharge/initiate`

## Recharge Page (recharge.vue)

```vue
<template>
  <view class="container">
    <view class="balance-card">
      <text class="label">当前余额</text>
      <text class="balance">¥ {{ balance }}</text>
    </view>

    <view class="section">
      <text class="section-title">选择充值金额</text>
      <view class="preset-grid">
        <view
          v-for="amt in presets"
          :key="amt"
          class="preset"
          :class="{ active: selectedAmount === amt }"
          @click="selectedAmount = amt"
        >
          ¥ {{ amt }}
        </view>
      </view>

      <view v-if="promotions.length" class="promo">
        <text class="promo-title">优惠</text>
        <view v-for="p in promotions" :key="p.id" class="promo-item">
          <text>{{ p.name }}：充 {{ p.rechargeAmount }} 送 {{ p.bonusAmount }}</text>
        </view>
      </view>
    </view>

    <view class="section">
      <text class="section-title">支付方式</text>
      <radio-group @change="onChannelChange">
        <label class="radio">
          <radio value="WECHAT_PAY" :checked="channel==='WECHAT_PAY'" /> 微信支付
        </label>
        <label class="radio">
          <radio value="ALIPAY" :checked="channel==='ALIPAY'" /> 支付宝
        </label>
      </radio-group>
    </view>

    <button class="btn" :loading="loading" :disabled="loading" @click="startRecharge">
      {{ loading ? '处理中...' : '立即充值' }}
    </button>
  </view>
</template>

<script>
import apiClient from '@/uni_modules/wechat-login/utils/apiClient.js';

export default {
  data() {
    return {
      balance: '0.00',
      presets: [10, 20, 50, 100, 200],
      selectedAmount: 20,
      channel: 'WECHAT_PAY',
      promotions: [],
      loading: false
    }
  },
  async onShow() {
    await this.refreshBalance();
    await this.loadPromotions();
  },
  methods: {
    onChannelChange(e) {
      this.channel = e.detail.value;
    },
    async refreshBalance() {
      const res = await apiClient.get('/api/wallet/balance');
      this.balance = res.data.balance;
    },
    async loadPromotions() {
      const res = await apiClient.get('/api/wallet/promotions');
      this.promotions = res.data || [];
    },
    async startRecharge() {
      try {
        this.loading = true;
        const init = await apiClient.post('/api/wallet/recharge/initiate', {
          channel: this.channel,
          amount: String(this.selectedAmount)
        });

        if (this.channel === 'WECHAT_PAY') {
          const p = init.data.wechatPayParams;
          await new Promise((resolve, reject) => {
            uni.requestPayment({
              provider: 'wxpay',
              timeStamp: p.timeStamp,
              nonceStr: p.nonceStr,
              package: p.package,
              signType: p.signType,
              paySign: p.paySign,
              success: resolve,
              fail: reject
            });
          });
        } else {
          await new Promise((resolve, reject) => {
            uni.requestPayment({
              provider: 'alipay',
              orderInfo: init.data.alipayOrderString,
              success: resolve,
              fail: reject
            });
          });
        }

        // Payment success means the provider finished; webhook will credit the balance.
        // Poll/refresh balance to reflect the credited amount.
        await this.refreshBalance();
        uni.showToast({ title: '充值成功' });
      } catch (e) {
        uni.showToast({ title: '充值失败', icon: 'none' });
      } finally {
        this.loading = false;
      }
    }
  }
}
</script>

<style scoped>
.container { padding: 16px; }
.balance-card { padding: 16px; background: #fff; border-radius: 12px; margin-bottom: 16px; }
.label { color: #666; display: block; }
.balance { font-size: 28px; font-weight: bold; margin-top: 8px; display: block; }
.section { background: #fff; border-radius: 12px; padding: 16px; margin-bottom: 16px; }
.section-title { font-weight: 600; display: block; margin-bottom: 12px; }
.preset-grid { display: flex; flex-wrap: wrap; gap: 10px; }
.preset { padding: 10px 14px; border: 1px solid #eee; border-radius: 8px; }
.preset.active { border-color: #09bb07; color: #09bb07; }
.promo { margin-top: 12px; }
.promo-title { font-weight: 600; display: block; margin-bottom: 6px; }
.promo-item { color: #666; font-size: 12px; margin-top: 4px; }
.radio { display: block; margin-top: 8px; }
.btn { background: #09bb07; color: #fff; }
</style>
```

Notes:
- In production, the balance will update after the provider calls the backend webhook (`/api/payments/wechat/notify` or `/api/payments/alipay/notify`).
- You can add a polling loop (e.g. retry `GET /api/wallet/balance` for ~10s) if immediate refresh is required.
