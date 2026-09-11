package com.restaurante;

import java.util.*;

/**
 * 💣 CLASSE "GOD CLASS" / LEGADO COM TODOS OS CODE SMELLS INTENCIONAIS.
 * 
 * Centraliza TUDO: cardápio, estoque, mesas, garçons, fila da cozinha,
 * faturamento e comprovantes no terminal.
 */
public class RestauranteLegado {

    // Nomes ruins e abreviações
    public String nomeRestaurante; // nome do restaurante
    public int totalMesas;
    public Map<Integer, Boolean> statusMesaOcupada; // mesa ocupada ou nao
    public Map<Integer, Pedido> pedidosPorMesa;
    public List<Item> cardapio;
    public List<Garcom> garcons;
    public List<String> cuponsValidos;

    // Construtor
    public RestauranteLegado(String nomeRestaurante, int totalMesas) {
        this.nomeRestaurante = nomeRestaurante;
        this.totalMesas = totalMesas;
        this.statusMesaOcupada = new HashMap<>();
        this.pedidosPorMesa = new HashMap<>();
        this.cardapio = new ArrayList<>();
        this.garcons = new ArrayList<>();
        this.cuponsValidos = new ArrayList<>(Arrays.asList("DESC10", "PROMO5", "BELLA20"));

        // Inicializa todas as mesas como livres
        for (int i = 1; i <= totalMesas; i++) {
            statusMesaOcupada.put(i, false);
        }

        // Carrega cardápio padrão inicial
        carregarCardapioPadrao();
    }

    // RF01: Cardápio inicial
    private void carregarCardapioPadrao() {
        // Construtores cheios de parâmetros e primitive obsession
        cardapio.add(new Item(1, "Lasanha Bolonhesa", 48.0, "PRATO", 25));
        cardapio.add(new Item(2, "Risoto de Cogumelos", 55.0, "PRATO", 20));
        cardapio.add(new Item(3, "Bife de Chorizo", 68.0, "PRATO", 30));
        cardapio.add(new Item(4, "Refrigerante Lata", 7.5, "BEBIDA", 2));
        cardapio.add(new Item(5, "Suco Natural de Laranja", 10.0, "BEBIDA", 5));
        cardapio.add(new Item(6, "Vinho Tinto Taça", 22.0, "BEBIDA", 3));
        cardapio.add(new Item(7, "Tiramisu Clássico", 24.0, "SOBREMESA", 8));
        cardapio.add(new Item(8, "Panna Cotta", 18.0, "SOBREMESA", 5));
    }

    public void cadastrarGarcom(Garcom g) {
        this.garcons.add(g);
    }

    public Garcom buscarGarcomPorId(int id) {
        for (Garcom g : garcons) {
            if (g.getId() == id) return g;
        }
        return null; // Retorno de null
    }

    public Item buscarItemCardapioPorId(int id) {
        for (Item it : cardapio) {
            if (it.getId() == id) return it;
        }
        return null; // Retorno de null
    }

    // RF02 & Code Smell: Flag Arguments & Retorno de Códigos de Erro em vez de Exceptions
    // criarPedido com 8 parâmetros
    public String abrirAtendimento(int mesa, String cliente, boolean vip, boolean paraViagem, int tipoAtendimento, double descManual, String obs, int garcomId) {
        // Valida se mesa existe
        if (mesa < 1 || mesa > totalMesas) {
            return "ERRO_MESA_INVALIDA";
        }

        // Verifica se mesa ta ocupada (comentário ruim/óbvio)
        if (Boolean.TRUE.equals(statusMesaOcupada.get(mesa))) {
            return "ERRO_MESA_OCUPADA";
        }

        Garcom g = buscarGarcomPorId(garcomId);
        if (g == null) {
            return "ERRO_GARCOM_INEXISTENTE";
        }

        // Ocupa a mesa
        statusMesaOcupada.put(mesa, true);
        int novoIdPedido = pedidosPorMesa.size() + 1;
        Pedido novoPedido = new Pedido(novoIdPedido, mesa, cliente, vip, paraViagem, obs, g);
        pedidosPorMesa.put(mesa, novoPedido);

        System.out.println("[RESTAURANTE " + nomeRestaurante + "]: Mesa " + mesa + " aberta para " + cliente + " com garcom " + g.getNome());
        return "OK";
    }

