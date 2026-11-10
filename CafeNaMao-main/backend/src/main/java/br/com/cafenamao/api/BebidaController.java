package br.com.cafenamao.api;

import br.com.cafenamao.api.dto.BebidaRequest;
import br.com.cafenamao.api.dto.BebidaResponse;
import br.com.cafenamao.dominio.Bebida;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Endpoints REST de bebidas.
 */
@RestController
@RequestMapping("/api/bebidas")
public class BebidaController {

    private final BebidaService service;

    public BebidaController(BebidaService service) {        this.service = service;
    }

    @GetMapping
    public List<BebidaResponse> listar() {
        return service.listar().stream()
                .map(BebidaResponse::de)
                .toList();
    }

    @GetMapping("/{id}")
    public BebidaResponse buscar(@PathVariable String id) {
        return BebidaResponse.de(service.buscar(id));
    }

    @PostMapping
    public ResponseEntity<BebidaResponse> criar(@Valid @RequestBody BebidaRequest request) {
        Bebida bebida = service.criar(request);
        URI localizacao = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(bebida.id())
                .toUri();

        return ResponseEntity.created(localizacao).body(BebidaResponse.de(bebida));
    }

    @PutMapping("/{id}")
    public BebidaResponse atualizar(
            @PathVariable String id,
            @Valid @RequestBody BebidaRequest request) {

        return BebidaResponse.de(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable String id) {
        service.remover(id);
    }
}
