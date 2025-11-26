document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("jwtToken");
    if (!token) {
        window.location.href = "/auth/login";
        return;
    }

    const usernameEl = document.getElementById("username");
    const tourList = document.getElementById("tourList");
    const orderList = document.getElementById("orderList");
    const toastEl = document.getElementById("dashboardToast");
    const toastMessage = document.getElementById("dashboardToastMessage");

    const bookModal = new bootstrap.Modal(document.getElementById("bookModal"));
    const cancelModal = new bootstrap.Modal(document.getElementById("cancelModal"));

    const bookModalTourName = document.getElementById("bookModalTourName");
    const bookModalConfirm = document.getElementById("bookModalConfirm");

    const cancelModalTourName = document.getElementById("cancelModalTourName");
    const cancelModalConfirm = document.getElementById("cancelModalConfirm");

    let selectedTourId = null;
    let selectedOrderId = null;
    let myOrders = [];

    function showToast(message, type = "success") {
        toastEl.className = `toast align-items-center text-bg-${type} border-0 position-fixed bottom-0 end-0 m-3`;
        toastMessage.textContent = message;
        new bootstrap.Toast(toastEl).show();
    }

    function parseJwtPayload(token) {
        return JSON.parse(atob(token.split(".")[1]));
    }

    const username = parseJwtPayload(token).sub;
    usernameEl.textContent = username;

    async function loadOrders() {
        try {
            const res = await fetch("/orders/me", {
                headers: { "Authorization": `Bearer ${token}` }
            });
            myOrders = await res.json();

            orderList.innerHTML = myOrders.map(order => `
                <div class="col-md-4 mb-3">
                    <div class="card shadow-sm">
                        <div class="card-body">
                            <h5>${order.tourName}</h5>
                            <p>Status: <b>${order.status}</b></p>
                            <p>Date: ${new Date(order.bookingDate).toLocaleString()}</p>

                            ${
                                order.status === "NEW"
                                ? `<button class="btn btn-danger cancelBtn" data-id="${order.id}" data-tour="${order.tourName}">Cancel</button>`
                                : ""
                            }

                            ${
                                order.status === "CANCELLED"
                                ? `<button class="btn btn-outline-danger mt-2 deleteBtn" data-id="${order.id}">Delete</button>`
                                : ""
                            }
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
                    const id = btn.dataset.id;

                    try {
                        const res = await fetch(`/orders/${id}`, {
                            method: "DELETE",
                            headers: { "Authorization": `Bearer ${token}` }
                        });

                        if (!res.ok) throw new Error("Failed to delete order");

                        showToast("Order deleted");
                        await loadOrders();
                        await loadTours();

                    } catch (err) {
                        showToast(err.message, "danger");
                    }
                });
            });

        } catch (err) {
            showToast("Failed to load orders", "danger");
        }
    }

    async function loadTours() {
        try {
            const res = await fetch("/tours", {
                headers: { "Authorization": `Bearer ${token}` }
            });
            const tours = await res.json();

            const bookedTourIds = myOrders.map(o => o.tourId);
            const availableTours = tours.filter(t => !bookedTourIds.includes(t.id) && t.availableSeats > 0);

            tourList.innerHTML = availableTours.map(tour => `
                <div class="col-md-4 mb-3">
                    <div class="card shadow-sm">
                        <div class="card-body">
                            <h5 class="card-title">${tour.name}</h5>
                            <p>Price: <b>$${tour.price}</b></p>
                            <p>Seats: <b>${tour.availableSeats}</b></p>
                            <p>Start: ${new Date(tour.startDate).toLocaleDateString()}</p>
                            <p>End: ${new Date(tour.endDate).toLocaleDateString()}</p>
                            <button class="btn btn-primary bookBtn" data-id="${tour.id}" data-name="${tour.name}">Book</button>
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

        } catch (err) {
            showToast("Failed to load tours", "danger");
        }
    }

    bookModalConfirm.addEventListener("click", async () => {
        try {
            const res = await fetch("/orders", {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ tourId: selectedTourId })
            });

            if (!res.ok) throw new Error("Booking failed");

            showToast("Tour booked successfully!");
            bookModal.hide();

            await loadOrders();
            await loadTours();

        } catch (err) {
            showToast(err.message, "danger");
        }
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

        } catch (err) {
            showToast(err.message, "danger");
        }
    });

    await loadOrders();
    await loadTours();
});
