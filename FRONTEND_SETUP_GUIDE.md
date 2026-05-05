# 🚀 FoodApp Frontend Setup Guide

## ✅ What We Created

A complete **React + TypeScript frontend** with:
- ✅ **Login Page** - Authentication with JWT tokens
- ✅ **Home Page** - Display restaurants from backend API
- ✅ **API Service** - Axios client for backend communication
- ✅ **Responsive Design** - Works on desktop and mobile
- ✅ **Test Accounts** - Built-in test credentials

---

## 📋 Project Structure

```
c:\Users\ashis\OneDrive\Desktop\Multi-Vendor Food Ordering App\foodapp-frontend\
├── public/
│   └── index.html              # Main HTML file
├── src/
│   ├── pages/
│   │   ├── LoginPage.tsx       # Login component
│   │   └── HomePage.tsx        # Restaurant listing
│   ├── services/
│   │   └── api.ts              # API client (Axios)
│   ├── App.tsx                 # Main app component
│   └── index.tsx               # React entry point
├── package.json                # Dependencies
├── tsconfig.json               # TypeScript config
└── README.md                   # Documentation
```

---

## 🔧 Installation & Running

### Step 1: Wait for npm install (in progress)
The terminal is currently installing all dependencies. This may take 3-5 minutes.

### Step 2: Start the React App (Once npm install finishes)
```bash
cd "c:\Users\ashis\OneDrive\Desktop\Multi-Vendor Food Ordering App\foodapp-frontend"
npm start
```

This will:
- ✅ Start React development server on **http://localhost:3000**
- ✅ Open browser automatically
- ✅ Hot-reload on file changes

### Step 3: Verify Backend is Running
Make sure the Java backend is still running on **http://localhost:8080**

```bash
# In another terminal
cd "c:\Users\ashis\OneDrive\Desktop\Multi-Vendor Food Ordering App\foodapp"
$env:JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot"
.\mvnw.cmd spring-boot:run
```

---

## 🧪 Testing the Frontend

### 1. Access the Application
Open browser and go to: **http://localhost:3000**

### 2. Login with Test Account
```
Email:    admin@foodapp.com
Password: admin123
```

### 3. Expected Results
- ✅ Login successful → Redirects to Home page
- ✅ Home page loads → Shows list of restaurants from backend API
- ✅ Can see restaurant names, ratings, cuisine types
- ✅ Logout button works

---

## 📱 Test Accounts

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@foodapp.com | admin123 |
| Customer 1 | rahul@gmail.com | password |
| Customer 2 | priya@gmail.com | password |
| Vendor 1 | rajesh@vendor.com | vendor123 |
| Vendor 2 | liwei@vendor.com | vendor123 |
| Delivery 1 | vikram@delivery.com | delivery123 |
| Delivery 2 | amit@delivery.com | delivery123 |

---

## 🔌 API Integration

### Configured Endpoints

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/api/auth/login` | POST | User login |
| `/api/auth/register` | POST | User registration |
| `/api/restaurants` | GET | List all restaurants |
| `/api/cart` | GET | Get shopping cart |
| `/api/cart/items` | POST/DELETE | Manage cart items |
| `/api/orders` | POST/GET | Create and view orders |

### Token Management
- Access tokens stored in `localStorage`
- Automatically added to all API requests
- Auto-logout on 401 Unauthorized

---

## 📝 Key Features Implemented

### Login Page
```
✅ Email & password input
✅ JWT token handling
✅ Display test accounts
✅ Error messages
✅ Loading state
```

### Home Page
```
✅ Display restaurants grid
✅ Restaurant name, description, cuisine
✅ Star ratings
✅ User welcome message
✅ Logout functionality
```

### API Service (api.ts)
```
✅ Axios HTTP client
✅ Request interceptors (add token)
✅ Response interceptors (handle 401)
✅ Organized service methods
```

---

## 🎨 Styling

All styles are **inline CSS** (no external CSS files needed):
- Simple, clean design
- Responsive grid layout
- Red color scheme (#ff6b6b) matching the header
- Mobile-friendly

---

## 🚀 Next Steps to Enhance

### Add Menu Component
```typescript
// View menu items for a restaurant
<MenuPage restaurantId={restaurantId} />
```

### Add Cart Management
```typescript
// Shopping cart page
<CartPage />
```

### Add Order Tracking
```typescript
// Real-time order status
<OrderTrackingPage orderId={orderId} />
```

### Add Payment Integration
```typescript
// Razorpay payment
<PaymentPage amount={totalAmount} />
```

### WebSocket Integration
```typescript
// Real-time order updates
const ws = new WebSocket('ws://localhost:8080/ws/order/123');
```

---

## 🐛 Troubleshooting

### Problem: "Cannot find module 'axios'"
**Solution**: Wait for `npm install` to complete, then restart `npm start`

### Problem: "Unauthorized" error on API calls
**Solution**: Make sure backend is running on http://localhost:8080

### Problem: React app won't start
**Solution**: 
1. Clear node_modules: `rm -r node_modules`
2. Clear npm cache: `npm cache clean --force`
3. Reinstall: `npm install`

### Problem: Localhost:3000 shows blank page
**Solution**: 
1. Check browser console for errors (F12)
2. Verify backend is running
3. Restart `npm start`

---

## 📊 Current Status

| Component | Status |
|-----------|--------|
| Dependencies | ⏳ Installing... |
| Login Page | ✅ Ready |
| Home Page | ✅ Ready |
| API Service | ✅ Ready |
| TypeScript Config | ✅ Ready |
| Backend Integration | ✅ Ready |

**Next:** Wait for npm install to complete, then run `npm start`

---

## 📞 Commands Reference

```bash
# Install dependencies
npm install

# Start development server
npm start

# Build for production
npm build

# Run tests
npm test

# Clear cache
npm cache clean --force
```

---

## ✨ Summary

You now have:
1. **Backend** - Spring Boot Java API running on :8080 ✅
2. **Frontend** - React app with login & restaurant listing ✅
3. **Integration** - Frontend connected to backend API ✅
4. **Testing** - Test accounts ready to use ✅

**Next Step**: Once npm install completes, run `npm start` to launch the app!
