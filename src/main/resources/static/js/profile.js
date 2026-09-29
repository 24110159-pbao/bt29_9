// ==========================================
// Lấy JWT
// ==========================================

const token =
    localStorage.getItem("token");


// ==========================================
// Nếu chưa đăng nhập hoặc token không hợp lệ
// ==========================================

if (!token || token === "undefined" || token === "null") {
    localStorage.removeItem("token");
    window.location.href =
        "/login.html";
}


// ==========================================
// Gọi API /users/me
// ==========================================

async function loadProfile() {

    try {

        const response =
            await fetch(
                "/users/me",
                {
                    method: "GET",
                    headers: {
                        "Authorization":
                            "Bearer " + token
                    }
                }
            );


        // ==========================================
        // JWT không hợp lệ / hết hạn / bị từ chối
        // ==========================================

        if (response.status === 401 || response.status === 403) {

            localStorage.removeItem("token");

            window.location.href =
                "/login.html";

            return;
        }


        if (!response.ok) {
            const errorData = await response.json().catch(() => null);
            throw new Error(
                (errorData && errorData.message) ? errorData.message : "Không thể lấy thông tin user"
            );
        }


        const user =
            await response.json();


        // ==========================================
        // Hiển thị thông tin
        // ==========================================

        document.getElementById("userId")
            .textContent = user.id;

        document.getElementById("username")
            .textContent = user.username;

        document.getElementById("role")
            .textContent = user.role;


    } catch (error) {

        console.error(error);

        document.getElementById("message")
            .textContent =
                "Không thể tải thông tin tài khoản.";
    }
}


// ==========================================
// Đăng xuất
// ==========================================

function logout() {

    localStorage.removeItem("token");

    window.location.href =
        "/login.html";
}


// ==========================================
// Trang chủ
// ==========================================

function goHome() {

    window.location.href =
        "/profile.html";
}


// ==========================================
// Chạy khi mở trang
// ==========================================

loadProfile();
