
const CHECKLIST_API = "http://localhost:8080/api/checklist";

const usuario = JSON.parse(localStorage.getItem("usuario"));
const authHeader = localStorage.getItem("authHeader");

if (!usuario || !authHeader || usuario.funcao?.toUpperCase() !== "MECANICO") {
    salvarMensagemTemporaria("Acesso negado.", "warning");
    localStorage.clear();
    window.location.href = "index.html";
}

document.getElementById("logoutBtn").addEventListener("click", () => {
    localStorage.clear();
    window.location.href = "index.html";
});

async function listarChecklists() {
    const tbody = document.getElementById("checklistTable");
    tbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted">Carregando...</td></tr>`;

    try {
        const res = await fetch(`${CHECKLIST_API}/listar`, {
            headers: { "Authorization": authHeader }
        });

        if (res.status === 401 || res.status === 403) {
            salvarMensagemTemporaria("Sua sessão expirou. Entre novamente.", "warning");
            localStorage.clear();
            window.location.href = "index.html";
            return;
        }

        if (!res.ok) {
            mostrarMensagemGeral("Não foi possível carregar os checklists.", "error");
            tbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted">Não foi possível carregar os dados.</td></tr>`;
            return;
        }

        const checklists = await res.json();

        const total = checklists.length;
        const resolvidos = checklists.filter(c => c.solucaoMecanico).length;
        const pendentes = checklists.filter(c => {
            const itensOk = [
                c.faroisDianteiros, c.setasDianteiras, c.faroisTraseiros,
                c.setasTraseiras, c.luzesFreio, c.nivelOleo, c.nivelAgua
            ].filter(Boolean).length;
            return itensOk < 7;
        }).length;

        document.getElementById("totalChecklists").textContent = total;
        document.getElementById("checklistsResolvidos").textContent = resolvidos;
        document.getElementById("checklistsPendentes").textContent = pendentes;

        if (checklists.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted py-3">Nenhum checklist registrado.</td></tr>`;
            return;
        }

        tbody.innerHTML = "";

        checklists.forEach(c => {
            const badgeTipo = c.tipo === "ENTRADA"
                ? `<span class="badge bg-success">ENTRADA</span>`
                : `<span class="badge bg-warning text-dark">SAÍDA</span>`;

            const temProblema =
                !c.faroisDianteiros || !c.setasDianteiras || !c.faroisTraseiros ||
                !c.setasTraseiras || !c.luzesFreio || !c.nivelOleo || !c.nivelAgua;

            const itensOk = [
                c.faroisDianteiros, c.setasDianteiras, c.faroisTraseiros,
                c.setasTraseiras, c.luzesFreio, c.nivelOleo, c.nivelAgua
            ].filter(Boolean).length;

            const badgeItens = itensOk === 7
                ? `<span class="badge bg-success">${itensOk}/7 OK</span>`
                : `<span class="badge bg-danger">${itensOk}/7 OK</span>`;

            let solucaoCell;

            if (c.solucaoMecanico) {
                const resolvidoEm = c.resolvidoEm
                    ? new Date(c.resolvidoEm).toLocaleString("pt-BR")
                    : "";

                solucaoCell = `
                    <div class="text-success fw-semibold small">Resolvido</div>
                    <div class="text-muted small">${c.solucaoMecanico}</div>
                    <div class="text-muted" style="font-size:.75rem">${resolvidoEm}</div>`;
            } else {
                solucaoCell = `<span class="text-muted fst-italic small">Sem solução</span>`;
            }

            let btnSolucao = "";

            if (temProblema) {
                btnSolucao = c.solucaoMecanico
                    ? `<button class="btn btn-outline-success btn-sm"
                        onclick="abrirModalSolucao(${c.id}, \`${(c.solucaoMecanico || "").replace(/`/g, "'")}\`)">
                        Editar
                       </button>`
                    : `<button class="btn btn-warning btn-sm"
                        onclick="abrirModalSolucao(${c.id}, '')">
                        Confirmar solução
                       </button>`;
            }

            tbody.innerHTML += `
                <tr class="${c.solucaoMecanico ? "" : (temProblema ? "table-danger" : "")}">
                    <td>${badgeTipo}</td>
                    <td>Nº ${c.veiculo?.numero ?? "?"} — ${c.veiculo?.placa ?? ""}</td>
                    <td>${c.motorista?.nome ?? ""} ${c.motorista?.sobrenome ?? ""}</td>
                    <td>${c.km} km</td>
                    <td>${badgeItens}<div class="text-muted small mt-1">${c.observacoes || ""}</div></td>
                    <td>${solucaoCell}</td>
                    <td>
                        ${btnSolucao}
                        <button class="btn btn-secondary btn-sm ${temProblema ? "mt-1" : ""}"
                            onclick="irParaChat(${c.id}, '${c.motorista?.matricula ?? ""}')">
                            Chat
                        </button>
                    </td>
                </tr>`;
        });

    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted">Falha na conexão.</td></tr>`;
        mostrarMensagemGeral("Não foi possível conectar ao servidor.", "error");
        console.error(err);
    }
}

