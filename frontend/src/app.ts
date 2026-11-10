declare const Chart: any;

const API_URL = "http://localhost:8080/api";

interface Movimentacao {
    id: number;
    descricao: string;
    valor: number;
    tipo: "RECEITA" | "DESPESA";
    categoria: string;
    data: string;
}

interface ResumoFinanceiro {
    receitas: number;
    despesas: number;
    saldo: number;
}

interface ResumoCategoria {
    categoria: string;
    total: number;
}

interface AnaliseIA {
    mes: string;
    analise: string;
}

interface Categorias {
    receitas: string[];
    despesas: string[];
}

// Elementos da página
const form = document.querySelector<HTMLFormElement>("#formMovimentacao")!;
const campoId = document.querySelector<HTMLInputElement>("#movimentacaoId")!;
const campoDescricao = document.querySelector<HTMLInputElement>("#descricao")!;
const campoValor = document.querySelector<HTMLInputElement>("#valor")!;
const campoTipo = document.querySelector<HTMLSelectElement>("#tipo")!;
const campoCategoria = document.querySelector<HTMLSelectElement>("#categoria")!;
const campoData = document.querySelector<HTMLInputElement>("#data")!;
const filtroMes = document.querySelector<HTMLInputElement>("#filtroMes")!;

const listaMovimentacoes =
    document.querySelector<HTMLTableSectionElement>("#listaMovimentacoes")!;

const mensagemFormulario =
    document.querySelector<HTMLParagraphElement>("#mensagemFormulario")!;

const btnSalvar =
    document.querySelector<HTMLButtonElement>("#btnSalvar")!;

const btnCancelar =
    document.querySelector<HTMLButtonElement>("#btnCancelar")!;

const tituloFormulario =
    document.querySelector<HTMLHeadingElement>("#tituloFormulario")!;

const mensagemGrafico =
    document.querySelector<HTMLParagraphElement>("#mensagemGrafico")!;

const btnAnalisarIA =
    document.querySelector<HTMLButtonElement>("#btnAnalisarIA")!;

const resultadoIA =
    document.querySelector<HTMLDivElement>("#resultadoIA")!;
// Dados e estado da aplicação
let movimentacoes: Movimentacao[] = [];
let categorias: Categorias = {
    receitas: [],
    despesas: []
};

let grafico: any = null;

// Formatação de valores
function formatarMoeda(valor: number): string {
    return new Intl.NumberFormat("pt-BR", {
        style: "currency",
        currency: "BRL"
    }).format(valor);
}

// Data local no formato YYYY-MM-DD
function dataLocal(): string {
    const agora = new Date();
    const ano = agora.getFullYear();
    const mes = String(agora.getMonth() + 1).padStart(2, "0");
    const dia = String(agora.getDate()).padStart(2, "0");

    return `${ano}-${mes}-${dia}`;
}

// Evita interpretar texto recebido da API como HTML
function escaparHTML(valor: string): string {
    return valor.replace(/[&<>"']/g, caractere => {
        const entidades: Record<string, string> = {
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#039;"
        };

        return entidades[caractere];
    });
}

// Requisição padrão para a API
async function requisicaoAPI<T>(
    endpoint: string,
    options: RequestInit = {}
): Promise<T> {

    const resposta = await fetch(`${API_URL}${endpoint}`, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            ...options.headers
        }
    });

    if (!resposta.ok) {
        let mensagem = `Erro HTTP ${resposta.status}`;

        try {
            const erro = await resposta.json();

            if (erro.fields) {
                mensagem = Object.values(erro.fields).join(" ");
            } else if (erro.message) {
                mensagem = erro.message;
            }
        } catch {
            // Mantém a mensagem HTTP padrão.
        }

        throw new Error(mensagem);
    }

    if (resposta.status === 204 || resposta.status === 200 &&
        resposta.headers.get("content-length") === "0") {
        return undefined as T;
    }

    const texto = await resposta.text();

    if (!texto) {
        return undefined as T;
    }

    return JSON.parse(texto) as T;
}

