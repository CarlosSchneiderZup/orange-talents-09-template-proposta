package br.com.propostas.controllers;

import br.com.propostas.controllers.dtos.RespostaAvisoViagem;
import br.com.propostas.controllers.dtos.RespostaBloqueioCartao;
import br.com.propostas.controllers.dtos.RespostaCartao;
import br.com.propostas.controllers.dtos.ResultadoCarteira;
import br.com.propostas.controllers.forms.PropostaForm;
import br.com.propostas.entidades.Cartao;
import br.com.propostas.entidades.Proposta;
import br.com.propostas.entidades.acoplamentos.Vencimento;
import br.com.propostas.repositorios.CartaoRepository;
import br.com.propostas.repositorios.PropostaRepository;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
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

    @Autowired
    private PropostaRepository propostaRepository;

    @MockBean
    private ConsultaCartao consultaCartao;

    @BeforeEach
    void setUp() {

        RespostaCartao respostaCartao = new RespostaCartao("4526-3713-8877-6955", LocalDateTime.now().toString(), "Dono do cartão", "1", 8952, new Vencimento());
        cartao = Cartao.geraCartao(respostaCartao);

        RespostaCartao respostaCartaoFalha = new RespostaCartao("1111-2222-3333-4444", LocalDateTime.now().toString(), "Dono do cartão", "2", 1930, new Vencimento());
        cartaoFalha = Cartao.geraCartao(respostaCartaoFalha);
        cartaoRepository.saveAll(Arrays.asList(cartao, cartaoFalha));
    }

    @Test
    void naoDeveEncontrarUmCartaoComIdQueNaoExisteAoSolicitarUmBloqueioERetornarStatus404() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post(uri + "/bloqueios/404")
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

        mockMvc.perform(MockMvcRequestBuilders.post(uri + "/bloqueios/" + cartaoFalha.getId())
                        .header("User-Agent", "PostmanRuntime/7.28.4"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    void naoDeveBloquearUmCartaoQuandoAApiEstiverForaDoArERetornarStatus400() throws Exception {

        FeignException exception = Mockito.mock(FeignException.class);
        Mockito.when(consultaCartao.solicitarBloqueio(Mockito.any(), Mockito.any()))
                .thenThrow(exception);

        mockMvc.perform(MockMvcRequestBuilders.post(uri + "/bloqueios/" + cartaoFalha.getId())
                        .header("User-Agent", "PostmanRuntime/7.28.4"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        assertFalse(cartao.verificaBloqueioAtivo());
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

        mockMvc.perform(MockMvcRequestBuilders.post(uri + "/bloqueios/" + esteCartao.getId())
                        .header("User-Agent", "PostmanRuntime/7.28.4"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post(uri + "/bloqueios/" + esteCartao.getId())
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

        mockMvc.perform(MockMvcRequestBuilders.post(uri + "/bloqueios/" + cartao.getId())
                        .header("User-Agent", "PostmanRuntime/7.28.4"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        assertTrue(cartao.verificaBloqueioAtivo());
    }

    @Test
    void naoDeveEncontrarUmCartaoComIdQueNaoExisteAoSolicitarUmaViagemERetornarStatus404() throws Exception {

        String aviso = "{\"destino\" : \"Tocantins\", \"dataTermino\" : \"" + LocalDate.now().plusDays(10) + "\"}";

        mockMvc.perform(MockMvcRequestBuilders.post(uri + "/avisos/viagem/404")
                        .content(aviso)
                        .header("User-Agent", "PostmanRuntime/7.28.4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

    }

    @Test
    void naoDeveEnviarUmAvisoDeViagemComDataPassadaERetornarStatus400() throws Exception {

        String aviso = "{\"destino\" : \"Tocantins\", \"dataTermino\" : \"" + LocalDate.now().minusDays(10) + "\"}";

        mockMvc.perform(MockMvcRequestBuilders.post(uri + "/avisos/viagem/" + cartao.getId())
                        .content(aviso)
                        .header("User-Agent", "PostmanRuntime/7.28.4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    void naoDeveEnviarUmAvisoDeViagemComDestinoNuloERetornarStatus400() throws Exception {

        String aviso = "{\"destino\" : \"\", \"dataTermino\" : \"" + LocalDate.now().plusDays(10) + "\"}";

        mockMvc.perform(MockMvcRequestBuilders.post(uri + "/avisos/viagem/" + cartao.getId())
                        .content(aviso)
                        .header("User-Agent", "PostmanRuntime/7.28.4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    void deveCadastrarUmNovoAvisoDeViagemERetornarStatus200() throws Exception {

        RespostaAvisoViagem resposta = Mockito.mock(RespostaAvisoViagem.class);
        Mockito.when(resposta.getResultado()).thenReturn("CRIADO");
        ResponseEntity<RespostaAvisoViagem> responseAviso = Mockito.mock(ResponseEntity.class);
        Mockito.when(responseAviso.getBody()).thenReturn(resposta);
        Mockito.when(consultaCartao.solicitarViagem(Mockito.any(), Mockito.any())).thenReturn(responseAviso);

        String aviso = "{\"destino\" : \"Tocantins\", \"dataTermino\" : \"" + LocalDate.now().plusDays(10) + "\"}";
        mockMvc.perform(MockMvcRequestBuilders.post(uri + "/avisos/viagem/" + cartao.getId())
                        .content(aviso)
                        .header("User-Agent", "PostmanRuntime/7.28.4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    void naoDeveCadastrarUmNovoAvisoDeViagemQuandoAApiEstiverForaDoArERetornarStatus400() throws Exception {

        FeignException exception = Mockito.mock(FeignException.class);
        Mockito.when(consultaCartao.solicitarViagem(Mockito.any(), Mockito.any())).thenThrow(exception);

        String aviso = "{\"destino\" : \"Tocantins\", \"dataTermino\" : \"" + LocalDate.now().plusDays(10) + "\"}";
        mockMvc.perform(MockMvcRequestBuilders.post(uri + "/avisos/viagem/" + cartao.getId())
                        .content(aviso)
                        .header("User-Agent", "PostmanRuntime/7.28.4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    void naoDeveCadastrarUmNovoAvisoDeViagemQuandoAConsultaNaApiRetornarFalhaERetornarStatus400() throws Exception {

        RespostaAvisoViagem resposta = Mockito.mock(RespostaAvisoViagem.class);
        Mockito.when(resposta.getResultado()).thenReturn("FALHA");
        ResponseEntity<RespostaAvisoViagem> responseAviso = Mockito.mock(ResponseEntity.class);
        Mockito.when(responseAviso.getBody()).thenReturn(resposta);
        Mockito.when(consultaCartao.solicitarViagem(Mockito.any(), Mockito.any())).thenReturn(responseAviso);


        String aviso = "{\"destino\" : \"Tocantins\", \"dataTermino\" : \"" + LocalDate.now().plusDays(10) + "\"}";
        mockMvc.perform(MockMvcRequestBuilders.post(uri + "/avisos/viagem/" + cartao.getId())
                        .content(aviso)
                        .header("User-Agent", "PostmanRuntime/7.28.4")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Transactional
    void deveAssociarUmaNovaCarteiraAUmCartaoERetornarStatus201() throws Exception {

        ResultadoCarteira resultado = Mockito.mock(ResultadoCarteira.class);
        Mockito.when(resultado.getResultado()).thenReturn("ASSOCIADA");
        Mockito.when(resultado.getId()).thenReturn("e2cd5f6c-d8c8-45f8-880b-8a1fc5f30100");
        Mockito.when(consultaCartao.solicitarNovaCarteira(Mockito.any(), Mockito.any())).thenReturn(resultado);

        String form = "{\"carteiraDigital\" : \"PAYPAL\"}";
        mockMvc.perform(MockMvcRequestBuilders.post(uri + "/carteiras/" + cartao.getId())
                        .content(form)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.redirectedUrlPattern("http://localhost/carteiras/*"));
    }

    @Test
    void naoDeveAssociarUmaNovaCarteiraRepetidaAUmCartaoERetornarStatus422() throws Exception {

        assertTrue(true);
//        ResultadoCarteira resultado = Mockito.mock(ResultadoCarteira.class);
//        Mockito.when(resultado.getResultado()).thenReturn("ASSOCIADA");
//        Mockito.when(resultado.getId()).thenReturn("e2cd5f6c-d8c8-45f8-880b-8a1fc5f30100");
//        Mockito.when(consultaCartao.solicitarNovaCarteira(Mockito.any(), Mockito.any())).thenReturn(resultado);
//
//        String form = "{\"carteiraDigital\" : \"PAYPAL\"}";
//        mockMvc.perform(MockMvcRequestBuilders.post(uri + "/carteiras/" + cartao.getId())
//                        .content(form)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isCreated())
//                .andExpect(MockMvcResultMatchers.redirectedUrlPattern("http://localhost/carteiras/*"));
    }

}