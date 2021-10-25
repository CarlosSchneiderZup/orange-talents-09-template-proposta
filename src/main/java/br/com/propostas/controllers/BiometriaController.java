package br.com.propostas.controllers;

import br.com.propostas.controllers.dtos.NovaUrlDto;
import br.com.propostas.controllers.forms.BiometriaForm;
import br.com.propostas.entidades.Biometria;
import br.com.propostas.entidades.Proposta;
import br.com.propostas.repositorios.BiometriaRepository;
import br.com.propostas.repositorios.PropostaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/biometrias")
public class BiometriaController {

    @Autowired
    private BiometriaRepository biometriaRepository;

    @Autowired
    private PropostaRepository propostaRepository;

    @PostMapping(value = "/{id}")
    public ResponseEntity<NovaUrlDto> cadastrarBiometria(@PathVariable Long id, @RequestBody @Valid BiometriaForm form, UriComponentsBuilder builder) {

        Optional<Proposta> possivelProposta = propostaRepository.findById(id);

        if(possivelProposta.isPresent()) {
            Proposta proposta = possivelProposta.get();

            Biometria biometria = Biometria.montaNovaBiometria(form, proposta);
            biometriaRepository.save(biometria);

            URI uri = builder.path("/biometrias/{id}").buildAndExpand(biometria.getId()).toUri();

            return ResponseEntity.created(uri).body(new NovaUrlDto(biometria.getId(), "biometrias"));
        }
        return ResponseEntity.notFound().build();
    }
}
