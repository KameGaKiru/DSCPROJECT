"use strict";

const USUARIO_API = "/api/usuario/listar";
const CHECKLIST_API = "/api/checklist/listar";

document.addEventListener(
    "DOMContentLoaded",
    inicializarDashboard
);

function inicializarDashboard() {
    const elementos = obterElementos();
    const sessao = obterSessao();

    if (!validarElementos(elementos)) {
        return;
    }

    if (!sessao.valida) {
        redirecionarParaLogin();
        return;
    }

    configurarBotoes(elementos);
    exibirNomeMotorista(elementos, sessao.usuario);
    carregarDashboard(elementos, sessao);
}

function obterElementos() {
    return {
        nomeMotorista:
            document.getElementById("nomeMotorista"),

        totalChecklists:
            document.getElementById("totalChecklists"),

        rankingCard:
            document.getElementById("rankingCard"),

        rankingPosicao:
            document.getElementById("rankingPosicao"),

        rankingSub:
            document.getElementById("rankingSub"),

        mensagemGeral:
            document.getElementById("mensagemGeral"),

        tabela:
            document.querySelector("#usuariosTable tbody"),

        chatBtn:
            document.getElementById("chatBtn"),

        checklistBtn:
            document.getElementById("checklistBtn"),

        logoutBtn:
            document.getElementById("logoutBtn")
    };
}

function validarElementos(elementos) {
    const obrigatorios = [
        "nomeMotorista",
        "totalChecklists",
        "rankingCard",
        "rankingPosicao",
        "rankingSub",
        "mensagemGeral",
        "tabela",
        "chatBtn",
        "checklistBtn",
        "logoutBtn"
    ];

    const ausentes = obrigatorios.filter(
        nome => !elementos[nome]
    );

    if (ausentes.length === 0) {
        return true;
    }

    console.error(
        "Elementos não encontrados no HTML:",
        ausentes
    );

    return false;
}

function obterSessao() {
    let usuario = null;

    try {
        const usuarioSalvo =
            localStorage.getItem("usuario");

        if (usuarioSalvo) {
            usuario = JSON.parse(usuarioSalvo);
        }

    } catch (erro) {
        console.error(
            "Erro ao ler usuário salvo:",
            erro
        );
    }

    const authHeader =
        localStorage.getItem("authHeader");

    const funcao =
        usuario?.funcao?.toUpperCase();

    return {
        usuario,
        authHeader,

        valida:
            Boolean(usuario) &&
            Boolean(authHeader) &&
            funcao === "MOTORISTA"
    };
}

function configurarBotoes(elementos) {
    elementos.chatBtn.addEventListener(
        "click",
        () => {
            window.location.href =
                "chat.html";
        }
    );

    elementos.checklistBtn.addEventListener(
        "click",
        () => {
            window.location.href =
                "dashboard_checklist.html";
        }
    );

    elementos.logoutBtn.addEventListener(
        "click",
        () => {
            localStorage.clear();

            window.location.href =
                "index.html";
        }
    );
}

function exibirNomeMotorista(
    elementos,
    usuario
) {
    const nomeCompleto =
        obterNomeCompleto(usuario);

    elementos.nomeMotorista.textContent =
        nomeCompleto
            ? `Olá, ${nomeCompleto}`
            : `Matrícula ${usuario.matricula}`;
}

