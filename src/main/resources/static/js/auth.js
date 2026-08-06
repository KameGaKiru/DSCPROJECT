
const USUARIO_API = "http://localhost:8080/api/usuario";

function limparErrosAutenticacao() {
    ["matricula", "senha", "nome", "sobrenome", "funcao"]
        .forEach(id => limparErroCampo(id));
    limparMensagemGeral();
}

async function login() {
    limparErrosAutenticacao();

    const matricula = document.getElementById("matricula").value.trim();
    const senha = document.getElementById("senha").value.replace(/\s/g, "");

    let valido = true;

    if (!matricula) {
        mostrarErroCampo("matricula", "Informe a matrícula.");
        valido = false;
    }

    if (!senha) {
        mostrarErroCampo("senha", "Informe a senha.", !matricula);
        valido = false;
    }

    if (!valido) return;

    try {
        const res = await fetch(`${USUARIO_API}/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ matricula, senha })
        });

        if (!res.ok) {
            mostrarErroCampo("matricula", "Matrícula ou senha inválidas.", false);
            mostrarErroCampo("senha", "Confira suas credenciais.");
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
            mostrarMensagemGeral(`Função não reconhecida: ${funcao ?? "não informada"}.`, "error");
        }

    } catch (err) {
        mostrarMensagemGeral("Não foi possível conectar ao servidor. Tente novamente.", "error");
        console.error(err);
    }
}

async function registrar() {
    limparErrosAutenticacao();

    const matricula = document.getElementById("matricula").value.trim();
    const nome = document.getElementById("nome").value.replace(/\s/g, "").trim();
    const sobrenome = document.getElementById("sobrenome").value.replace(/\s/g, "").trim();
    const senha = document.getElementById("senha").value.replace(/\s/g, "");
    const funcao = document.getElementById("funcao").value;

    let valido = true;

    if (!/^\d+$/.test(matricula) || matricula.length > 12) {
        mostrarErroCampo("matricula", "Informe uma matrícula numérica com no máximo 12 dígitos.");
        valido = false;
    }

    if (!nome || nome.length > 60) {
        mostrarErroCampo("nome", "Informe o nome com no máximo 60 caracteres.", !valido);
        valido = false;
    }

    if (!sobrenome || sobrenome.length > 60) {
        mostrarErroCampo("sobrenome", "Informe o sobrenome com no máximo 60 caracteres.", !valido);
        valido = false;
    }

    if (senha.length < 6 || senha.length > 20) {
        mostrarErroCampo("senha", "A senha deve ter entre 6 e 20 caracteres.", !valido);
        valido = false;
    }

    if (!funcao) {
        mostrarErroCampo("funcao", "Selecione uma função.", !valido);
        valido = false;
    }

    if (!valido) return;

    try {
        const res = await fetch(`${USUARIO_API}/cadastrar`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ matricula, nome, sobrenome, senha, funcao })
        });

        if (res.ok) {
            salvarMensagemTemporaria("Usuário registrado com sucesso.", "success");
            window.location.href = "index.html";
            return;
        }

        const erro = await res.text();

        if (/matr[ií]cula|duplic|cadastrad/i.test(erro)) {
            mostrarErroCampo("matricula", "Esta matrícula já está cadastrada.");
        } else {
            mostrarMensagemGeral(`Não foi possível registrar o usuário. ${erro}`, "error");
        }

    } catch (err) {
        mostrarMensagemGeral("Não foi possível conectar ao servidor. Tente novamente.", "error");
        console.error(err);
    }
}


document.addEventListener("DOMContentLoaded", () => {
    const matricula = document.getElementById("matricula");
    const senha = document.getElementById("senha");
    const nome = document.getElementById("nome");
    const sobrenome = document.getElementById("sobrenome");
    const funcao = document.getElementById("funcao");

    matricula?.addEventListener("input", function () {
        this.value = this.value.replace(/\D/g, "").slice(0, 12);
        limparErroCampo("matricula");
    });

    senha?.addEventListener("input", function () {
        this.value = this.value.replace(/\s/g, "").slice(0, 20);
        limparErroCampo("senha");
    });

    nome?.addEventListener("input", function () {
        this.value = this.value
            .replace(/\s/g, "")
            .replace(/[^a-zA-ZÀ-ÿ]/g, "")
            .slice(0, 60);

        limparErroCampo("nome");
    });

    sobrenome?.addEventListener("input", function () {
        this.value = this.value
            .replace(/\s/g, "")
            .replace(/[^a-zA-ZÀ-ÿ]/g, "")
            .slice(0, 60);

        limparErroCampo("sobrenome");
    });

    funcao?.addEventListener("change", () => {
        limparErroCampo("funcao");
    });

    document.getElementById("loginForm")?.addEventListener("submit", event => {
        event.preventDefault();
        login();
    });

    document.getElementById("registroForm")?.addEventListener("submit", event => {
        event.preventDefault();
        registrar();
    });
});
