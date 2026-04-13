    const API_URL = "http://localhost:8080/api/usuario";

    async function login() {
        const matricula = document.getElementById("matricula").value;
        const senha = document.getElementById("senha").value;

        try {
            const response = await fetch(`${API_URL}/login`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ matricula, senha })
            });

            if (!response.ok) {
                alert("Usuário ou senha inválidos!");
                return;
            }

            const dados = await response.json();

            localStorage.setItem("authHeader", dados.authHeader);
            localStorage.setItem("usuario", JSON.stringify(dados.usuario));

            const funcao = dados.usuario.funcao?.toUpperCase();

            if (funcao === "MOTORISTA") {
                window.location.href = "dashboard_motorista.html";
            } else if (funcao === "COORDENADOR") {
                window.location.href = "dashboard_coordenador.html";
            } else {
                alert("Função não reconhecida: " + funcao);
            }

        } catch (err) {
            alert("Erro ao conectar ao servidor!");
            console.error(err);
        }
    }

    async function registrar() {
        const data = {
            nome: document.getElementById("nome").value,
            sobrenome: document.getElementById("sobrenome").value,
            matricula: document.getElementById("matricula").value,
            senha: document.getElementById("senha").value,
            funcao: document.getElementById("funcao").value
        };

        try {
            const response = await fetch(`${API_URL}/cadastrar`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                alert("Usuário registrado com sucesso!");
                window.location.href = "index.html";
            } else {
                const erro = await response.text();
                alert("Erro ao registrar: " + erro);
            }
        } catch (err) {
            alert("Erro ao conectar ao servidor!");
            console.error(err);
        }
    }