// Carrega as categorias disponíveis
async function carregarCategorias(): Promise<void> {

    categorias = await requisicaoAPI<Categorias>("/categorias");

    atualizarOpcoesCategoria();
}

// Atualiza as opções de categoria de acordo com o tipo
function atualizarOpcoesCategoria(): void {

    const lista = campoTipo.value === "RECEITA"
        ? categorias.receitas
        : categorias.despesas;

    const categoriaAtual = campoCategoria.value;

    campoCategoria.innerHTML = `
        <option value="">Selecione</option>
        ${lista.map(categoria =>
            `<option value="${escaparHTML(categoria)}">
                ${escaparHTML(categoria)}
            </option>`
        ).join("")}
    `;

    if (lista.includes(categoriaAtual)) {
        campoCategoria.value = categoriaAtual;
    }
}

// Carrega as movimentações
async function carregarMovimentacoes(): Promise<void> {

    movimentacoes = await requisicaoAPI<Movimentacao[]>(
        "/movimentacoes"
    );

    renderizarMovimentacoes();
}

// Atualiza os indicadores financeiros
async function carregarResumo(): Promise<void> {

    const mes = filtroMes.value;

    const resumo = await requisicaoAPI<ResumoFinanceiro>(
        `/movimentacoes/resumo?mes=${mes}`
    );

    document.querySelector("#saldo")!.textContent =
        formatarMoeda(Number(resumo.saldo));

    document.querySelector("#receitas")!.textContent =
        formatarMoeda(Number(resumo.receitas));

    document.querySelector("#despesas")!.textContent =
        formatarMoeda(Number(resumo.despesas));
}

// Carrega os valores agrupados por categoria
async function carregarGrafico(): Promise<void> {

    const mes = filtroMes.value;

    const dados = await requisicaoAPI<ResumoCategoria[]>(
        `/movimentacoes/resumo/categorias?mes=${mes}`
    );

    renderizarGrafico(dados);
}

// Solicita análise financeira ao CashHub AI
async function analisarFinancas(): Promise<void> {

    const mes = filtroMes.value;

    if (!mes) {
        resultadoIA.textContent = "Selecione um período para analisar.";
        return;
    }

    btnAnalisarIA.disabled = true;
    btnAnalisarIA.textContent = "Analisando...";

    resultadoIA.innerHTML = `
        <p class="ai-loading">
            ✦ Analisando suas movimentações...
            <br>
            Aguarde enquanto a IA prepara seu relatório.
        </p>
    `;

    try {
        const resultado = await requisicaoAPI<AnaliseIA>(
            `/ia/analisar?mes=${encodeURIComponent(mes)}`
        );

        // Converte a resposta Markdown em texto seguro e legível.
        const textoSeguro = escaparHTML(resultado.analise);

        resultadoIA.innerHTML = `
            <div class="ai-response">${textoSeguro
                .replace(/\*\*(.*?)\*\*/g, "<strong>$1</strong>")
                .replace(/\*(.*?)\*/g, "<em>$1</em>")
                .replace(/\n/g, "<br>")}</div>
        `;

    } catch (erro) {
        resultadoIA.textContent =
            erro instanceof Error
                ? `Não foi possível gerar a análise: ${erro.message}`
                : "Não foi possível conectar ao CashHub AI.";
    } finally {
        btnAnalisarIA.disabled = false;
        btnAnalisarIA.textContent = "Analisar minhas finanças";
    }
}

