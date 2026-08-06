function obterMensagemCampo(campoId) {
    return document.getElementById(`${campoId}Erro`);
}

function mostrarErroCampo(campoId, mensagem, focar = true) {
    const campo = document.getElementById(campoId);
    const erro = obterMensagemCampo(campoId);

    if (!campo || !erro) {
        console.warn(`Campo ou mensagem não encontrado: ${campoId}`);
        return;
    }

    campo.classList.add("is-invalid");
    erro.textContent = mensagem;
    erro.style.display = "block";

    if (focar) {
        campo.focus();
    }
}

function limparErroCampo(campoId) {
    const campo = document.getElementById(campoId);
    const erro = obterMensagemCampo(campoId);

    campo?.classList.remove("is-invalid");

    if (erro) {
        erro.textContent = "";
        erro.style.display = "none";
    }
}

function mostrarMensagemGeral(mensagem, tipo = "danger", tempo = 0) {
    const elemento = document.getElementById("mensagemGeral");

    if (!elemento) {
        console.warn("Elemento #mensagemGeral não encontrado.");
        return;
    }

    elemento.className = `alert alert-${tipo}`;
    elemento.textContent = mensagem;
    elemento.style.display = "block";

    if (tempo > 0) {
        window.setTimeout(() => {
            limparMensagemGeral();
        }, tempo);
    }
}

function limparMensagemGeral() {
    const elemento = document.getElementById("mensagemGeral");

    if (!elemento) {
        return;
    }

    elemento.textContent = "";
    elemento.style.display = "none";
}

function limparTodosErros() {
    limparMensagemGeral();

    document.querySelectorAll(".is-invalid").forEach(campo => {
        campo.classList.remove("is-invalid");
    });

    document.querySelectorAll("[id$='Erro']").forEach(erro => {
        erro.textContent = "";
        erro.style.display = "none";
    });
}

function salvarMensagemTemporaria(mensagem, tipo = "warning") {
    sessionStorage.setItem(
        "mensagemTemporaria",
        JSON.stringify({ mensagem, tipo })
    );
}

function exibirMensagemTemporaria() {
    const dados = sessionStorage.getItem("mensagemTemporaria");

    if (!dados) {
        return;
    }

    sessionStorage.removeItem("mensagemTemporaria");

    try {
        const objeto = JSON.parse(dados);
        mostrarMensagemGeral(objeto.mensagem, objeto.tipo);
    } catch {
        mostrarMensagemGeral(dados, "warning");
    }
}

document.addEventListener("DOMContentLoaded", exibirMensagemTemporaria);