    // RF03: Adicionar itens ao pedido
    public String adicionarItemAoPedido(int mesa, int itemId, int qtd, String observacaoItem) {
        String ERRO_MESA_SEM_PEDIDO = procuraMesa(mesa);
        if (ERRO_MESA_SEM_PEDIDO != null) return ERRO_MESA_SEM_PEDIDO;

        Item modelo = buscarItemCardapioPorId(itemId);
        if (modelo == null) {
            return "ERRO_ITEM_INEXISTENTE";
        }

        Pedido p = pedidosPorMesa.get(mesa);

        // Se o pedido já tiver sido pago ou cancelado, não permite
        if (p.isPago() || p.isCancelado()) {
            return "ERRO_PEDIDO_FECHADO";
        }

        for (int i = 0; i < qtd; i++) {
            Item itemInstancia = modelo.comObs(observacaoItem);
            p.adicionarItem(itemInstancia);
        }

        System.out.println("[PEDIDO MESA " + mesa + "]: Adicionado " + qtd + "x " + modelo.getNome() + (observacaoItem != null && !observacaoItem.isEmpty() ? " (" + observacaoItem + ")" : ""));
        return "OK";
    }

    // Scanner coupling: Método acoplado diretamente ao console Scanner
    public void fazerPedidoViaConsole(Scanner sc) {
        System.out.print("Digite o numero da mesa: ");
        int mesa = sc.nextInt();
        sc.nextLine(); // consome enter
        System.out.print("Digite o ID do item: ");
        int itemId = sc.nextInt();
        System.out.print("Digite a quantidade: ");
        int qtd = sc.nextInt();
        sc.nextLine();
        System.out.print("Observacao: ");
        String obs = sc.nextLine();
        String res = adicionarItemAoPedido(mesa, itemId, qtd, obs);
        System.out.println("Resultado: " + res);
    }

    // RF04 & Code Smell: Template Method Smell (duplicação de passos entre Cozinha e Bar)
    // Cozinha prepara pratos quentes e sobremesas
    public String enviarCozinhaEPreparar(int mesa) {
        Pedido p = pedidosPorMesa.get(mesa);
        if (p == null) return "ERRO_MESA_SEM_PEDIDO";

        // State Smell: múltiplos ifs aninhados
        if (p.isCancelado()) return "ERRO_PEDIDO_CANCELADO";
        if (p.isPago()) return "ERRO_PEDIDO_PAGO";

        p.setStatus(Pedido.STATUS_EM_PREPARO);
        System.out.println("\n--- [COZINHA] Processando Pedido da Mesa " + mesa + " ---");

        for (Item it : p.getItens()) {
            if ("PRATO".equalsIgnoreCase(it.getCategoria()) || "SOBREMESA".equalsIgnoreCase(it.getCategoria())) {
                // Passo 1: Validar ingredientes
                System.out.println("[COZINHA] 1. Checando ingredientes para: " + it.getNome());
                // Passo 2: Preparar
                System.out.println("[COZINHA] 2. Cozinhando " + it.getNome() + " (Tempo est: " + it.getTempoPreparo() + " min)");
                if (it.getObservacao() != null && !it.getObservacao().isEmpty()) {
                    System.out.println("[COZINHA]    -> Atendendo observacao especial: " + it.getObservacao());
                }
                // Passo 3: Empratar
                System.out.println("[COZINHA] 3. Empratando e finalizando: " + it.getNome());
                it.setPronto(true);

                // Observer Smell: Notifica garçom diretamente acoplado
                if (p.getGarcom() != null) {
                    p.getGarcom().avisarPratoPronto(mesa, it.getNome());
                }
            }
        }

        // Se bebidas também estiverem prontas ou não houver bebidas pendentes, avalia status
        verificarEAtualizarStatusPronto(p);
        return "OK";
    }

