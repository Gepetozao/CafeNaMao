(function (raiz) {
  "use strict";

  const emNode = typeof module !== "undefined" && module.exports;
  const detalhes = emNode ? require("./detalhes.js") : raiz.CafeNaMaoDetalhes;
  const CHAVE_HISTORICO = "cafe-na-mao:historico:v1";

  const STATUS = Object.freeze({
    RECEBIDO: "recebido",
    EM_PREPARO: "em-preparo",
    FINALIZADO: "finalizado",
  });

  const PAGAMENTOS = Object.freeze({
    DINHEIRO: "dinheiro",
    CARTAO_FICTICIO: "cartao-ficticio",
  });

  const ROTULOS_STATUS = Object.freeze({
    recebido: "Recebido",
    "em-preparo": "Em preparo",
    finalizado: "Finalizado",
  });

  const ROTULOS_PAGAMENTO = Object.freeze({
    dinheiro: "Dinheiro",
    "cartao-ficticio": "Cartão fictício",
  });

  function statusValido(status) {
    return Object.prototype.hasOwnProperty.call(ROTULOS_STATUS, status);
  }

  function pagamentoValido(pagamento) {
    return Object.prototype.hasOwnProperty.call(ROTULOS_PAGAMENTO, pagamento);
  }

  function normalizarItens(itens) {
    if (!Array.isArray(itens) || itens.length === 0) {
      throw new TypeError("O pedido precisa ter pelo menos um item.");
    }

    const identificadores = new Set();

    return itens.map(function (item) {
      if (
        !item ||
        typeof item.id !== "string" ||
        item.id.trim() === "" ||
        typeof item.nome !== "string" ||
        item.nome.trim() === "" ||
        !Number.isInteger(item.precoCentavos) ||
        item.precoCentavos < 0 ||
        !Number.isInteger(item.quantidade) ||
        item.quantidade < detalhes.QUANTIDADE_MINIMA ||
        item.quantidade > detalhes.QUANTIDADE_MAXIMA ||
        identificadores.has(item.id)
      ) {
        throw new TypeError("O pedido contém um item inválido.");
      }

      identificadores.add(item.id);

      return {
        id: item.id,
        nome: item.nome,
        precoCentavos: item.precoCentavos,
        quantidade: item.quantidade,
      };
    });
  }

  function calcularTotalItens(itens) {
    return itens.reduce(function (total, item) {
      return total + detalhes.calcularTotal(item.precoCentavos, item.quantidade);
    }, 0);
  }

  function normalizarPedido(pedido) {
    if (
      !pedido ||
      typeof pedido.numero !== "string" ||
      pedido.numero.trim() === "" ||
      typeof pedido.criadoEm !== "string" ||
      Number.isNaN(Date.parse(pedido.criadoEm)) ||
      !pagamentoValido(pedido.pagamento) ||
      !statusValido(pedido.status)
    ) {
      throw new TypeError("O pedido do histórico é inválido.");
    }

    const itens = normalizarItens(pedido.itens);
    const totalCentavos = calcularTotalItens(itens);

    if (
      !Number.isInteger(pedido.totalCentavos) ||
      pedido.totalCentavos !== totalCentavos
    ) {
      throw new RangeError("O total do pedido não corresponde aos itens.");
    }

    return {
      numero: pedido.numero,
      criadoEm: pedido.criadoEm,
      itens: itens,
      totalCentavos: totalCentavos,
      pagamento: pedido.pagamento,
      status: pedido.status,
    };
  }

  function criarRegistroPedido(pedido, pagamento, status) {
    const statusInicial = status === undefined ? STATUS.RECEBIDO : status;

    if (!pagamentoValido(pagamento)) {
      throw new TypeError("A forma de pagamento é inválida.");
    }

    if (!statusValido(statusInicial)) {
      throw new TypeError("O status do pedido é inválido.");
    }

    return normalizarPedido({
      numero: pedido && pedido.numero,
      criadoEm: pedido && pedido.criadoEm,
      itens: pedido && pedido.itens,
      totalCentavos: pedido && pedido.totalCentavos,
      pagamento: pagamento,
      status: statusInicial,
    });
  }

  function obterArmazenamento() {
    if (typeof window === "undefined" || !window.localStorage) {
      return null;
    }

    try {
      const teste = CHAVE_HISTORICO + ":teste";
      window.localStorage.setItem(teste, "1");
      window.localStorage.removeItem(teste);
      return window.localStorage;
    } catch (erro) {
      return null;
    }
  }

  function carregarHistorico() {
    const armazenamento = obterArmazenamento();

    if (!armazenamento) {
      return [];
    }

    try {
      const bruto = armazenamento.getItem(CHAVE_HISTORICO);

      if (!bruto) {
        return [];
      }

      const salvo = JSON.parse(bruto);

      if (!Array.isArray(salvo)) {
        return [];
      }

      const numeros = new Set();
      const pedidos = [];

      salvo.forEach(function (registro) {
        try {
          const pedido = normalizarPedido(registro);

          if (!numeros.has(pedido.numero)) {
            numeros.add(pedido.numero);
            pedidos.push(pedido);
          }
        } catch (erro) {
          return;
        }
      });

      return pedidos.sort(function (a, b) {
        return Date.parse(b.criadoEm) - Date.parse(a.criadoEm);
      });
    } catch (erro) {
      return [];
    }
  }

  function gravarHistorico(historico) {
    const armazenamento = obterArmazenamento();

    if (!armazenamento) {
      return false;
    }

    try {
      armazenamento.setItem(CHAVE_HISTORICO, JSON.stringify(historico));
      return true;
    } catch (erro) {
      return false;
    }
  }

  function salvarPedido(pedido) {
    let normalizado;

    try {
      normalizado = normalizarPedido(pedido);
    } catch (erro) {
      return false;
    }

    const historico = carregarHistorico().filter(function (registro) {
      return registro.numero !== normalizado.numero;
    });

    historico.unshift(normalizado);
    return gravarHistorico(historico);
  }

  function alterarStatus(numero, status) {
    if (!statusValido(status) || typeof numero !== "string") {
      return false;
    }

    const historico = carregarHistorico();
    const pedido = historico.find(function (registro) {
      return registro.numero === numero;
    });

    if (!pedido) {
      return false;
    }

    pedido.status = status;
    return gravarHistorico(historico);
  }

  function limparHistorico() {
    const armazenamento = obterArmazenamento();

    if (!armazenamento) {
      return false;
    }

    try {
      armazenamento.removeItem(CHAVE_HISTORICO);
      return true;
    } catch (erro) {
      return false;
    }
  }

  function rotuloStatus(status) {
    return ROTULOS_STATUS[status] || "";
  }

  function rotuloPagamento(pagamento) {
    return ROTULOS_PAGAMENTO[pagamento] || "";
  }

  const api = Object.freeze({
    CHAVE_HISTORICO,
    STATUS,
    PAGAMENTOS,
    statusValido,
    pagamentoValido,
    criarRegistroPedido,
    carregarHistorico,
    salvarPedido,
    alterarStatus,
    limparHistorico,
    rotuloStatus,
    rotuloPagamento,
  });

  if (emNode) {
    module.exports = api;
  }

  if (typeof window !== "undefined") {
    window.CafeNaMaoHistorico = api;
  }
})(typeof globalThis !== "undefined" ? globalThis : this);
