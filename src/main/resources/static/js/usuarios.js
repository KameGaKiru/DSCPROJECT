
const USER_API = "http://localhost:8080/api/usuario";
const auth = localStorage.getItem("authHeader");
const usuarioLogado =
    JSON.parse(localStorage.getItem("usuario")) ||
    JSON.parse(localStorage.getItem("usuarioLogado"));

if (!usuarioLogado || !auth || usuarioLogado.funcao?.toUpperCase() !== "COORDENADOR") {
    salvarMensagemTemporaria("Acesso negado.", "warning");
    window.location.href = "index.html";
}

async function listarUsuarios() {
    try {
        const response = await fetch(`${USER_API}/listar`, {
            headers: { "Authorization": auth }
        });

        if (response.status === 401 || response.status === 403) {
            salvarMensagemTemporaria("Sua sessão expirou. Entre novamente.", "warning");
            localStorage.clear();
            window.location.href = "index.html";
            return;
        }

        if (!response.ok) {
            mostrarMensagemGeral("Não foi possível carregar os usuários.", "error");
            return;
        }

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
                </tr>`;
        });

    } catch (err) {
        mostrarMensagemGeral("Não foi possível conectar ao servidor.", "error");
        console.error(err);
    }
}

async function cadastrarUsuario() {
    limparTodosErros();

    const data = {
        matricula: document.getElementById("matricula").value.trim(),
        nome: document.getElementById("nome").value.trim(),
        sobrenome: document.getElementById("sobrenome").value.trim(),
        senha: document.getElementById("senha").value,
        funcao: document.getElementById("funcao").value
    };

    let valido = true;

    if (!data.matricula) {
        mostrarErroCampo("matricula", "Informe a matrícula.");
        valido = false;
    }
    if (!data.nome) {
        mostrarErroCampo("nome", "Informe o nome.", !valido);
        valido = false;
    }
    if (!data.sobrenome) {
        mostrarErroCampo("sobrenome", "Informe o sobrenome.", !valido);
        valido = false;
    }
    if (!data.senha) {
        mostrarErroCampo("senha", "Informe a senha.", !valido);
        valido = false;
    }
    if (!data.funcao) {
        mostrarErroCampo("funcao", "Selecione uma função.", !valido);
        valido = false;
    }

    if (!valido) return;

    try {
        const response = await fetch(`${USER_API}/cadastrar`, {
            method: "POST",
            headers: {
                "Authorization": auth,
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            mostrarMensagemGeral("Usuário cadastrado com sucesso.", "success", 5000);
            document.getElementById("usuarioForm")?.reset();
            listarUsuarios();
        } else {
            const erro = await response.text();
            if (/matr[ií]cula|duplic|cadastrad/i.test(erro)) {
                mostrarErroCampo("matricula", "Esta matrícula já está cadastrada.");
            } else {
                mostrarMensagemGeral(`Não foi possível cadastrar o usuário. ${erro}`, "error");
            }
        }

    } catch (err) {
        mostrarMensagemGeral("Não foi possível conectar ao servidor.", "error");
        console.error(err);
    }
}

async function atualizarUsuario(matricula) {
    const novoNome = prompt("Novo nome:");
    if (novoNome === null) return;

    const novoSobrenome = prompt("Novo sobrenome:");
    if (novoSobrenome === null) return;

    const novaFuncao = prompt("Nova função (MOTORISTA/COORDENADOR/MECANICO):");
    if (novaFuncao === null) return;

    try {
        const response = await fetch(`${USER_API}/atualizar/${matricula}`, {
            method: "PUT",
            headers: {
                "Authorization": auth,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                nome: novoNome.trim(),
                sobrenome: novoSobrenome.trim(),
                funcao: novaFuncao.trim().toUpperCase()
            })
        });

        if (response.ok) {
            mostrarMensagemGeral("Usuário atualizado com sucesso.", "success", 5000);
            listarUsuarios();
        } else {
            const erro = await response.text();
            mostrarMensagemGeral(`Não foi possível atualizar o usuário. ${erro}`, "error");
        }

    } catch (err) {
        mostrarMensagemGeral("Não foi possível conectar ao servidor.", "error");
        console.error(err);
    }
}

async function deletarUsuario(matricula) {
    if (!confirm("Tem certeza que deseja excluir este usuário?")) return;

    try {
        const response = await fetch(`${USER_API}/deletar/${matricula}`, {
            method: "DELETE",
            headers: { "Authorization": auth }
        });

        if (response.ok) {
            mostrarMensagemGeral("Usuário excluído com sucesso.", "success", 5000);
            listarUsuarios();
        } else {
            const erro = await response.text();
            mostrarMensagemGeral(`Não foi possível excluir o usuário. ${erro}`, "error");
        }

    } catch (err) {
        mostrarMensagemGeral("Não foi possível conectar ao servidor.", "error");
        console.error(err);
    }
}

document.addEventListener("DOMContentLoaded", listarUsuarios);
