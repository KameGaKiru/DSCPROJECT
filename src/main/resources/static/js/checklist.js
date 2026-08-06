
const CHECKLIST_API = "http://localhost:8080/api/checklist";
const VEICULO_API = "http://localhost:8080/api/veiculo";

const usuario = JSON.parse(localStorage.getItem("usuario"));
const authHeader = localStorage.getItem("authHeader");

if (!usuario || !authHeader || usuario.funcao?.toUpperCase() !== "MOTORISTA") {
    salvarMensagemTemporaria("Acesso negado.", "warning");
    localStorage.clear();
    window.location.href = "index.html";
}

document.getElementById("logoutBtn").addEventListener("click", () => {
    localStorage.clear();
    window.location.href = "index.html";
});

let checklistsCarregados = [];

function obterUltimoChecklist(numeroVeiculo) {
    const doVeiculo = checklistsCarregados
        .filter(c => String(c.veiculo?.numero) === String(numeroVeiculo))
        .sort((a, b) => new Date(b.criadoEm || 0) - new Date(a.criadoEm || 0));

    return doVeiculo[0] || null;
}

function obterUltimoKm(numeroVeiculo) {
    return obterUltimoChecklist(numeroVeiculo)?.km ?? null;
}

async function listarVeiculos() {
    const select = document.getElementById("veiculoSelect");
    select.innerHTML = "<option value=''>Carregando...</option>";

    try {
        const res = await fetch(`${VEICULO_API}/listar`, {
            headers: { "Authorization": authHeader }
        });

        if (res.status === 401 || res.status === 403) {
            salvarMensagemTemporaria("Sua sessão expirou. Entre novamente.", "warning");
            localStorage.clear();
            window.location.href = "index.html";
            return;
        }

        if (!res.ok) {
            select.innerHTML = "<option value=''>Erro ao carregar veículos</option>";
            mostrarErroCampo("veiculoSelect", "Não foi possível carregar os veículos.", false);
            return;
        }

        const veiculos = await res.json();

        if (!veiculos?.length) {
            select.innerHTML = "<option value=''>Nenhum veículo cadastrado</option>";
            return;
        }

        select.innerHTML = "<option value=''>Selecione o veículo</option>";

        veiculos.forEach(v => {
            const opt = document.createElement("option");
            opt.value = v.numero;
            opt.textContent = `Nº ${v.numero} — ${v.placa} (${v.marca})`;
            select.appendChild(opt);
        });

    } catch (err) {
        select.innerHTML = "<option value=''>Falha na conexão</option>";
        mostrarMensagemGeral("Não foi possível conectar ao servidor.", "error");
        console.error(err);
    }
}

async function registrarChecklist() {
    limparMensagemGeral();
    ["veiculoSelect", "tipo", "km", "observacoes"]
        .forEach(id => limparErroCampo(id));

    const numeroVeiculo = document.getElementById("veiculoSelect").value;
    const tipoAtual = document.getElementById("tipo").value;

    if (!numeroVeiculo) {
        mostrarErroCampo("veiculoSelect", "Selecione um veículo.");
        return;
    }

    const ultimoChecklist = obterUltimoChecklist(numeroVeiculo);

    if (ultimoChecklist) {
        const ultimoTipo = ultimoChecklist.tipo?.toUpperCase();

        if (ultimoTipo === tipoAtual) {
            const proximoTipo =
                ultimoTipo === "ENTRADA" ? "SAÍDA" : "ENTRADA";

            mostrarErroCampo(
                "tipo",
                `O último checklist foi ${ultimoTipo}. O próximo deve ser ${proximoTipo}.`
            );
            return;
        }
    }

    const kmInput = document.getElementById("km").value
        .replace(",", ".")
        .trim();

    const kmValor = Number.parseFloat(kmInput);

    if (!Number.isFinite(kmValor) || kmValor <= 0) {
        mostrarErroCampo("km", "Informe um KM válido e maior que zero.");
        return;
    }

    const ultimoKm = obterUltimoKm(numeroVeiculo);

    if (ultimoKm !== null && kmValor <= ultimoKm) {
        mostrarErroCampo(
            "km",
            `O KM deve ser superior ao último registro (${ultimoKm} km).`
        );
        return;
    }

    const obsEl = document.getElementById("observacoes");
    const obsValor = obsEl.value;
    const obsLimpo = obsValor.trim();

    const todosOk = [
        "faroisDianteiros", "setasDianteiras", "faroisTraseiros",
        "setasTraseiras", "luzesFreio", "nivelOleo", "nivelAgua"
    ].every(id => document.getElementById(id).checked);

    if (!todosOk && !obsLimpo) {
        mostrarErroCampo(
            "observacoes",
            "A observação é obrigatória quando algum item não foi verificado."
        );
        return;
    }

    if (obsLimpo && !/[a-zA-ZÀ-ÿ]/.test(obsLimpo)) {
        mostrarErroCampo(
            "observacoes",
            "A observação deve conter ao menos uma letra."
        );
        return;
    }

    if (obsLimpo.length > 400) {
        mostrarErroCampo(
            "observacoes",
            "A observação deve ter no máximo 400 caracteres."
        );
        return;
    }

    const body = {
        tipo: tipoAtual,
        km: kmValor,
        faroisDianteiros: document.getElementById("faroisDianteiros").checked,
        setasDianteiras: document.getElementById("setasDianteiras").checked,
        faroisTraseiros: document.getElementById("faroisTraseiros").checked,
        setasTraseiras: document.getElementById("setasTraseiras").checked,
        luzesFreio: document.getElementById("luzesFreio").checked,
        nivelOleo: document.getElementById("nivelOleo").checked,
        nivelAgua: document.getElementById("nivelAgua").checked,
        observacoes: obsLimpo
    };

    try {
        const res = await fetch(
            `${CHECKLIST_API}/cadastrar/${usuario.matricula}/${numeroVeiculo}`,
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": authHeader
                },
                body: JSON.stringify(body)
            }
        );

        if (res.status === 401 || res.status === 403) {
            salvarMensagemTemporaria("Sua sessão expirou. Entre novamente.", "warning");
            localStorage.clear();
            window.location.href = "index.html";
            return;
        }

        if (res.ok) {
            mostrarMensagemGeral("Checklist registrado com sucesso.", "success", 5000);

            document.getElementById("km").value = "";
            document.getElementById("observacoes").value = "";
            document.getElementById("contadorObservacoes").textContent = "0";
            document.getElementById("veiculoSelect").value = "";
            document.getElementById("tipo").value = "ENTRADA";

            [
                "faroisDianteiros", "setasDianteiras", "faroisTraseiros",
                "setasTraseiras", "luzesFreio", "nivelOleo", "nivelAgua"
            ].forEach(id => {
                document.getElementById(id).checked = false;
            });

            listarChecklists();
        } else {
            const erro = await res.text();
            mostrarMensagemGeral(`Não foi possível registrar o checklist. ${erro}`, "error");
        }

    } catch (err) {
        mostrarMensagemGeral("Não foi possível conectar ao servidor.", "error");
        console.error(err);
    }
}

