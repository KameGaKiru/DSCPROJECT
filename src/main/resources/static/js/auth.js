const USUARIO_API = "http://localhost:8080/api/usuario";

// LOGIN
async function login() {

    const matricula = document.getElementById("matricula").value.trim();
    const senha = document.getElementById("senha").value.replace(/\s/g, "");
    
    if (!matricula || !senha) {
        alert("Preencha os campos!");
        return;
    }

    try {
        const res = await fetch(`${USUARIO_API}/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ matricula, senha })
        });

        if (!res.ok) {
            alert("Dados inválidos!");
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
        alert("Erro de conexão!");
        console.error(err);
    }
}

// REGISTRAR 
async function registrar() {

    const matricula = document.getElementById("matricula").value.trim();
    const nome      = document.getElementById("nome").value.replace(/\s/g, "").trim();
    const sobrenome = document.getElementById("sobrenome").value.replace(/\s/g, "").trim();
    const senha     = document.getElementById("senha").value.replace(/\s/g, "");
    const funcao    = document.getElementById("funcao").value;

    if (!/^\d+$/.test(matricula) || matricula.length > 12) {
        alert("Matrícula obrigatória: (máximo 12 dígitos).");
        return;
    }
    if (!nome || nome.length > 60) {
        alert("Nome obrigatório: (máximo 60 caracteres).");
        return;
    }
    if (!sobrenome || sobrenome.length > 60) {
        alert("Sobrenome obrigatório: (máximo 60 caracteres).");
        return;
    }
    if (senha.length < 6 || senha.length > 20) {
        alert("A senha deve ter: (6 a 20 caracteres).");
        return;
    }

    try {
        const res = await fetch(`${USUARIO_API}/cadastrar`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ matricula, nome, sobrenome, senha, funcao })
        });

        if (res.ok) {
            alert("Usuário registrado com sucesso!");
            window.location.href = "index.html";
        } else {
            const erro = await res.text();
            alert("Erro ao registrar: " + erro);
        }
    } catch (err) {
        alert("Erro de conexão!");
        console.error(err);
    }
}