const VEICULO_API = "http://localhost:8080/api/veiculo";
const authV = localStorage.getItem("authHeader");
const usuarioLogadoV = JSON.parse(localStorage.getItem("usuarioLogado"));

// Bloquear motorista
if (usuarioLogadoV.funcao !== "COORDENADOR") {
    alert("Acesso negado! Somente coordenadores podem acessar veículos.");
    window.location.href = "dashboard_motorista.html";
}

// Listar
async function listarVeiculos() {
    const response = await fetch(`${VEICULO_API}/listar`, {
        headers: { "Authorization": authV }
    });
    const veiculos = await response.json();

    const tbody = document.getElementById("veiculosTable");
    tbody.innerHTML = "";
    veiculos.forEach(v => {
        tbody.innerHTML += `
            <tr>
                <td>${v.id}</td>
                <td>${v.placa}</td>
                <td>${v.modelo}</td>
                <td>
                    <button onclick="deletarVeiculo('${v.id}')">Excluir</button>
                    <button onclick="atualizarVeiculo('${v.id}')">Editar</button>
                </td>
            </tr>
        `;
    });
}

// Cadastrar
async function cadastrarVeiculo() {
    const data = {
        placa: document.getElementById("placa").value,
        modelo: document.getElementById("modelo").value
    };

    const response = await fetch(`${VEICULO_API}/cadastrar`, {
        method: "POST",
        headers: { 
            "Authorization": authV,
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    });

    if (response.ok) {
        alert("Veículo cadastrado!");
        listarVeiculos();
    } else {
        alert("Erro ao cadastrar veículo!");
    }
}

// Atualizar
async function atualizarVeiculo(id) {
    const novaPlaca = prompt("Nova placa:");
    const novoModelo = prompt("Novo modelo:");

    const response = await fetch(`${VEICULO_API}/atualizar/${id}`, {
        method: "PUT",
        headers: { 
            "Authorization": authV,
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ placa: novaPlaca, modelo: novoModelo })
    });

    if (response.ok) {
        alert("Veículo atualizado!");
        listarVeiculos();
    } else {
        alert("Erro ao atualizar veículo!");
    }
}

// Deletar
async function deletarVeiculo(id) {
    if (!confirm("Tem certeza que deseja excluir?")) return;

    const response = await fetch(`${VEICULO_API}/deletar/${id}`, {
        method: "DELETE",
        headers: { "Authorization": authV }
    });

    if (response.ok) {
        alert("Veículo deletado!");
        listarVeiculos();
    } else {
        alert("Erro ao deletar veículo!");
    }
}

window