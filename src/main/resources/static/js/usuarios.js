const USER_API = "http://localhost:8080/api/usuario";
const auth = localStorage.getItem("authHeader");

// === LISTAR ===
async function listarUsuarios() {
    const response = await fetch(`${USER_API}/listar`, {
        headers: { "Authorization": auth }
    });
    const usuarios = await response.json();

    const tbody = document.getElementById("usuariosTable");
    tbody.innerHTML = "";
    usuarios.forEach(u => {
        tbody.innerHTML += `
            <tr>
                <td>${u.id}</td>
                <td>${u.nome}</td>
                <td>${u.sobrenome}</td>
                <td>
                    <button onclick="deletarUsuario('${u.id}')">Excluir</button>
                    <button onclick="atualizarUsuario('${u.id}')">Editar</button>
                </td>
            </tr>
        `;
    });
}

// === CADASTRAR ===
async function cadastrarUsuario() {
    const data = {
        nome: document.getElementById("nome").value,
        sobrenome: document.getElementById("sobrenome").value,
        login: document.getElementById("login").value,
        senha: document.getElementById("senha").value
    };

    const response = await fetch(`${USER_API}/cadastrar`, {
        method: "POST",
        headers: { 
            "Authorization": auth,
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    });

    if (response.ok) {
        alert("Usuário cadastrado!");
        listarUsuarios();
    } else {
        alert("Erro ao cadastrar usuário!");
    }
}

// === ATUALIZAR ===
async function atualizarUsuario(id) {
    const novoNome = prompt("Novo nome:");
    const novoSobrenome = prompt("Novo sobrenome:");

    const response = await fetch(`${USER_API}/atualizar/${id}`, {
        method: "PUT",
        headers: { 
            "Authorization": auth,
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ nome: novoNome, sobrenome: novoSobrenome })
    });

    if (response.ok) {
        alert("Usuário atualizado!");
        listarUsuarios();
    } else {
        alert("Erro ao atualizar usuário!");
    }
}

// === DELETAR ===
async function deletarUsuario(id) {
    if (!confirm("Tem certeza que deseja excluir?")) return;

    const response = await fetch(`${USER_API}/deletar/${id}`, {
        method: "DELETE",
        headers: { "Authorization": auth }
    });

    if (response.ok) {
        alert("Usuário deletado!");
        listarUsuarios();
    } else {
        alert("Erro ao deletar usuário!");
    }
}

window.onload = listarUsuarios;