async function listarChecklists() {
    const tbody = document.getElementById("checklistTable");
    tbody.innerHTML =
        `<tr><td colspan="7" class="text-center text-muted">Carregando...</td></tr>`;

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
            tbody.innerHTML =
                `<tr><td colspan="7" class="text-center text-muted">Erro ao carregar.</td></tr>`;
            mostrarMensagemGeral("Não foi possível carregar os checklists.", "error");
            return;
        }

        const checklists = await res.json();
        checklistsCarregados = checklists;

        if (!checklists?.length) {
            tbody.innerHTML =
                `<tr><td colspan="7" class="text-center text-muted py-3">Nenhum checklist registrado.</td></tr>`;
            return;
        }

        tbody.innerHTML = "";

        checklists.forEach(c => {
            const dataFormatada = c.criadoEm
                ? new Date(c.criadoEm).toLocaleString("pt-BR")
                : "-";

            const badge = c.tipo === "ENTRADA"
                ? `<span class="badge bg-success">ENTRADA</span>`
                : `<span class="badge bg-warning text-dark">SAÍDA</span>`;

            const nomeMotorista = c.motorista
                ? `${c.motorista.nome} ${c.motorista.sobrenome ?? ""}`.trim()
                : "-";

            const observacoes = c.observacoes?.trim()
                ? c.observacoes
                : `<span class="text-muted fst-italic">Sem observações</span>`;

            let solucaoCell;

            if (c.solucaoMecanico) {
                const resolvidoEm = c.resolvidoEm
                    ? new Date(c.resolvidoEm).toLocaleString("pt-BR")
                    : "";

                const nomeMecanico = c.mecanico
                    ? `${c.mecanico.nome} ${c.mecanico.sobrenome ?? ""}`.trim()
                    : "";

                solucaoCell = `
                    <span class="badge bg-success mb-1">Resolvido</span><br>
                    <small class="text-muted">${c.solucaoMecanico}</small><br>
                    <small class="text-muted">${nomeMecanico} — ${resolvidoEm}</small>`;
            } else {
                solucaoCell =
                    `<span class="text-muted fst-italic small">Aguardando mecânico</span>`;
            }

            tbody.innerHTML += `
                <tr>
                    <td>${badge}</td>
                    <td>Nº ${c.veiculo?.numero ?? "?"} — ${c.veiculo?.placa ?? ""}</td>
                    <td>${nomeMotorista}</td>
                    <td>${c.km} km</td>
                    <td>${observacoes}</td>
                    <td>${solucaoCell}</td>
                    <td>${dataFormatada}</td>
                </tr>`;
        });

    } catch (err) {
        tbody.innerHTML =
            `<tr><td colspan="7" class="text-center text-muted">Falha na conexão.</td></tr>`;
        mostrarMensagemGeral("Não foi possível conectar ao servidor.", "error");
        console.error(err);
    }
}

document.getElementById("veiculoSelect")?.addEventListener("change", () => {
    limparErroCampo("veiculoSelect");
});

document.getElementById("tipo")?.addEventListener("change", () => {
    limparErroCampo("tipo");
});

document.getElementById("km")?.addEventListener("input", () => {
    limparErroCampo("km");
});

document.getElementById("observacoes")?.addEventListener("input", () => {
    limparErroCampo("observacoes");
});

document.addEventListener("DOMContentLoaded", () => {
    listarVeiculos();
    listarChecklists();
});
