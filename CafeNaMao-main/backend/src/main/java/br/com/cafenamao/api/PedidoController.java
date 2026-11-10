package br.com.cafenamao.api;

import br.com.cafenamao.api.dto.PedidoRequest;
import br.com.cafenamao.api.dto.PedidoResponse;
import br.com.cafenamao.api.dto.StatusRequest;
import br.com.cafenamao.dominio.Pedido;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Endpoints REST de pedidos.
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> criar(@Valid @RequestBody PedidoRequest request) {
        Pedido pedido = service.criar(request);
        URI localizacao = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{numero}")
                .buildAndExpand(pedido.getNumero())
                .toUri();

        return ResponseEntity.created(localizacao).body(PedidoResponse.de(pedido));
    }

    @GetMapping
    public List<PedidoResponse> listar() {
        return service.listar().stream()
                .map(PedidoResponse::de)
                .toList();
    }

    @GetMapping("/{numero}")
    public PedidoResponse buscar(@PathVariable String numero) {
        return PedidoResponse.de(service.buscar(numero));
    }

    @PatchMapping("/{numero}/status")
    public PedidoResponse alterarStatus(
            @PathVariable String numero,
            @Valid @RequestBody StatusRequest request) {

        return PedidoResponse.de(service.alterarStatus(numero, request));
    }
}
