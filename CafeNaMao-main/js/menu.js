(function () {
  "use strict";

  const dados = window.CafeNaMaoDados;
  const detalhes = window.CafeNaMaoDetalhes;
  const carrinho = window.CafeNaMaoCarrinho;
  const api = window.CafeNaMaoApi;
  const lista = document.getElementById("lista-bebidas");
  const status = document.getElementById("status-catalogo");
  const origem = document.getElementById("origem-catalogo");
  const resumoCarrinho = document.getElementById("resumo-carrinho");
  const resumoCarrinhoTexto = document.getElementById("resumo-carrinho-texto");
  const estado = document.getElementById("estado-catalogo");
  const mensagem = document.getElementById("mensagem-estado");
  const acao = document.getElementById("acao-estado");
  const painelDetalhes = document.getElementById("painel-detalhes");
  const detalhesCategoria = document.getElementById("detalhes-categoria");
  const detalhesTitulo = document.getElementById("detalhes-titulo");
  const detalhesDescricao = document.getElementById("detalhes-descricao");
  const detalhesPreco = document.getElementById("detalhes-preco");
  const detalhesTotal = document.getElementById("detalhes-total");
  const detalhesQuantidade = document.getElementById("detalhes-quantidade");
  const detalhesErro = document.getElementById("detalhes-erro");
  const detalhesAdicionar = document.getElementById("detalhes-adicionar");
  const detalhesFechar = document.getElementById("detalhes-fechar");

  if (
    !lista ||
    !status ||
    !origem ||
    !estado ||
    !mensagem ||
    !acao ||
    !detalhes ||
    !carrinho ||
    !resumoCarrinho ||
    !resumoCarrinhoTexto ||
    !painelDetalhes ||
    !detalhesCategoria ||
    !detalhesTitulo ||
    !detalhesDescricao ||
    !detalhesPreco ||
    !detalhesTotal ||
    !detalhesQuantidade ||
    !detalhesErro ||
    !detalhesAdicionar ||
    !detalhesFechar
  ) {
    return;
  }

  let bebidaAtual = null;
  let botaoQueAbriu = null;

  const parametros = new URLSearchParams(window.location.search);
  const estadoDaUrl = parametros.get("estado");
  let estadoSimulado = estadoDaUrl === "vazio" || estadoDaUrl === "erro" ? estadoDaUrl : null;

  function anunciar(texto) {
    status.textContent = "";
    window.setTimeout(function () {
      status.textContent = texto;
    }, 20);
  }

  function esperar(milissegundos) {
    return new Promise(function (resolver) {
      window.setTimeout(resolver, milissegundos);
    });
  }

  function ocultarEstado() {
    estado.hidden = true;
    mensagem.textContent = "";
    acao.replaceChildren();
    origem.hidden = true;
    origem.textContent = "";
  }

  function criarEsqueleto() {
    const article = document.createElement("article");
    article.className = "bebida bebida--esqueleto";
    article.setAttribute("aria-hidden", "true");
    article.innerHTML =
      '<div class="bebida__topo">' +
      '<span class="esqueleto esqueleto--badge"></span>' +
      '<span class="esqueleto esqueleto--tag"></span>' +
      "</div>" +
      '<div class="esqueleto esqueleto--titulo"></div>' +
      '<div class="esqueleto esqueleto--linha"></div>' +
      '<div class="esqueleto esqueleto--linha"></div>' +
      '<div class="esqueleto esqueleto--linha-curta"></div>' +
      '<div class="esqueleto esqueleto--preco"></div>';
    return article;
  }

  function atualizarTotalDetalhes() {
    const quantidade = Number(detalhesQuantidade.value);
    const erro = detalhes.validarQuantidade(quantidade);

    if (erro) {
      detalhesErro.textContent = erro;
      detalhesQuantidade.setAttribute("aria-invalid", "true");
      detalhesTotal.textContent = "—";
      return;
    }

    detalhesErro.textContent = "";
    detalhesQuantidade.removeAttribute("aria-invalid");
    detalhesTotal.textContent = dados.formatarPreco(
      detalhes.calcularTotal(bebidaAtual.precoCentavos, quantidade)
    );
  }

  function atualizarResumoCarrinho() {
    const carrinhoAtual = carrinho.carregarCarrinho();
    const totalItens = carrinho.contarItens(carrinhoAtual);

    if (totalItens === 0) {
      resumoCarrinho.hidden = true;
      resumoCarrinhoTexto.textContent = "";
      return;
    }

    resumoCarrinhoTexto.textContent =
      totalItens +
      (totalItens === 1 ? " item · " : " itens · ") +
      dados.formatarPreco(carrinho.calcularTotalCarrinho(carrinhoAtual));
    resumoCarrinho.hidden = false;
  }

  function adicionarAoCarrinho() {
    if (!bebidaAtual) {
      return;
    }

    const quantidade = Number(detalhesQuantidade.value);
    const erro = detalhes.validarQuantidade(quantidade);

    if (erro) {
      detalhesErro.textContent = erro;
      detalhesQuantidade.setAttribute("aria-invalid", "true");
      return;
    }

    try {
      const carrinhoAtual = carrinho.carregarCarrinho();
      carrinho.adicionarItem(carrinhoAtual, bebidaAtual, quantidade);
      carrinho.salvarCarrinho(carrinhoAtual);
    } catch (erroCarrinho) {
      detalhesErro.textContent = erroCarrinho.message;
      detalhesQuantidade.setAttribute("aria-invalid", "true");
      return;
    }

    const nomeAdicionado = bebidaAtual.nome;
    atualizarResumoCarrinho();
    fecharDetalhes();
    anunciar(nomeAdicionado + " adicionado ao pedido.");
  }

  function abrirDetalhes(bebida, botao) {
    bebidaAtual = bebida;
    botaoQueAbriu = botao;

    detalhesCategoria.textContent = bebida.categoria;
    detalhesTitulo.textContent = bebida.nome;
    detalhesDescricao.textContent = bebida.descricao;
    detalhesPreco.textContent = dados.formatarPreco(bebida.precoCentavos);
    detalhesQuantidade.value = "1";
    atualizarTotalDetalhes();

    if (typeof painelDetalhes.showModal === "function") {
      painelDetalhes.showModal();
    } else {
      painelDetalhes.setAttribute("open", "");
    }

    detalhesQuantidade.focus();
    detalhesQuantidade.select();
  }

  function fecharDetalhes() {
    if (typeof painelDetalhes.close === "function") {
      painelDetalhes.close();
    } else {
      painelDetalhes.removeAttribute("open");

      if (botaoQueAbriu && document.contains(botaoQueAbriu)) {
        botaoQueAbriu.focus();
      }
    }
  }

  painelDetalhes.addEventListener("close", function () {
    if (botaoQueAbriu && document.contains(botaoQueAbriu)) {
      botaoQueAbriu.focus();
    }

    botaoQueAbriu = null;
    bebidaAtual = null;
  });

  detalhesFechar.addEventListener("click", fecharDetalhes);
  detalhesAdicionar.addEventListener("click", adicionarAoCarrinho);
  detalhesQuantidade.addEventListener("input", atualizarTotalDetalhes);

  window.addEventListener("pageshow", atualizarResumoCarrinho);
  document.addEventListener("visibilitychange", function () {
    if (!document.hidden) {
      atualizarResumoCarrinho();
    }
  });

  function criarCardBebida(bebida, indice) {
    const article = document.createElement("article");
    article.className = "bebida";
    article.dataset.bebidaId = bebida.id;

    const topo = document.createElement("div");
    topo.className = "bebida__topo";

    const numero = document.createElement("span");
    numero.className = "bebida__numero";
    numero.setAttribute("aria-hidden", "true");
    numero.textContent = String(indice + 1).padStart(2, "0");

    const categoria = document.createElement("span");
    categoria.className = "bebida__categoria";
    categoria.textContent = bebida.categoria;

    topo.append(numero, categoria);

    const titulo = document.createElement("h2");
    titulo.id = "bebida-titulo-" + bebida.id;
    titulo.textContent = bebida.nome;
    article.setAttribute("aria-labelledby", titulo.id);

    const descricao = document.createElement("p");
    descricao.className = "bebida__descricao";
    descricao.textContent = bebida.descricao;

    const preco = document.createElement("p");
    preco.className = "bebida__preco";
    preco.textContent = dados.formatarPreco(bebida.precoCentavos);

    const acoes = document.createElement("div");
    acoes.className = "bebida__acoes";

    const botaoDetalhes = document.createElement("button");
    botaoDetalhes.type = "button";
    botaoDetalhes.className = "botao botao--secundario bebida__detalhes";
    botaoDetalhes.textContent = "Ver detalhes";
    botaoDetalhes.setAttribute("aria-haspopup", "dialog");
    botaoDetalhes.setAttribute("aria-controls", "painel-detalhes");
    botaoDetalhes.addEventListener("click", function () {
      abrirDetalhes(bebida, botaoDetalhes);
    });

    acoes.append(botaoDetalhes);
    article.append(topo, titulo, descricao, preco, acoes);
    return article;
  }

  function renderizarBebidas(bebidas, veioDaApi) {
    const fragmento = document.createDocumentFragment();

    bebidas.forEach(function (bebida, indice) {
      fragmento.appendChild(criarCardBebida(bebida, indice));
    });

    lista.replaceChildren(fragmento);
    lista.setAttribute("aria-busy", "false");
    ocultarEstado();

    origem.textContent = veioDaApi
      ? "Catálogo carregado da API."
      : "Modo local: API indisponível. O catálogo salvo neste navegador está em uso.";
    origem.hidden = false;

    anunciar(
      (veioDaApi ? "Catálogo carregado da API. " : "Catálogo local. ") +
        bebidas.length +
        " bebidas disponíveis."
    );
  }

  function mostrarCarregando() {
    ocultarEstado();
    lista.setAttribute("aria-busy", "true");

    const fragmento = document.createDocumentFragment();
    for (let indice = 0; indice < 3; indice += 1) {
      fragmento.appendChild(criarEsqueleto());
    }

    lista.replaceChildren(fragmento);
    anunciar("Carregando bebidas.");
  }

  function mostrarVazio() {
    lista.replaceChildren();
    lista.setAttribute("aria-busy", "false");

    mensagem.textContent = "Nenhuma bebida disponível no momento.";

    const link = document.createElement("a");
    link.className = "botao botao--secundario";
    link.href = "index.html";
    link.textContent = "Voltar ao início";

    acao.replaceChildren(link);
    estado.hidden = false;
    anunciar("Nenhuma bebida disponível no momento.");
  }

  function mostrarErro() {
    lista.replaceChildren();
    lista.setAttribute("aria-busy", "false");

    mensagem.textContent =
      "Não foi possível carregar o menu. Verifique sua conexão e tente novamente.";

    const botao = document.createElement("button");
    botao.className = "botao botao--primario";
    botao.type = "button";
    botao.textContent = "Tentar novamente";
    botao.addEventListener("click", carregarCatalogo);

    acao.replaceChildren(botao);
    estado.hidden = false;
    anunciar("Não foi possível carregar o menu.");
  }

  async function carregarCatalogo() {
    const estadoDaCarga = estadoSimulado;
    estadoSimulado = null;

    mostrarCarregando();

    const inicio = Date.now();

    if (estadoDaCarga === "erro") {
      await esperar(450);
      mostrarErro();
      return;
    }

    if (estadoDaCarga === "vazio") {
      await esperar(450);
      mostrarVazio();
      return;
    }

    let bebidas = dados ? dados.bebidas : [];
    let veioDaApi = false;

    if (api) {
      try {
        bebidas = await api.listarBebidas();
        veioDaApi = true;
      } catch (erroApi) {
        // Sem a API, o catálogo local mantém a página funcionando por file://.
        bebidas = dados ? dados.bebidas : [];
      }
    }

    const decorrido = Date.now() - inicio;

    if (decorrido < 450) {
      await esperar(450 - decorrido);
    }

    if (bebidas.length === 0) {
      mostrarVazio();
      return;
    }

    renderizarBebidas(bebidas, veioDaApi);
  }

  atualizarResumoCarrinho();
  carregarCatalogo();
})();
