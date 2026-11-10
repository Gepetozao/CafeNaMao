(function (raiz) {
  "use strict";

  const QUANTIDADE_MINIMA = 1;
  const QUANTIDADE_MAXIMA = 10;

  function validarQuantidade(quantidade) {
    if (!Number.isInteger(quantidade)) {
      return "Informe uma quantidade inteira.";
    }

    if (quantidade < QUANTIDADE_MINIMA) {
      return "A quantidade mínima é " + QUANTIDADE_MINIMA + ".";
    }

    if (quantidade > QUANTIDADE_MAXIMA) {
      return "A quantidade máxima é " + QUANTIDADE_MAXIMA + ".";
    }

    return "";
  }

  function calcularTotal(precoCentavos, quantidade) {
    if (!Number.isInteger(precoCentavos) || precoCentavos < 0) {
      throw new TypeError("O preço deve ser um número inteiro de centavos maior ou igual a zero.");
    }

    const erro = validarQuantidade(quantidade);

    if (erro) {
      throw new RangeError(erro);
    }

    return precoCentavos * quantidade;
  }

  const api = Object.freeze({
    QUANTIDADE_MINIMA,
    QUANTIDADE_MAXIMA,
    validarQuantidade,
    calcularTotal,
  });

  if (typeof module !== "undefined" && module.exports) {
    module.exports = api;
  }

  if (typeof window !== "undefined") {
    window.CafeNaMaoDetalhes = api;
  }
})(typeof globalThis !== "undefined" ? globalThis : this);
