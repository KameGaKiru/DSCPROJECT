//   GET  /api/checklist/listar
//   PUT  /api/checklist/solucao/{id}/{matriculaMecanico}

const CHECKLIST_API = "http://localhost:8080/api/checklist";

// SESSÃO
const usuario    = JSON.parse(localStorage.getItem("usuario"));
const authHeader = localStorage.getItem("authHeader");

if (!usuario || !authHeader || usuario.funcao?.toUpperCase() !== "MECANICO") {
    alert("Sessão inválida ou acesso negado!");
    localStorage.clear();
    window.location.href = "index.html";
}

// LOGOUT
document.getElementById("logoutBtn").addEventListener("click", () => {
    localStorage.clear();
    window.location.href = "index.html";
});

// LISTAR CHECKLISTS
async function listarChecklists() {
    const tbody = document.getElementById("checklistTable");
    tbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted">Carregando...</td></tr>`;

    try {
        const res = await fetch(`${CHECKLIST_API}/listar`, {
            headers: { "Authorization": authHeader }
        });

        if (res.status === 401 || res.status === 403) {
            alert("Sessão expirada!");
            localStorage.clear();
            window.location.href = "index.html";
            return;
        }

        const checklists = await res.json();

        // Contadores para os cards
        const total     = checklists.length;
        const resolvidos = checklists.filter(c => c.solucaoMecanico).length;
        const pendentes  = total - resolvidos;

        document.getElementById("totalChecklists").textContent  = total;
        document.getElementById("checklistsResolvidos").textContent = resolvidos;
        document.getElementById("checklistsPendentes").textContent  = pendentes;

        if (checklists.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted py-3">Nenhum checklist registrado ainda.</td></tr>`;
            return;
        }

        tbody.innerHTML = "";
        checklists.forEach(c => {
            const dataFormatada = c.criadoEm
                ? new Date(c.criadoEm).toLocaleString("pt-BR")
                : "-";

            const badgeTipo = c.tipo === "ENTRADA"
                ? `<span class="badge bg-success">ENTRADA</span>`
                : `<span class="badge bg-warning text-dark">SAÍDA</span>`;

            const temProblema = !c.faroisDianteiros || !c.setasDianteiras || !c.faroisTraseiros
                             || !c.setasTraseiras   || !c.luzesFreio      || !c.nivelOleo
                             || !c.nivelAgua;

            const itensOk = [
                c.faroisDianteiros, c.setasDianteiras, c.faroisTraseiros,
                c.setasTraseiras,   c.luzesFreio,      c.nivelOleo, c.nivelAgua
            ].filter(Boolean).length;

            const badgeItens = itensOk === 7
                ? `<span class="badge bg-success">${itensOk}/7 OK</span>`
                : `<span class="badge bg-danger">${itensOk}/7 OK</span>`;

            // Solução
            let solucaoCell;
            if (c.solucaoMecanico) {
                const resolvidoEm = c.resolvidoEm
                    ? new Date(c.resolvidoEm).toLocaleString("pt-BR")
                    : "";
                solucaoCell = `
                    <div class="text-success fw-semibold small">✅ Resolvido</div>
                    <div class="text-muted small">${c.solucaoMecanico}</div>
                    <div class="text-muted" style="font-size:0.75rem">${resolvidoEm}</div>`;
            } else {
                solucaoCell = `<span class="text-muted fst-italic small">Sem solução</span>`;
            }

            // Botão confirmar solução 
            const btnSolucao = c.solucaoMecanico
                ? `<button class="btn btn-outline-success btn-sm"
                        onclick="abrirModalSolucao(${c.id}, \`${(c.solucaoMecanico || "").replace(/`/g, "'")}\`)">
                        ✏️ Editar
                   </button>`
                : `<button class="btn btn-warning btn-sm"
                        onclick="abrirModalSolucao(${c.id}, '')">
                        🔧 Confirmar Solução
                   </button>`;

            tbody.innerHTML += `
                <tr class="${c.solucaoMecanico ? '' : (temProblema ? 'table-danger' : '')}">
                    <td>${badgeTipo}</td>
                    <td>Nº ${c.veiculo?.numero ?? "?"} — ${c.veiculo?.placa ?? ""}</td>
                    <td>${c.motorista?.nome ?? ""} ${c.motorista?.sobrenome ?? ""}</td>
                    <td>${c.km} km</td>
                    <td>${badgeItens}<div class="text-muted small mt-1">${c.observacoes || ""}</div></td>
                    <td>${solucaoCell}</td>
                    <td>
                        ${btnSolucao}
                        <button class="btn btn-secondary btn-sm mt-1"
                            onclick="irParaChat()">
                            💬 Chat
                        </button>
                    </td>
                </tr>
            `;
        });

    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="7" class="text-center text-danger">Falha na conexão.</td></tr>`;
        console.error("Erro ao listar checklists:", err);
    }
}

// MODAL DE SOLUÇÃO
function abrirModalSolucao(checklistId, solucaoAtual) {
    document.getElementById("checklistIdSolucao").value    = checklistId;
    document.getElementById("solucaoMecanico").value       = solucaoAtual;
    document.getElementById("modalSolucaoLabel").textContent =
        solucaoAtual ? "Editar Solução" : "Confirmar Solução";

    new bootstrap.Modal(document.getElementById("modalSolucao")).show();
}

// SALVAR SOLUÇÃO
// PUT /api/checklist/solucao/{id}/{matriculaMecanico}
document.getElementById("formSolucao").addEventListener("submit", async function(e) {
    e.preventDefault();

    const id      = document.getElementById("checklistIdSolucao").value;
    const solucao = document.getElementById("solucaoMecanico").value.trim();

    if (!solucao) {
        alert("Descreva a solução antes de confirmar!");
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
            alert("Sessão expirada!");
            localStorage.clear();
            window.location.href = "index.html";
            return;
        }

        if (res.ok) {
            bootstrap.Modal.getInstance(document.getElementById("modalSolucao")).hide();
            alert("Solução registrada com sucesso!");
            listarChecklists();
        } else {
            const erro = await res.text();
            alert("Erro ao registrar solução:\n" + erro);
        }

    } catch (err) {
        alert("Erro ao conectar ao servidor!");
        console.error(err);
    }
});


// CHAT (futuro)
function irParaChat() {
    alert("Chat em desenvolvimento — disponível em breve! 🚧");
    // window.location.href = "chat.html"; // descomente quando implementar
}

document.addEventListener("DOMContentLoaded", () => {
    listarChecklists();
});