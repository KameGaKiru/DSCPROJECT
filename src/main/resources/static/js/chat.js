const usuario = JSON.parse(localStorage.getItem("usuario"));
const authHeader = localStorage.getItem("authHeader");

const mensagensEl = document.getElementById("mensagens");
const mensagemInput = document.getElementById("mensagemInput");
const enviarBtn = document.getElementById("enviarBtn");
const chatForm = document.getElementById("chatForm");
const statusConexao = document.getElementById("statusConexao");
const contadorMensagem = document.getElementById("contadorMensagem");
const voltarBtn = document.getElementById("voltarBtn");

let stompClient = null;
let primeiraMensagem = true;

if (
    !usuario ||
    !authHeader ||
    !["MOTORISTA", "MECANICO"].includes(usuario.funcao?.toUpperCase())
) {
    alert("Sessão inválida ou acesso negado!");
    localStorage.clear();
    window.location.href = "index.html";
}

function conectarChat() {
    stompClient = new StompJs.Client({
        webSocketFactory: () => new SockJS("/ws-chat"),

        reconnectDelay: 5000,

        onConnect: () => {
            statusConexao.textContent = "Conectado";
            enviarBtn.disabled = false;

            stompClient.subscribe("/topic/chat", mensagem => {
                const dados = JSON.parse(mensagem.body);
                exibirMensagem(dados);
            });

            stompClient.publish({
                destination: "/app/chat.entrar",
                body: JSON.stringify({
                    remetenteMatricula: usuario.matricula,
                    remetenteNome: `${usuario.nome} ${usuario.sobrenome ?? ""}`.trim(),
                    remetenteFuncao: usuario.funcao
                })
            });
        },

        onStompError: frame => {
            console.error("Erro STOMP:", frame);
            statusConexao.textContent = "Erro na conexão";
            enviarBtn.disabled = true;
        },

        onWebSocketClose: () => {
            statusConexao.textContent = "Desconectado";
            enviarBtn.disabled = true;
        }
    });

    stompClient.activate();
}

function enviarMensagem() {
    const conteudo = mensagemInput.value.trim();

    if (!conteudo) {
        alert("Digite uma mensagem antes de enviar.");
        return;
    }

    if (conteudo.length > 500) {
        alert("A mensagem deve possuir no máximo 500 caracteres.");
        return;
    }

    if (!stompClient || !stompClient.connected) {
        alert("O chat não está conectado.");
        return;
    }

    stompClient.publish({
        destination: "/app/chat.enviar",
        body: JSON.stringify({
            remetenteMatricula: usuario.matricula,
            remetenteNome: `${usuario.nome} ${usuario.sobrenome ?? ""}`.trim(),
            remetenteFuncao: usuario.funcao,
            conteudo
        })
    });

    mensagemInput.value = "";
    contadorMensagem.textContent = "0";
    mensagemInput.focus();
}

function exibirMensagem(mensagem) {
    if (primeiraMensagem) {
        mensagensEl.innerHTML = "";
        primeiraMensagem = false;
    }

    const item = document.createElement("div");

    if (mensagem.tipo === "ENTRADA" || mensagem.tipo === "SAIDA") {
        item.className = "mensagem mensagem-sistema";
        item.textContent = mensagem.conteudo;
    } else {
        const mensagemPropria =
            String(mensagem.remetenteMatricula) === String(usuario.matricula);

        item.className = mensagemPropria
            ? "mensagem mensagem-propria"
            : "mensagem";

        const remetente = document.createElement("div");
        remetente.className = "mensagem-remetente";
        remetente.textContent =
            `${mensagem.remetenteNome} — ${mensagem.remetenteFuncao}`;

        const conteudo = document.createElement("div");
        conteudo.textContent = mensagem.conteudo;

        const horario = document.createElement("div");
        horario.className = "mensagem-horario";
        horario.textContent = formatarHorario(mensagem.enviadaEm);

        item.appendChild(remetente);
        item.appendChild(conteudo);
        item.appendChild(horario);
    }

    mensagensEl.appendChild(item);
    mensagensEl.scrollTop = mensagensEl.scrollHeight;
}

function formatarHorario(data) {
    if (!data) {
        return "";
    }

    return new Date(data).toLocaleTimeString("pt-BR", {
        hour: "2-digit",
        minute: "2-digit"
    });
}

chatForm.addEventListener("submit", event => {
    event.preventDefault();
    enviarMensagem();
});

mensagemInput.addEventListener("input", function () {
    contadorMensagem.textContent = this.value.length;
});

voltarBtn.addEventListener("click", () => {
    const funcao = usuario.funcao?.toUpperCase();

    if (stompClient) {
        stompClient.deactivate();
    }

    if (funcao === "MECANICO") {
        window.location.href = "dashboard_mecanico.html";
    } else {
        window.location.href = "dashboard_motorista.html";
    }
});

window.addEventListener("beforeunload", () => {
    if (stompClient) {
        stompClient.deactivate();
    }
});

conectarChat();