// Monta o gráfico
function renderizarGrafico(dados: ResumoCategoria[]): void {

    const canvas = document.querySelector<HTMLCanvasElement>(
        "#graficoCategorias"
    )!;

    if (grafico) {
        grafico.destroy();
        grafico = null;
    }

    if (dados.length === 0) {
        canvas.style.display = "none";
        mensagemGrafico.style.display = "flex";
        return;
    }

    canvas.style.display = "block";
    mensagemGrafico.style.display = "none";

    grafico = new Chart(canvas, {
        type: "doughnut",

        data: {
            labels: dados.map(item => item.categoria),

            datasets: [{
                data: dados.map(item => Number(item.total)),
                backgroundColor: [
                    "#8b7cff",
                    "#38d996",
                    "#ff6b78",
                    "#f5b942",
                    "#45a7e8",
                    "#d77cff",
                    "#ff985e",
                    "#6c7a91"
                ],
                borderWidth: 0,
                hoverOffset: 5
            }]
        },

        options: {
            responsive: true,
            maintainAspectRatio: false,

            plugins: {
                legend: {
                    position: "bottom",
                    labels: {
                        color: "#b7bdca",
                        padding: 18,
                        usePointStyle: true
                    }
                },

                tooltip: {
                    callbacks: {
                        label: (context: any) =>
                            `${context.label}: ${formatarMoeda(context.raw)}`
                    }
                }
            }
        }
    });
}

// Exibe o histórico na tabela
function renderizarMovimentacoes(): void {

    const mes = filtroMes.value;

    const filtradas = movimentacoes
        .filter(item => item.data.startsWith(mes))
        .sort((a, b) => b.data.localeCompare(a.data));

    document.querySelector("#quantidadeMovimentacoes")!.textContent =
        `${filtradas.length} registro(s)`;

    if (filtradas.length === 0) {
        listaMovimentacoes.innerHTML = `
            <tr>
                <td colspan="6" class="empty-table">
                    Nenhuma movimentação encontrada neste mês.
                </td>
            </tr>
        `;
        return;
    }

    listaMovimentacoes.innerHTML = filtradas.map(item => {

        const receita = item.tipo === "RECEITA";

        const tipoTexto = receita ? "Receita" : "Despesa";
        const classeTipo = receita ? "type-income" : "type-expense";
        const sinal = receita ? "+" : "-";

        const dataFormatada = new Date(
            `${item.data}T12:00:00`
        ).toLocaleDateString("pt-BR");

        return `
            <tr>
                <td>${escaparHTML(item.descricao)}</td>

                <td>${escaparHTML(item.categoria)}</td>

                <td>${dataFormatada}</td>

                <td>
                    <span class="type-badge ${classeTipo}">
                        ${tipoTexto}
                    </span>
                </td>

                <td class="${receita ? "income" : "expense"}">
                    ${sinal} ${formatarMoeda(Number(item.valor))}
                </td>

                <td>
                    <div class="action-buttons">
                        <button
                            class="btn-edit"
                            data-action="edit"
                            data-id="${item.id}">
                            Editar
                        </button>

                        <button
                            class="btn-delete"
                            data-action="delete"
                            data-id="${item.id}">
                            Excluir
                        </button>
                    </div>
                </td>
            </tr>
        `;

    }).join("");
}

// Atualiza todos os dados do dashboard
async function atualizarDashboard(): Promise<void> {

    try {
        await Promise.all([
            carregarMovimentacoes(),
            carregarResumo(),
            carregarGrafico()
        ]);
    } catch (erro) {
        console.error("Erro ao atualizar dashboard:", erro);
        alert("Não foi possível carregar os dados. Verifique se o backend está funcionando.");
    }
}

// Limpa o formulário
function limparFormulario(): void {

    form.reset();

    campoId.value = "";
    campoTipo.value = "DESPESA";
    campoData.value = dataLocal();

    atualizarOpcoesCategoria();

    tituloFormulario.textContent = "Nova movimentação";
    btnSalvar.textContent = "Salvar movimentação";
    btnCancelar.classList.add("hidden");

    mensagemFormulario.textContent = "";
    mensagemFormulario.className = "form-message";
}

// Preenche o formulário para edição
function editarMovimentacao(id: number): void {

    const item = movimentacoes.find(m => m.id === id);

    if (!item) {
        return;
    }

    campoId.value = String(item.id);
    campoDescricao.value = item.descricao;
    campoValor.value = String(item.valor);
    campoTipo.value = item.tipo;
    atualizarOpcoesCategoria();
    campoCategoria.value = item.categoria;
    campoData.value = item.data;

    tituloFormulario.textContent = "Editar movimentação";
    btnSalvar.textContent = "Salvar alterações";
    btnCancelar.classList.remove("hidden");

    document.querySelector("#dashboard")!.scrollIntoView({
        behavior: "smooth"
    });
}

