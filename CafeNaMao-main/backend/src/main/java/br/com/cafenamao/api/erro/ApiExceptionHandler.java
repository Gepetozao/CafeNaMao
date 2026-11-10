package br.com.cafenamao.api.erro;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Converte exceções da API em respostas {@link ProblemDetail}, no formato
 * definido pela RFC 9457.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ProblemDetail tratarNaoEncontrado(RecursoNaoEncontradoException erro) {
        return problema(HttpStatus.NOT_FOUND, "Recurso não encontrado",
                erro.getMessage(), "nao-encontrado");
    }

    @ExceptionHandler(ConflitoException.class)
    public ProblemDetail tratarConflito(ConflitoException erro) {
        return problema(HttpStatus.CONFLICT, "Conflito",
                erro.getMessage(), "conflito");
    }

    @ExceptionHandler(RequisicaoInvalidaException.class)
    public ProblemDetail tratarRequisicaoInvalida(RequisicaoInvalidaException erro) {
        return problema(HttpStatus.BAD_REQUEST, "Requisição inválida",
                erro.getMessage(), "requisicao-invalida");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail tratarValidacao(MethodArgumentNotValidException erro) {
        ProblemDetail problema = problema(HttpStatus.BAD_REQUEST, "Requisição inválida",
                "Um ou mais campos são inválidos.", "validacao");

        Map<String, String> erros = new LinkedHashMap<>();
        erro.getBindingResult().getFieldErrors().forEach(campo ->
                erros.putIfAbsent(campo.getField(), campo.getDefaultMessage()));
        problema.setProperty("erros", erros);

        return problema;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail tratarCorpoInvalido(HttpMessageNotReadableException erro) {
        return problema(HttpStatus.BAD_REQUEST, "Requisição inválida",
                "O corpo da requisição é inválido ou está ausente.", "corpo-invalido");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail tratarArgumentoInvalido(IllegalArgumentException erro) {
        return problema(HttpStatus.BAD_REQUEST, "Requisição inválida",
                erro.getMessage(), "argumento-invalido");
    }

    private ProblemDetail problema(HttpStatus status, String titulo, String detalhe, String tipo) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(status, detalhe);
        problema.setTitle(titulo);
        problema.setType(URI.create("urn:cafenamao:erro:" + tipo));
        return problema;
    }
}
