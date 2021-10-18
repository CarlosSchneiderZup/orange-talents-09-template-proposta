package br.com.propostas.controllers.dtos;

public class NovaPropostaDto {

    private String url;

    public NovaPropostaDto(Long idProposta) {
        this.url = montaUrlNovaProposta(idProposta);
    }

    private String montaUrlNovaProposta(Long idProposta) {
        return "localhost:8080/propostas/" + idProposta;
    }

	public String getUrl() {
		return url;
	}

}
