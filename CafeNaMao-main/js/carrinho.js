(function (raiz) {
  "use strict";

  const emNode = typeof module !== "undefined" && module.exports;
  const detalhes = emNode ? require("./detalhes.js") : raiz.CafeNaMaoDetalhes;
  const CHAVE_ARMAZENAMENTO = "cafe-na-mao:carrinho:v1";

  function criarCarrinhoVazio() {
    return { itens: [] };
  }

  function validarBebida(bebida) {
    if (
      !bebida ||
      typeof bebida.id !== "string" ||
      bebida.id.trim() === "" ||
      typeof bebida.nome !== "string" ||
      bebida.nome.trim() === "" ||
      !Number.isInteger(bebida.precoCentavos) ||
      bebida.precoCentavos < 0
    ) {
      throw new TypeError("A bebida informada é inválida.");
    }
  }

  function validarQuantidade(quantidade) {
    const erro = detalhes.validarQuantidade(quantidade);

    if (erro) {
      throw new RangeError(erro);
    }
  }

  function localizarItem(carrinho, id) {
    return carrinho.itens.find(function (item) {
      return item.id === id;
    });
  }

  function adicionarItem(carrinho, bebida, quantidade) {
    validarBebida(bebida);
    validarQuantidade(quantidade);

    const existente = localizarItem(carrinho, bebida.id);

    if (existente) {
      const novaQuantidade = existente.quantidade + quantidade;
      validarQuantidade(novaQuantidade);
      existente.quantidade = novaQuantidade;
      return carrinho;
    }

    carrinho.itens.push({
      id: bebida.id,
      nome: bebida.nome,
      precoCentavos: bebida.precoCentavos,
      quantidade: quantidade,
    });

    return carrinho;
  }

  function alterarQuantidade(carrinho, id, quantidade) {
    validarQuantidade(quantidade);

    const item = localizarItem(carrinho, id);

    if (!item) {
      throw new RangeError("Bebida não encontrada no carrinho.");
    }

    item.quantidade = quantidade;
    return carrinho;
  }

  function removerItem(carrinho, id) {
    const quantidadeAnterior = carrinho.itens.length;

    carrinho.itens = carrinho.itens.filter(function (item) {
      return item.id !== id;
    });

    return carrinho.itens.length < quantidadeAnterior;
  }

  function contarItens(carrinho) {
    return carrinho.itens.reduce(function (total, item) {
      return total + item.quantidade;
    }, 0);
  }

  function carrinhoVazio(carrinho) {
    return carrinho.itens.length === 0;
  }

  function calcularTotalCarrinho(carrinho) {
    return carrinho.itens.reduce(function (total, item) {
      if (
        !Number.isInteger(item.precoCentavos) ||
        item.precoCentavos < 0 ||
        !Number.isInteger(item.quantidade) ||
        item.quantidade < 0
      ) {
        throw new TypeError("O carrinho contém valores inválidos.");
      }

      return total + detalhes.calcularTotal(item.precoCentavos, item.quantidade);
    }, 0);
  }

  function validarCarrinhoParaPedido(carrinho) {
    if (carrinhoVazio(carrinho)) {
      return "Adicione pelo menos uma bebida antes de concluir o pedido.";
    }

    return "";
  }

  function criarPedido(carrinho, data) {
    const erro = validarCarrinhoParaPedido(carrinho);

    if (erro) {
      throw new RangeError(erro);
    }

    const momento = data instanceof Date ? data : new Date();

    return {
      numero: "PED-" + String(momento.getTime()).slice(-8),
      criadoEm: momento.toISOString(),
      itens: carrinho.itens.map(function (item) {
        return {
          id: item.id,
          nome: item.nome,
          precoCentavos: item.precoCentavos,
          quantidade: item.quantidade,
        };
      }),
      totalCentavos: calcularTotalCarrinho(carrinho),
    };
  }

  function obterArmazenamento() {
    if (typeof window === "undefined" || !window.localStorage) {
      return null;
    }

    try {
      const teste = CHAVE_ARMAZENAMENTO + ":teste";
      window.localStorage.setItem(teste, "1");
      window.localStorage.removeItem(teste);
      return window.localStorage;
    } catch (erro) {
      return null;
    }
  }

  function carregarCarrinho() {
    const armazenamento = obterArmazenamento();

    if (!armazenamento) {
      return criarCarrinhoVazio();
    }

    try {
      const bruto = armazenamento.getItem(CHAVE_ARMAZENAMENTO);

      if (!bruto) {
        return criarCarrinhoVazio();
      }

      const salvo = JSON.parse(bruto);

      if (!salvo || !Array.isArray(salvo.itens)) {
        return criarCarrinhoVazio();
      }

      const itens = [];

      salvo.itens.forEach(function (item) {
        const idUnico = !itens.some(function (existente) {
          return existente.id === item.id;
        });

        if (
          item &&
          typeof item.id === "string" &&
          typeof item.nome === "string" &&
          Number.isInteger(item.precoCentavos) &&
          item.precoCentavos >= 0 &&
          Number.isInteger(item.quantidade) &&
          item.quantidade >= detalhes.QUANTIDADE_MINIMA &&
          item.quantidade <= detalhes.QUANTIDADE_MAXIMA &&
          idUnico
        ) {
          itens.push({
            id: item.id,
            nome: item.nome,
            precoCentavos: item.precoCentavos,
            quantidade: item.quantidade,
          });
        }
      });

      return { itens: itens };
    } catch (erro) {
      return criarCarrinhoVazio();
    }
  }

  function salvarCarrinho(carrinho) {
    const armazenamento = obterArmazenamento();

    if (!armazenamento) {
      return;
    }

    try {
      armazenamento.setItem(
        CHAVE_ARMAZENAMENTO,
        JSON.stringify({ itens: carrinho.itens })
      );
    } catch (erro) {
      return;
    }
  }

  function limparCarrinhoSalvo() {
    const armazenamento = obterArmazenamento();

    if (!armazenamento) {
      return;
    }

    try {
      armazenamento.removeItem(CHAVE_ARMAZENAMENTO);
    } catch (erro) {
      return;
    }
  }

  const api = Object.freeze({
    criarCarrinhoVazio,
    adicionarItem,
    alterarQuantidade,
    removerItem,
    contarItens,
    carrinhoVazio,
    calcularTotalCarrinho,
    validarCarrinhoParaPedido,
    criarPedido,
    carregarCarrinho,
    salvarCarrinho,
    limparCarrinhoSalvo,
  });

  if (emNode) {
    module.exports = api;
  }

  if (typeof window !== "undefined") {
    window.CafeNaMaoCarrinho = api;
  }
})(typeof globalThis !== "undefined" ? globalThis : this);
