const API = '/api';
let crops = [];

const today = new Date().toISOString().slice(0, 10);
document.getElementById('harvestDate').value = today;
document.getElementById('saleDate').value = today;
document.getElementById('fromDate').value = today.slice(0, 8) + '01';
document.getElementById('toDate').value = today;

document.getElementById('cropForm').addEventListener('submit', async e => {
    e.preventDefault();
    try {
        await request('/crops', 'POST', {
            name: value('cropName'), category: value('category'), unit: value('unit')
        }, 'Crop added successfully');
        e.target.reset();
        await loadCrops();
    } catch (_) {}
});

document.getElementById('harvestForm').addEventListener('submit', async e => {
    e.preventDefault();
    const cropId = value('harvestCrop');
    try {
        await request(`/crops/${cropId}/harvests`, 'POST', {
        quantity: Number(value('harvestQty')), harvestDate: value('harvestDate')
        }, 'Harvest recorded successfully');
        e.target.reset();
        document.getElementById('harvestDate').value = today;
        await loadStock();
    } catch (_) {}
});

document.getElementById('saleForm').addEventListener('submit', async e => {
    e.preventDefault();
    const cropId = value('saleCrop');
    try {
        await request(`/crops/${cropId}/sales`, 'POST', {
        quantity: Number(value('saleQty')), pricePerUnit: Number(value('price')), saleDate: value('saleDate')
        }, 'Sale recorded successfully');
        e.target.reset();
        document.getElementById('saleDate').value = today;
        await loadStock();
    } catch (_) {}
});

async function request(url, method = 'GET', body, successMessage) {
    try {
        const res = await fetch(API + url, {
            method,
            headers: { 'Content-Type': 'application/json' },
            body: body ? JSON.stringify(body) : undefined
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.message || 'Request failed');
        if (successMessage) showMessage(successMessage);
        return data;
    } catch (err) {
        showMessage(err.message, true);
        throw err;
    }
}

async function loadCrops() {
    try {
        crops = await request('/crops');
        ['harvestCrop', 'saleCrop', 'reportCrop'].forEach(id => {
            const select = document.getElementById(id);
            select.innerHTML = crops.map(c => `<option value="${c.id}">${escapeHtml(c.name)}</option>`).join('');
        });
        await loadStock();
    } catch (_) {}
}

async function loadStock() {
    const container = document.getElementById('stockList');
    if (!crops.length) {
        container.innerHTML = '<p>No crops yet. Add a crop above.</p>';
        return;
    }
    const results = await Promise.all(crops.map(async crop => {
        const stock = await request(`/crops/${crop.id}/stock`);
        return `<div class="stock-item"><strong>${escapeHtml(crop.name)}</strong><span class="stock-number">${stock.currentStock}</span> ${escapeHtml(crop.unit)}</div>`;
    }));
    container.innerHTML = results.join('');
}

async function getRevenue() {
    const cropId = value('reportCrop');
    const from = value('fromDate');
    const to = value('toDate');
    try {
        const data = await request(`/crops/${cropId}/revenue?from=${from}&to=${to}`);
        document.getElementById('revenueResult').textContent = `Total revenue: ₹${Number(data.totalRevenue).toFixed(2)}`;
    } catch (_) {}
}

function value(id) { return document.getElementById(id).value; }
function showMessage(text, error = false) {
    const el = document.getElementById('message');
    el.textContent = text;
    el.style.background = error ? '#a33b3b' : '#285943';
    el.style.display = 'block';
    setTimeout(() => el.style.display = 'none', 2500);
}
function escapeHtml(s) { return String(s).replace(/[&<>'"]/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[c])); }

loadCrops();
