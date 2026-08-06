
const API = "http://localhost:8080/api/veiculo";
const CHECKLIST_API = "http://localhost:8080/api/checklist";
const USUARIO_API = "http://localhost:8080/api/usuario";

const usuario = JSON.parse(localStorage.getItem("usuario"));
const authHeader = localStorage.getItem("authHeader");

if (!usuario || !authHeader || usuario.funcao?.toUpperCase() !== "COORDENADOR") {
    salvarMensagemTemporaria("Acesso negado.", "warning");
    localStorage.clear();
    window.location.href = "index.html";
}

document.getElementById("logoutBtn").addEventListener("click", () => {
    localStorage.clear();
    window.location.href = "index.html";
});

document.getElementById("numero").addEventListener("input", function () {
    this.value = this.value.replace(/\D/g, "").slice(0, 10);
    limparErroCampo("numero");
});

document.getElementById("placa").addEventListener("input", function () {
    let v = this.value.toUpperCase().replace(/[^A-Z0-9-]/g, "");
    if (v.length > 3 && v[3] !== "-") v = v.slice(0, 3) + "-" + v.slice(3);
    this.value = v.slice(0, 8);
    limparErroCampo("placa");
});

document.getElementById("editNumero").addEventListener("input", function () {
    this.value = this.value.replace(/\D/g, "").slice(0, 10);
    limparErroCampo("editNumero");
});

document.getElementById("editPlaca").addEventListener("input", function () {
    let v = this.value.toUpperCase().replace(/[^A-Z0-9-]/g, "");
    if (v.length > 3 && v[3] !== "-") v = v.slice(0, 3) + "-" + v.slice(3);
    this.value = v.slice(0, 8);
    limparErroCampo("editPlaca");
});

function validarVeiculo(numero, placa, marca, tipo, prefixo = "") {
    ["Numero", "Placa", "Marca", "Tipo"].forEach(sufixo => {
        const id = prefixo ? `${prefixo}${sufixo}` : sufixo.toLowerCase();
        limparErroCampo(id);
    });

    let valido = true;
    const idNumero = prefixo ? `${prefixo}Numero` : "numero";
    const idPlaca = prefixo ? `${prefixo}Placa` : "placa";
    const idMarca = prefixo ? `${prefixo}Marca` : "marca";
    const idTipo = prefixo ? `${prefixo}Tipo` : "tipo";

    if (!/^\d+$/.test(numero) || Number(numero) <= 0) {
        mostrarErroCampo(idNumero, "Informe um número inteiro e positivo.");
        valido = false;
    } else if (numero.length > 10) {
        mostrarErroCampo(idNumero, "O número deve ter no máximo 10 dígitos.");
        valido = false;
    }

    const placaAntiga = /^[A-Z]{3}-\d{4}$/;
    const placaMercosul = /^[A-Z]{3}-\d[A-Z]\d{2}$/;
    const placaNormalizada = placa.trim().toUpperCase();

    if (!placaAntiga.test(placaNormalizada) && !placaMercosul.test(placaNormalizada)) {
        mostrarErroCampo(idPlaca, "Use o formato AAA-1111 ou AAA-1A11.", !valido);
        valido = false;
    }

    if (!marca) {
        mostrarErroCampo(idMarca, "Selecione uma marca.", !valido);
        valido = false;
    }

    if (!tipo) {
        mostrarErroCampo(idTipo, "Selecione um tipo.", !valido);
        valido = false;
    }

    return valido;
}

