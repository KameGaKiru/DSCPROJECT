
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
    salvarMensagemTemporaria("Sessão inválida ou acesso negado.", "warning");
    localStorage.clear();
    window.location.href = "index.html";
}

function definirStatus(texto, tipo = "normal") {
    statusConexao.textContent = texto;
    statusConexao.dataset.status = tipo;
}

function conectarChat() {
    stompClient = new StompJs.Client({
        webSocketFactory: () => new SockJS("/ws-chat"),
        reconnectDelay: 5000,

        onConnect: () => {
            definirStatus("Conectado", "success");
            limparMensagemGeral();
            limparErroCampo("mensagemInput");
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
            definirStatus("Erro na conexão", "error");
            enviarBtn.disabled = true;
            mostrarMensagemGeral("Não foi possível conectar ao chat.", "error");
        },

        onWebSocketClose: () => {
            definirStatus("Desconectado", "warning");
            enviarBtn.disabled = true;
            mostrarMensagemGeral("Conexão encerrada. Tentando reconectar...", "warning");
        }
    });

    stompClient.activate();
}

function enviarMensagem() {
    limparErroCampo("mensagemInput");

    const conteudo = mensagemInput.value.trim();

    if (!conteudo) {
        mostrarErroCampo("mensagemInput", "Digite uma mensagem antes de enviar.");
        return;
    }

    if (conteudo.length > 500) {
        mostrarErroCampo("mensagemInput", "A mensagem deve ter no máximo 500 caracteres.");
        return;
    }

    if (!stompClient || !stompClient.connected) {
        mostrarErroCampo("mensagemInput", "O chat ainda não está conectado.");
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
    if (!data) return "";

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
    if (this.value.trim()) limparErroCampo("mensagemInput");
});

voltarBtn.addEventListener("click", () => {
    const funcao = usuario.funcao?.toUpperCase();

    if (stompClient) stompClient.deactivate();

    window.location.href =
        funcao === "MECANICO"
            ? "dashboard_mecanico.html"
            : "dashboard_motorista.html";
});

window.addEventListener("beforeunload", () => {
    if (stompClient) stompClient.deactivate();
});

conectarChat();
