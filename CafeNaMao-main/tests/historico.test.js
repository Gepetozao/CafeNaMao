const test = require("node:test");
const assert = require("node:assert/strict");
const {
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
} = require("../js/historico.js");

const pedidoBase = {
  numero: "PED-00000001",
  criadoEm: "2026-11-06T12:00:00.000Z",
  itens: [
    {
      id: "espresso-curto",
      nome: "Espresso Curto",
      precoCentavos: 400,
      quantidade: 2,
    },
    {
      id: "cappuccino",
      nome: "Cappuccino",
      precoCentavos: 750,
      quantidade: 1,
    },
  ],
  totalCentavos: 1550,
};

const segundoPedido = {
  numero: "PED-00000002",
  criadoEm: "2026-11-06T13:00:00.000Z",
  itens: [
    {
      id: "cafe-com-leite",
      nome: "Café com Leite",
      precoCentavos: 650,
      quantidade: 1,
    },
  ],
  totalCentavos: 650,
};

function instalarArmazenamentoFake() {
  const memoria = new Map();
  const armazenamento = {
    getItem: function (chave) {
      return memoria.has(chave) ? memoria.get(chave) : null;
    },
    setItem: function (chave, valor) {
      memoria.set(chave, String(valor));
    },
    removeItem: function (chave) {
      memoria.delete(chave);
    },
  };

  global.window = { localStorage: armazenamento };
  return memoria;
}

test.afterEach(function () {
  delete global.window;
});

test("cria registro com pagamento e status recebido por padrão", function () {
  const registro = criarRegistroPedido(pedidoBase, PAGAMENTOS.DINHEIRO);

  assert.equal(registro.pagamento, PAGAMENTOS.DINHEIRO);
  assert.equal(registro.status, STATUS.RECEBIDO);
  assert.equal(registro.numero, pedidoBase.numero);
  assert.equal(registro.totalCentavos, 1550);
  assert.notEqual(registro.itens, pedidoBase.itens);
});

test("cria registro com status informado", function () {
  const registro = criarRegistroPedido(
    pedidoBase,
    PAGAMENTOS.CARTAO_FICTICIO,
    STATUS.FINALIZADO
  );

  assert.equal(registro.pagamento, PAGAMENTOS.CARTAO_FICTICIO);
  assert.equal(registro.status, STATUS.FINALIZADO);
});

test("rejeita forma de pagamento inválida", function () {
  assert.throws(function () {
    criarRegistroPedido(pedidoBase, "cartao-real");
  }, /pagamento/i);
});

test("rejeita status inválido", function () {
  assert.throws(function () {
    criarRegistroPedido(pedidoBase, PAGAMENTOS.DINHEIRO, "cancelado");
  }, /status/i);
});

test("rejeita pedido cujo total não corresponde aos itens", function () {
  assert.throws(function () {
    criarRegistroPedido(
      Object.assign({}, pedidoBase, { totalCentavos: 999 }),
      PAGAMENTOS.DINHEIRO
    );
  }, /total/i);
});

test("rejeita item com quantidade fora do limite", function () {
  assert.throws(function () {
    criarRegistroPedido(
      Object.assign({}, pedidoBase, {
        itens: [
          {
            id: "espresso-curto",
            nome: "Espresso Curto",
            precoCentavos: 400,
            quantidade: 11,
          },
        ],
        totalCentavos: 4400,
      }),
      PAGAMENTOS.DINHEIRO
    );
  }, /item inválido/i);
});

test("carrega histórico vazio quando não há armazenamento", function () {
  assert.deepEqual(carregarHistorico(), []);
});

test("carrega histórico vazio quando a chave não existe", function () {
  instalarArmazenamentoFake();

  assert.deepEqual(carregarHistorico(), []);
});

test("salva e recupera um pedido do histórico", function () {
  instalarArmazenamentoFake();
  const registro = criarRegistroPedido(pedidoBase, PAGAMENTOS.DINHEIRO);

  assert.equal(salvarPedido(registro), true);

  const historico = carregarHistorico();

  assert.equal(historico.length, 1);
  assert.deepEqual(historico[0], registro);
});

test("mantém o pedido mais recente primeiro", function () {
  instalarArmazenamentoFake();

  salvarPedido(criarRegistroPedido(pedidoBase, PAGAMENTOS.DINHEIRO));
  salvarPedido(criarRegistroPedido(segundoPedido, PAGAMENTOS.CARTAO_FICTICIO));

  const historico = carregarHistorico();

  assert.equal(historico.length, 2);
  assert.equal(historico[0].numero, segundoPedido.numero);
  assert.equal(historico[1].numero, pedidoBase.numero);
});