function abrirModalSolucao(checklistId, solucaoAtual) {
    document.getElementById("checklistIdSolucao").value = checklistId;
    document.getElementById("solucaoMecanico").value = solucaoAtual;
    document.getElementById("modalSolucaoLabel").textContent =
        solucaoAtual ? "Editar solução" : "Confirmar solução";

    limparErroCampo("solucaoMecanico");

    const textarea = document.getElementById("solucaoMecanico");
    const contador = document.getElementById("contadorSolucao");

    contador.textContent = solucaoAtual.length;
    textarea.oninput = () => {
        contador.textContent = textarea.value.length;
        if (textarea.value.trim()) limparErroCampo("solucaoMecanico");
    };

    new bootstrap.Modal(document.getElementById("modalSolucao")).show();
}

document.getElementById("formSolucao").addEventListener("submit", async function (e) {
    e.preventDefault();

    limparErroCampo("solucaoMecanico");
    limparMensagemGeral();

    const id = document.getElementById("checklistIdSolucao").value;
    const solucao = document.getElementById("solucaoMecanico").value.trim();

    if (!solucao) {
        mostrarErroCampo("solucaoMecanico", "Descreva a solução.");
        return;
    }

    if (!/[a-zA-ZÀ-ÿ]/.test(solucao)) {
        mostrarErroCampo("solucaoMecanico", "A solução deve conter ao menos uma letra.");
        return;
    }

    try {
        const res = await fetch(
            `${CHECKLIST_API}/solucao/${id}/${usuario.matricula}`,
            {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": authHeader
                },
                body: JSON.stringify({ solucao })
            }
        );

        if (res.status === 401 || res.status === 403) {
            salvarMensagemTemporaria("Sua sessão expirou. Entre novamente.", "warning");
            localStorage.clear();
            window.location.href = "index.html";
            return;
        }

        if (res.ok) {
            bootstrap.Modal.getInstance(document.getElementById("modalSolucao")).hide();
            mostrarMensagemGeral("Solução registrada com sucesso.", "success", 5000);
            listarChecklists();
        } else {
            const erro = await res.text();
            mostrarMensagemGeral(`Não foi possível registrar a solução. ${erro}`, "error");
        }

    } catch (err) {
        mostrarMensagemGeral("Não foi possível conectar ao servidor.", "error");
        console.error(err);
    }
});

function irParaChat(checklistId, matriculaMotorista) {
    if (!checklistId || !matriculaMotorista) {
        mostrarMensagemGeral("Não foi possível identificar o checklist ou o motorista.", "error");
        return;
    }

    const params = new URLSearchParams({
        checklistId: String(checklistId),
        motorista: matriculaMotorista
    });

    window.location.href = `chat.html?${params.toString()}`;
}

document.addEventListener("DOMContentLoaded", listarChecklists);
