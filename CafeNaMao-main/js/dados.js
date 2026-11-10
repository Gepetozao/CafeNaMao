(function (raiz) {
  "use strict";

  const formatadorPreco = new Intl.NumberFormat("pt-BR", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });

  const bebidas = Object.freeze([
    Object.freeze({
      id: "espresso-curto",
      nome: "Espresso Curto",
      descricao: "Café intenso e encorpado, preparado em dose curta.",
      categoria: "Café",
      precoCentavos: 400,
    }),
    Object.freeze({
      id: "cafe-com-leite",
      nome: "Café com Leite",
      descricao: "Espresso suave com leite vaporizado e cremoso.",
      categoria: "Café",
      precoCentavos: 550,
    }),
    Object.freeze({
      id: "cappuccino",
      nome: "Cappuccino",
      descricao: "Café, leite vaporizado e uma camada generosa de espuma.",
      categoria: "Café",
      precoCentavos: 750,
    }),
    Object.freeze({
      id: "mocha",
      nome: "Mocha",
      descricao: "Espresso com chocolate, leite e finalização cremosa.",
      categoria: "Café",
      precoCentavos: 850,
    }),
    Object.freeze({
      id: "chocolate-quente",
      nome: "Chocolate Quente",
      descricao: "Chocolate dissolvido lentamente em leite quente.",
      categoria: "Chocolate",
      precoCentavos: 700,
    }),
    Object.freeze({
      id: "cha-de-canela",
      nome: "Chá de Canela",
      descricao: "Infusão aromática com canela e um toque de mel.",
      categoria: "Chá",
      precoCentavos: 500,
    }),
  ]);

  function formatarPreco(centavos) {
    if (!Number.isInteger(centavos) || centavos < 0) {
      throw new TypeError("O preço deve ser um número inteiro de centavos maior ou igual a zero.");
    }

    return `R$ ${formatadorPreco.format(centavos / 100)}`;
  }

  const api = Object.freeze({
    bebidas,
    formatarPreco,
  });

  if (typeof module !== "undefined" && module.exports) {
    module.exports = api;
  }

  if (typeof window !== "undefined") {
    window.CafeNaMaoDados = api;
  }
})(typeof globalThis !== "undefined" ? globalThis : this);
