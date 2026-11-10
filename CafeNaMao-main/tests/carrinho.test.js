const test = require("node:test");
const assert = require("node:assert/strict");
const {
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
} = require("../js/carrinho.js");

const espresso = {
  id: "espresso-curto",
  nome: "Espresso Curto",
  precoCentavos: 400,
};

const cappuccino = {
  id: "cappuccino",
  nome: "Cappuccino",
  precoCentavos: 750,
};

test("carrinho vazio tem total zero", function () {
  const carrinho = criarCarrinhoVazio();

  assert.equal(calcularTotalCarrinho(carrinho), 0);
  assert.equal(contarItens(carrinho), 0);
  assert.ok(carrinhoVazio(carrinho));
});

test("soma o total do carrinho em centavos", function () {
  const carrinho = criarCarrinhoVazio();

  adicionarItem(carrinho, espresso, 2);
  adicionarItem(carrinho, cappuccino, 3);

  assert.equal(calcularTotalCarrinho(carrinho), 2 * 400 + 3 * 750);
  assert.equal(contarItens(carrinho), 5);
});

test("itens repetidos somam quantidade em uma única linha", function () {
  const carrinho = criarCarrinhoVazio();

  adicionarItem(carrinho, espresso, 2);
  adicionarItem(carrinho, espresso, 3);

  assert.equal(carrinho.itens.length, 1);
  assert.equal(carrinho.itens[0].quantidade, 5);
  assert.equal(calcularTotalCarrinho(carrinho), 5 * 400);
});

test("alterar quantidade atualiza o total", function () {
  const carrinho = criarCarrinhoVazio();

  adicionarItem(carrinho, cappuccino, 1);
  alterarQuantidade(carrinho, cappuccino.id, 4);

  assert.equal(carrinho.itens[0].quantidade, 4);
  assert.equal(calcularTotalCarrinho(carrinho), 4 * 750);
});

test("remover item atualiza o total", function () {
  const carrinho = criarCarrinhoVazio();

  adicionarItem(carrinho, espresso, 2);
  adicionarItem(carrinho, cappuccino, 1);

  assert.equal(removerItem(carrinho, espresso.id), true);
  assert.equal(carrinho.itens.length, 1);
  assert.equal(calcularTotalCarrinho(carrinho), 750);
});

test("remover item inexistente não altera o carrinho", function () {
  const carrinho = criarCarrinhoVazio();

  adicionarItem(carrinho, espresso, 1);

  assert.equal(removerItem(carrinho, "nao-existe"), false);
  assert.equal(carrinho.itens.length, 1);
});

test("carrinho vazio bloqueia a conclusão do pedido", function () {
  const carrinho = criarCarrinhoVazio();

  assert.equal(
    validarCarrinhoParaPedido(carrinho),
    "Adicione pelo menos uma bebida antes de concluir o pedido."
  );
  assert.throws(function () {
    criarPedido(carrinho);
  }, RangeError);
});

test("carrinho com itens permite criar pedido com resumo", function () {
  const carrinho = criarCarrinhoVazio();
  const momento = new Date("2026-11-05T12:30:00.000Z");

  adicionarItem(carrinho, espresso, 2);
  adicionarItem(carrinho, cappuccino, 3);

  assert.equal(validarCarrinhoParaPedido(carrinho), "");

  const pedido = criarPedido(carrinho, momento);

  assert.equal(pedido.numero, "PED-" + String(momento.getTime()).slice(-8));
  assert.equal(pedido.criadoEm, momento.toISOString());
  assert.equal(pedido.totalCentavos, 3050);
  assert.equal(pedido.itens.length, 2);
});

test("pedido criado não muda quando o carrinho é alterado", function () {
  const carrinho = criarCarrinhoVazio();

  adicionarItem(carrinho, espresso, 1);
  const pedido = criarPedido(carrinho);

  carrinho.itens[0].quantidade = 9;

  assert.equal(pedido.itens[0].quantidade, 1);
  assert.equal(pedido.totalCentavos, 400);
});

test("rejeita quantidade inválida ao adicionar", function () {
  const carrinho = criarCarrinhoVazio();

  assert.throws(function () {
    adicionarItem(carrinho, espresso, 0);
  }, RangeError);
});

test("rejeita soma acima da quantidade máxima", function () {
  const carrinho = criarCarrinhoVazio();

  adicionarItem(carrinho, espresso, 8);

  assert.throws(function () {
    adicionarItem(carrinho, espresso, 4);
  }, RangeError);
});

test("rejeita alteração de quantidade inválida", function () {
  const carrinho = criarCarrinhoVazio();

  adicionarItem(carrinho, espresso, 1);

  assert.throws(function () {
    alterarQuantidade(carrinho, espresso.id, 11);
  }, RangeError);
});

test("rejeita bebida inválida", function () {
  const carrinho = criarCarrinhoVazio();

  assert.throws(function () {
    adicionarItem(carrinho, { id: "", nome: "", precoCentavos: -1 }, 1);
  }, TypeError);
});

test("salva, recupera e limpa o carrinho do armazenamento", function () {
  const memoria = new Map();

  global.window = {
    localStorage: {
      getItem: function (chave) {
        return memoria.has(chave) ? memoria.get(chave) : null;
      },
      setItem: function (chave, valor) {
        memoria.set(chave, String(valor));
      },
      removeItem: function (chave) {
        memoria.delete(chave);
      },
    },
  };

  try {
    const carrinho = criarCarrinhoVazio();

    adicionarItem(carrinho, cappuccino, 2);
    salvarCarrinho(carrinho);

    const recuperado = carregarCarrinho();

    assert.equal(recuperado.itens.length, 1);
    assert.equal(recuperado.itens[0].nome, "Cappuccino");
    assert.equal(calcularTotalCarrinho(recuperado), 1500);

    limparCarrinhoSalvo();

    assert.ok(carrinhoVazio(carregarCarrinho()));
  } finally {
    delete global.window;
  }
});