    // Bar prepara bebidas
    public String enviarBarEPreparar(int mesa) {
        Pedido p = pedidosPorMesa.get(mesa);
        if (p == null) return "ERRO_MESA_SEM_PEDIDO";
        if (p.isCancelado()) return "ERRO_PEDIDO_CANCELADO";
        if (p.isPago()) return "ERRO_PEDIDO_PAGO";

        p.setStatus(Pedido.STATUS_EM_PREPARO);
        System.out.println("\n--- [BAR] Processando Bebidas da Mesa " + mesa + " ---");

        for (Item it : p.getItens()) {
            if ("BEBIDA".equalsIgnoreCase(it.getCategoria())) {
                // Passo 1: Validar insumos (duplicação da estrutura da cozinha)
                System.out.println("[BAR] 1. Checando insumos e gelo para: " + it.getNome());
                // Passo 2: Preparar / Servir
                System.out.println("[BAR] 2. Tirando bebida " + it.getNome() + " (Tempo est: " + it.getTempoPreparo() + " min)");
                if (it.getObservacao() != null && !it.getObservacao().isEmpty()) {
                    System.out.println("[BAR]    -> Observacao: " + it.getObservacao());
                }
                // Passo 3: Envasar / Decorar taça
                System.out.println("[BAR] 3. Envasando taça/copo: " + it.getNome());
                it.setPronto(true);

                // Observer Smell: Notifica garçom diretamente acoplado
                if (p.getGarcom() != null) {
                    p.getGarcom().avisarPratoPronto(mesa, it.getNome());
                }
            }
        }

        verificarEAtualizarStatusPronto(p);
        return "OK";
    }

    private void verificarEAtualizarStatusPronto(Pedido p) {
        boolean todosProntos = true;
        for (Item it : p.getItens()) {
            if (!it.isPronto()) {
                todosProntos = false;
                break;
            }
        }
        if (todosProntos && !p.getItens().isEmpty()) {
            p.setStatus(Pedido.STATUS_PRONTO);
            System.out.println("[PEDIDO MESA " + p.getMesa() + "] TODOS OS ITENS ESTAO PRONTOS!");
        }
    }

    // RF05: Entrega na mesa
    public String entregarPedidoNaMesa(int mesa) {
        Pedido p = pedidosPorMesa.get(mesa);
        if (p == null) return "ERRO_MESA_SEM_PEDIDO";

        // State Smell: if encadeado
        if (p.getStatus() != Pedido.STATUS_PRONTO) {
            return "ERRO_PEDIDO_NAO_ESTA_PRONTO";
        }

        p.setStatus(Pedido.STATUS_ENTREGUE);
        System.out.println("[SERVICO]: Garcom " + (p.getGarcom() != null ? p.getGarcom().getNome() : "Desconhecido") + " entregou o pedido completo na Mesa " + mesa + "!");
        return "OK";
    }

    // Code Smell: Duplicated Code (Lógica de cálculo duplicada da rotina de pagamento)
    public double calcularSubtotal(int mesa) {
        Pedido p = pedidosPorMesa.get(mesa);
        if (p == null) return 0.0;
        double sub = 0.0;
        for (Item it : p.getItens()) {
            sub += it.getPreco();
        }
        return sub;
    }

