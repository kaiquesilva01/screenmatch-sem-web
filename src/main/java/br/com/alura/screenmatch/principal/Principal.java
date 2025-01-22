package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoAPI;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private final Scanner sc = new Scanner(System.in);
    private final ConsumoAPI consumo = new ConsumoAPI();
    private final ConverteDados converteDados = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";

    public void exibeMenu() {

        System.out.println("Digite o nome da série para busca");
        String nomeDaSerie = sc.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeDaSerie.replace(" ", "+") + API_KEY);

        DadosSerie dadosSerie = converteDados.obterDados(json, DadosSerie.class);
        System.out.println(dadosSerie);

        		List<DadosTemporada> temporada = new ArrayList<>();

		for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
			json = consumo.obterDados(ENDERECO + nomeDaSerie.replace(" ", "+") + "&season=" + i + API_KEY);
			DadosTemporada dadosTemporada = converteDados.obterDados(json, DadosTemporada.class);
			temporada.add(dadosTemporada);
		}
		temporada.forEach(System.out::println);
        temporada.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        List<DadosEpisodio> dadosEpisodios = temporada.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());
        System.out.println("Top 5 episódios");
        dadosEpisodios.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                .limit(5)
                .forEach(System.out::println);

        List<Episodio> episodios = temporada.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d)))
                .collect(Collectors.toList());

        episodios.forEach(System.out::println);

        System.out.println("Digite um trecho do titulo do episodio");
        var trechoEpisodio = sc.nextLine();
        Optional<Episodio> episodioBuscado = episodios.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(trechoEpisodio.toUpperCase()))
                .findFirst();
        if (episodioBuscado.isPresent()) {
            System.out.println("Episodio encontrado!");
            System.out.println("Temporada: " + episodioBuscado.get().getTemporada());
        }
        else {
            System.out.println("Episodio nao encontrado!");
        }
        System.out.println("A partir de que ano voce deseja ver os episodios?");
        var ano = sc.nextInt();
        sc.nextLine();

        LocalDate dataBusca = LocalDate.of(ano,1,1);


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        episodios.stream()
                .filter(episodio -> episodio.getDataLancamento() != null && episodio.getDataLancamento().isAfter(dataBusca))
                .forEach(episodio -> {
                    System.out.printf("""
                            Temporada: %s
                            Episodio: %s
                            Data de Lancamento: %s
                            %n""", episodio.getTemporada(),episodio.getNumeroEpisodio(),episodio.getDataLancamento().format(formatter));
                });
    }
}
