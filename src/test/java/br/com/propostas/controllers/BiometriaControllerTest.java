package br.com.propostas.controllers;

import br.com.propostas.controllers.dtos.RespostaCartao;
import br.com.propostas.controllers.dtos.ResultadoAnalise;
import br.com.propostas.controllers.forms.BiometriaForm;
import br.com.propostas.entidades.Cartao;
import br.com.propostas.repositorios.CartaoRepository;
import br.com.propostas.utils.clients.ConsultaFinanceiro;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class BiometriaControllerTest {

    private String uri = "/biometrias";
    private Cartao cartao;

    private Gson gson = new Gson();


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsultaFinanceiro consultaFinanceiro;

    @Autowired
    private CartaoRepository cartaoRepository;

    @BeforeEach
    void setUp() throws Exception {

        RespostaCartao respostaCartao = new RespostaCartao("4526-3713-8877-6955", LocalDateTime.now().toString(), "Dono do cartão", "1", 8952, null);
        cartao = Cartao.geraCartao(respostaCartao);
        cartaoRepository.save(cartao);

    }

    @Test
    void deveCadastrarUmaNovaBiometriaERetornarStatus201ComOLinkDeRedirect() throws Exception {

        BiometriaForm novaBiometria = new BiometriaForm("SGVsbG8gd29ybGQ=");

        mockMvc.perform(MockMvcRequestBuilders.post(uri + "/" + cartao.getId())
                .content(gson.toJson(novaBiometria))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.redirectedUrlPattern("http://localhost/biometrias/*"));
    }

    @Test
    void naoDeveCadastrarUmaNovaBiometriaComIdDeCartaoInvalidoERetornar404() throws Exception {

        BiometriaForm novaBiometria = new BiometriaForm("SGVsbG8gd29ybGQ=");

        mockMvc.perform(MockMvcRequestBuilders.post(uri + "/1000")
                        .content(gson.toJson(novaBiometria))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void naoDeveCadastrarUmaNovaBiometriaComStringQueNaoEhBase64ERetornarStatus400() throws Exception {

        BiometriaForm novaBiometria = new BiometriaForm("Biometria!");

        mockMvc.perform(MockMvcRequestBuilders.post(uri + "/" + cartao.getId())
                        .content(gson.toJson(novaBiometria))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void naoDeveCadastrarUmaNovaBiometriaValorDeBiometriaNuloERetornar400() throws Exception {

        BiometriaForm novaBiometria = new BiometriaForm(null);

        mockMvc.perform(MockMvcRequestBuilders.post(uri + "/" + cartao.getId())
                        .content(gson.toJson(novaBiometria))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}