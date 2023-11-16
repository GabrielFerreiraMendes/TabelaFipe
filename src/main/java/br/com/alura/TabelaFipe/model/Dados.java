package br.com.alura.TabelaFipe.model;

public record Dados(String codigo, String nome) {
    
    public Integer CodigoInt(){
        return Integer.parseInt(codigo);
    }
}
