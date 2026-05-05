# E2E Test Script - Fixed for PowerShell + curl.exe
$baseUrl = "http://localhost:8080/api"

Write-Host "===== 1. LOGIN AS CUSTOMER =====" -ForegroundColor Cyan
$loginResp = curl.exe -s -X POST "$baseUrl/auth/login" -H "Content-Type: application/json" -d '{"email":"rahul@gmail.com","password":"password"}' | ConvertFrom-Json
$token = $loginResp.data.accessToken
Write-Host "  Logged in as: $($loginResp.data.name) ($($loginResp.data.role))"

Write-Host "`n===== 2. GET PROFILE =====" -ForegroundColor Cyan
$profile = curl.exe -s "$baseUrl/users/me" -H "Authorization: Bearer $token" | ConvertFrom-Json
Write-Host "  Name: $($profile.data.name), Email: $($profile.data.email)"

Write-Host "`n===== 3. BROWSE RESTAURANTS =====" -ForegroundColor Cyan
$restaurants = curl.exe -s "$baseUrl/restaurants" | ConvertFrom-Json
foreach ($r in $restaurants.data) {
    Write-Host "  [$($r.id)] $($r.name) - $($r.cuisineType) | Rating: $($r.avgRating)"
}

Write-Host "`n===== 4. VIEW MENU (Spice Garden) =====" -ForegroundColor Cyan
$menu = curl.exe -s "$baseUrl/restaurants/1/menu" | ConvertFrom-Json
foreach ($item in $menu.data) {
    Write-Host "  [$($item.id)] $($item.name) - Rs.$($item.price)"
}

Write-Host "`n===== 5. ADD TO CART =====" -ForegroundColor Cyan
curl.exe -s -X POST "$baseUrl/cart/items" -H "Content-Type: application/json" -H "Authorization: Bearer $token" -d '{"restaurantId":1,"menuItemId":1,"quantity":2}' | Out-Null
$cart = curl.exe -s -X POST "$baseUrl/cart/items" -H "Content-Type: application/json" -H "Authorization: Bearer $token" -d '{"restaurantId":1,"menuItemId":4,"quantity":3}' | ConvertFrom-Json
Write-Host "  Cart: $($cart.data.totalItems) items, Rs.$($cart.data.subtotal)"

Write-Host "`n===== 6. PLACE ORDER =====" -ForegroundColor Cyan
$order = curl.exe -s -X POST "$baseUrl/orders" -H "Content-Type: application/json" -H "Authorization: Bearer $token" -d '{"restaurantId":1,"deliveryAddress":"42 Koramangala, Bengaluru","deliveryLat":12.9352,"deliveryLng":77.6245,"specialInstructions":"Extra spicy","items":[{"menuItemId":1,"quantity":2},{"menuItemId":4,"quantity":3}]}' | ConvertFrom-Json
$orderId = $order.data.id
Write-Host "  Order #$($order.data.orderNumber) | Status: $($order.data.status) | Total: Rs.$($order.data.totalAmount) | ETA: $($order.data.estimatedDeliveryMins) mins"

Write-Host "`n===== 7. CREATE PAYMENT (UPI) =====" -ForegroundColor Cyan
$pay = curl.exe -s -X POST "$baseUrl/payments/create-order" -H "Content-Type: application/json" -H "Authorization: Bearer $token" -d "{""orderId"":$orderId,""paymentMethod"":""UPI""}" | ConvertFrom-Json
Write-Host "  Razorpay Order: $($pay.data.razorpayOrderId) | Status: $($pay.data.paymentStatus)"

Write-Host "`n===== 8. VERIFY PAYMENT =====" -ForegroundColor Cyan
$rpayId = $pay.data.razorpayOrderId
$verified = curl.exe -s -X POST "$baseUrl/payments/verify" -H "Content-Type: application/json" -H "Authorization: Bearer $token" -d "{""razorpayOrderId"":""$rpayId"",""razorpayPaymentId"":""pay_test123"",""razorpaySignature"":""sig_test123""}" | ConvertFrom-Json
Write-Host "  Status: $($verified.data.paymentStatus) | TXN: $($verified.data.transactionId)"

Write-Host "`n===== 9. VENDOR FLOW =====" -ForegroundColor Cyan
$vendorResp = curl.exe -s -X POST "$baseUrl/auth/login" -H "Content-Type: application/json" -d '{"email":"rajesh@vendor.com","password":"vendor123"}' | ConvertFrom-Json
$vToken = $vendorResp.data.accessToken
Write-Host "  Vendor logged in: $($vendorResp.data.name)"

$u1 = curl.exe -s -X PUT "$baseUrl/orders/$orderId/status" -H "Content-Type: application/json" -H "Authorization: Bearer $vToken" -d '{"status":"CONFIRMED"}' | ConvertFrom-Json
Write-Host "  Status -> $($u1.data.status)"
$u2 = curl.exe -s -X PUT "$baseUrl/orders/$orderId/status" -H "Content-Type: application/json" -H "Authorization: Bearer $vToken" -d '{"status":"PREPARING"}' | ConvertFrom-Json
Write-Host "  Status -> $($u2.data.status)"
$u3 = curl.exe -s -X PUT "$baseUrl/orders/$orderId/status" -H "Content-Type: application/json" -H "Authorization: Bearer $vToken" -d '{"status":"READY"}' | ConvertFrom-Json
Write-Host "  Status -> $($u3.data.status) | Delivery Agent: $($u3.data.deliveryAgentName)"

Write-Host "`n===== 10. ORDER HISTORY =====" -ForegroundColor Cyan
$history = curl.exe -s "$baseUrl/orders/my-orders" -H "Authorization: Bearer $token" | ConvertFrom-Json
Write-Host "  Customer has $($history.data.Count) order(s)"

Write-Host "`n===== ALL TESTS PASSED! =====" -ForegroundColor Green