async function carregarDashboard(
    elementos,
    sessao
) {
    limparMensagem(elementos);
    mostrarCarregamento(elementos);

    try {
        const headers = {
            "Authorization":
                sessao.authHeader,

            "Accept":
                "application/json"
        };

        const [
            respostaUsuarios,
            respostaChecklists
        ] = await Promise.all([
            fetch(
                USUARIO_API,
                { headers }
            ),

            fetch(
                CHECKLIST_API,
                { headers }
            )
        ]);

        if (
            respostaUsuarios.status === 401 ||
            respostaUsuarios.status === 403 ||
            respostaChecklists.status === 401 ||
            respostaChecklists.status === 403
        ) {
            redirecionarParaLogin();
            return;
        }

        if (!respostaUsuarios.ok) {
            throw new Error(
                await obterMensagemErro(
                    respostaUsuarios,
                    "Não foi possível carregar os funcionários."
                )
            );
        }

        if (!respostaChecklists.ok) {
            throw new Error(
                await obterMensagemErro(
                    respostaChecklists,
                    "Não foi possível carregar os checklists."
                )
            );
        }

        const usuariosRecebidos =
            await respostaUsuarios.json();

        const checklistsRecebidos =
            await respostaChecklists.json();

        const usuarios =
            Array.isArray(usuariosRecebidos)
                ? usuariosRecebidos
                : [];

        const checklists =
            Array.isArray(checklistsRecebidos)
                ? checklistsRecebidos
                : [];

        const contagem =
            contarChecklistsPorMotorista(
                checklists
            );

        const ranking =
            montarRanking(
                usuarios,
                contagem
            );

        atualizarCards(
            elementos,
            sessao.usuario,
            ranking,
            contagem
        );

        preencherTabela(
            elementos,
            sessao.usuario,
            usuarios,
            contagem
        );

    } catch (erro) {
        console.error(
            "Erro ao carregar dashboard:",
            erro
        );

        elementos.totalChecklists.textContent =
            "—";

        elementos.rankingPosicao.textContent =
            "—";

        elementos.rankingSub.textContent =
            "Não foi possível carregar o ranking";

        elementos.tabela.innerHTML = `
            <tr>
                <td
                    colspan="5"
                    class="text-center text-danger py-4">
                    Não foi possível carregar os dados.
                </td>
            </tr>
        `;

        mostrarMensagem(
            elementos,
            erro.message ||
                "Não foi possível conectar ao servidor.",
            "danger"
        );
    }
}

function mostrarCarregamento(elementos) {
    elementos.totalChecklists.textContent =
        "—";

    elementos.rankingPosicao.textContent =
        "—";

    elementos.rankingSub.textContent =
        "Carregando ranking...";

    elementos.tabela.innerHTML = `
        <tr>
            <td
                colspan="5"
                class="text-center text-muted py-4">
                Carregando funcionários...
            </td>
        </tr>
    `;
}

function contarChecklistsPorMotorista(
    checklists
) {
    const contagem = {};

    checklists.forEach(checklist => {
        const matricula =
            checklist.motorista?.matricula;

        if (matricula == null) {
            return;
        }

        const chave = String(matricula);

        contagem[chave] =
            (contagem[chave] || 0) + 1;
    });

    return contagem;
}

function montarRanking(
    usuarios,
    contagem
) {
    return usuarios
        .filter(usuario =>
            usuario.funcao?.toUpperCase() ===
            "MOTORISTA"
        )
        .map(usuario => ({
            ...usuario,

            total:
                contagem[
                    String(usuario.matricula)
                ] || 0
        }))
        .sort((primeiro, segundo) => {
            if (
                segundo.total !==
                primeiro.total
            ) {
                return (
                    segundo.total -
                    primeiro.total
                );
            }

            return obterNomeCompleto(primeiro)
                .localeCompare(
                    obterNomeCompleto(segundo),
                    "pt-BR"
                );
        });
}

function atualizarCards(
    elementos,
    usuarioLogado,
    ranking,
    contagem
) {
    const matriculaLogada =
        String(usuarioLogado.matricula);

    const meuTotal =
        contagem[matriculaLogada] || 0;

    elementos.totalChecklists.textContent =
        meuTotal;

    const indice =
        ranking.findIndex(motorista =>
            String(motorista.matricula) ===
            matriculaLogada
        );

    elementos.rankingCard.classList.remove(
        "rank-1",
        "rank-2",
        "rank-3"
    );

    if (indice < 0) {
        elementos.rankingPosicao.textContent =
            "—";

        elementos.rankingSub.textContent =
            "Motorista não encontrado no ranking";

        return;
    }

    const posicao = indice + 1;

    elementos.rankingPosicao.textContent =
        `${posicao}º`;

    elementos.rankingSub.textContent =
        `entre ${ranking.length} ` +
        `motorista${
            ranking.length === 1
                ? ""
                : "s"
        }`;

    if (posicao <= 3) {
        elementos.rankingCard.classList.add(
            `rank-${posicao}`
        );
    }
}

