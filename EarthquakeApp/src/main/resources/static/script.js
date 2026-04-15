const API_BASE = '/api/earthquakes';

let currentEarthquakes = [];

function formatTime(timestamp) {
    const date = new Date(timestamp);
    return date.toLocaleString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        hour12: true
    });
}

function getMagClass(mag) {
    if (mag >= 6.0) return 'mag-high';
    if (mag >= 4.0) return 'mag-medium';
    return '';
}

function renderTable(earthquakes) {
    const tbody = document.getElementById('tableBody');
    tbody.innerHTML = '';

    currentEarthquakes = earthquakes;

    document.getElementById('resultCount').textContent = `Earthquakes (${earthquakes.length})`;
    document.getElementById('lastUpdated').textContent = new Date().toLocaleTimeString();

    if (earthquakes.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="5" class="text-center py-4 text-muted">
                    No earthquakes found. Try fetching latest data.
                </td>
            </tr>`;
        return;
    }

    earthquakes.forEach(eq => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td><span class="${getMagClass(eq.magnitude)}">${eq.magnitude}</span></td>
            <td>${eq.place || 'Unknown'}</td>
            <td>${formatTime(eq.time)}</td>
            <td>${eq.magType || '-'}</td>
            <td>
                <button class="btn btn-sm btn-danger" onclick="deleteEarthquake(${eq.id})">
                    Delete
                </button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

async function refreshData() {
    if (!confirm("Fetch latest earthquake data from USGS API?")) return;

    try {
        const response = await fetch(`${API_BASE}/fetch`);
        const message = await response.text();

        alert(message || "Data fetched successfully!");
        loadAllEarthquakes();
    } catch (error) {
        console.error(error);
        alert("Failed to fetch data. Please check if backend is running.");
    }
}

async function loadAllEarthquakes() {
    try {
        const response = await fetch(API_BASE);
        if (!response.ok) throw new Error("Failed to load data");

        const data = await response.json();
        renderTable(data);
    } catch (error) {
        console.error(error);
        alert("Error loading earthquakes. Make sure Spring Boot is running on port 8080.");
    }
}

async function filterByMagnitude() {
    const minMag = document.getElementById('minMag').value;
    if (!minMag) return;

    try {
        const response = await fetch(`${API_BASE}/filter?minMag=${minMag}`);
        const data = await response.json();
        renderTable(data);
    } catch (error) {
        alert("Error applying magnitude filter");
    }
}

async function filterAfterTime() {
    const timeInput = document.getElementById('afterTime').value;
    if (!timeInput) {
        alert("Please select a date and time");
        return;
    }

    const timestamp = new Date(timeInput).getTime();

    try {
        const response = await fetch(`${API_BASE}/after?timestamp=${timestamp}`);
        const data = await response.json();
        renderTable(data);
    } catch (error) {
        alert("Error applying time filter");
    }
}

async function deleteEarthquake(id) {
    if (!confirm("Are you sure you want to delete this earthquake record?")) return;

    try {
        const response = await fetch(`/api/earthquakes/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            alert("Earthquake deleted successfully");
            loadAllEarthquakes();
        } else {
            alert("Failed to delete the record");
        }
    } catch (error) {
        console.error(error);
        alert("Error occurred while deleting");
    }
}

window.onload = function() {
    loadAllEarthquakes();
};