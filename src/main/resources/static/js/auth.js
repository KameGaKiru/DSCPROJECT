const USUARIO_API = "http://localhost:8080/api/usuario";

// LOGIN
async function login() {

    const matricula = document.getElementById("matricula").value.trim();
    const senha     = document.getElementById("senha").value;

    if (!matricula || !senha) {
        alert("Preencha matrícula e senha!");
        return;
    }

    try {
        const res = await fetch(`${USUARIO_API}/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ matricula, senha })
        });

        if (!res.ok) {
            alert("Matrícula ou senha inválidos!");
            return;
        }

        const dados = await res.json();

        localStorage.setItem("authHeader", dados.authHeader);
        localStorage.setItem("usuario", JSON.stringify(dados.usuario));

        const funcao = dados.usuario.funcao?.toUpperCase();

        if (funcao === "MOTORISTA") {
            window.location.href = "dashboard_motorista.html";
        } else if (funcao === "COORDENADOR") {
            window.location.href = "dashboard_coordenador.html";
        } else if (funcao === "MECANICO") {
            window.location.href = "dashboard_mecanico.html";
        } else {
            alert("Função não reconhecida: " + funcao);
        }

    } catch (err) {
        alert("Erro ao conectar ao servidor!");
        console.error(err);
    }
}

// REGISTRAR
async function registrar() {

    const data = {
        nome:      document.getElementById("nome").value.trim(),
        sobrenome: document.getElementById("sobrenome").value.trim(),
        matricula: document.getElementById("matricula").value.trim(),
        senha:     document.getElementById("senha").value,
        funcao:    document.getElementById("funcao").value
    };

    try {
        const res = await fetch(`${USUARIO_API}/cadastrar`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data)
        });

        if (res.ok) {
            alert("Usuário registrado com sucesso!");
            window.location.href = "index.html";
        } else {
            const erro = await res.text();
            alert("Erro ao registrar: " + erro);
        }
    } catch (err) {
        alert("Erro ao conectar ao servidor!");
        console.error(err);
    }
}