test("salvar o mesmo número substitui o registro anterior", function () {
  instalarArmazenamentoFake();

  salvarPedido(criarRegistroPedido(pedidoBase, PAGAMENTOS.DINHEIRO));
  salvarPedido(
    criarRegistroPedido(
      pedidoBase,
      PAGAMENTOS.CARTAO_FICTICIO,
      STATUS.EM_PREPARO
    )
  );

  const historico = carregarHistorico();

  assert.equal(historico.length, 1);
  assert.equal(historico[0].pagamento, PAGAMENTOS.CARTAO_FICTICIO);
  assert.equal(historico[0].status, STATUS.EM_PREPARO);
});

test("altera o status de um pedido existente", function () {
  instalarArmazenamentoFake();
  salvarPedido(criarRegistroPedido(pedidoBase, PAGAMENTOS.DINHEIRO));

  assert.equal(alterarStatus(pedidoBase.numero, STATUS.EM_PREPARO), true);
  assert.equal(carregarHistorico()[0].status, STATUS.EM_PREPARO);
});

test("não altera status inválido nem pedido inexistente", function () {
  instalarArmazenamentoFake();
  salvarPedido(criarRegistroPedido(pedidoBase, PAGAMENTOS.DINHEIRO));

  assert.equal(alterarStatus(pedidoBase.numero, "cancelado"), false);
  assert.equal(alterarStatus("PED-inexistente", STATUS.FINALIZADO), false);
  assert.equal(carregarHistorico()[0].status, STATUS.RECEBIDO);
});

test("ignora conteúdo inválido no armazenamento", function () {
  const memoria = instalarArmazenamentoFake();

  memoria.set(CHAVE_HISTORICO, "{conteúdo inválido");
  assert.deepEqual(carregarHistorico(), []);

  memoria.set(CHAVE_HISTORICO, JSON.stringify({ pedidos: [] }));
  assert.deepEqual(carregarHistorico(), []);
});

test("filtra registros corrompidos e números duplicados", function () {
  const memoria = instalarArmazenamentoFake();
  const registro = criarRegistroPedido(pedidoBase, PAGAMENTOS.DINHEIRO);

  memoria.set(
    CHAVE_HISTORICO,
    JSON.stringify([
      registro,
      Object.assign({}, registro, { status: STATUS.EM_PREPARO }),
      Object.assign({}, registro, { status: "cancelado" }),
      Object.assign({}, registro, { numero: "PED-00000003" }),
    ])
  );

  const historico = carregarHistorico();

  assert.equal(historico.length, 2);
  assert.deepEqual(
    historico.map(function (pedido) {
      return pedido.numero;
    }).sort(),
    [pedidoBase.numero, "PED-00000003"]
  );

  const original = historico.find(function (pedido) {
    return pedido.numero === pedidoBase.numero;
  });

  assert.equal(original.status, STATUS.RECEBIDO);
});

test("limpa o histórico salvo", function () {
  const memoria = instalarArmazenamentoFake();
  salvarPedido(criarRegistroPedido(pedidoBase, PAGAMENTOS.DINHEIRO));

  assert.equal(memoria.has(CHAVE_HISTORICO), true);
  assert.equal(limparHistorico(), true);
  assert.equal(memoria.has(CHAVE_HISTORICO), false);
  assert.deepEqual(carregarHistorico(), []);
});

test("retorna falha quando o armazenamento não está disponível", function () {
  const registro = criarRegistroPedido(pedidoBase, PAGAMENTOS.DINHEIRO);

  assert.equal(salvarPedido(registro), false);
  assert.equal(alterarStatus(pedidoBase.numero, STATUS.EM_PREPARO), false);
  assert.equal(limparHistorico(), false);
});

test("valida status e formas de pagamento conhecidas", function () {
  assert.equal(statusValido(STATUS.RECEBIDO), true);
  assert.equal(statusValido(STATUS.EM_PREPARO), true);
  assert.equal(statusValido(STATUS.FINALIZADO), true);
  assert.equal(statusValido("cancelado"), false);

  assert.equal(pagamentoValido(PAGAMENTOS.DINHEIRO), true);
  assert.equal(pagamentoValido(PAGAMENTOS.CARTAO_FICTICIO), true);
  assert.equal(pagamentoValido("cartao-real"), false);
});

test("devolve rótulos em português para status e pagamento", function () {
  assert.equal(rotuloStatus(STATUS.RECEBIDO), "Recebido");
  assert.equal(rotuloStatus(STATUS.EM_PREPARO), "Em preparo");
  assert.equal(rotuloStatus(STATUS.FINALIZADO), "Finalizado");
  assert.equal(rotuloStatus("cancelado"), "");

  assert.equal(rotuloPagamento(PAGAMENTOS.DINHEIRO), "Dinheiro");
  assert.equal(rotuloPagamento(PAGAMENTOS.CARTAO_FICTICIO), "Cartão fictício");
  assert.equal(rotuloPagamento("cartao-real"), "");
});
