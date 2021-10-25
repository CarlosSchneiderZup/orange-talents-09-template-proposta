package br.com.propostas.controllers;

import br.com.propostas.controllers.dtos.PropostaDto;
import br.com.propostas.controllers.dtos.ResultadoAnalise;
import br.com.propostas.controllers.forms.PropostaForm;
import br.com.propostas.entidades.Proposta;
import br.com.propostas.entidades.enums.AvaliacaoFinanceira;
import br.com.propostas.repositorios.PropostaRepository;
import br.com.propostas.utils.clients.ConsultaFinanceiro;
import com.google.gson.Gson;
import feign.FeignException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PropostaControllerTest {

    private String uri = "/propostas";

    private Gson gson = new Gson();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PropostaRepository propostaRepository;

    @MockBean
    private ConsultaFinanceiro consultaFinanceiro;

    @Test
    void deveCadastrarUmaNovaPropostaERetornarStatus201ComLinkDeRedirect() throws Exception {

        PropostaForm novaProposta = new PropostaForm("joao@gmail.com", "Lucas João", "85393222009",
                "Rua dos Lirios, 1000", 45000.00);

        ResponseEntity<ResultadoAnalise> response = ResponseEntity.status(HttpStatus.CREATED).body(new ResultadoAnalise("85393222009", "Lucas João", "SEM_RESTRICAO", "1"));
        Mockito.when(consultaFinanceiro.solicitarConsulta(Mockito.any())).thenReturn(response);


        mockMvc.perform(MockMvcRequestBuilders.post(uri).content(gson.toJson(novaProposta))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.redirectedUrlPattern("http://localhost/propostas/*"));
    }

    @Test
    void deveCadastrarUmaNovaPropostaComDocumentoElegivelEVerificarQueODocumentoEhElegivel() throws Exception {

        PropostaForm propostaElegivel = new PropostaForm("robson@gmail.com", "Robson Souza Cruz", "03594839000193", "Av das Industrias, 1000", 300000.00);

        ResponseEntity<ResultadoAnalise> response = ResponseEntity.status(HttpStatus.CREATED).body(new ResultadoAnalise("03594839000193", "Robson Souza Cruz", "SEM_RESTRICAO", "1"));
        Mockito.when(consultaFinanceiro.solicitarConsulta(Mockito.any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post(uri).content(gson.toJson(propostaElegivel))
                .contentType(MediaType.APPLICATION_JSON));

        Proposta propostaSalva = propostaRepository.findByDocumento(propostaElegivel.getDocumento()).get();

        assertEquals(AvaliacaoFinanceira.ELEGIVEL, propostaSalva.getAvaliacaoFinanceira());
    }

    @Test
    void deveCadastrarUmaNovaPropostaComDocumentoNaoElegivelEVerificarQueODocumentoEhNaoElegivel() throws Exception {

        PropostaForm propostaInelegivel = new PropostaForm("crsouza10@terra.com.br", "Christian Ruiz Souza", "31052336019", "Rua das lavandas, 39", 4000.50);

        ResponseEntity<ResultadoAnalise> response = ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ResultadoAnalise("31052336019", "Christian Ruiz Souza", "COM_RESTRICAO", "1"));
        Mockito.when(consultaFinanceiro.solicitarConsulta(Mockito.any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post(uri).content(gson.toJson(propostaInelegivel))
                .contentType(MediaType.APPLICATION_JSON));

        Proposta propostaSalva = propostaRepository.findByDocumento(propostaInelegivel.getDocumento()).get();

        assertEquals(AvaliacaoFinanceira.NAO_ELEGIVEL, propostaSalva.getAvaliacaoFinanceira());
    }

    @Test
    void deveCadastrarUmaNovaPropostaComServicoIndisponivelEVerificarQueOProcessoEstaEmAnalise() throws Exception {

        PropostaForm propostaInelegivel = new PropostaForm("adrianog@gmail.com", "Adriano Gotuzzo", "48580731062", "Rua das lavandas, 39", 4000.50);
        FeignException mock = Mockito.mock(FeignException.class);
        Mockito.when(consultaFinanceiro.solicitarConsulta(Mockito.any())).thenThrow(mock);

        mockMvc.perform(MockMvcRequestBuilders.post(uri).content(gson.toJson(propostaInelegivel))
                .contentType(MediaType.APPLICATION_JSON));

        Proposta propostaSalva = propostaRepository.findByDocumento(propostaInelegivel.getDocumento()).get();

        assertEquals(AvaliacaoFinanceira.EM_ANALISE, propostaSalva.getAvaliacaoFinanceira());
    }

    @Test
    void naoDeveCadastrarUmaNovaPropostaComEmailEmFormatoInvalidoERetornarStatus400() throws Exception {

        PropostaForm novaProposta = new PropostaForm("joao.gmail.com", "Lucas João", "11491431000114",
                "Rua dos Lirios, 1000", 45000.00);

        mockMvc.perform(MockMvcRequestBuilders.post(uri).content(gson.toJson(novaProposta))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void naoDeveCadastrarUmaNovaPropostaComEnderecoEmBrancoERetornarStatus400() throws Exception {

        PropostaForm novaProposta = new PropostaForm("joao@gmail.com", "Lucas João", "11491431000114",
                "", 45000.00);

        mockMvc.perform(MockMvcRequestBuilders.post(uri).content(gson.toJson(novaProposta))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());

    }

    @Test
    void naoDeveCadastrarUmaNovaPropostaComDocumentoComMenosCaracteresERetornarStatus400() throws Exception {

        PropostaForm novaProposta = new PropostaForm("joao@gmail.com", "Lucas João", "1234",
                "Rua dos Lirios, 1000", 70000.00);

        mockMvc.perform(MockMvcRequestBuilders.post(uri).content(gson.toJson(novaProposta))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void naoDeveCadastrarUmaNovaPropostaComCpfInvalidoERetornarStatus400() throws Exception {

        PropostaForm novaProposta = new PropostaForm("joao@gmail.com", "Lucas João", "12312312399",
                "Rua dos Lirios, 1000", 3000.00);

        mockMvc.perform(MockMvcRequestBuilders.post(uri).content(gson.toJson(novaProposta))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void naoDeveCadastrarUmaNovaPropostaComSaldoZeradoERetornarStatus400() throws Exception {

        PropostaForm novaProposta = new PropostaForm("joao@gmail.com", "Lucas João", "11491431000114",
                "Rua dos Lirios, 1000", 0.00);

        mockMvc.perform(MockMvcRequestBuilders.post(uri).content(gson.toJson(novaProposta))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void naoDeveCadastrarUmaNovaPropostaParaDocumentoJaRegistradoERetornarStatus422() throws Exception {

        PropostaForm propostaForm = new PropostaForm("carlos@gmail.com", "Carlos Reis", "80063309050", "Rua Olaria 10", 1.99);
        Proposta proposta = Proposta.montaPropostaValida(propostaForm, propostaRepository);
        propostaRepository.save(proposta);

        PropostaForm novaProposta = new PropostaForm("carlos@gmail.com", "Carlos G. R. Schneider", "80063309050",
                "Rua dos Oleiros, 109", 5000.00);

        mockMvc.perform(MockMvcRequestBuilders.post(uri).content(gson.toJson(novaProposta))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }

    @Test
    void deveBuscarUmaPropostaCadastradaERetornarStatus200ComDadosOfuscados() throws Exception {

        PropostaForm novaProposta = new PropostaForm("tatipapelaria@gmail.com", "Tatiane Viegas", "48380450000183", "Rua das hortencias, 150", 95000.90);

        ResponseEntity<ResultadoAnalise> response = ResponseEntity.status(HttpStatus.CREATED).body(new ResultadoAnalise("48380450000183", "Tatiane Viegas", "SEM_RESTRICAO", "1"));
        Mockito.when(consultaFinanceiro.solicitarConsulta(Mockito.any())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post(uri).content(gson.toJson(novaProposta))
                .contentType(MediaType.APPLICATION_JSON));

        Proposta propostaSalva = propostaRepository.findByDocumento(novaProposta.getDocumento()).get();

        PropostaDto visualizacaoProposta = propostaSalva.montaPropostaDto();

        mockMvc.perform(MockMvcRequestBuilders.get(uri + "/" + propostaSalva.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(gson.toJson(visualizacaoProposta)));
    }

    @Test
    void deveBuscarUmaPropostaComIdInexistenteERetornarStatus404() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get(uri + "/ + 404" )
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
