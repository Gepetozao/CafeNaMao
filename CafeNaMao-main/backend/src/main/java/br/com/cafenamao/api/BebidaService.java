package br.com.cafenamao.api;

import br.com.cafenamao.api.dto.BebidaRequest;
import br.com.cafenamao.api.erro.ConflitoException;
import br.com.cafenamao.api.erro.RecursoNaoEncontradoException;
import br.com.cafenamao.api.erro.RequisicaoInvalidaException;
import br.com.cafenamao.dominio.Bebida;
import br.com.cafenamao.dominio.Preco;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Regras de aplicação para o catálogo de bebidas.
 */
@Service
public class BebidaService {

    private final BebidaRepositorio repositorio;

    public BebidaService(BebidaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<Bebida> listar() {
        return repositorio.listar();
    }

    public Bebida buscar(String id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> naoEncontrada(id));
    }

    public Bebida criar(BebidaRequest request) {
        String id = idInformadoOuGerado(request);

        if (repositorio.existePorId(id)) {
            throw new ConflitoException(
                    "Já existe uma bebida com o identificador '" + id + "'.");
        }

        return repositorio.salvar(montar(id, request));
    }

    public Bebida atualizar(String id, BebidaRequest request) {
        buscar(id);

        if (request.id() != null && !request.id().isBlank() && !request.id().equals(id)) {
            throw new RequisicaoInvalidaException(
                    "O identificador do corpo não pode ser diferente do identificador da URL.");
        }

        return repositorio.salvar(montar(id, request));
    }

    public void remover(String id) {
        if (!repositorio.remover(id)) {
            throw naoEncontrada(id);
        }
    }

    private String idInformadoOuGerado(BebidaRequest request) {
        if (request.id() != null && !request.id().isBlank()) {
            return request.id().trim();
        }

        return Slug.de(request.nome());
    }

    private Bebida montar(String id, BebidaRequest request) {
        if (request.precoCentavos() == null) {
            throw new RequisicaoInvalidaException("O preço em centavos é obrigatório.");
        }

        return new Bebida(
                id,
                request.nome(),
                request.descricao(),
                request.categoria(),
                Preco.deCentavos(request.precoCentavos()));
    }

    private RecursoNaoEncontradoException naoEncontrada(String id) {
        return new RecursoNaoEncontradoException(
                "Bebida não encontrada para o identificador '" + id + "'.");
    }
}
