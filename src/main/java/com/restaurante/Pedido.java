package com.restaurante;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa a comanda / pedido de uma mesa.
 * Smells intencionais:
 * - State Smell (status por inteiros soltos e flags booleanas espalhadas)
 * - Feature Envy (classe de faturamento lê getters para fazer todas as contas)
 * - Primitive Obsession (status como int, mesa como int)
 */
public class Pedido {
    public static final int STATUS_RECEBIDO = 1;
    public static final int STATUS_EM_PREPARO = 2;
    public static final int STATUS_PRONTO = 3;
    public static final int STATUS_ENTREGUE = 4;
    public static final int STATUS_PAGO = 5;

    public int id;
    public int m1; // numero da mesa (nome ruim)
    public String cli; // nome do cliente (nome ruim)
    public int p_st; // status do pedido (nome ruim)
    public boolean cancelado;
    public boolean pago;
    public boolean vip;
    public boolean paraViagem;
    public Garcom garcom;
    public List<Item> itens;
    public String obsGeral;

    public Pedido(int id, int m1, String cli, boolean vip, boolean paraViagem, String obsGeral, Garcom garcom) {
        this.id = id;
        this.m1 = m1;
        this.cli = cli;
        this.vip = vip;
        this.paraViagem = paraViagem;
        this.obsGeral = obsGeral;
        this.garcom = garcom;
        this.p_st = STATUS_RECEBIDO;
        this.cancelado = false;
        this.pago = false;
        this.itens = new ArrayList<>();
    }

    public void adicionarItem(Item item) {
        this.itens.add(item);
    }

    public int getId() { return id; }
    public int getMesa() { return m1; }
    public String getCliente() { return cli; }
    public int getStatus() { return p_st; }
    public void setStatus(int st) { this.p_st = st; }
    public boolean isCancelado() { return cancelado; }
    public void setCancelado(boolean cancelado) { this.cancelado = cancelado; }
    public boolean isPago() { return pago; }
    public void setPago(boolean pago) { this.pago = pago; }
    public boolean isVip() { return vip; }
    public boolean isParaViagem() { return paraViagem; }
    public Garcom getGarcom() { return garcom; }
    public List<Item> getItens() { return itens; }
    public String getObsGeral() { return obsGeral; }

    public String getDescricaoStatus() {
        if (cancelado) return "CANCELADO";
        switch (p_st) {
            case STATUS_RECEBIDO: return "RECEBIDO";
            case STATUS_EM_PREPARO: return "EM_PREPARO";
            case STATUS_PRONTO: return "PRONTO";
            case STATUS_ENTREGUE: return "ENTREGUE";
            case STATUS_PAGO: return "PAGO";
            default: return "DESCONHECIDO";
        }
    }
}
