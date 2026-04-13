const API = "http://localhost:8080/api/veiculo";
const usuario    = JSON.parse(localStorage.getItem("usuario"));   // ← era "usuarioLogado"
const authHeader = localStorage.getItem("authHeader");

// VALIDAÇÃO
if (!usuario || !authHeader || usuario.funcao?.toUpperCase() !== "COORDENADOR") {
    alert("Acesso negado!");
    localStorage.clear();
    window.location.href = "index.html";
}

document.getElementById("logoutBtn").addEventListener("click", () => {
    localStorage.clear();
    window.location.href = "index.html";
});

// LISTAR
async function listarVeiculos() {
    try {
        const res = await fetch(`${API}/listar`, {
            headers: { "Authorization": authHeader }
        });

        if (res.status === 401 || res.status === 403) {
            alert("Sessão expirada!");
            localStorage.clear();
            window.location.href = "index.html";
            return;
        }

        const veiculos = await res.json();
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
                    <button class="btn btn-warning btn-sm"
                        onclick="abrirModal(${v.numero}, '${v.placa}', '${v.marca}', '${v.tipo}')">
                        Editar
                    </button>
                    <button class="btn btn-danger btn-sm"
                        onclick="deletarVeiculo(${v.numero})">
                        Excluir
                    </button>
                </td>
            `;
            tbody.appendChild(tr);
        });

    } catch (err) {
        alert("Erro ao conectar ao servidor!");
        console.error(err);
    }
}

listarVeiculos();

// CADASTRAR
document.getElementById("cadVeiculoForm").addEventListener("submit", async function(e) {
    e.preventDefault();

    const data = {
        numero: parseInt(document.getElementById("numero").value),
        placa:  document.getElementById("placa").value,
        marca:  document.getElementById("marca").value,
        tipo:   document.getElementById("tipo").value
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
        listarVeiculos();
        this.reset();
    } else {
        const erro = await res.text();
        alert("Erro: " + erro);
    }
});

// ABRIR MODAL
function abrirModal(numero, placa, marca, tipo) {
    document.getElementById("numeroOriginal").value = numero;
    document.getElementById("editNumero").value     = numero;
    document.getElementById("editPlaca").value      = placa;
    document.getElementById("editMarca").value      = marca;
    document.getElementById("editTipo").value       = tipo;

    new bootstrap.Modal(document.getElementById("editModal")).show();
}

// SALVAR EDIÇÃO
document.getElementById("editVeiculoForm").addEventListener("submit", async function(e) {
    e.preventDefault();

    const numeroOriginal = document.getElementById("numeroOriginal").value;

    const data = {
        numero: parseInt(document.getElementById("editNumero").value),
        placa:  document.getElementById("editPlaca").value,
        marca:  document.getElementById("editMarca").value,
        tipo:   document.getElementById("editTipo").value
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
        listarVeiculos();
        bootstrap.Modal.getInstance(document.getElementById("editModal")).hide();
    } else {
        const erro = await res.text();
        alert("Erro: " + erro);
    }
});

// DELETAR
async function deletarVeiculo(numero) {
    if (!confirm("Confirma exclusão?")) return;

    const res = await fetch(`${API}/deletar/${numero}`, {
        method: "DELETE",
        headers: { "Authorization": authHeader }
    });

    if (res.ok) {
        alert("Veículo excluído!");
        listarVeiculos();
    } else {
        alert("Erro ao excluir veículo");
    }
}