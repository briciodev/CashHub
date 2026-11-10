package br.com.cashhub.exception;

public class MovimentacaoNaoEncontradaException
        extends RuntimeException {

    public MovimentacaoNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
