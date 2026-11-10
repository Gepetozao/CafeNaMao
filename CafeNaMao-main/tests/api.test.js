const test = require("node:test");
const assert = require("node:assert/strict");
const {
  URL_PADRAO,
  urlBase,
  listarBebidas,
  paraRegistro,
} = require("../js/api.js");

const fetchOriginal = globalThis.fetch;

const pedidoApi = {
  numero: "PED-12345678",
  criadoEm: "2026-11-09T22:00:00.123",
  itens: [
    {
      bebidaId: "cappuccino",
      nome: "Cappuccino",
      quantidade: 2,
      precoCentavos: 750,
      subtotalCentavos: 1500,
    },
    {
      bebidaId: "espresso-curto",
      nome: "Espresso Curto",
      quantidade: 1,
      precoCentavos: 400,
      subtotalCentavos: 400,
    },
  ],
  formaPagamento: "dinheiro",
  formaPagamentoRotulo: "Dinheiro",
  status: "recebido",
  statusRotulo: "Recebido",
  totalCentavos: 1900,
};

test.afterEach(function () {
  delete globalThis.CAFE_NA_MAO_API_URL;
  globalThis.fetch = fetchOriginal;
});

test("converte o pedido da API para o formato do histórico", function () {
  const registro = paraRegistro(pedidoApi);

  assert.deepEqual(registro, {
    numero: "PED-12345678",
    criadoEm: "2026-11-09T22:00:00.123",
    itens: [
      {
        id: "cappuccino",
        nome: "Cappuccino",
        precoCentavos: 750,
        quantidade: 2,
      },
      {
        id: "espresso-curto",
        nome: "Espresso Curto",
        precoCentavos: 400,
        quantidade: 1,
      },
    ],
    totalCentavos: 1900,
    pagamento: "dinheiro",
    status: "recebido",
  });
});

test("rejeita pedido da API sem itens", function () {
  const invalido = Object.assign({}, pedidoApi, { itens: [] });

  assert.throws(function () {
    paraRegistro(invalido);
  }, /pedido inválido/);
});

test("rejeita item da API com preço fracionário", function () {
  const invalido = {
    numero: "PED-1",
    criadoEm: "2026-11-09T22:00:00",
    itens: [
      {
        bebidaId: "cappuccino",
        nome: "Cappuccino",
        quantidade: 1,
        precoCentavos: 750.5,
      },
    ],
    formaPagamento: "dinheiro",
    status: "recebido",
    totalCentavos: 750,
  };

  assert.throws(function () {
    paraRegistro(invalido);
  }, /item de pedido inválido/);
});

test("usa a URL padrão quando nada foi configurado", function () {
  assert.equal(urlBase(), URL_PADRAO);
});

test("aceita a URL configurada e remove a barra final", function () {
  globalThis.CAFE_NA_MAO_API_URL = "http://192.168.0.10:8080/api/";

  assert.equal(urlBase(), "http://192.168.0.10:8080/api");
});

test("valida e devolve o catálogo recebido da API", async function () {
  globalThis.fetch = async function () {
    return {
      ok: true,
      status: 200,
      json: async function () {
        return [
          {
            id: "cappuccino",
            nome: "Cappuccino",
            descricao: "Café com leite.",
            categoria: "Café",
            precoCentavos: 750,
          },
        ];
      },
    };
  };

  const bebidas = await listarBebidas();

  assert.deepEqual(bebidas, [
    {
      id: "cappuccino",
      nome: "Cappuccino",
      descricao: "Café com leite.",
      categoria: "Café",
      precoCentavos: 750,
    },
  ]);
});

test("marca falha de rede como sem rede para permitir o modo local", async function () {
  globalThis.fetch = async function () {
    throw new TypeError("fetch failed");
  };

  await assert.rejects(listarBebidas(), function (erro) {
    return erro.semRede === true;
  });
});

test("preserva o status quando a API recusa a requisição", async function () {
  globalThis.fetch = async function () {
    return {
      ok: false,
      status: 404,
      json: async function () {
        return { detail: "Bebida não encontrada." };
      },
    };
  };

  await assert.rejects(listarBebidas(), function (erro) {
    return (
      erro.status === 404 &&
      erro.semRede === undefined &&
      erro.message === "Bebida não encontrada."
    );
  });
});
