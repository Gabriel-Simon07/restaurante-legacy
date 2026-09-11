package com.restaurante;

/**
 * Representa um item do cardápio ou do pedido.
 * Smell intencional: Primitive Obsession (preço double, categoria String),
 * Nomes ruins de variáveis e Construtor com muitos parâmetros.
 */
public class Item {
    public int id;
    public String nome; // nome do item
    public double precoItem; // preco do item
    public String categoria; // categoria ("PRATO", "BEBIDA", "SOBREMESA")
    public String observacao; // observação (ex: "sem cebola", "ponto da carne")
    public int tempoPreparo; // tempo de preparo estimado
    public boolean pronto;

    // Construtor com muitos parâmetros (Data Clump / Telescoping constructor)
    public Item(int id, String nome, double precoItem, String categoria, String observacao, int tempoPreparo) {
        this.id = id;
        this.nome = nome;
        this.precoItem = precoItem;
        this.categoria = categoria;
        this.observacao = observacao;
        this.tempoPreparo = tempoPreparo;
        this.pronto = false;
    }

    public Item(int id, String nome, double precoItem, String categoria, int tempoPreparo) {
        this(id, nome, precoItem, categoria, "", tempoPreparo);
    }

    // Cria uma cópia com observação personalizada
    public Item comObs(String novaObs) {
        Item copia = new Item(this.id, this.nome, this.precoItem, this.categoria, novaObs, this.tempoPreparo);
        copia.pronto = this.pronto;
        return copia;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public double getPreco() { return precoItem; }
    public String getCategoria() { return categoria; }
    public String getObservacao() { return observacao; }
    public int getTempoPreparo() { return tempoPreparo; }
    public boolean isPronto() { return pronto; }
    public void setPronto(boolean pronto) { this.pronto = pronto; }

    @Override
    public String toString() {
        return nome + " (R$ " + String.format("%.2f", precoItem) + ")" + (observacao != null && !observacao.isEmpty() ? " [Obs: " + observacao + "]" : "");
    }
}
