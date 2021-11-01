package br.com.propostas.entidades;

import br.com.propostas.commons.exceptions.ApiErrorException;
import br.com.propostas.entidades.enums.AvaliacaoFinanceira;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PropostaTest {

    @Test
    void deveFicarComStatusElegivelAoReceberUmaAvaliacaoFinanceiraSemRestricao() {
        Proposta proposta = new Proposta();
        proposta.setAvaliacaoFinanceira("SEM_RESTRICAO");

        assertEquals(AvaliacaoFinanceira.ELEGIVEL, proposta.getAvaliacaoFinanceira());
    }

    @Test
    void deveFicarComStatusInelegivelAoReceberUmaAvaliacaoFinanceiraComRestricao() {
        Proposta proposta = new Proposta();
        proposta.setAvaliacaoFinanceira("COM_RESTRICAO");

        assertEquals(AvaliacaoFinanceira.NAO_ELEGIVEL, proposta.getAvaliacaoFinanceira());
    }

    @Test
    void deveLancarUmaExceptionAoReceberUmaAvaliacaoFinanceiraForaDoPadrao() {
        Proposta proposta = new Proposta();


        assertThrows(ApiErrorException.class, () -> proposta.setAvaliacaoFinanceira("EH_COMPLICADO"));
    }
}