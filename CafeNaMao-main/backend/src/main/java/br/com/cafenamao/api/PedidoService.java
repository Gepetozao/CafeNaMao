package br.com.cafenamao.api;

import br.com.cafenamao.api.dto.PedidoRequest;
import br.com.cafenamao.api.dto.StatusRequest;
import br.com.cafenamao.api.erro.RecursoNaoEncontradoException;
import br.com.cafenamao.api.erro.RequisicaoInvalidaException;
import br.com.cafenamao.dominio.FormaPagamento;
import br.com.cafenamao.dominio.ItemPedido;
import br.com.cafenamao.dominio.Pedido;
import br.com.cafenamao.dominio.StatusPedido;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Regras de aplicação para os pedidos.
 */
@Service
public class PedidoService {

    private final PedidoRepositorio repositorio;
    private final BebidaService bebidaService;

    public PedidoService(PedidoRepositorio repositorio, BebidaService bebidaService) {
        this.repositorio = repositorio;
        this.bebidaService = bebidaService;
    }

    public Pedido criar(PedidoRequest request) {
        FormaPagamento formaPagamento = FormaPagamento.deCodigo(request.formaPagamento())
                .orElseThrow(() -> new RequisicaoInvalidaException(
                        "Forma de pagamento inválida: '" + request.formaPagamento()
                                + "'. Use 'dinheiro' ou 'cartao-ficticio'."));

        List<ItemPedido> itens = request.itens().stream()
                .map(item -> new ItemPedido(
                        bebidaService.buscar(item.bebidaId()),
                        item.quantidade()))
                .toList();

        Pedido pedido = Pedido.novo(itens, formaPagamento);
        return repositorio.salvar(pedido);
    }

    public List<Pedido> listar() {
        return repositorio.listar();
    }

    public Pedido buscar(String numero) {
        return repositorio.buscarPorNumero(numero)
                .orElseThrow(() -> naoEncontrado(numero));
    }

    public Pedido alterarStatus(String numero, StatusRequest request) {
        StatusPedido status = StatusPedido.deCodigo(request.status())
                .orElseThrow(() -> new RequisicaoInvalidaException(
                        "Status inválido: '" + request.status()
                                + "'. Use 'recebido', 'em-preparo' ou 'finalizado'."));

        Pedido pedido = buscar(numero);
        pedido.alterarStatus(status);
        return repositorio.salvar(pedido);
    }

    private RecursoNaoEncontradoException naoEncontrado(String numero) {
        return new RecursoNaoEncontradoException(
                "Pedido não encontrado para o número '" + numero + "'.");
    }
}
