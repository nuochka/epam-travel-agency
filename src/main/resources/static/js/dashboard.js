document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("jwtToken");
    if (!token) { window.location.href = "/auth/login"; return; }

    const usernameEl = document.getElementById("username");
    const tourList = document.getElementById("tourList");
    const orderList = document.getElementById("orderList");
    const orderSection = document.getElementById("orderSection");
    const noOrdersMsg = document.getElementById("noOrdersMsg");
    const toastEl = document.getElementById("dashboardToast");
    const toastMessage = document.getElementById("dashboardToastMessage");

    const bookModal = new bootstrap.Modal(document.getElementById("bookModal"));
    const cancelModal = new bootstrap.Modal(document.getElementById("cancelModal"));

    const bookModalTourName = document.getElementById("bookModalTourName");
    const bookModalConfirm = document.getElementById("bookModalConfirm");
    const cancelModalTourName = document.getElementById("cancelModalTourName");
    const cancelModalConfirm = document.getElementById("cancelModalConfirm");

    const searchInput = document.getElementById("searchInput");
    const sortSelect = document.getElementById("sortSelect");

    let selectedTourId = null;
    let selectedOrderId = null;
    let myOrders = [];
    let allTours = [];

    function showToast(message, type = "success") {
        toastEl.className = `toast align-items-center text-bg-${type} border-0 position-fixed bottom-0 end-0 m-3`;
        toastMessage.textContent = message;
        new bootstrap.Toast(toastEl).show();
    }

    usernameEl.textContent = JSON.parse(atob(token.split(".")[1])).sub;

    async function loadOrders() {
        try {
            const res = await fetch("/orders/me", { headers: { "Authorization": `Bearer ${token}` } });
            myOrders = await res.json();

            if (myOrders.length === 0) {
                orderSection.style.display = "none";
                noOrdersMsg.style.display = "block";
            } else {
                orderSection.style.display = "block";
                noOrdersMsg.style.display = "none";
            }

            orderList.innerHTML = myOrders.map(order => `
                <div class="col-md-4 mb-3">
                    <div class="card shadow-sm">
                        <div class="card-body">
                            <h5>${order.tourName}</h5>
                            <p>Status: <b>${order.status}</b></p>
                            <p>Date: ${new Date(order.bookingDate).toLocaleString()}</p>
                            ${order.status === "NEW" ? `<button class="btn btn-danger cancelBtn" data-id="${order.id}" data-tour="${order.tourName}">Cancel</button>` : ""}
                            ${order.status === "CANCELLED" ? `<button class="btn btn-outline-danger mt-2 deleteBtn" data-id="${order.id}">Delete</button>` : ""}
                        </div>
                    </div>
                </div>
            `).join('');

            document.querySelectorAll(".cancelBtn").forEach(btn => {
                btn.addEventListener("click", () => {
                    selectedOrderId = btn.dataset.id;
                    cancelModalTourName.textContent = btn.dataset.tour;
                    cancelModal.show();
                });
            });

            document.querySelectorAll(".deleteBtn").forEach(btn => {
                btn.addEventListener("click", async () => {
                    try {
                        const res = await fetch(`/orders/${btn.dataset.id}`, {
                            method: "DELETE",
                            headers: { "Authorization": `Bearer ${token}` }
                        });
                        if (!res.ok) throw new Error("Failed to delete order");
                        showToast("Order deleted");
                        await loadOrders();
                        await loadTours();
                    } catch (err) { showToast(err.message, "danger"); }
                });
            });

        } catch {
            showToast("Failed to load orders", "danger");
        }
    }

    async function loadTours() {
        try {
            const res = await fetch("/tours", { headers: { "Authorization": `Bearer ${token}` } });
            allTours = await res.json();

            let filtered = [...allTours];

            const searchTerm = searchInput.value.toLowerCase();
            if (searchTerm) {
                filtered = filtered.filter(t =>
                    t.name.toLowerCase().includes(searchTerm) ||
                    t.cityName.toLowerCase().includes(searchTerm)
                );
            }

            const sortBy = sortSelect.value;
            filtered.sort((a, b) => {
                if (sortBy === "price" || sortBy === "availableSeats") return a[sortBy] - b[sortBy];
                if (sortBy === "startDate") return new Date(a.startDate) - new Date(b.startDate);
            });

            tourList.innerHTML = filtered.map(t => `
                <div class="col-md-4 mb-3">
                    <div class="card shadow-sm">
                        <div class="card-body">
                            <h5 class="card-title">${t.name}</h5>
                            <p>Price: <b>$${t.price}</b></p>
                            <p>Seats: <b>${t.availableSeats}</b></p>
                            <p>Start: ${new Date(t.startDate).toLocaleDateString()}</p>
                            <p>End: ${new Date(t.endDate).toLocaleDateString()}</p>
                            <button class="btn btn-primary bookBtn" data-id="${t.id}" data-name="${t.name}">Book</button>
                        </div>
                    </div>
                </div>
            `).join('');

            document.querySelectorAll(".bookBtn").forEach(btn => {
                btn.addEventListener("click", () => {
                    selectedTourId = btn.dataset.id;
                    bookModalTourName.textContent = btn.dataset.name;
                    bookModal.show();
                });
            });

        } catch { showToast("Failed to load tours", "danger"); }
    }

    bookModalConfirm.addEventListener("click", async () => {
        try {
            const res = await fetch("/orders", {
                method: "POST",
                headers: { "Authorization": `Bearer ${token}`, "Content-Type": "application/json" },
                body: JSON.stringify({ tourId: selectedTourId })
            });
            if (!res.ok) throw new Error("Booking failed");
            showToast("Tour booked successfully!");
            bookModal.hide();
            await loadOrders();
            await loadTours();
        } catch (err) { showToast(err.message, "danger"); }
    });

    cancelModalConfirm.addEventListener("click", async () => {
        try {
            const res = await fetch(`/orders/${selectedOrderId}/cancel`, {
                method: "PUT",
                headers: { "Authorization": `Bearer ${token}` }
            });
            if (!res.ok) throw new Error("Failed to cancel order");
            showToast("Order cancelled");
            cancelModal.hide();
            await loadOrders();
            await loadTours();
        } catch (err) { showToast(err.message, "danger"); }
    });

    searchInput.addEventListener("input", loadTours);
    sortSelect.addEventListener("change", loadTours);

    await loadOrders();
    await loadTours();
});