function preencherTabela(
    elementos,
    usuarioLogado,
    usuarios,
    contagem
) {
    elementos.tabela.innerHTML = "";

    if (usuarios.length === 0) {
        elementos.tabela.innerHTML = `
            <tr>
                <td
                    colspan="5"
                    class="text-center text-muted py-4">
                    Nenhum funcionário encontrado.
                </td>
            </tr>
        `;

        return;
    }

    const usuariosOrdenados =
        [...usuarios].sort(
            (primeiro, segundo) =>
                obterNomeCompleto(primeiro)
                    .localeCompare(
                        obterNomeCompleto(segundo),
                        "pt-BR"
                    )
        );

    usuariosOrdenados.forEach(usuario => {
        const linha =
            document.createElement("tr");

        if (
            String(usuario.matricula) ===
            String(usuarioLogado.matricula)
        ) {
            linha.classList.add(
                "usuario-logado"
            );
        }

        linha.appendChild(
            criarCelula(usuario.matricula)
        );

        linha.appendChild(
            criarCelula(
                obterNomeCompleto(usuario)
            )
        );

        linha.appendChild(
            criarCelula(
                formatarFuncao(usuario.funcao)
            )
        );

        linha.appendChild(
            criarCelula(
                usuario.email || "—"
            )
        );

        linha.appendChild(
            criarCelulaQuantidade(
                usuario,
                contagem
            )
        );

        elementos.tabela.appendChild(
            linha
        );
    });
}

function criarCelula(valor) {
    const celula =
        document.createElement("td");

    celula.textContent =
        valor == null ||
        String(valor).trim() === ""
            ? "—"
            : String(valor);

    return celula;
}

function criarCelulaQuantidade(
    usuario,
    contagem
) {
    const celula =
        document.createElement("td");

    const ehMotorista =
        usuario.funcao?.toUpperCase() ===
        "MOTORISTA";

    if (!ehMotorista) {
        celula.textContent = "—";
        celula.classList.add("text-muted");

        return celula;
    }

    const badge =
        document.createElement("span");

    badge.className = "badge bg-primary";

    badge.textContent =
        contagem[
            String(usuario.matricula)
        ] || 0;

    celula.appendChild(badge);

    return celula;
}

function obterNomeCompleto(usuario) {
    return [
        usuario?.nome,
        usuario?.sobrenome
    ]
        .filter(valor =>
            valor != null &&
            String(valor).trim() !== ""
        )
        .join(" ")
        .trim();
}

function formatarFuncao(funcao) {
    const funcoes = {
        MOTORISTA: "Motorista",
        COORDENADOR: "Coordenador",
        MECANICO: "Mecânico"
    };

    const chave =
        funcao?.toUpperCase();

    return funcoes[chave] ||
        funcao ||
        "—";
}

async function obterMensagemErro(
    resposta,
    mensagemPadrao
) {
    try {
        const texto =
            await resposta.text();

        return texto.trim() ||
            mensagemPadrao;

    } catch {
        return mensagemPadrao;
    }
}

function mostrarMensagem(
    elementos,
    texto,
    tipo
) {
    elementos.mensagemGeral.textContent =
        texto;

    elementos.mensagemGeral.className =
        `alert alert-${tipo}`;

    elementos.mensagemGeral.style.display =
        "block";
}

function limparMensagem(elementos) {
    elementos.mensagemGeral.textContent = "";

    elementos.mensagemGeral.style.display =
        "none";
}

function redirecionarParaLogin() {
    localStorage.clear();

    window.location.replace(
        "index.html"
    );
}