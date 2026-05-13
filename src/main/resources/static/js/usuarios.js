const USER_API = "http://localhost:8080/api/usuario";
const auth = localStorage.getItem("authHeader");
const usuarioLogado = JSON.parse(localStorage.getItem("usuarioLogado"));

// Bloquear motorista
if (usuarioLogado.funcao !== "COORDENADOR") {
    alert("Acesso negado! Somente coordenadores podem acessar essa página.");
    window.location.href = "dashboard_motorista.html";
}

// Listar
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
                <td>${u.matricula}</td>
                <td>${u.nome}</td>
                <td>${u.sobrenome}</td>
                <td>${u.funcao}</td>
                <td>
                    <button onclick="deletarUsuario('${u.matricula}')">Excluir</button>
                    <button onclick="atualizarUsuario('${u.matricula}')">Editar</button>
                </td>
            </tr>
        `;
    });
}

// Cadastrar
async function cadastrarUsuario() {
    const data = {
        matricula: document.getElementById("matricula").value,
        nome: document.getElementById("nome").value,
        sobrenome: document.getElementById("sobrenome").value,
        senha: document.getElementById("senha").value,
        funcao: document.getElementById("funcao").value
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

// Atualizar
async function atualizarUsuario(matricula) {
    const novoNome = prompt("Novo nome:");
    const novoSobrenome = prompt("Novo sobrenome:");
    const novaFuncao = prompt("Nova função (MOTORISTA/COORDENADOR):");

    const response = await fetch(`${USER_API}/atualizar/${matricula}`, {
        method: "PUT",
        headers: { 
            "Authorization": auth,
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ nome: novoNome, sobrenome: novoSobrenome, funcao: novaFuncao })
    });

    if (response.ok) {
        alert("Usuário atualizado!");
        listarUsuarios();
    } else {
        alert("Erro ao atualizar usuário!");
    }
}

// Deletar
async function deletarUsuario(matricula) {
    if (!confirm("Tem certeza que deseja excluir?")) return;

    const response = await fetch(`${USER_API}/deletar/${matricula}`, {
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

window
