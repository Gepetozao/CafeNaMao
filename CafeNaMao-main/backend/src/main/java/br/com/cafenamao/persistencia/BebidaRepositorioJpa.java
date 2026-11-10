package br.com.cafenamao.persistencia;

import br.com.cafenamao.api.BebidaRepositorio;
import br.com.cafenamao.catalogo.CatalogoBebidas;
import br.com.cafenamao.dominio.Bebida;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador JPA do repositório de bebidas.
 *
 * <p>O serviço continua dependendo da interface {@link BebidaRepositorio}; a
 * troca do armazenamento em memória pelo banco fica isolada aqui.</p>
 */
@Repository
@Transactional(readOnly = true)
public class BebidaRepositorioJpa implements BebidaRepositorio {

    private final BebidaJpaRepositorio jpa;
    private final CatalogoBebidas catalogo;

    public BebidaRepositorioJpa(BebidaJpaRepositorio jpa, CatalogoBebidas catalogo) {
        this.jpa = jpa;
        this.catalogo = catalogo;
    }

    @Override
    public List<Bebida> listar() {
        return jpa.findAllByOrderByOrdemAsc().stream()
                .map(BebidaMapper::paraDominio)
                .toList();
    }

    @Override
    public Optional<Bebida> buscarPorId(String id) {
        return jpa.findById(id).map(BebidaMapper::paraDominio);
    }

    @Override
    public boolean existePorId(String id) {
        return jpa.existsById(id);
    }

    @Override
    @Transactional
    public Bebida salvar(Bebida bebida) {
        Optional<BebidaEntity> existente = jpa.findById(bebida.id());

        BebidaEntity entity = existente.orElseGet(BebidaEntity::new);
        if (existente.isEmpty()) {
            entity.setOrdem(jpa.proximaOrdem());
        }

        BebidaMapper.copiarParaEntidade(bebida, entity);
        jpa.save(entity);
        return bebida;
    }

    @Override
    @Transactional
    public boolean remover(String id) {
        if (!jpa.existsById(id)) {
            return false;
        }

        jpa.deleteById(id);
        return true;
    }

    @Override
    @Transactional
    public void reiniciar() {
        jpa.deleteAllInBatch();

        int ordem = 0;
        for (Bebida bebida : catalogo.listar()) {
            jpa.save(BebidaMapper.paraNovaEntidade(bebida, ordem++));
        }
    }
}