    // Code Smell: Duplicated Code (Cálculo de prévia repetindo regras de desconto e taxas)
    public void imprimirPreviaComanda(int mesa, String cupom) {
        Pedido p = pedidosPorMesa.get(mesa);
        if (p == null) {
            System.out.println("Mesa nao encontrada!");
            return;
        }

        double val_tot_aux = 0.0; // nome ruim
        System.out.println("\n========== PREVIA DA CONTA - MESA " + mesa + " ==========");
        System.out.println("Cliente: " + p.getCliente() + " | Garcom: " + (p.getGarcom() != null ? p.getGarcom().getNome() : "-"));
        for (Item it : p.getItens()) {
            System.out.printf("- %-25s R$ %6.2f\n", it.getNome(), it.getPreco());
            val_tot_aux += it.getPreco();
        }
        System.out.println("---------------------------------------------");
        System.out.printf("Subtotal:                   R$ %6.2f\n", val_tot_aux);

        // Duplicação de regra de desconto:
        double desc_cup = 0.0;
        if (cupom != null && cuponsValidos.contains(cupom.toUpperCase())) {
            if ("DESC10".equalsIgnoreCase(cupom)) desc_cup = val_tot_aux * 0.10;
            else if ("PROMO5".equalsIgnoreCase(cupom)) desc_cup = 5.0;
            else if ("BELLA20".equalsIgnoreCase(cupom)) desc_cup = val_tot_aux * 0.20;
        }
        if (p.isVip()) {
            desc_cup += val_tot_aux * 0.05; // VIP ganha 5% extra
        }

        double baseComDesconto = val_tot_aux - desc_cup;
        if (baseComDesconto < 0) baseComDesconto = 0;

        // soma 10% da taxa de servico (comentario inutil)
        double taxaServico = baseComDesconto * 0.10;
        double totalEstimado = baseComDesconto + taxaServico;

        System.out.printf("Descontos:                - R$ %6.2f\n", desc_cup);
        System.out.printf("Taxa de Servico (10%%):     + R$ %6.2f\n", taxaServico);
        System.out.printf("TOTAL ESTIMADO:             R$ %6.2f\n", totalEstimado);
        System.out.println("=============================================");
    }

    // 💣 MÉTODO MONSTRUOSO (Long Method), Níveis de Abstração Misturados,
    // Feature Envy, Strategy Smell (Switch-case gigante), Primitive Obsession
    // RF06: Fechamento de Conta e Checkout
    public String processarTudoEPagar(int mesa, int formaPagamento, double valorEntregueDinheiro, String cupom, boolean aceitaTaxaServico, int numeroPessoasDivisao) {
        // Validação da mesa e pedido
        String ERRO_MESA_SEM_PEDIDO = procuraMesa(mesa);
        if (ERRO_MESA_SEM_PEDIDO != null) return ERRO_MESA_SEM_PEDIDO;

        Pedido p = pedidosPorMesa.get(mesa);
        String ERRO_PEDIDO_JA_PAGO = statusPedido(p);
        if (ERRO_PEDIDO_JA_PAGO != null) return ERRO_PEDIDO_JA_PAGO;

        // Inveja de Recursos (Feature Envy): RestauranteLegado acessa cada detalhe interno de Pedido e Item
        Double val_tot_aux = somaValorTotal(p);
        if (val_tot_aux == null) return "ERRO_PEDIDO_VAZIO";

        // Lógica duplicada de cupom
        double desc_cup = aplicaCupomDesconto(cupom, val_tot_aux);

        // Desconto adicional para cliente VIP (Feature Envy)
        desc_cup = isVip(p, desc_cup, val_tot_aux);

        // Evita desconto maior que o valor
        desc_cup = verificaDescontoMaiorQueValor(desc_cup, val_tot_aux);

        double valorComDesconto = val_tot_aux - desc_cup;

        // Cálculo da taxa de serviço (10%)
        double taxaServico = calculaTaxaServico(aceitaTaxaServico, valorComDesconto);

        double totalFinal = valorComDesconto + taxaServico;

        // STRATEGY SMELL: Switch gigante para calcular acréscimos/descontos da forma de pagamento
        // 1 = Dinheiro (precisa calcular troco)
        // 2 = Cartao (taxa maquininha +2.5%)
        // 3 = PIX (5% de desconto extra)
        double totalAposFormaPagamento = totalFinal;
        double troco = 0.0;
        String descForma = "";

        if (formaPagamento == 1) { // DINHEIRO
            descForma = "Dinheiro";
            if (valorEntregueDinheiro < totalFinal) {
                return "ERRO_VALOR_INSUFICIENTE";
            }
            troco = valorEntregueDinheiro - totalFinal;
        } else if (formaPagamento == 2) { // CARTAO
            descForma = "Cartao de Credito/Debito";
            double taxaCartao = totalFinal * 0.025; // 2.5% de custo de conveniência
            totalAposFormaPagamento = totalFinal + taxaCartao;
        } else if (formaPagamento == 3) { // PIX
            descForma = "PIX";
            double descPix = totalFinal * 0.05; // 5% de desconto no PIX
            totalAposFormaPagamento = totalFinal - descPix;
        } else {
            return "ERRO_FORMA_PAGAMENTO_INVALIDA";
        }

        // Rateio / Divisão da conta
        numeroPessoasDivisao = calculaNumeroPessoasDivisaoValor(numeroPessoasDivisao);
        double valorPorPessoa = totalAposFormaPagamento / numeroPessoasDivisao;

        // Mistura de Nível de Abstração: Impressão direta do cupom fiscal / comprovante no terminal
        cupomFiscal(mesa, formaPagamento, valorEntregueDinheiro, aceitaTaxaServico, numeroPessoasDivisao, p, val_tot_aux, desc_cup, taxaServico, totalFinal, descForma, totalAposFormaPagamento, troco, valorPorPessoa);

        // Atualização de estado e limpeza (liberação da mesa para próximo cliente)
        finalizaPedido(mesa, p);

        return "OK";
    }

