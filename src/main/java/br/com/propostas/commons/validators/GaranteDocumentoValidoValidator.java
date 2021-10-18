package br.com.propostas.commons.validators;

import br.com.propostas.controllers.forms.PropostaForm;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class GaranteDocumentoValidoValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return PropostaForm.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		if (errors.hasErrors()) {
			return;
		}

		PropostaForm formulario = (PropostaForm) target;

		if (!documentoValido(formulario.getDocumento())) {
			errors.rejectValue("Documento", null, "Formato inválido de documento");
		}
	}

	private boolean documentoValido(String documento) {
		List<Integer> tamanhosValidos = Arrays.asList(11, 14);
		if (!tamanhosValidos.contains(documento.length())) {
			return false;
		}
		if (documento.length() == 11) {
			return validaCpf(documento);
		} else {
			return validaCnpj(documento);
		}
	}

	private boolean validaCnpj(String cnpj) {
		char dig13, dig14;
		int sm, i, r, num, peso;

		sm = 0;
		peso = 2;
		for (i = 11; i >= 0; i--) {
			num = (cnpj.charAt(i) - 48);
			sm = sm + (num * peso);
			peso = peso + 1;
			if (peso == 10)
				peso = 2;
		}

		r = sm % 11;
		if ((r == 0) || (r == 1))
			dig13 = '0';
		else
			dig13 = (char) ((11 - r) + 48);

		sm = 0;
		peso = 2;
		for (i = 12; i >= 0; i--) {
			num = (cnpj.charAt(i) - 48);
			sm = sm + (num * peso);
			peso = peso + 1;
			if (peso == 10)
				peso = 2;
		}

		r = sm % 11;
		if ((r == 0) || (r == 1))
			dig14 = '0';
		else
			dig14 = (char) ((11 - r) + 48);

		return (dig13 == cnpj.charAt(12)) && (dig14 == cnpj.charAt(13));
	}

	private boolean validaCpf(String cpf) {

		int digitoVerificador1 = 0;
		int digitoVerificador2 = 0;
		int digitoCPF;
		for (int i = 1; i <= cpf.length() - 2; i++) {
			digitoCPF = Integer.parseInt(cpf.substring(i - 1, i));
			digitoVerificador1 = digitoVerificador1 + (11 - i) * digitoCPF;
			digitoVerificador2 = digitoVerificador2 + (12 - i) * digitoCPF;
		}

		Integer digito1 = 0;
		Integer digito2 = 0;
		int resto = 0;
		resto = (digitoVerificador1 % 11);
		if (resto < 2) {
			digito1 = 0;
		} else {
			digito1 = 11 - resto;
		}
		digitoVerificador2 += 2 * digito1;
		resto = (digitoVerificador2 % 11);
		if (resto < 2) {
			digito2 = 0;
		} else {
			digito2 = 11 - resto;
		}

		return digito1.toString().equals(cpf.substring(9, 10)) && digito2.toString().equals(cpf.substring(10));
	}
}
