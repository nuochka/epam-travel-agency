const token = localStorage.getItem('jwtToken');

function showToast(message) {
    const toastElement = document.getElementById('dashboardToast');
    const toastBody = document.getElementById('dashboardToastMessage');
    toastBody.textContent = message;
    const toast = new bootstrap.Toast(toastElement, { delay: 5000 });
    toast.show();
}

document.addEventListener('DOMContentLoaded', () => {
    if (token) {
        fetch('/api/dashboard-info', { headers: { 'Authorization': 'Bearer ' + token } })
        .then(res => {
            if (!res.ok) throw new Error("Authentication failed or server error.");
            return res.json();
        })
        .then(data => {
            document.getElementById('username').textContent = data.username;
            
            if (data.isAdmin) {
                document.getElementById('adminSection').style.display = 'block';
                fetch('/api/cities', { headers: { 'Authorization': 'Bearer ' + token } })
                    .then(res => {
                        if (!res.ok) throw new Error(`HTTP error! Status: ${res.status}`);
                        return res.json();
                    })
                    .then(cities => {
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
                    })
                    .catch(err => {
                        console.error('Error loading cities:', err);
                        const select = document.getElementById('tourCity');
                        select.innerHTML = '<option value="">Failed to load cities</option>';
                        showToast('Error loading cities. Check console for details.');
                    });
            }
        })
        .catch(error => {
            console.error("Dashboard info error:", error);
        });
    }

    document.getElementById('createTourForm').addEventListener('submit', e => {
        e.preventDefault();

        const cityIdValue = document.getElementById('tourCity').value;

        if (!cityIdValue) {
            showToast('Please select a city to create a tour.');
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

        fetch('/tours/create', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify(dto)
        })
        .then(async res => {
            if (!res.ok) {
                const errorText = await res.text();
                throw new Error(`Tour creation failed. Status: ${res.status}. Server response: ${errorText.substring(0, 100)}...`);
            }
            return res.json();
        })
        .then(data => {
            showToast(`Tour '${data.name}' created successfully!`);
            document.getElementById('createTourForm').reset();
            window.location.reload(); 
        })
        .catch(err => {
            console.error("Tour creation error:", err);
            showToast(`Error creating tour: ${err.message}`);
        });
    });
});