    private void finalizaPedido(int mesa, Pedido p) {
        p.setStatus(Pedido.STATUS_PAGO);
        p.setPago(true);
        statusMesaOcupada.put(mesa, false); // Libera mesa (comentário óbvio)
    }

    private void cupomFiscal(int mesa, int formaPagamento, double valorEntregueDinheiro, boolean aceitaTaxaServico, int numeroPessoasDivisao, Pedido p, Double val_tot_aux, double desc_cup, double taxaServico, double totalFinal, String descForma, double totalAposFormaPagamento, double troco, double valorPorPessoa) {
        System.out.println("\n========================================================");
        System.out.println("            RESTAURANTE " + nomeRestaurante.toUpperCase());
        System.out.println("               CUPOM FISCAL / COMPROVANTE               ");
        System.out.println("========================================================");
        System.out.println("Mesa: " + mesa + " | Atendente: " + (p.getGarcom() != null ? p.getGarcom().getNome() : "Sem Garçom"));
        System.out.println("Cliente: " + p.getCliente() + (p.isVip() ? " [CLIENTE VIP]" : ""));
        System.out.println("Status Anterior: " + p.getDescricaoStatus());
        System.out.println("--------------------------------------------------------");
        System.out.println("ITENS CONSUMIDOS:");
        for (Item it : p.getItens()) {
            System.out.printf(" - %-28s R$ %8.2f\n", it.getNome(), it.getPreco());
        }
        System.out.println("--------------------------------------------------------");
        System.out.printf("Subtotal Itens:                         R$ %8.2f\n", val_tot_aux);
        if (desc_cup > 0) {
            System.out.printf("Descontos (Cupom/VIP):                - R$ %8.2f\n", desc_cup);
        }
        if (aceitaTaxaServico) {
            System.out.printf("Taxa de Servico (10%%):                 + R$ %8.2f\n", taxaServico);
        } else {
            System.out.println("Taxa de Servico:                       (Nao autorizada)");
        }
        System.out.println("--------------------------------------------------------");
        System.out.printf("Total com Taxa/Desconto:                R$ %8.2f\n", totalFinal);
        System.out.println("Forma de Pagamento: " + descForma);
        if (formaPagamento == 2) {
            System.out.printf("Acréscimo Operadora Cartão (2.5%%):     + R$ %8.2f\n", totalFinal * 0.025);
        } else if (formaPagamento == 3) {
            System.out.printf("Desconto Especial PIX (5%%):           - R$ %8.2f\n", totalFinal * 0.05);
        }
        System.out.printf("VALOR FINAL COBRADO:                    R$ %8.2f\n", totalAposFormaPagamento);
        if (formaPagamento == 1) {
            System.out.printf("Valor Pago em Dinheiro:                 R$ %8.2f\n", valorEntregueDinheiro);
            System.out.printf("Troco:                                  R$ %8.2f\n", troco);
        }
        if (numeroPessoasDivisao > 1) {
            System.out.println("--------------------------------------------------------");
            System.out.printf("Divisao da Conta (%d pessoas):           R$ %8.2f cada\n", numeroPessoasDivisao, valorPorPessoa);
        }
        System.out.println("========================================================");
        System.out.println("    Obrigado pela preferencia! Volte sempre ao " + nomeRestaurante + "!");
        System.out.println("========================================================\n");
    }

