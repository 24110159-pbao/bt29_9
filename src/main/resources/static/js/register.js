const registerForm =
    document.getElementById("registerForm");

const registerButton =
    document.getElementById("registerButton");

const message =
    document.getElementById("message");


registerForm.addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();

        const username =
            document
                .getElementById("username")
                .value
                .trim();

        const password =
            document
                .getElementById("password")
                .value;

        const role =
            document
                .getElementById("role")
                .value;


        message.textContent = "";
        message.className = "";

        registerButton.disabled = true;
        registerButton.textContent =
            "Đang đăng ký...";


        try {

            const response = await fetch(
                "/auth/register",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({
                        username: username,
                        password: password,
                        role: role
                    })
                }
            );


            const data =
                await response.json();


            if (!response.ok) {

                throw new Error(
                    data.message ||
                    "Đăng ký thất bại"
                );
            }


            message.textContent =
                "Đăng ký thành công!";

            message.className =
                "success";


            // Chuyển sang trang login
            setTimeout(function () {

                window.location.href =
                    "/login.html";

            }, 1000);


        } catch (error) {

            console.error(error);

            message.textContent =
                error.message ||
                "Có lỗi xảy ra";

            message.className =
                "error";


        } finally {

            registerButton.disabled =
                false;

            registerButton.textContent =
                "Đăng ký";
        }
    }
);
