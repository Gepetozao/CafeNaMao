(function () {
  "use strict";

  const dados = window.CafeNaMaoDados;
  const detalhes = window.CafeNaMaoDetalhes;
  const carrinhoApi = window.CafeNaMaoCarrinho;
  const historicoApi = window.CafeNaMaoHistorico;
  const api = window.CafeNaMaoApi;

  const status = document.getElementById("status-pedido");
  const carrinhoVazio = document.getElementById("carrinho-vazio");
  const carrinhoConteudo = document.getElementById("carrinho-conteudo");
  const lista = document.getElementById("lista-carrinho");
  const total = document.getElementById("total-carrinho");
  const pagamentoPedido = document.getElementById("pagamento-pedido");
  const erroPagamento = document.getElementById("erro-pagamento");
  const concluir = document.getElementById("concluir-pedido");
  const confirmacao = document.getElementById("confirmacao-pedido");
  const pedidoDetalhe = document.getElementById("pedido-detalhe");
  const pedidoData = document.getElementById("pedido-data");
  const pedidoPagamento = document.getElementById("pedido-pagamento");
  const pedidoStatus = document.getElementById("pedido-status");
  const pedidoItens = document.getElementById("pedido-itens");
  const pedidoTotal = document.getElementById("pedido-total");
  const historicoVazio = document.getElementById("historico-vazio");
  const listaHistorico = document.getElementById("lista-historico");
  const limparHistoricoBotao = document.getElementById("limpar-historico");
  const statusHistorico = document.getElementById("status-historico");
  const tituloHistorico = document.getElementById("titulo-historico");
  const dialogoLimpar = document.getElementById("dialogo-limpar-historico");
  const cancelarLimpeza = document.getElementById("cancelar-limpeza");
  const confirmarLimpeza = document.getElementById("confirmar-limpeza");

  if (
    !dados ||
    !detalhes ||
    !carrinhoApi ||
    !historicoApi ||
    !status ||
    !carrinhoVazio ||
    !carrinhoConteudo ||
    !lista ||
    !total ||
    !pagamentoPedido ||
    !erroPagamento ||
    !concluir ||
    !confirmacao ||
    !pedidoDetalhe ||
    !pedidoData ||
    !pedidoPagamento ||
    !pedidoStatus ||
    !pedidoItens ||
    !pedidoTotal ||
    !historicoVazio ||
    !listaHistorico ||
    !limparHistoricoBotao ||
    !statusHistorico ||
    !tituloHistorico ||
    !dialogoLimpar ||
    !cancelarLimpeza ||
    !confirmarLimpeza
  ) {
    return;
  }

  let carrinho = carrinhoApi.carregarCarrinho();
  let apiDisponivel = false;
  let verificacaoApi = Promise.resolve();

  const formatadorData = new Intl.DateTimeFormat("pt-BR", {
    dateStyle: "short",
    timeStyle: "short",
  });

  const STATUS_DISPONIVEIS = [
    historicoApi.STATUS.RECEBIDO,
    historicoApi.STATUS.EM_PREPARO,
    historicoApi.STATUS.FINALIZADO,
  ];

  function anunciar(texto) {
    status.textContent = "";
    window.setTimeout(function () {
      status.textContent = texto;
    }, 20);
  }

  function anunciarHistorico(texto) {
    statusHistorico.textContent = "";
    window.setTimeout(function () {
      statusHistorico.textContent = texto;
    }, 20);
  }

  function subtotalItem(item) {
    return detalhes.calcularTotal(item.precoCentavos, item.quantidade);
  }

  async function verificarApi() {
    if (!api) {
      apiDisponivel = false;
      return;
    }

    apiDisponivel = await api.disponivel();
  }

  function itensParaApi() {
    return carrinho.itens.map(function (item) {
      return {
        bebidaId: item.id,
        quantidade: item.quantidade,
      };
    });
  }

  function criarRegistroLocal(pagamento) {
    const pedido = carrinhoApi.criarPedido(carrinho);

    return historicoApi.criarRegistroPedido(
      pedido,
      pagamento,
      historicoApi.STATUS.RECEBIDO
    );
  }

  function criarLinhaItem(item) {
    const linha = document.createElement("li");
    linha.className = "carrinho__item";
    linha.dataset.itemId = item.id;

    const informacoes = document.createElement("div");
    informacoes.className = "carrinho__item-info";

    const nome = document.createElement("h2");
    nome.textContent = item.nome;

    const preco = document.createElement("p");
    preco.className = "carrinho__item-preco";
    preco.textContent = "Preço unitário: " + dados.formatarPreco(item.precoCentavos);

    informacoes.append(nome, preco);

    const controles = document.createElement("div");
    controles.className = "carrinho__controles";

    const rotulo = document.createElement("label");
    rotulo.className = "carrinho__rotulo";
    rotulo.htmlFor = "quantidade-" + item.id;
    rotulo.textContent = "Quantidade";

    const campo = document.createElement("input");
    campo.type = "number";
    campo.id = "quantidade-" + item.id;
    campo.name = "quantidade-" + item.id;
    campo.inputMode = "numeric";
    campo.min = String(detalhes.QUANTIDADE_MINIMA);
    campo.max = String(detalhes.QUANTIDADE_MAXIMA);
    campo.step = "1";
    campo.value = String(item.quantidade);
    campo.setAttribute("data-quantidade-id", item.id);
    campo.setAttribute("aria-describedby", "erro-" + item.id);

    const erro = document.createElement("p");
    erro.className = "carrinho__erro";
    erro.id = "erro-" + item.id;
    erro.setAttribute("role", "alert");

    const subtotal = document.createElement("p");
    subtotal.className = "carrinho__subtotal";
    subtotal.id = "subtotal-" + item.id;
    subtotal.textContent = "Subtotal: " + dados.formatarPreco(subtotalItem(item));

    const remover = document.createElement("button");
    remover.type = "button";
    remover.className = "botao botao--secundario carrinho__remover";
    remover.textContent = "Remover";
    remover.setAttribute("data-remover-id", item.id);
    remover.setAttribute("aria-label", "Remover " + item.nome + " do pedido");

    controles.append(rotulo, campo, erro, subtotal, remover);
    linha.append(informacoes, controles);
    return linha;
  }

  function atualizarTotal() {
    total.textContent = dados.formatarPreco(carrinhoApi.calcularTotalCarrinho(carrinho));
  }

  function renderizarCarrinho() {
    if (carrinhoApi.carrinhoVazio(carrinho)) {
      lista.replaceChildren();
      carrinhoConteudo.hidden = true;
      carrinhoVazio.hidden = false;
      anunciar("Seu carrinho está vazio.");
      return;
    }

    const fragmento = document.createDocumentFragment();

    carrinho.itens.forEach(function (item) {
      fragmento.appendChild(criarLinhaItem(item));
    });

    lista.replaceChildren(fragmento);
    carrinhoVazio.hidden = true;
    carrinhoConteudo.hidden = false;
    atualizarTotal();
  }

  function tratarMudancaQuantidade(campo) {
    const id = campo.getAttribute("data-quantidade-id");
    const quantidade = Number(campo.value);
    const erro = detalhes.validarQuantidade(quantidade);
    const elementoErro = document.getElementById("erro-" + id);
    const item = carrinho.itens.find(function (existente) {
      return existente.id === id;
    });

    if (!item) {
      return;
    }

    if (erro) {
      elementoErro.textContent = erro;
      campo.setAttribute("aria-invalid", "true");
      return;
    }

    elementoErro.textContent = "";
    campo.removeAttribute("aria-invalid");
    carrinhoApi.alterarQuantidade(carrinho, id, quantidade);
    carrinhoApi.salvarCarrinho(carrinho);

    document.getElementById("subtotal-" + id).textContent =
      "Subtotal: " + dados.formatarPreco(subtotalItem(item));
    atualizarTotal();
    anunciar(item.nome + " atualizado para " + quantidade + " unidades.");
  }

  function removerDoCarrinho(id) {
    const item = carrinho.itens.find(function (existente) {
      return existente.id === id;
    });

    if (!item) {
      return;
    }

    carrinhoApi.removerItem(carrinho, id);
    carrinhoApi.salvarCarrinho(carrinho);
    renderizarCarrinho();

    if (!carrinhoApi.carrinhoVazio(carrinho)) {
      anunciar(item.nome + " removido do pedido.");
    }
  }

  function limparErroPagamento() {
    erroPagamento.textContent = "";
    pagamentoPedido.removeAttribute("aria-invalid");
  }

  function validarPagamento() {
    const selecionado = document.querySelector('input[name="pagamento"]:checked');

    if (!selecionado) {
      erroPagamento.textContent = "Escolha uma forma de pagamento para continuar.";
      pagamentoPedido.setAttribute("aria-invalid", "true");

      const primeiro = pagamentoPedido.querySelector('input[name="pagamento"]');

      if (primeiro) {
        primeiro.focus();
      }

      return "";
    }

    if (!historicoApi.pagamentoValido(selecionado.value)) {
      erroPagamento.textContent = "Escolha uma forma de pagamento válida.";
      pagamentoPedido.setAttribute("aria-invalid", "true");
      return "";
    }

    limparErroPagamento();
    return selecionado.value;
  }

  function validarQuantidades() {
    const campos = lista.querySelectorAll("input[data-quantidade-id]");
    const alteracoes = [];
    let primeiroInvalido = null;

    campos.forEach(function (campo) {
      const id = campo.getAttribute("data-quantidade-id");
      const quantidade = Number(campo.value);
      const erro = detalhes.validarQuantidade(quantidade);
      const elementoErro = document.getElementById("erro-" + id);
      const item = carrinho.itens.find(function (existente) {
        return existente.id === id;
      });

      if (!item) {
        return;
      }

      if (erro) {
        elementoErro.textContent = erro;
        campo.setAttribute("aria-invalid", "true");

        if (!primeiroInvalido) {
          primeiroInvalido = campo;
        }

        return;
      }

      elementoErro.textContent = "";
      campo.removeAttribute("aria-invalid");

      if (item.quantidade !== quantidade) {
        alteracoes.push({ id: id, quantidade: quantidade });
      }
    });

    if (primeiroInvalido) {
      anunciar("Corrija as quantidades destacadas antes de concluir.");
      primeiroInvalido.focus();
      return false;
    }

    if (alteracoes.length > 0) {
      alteracoes.forEach(function (alteracao) {
        carrinhoApi.alterarQuantidade(carrinho, alteracao.id, alteracao.quantidade);
      });
      carrinhoApi.salvarCarrinho(carrinho);
      atualizarTotal();
    }

    return true;
  }

  function preencherConfirmacao(pedido, origem) {
    pedidoDetalhe.textContent =
      "Pedido " +
      pedido.numero +
      (origem === "api"
        ? " registrado na API."
        : " registrado neste navegador porque a API não estava disponível.");
    pedidoData.textContent =
      "Realizado em " + formatadorData.format(new Date(pedido.criadoEm)) + ".";
    pedidoPagamento.textContent =
      "Pagamento: " + historicoApi.rotuloPagamento(pedido.pagamento) + " (simulado).";
    pedidoStatus.textContent = "Status: " + historicoApi.rotuloStatus(pedido.status) + ".";
    pedidoTotal.textContent = dados.formatarPreco(pedido.totalCentavos);

    const fragmento = document.createDocumentFragment();

    pedido.itens.forEach(function (item) {
      const linha = document.createElement("li");
      linha.textContent =
        item.quantidade +
        "x " +
        item.nome +
        " — " +
        dados.formatarPreco(item.precoCentavos * item.quantidade);
      fragmento.appendChild(linha);
    });

    pedidoItens.replaceChildren(fragmento);
  }

  async function concluirPedido() {
    const erro = carrinhoApi.validarCarrinhoParaPedido(carrinho);

    if (erro) {
      anunciar(erro);
      return;
    }

    if (!validarQuantidades()) {
      return;
    }

    const pagamento = validarPagamento();

    if (!pagamento) {
      return;
    }

    concluir.disabled = true;
    concluir.textContent = "Concluindo...";

    let registro;
    let origem = "local";

    try {
      await verificacaoApi;

      let pedidoApi = null;

      if (api && apiDisponivel) {
        try {
          pedidoApi = await api.criarPedido(itensParaApi(), pagamento);
        } catch (erroApi) {
          if (erroApi && erroApi.semRede) {
            apiDisponivel = false;
          } else {
            concluir.disabled = false;
            concluir.textContent = "Concluir pedido";
            anunciar(
              "A API não aceitou o pedido: " +
                (erroApi && erroApi.message ? erroApi.message : "verifique os itens.") +
                " O carrinho foi mantido."
            );
            return;
          }
        }
      }

      if (pedidoApi) {
        registro = api.paraRegistro(pedidoApi);
        origem = "api";
      } else {
        registro = criarRegistroLocal(pagamento);
      }
    } catch (erroCriacao) {
      concluir.disabled = false;
      concluir.textContent = "Concluir pedido";
      anunciar("Não foi possível concluir o pedido. Revise os dados e tente novamente.");
      return;
    }

    const salvouLocal = historicoApi.salvarPedido(registro);

    if (!salvouLocal && origem === "local") {
      concluir.disabled = false;
      concluir.textContent = "Concluir pedido";
      anunciar(
        "Não foi possível salvar o pedido no histórico. O carrinho foi mantido."
      );
      return;
    }

    const totalFormatado = dados.formatarPreco(registro.totalCentavos);

    carrinhoApi.limparCarrinhoSalvo();
    carrinho = carrinhoApi.criarCarrinhoVazio();

    carrinhoConteudo.hidden = true;
    carrinhoVazio.hidden = true;
    preencherConfirmacao(registro, origem);
    confirmacao.hidden = false;
    confirmacao.focus();

    pagamentoPedido.querySelectorAll('input[name="pagamento"]').forEach(function (radio) {
      radio.checked = false;
    });
    limparErroPagamento();
    renderizarHistorico();

    concluir.disabled = false;
    concluir.textContent = "Concluir pedido";

    anunciar(
      "Pedido " +
        registro.numero +
        " confirmado" +
        (origem === "api" ? " na API" : " no modo local") +
        ". Total de " +
        totalFormatado +
        "."
    );
  }

  function criarItemHistorico(pedido) {
    const item = document.createElement("li");
    item.className = "historico__pedido";
    item.dataset.numero = pedido.numero;
    item.dataset.status = pedido.status;

    const cabecalho = document.createElement("div");
    cabecalho.className = "historico__pedido-cabecalho";

    const titulo = document.createElement("h3");
    titulo.textContent = "Pedido " + pedido.numero;

    const statusPedido = document.createElement("p");
    statusPedido.className = "historico__status";
    statusPedido.setAttribute("data-status-rotulo", "");
    statusPedido.textContent = historicoApi.rotuloStatus(pedido.status);

    cabecalho.append(titulo, statusPedido);

    const data = document.createElement("p");
    data.className = "historico__data";
    data.textContent =
      "Realizado em " + formatadorData.format(new Date(pedido.criadoEm)) + ".";

    const itens = document.createElement("ul");
    itens.className = "historico__itens";

    pedido.itens.forEach(function (bebida) {
      const linha = document.createElement("li");
      linha.textContent =
        bebida.quantidade +
        "x " +
        bebida.nome +
        " — " +
        dados.formatarPreco(bebida.precoCentavos * bebida.quantidade);
      itens.appendChild(linha);
    });

    const rodape = document.createElement("div");
    rodape.className = "historico__rodape";

    const totalPedido = document.createElement("p");
    totalPedido.className = "historico__total";
    totalPedido.textContent = "Total: " + dados.formatarPreco(pedido.totalCentavos);

    const pagamento = document.createElement("p");
    pagamento.className = "historico__pagamento";
    pagamento.textContent =
      "Pagamento: " + historicoApi.rotuloPagamento(pedido.pagamento) + " (simulado)";

    const controle = document.createElement("div");
    controle.className = "historico__controle";

    const rotulo = document.createElement("label");
    rotulo.className = "historico__rotulo";
    rotulo.htmlFor = "status-" + pedido.numero;
    rotulo.textContent = "Status do pedido";

    const selecao = document.createElement("select");
    selecao.className = "historico__select";
    selecao.id = "status-" + pedido.numero;
    selecao.setAttribute("data-status-numero", pedido.numero);

    STATUS_DISPONIVEIS.forEach(function (opcao) {
      const elemento = document.createElement("option");
      elemento.value = opcao;
      elemento.textContent = historicoApi.rotuloStatus(opcao);
      elemento.selected = opcao === pedido.status;
      selecao.appendChild(elemento);
    });

    controle.append(rotulo, selecao);
    rodape.append(totalPedido, pagamento, controle);
    item.append(cabecalho, data, itens, rodape);

    return item;
  }

  function renderizarHistorico() {
    const historico = historicoApi.carregarHistorico();

    if (historico.length === 0) {
      listaHistorico.replaceChildren();
      historicoVazio.hidden = false;
      limparHistoricoBotao.hidden = true;
      return;
    }

    const fragmento = document.createDocumentFragment();

    historico.forEach(function (pedido) {
      fragmento.appendChild(criarItemHistorico(pedido));
    });

    listaHistorico.replaceChildren(fragmento);
    historicoVazio.hidden = true;
    limparHistoricoBotao.hidden = false;
  }

  function abrirDialogoLimpeza() {
    if (typeof dialogoLimpar.showModal === "function") {
      dialogoLimpar.showModal();
      return;
    }

    dialogoLimpar.setAttribute("open", "");
  }

  function fecharDialogoLimpeza() {
    if (typeof dialogoLimpar.close === "function") {
      dialogoLimpar.close();
      return;
    }

    dialogoLimpar.removeAttribute("open");
  }

  function limparTodoOHistorico() {
    const limpou = historicoApi.limparHistorico();

    fecharDialogoLimpeza();

    if (!limpou) {
      anunciarHistorico("Não foi possível limpar o histórico de pedidos.");
      return;
    }

    renderizarHistorico();
    tituloHistorico.focus();
    anunciarHistorico("Histórico de pedidos limpo.");
  }

  async function tratarMudancaStatus(evento) {
    const selecao = evento.target.closest("select[data-status-numero]");

    if (!selecao) {
      return;
    }

    const numero = selecao.getAttribute("data-status-numero");
    const novoStatus = selecao.value;
    const item = selecao.closest(".historico__pedido");
    const statusAnterior = item ? item.dataset.status : "";

    if (!historicoApi.statusValido(novoStatus)) {
      selecao.value = statusAnterior;
      anunciarHistorico("Status inválido para o pedido " + numero + ".");
      return;
    }

    selecao.disabled = true;

    let atualizou = false;
    let atualizouNaApi = false;

    await verificacaoApi;

    if (api && apiDisponivel) {
      try {
        await api.alterarStatus(numero, novoStatus);
        atualizou = true;
        atualizouNaApi = true;
      } catch (erroApi) {
        if (erroApi && erroApi.semRede) {
          apiDisponivel = false;
        } else if (!erroApi || erroApi.status !== 404) {
          selecao.disabled = false;

          if (item) {
            selecao.value = statusAnterior;
          }

          anunciarHistorico(
            "A API não aceitou o novo status: " +
              (erroApi && erroApi.message ? erroApi.message : "tente novamente.")
          );
          return;
        }
      }
    }

    if (!atualizou) {
      atualizou = historicoApi.alterarStatus(numero, novoStatus);
    }

    selecao.disabled = false;

    if (!atualizou) {
      if (item) {
        selecao.value = statusAnterior;
      }

      anunciarHistorico("Não foi possível atualizar o status do pedido " + numero + ".");
      return;
    }

    if (item) {
      item.dataset.status = novoStatus;

      const rotulo = item.querySelector("[data-status-rotulo]");

      if (rotulo) {
        rotulo.textContent = historicoApi.rotuloStatus(novoStatus);
      }
    }

    anunciarHistorico(
      "Pedido " +
        numero +
        ": status atualizado para " +
        historicoApi.rotuloStatus(novoStatus) +
        (atualizouNaApi ? " na API." : " neste navegador.")
    );
  }

  lista.addEventListener("change", function (evento) {
    const campo = evento.target;

    if (campo && campo.matches('input[data-quantidade-id]')) {
      tratarMudancaQuantidade(campo);
    }
  });

  lista.addEventListener("click", function (evento) {
    const botao = evento.target.closest("button[data-remover-id]");

    if (botao) {
      removerDoCarrinho(botao.getAttribute("data-remover-id"));
    }
  });

  pagamentoPedido.addEventListener("change", function (evento) {
    if (evento.target.matches('input[name="pagamento"]')) {
      limparErroPagamento();
    }
  });

  concluir.addEventListener("click", concluirPedido);

  listaHistorico.addEventListener("change", tratarMudancaStatus);

  limparHistoricoBotao.addEventListener("click", abrirDialogoLimpeza);
  cancelarLimpeza.addEventListener("click", fecharDialogoLimpeza);
  confirmarLimpeza.addEventListener("click", limparTodoOHistorico);

  window.addEventListener("pageshow", function (evento) {
    if (evento.persisted) {
      carrinho = carrinhoApi.carregarCarrinho();
      renderizarHistorico();

      if (confirmacao.hidden) {
        renderizarCarrinho();
      }

      verificacaoApi = verificarApi();
    }
  });

  renderizarHistorico();
  renderizarCarrinho();
  verificacaoApi = verificarApi();
})();
