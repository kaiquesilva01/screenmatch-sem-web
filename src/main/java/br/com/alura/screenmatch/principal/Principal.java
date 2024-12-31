package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.service.ConsumoAPI;

import java.util.Scanner;

public class Principal {
    private final Scanner sc = new Scanner(System.in);
    private final ConsumoAPI consumo = new ConsumoAPI();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    public void exibeMenu() {

        System.out.println("Digite o nome da série para busca");
        String nomeDaSerie = sc.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeDaSerie.replace(" ", "+") + API_KEY);
    //https://www.omdbapi.com/?t=gilmore+girls&apikey=6585022c
    }
}
