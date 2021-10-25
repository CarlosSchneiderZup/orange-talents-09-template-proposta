package br.com.propostas.controllers.dtos;

public class NovaUrlDto {

    private String url;

    public NovaUrlDto(Long id, String caminho) {

        this.url = montaUrlNovaProposta(id, caminho);
    }

    private String montaUrlNovaProposta(Long id, String caminho) {
        return "localhost:8080/" + caminho +"/" + id;
    }

	public String getUrl() {
		return url;
	}

}
