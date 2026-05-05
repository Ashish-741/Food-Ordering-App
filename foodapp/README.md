# 🍔 FoodApp — Multi-Vendor Food Ordering Platform

A production-ready food ordering backend (like Swiggy/Zomato) built with **Spring Boot 3.5**, featuring multi-vendor support, JWT authentication, real-time WebSocket order tracking, and a delivery assignment engine.

## 🚀 Quick Start

### Option 1: Local (H2 in-memory DB)
```bash
./mvnw spring-boot:run
```
Server starts at `http://localhost:8080`

### Option 2: Docker (PostgreSQL + Redis)
```bash
docker-compose up --build
```

### Option 3: With existing PostgreSQL
```bash
./mvnw spring-boot:run -Dspring.profiles.active=prod
```

## 🧪 Test Accounts (Auto-seeded)

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@foodapp.com | admin123 |
| Customer | rahul@gmail.com | password |
| Vendor | rajesh@vendor.com | vendor123 |
| Delivery | vikram@delivery.com | delivery123 |

## 🏗️ Tech Stack

| Component | Technology |
|-----------|-----------|
| Framework | Spring Boot 3.5 |
| Auth | JWT (JJWT 0.12.6) + Spring Security |
| Database | PostgreSQL / H2 (dev) |
| ORM | Spring Data JPA + Hibernate |
| Real-time | WebSocket (STOMP + SockJS) |
| Cache | Redis (optional) |
| Build | Maven |
| Java | 17 |

## 📁 Project Structure

```
com.foodapp/
├── auth/          → JWT login, signup, refresh tokens
├── user/          → User entity, profile management
├── restaurant/    → Restaurant CRUD, vendor analytics
├── menu/          → Menu item management
├── cart/          → In-memory cart (ConcurrentHashMap)
├── order/         → Order placement, state machine
├── payment/       → Razorpay integration (simulated)
├── delivery/      → Delivery agent management, GPS tracking
├── review/        → Rating system with weighted averages
├── admin/         → Platform analytics, user management
├── address/       → Multi-address management
├── websocket/     → Real-time order tracking (STOMP)
├── security/      → JWT filter, token provider
├── config/        → Security, WebSocket, data seeder
└── common/        → ApiResponse, exceptions, Haversine utility
```

## 🔑 Key Features

- **JWT Authentication** with access + refresh tokens
- **Role-Based Access**: CUSTOMER, VENDOR, DELIVERY, ADMIN
- **Multi-Vendor Isolation**: Each vendor manages only their restaurant
- **Order State Machine**: PLACED → CONFIRMED → PREPARING → READY → PICKED_UP → DELIVERED
- **Delivery Assignment**: Auto-assigns nearest agent using Haversine formula
- **Real-Time Updates**: WebSocket notifications for order status changes
- **Payment Flow**: Razorpay-ready (simulated for development)
- **Weighted Ratings**: Recent reviews weighted more heavily
- **ETA Calculation**: Distance-based delivery time estimation

## 📡 API Endpoints Overview

| Module | Endpoints | Auth Required |
|--------|-----------|---------------|
| Auth | POST /api/auth/register, /login, /refresh-token | No |
| Restaurants | GET/POST/PUT /api/restaurants | GET: No, Modify: VENDOR |
| Menu | GET/POST/PUT/DELETE /api/restaurants/{id}/menu | GET: No, Modify: VENDOR |
| Cart | GET/POST/PUT/DELETE /api/cart | CUSTOMER |
| Orders | POST/GET/PUT /api/orders | Yes |
| Payments | POST /api/payments/create-order, /verify | Yes |
| Delivery | PATCH/PUT /api/delivery | DELIVERY |
| Reviews | POST/GET /api/reviews | POST: CUSTOMER, GET: No |
| Admin | GET/PUT /api/admin | ADMIN |
| Profile | GET/PUT /api/users/me | Yes |

## 🔌 WebSocket Channels

| Channel | Purpose |
|---------|---------|
| `/topic/order/{orderId}` | Customer tracks their order |
| `/topic/vendor/{vendorId}/orders` | Vendor receives new orders |
| `/topic/delivery/{agentId}` | Agent receives delivery assignments |

## 🐳 Docker

```bash
# Start all services
docker-compose up --build

# Stop
docker-compose down

# With data persistence
docker-compose down  # data persists in volumes
docker-compose down -v  # removes volumes too
```

## 📝 License

MIT
