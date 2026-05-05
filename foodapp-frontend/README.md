# 🍔 FoodApp Frontend

Simple React frontend for the FoodApp multi-vendor food ordering platform.

## 🚀 Quick Start

### 1. Install Dependencies
```bash
cd foodapp-frontend
npm install
```

### 2. Start Development Server
```bash
npm start
```

The app will open at `http://localhost:3000`

### 3. Make sure Backend is Running
The backend must be running on `http://localhost:8080`

```bash
# In another terminal, from foodapp directory
./mvnw spring-boot:run
```

## 🔑 Test Accounts

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@foodapp.com | admin123 |
| Customer | rahul@gmail.com | password |
| Vendor | rajesh@vendor.com | vendor123 |
| Delivery | vikram@delivery.com | delivery123 |

## 📁 Project Structure

```
src/
├── pages/
│   ├── LoginPage.tsx      → Login form with test accounts
│   └── HomePage.tsx       → Display restaurants & menu
├── components/            → Reusable components
├── services/
│   └── api.ts            → API client & services
├── App.tsx               → Main app component
└── index.tsx             → React entry point
```

## 🛠️ Features

- ✅ Login/Register with JWT tokens
- ✅ View all restaurants
- ✅ Display restaurant details
- ✅ Automatic token refresh
- ✅ Protected routes
- ✅ Responsive design

## 🔗 API Integration

All API calls go to `http://localhost:8080/api`

Supported endpoints:
- `POST /auth/login` - User login
- `GET /restaurants` - List all restaurants
- `GET /cart` - Get shopping cart
- `POST /orders` - Create order

## 📦 Dependencies

- React 18
- Axios - HTTP client
- TypeScript - Type safety

## 🚀 Next Steps

1. **Add Menu Component** - Display restaurant menu items
2. **Add Cart Page** - Manage shopping cart
3. **Add Order Page** - View order history
4. **Add Payment** - Razorpay integration
5. **WebSocket** - Real-time order tracking
6. **Styling** - Tailwind CSS or Material-UI

## 📝 Notes

- CORS is enabled on backend for `localhost:3000`
- Tokens are stored in localStorage
- Backend must be running for API calls to work