async function carregarDashboard() {
    try {
        const [resV, resC, resU] = await Promise.all([
            fetch(`${API}/listar`, { headers: { "Authorization": authHeader } }),
            fetch(`${CHECKLIST_API}/listar`, { headers: { "Authorization": authHeader } }),
            fetch(`${USUARIO_API}/listar`, { headers: { "Authorization": authHeader } })
        ]);

        if ([resV, resC, resU].some(r => r.status === 401 || r.status === 403)) {
            salvarMensagemTemporaria("Sua sessão expirou. Entre novamente.", "warning");
            localStorage.clear();
            window.location.href = "index.html";
            return;
        }

        if (![resV, resC, resU].every(r => r.ok)) {
            mostrarMensagemGeral("Não foi possível carregar todos os dados do dashboard.", "error");
            return;
        }

        const veiculos = await resV.json();
        const checklists = await resC.json();
        const usuarios = await resU.json();

        const checkPorVeiculo = {};
        const checkPorMotorista = {};

        checklists.forEach(c => {
            const numero = c.veiculo?.numero;
            if (numero != null) checkPorVeiculo[numero] = (checkPorVeiculo[numero] || 0) + 1;

            const matricula = c.motorista?.matricula;
            if (matricula) checkPorMotorista[matricula] = (checkPorMotorista[matricula] || 0) + 1;
        });

        const motoristas = usuarios.filter(u => u.funcao?.toUpperCase() === "MOTORISTA");

        document.getElementById("totalVeiculos").textContent = veiculos.length;
        document.getElementById("totalChecklists").textContent = checklists.length;
        document.getElementById("totalMotoristas").textContent =
            motoristas.filter(m => checkPorMotorista[m.matricula] > 0).length;

        const tbody = document.getElementById("veiculosTable");
        tbody.innerHTML = "";

        veiculos.forEach(v => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${v.numero}</td>
                <td>${v.placa}</td>
                <td>${v.marca}</td>
                <td>${v.tipo}</td>
                <td>
                    <button class="btn btn-warning btn-sm btn-editar"
                        data-numero="${v.numero}"
                        data-placa="${v.placa}"
                        data-marca="${v.marca}"
                        data-tipo="${v.tipo}">
                        Editar
                    </button>
                    <button class="btn btn-danger btn-sm btn-deletar"
                        data-numero="${v.numero}">
                        Excluir
                    </button>
                </td>`;
            tbody.appendChild(tr);
        });

        const rankingBody = document.getElementById("rankingTable");
        const ranking = motoristas
            .map(m => ({ ...m, total: checkPorMotorista[m.matricula] || 0 }))
            .sort((a, b) => b.total - a.total);

        rankingBody.innerHTML = "";

        if (ranking.length === 0) {
            rankingBody.innerHTML = `
                <tr><td colspan="4" class="text-center text-muted">
                    Nenhum motorista encontrado.
                </td></tr>`;
        } else {
            const medalhas = ["🥇", "🥈", "🥉"];
            ranking.forEach((m, i) => {
                const tr = document.createElement("tr");
                tr.innerHTML = `
                    <td>${medalhas[i] || `${i + 1}º`}</td>
                    <td>${m.nome} ${m.sobrenome || ""}</td>
                    <td>${m.matricula}</td>
                    <td><span class="badge bg-success">${m.total}</span></td>`;
                rankingBody.appendChild(tr);
            });
        }

    } catch (err) {
        mostrarMensagemGeral("Não foi possível carregar o dashboard.", "error");
        console.error(err);
    }
}

document.getElementById("veiculosTable").addEventListener("click", function (e) {
    const btnEditar = e.target.closest(".btn-editar");
    const btnDeletar = e.target.closest(".btn-deletar");

    if (btnEditar) {
        const { numero, placa, marca, tipo } = btnEditar.dataset;
        abrirModal(numero, placa, marca, tipo);
    }

    if (btnDeletar) deletarVeiculo(btnDeletar.dataset.numero);
});

document.getElementById("cadVeiculoForm").addEventListener("submit", async function (e) {
    e.preventDefault();
    limparMensagemGeral();

    const numero = document.getElementById("numero").value.trim();
    const placa = document.getElementById("placa").value.trim().toUpperCase();
    const marca = document.getElementById("marca").value;
    const tipo = document.getElementById("tipo").value;

    if (!validarVeiculo(numero, placa, marca, tipo)) return;

    try {
        const res = await fetch(`${API}/cadastrar`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": authHeader
            },
            body: JSON.stringify({ numero: Number(numero), placa, marca, tipo })
        });

        if (res.ok) {
            mostrarMensagemGeral("Veículo cadastrado com sucesso.", "success", 5000);
            this.reset();
            carregarDashboard();
        } else {
            const erro = await res.text();
            if (/placa/i.test(erro)) {
                mostrarErroCampo("placa", erro);
            } else if (/n[uú]mero|numero/i.test(erro)) {
                mostrarErroCampo("numero", erro);
            } else {
                mostrarMensagemGeral(erro || "Não foi possível cadastrar o veículo.", "error");
            }
        }
    } catch (err) {
        mostrarMensagemGeral("Não foi possível conectar ao servidor.", "error");
        console.error(err);
    }
});

function abrirModal(numero, placa, marca, tipo) {
    document.getElementById("numeroOriginal").value = numero;
    document.getElementById("editNumero").value = numero;
    document.getElementById("editPlaca").value = placa;
    document.getElementById("editMarca").value = marca;
    document.getElementById("editTipo").value = tipo;

    ["editNumero", "editPlaca", "editMarca", "editTipo"]
        .forEach(id => limparErroCampo(id));

    new bootstrap.Modal(document.getElementById("editModal")).show();
}

document.getElementById("editVeiculoForm").addEventListener("submit", async function (e) {
    e.preventDefault();
    limparMensagemGeral();

    const numeroOriginal = document.getElementById("numeroOriginal").value;
    const numero = document.getElementById("editNumero").value.trim();
    const placa = document.getElementById("editPlaca").value.trim().toUpperCase();
    const marca = document.getElementById("editMarca").value;
    const tipo = document.getElementById("editTipo").value;

    if (!validarVeiculo(numero, placa, marca, tipo, "edit")) return;

    try {
        const res = await fetch(`${API}/atualizar/${numeroOriginal}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": authHeader
            },
            body: JSON.stringify({ numero: Number(numero), placa, marca, tipo })
        });

        if (res.ok) {
            bootstrap.Modal.getInstance(document.getElementById("editModal")).hide();
            mostrarMensagemGeral("Veículo atualizado com sucesso.", "success", 5000);
            carregarDashboard();
        } else {
            const erro = await res.text();
            if (/placa/i.test(erro)) {
                mostrarErroCampo("editPlaca", erro);
            } else if (/n[uú]mero|numero/i.test(erro)) {
                mostrarErroCampo("editNumero", erro);
            } else {
                mostrarMensagemGeral(erro || "Não foi possível atualizar o veículo.", "error");
            }
        }
    } catch (err) {
        mostrarMensagemGeral("Não foi possível conectar ao servidor.", "error");
        console.error(err);
    }
});

async function deletarVeiculo(numero) {
    if (!confirm(`Excluir veículo Nº ${numero}?`)) return;

    try {
        const res = await fetch(`${API}/deletar/${numero}`, {
            method: "DELETE",
            headers: { "Authorization": authHeader }
        });

        if (res.ok) {
            mostrarMensagemGeral("Veículo excluído com sucesso.", "success", 5000);
            carregarDashboard();
        } else {
            const erro = await res.text();
            mostrarMensagemGeral(erro || "Não foi possível excluir o veículo.", "error");
        }
    } catch (err) {
        mostrarMensagemGeral("Não foi possível conectar ao servidor.", "error");
        console.error(err);
    }
}

carregarDashboard();
