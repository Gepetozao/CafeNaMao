const test = require("node:test");
const assert = require("node:assert/strict");
const { bebidas, formatarPreco } = require("../js/dados.js");

test("o catálogo possui pelo menos seis bebidas", function () {
  assert.ok(bebidas.length >= 6);
});

test("todos os identificadores de bebida são únicos", function () {
  const identificadores = bebidas.map(function (bebida) {
    return bebida.id;
  });

  assert.equal(new Set(identificadores).size, bebidas.length);
});

test("todas as bebidas possuem dados obrigatórios", function () {
  bebidas.forEach(function (bebida) {
    assert.match(bebida.id, /\S/);
    assert.match(bebida.nome, /\S/);
    assert.match(bebida.descricao, /\S/);
    assert.match(bebida.categoria, /\S/);
  });
});

test("todos os preços são inteiros positivos em centavos", function () {
  bebidas.forEach(function (bebida) {
    assert.ok(Number.isInteger(bebida.precoCentavos));
    assert.ok(bebida.precoCentavos > 0);
  });
});

test("formata centavos como moeda brasileira", function () {
  assert.equal(formatarPreco(400), "R$ 4,00");
  assert.equal(formatarPreco(755), "R$ 7,55");
});

test("formata preço zero no limite inferior", function () {
  assert.equal(formatarPreco(0), "R$ 0,00");
});

test("formata preço com separador de milhar", function () {
  assert.equal(formatarPreco(123456), "R$ 1.234,56");
});

test("rejeita preço negativo", function () {
  assert.throws(function () {
    formatarPreco(-1);
  }, TypeError);
});

test("rejeita preço fracionário em centavos", function () {
  assert.throws(function () {
    formatarPreco(4.5);
  }, TypeError);
});

test("rejeita preço que não é número", function () {
  assert.throws(function () {
    formatarPreco("400");
  }, TypeError);
});
