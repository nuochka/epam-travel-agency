document.addEventListener("DOMContentLoaded", () => {
    const registerForm = document.getElementById("registerForm");
    const registerToastEl = document.getElementById("registerToast");
    const registerToastMessage = document.getElementById("registerToastMessage"); 

    const loginForm = document.getElementById("loginForm");
    const loginToastEl = document.getElementById("loginToast");
    const loginToastMessage = document.getElementById("loginToastMessage");

    function showToast(toastEl, messageEl, message, type = "danger") {
        toastEl.className = `toast align-items-center text-bg-${type} border-0 position-fixed bottom-0 end-0 m-3`;
        messageEl.textContent = message;
        new bootstrap.Toast(toastEl).show();
    }

    function isPasswordStrong(password) {
        return /^(?=.*[A-Z])(?=.*\d).{8,}$/.test(password);
    }

    function isEmailValid(email) {
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    }

    if (registerForm) {
        registerForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            const formData = new FormData(registerForm);
            const username = formData.get("username").trim();
            const email = formData.get("email").trim();
            const password = formData.get("password").trim();

            if (!isEmailValid(email)) {
                showToast(registerToastEl, registerToastMessage, "Invalid email format", "danger");
                return;
            }

            if (!isPasswordStrong(password)) {
                showToast(registerToastEl, registerToastMessage, "Password too weak! Must be at least 8 characters, 1 uppercase letter, 1 number.", "danger");
                return;
            }

            try {
                const response = await fetch("/auth/register", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ username, email, password })
                });

                if (response.ok) {
                    showToast(registerToastEl, registerToastMessage, "Registration successful! Redirecting to sign in page", "success");
                    registerForm.reset();

                    setTimeout(() => {
                    window.location.href = "/auth/login"; 
                }, 2000);
                } else {
                    const data = await response.json();
                    showToast(registerToastEl, registerToastMessage, data.message || "Registration failed", "danger");
                }
            } catch (err) {
                showToast(registerToastEl, registerToastMessage, "Network error. Please try again.", "danger");
            }
        });
    }

    if (loginForm) {
        loginForm.addEventListener("submit", async (e) => {
            e.preventDefault();
            const formData = new FormData(loginForm);
            const username = formData.get("username").trim();
            const password = formData.get("password").trim();
            const loginButton = loginForm.querySelector('button[type="submit"]');

            loginButton.disabled = true;

            try {
                const response = await fetch("/auth/login", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ username, password })
                });

                if (response.ok) {
                    const data = await response.json();
                    showToast(loginToastEl, loginToastMessage, "Login successful! Redirecting...", "success");

                    localStorage.setItem("jwtToken", data.token);
                    localStorage.setItem("username", data.username);
                    localStorage.setItem("userId", data.userId);

                    setTimeout(() => {
                        window.location.href = "/dashboard";
                    }, 2000);
                } else {
                    const data = await response.json();
                    showToast(loginToastEl, loginToastMessage, data.message || "Login failed. Check your credentials.", "danger");
                    loginButton.disabled = false;
                }
            } catch (err) {
                console.error("Network error during login:", err);
                showToast(loginToastEl, loginToastMessage, "Network error. Please try again later.", "danger");
                loginButton.disabled = false;
            }
        });
    }
});
