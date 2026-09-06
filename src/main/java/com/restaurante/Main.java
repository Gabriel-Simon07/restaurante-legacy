package com.restaurante;

/**
 * Ponto de entrada do sistema.
 * Executa uma simulação ponta a ponta de todo o fluxo operacional do restaurante "Bella Tavola":
 * 1. RF01: Abertura do restaurante, cadastro de garçons e cardápio.
 * 2. RF02: Chegada de clientes e ocupação de mesas.
 * 3. RF03: Lançamento de pedidos completos (pratos, bebidas, sobremesas) com observações.
 * 4. RF04: Linha de produção (Cozinha e Bar) com notificações em tempo real ao garçom.
 * 5. RF05: Entrega dos pedidos na mesa.
 * 6. RF06: Prévia de comanda, aplicação de cupons, taxa de serviço e pagamento (PIX / Dinheiro / Cartão).
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("  SISTEMA DE GESTAO DE RESTAURANTE - BELLA TAVOLA (LEGACY)");
        System.out.println("===============================================================\n");

        // RF01: Configuração do Restaurante
        RestauranteLegado restaurante = new RestauranteLegado("Bella Tavola Trattoria", 10);
        Garcom garcomGiovanni = new Garcom(1, "Giovanni Rossi");
        Garcom garcomMarco = new Garcom(2, "Marco Aurelio");
        restaurante.cadastrarGarcom(garcomGiovanni);
        restaurante.cadastrarGarcom(garcomMarco);

        System.out.println("-> Restaurante aberto com 10 mesas e cardapio inicial carregado.");
        System.out.println("-> Garçons cadastrados: " + garcomGiovanni.getNome() + ", " + garcomMarco.getNome() + "\n");

        // =========================================================================
        // CENÁRIO 1: Mesa 3 - Cliente VIP com pedido completo e pagamento via PIX
        // =========================================================================
        System.out.println("###############################################################");
        System.out.println(" CENÁRIO 1: Atendimento Mesa 3 (Cliente VIP - Maria Silva)");
        System.out.println("###############################################################");

        // RF02: Abertura de Atendimento (Flag arguments galore!)
        String resMesa3 = restaurante.abrirAtendimento(3, "Maria Silva", true, false, 1, 0.0, "Mesa proxima a janela", 1);
        System.out.println("Abertura Mesa 3: " + resMesa3);

        // RF03: Lançamento de itens com observações
        restaurante.adicionarItemAoPedido(3, 1, 1, "Sem pimenta do reino"); // Lasanha Bolonhesa (48.0)
        restaurante.adicionarItemAoPedido(3, 6, 2, "Temperatura ambiente"); // 2x Vinho Tinto (22.0 cada)
        restaurante.adicionarItemAoPedido(3, 7, 1, "Caprichar no cacau"); // Tiramisu (24.0)

        // RF04: Linha de produção (Cozinha e Bar)
        restaurante.enviarBarEPreparar(3);
        restaurante.enviarCozinhaEPreparar(3);

        // RF05: Notificação e Entrega na Mesa
        restaurante.entregarPedidoNaMesa(3);

        // RF06: Prévia da conta e Fechamento com Cupom BELLA20 + PIX
        restaurante.imprimirPreviaComanda(3, "BELLA20");
        String resPgto3 = restaurante.processarTudoEPagar(
                3,       // Mesa
                3,       // Forma: PIX (5% desconto extra)
                0.0,     // Dinheiro entregue (n/a)
                "BELLA20",// Cupom (20% desconto)
                true,    // Aceita taxa de serviço (10%)
                2        // Divisão entre 2 pessoas
        );
        System.out.println("Resultado Fechamento Mesa 3: " + resPgto3);
        System.out.println("Mesa 3 esta ocupada agora? " + restaurante.isMesaOcupada(3) + "\n");

        // =========================================================================
        // CENÁRIO 2: Mesa 5 - Almoço Executivo, pagamento em Dinheiro com troco
        // =========================================================================
        System.out.println("###############################################################");
        System.out.println(" CENÁRIO 2: Atendimento Mesa 5 (Carlos Mendes - Dinheiro c/ Troco)");
        System.out.println("###############################################################");

        restaurante.abrirAtendimento(5, "Carlos Mendes", false, false, 1, 0.0, "", 2);
        restaurante.adicionarItemAoPedido(5, 3, 1, "Ponto: ao ponto para bem"); // Bife Chorizo (68.0)
        restaurante.adicionarItemAoPedido(5, 4, 1, "Gelo e limao"); // Refrigerante (7.5)

        restaurante.enviarBarEPreparar(5);
        restaurante.enviarCozinhaEPreparar(5);
        restaurante.entregarPedidoNaMesa(5);

        // Fechamento em dinheiro: Total itens = 75.50 + 10% taxa = 83.05. Pagamento com nota de R$ 100,00
        String resPgto5 = restaurante.processarTudoEPagar(
                5,
                1,       // Forma: Dinheiro
                100.0,   // Entregue R$ 100,00
                "PROMO5",// Cupom R$ 5,00 OFF
                true,    // Aceita 10%
                1        // 1 pessoa
        );
        System.out.println("Resultado Fechamento Mesa 5: " + resPgto5);
        System.out.println("Mesa 5 esta ocupada agora? " + restaurante.isMesaOcupada(5) + "\n");

        System.out.println("===============================================================");
        System.out.println("  SIMULAÇÃO CONCLUÍDA COM SUCESSO!");
        System.out.println("  Pronto para testes e refatorações (Clean Code & GoF Patterns).");
        System.out.println("===============================================================");
    }
}
