document.addEventListener("DOMContentLoaded", () => {
    const registerForm = document.getElementById("registerForm");
    const toastEl = document.getElementById("registerToast");
    const toastMessage = document.getElementById("toastMessage");
    const toast = new bootstrap.Toast(toastEl);

    registerForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const formData = new FormData(registerForm);
        const username = formData.get("username").trim();
        const email = formData.get("email").trim();
        const password = formData.get("password").trim();

        if (!isEmailValid(email)) {
            showToast("Invalid email format", "danger");
            return;
        }

        if (!isPasswordStrong(password)) {
            showToast("Password too weak! Must be at least 8 chars, 1 uppercase, 1 number.", "danger");
            return;
        }

        try {
            const response = await fetch("/auth/register", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username, email, password })
            });

            if (response.ok) {
                showToast("Registration successful!", "success");
                registerForm.reset();
            } else {
                const data = await response.json();
                showToast(data.message || "Registration failed", "danger");
            }
        } catch (err) {
            showToast("Network error", "danger");
        }
    });

    function showToast(message, type = "danger") {
        toastEl.className = `toast align-items-center text-bg-${type} border-0`;
        toastMessage.textContent = message;
        toast.show();
    }

    function isPasswordStrong(password) {
        return /^(?=.*[A-Z])(?=.*\d).{8,}$/.test(password);
    }

    function isEmailValid(email) {
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    }
});
