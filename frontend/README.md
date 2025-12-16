# Frontend - UniApp Application

## Description

A cross-platform frontend application built with UniApp and Vue 3, featuring:
- UniApp framework for multi-platform support (H5, WeChat Mini Program, Alipay Mini Program, App)
- Vue 3 Composition API
- Pinia for state management
- Axios for HTTP requests
- Responsive design with custom styling

## Prerequisites

- Node.js >= 16.x
- npm >= 8.x or yarn >= 1.x

## Installation

```bash
npm install
```

## Development

### H5 (Web Browser)

```bash
npm run dev:h5
```

The app will be available at `http://localhost:5173`

### WeChat Mini Program

```bash
npm run dev:mp-weixin
```

Then open the `dist/dev/mp-weixin` directory in WeChat Developer Tools.

### Alipay Mini Program

```bash
npm run dev:mp-alipay
```

### App (iOS/Android)

```bash
npm run dev:app
```

## Build for Production

### H5

```bash
npm run build:h5
```

Built files will be in `dist/build/h5/`

### WeChat Mini Program

```bash
npm run build:mp-weixin
```

Built files will be in `dist/build/mp-weixin/`

## Project Structure

```
frontend/
├── pages/              # Application pages
│   ├── index/         # Home page
│   └── splash/        # Boot/splash screen
├── src/
│   ├── api/           # API modules
│   ├── store/         # Pinia stores
│   └── utils/         # Utility functions (axios wrapper, etc.)
├── static/
│   └── styles/        # Global styles
├── App.vue            # Root component
├── main.js            # Application entry point
├── pages.json         # Page configuration
├── manifest.json      # App manifest
└── vite.config.js     # Vite configuration
```

## Environment Variables

Environment variables are defined in `.env.development` and `.env.production` files:

- `VITE_API_BASE_URL` - Backend API base URL
- `VITE_WECHAT_APP_ID` - WeChat Mini Program App ID
- `VITE_ALIPAY_APP_ID` - Alipay Mini Program App ID

## Features

### Splash Screen
- Boot screen that validates backend connectivity
- Health check API integration
- Automatic redirect to home page on success

### State Management
- Pinia store for global app state
- Server status tracking
- User information management

### HTTP Client
- Axios wrapper with interceptors
- Automatic token handling
- Error handling with user feedback
- Request/response logging

### Styling
- Custom CSS with utility classes
- Responsive design
- Gradient backgrounds
- Modern UI components

## API Integration

The app connects to the backend API at:
- Development: `http://localhost:8080`
- Production: Configure in `.env.production`

### Available Endpoints

- `GET /api/health` - Health check endpoint

## Troubleshooting

### Port already in use
Change the port in `vite.config.js` or `manifest.json` (h5 section)

### Dependencies not installing
```bash
rm -rf node_modules package-lock.json
npm install
```

### Build errors
```bash
rm -rf unpackage dist
npm run build:h5
```

## Platform-Specific Notes

### WeChat Mini Program
- Configure `mp-weixin.appid` in `manifest.json`
- Requires WeChat Developer Tools
- Domain whitelist required for API calls

### Alipay Mini Program
- Configure `mp-alipay.appid` in `manifest.json`
- Requires Alipay Developer Tools
- Domain whitelist required for API calls

## Documentation

- [UniApp Documentation](https://uniapp.dcloud.io/)
- [Vue 3 Documentation](https://vuejs.org/)
- [Pinia Documentation](https://pinia.vuejs.org/)
