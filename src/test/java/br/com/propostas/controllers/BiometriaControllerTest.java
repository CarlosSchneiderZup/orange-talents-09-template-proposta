package br.com.propostas.controllers;

import br.com.propostas.controllers.dtos.ResultadoAnalise;
import br.com.propostas.controllers.forms.BiometriaForm;
import br.com.propostas.controllers.forms.PropostaForm;
import br.com.propostas.entidades.Proposta;
import br.com.propostas.repositorios.PropostaRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class BiometriaControllerTest {

    private String uri = "/biometrias";
    private Proposta proposta;

    private Gson gson = new Gson();


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsultaFinanceiro consultaFinanceiro;

    @Autowired
    private PropostaRepository propostaRepository;

    @BeforeEach
    void setUp() throws Exception {

        PropostaForm novaProposta = new PropostaForm("joao@gmail.com", "Lucas João Mattos", "85393222009",
                "Rua do Comércio, s/n", 33000.00);

        ResponseEntity<ResultadoAnalise> response = ResponseEntity.status(HttpStatus.CREATED).body(new ResultadoAnalise("85393222009", "Lucas João Mattos", "SEM_RESTRICAO", "1"));
        Mockito.when(consultaFinanceiro.solicitarConsulta(Mockito.any())).thenReturn(response);


        mockMvc.perform(MockMvcRequestBuilders.post("/propostas").content(gson.toJson(novaProposta))
                        .contentType(MediaType.APPLICATION_JSON));

        proposta = propostaRepository.findByDocumento(novaProposta.getDocumento()).get();
    }

    @Test
    void deveCadastrarUmaNovaBiometriaERetornarStatus201ComOLinkDeRedirect() throws Exception {

        BiometriaForm novaBiometria = new BiometriaForm("SGVsbG8gd29ybGQ=");

        mockMvc.perform(MockMvcRequestBuilders.post(uri + "/" + proposta.getId())
                .content(gson.toJson(novaBiometria))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.redirectedUrlPattern("http://localhost/biometrias/*"));
    }

    @Test
    void naoDeveCadastrarUmaNovaBiometriaComIdDePropostaInvalidoERetornar404() throws Exception {

        BiometriaForm novaBiometria = new BiometriaForm("SGVsbG8gd29ybGQ=");

        mockMvc.perform(MockMvcRequestBuilders.post(uri + "/1000")
                        .content(gson.toJson(novaBiometria))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void naoDeveCadastrarUmaNovaBiometriaComStringQueNaoEhBase64ERetornarStatus400() throws Exception {

        BiometriaForm novaBiometria = new BiometriaForm("Biometria!");

        mockMvc.perform(MockMvcRequestBuilders.post(uri + "/" + proposta.getId())
                        .content(gson.toJson(novaBiometria))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void naoDeveCadastrarUmaNovaBiometriaValorDeBiometriaNuloERetornar400() throws Exception {

        BiometriaForm novaBiometria = new BiometriaForm(null);

        mockMvc.perform(MockMvcRequestBuilders.post(uri + "/" + proposta.getId())
                        .content(gson.toJson(novaBiometria))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}