const CHECKLIST_API = "http://localhost:8080/api/checklist";
const VEICULO_API   = "http://localhost:8080/api/veiculo";

const usuario    = JSON.parse(localStorage.getItem("usuario"));
const authHeader = localStorage.getItem("authHeader");

if (!usuario || !authHeader || usuario.funcao?.toUpperCase() !== "MOTORISTA") {
    alert("Sessão inválida ou acesso negado!");
    localStorage.clear();
    window.location.href = "index.html";
}

// LOGOUT
document.getElementById("logoutBtn").addEventListener("click", () => {
    localStorage.clear();
    window.location.href = "index.html";
});

// LISTAR VEÍCULOS NO SELECT
async function listarVeiculos() {
    const select = document.getElementById("veiculoSelect");
    select.innerHTML = "<option value=''>Carregando...</option>";

    try {
        const res = await fetch(`${VEICULO_API}/listar`, {
            headers: { "Authorization": authHeader }
        });

        if (res.status === 401 || res.status === 403) {
            alert("Sessão expirada!");
            localStorage.clear();
            window.location.href = "index.html";
            return;
        }

        if (!res.ok) {
            select.innerHTML = "<option value=''>Erro ao carregar veículos</option>";
            return;
        }

        const veiculos = await res.json();

        if (!veiculos || veiculos.length === 0) {
            select.innerHTML = "<option value=''>Nenhum veículo cadastrado</option>";
            return;
        }

        select.innerHTML = "<option value=''>-- Selecione o veículo --</option>";
        veiculos.forEach(v => {
            const opt = document.createElement("option");
            opt.value = v.numero;
            opt.textContent = `Nº ${v.numero} — ${v.placa} (${v.marca})`;
            select.appendChild(opt);
        });

    } catch (err) {
        select.innerHTML = "<option value=''>Falha na conexão</option>";
        console.error("Erro ao buscar veículos:", err);
    }
}