    private static int calculaNumeroPessoasDivisaoValor(int numeroPessoasDivisao) {
        if (numeroPessoasDivisao <= 0) {
            numeroPessoasDivisao = 1;
        }
        return numeroPessoasDivisao;
    }

    private static double calculaTaxaServico(boolean aceitaTaxaServico, double valorComDesconto) {
        double taxaServico = 0.0;
        if (aceitaTaxaServico) {
            taxaServico = valorComDesconto * 0.10;
        }
        return taxaServico;
    }

    private static double verificaDescontoMaiorQueValor(double desc_cup, Double val_tot_aux) {
        if (desc_cup > val_tot_aux) {
            desc_cup = val_tot_aux;
        }
        return desc_cup;
    }

    private static double isVip(Pedido p, double desc_cup, Double val_tot_aux) {
        if (p.isVip()) {
            desc_cup += (val_tot_aux * 0.05);
        }
        return desc_cup;
    }

    private double aplicaCupomDesconto(String cupom, Double val_tot_aux) {
        double desc_cup = 0.0;
        if (cupom != null && !cupom.trim().isEmpty()) {
            String cupomUpper = cupom.trim().toUpperCase();
            if (cuponsValidos.contains(cupomUpper)) {
                if ("DESC10".equals(cupomUpper)) {
                    desc_cup = val_tot_aux * 0.10;
                } else if ("PROMO5".equals(cupomUpper)) {
                    desc_cup = 5.0;
                } else if ("BELLA20".equals(cupomUpper)) {
                    desc_cup = val_tot_aux * 0.20;
                }
            } else {
                System.out.println("[AVISO]: Cupom invalido ignorado: " + cupom);
            }
        }
        return desc_cup;
    }

    private static Double somaValorTotal(Pedido p) {
        double val_tot_aux = 0.0;
        List<Item> itensDoPedido = p.getItens();
        for (int i = 0; i < itensDoPedido.size(); i++) {
            Item itemAtual = itensDoPedido.get(i);
            val_tot_aux += itemAtual.getPreco(); // Acumula preco
        }

        if (val_tot_aux <= 0) {
            return null;
        }
        return val_tot_aux;
    }

    private static String statusPedido(Pedido p) {
        if (p.isPago()) {
            return "ERRO_PEDIDO_JA_PAGO";
        }
        if (p.isCancelado()) {
            return "ERRO_PEDIDO_CANCELADO";
        }
        return null;
    }

    private String procuraMesa(int mesa) {
        if (!pedidosPorMesa.containsKey(mesa)) {
            return "ERRO_MESA_SEM_PEDIDO";
        }
        return null;
    }

    public Pedido getPedido(int mesa) {
        return pedidosPorMesa.get(mesa);
    }

    public boolean isMesaOcupada(int mesa) {
        return Boolean.TRUE.equals(statusMesaOcupada.get(mesa));
    }
}
