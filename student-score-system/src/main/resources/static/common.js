const API_BASE = 'http://localhost:8080/api';

function getToken() {
    return localStorage.getItem('token');
}

function checkAuth(requiredRole) {
    const token = getToken();
    const user = JSON.parse(localStorage.getItem('user') || '{}');
    
    if (!token || !user.role) {
        window.location.href = 'login.html';
        return;
    }
    
    if (requiredRole && user.role !== requiredRole) {
        alert('权限不足');
        window.location.href = 'login.html';
    }
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = 'login.html';
}

async function apiGet(url) {
    const response = await fetch(`${API_BASE}${url}`, {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        }
    });
    const result = await response.json();
    if (result.code === 200) {
        return result.data;
    } else {
        throw new Error(result.message);
    }
}

async function apiPost(url, data) {
    const response = await fetch(`${API_BASE}${url}`, {
        method: 'POST',
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });
    const result = await response.json();
    if (result.code !== 200) {
        throw new Error(result.message);
    }
    return result.data;
}

async function apiPut(url, data) {
    const response = await fetch(`${API_BASE}${url}`, {
        method: 'PUT',
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });
    const result = await response.json();
    if (result.code !== 200) {
        throw new Error(result.message);
    }
    return result.data;
}

async function apiDelete(url) {
    const response = await fetch(`${API_BASE}${url}`, {
        method: 'DELETE',
        headers: {
            'Authorization': 'Bearer ' + getToken(),
            'Content-Type': 'application/json'
        }
    });
    const result = await response.json();
    if (result.code !== 200) {
        throw new Error(result.message);
    }
    return result.data;
}
