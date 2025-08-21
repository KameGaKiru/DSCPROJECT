const API_URL = "http://localhost:8080/api/usuario";
let authHeader = null;

// === LOGIN ===
async function login() {
    const username = document.getElementById("username").value;
    const senha = document.getElementById("senha").value;

    authHeader = "Basic " + btoa(username + ":" + senha);

    const response = await fetch(`${API_URL}/listar`, {
        method: "GET",
        headers: { "Authorization": authHeader }
    });

    if (response.ok) {
        alert("Login realizado com sucesso!");
        localStorage.setItem("authHeader", authHeader);
        window.location.href = "usuarios.html";
    } else {
        alert("Usuário ou senha inválidos!");
    }
}

// === REGISTRO DE NOVO USUÁRIO ===
async function registrar() {
    const data = {
        nome: document.getElementById("nome").value,
        sobrenome: document.getElementById("sobrenome").value,
        login: document.getElementById("login").value,
        senha: document.getElementById("senha").value
    };

    const response = await fetch(`${API_URL}/cadastrar`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    });

    if (response.ok) {
        alert("Usuário registrado com sucesso!");
        window.location.href = "index.html";
    } else {
        alert("Erro ao registrar usuário!");
    }
}
