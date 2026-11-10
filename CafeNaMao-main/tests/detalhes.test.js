const test = require("node:test");
const assert = require("node:assert/strict");
const {
  QUANTIDADE_MINIMA,
  QUANTIDADE_MAXIMA,
  validarQuantidade,
  calcularTotal,
} = require("../js/detalhes.js");

test("aceita quantidade dentro do limite", function () {
  assert.equal(validarQuantidade(QUANTIDADE_MINIMA), "");
  assert.equal(validarQuantidade(3), "");
  assert.equal(validarQuantidade(QUANTIDADE_MAXIMA), "");
});

test("rejeita quantidade abaixo do mínimo", function () {
  assert.equal(validarQuantidade(0), "A quantidade mínima é 1.");
  assert.equal(validarQuantidade(-2), "A quantidade mínima é 1.");
});

test("rejeita quantidade acima do máximo", function () {
  assert.equal(validarQuantidade(QUANTIDADE_MAXIMA + 1), "A quantidade máxima é 10.");
});

test("rejeita quantidade fracionária", function () {
  assert.equal(validarQuantidade(2.5), "Informe uma quantidade inteira.");
});

test("rejeita quantidade que não é número", function () {
  assert.equal(validarQuantidade("3"), "Informe uma quantidade inteira.");
  assert.equal(validarQuantidade(NaN), "Informe uma quantidade inteira.");
});

test("calcula o total em centavos", function () {
  assert.equal(calcularTotal(750, 3), 2250);
});

test("calcula o total no limite mínimo", function () {
  assert.equal(calcularTotal(400, QUANTIDADE_MINIMA), 400);
});

test("calcula o total no limite máximo", function () {
  assert.equal(calcularTotal(850, QUANTIDADE_MAXIMA), 8500);
});

test("rejeita quantidade inválida no cálculo", function () {
  assert.throws(function () {
    calcularTotal(400, 0);
  }, RangeError);

  assert.throws(function () {
    calcularTotal(400, QUANTIDADE_MAXIMA + 1);
  }, RangeError);
});

test("rejeita preço inválido no cálculo", function () {
  assert.throws(function () {
    calcularTotal(-1, 1);
  }, TypeError);

  assert.throws(function () {
    calcularTotal(4.5, 1);
  }, TypeError);
});
