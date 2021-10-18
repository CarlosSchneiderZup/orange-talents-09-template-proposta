package br.com.propostas.commons.validators;

import br.com.propostas.controllers.forms.PropostaForm;
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

        if(errors.hasErrors()) {
            return;
        }

        PropostaForm formulario = (PropostaForm) target;

        if(!documentoValido(formulario.getDocumento())) {
            errors.rejectValue("Documento", null, "Formato inválido de documento");
        }
    }

    private boolean documentoValido(String documento) {
        if( documento.length()!= 11) {
            return false;
        }
        return true;
    }
}