// REGISTRAR CHECKLIST
async function registrarChecklist() {

    const numeroVeiculo = document.getElementById("veiculoSelect").value;
    if (!numeroVeiculo) { alert("Selecione um veículo!"); 
        return; 
    }

    const kmValor = parseInt(document.getElementById("km").value);
        if (isNaN(kmValor) || kmValor < 0) { alert("Informe um KM válido!"); 
        return; 
    }

    // Validação das observações
    const obsEl    = document.getElementById("observacoes");
    const obsErro  = document.getElementById("observacoesErro");
    const obsValor = obsEl.value;
    const obsLimpo = obsValor.trim();

    // Verifica se todos os itens estão marcados
    const todosOk = ["faroisDianteiros","setasDianteiras","faroisTraseiros",
                    "setasTraseiras","luzesFreio","nivelOleo","nivelAgua"]
                    .every(id => document.getElementById(id).checked);

    // Se algum item estiver desmarcado, observação é obrigatória
    if (!todosOk) {

        if (obsLimpo.length === 0) {
            obsEl.classList.add("is-invalid");
            obsErro.style.display = "block";
            obsErro.textContent   = "Observação obrigatória quando algum item não foi verificado.";
            obsEl.focus();
            return;
        }

        if (!/[a-zA-ZÀ-ÿ]/.test(obsLimpo)) {
            obsEl.classList.add("is-invalid");
            obsErro.style.display = "block";
            obsErro.textContent   = "A observação deve conter ao menos uma letra, não apenas números.";
            obsEl.focus();
            return;
        }
    }

    // Se todos ok mas digitou algo, ainda valida o conteúdo digitado
    if (todosOk && obsLimpo.length > 0 && !/[a-zA-ZÀ-ÿ]/.test(obsLimpo)) {
        obsEl.classList.add("is-invalid");
        obsErro.style.display = "block";
        obsErro.textContent   = "A observação deve conter ao menos uma letra, não apenas números.";
        obsEl.focus();
        return;
    }

    if (obsLimpo.length > 400) {
        obsEl.classList.add("is-invalid");
        obsErro.style.display = "block";
        obsErro.textContent   = "A observação não pode ultrapassar 400 caracteres.";
        obsEl.focus();
        return;
    }

    // Sem erro — limpa feedback
    obsEl.classList.remove("is-invalid");
    obsErro.style.display = "none";

    const body = {
        tipo:             document.getElementById("tipo").value,
        km:               kmValor,
        faroisDianteiros: document.getElementById("faroisDianteiros").checked,
        setasDianteiras:  document.getElementById("setasDianteiras").checked,
        faroisTraseiros:  document.getElementById("faroisTraseiros").checked,
        setasTraseiras:   document.getElementById("setasTraseiras").checked,
        luzesFreio:       document.getElementById("luzesFreio").checked,
        nivelOleo:        document.getElementById("nivelOleo").checked,
        nivelAgua:        document.getElementById("nivelAgua").checked,
        observacoes:      obsLimpo
    };

    try {
        const res = await fetch(
            `${CHECKLIST_API}/cadastrar/${usuario.matricula}/${numeroVeiculo}`,
            {
                method: "POST",
                headers: { "Content-Type": "application/json", "Authorization": authHeader },
                body: JSON.stringify(body)
            }
        );

        if (res.status === 401 || res.status === 403) {
            alert("Sessão expirada!");
            localStorage.clear();
            window.location.href = "index.html";
            return;
        }

        if (res.ok) {
            alert("Checklist registrado com sucesso!");
            document.getElementById("km").value = "";
            document.getElementById("observacoes").value = "";
            document.getElementById("contadorObservacoes").textContent = "0";
            document.getElementById("veiculoSelect").value = "";
            document.getElementById("tipo").value = "ENTRADA";
            ["faroisDianteiros","setasDianteiras","faroisTraseiros",
             "setasTraseiras","luzesFreio","nivelOleo","nivelAgua"]
            .forEach(id => { document.getElementById(id).checked = false; });
            listarChecklists();
        } else {
            const erro = await res.text();
            alert("Erro ao registrar checklist:\n" + erro);
        }

    } catch (err) {
        alert("Erro ao conectar ao servidor!");
        console.error(err);
    }
}

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

        if (!res.ok) {
            tbody.innerHTML = `<tr><td colspan="7" class="text-center text-danger">Erro ao carregar.</td></tr>`;
            return;
        }

        const checklists = await res.json();

        if (!checklists || checklists.length === 0) {
            tbody.innerHTML = `<tr><td colspan="7" class="text-center text-muted py-3">Nenhum checklist registrado ainda.</td></tr>`;
            return;
        }

        tbody.innerHTML = "";
        checklists.forEach(c => {
            const dataFormatada = c.criadoEm
                ? new Date(c.criadoEm).toLocaleString("pt-BR") : "-";

            const badge = c.tipo === "ENTRADA"
                ? `<span class="badge bg-success">ENTRADA</span>`
                : `<span class="badge bg-warning text-dark">SAÍDA</span>`;

            const nomeMotorista = c.motorista
                ? `${c.motorista.nome} ${c.motorista.sobrenome ?? ""}`.trim() : "-";

            const observacoes = c.observacoes?.trim()
                ? c.observacoes
                : `<span class="text-muted fst-italic">Sem observações</span>`;

            // Coluna Solução
            let solucaoCell;
            if (c.solucaoMecanico) {
                const resolvidoEm = c.resolvidoEm
                    ? new Date(c.resolvidoEm).toLocaleString("pt-BR") : "";
                const nomeMec = c.mecanico
                    ? `${c.mecanico.nome} ${c.mecanico.sobrenome ?? ""}`.trim() : "";
                solucaoCell = `
                    <span class="badge bg-success mb-1">✅ Resolvido</span><br>
                    <small class="text-muted">${c.solucaoMecanico}</small><br>
                    <small class="text-muted">${nomeMec} — ${resolvidoEm}</small>`;
            } else {
                solucaoCell = `<span class="text-muted fst-italic small">Aguardando mecânico</span>`;
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
                </tr>
            `;
        });

    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="7" class="text-center text-danger">Falha na conexão.</td></tr>`;
        console.error(err);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    listarVeiculos();
    listarChecklists();
});