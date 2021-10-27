package br.com.propostas.controllers;

import br.com.propostas.controllers.dtos.RespostaBloqueioCartao;
import br.com.propostas.controllers.dtos.RespostaCartao;
import br.com.propostas.entidades.Cartao;
import br.com.propostas.repositorios.CartaoRepository;
import br.com.propostas.utils.clients.ConsultaCartao;
import com.google.gson.Gson;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class CartaoControllerTest {

    private String uri = "/cartoes";
    private Cartao cartao;
    private Cartao cartaoFalha;

    private Gson gson = new Gson();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CartaoRepository cartaoRepository;

    @MockBean
    private ConsultaCartao consultaCartao;


    @BeforeEach
    void setUp() {

        RespostaCartao respostaCartao = new RespostaCartao("4526-3713-8877-6955", LocalDateTime.now().toString(), "Dono do cartão", "1", 8952, null);
        cartao = Cartao.geraCartao(respostaCartao);

        RespostaCartao respostaCartaoFalha = new RespostaCartao("1111-2222-3333-4444", LocalDateTime.now().toString(), "Dono do cartão", "2", 1930, null);
        cartaoFalha = Cartao.geraCartao(respostaCartaoFalha);
        cartaoRepository.saveAll(Arrays.asList(cartao, cartaoFalha));

    }

    @Test
    void naoDeveEncontrarUmCartaoComIdQueNaoExisteERetornarStatus404() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put(uri + "/bloqueios/404")
                        .header("User-Agent", "PostmanRuntime/7.28.4"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void naoDeveBloquearUmCartaoQuandoARespostaDaApiExternaEhFalhaERetornarStatus400() throws Exception {

        RespostaBloqueioCartao resposta = Mockito.mock(RespostaBloqueioCartao.class);
        Mockito.when(resposta.getResultado()).thenReturn("FALHA");
        ResponseEntity<RespostaBloqueioCartao> responseBloqueio = Mockito.mock(ResponseEntity.class);
        Mockito.when(responseBloqueio.getBody()).thenReturn(resposta);

        Mockito.when(consultaCartao.solicitarBloqueio(Mockito.any(), Mockito.any()))
                .thenReturn(responseBloqueio);

        mockMvc.perform(MockMvcRequestBuilders.put(uri + "/bloqueios/" + cartaoFalha.getId())
                        .header("User-Agent", "PostmanRuntime/7.28.4"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    void naoDeveBloquearUmCartaoQuandoAApiEstiverForaDoArERetornarStatus400() throws Exception {

        FeignException exception = Mockito.mock(FeignException.class);
        Mockito.when(consultaCartao.solicitarBloqueio(Mockito.any(), Mockito.any()))
                .thenThrow(exception);

        mockMvc.perform(MockMvcRequestBuilders.put(uri + "/bloqueios/" + cartao.getId())
                        .header("User-Agent", "PostmanRuntime/7.28.4"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    void naoDeveBloquearUmCartaoQueJaPossuiBloqueioAtivoERetornarStatus422() throws Exception {

        RespostaCartao novoCartao = new RespostaCartao("1111-2222-3333-4444", LocalDateTime.now().toString(), "Dono do cartão", "2", 1930, null);
        Cartao esteCartao = Cartao.geraCartao(novoCartao);
        cartaoRepository.save(esteCartao);

        RespostaBloqueioCartao resposta = Mockito.mock(RespostaBloqueioCartao.class);
        Mockito.when(resposta.getResultado()).thenReturn("BLOQUEADO");
        ResponseEntity<RespostaBloqueioCartao> responseBloqueio = Mockito.mock(ResponseEntity.class);
        Mockito.when(responseBloqueio.getBody()).thenReturn(resposta);

        Mockito.when(consultaCartao.solicitarBloqueio(Mockito.any(), Mockito.any()))
                .thenReturn(responseBloqueio);

        mockMvc.perform(MockMvcRequestBuilders.put(uri + "/bloqueios/" + esteCartao.getId())
                        .header("User-Agent", "PostmanRuntime/7.28.4"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.put(uri + "/bloqueios/" + esteCartao.getId())
                        .header("User-Agent", "PostmanRuntime/7.28.4"))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());

    }

    @Test
    void deveBloquearUmCartaoQueNaoTemBloqueiosPendentesERetornarStatus200() throws Exception {

        RespostaBloqueioCartao resposta = Mockito.mock(RespostaBloqueioCartao.class);
        Mockito.when(resposta.getResultado()).thenReturn("BLOQUEADO");
        ResponseEntity<RespostaBloqueioCartao> responseBloqueio = Mockito.mock(ResponseEntity.class);
        Mockito.when(responseBloqueio.getBody()).thenReturn(resposta);

        Mockito.when(consultaCartao.solicitarBloqueio(Mockito.any(), Mockito.any()))
                .thenReturn(responseBloqueio);

        mockMvc.perform(MockMvcRequestBuilders.put(uri + "/bloqueios/" + cartao.getId())
                        .header("User-Agent", "PostmanRuntime/7.28.4"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        assertTrue(cartao.verificaBloqueioAtivo());
    }
}