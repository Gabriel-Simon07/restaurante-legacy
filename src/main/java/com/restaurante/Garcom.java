package com.restaurante;

/**
 * Representa um garçom.
 * Smell intencional: Acoplamento rígido direto (Observer Smell).
 */
public class Garcom {
    public int id;
    public String nome;
    public int notificacoesRecebidas = 0;

    public Garcom(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }

    // Método chamado diretamente pela cozinha de forma síncrona e fortemente acoplada
    public void avisarPratoPronto(int mesaNum, String nomeItem) {
        this.notificacoesRecebidas++;
        System.out.println("[NOTIFICACAO GARCOM " + nome + "]: Item '" + nomeItem + "' para a Mesa " + mesaNum + " esta pronto para entrega!");
    }
}
