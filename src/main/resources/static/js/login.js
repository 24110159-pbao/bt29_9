const loginForm = document.getElementById("loginForm");
const loginButton = document.getElementById("loginButton");
const message = document.getElementById("message");

loginForm.addEventListener("submit", async function (event) {

    // Không cho form reload trang
    event.preventDefault();

    // Lấy dữ liệu từ form
    const username = document
        .getElementById("username")
        .value
        .trim();

    const password = document
        .getElementById("password")
        .value;

    // Xóa thông báo cũ
    message.textContent = "";
    message.className = "";

    // Khóa nút trong lúc đăng nhập
    loginButton.disabled = true;
    loginButton.textContent = "Đang đăng nhập...";

    try {

        // Gọi API đăng nhập
        const response = await fetch("/auth/login", {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({
                username: username,
                password: password
            })
        });

        // Đọc dữ liệu server trả về
        const data = await response.json();

        // Nếu HTTP status không thành công
        if (!response.ok) {

            throw new Error(
                data.message ||
                "Username hoặc password không đúng"
            );
        }

        if (!data || !data.token) {
            throw new Error("Không nhận được token từ server");
        }

        // ==========================================
        // LƯU JWT
        // ==========================================

        localStorage.setItem(
            "token",
            data.token
        );

        // Thông báo thành công
        message.textContent =
            "Đăng nhập thành công!";

        message.className = "success";

        // ==========================================
        // Chuyển sang profile
        // ==========================================

        setTimeout(function () {

            window.location.href =
                "/profile.html";

        }, 500);

    } catch (error) {

        console.error(error);

        message.textContent =
            error.message ||
            "Có lỗi xảy ra khi đăng nhập.";

        message.className = "error";

    } finally {

        // Mở lại nút đăng nhập
        loginButton.disabled = false;

        loginButton.textContent =
            "Đăng nhập";
    }
});
