**url test cho postman**
cách đăng ký
post http://localhost:8080/auth/register
{
    "username": "user1",
    "password": "123456",
    "role": "user"
}

cách đăng nhập
post: http://localhost:8080/auth/login
{
    "username": "user1",
    "password": "123456",
}

return 
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3OTA2NTQyMTMsInN1YiI6InVzZXIxIiwiaWF0IjoxNzkwNjUwNjEzfQ.vf3N3xf94gBvcqv7NI4-ZStzDejEMEC2BH9Xt8soScI"
}

cách vào trang chủ(profile)
get: http://localhost:8080/users/me
chọn authorization, Auth type là bearer token(nhập token vào)

**url giao diện**
http://localhost:8080/login.html


sử dung java21 và mysql, chú ý cấu hình trong .property
