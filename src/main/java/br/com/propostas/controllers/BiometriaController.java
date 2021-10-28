package br.com.propostas.controllers;

import br.com.propostas.commons.exceptions.ApiErrorException;
import br.com.propostas.controllers.dtos.NovaUrlDto;
import br.com.propostas.controllers.forms.BiometriaForm;
import br.com.propostas.entidades.Biometria;
import br.com.propostas.entidades.Cartao;
import br.com.propostas.repositorios.BiometriaRepository;
import br.com.propostas.repositorios.CartaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/biometrias")
public class BiometriaController {

    @Autowired
    private BiometriaRepository biometriaRepository;

    @Autowired
    private CartaoRepository cartaoRepository;

    @PostMapping(value = "/{id}")
    public ResponseEntity<NovaUrlDto> cadastrarBiometria(@PathVariable Long id, @RequestBody @Valid BiometriaForm form, UriComponentsBuilder builder) {

        Cartao cartao = cartaoRepository.findById(id).orElseThrow(() -> new ApiErrorException("Cartão não encontrado", "Cartão", HttpStatus.NOT_FOUND));

            Biometria biometria = Biometria.montaNovaBiometria(form, cartao);
            biometriaRepository.save(biometria);

            URI uri = builder.path("/biometrias/{id}").buildAndExpand(biometria.getId()).toUri();

            return ResponseEntity.created(uri).body(new NovaUrlDto(biometria.getId(), "biometrias"));
    }
}
