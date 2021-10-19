package br.com.propostas.controllers;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.google.gson.Gson;

import br.com.propostas.controllers.forms.PropostaForm;
import br.com.propostas.entidades.Proposta;
import br.com.propostas.repositorios.PropostaRepository;

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

	@Test
	void deveCadastrarUmaNovaPropostaERetornarStatus201() throws Exception {

		PropostaForm novaProposta = new PropostaForm("joao@gmail.com", "Lucas João", "85393222009",
				"Rua dos Lirios, 1000", 45000.00);

		mockMvc.perform(MockMvcRequestBuilders.post(uri).content(gson.toJson(novaProposta))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isCreated());
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
				"Rua dos Oleiro, 109", 5000.00);

		mockMvc.perform(MockMvcRequestBuilders.post(uri).content(gson.toJson(novaProposta))
				.contentType(MediaType.APPLICATION_JSON)).andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
	}

}
