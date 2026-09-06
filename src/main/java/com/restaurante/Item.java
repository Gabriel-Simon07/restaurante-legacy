package com.restaurante;

/**
 * Representa um item do cardápio ou do pedido.
 * Smell intencional: Primitive Obsession (preço double, categoria String),
 * Nomes ruins de variáveis e Construtor com muitos parâmetros.
 */
public class Item {
    public int id;
    public String n; // nome do item
    public double p; // preco do item
    public String c; // categoria ("PRATO", "BEBIDA", "SOBREMESA")
    public String obs; // observação (ex: "sem cebola", "ponto da carne")
    public int tempPrep; // tempo de preparo estimado
    public boolean pronto;

    // Construtor com muitos parâmetros (Data Clump / Telescoping constructor)
    public Item(int id, String n, double p, String c, String obs, int tempPrep) {
        this.id = id;
        this.n = n;
        this.p = p;
        this.c = c;
        this.obs = obs;
        this.tempPrep = tempPrep;
        this.pronto = false;
    }

    public Item(int id, String n, double p, String c, int tempPrep) {
        this(id, n, p, c, "", tempPrep);
    }

    // Cria uma cópia com observação personalizada
    public Item comObs(String novaObs) {
        Item copia = new Item(this.id, this.n, this.p, this.c, novaObs, this.tempPrep);
        copia.pronto = this.pronto;
        return copia;
    }

    public int getId() { return id; }
    public String getNome() { return n; }
    public double getPreco() { return p; }
    public String getCategoria() { return c; }
    public String getObs() { return obs; }
    public int getTempPrep() { return tempPrep; }
    public boolean isPronto() { return pronto; }
    public void setPronto(boolean pronto) { this.pronto = pronto; }

    @Override
    public String toString() {
        return n + " (R$ " + String.format("%.2f", p) + ")" + (obs != null && !obs.isEmpty() ? " [Obs: " + obs + "]" : "");
    }
}
