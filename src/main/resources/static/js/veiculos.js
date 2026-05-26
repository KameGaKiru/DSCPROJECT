const API = "http://localhost:8080/api/veiculo";
const CHECKLIST_API = "http://localhost:8080/api/checklist";
const USUARIO_API = "http://localhost:8080/api/usuario";

const usuario = JSON.parse(localStorage.getItem("usuario"));
const authHeader = localStorage.getItem("authHeader");

// VALIDAÇÃO
if (!usuario || !authHeader || usuario.funcao?.toUpperCase() !== "COORDENADOR") {
    alert("Acesso negado!");
    localStorage.clear();
    window.location.href = "index.html";
}

// LOGOUT
document.getElementById("logoutBtn").addEventListener("click", () => {
    localStorage.clear();
    window.location.href = "index.html";
});

// MÁSCARAS DOS INPUTS
document.getElementById("numero").addEventListener("input", function () {
    this.value = this.value.replace(/\D/g, "").slice(0, 10);
});

document.getElementById("placa").addEventListener("input", function () {

    let v = this.value.toUpperCase().replace(/[^A-Z0-9-]/g, "");

    if (v.length > 3 && v[3] !== "-") {
        v = v.slice(0, 3) + "-" + v.slice(3);
    }

    this.value = v.slice(0, 8);
});

document.getElementById("editNumero").addEventListener("input", function () {
    this.value = this.value.replace(/\D/g, "").slice(0, 10);
});

document.getElementById("editPlaca").addEventListener("input", function () {

    let v = this.value.toUpperCase().replace(/[^A-Z0-9-]/g, "");

    if (v.length > 3 && v[3] !== "-") {
        v = v.slice(0, 3) + "-" + v.slice(3);
    }

    this.value = v.slice(0, 8);
});

// VALIDAÇÃO
function validarVeiculo(numero, placa, marca, tipo) {

    if (!/^\d+$/.test(numero) || parseInt(numero) <= 0) {
        alert("Número inválido.");
        return false;
    }

    if (numero.length > 10) {
        alert("Número deve ter no máximo 10 dígitos.");
        return false;
    }

    const placaAntiga = /^[A-Z]{3}-\d{4}$/;
    const placaMercosul = /^[A-Z]{3}-\d[A-Z]\d{2}$/;

    placa = placa.trim().toUpperCase();

    if (!placaAntiga.test(placa) && !placaMercosul.test(placa)) {
        alert("Placa inválida.");
        return false;
    }

    if (!marca || !tipo) {
        alert("Todos os campos são obrigatórios.");
        return false;
    }

    return true;
}

