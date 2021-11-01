package br.com.propostas.entidades;

import br.com.propostas.controllers.dtos.RespostaCartao;
import br.com.propostas.controllers.dtos.ResultadoCarteira;
import br.com.propostas.entidades.acoplamentos.Vencimento;
import br.com.propostas.entidades.enums.CarteiraDigitalCadastrada;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class CartaoTest {

    private Cartao cartao;

    @BeforeEach
    void setUpBeforeClass() {
        RespostaCartao respostaCartao = new RespostaCartao("5502-3060-9910-2518", LocalDateTime.now().toString(), "João dos testes", "10", 4590, new Vencimento());
        cartao = Cartao.geraCartao(respostaCartao);
    }

    @Test
    void deveVerificarQueUmNovoCartaovemSemBloqueioAtivo() {
        assertFalse(cartao.verificaBloqueioAtivo());
    }

    @Test
    void deveVerificarQueUmCartaoFicaBloqueadoAposASolicitacaoDeBloqueio() {
        cartao.bloquear();

        assertTrue(cartao.verificaBloqueioAtivo());
    }

    @Test
    void deveRetornarFalsoSeOCartaoNaoTemCarteirasAssociadasAEle() {
        assertFalse(cartao.verificaDuplicidadeDeCarteira(CarteiraDigitalCadastrada.PAYPAL));
    }

    @Test
    void deveRetornarFalsoSeOCartaoNaoTemUmaCarteiraDoFornecedorSolicitado() {
        ResultadoCarteira resultadoCarteira = Mockito.mock(ResultadoCarteira.class);
        Mockito.when(resultadoCarteira.getId()).thenReturn("bb7f54ce-1a85-4ac1-b445-195f8db6f752");
        CarteiraDigital carteiraDigital = CarteiraDigital.montaCarteiraDigital(cartao, CarteiraDigitalCadastrada.PAYPAL, resultadoCarteira);
        cartao.adicionaNovaCarteira(carteiraDigital);

        assertFalse(cartao.verificaDuplicidadeDeCarteira(CarteiraDigitalCadastrada.SAMSUNG_PAY));
    }

    @Test
    void deveRetornarVerdadeiroSeOCartaoJaPossuiUmaCarteiraDoFornecedorSolicitado() {
        ResultadoCarteira resultadoCarteira = Mockito.mock(ResultadoCarteira.class);
        Mockito.when(resultadoCarteira.getId()).thenReturn("bb7f54ce-1a85-4ac1-b445-195f8db6f752");
        CarteiraDigital carteiraDigital = CarteiraDigital.montaCarteiraDigital(cartao, CarteiraDigitalCadastrada.PAYPAL, resultadoCarteira);
        cartao.adicionaNovaCarteira(carteiraDigital);

        assertTrue(cartao.verificaDuplicidadeDeCarteira(CarteiraDigitalCadastrada.PAYPAL));
    }
}