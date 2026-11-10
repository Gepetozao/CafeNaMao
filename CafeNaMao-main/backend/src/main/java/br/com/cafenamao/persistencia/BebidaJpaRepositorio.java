package br.com.cafenamao.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Consultas Spring Data para bebidas.
 */
public interface BebidaJpaRepositorio extends JpaRepository<BebidaEntity, String> {

    List<BebidaEntity> findAllByOrderByOrdemAsc();

    @Query("select coalesce(max(b.ordem), -1) + 1 from BebidaEntity b")
    int proximaOrdem();
}