// Cadastra ou atualiza uma movimentação
form.addEventListener("submit", async (evento) => {

    evento.preventDefault();

    const id = campoId.value;

    const movimentacao = {
        descricao: campoDescricao.value.trim(),
        valor: Number(campoValor.value),
        tipo: campoTipo.value,
        categoria: campoCategoria.value,
        data: campoData.value
    };

    if (
        !movimentacao.descricao ||
        movimentacao.valor <= 0 ||
        !movimentacao.categoria ||
        !movimentacao.data
    ) {
        mensagemFormulario.textContent =
            "Preencha todos os campos corretamente.";

        mensagemFormulario.className = "form-message error";
        return;
    }

    try {

        if (id) {
            await requisicaoAPI<Movimentacao>(
                `/movimentacoes/${id}`,
                {
                    method: "PUT",
                    body: JSON.stringify(movimentacao)
                }
            );

            mensagemFormulario.textContent =
                "Movimentação atualizada com sucesso!";

        } else {
            await requisicaoAPI<Movimentacao>(
                "/movimentacoes",
                {
                    method: "POST",
                    body: JSON.stringify(movimentacao)
                }
            );

            mensagemFormulario.textContent =
                "Movimentação cadastrada com sucesso!";
        }

        mensagemFormulario.className = "form-message success";

        limparFormulario();
        await atualizarDashboard();

    } catch (erro) {

        mensagemFormulario.textContent =
            erro instanceof Error
                ? erro.message
                : "Não foi possível salvar a movimentação.";

        mensagemFormulario.className = "form-message error";
    }
});

// Editar e excluir movimentações
listaMovimentacoes.addEventListener("click", async (evento) => {

    const alvo = evento.target as HTMLElement;
    const botao = alvo.closest<HTMLButtonElement>("button[data-action]");

    if (!botao) {
        return;
    }

    const id = Number(botao.dataset.id);
    const acao = botao.dataset.action;

    if (acao === "edit") {
        editarMovimentacao(id);
        return;
    }

    if (acao === "delete") {

        const confirmar = confirm(
            "Tem certeza que deseja excluir esta movimentação?"
        );

        if (!confirmar) {
            return;
        }

        try {

            await requisicaoAPI<void>(
                `/movimentacoes/${id}`,
                { method: "DELETE" }
            );

            if (campoId.value === String(id)) {
                limparFormulario();
            }

            await atualizarDashboard();

        } catch (erro) {
            alert(
                erro instanceof Error
                    ? erro.message
                    : "Não foi possível excluir a movimentação."
            );
        }
    }
});

// Atualiza categorias quando o tipo muda
campoTipo.addEventListener("change", atualizarOpcoesCategoria);

// Cancela a edição
btnCancelar.addEventListener("click", limparFormulario);

// Atualiza os dados quando o mês muda
filtroMes.addEventListener("change", async () => {

    renderizarMovimentacoes();
    await Promise.all([
        carregarResumo(),
        carregarGrafico()
    ]).catch(erro => {
        console.error("Erro ao filtrar período:", erro);
        alert("Não foi possível atualizar o período selecionado.");
    });
});

// Evento do botão de análise da IA
btnAnalisarIA.addEventListener("click", analisarFinancas);

// Inicialização
async function iniciarAplicacao(): Promise<void> {

    const hoje = dataLocal();

    filtroMes.value = hoje.substring(0, 7);
    campoData.value = hoje;

    try {
        await carregarCategorias();
        await atualizarDashboard();

    } catch (erro) {
        console.error("Erro ao iniciar CashHub:", erro);

        alert(
            "Não foi possível conectar ao backend. " +
            "Verifique se o Spring Boot está em execução."
        );
    }
}

iniciarAplicacao();