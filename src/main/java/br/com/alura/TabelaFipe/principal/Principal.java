package br.com.alura.TabelaFipe.principal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import br.com.alura.TabelaFipe.model.Dados;
import br.com.alura.TabelaFipe.model.Modelos;
import br.com.alura.TabelaFipe.model.Veiculo;
import br.com.alura.TabelaFipe.service.ConsumoApi;
import br.com.alura.TabelaFipe.service.ConverteDados;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    
    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";
    
    public void exibeMenu() {
        var menu = """
            
            *** OPÇÕES ***
            1 - Para Carros
            2 - Para Motos
            3 - Para Caminhões
            
            Digite uma das opções (1, 2 ou 3) para consulta:            
            """;

        String opcao = "";

        while (!Arrays.asList("1", "2", "3").contains(opcao)) {
            System.out.println(menu);
            opcao = leitura.nextLine();  
        }
        
        String endereco = "";
        
        switch (Integer.parseInt(opcao)) {
            case 1: endereco = URL_BASE + "carros/marcas";
                    break;
            case 2: endereco = URL_BASE + "motos/marcas";
                    break;
            case 3: endereco = URL_BASE + "caminhoes/marcas";
                    break;
        }       
        
        var json = consumo.obterDados(endereco);
        var marcas = conversor.obterlista(json, Dados.class);
       
        marcas.stream()
                .sorted (Comparator.comparing(Dados::CodigoInt))
                .forEach(System.out::println);
        
        System.out.println("\nInforme o código da marca para consulta:");
        var codigoMarca = leitura.nextLine();
        
        endereco += "/" + codigoMarca + "/modelos";       

        json = consumo.obterDados(endereco);
        var modeloLista = conversor.obterDados(json, Modelos.class);

        System.out.println("\nModelos dessa marca: ");
        modeloLista.modelos()
            .stream()
            .sorted(Comparator.comparing(Dados::CodigoInt))
            .forEach(System.out::println);

       System.out.println("Digite o nome do carro a ser buscado: "); 
       var nomeVeiculo = leitura.nextLine();    

       List<Dados> modelosFiltrados = modeloLista.modelos()
                                                 .stream()
                                                 .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                                                 .collect(Collectors.toList());

       System.out.println("\nModelos encontrados");
       modelosFiltrados.forEach(System.out::println);    
       
       System.out.println("Digite o código do modelo para buscar os valores de avaliação.");
       var codigoModelo = leitura.nextLine();

       endereco += "/" + codigoModelo + "/anos";
       json = consumo.obterDados(endereco);

        List<Dados> anos = conversor.obterlista(json, Dados.class);
        List<Veiculo> veiculos = new ArrayList<>();

        for(int i = 0; i < anos.size(); i++){
            var enderecoAnos = endereco + "/" + anos.get(i).codigo();
            json = consumo.obterDados(enderecoAnos);

            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("\nTodos os veículos filtrados com avaliação por ano.");
        veiculos.forEach(System.out::println);

    }
}
