document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("jwtToken");
    if (!token) { 
        window.location.href = "/auth/login"; 
        return; 
    }

    const usernameEl = document.getElementById("username");
    const tourList = document.getElementById("tourList");
    const orderList = document.getElementById("orderList");
    const orderSection = document.getElementById("orderSection");
    const noOrdersMsg = document.getElementById("noOrdersMsg");
    const toastEl = document.getElementById("dashboardToast");
    const toastMessage = document.getElementById("dashboardToastMessage");
    const adminSection = document.getElementById("adminSection");

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
    let isAdmin = false;

    function showToast(message, type = "success") {
        toastEl.className = `toast align-items-center text-bg-${type} border-0 position-fixed bottom-0 end-0 m-3`;
        toastMessage.textContent = message;
        new bootstrap.Toast(toastEl).show();
    }

    async function deleteTour(tourId, buttonElement) {
        if (!confirm(`Are you sure you want to delete Tour ID ${tourId}?`)) {
            return;
        }

        try {
            const res = await fetch(`/tours/${tourId}`, {
                method: "DELETE",
                headers: { "Authorization": `Bearer ${token}` }
            });

            if (res.status === 204) {
                showToast(`Tour ID ${tourId} deleted successfully.`);

                const card = buttonElement.closest('.col-md-4');
                if (card) {
                    card.remove();
                }
                allTours = allTours.filter(t => t.id !== parseInt(tourId));

            } else if (res.status === 403) {
                showToast("Error: Access denied. Only ADMIN can delete tours.", "danger");
            } else {
                throw new Error(`Deletion failed with status: ${res.status}`);
            }

        } catch (err) {
            console.error("Deletion error:", err);
            showToast(`Error deleting tour: ${err.message}`, "danger");
        }
    }

    async function loadCities() {
        try {
            const res = await fetch('/api/cities', { headers: { 'Authorization': 'Bearer ' + token } });
            if (!res.ok) throw new Error(`HTTP error! Status: ${res.status}`);
            const cities = await res.json();
            
            const select = document.getElementById('tourCity');
            select.innerHTML = '';
            const defaultOption = document.createElement('option');
            defaultOption.value = "";
            defaultOption.textContent = "--- Select a City ---";
            defaultOption.disabled = true;
            defaultOption.selected = true; 
            select.appendChild(defaultOption);

            cities.forEach(city => {
                const option = document.createElement('option');
                option.value = city.id;
                option.textContent = `${city.name} (${city.countryName})`; 
                select.appendChild(option);
            });
        } catch (err) {
            console.error('Error loading cities:', err);
            const select = document.getElementById('tourCity');
            select.innerHTML = '<option value="">Failed to load cities</option>';
            showToast('Error loading cities. Check console for details.', "danger");
        }
    }


    async function loadAdminInfo() {
        try {
            const res = await fetch('/api/dashboard-info', { headers: { 'Authorization': 'Bearer ' + token } });
            if (!res.ok) throw new Error("Authentication failed or server error.");
            const data = await res.json();
            
            usernameEl.textContent = data.username;
            isAdmin = data.isAdmin;

            if (isAdmin) {
                adminSection.style.display = 'block';
                await loadCities();
            }
        } catch (error) {
            console.error("Dashboard info error:", error);
            showToast("Failed to load user info.", "danger");
        }
    }

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
                            <p>City: <b>${t.cityName}</b></p>
                            <p>Price: <b>$${t.price}</b></p>
                            <p>Seats: <b>${t.availableSeats}</b></p>
                            <p>Start: ${new Date(t.startDate).toLocaleDateString()}</p>
                            <p>End: ${new Date(t.endDate).toLocaleDateString()}</p>
                            <button class="btn btn-primary bookBtn" data-id="${t.id}" data-name="${t.name}">Book</button>
                            
                            ${isAdmin ? `<button class="btn btn-danger deleteTourBtn mt-2" data-id="${t.id}">Delete</button>` : ""}
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
            document.querySelectorAll(".deleteTourBtn").forEach(btn => {
                btn.addEventListener("click", () => {
                    deleteTour(btn.dataset.id, btn); 
                });
            });


        } catch { showToast("Failed to load tours", "danger"); }
    }

    document.getElementById('createTourForm').addEventListener('submit', async e => {
        e.preventDefault();

        const cityIdValue = document.getElementById('tourCity').value;

        if (!cityIdValue) {
            showToast('Please select a city to create a tour.', "danger");
            return; 
        }

        const dto = {
            name: document.getElementById('tourName').value,
            cityId: parseInt(cityIdValue),
            price: parseFloat(document.getElementById('tourPrice').value),
            availableSeats: parseInt(document.getElementById('tourSeats').value),
            startDate: document.getElementById('tourStartDate').value,
            endDate: document.getElementById('tourEndDate').value
        };

        try {
            const res = await fetch('/tours/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + token
                },
                body: JSON.stringify(dto)
            });
            
            if (!res.ok) {
                const errorText = await res.text();
                throw new Error(`Tour creation failed. Status: ${res.status}. Server response: ${errorText.substring(0, 100)}...`);
            }
            
            const data = await res.json();
            showToast(`Tour '${data.name}' created successfully!`);
            document.getElementById('createTourForm').reset();
            
            await loadTours(); 
            
        } catch (err) {
            console.error("Tour creation error:", err);
            showToast(`Error creating tour: ${err.message}`, "danger");
        }
    });


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

    await loadAdminInfo();
    await loadOrders();
    await loadTours();
});