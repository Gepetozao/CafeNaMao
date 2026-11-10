(function (raiz) {
  "use strict";

  const URL_PADRAO = "http://localhost:8080/api";
  const LIMITE_MS = 3500;

  function normalizarUrl(url) {
    return String(url).trim().replace(/\/+$/, "");
  }

  /**
   * Resolve a URL da API nesta ordem:
   * 1. parâmetro `?api=http://host:porta/api` na página;
   * 2. variável global `CAFE_NA_MAO_API_URL` definida antes deste script;
   * 3. `http://localhost:8080/api`.
   */
  function urlBase() {
    if (
      typeof window !== "undefined" &&
      window.location &&
      typeof window.location.search === "string"
    ) {
      const parametro = new URLSearchParams(window.location.search).get("api");

      if (parametro && parametro.trim() !== "") {
        return normalizarUrl(parametro);
      }
    }

    if (
      typeof raiz.CAFE_NA_MAO_API_URL === "string" &&
      raiz.CAFE_NA_MAO_API_URL.trim() !== ""
    ) {
      return normalizarUrl(raiz.CAFE_NA_MAO_API_URL);
    }

    return URL_PADRAO;
  }

  async function requisitar(caminho, opcoes) {
    if (typeof fetch !== "function") {
      throw new Error("Este navegador não oferece suporte a fetch.");
    }

    const configuracao = {
      method: opcoes && opcoes.method ? opcoes.method : "GET",
      headers: { Accept: "application/json" },
    };

    if (opcoes && Object.prototype.hasOwnProperty.call(opcoes, "body")) {
      configuracao.headers["Content-Type"] = "application/json";
      configuracao.body = JSON.stringify(opcoes.body);
    }

    const controlador =
      typeof AbortController === "function" ? new AbortController() : null;

    if (controlador) {
      configuracao.signal = controlador.signal;
    }

    const temporizador = controlador
      ? setTimeout(function () {
          controlador.abort();
        }, LIMITE_MS)
      : null;

    try {
      const resposta = await fetch(urlBase() + caminho, configuracao);

      if (!resposta.ok) {
        let detalhe = "A API respondeu com status " + resposta.status + ".";

        try {
          const problema = await resposta.json();

          if (problema && typeof problema.detail === "string") {
            detalhe = problema.detail;
          }
        } catch (erroLeitura) {
          // O corpo do erro não era JSON; mantém a mensagem padrão.
        }

        const erroHttp = new Error(detalhe);
        erroHttp.status = resposta.status;
        throw erroHttp;
      }

      if (resposta.status === 204) {
        return null;
      }

      return await resposta.json();
    } catch (erro) {
      if (erro && erro.name === "AbortError") {
        const erroTempo = new Error("A API não respondeu em " + LIMITE_MS + " ms.");
        erroTempo.semRede = true;
        throw erroTempo;
      }

      if (erro && typeof erro.status === "number") {
        throw erro;
      }

      if (erro instanceof SyntaxError) {
        throw new Error("A API devolveu um JSON inválido.");
      }

      const erroRede = new Error(
        erro && erro.message ? erro.message : "Não foi possível acessar a API."
      );
      erroRede.semRede = true;
      throw erroRede;
    } finally {
      if (temporizador !== null) {
        clearTimeout(temporizador);
      }
    }
  }

  function validarBebidaApi(bebida) {
    if (
      !bebida ||
      typeof bebida.id !== "string" ||
      bebida.id.trim() === "" ||
      typeof bebida.nome !== "string" ||
      bebida.nome.trim() === "" ||
      typeof bebida.descricao !== "string" ||
      typeof bebida.categoria !== "string" ||
      !Number.isInteger(bebida.precoCentavos) ||
      bebida.precoCentavos < 0
    ) {
      throw new TypeError("A API devolveu uma bebida inválida.");
    }

    return {
      id: bebida.id,
      nome: bebida.nome,
      descricao: bebida.descricao,
      categoria: bebida.categoria,
      precoCentavos: bebida.precoCentavos,
    };
  }

  async function listarBebidas() {
    const resposta = await requisitar("/bebidas");

    if (!Array.isArray(resposta)) {
      throw new TypeError("A API devolveu um catálogo inválido.");
    }

    return resposta.map(validarBebidaApi);
  }

  async function criarPedido(itens, formaPagamento) {
    if (!Array.isArray(itens) || itens.length === 0) {
      throw new TypeError("O pedido precisa ter pelo menos um item.");
    }

    const corpo = {
      itens: itens.map(function (item) {
        return {
          bebidaId: item.bebidaId,
          quantidade: item.quantidade,
        };
      }),
      formaPagamento: formaPagamento,
    };

    return requisitar("/pedidos", { method: "POST", body: corpo });
  }

  async function alterarStatus(numero, status) {
    return requisitar(
      "/pedidos/" + encodeURIComponent(numero) + "/status",
      { method: "PATCH", body: { status: status } }
    );
  }

  async function disponivel() {
    try {
      await listarBebidas();
      return true;
    } catch (erro) {
      return false;
    }
  }

  /**
   * Converte o pedido devolvido pela API para o mesmo formato usado pelo
   * histórico local. Assim o restante da interface não precisa conhecer os
   * nomes de campos do contrato HTTP.
   */
  function paraRegistro(pedido) {
    if (
      !pedido ||
      typeof pedido.numero !== "string" ||
      pedido.numero.trim() === "" ||
      typeof pedido.criadoEm !== "string" ||
      typeof pedido.formaPagamento !== "string" ||
      typeof pedido.status !== "string" ||
      !Array.isArray(pedido.itens) ||
      pedido.itens.length === 0 ||
      !Number.isInteger(pedido.totalCentavos) ||
      pedido.totalCentavos < 0
    ) {
      throw new TypeError("A API devolveu um pedido inválido.");
    }

    const itens = pedido.itens.map(function (item) {
      if (
        !item ||
        typeof item.bebidaId !== "string" ||
        item.bebidaId.trim() === "" ||
        typeof item.nome !== "string" ||
        item.nome.trim() === "" ||
        !Number.isInteger(item.precoCentavos) ||
        item.precoCentavos < 0 ||
        !Number.isInteger(item.quantidade) ||
        item.quantidade < 1
      ) {
        throw new TypeError("A API devolveu um item de pedido inválido.");
      }

      return {
        id: item.bebidaId,
        nome: item.nome,
        precoCentavos: item.precoCentavos,
        quantidade: item.quantidade,
      };
    });

    return {
      numero: pedido.numero,
      criadoEm: pedido.criadoEm,
      itens: itens,
      totalCentavos: pedido.totalCentavos,
      pagamento: pedido.formaPagamento,
      status: pedido.status,
    };
  }

  const api = Object.freeze({
    URL_PADRAO,
    urlBase,
    listarBebidas,
    criarPedido,
    alterarStatus,
    disponivel,
    paraRegistro,
  });

  if (typeof module !== "undefined" && module.exports) {
    module.exports = api;
  }

  if (typeof window !== "undefined") {
    window.CafeNaMaoApi = api;
  }
})(typeof globalThis !== "undefined" ? globalThis : this);