// DASHBOARD COMPLETO
async function carregarDashboard() {

    try {

        const [resV, resC, resU] = await Promise.all([
            fetch(`${API}/listar`, {
                headers: { "Authorization": authHeader }
            }),

            fetch(`${CHECKLIST_API}/listar`, {
                headers: { "Authorization": authHeader }
            }),

            fetch(`${USUARIO_API}/listar`, {
                headers: { "Authorization": authHeader }
            })
        ]);

        if ([resV, resC, resU].some(r => r.status === 401 || r.status === 403)) {

            alert("Sessão expirada!");

            localStorage.clear();
            window.location.href = "index.html";
            return;
        }

        const veiculos = await resV.json();
        const checklists = await resC.json();
        const usuarios = await resU.json();

        // CONTADORES
        const checkPorVeiculo = {};
        const checkPorMotorista = {};

        checklists.forEach(c => {

            const numero = c.veiculo?.numero;

            if (numero != null) {
                checkPorVeiculo[numero] =
                    (checkPorVeiculo[numero] || 0) + 1;
            }

            const matricula = c.motorista?.matricula;

            if (matricula) {
                checkPorMotorista[matricula] =
                    (checkPorMotorista[matricula] || 0) + 1;
            }
        });

        const motoristas = usuarios.filter(
            u => u.funcao?.toUpperCase() === "MOTORISTA"
        );

        // CARDS
        document.getElementById("totalVeiculos").textContent =
            veiculos.length;
        document.getElementById("totalChecklists").textContent =
            checklists.length;
        document.getElementById("totalMotoristas").textContent =
            motoristas.filter(
                m => checkPorMotorista[m.matricula] > 0
            ).length;

        // TABELA VEÍCULOS
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
                    <button
                        class="btn btn-warning btn-sm btn-editar"
                        data-numero="${v.numero}"
                        data-placa="${v.placa}"
                        data-marca="${v.marca}"
                        data-tipo="${v.tipo}">
                        Editar
                    </button>

                    <button
                        class="btn btn-danger btn-sm btn-deletar"
                        data-numero="${v.numero}">
                        Excluir
                    </button>
                </td>
            `;

            tbody.appendChild(tr);
        });

        // RANKING
        const rankingBody = document.getElementById("rankingTable");
        const ranking = motoristas
            .map(m => ({
                ...m,
                total: checkPorMotorista[m.matricula] || 0
            }))
            .sort((a, b) => b.total - a.total);

        rankingBody.innerHTML = "";

        if (ranking.length === 0) {
            rankingBody.innerHTML = `
                <tr>
                    <td colspan="4" class="text-center text-muted">
                        Nenhum motorista encontrado.
                    </td>
                </tr>
            `;

        } else {

            const medalhas = ["🥇", "🥈", "🥉"];

            ranking.forEach((m, i) => {
                const tr = document.createElement("tr");
                tr.innerHTML = `
                    <td>${medalhas[i] || (i + 1) + "º"}</td>
                    <td>
                        ${m.nome} ${m.sobrenome || ""}
                    </td>
                    <td>${m.matricula}</td>
                    <td>
                        <span class="badge bg-success">
                            ${m.total}
                        </span>
                    </td>
                `;

                rankingBody.appendChild(tr);
            });
        }

    } catch (err) {
        console.error(err);
        alert("Erro ao carregar dashboard!");
    }
}

carregarDashboard();

// EVENTOS DA TABELA
document.getElementById("veiculosTable")
.addEventListener("click", function(e) {

    const btnEditar = e.target.closest(".btn-editar");
    const btnDeletar = e.target.closest(".btn-deletar");

    if (btnEditar) {
        const { numero, placa, marca, tipo } = btnEditar.dataset;
        abrirModal(numero, placa, marca, tipo);
    }

    if (btnDeletar) {
        deletarVeiculo(btnDeletar.dataset.numero);
    }
});

// CADASTRAR
document.getElementById("cadVeiculoForm")
.addEventListener("submit", async function(e) {
    e.preventDefault();

    const numero = document.getElementById("numero").value.trim();
    const placa = document.getElementById("placa").value.trim().toUpperCase();
    const marca = document.getElementById("marca").value;
    const tipo = document.getElementById("tipo").value;

    if (!validarVeiculo(numero, placa, marca, tipo)) return;

    const data = {
        numero: parseInt(numero),
        placa,
        marca,
        tipo
    };

    const res = await fetch(`${API}/cadastrar`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": authHeader
        },
        body: JSON.stringify(data)
    });

    if (res.ok) {
        alert("Veículo cadastrado!");
        this.reset();
        carregarDashboard();

    } else {
        alert(await res.text());
    }
});

// MODAL
function abrirModal(numero, placa, marca, tipo) {

    document.getElementById("numeroOriginal").value = numero;
    document.getElementById("editNumero").value = numero;
    document.getElementById("editPlaca").value = placa;
    document.getElementById("editMarca").value = marca;
    document.getElementById("editTipo").value = tipo;

    new bootstrap.Modal(
        document.getElementById("editModal")
    ).show();
}
// EDITAR
document.getElementById("editVeiculoForm")
.addEventListener("submit", async function(e) {

    e.preventDefault();

    const numeroOriginal =
        document.getElementById("numeroOriginal").value;
    const numero =
        document.getElementById("editNumero").value.trim();
    const placa =
        document.getElementById("editPlaca").value.trim().toUpperCase();
    const marca =
        document.getElementById("editMarca").value;
    const tipo =
        document.getElementById("editTipo").value;

    if (!validarVeiculo(numero, placa, marca, tipo)) return;
    const data = {
        numero: parseInt(numero),
        placa,
        marca,
        tipo
    };

    const res = await fetch(`${API}/atualizar/${numeroOriginal}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "Authorization": authHeader
        },

        body: JSON.stringify(data)
    });

    if (res.ok) {

        alert("Veículo atualizado!");
        bootstrap.Modal
            .getInstance(document.getElementById("editModal"))
            .hide();

        carregarDashboard();

    } else {
        alert(await res.text());
    }
});

// DELETAR
async function deletarVeiculo(numero) {

    if (!confirm(`Excluir veículo Nº ${numero}?`)) return;
    const res = await fetch(`${API}/deletar/${numero}`, {
        method: "DELETE",
        headers: {
            "Authorization": authHeader
        }
    });

    if (res.ok) {
        alert("Veículo excluído!");

        carregarDashboard();

    } else {
        alert(await res.text());
    }
}