package com.restaurante;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 🛡️ SUÍTE DE TESTES DE REGRESSÃO
 * Garante que todos os requisitos funcionais (RF01 a RF06) funcionem perfeitamente,
 * servindo como "Rede de Segurança" para as futuras refatorações.
 */
public class RestauranteTest {

    private RestauranteLegado restaurante;
    private Garcom garcom;

    @BeforeEach
    void setUp() {
        restaurante = new RestauranteLegado("Bella Tavola", 5);
        garcom = new Garcom(1, "Giovanni");
        restaurante.cadastrarGarcom(garcom);
    }

    @Test
    @DisplayName("RF01 - Deve inicializar restaurante com mesas livres e cardapio carregado")
    void testInicializacaoRestaurante() {
        assertEquals("Bella Tavola", restaurante.r_n);
        assertEquals(5, restaurante.totalMesas);
        assertFalse(restaurante.isMesaOcupada(1));
        assertFalse(restaurante.isMesaOcupada(5));
        assertTrue(restaurante.cardapio.size() >= 8);
        assertNotNull(restaurante.buscarItemCardapioPorId(1));
    }

    @Test
    @DisplayName("RF02 - Deve abrir mesa com sucesso e recusar mesa ocupada ou invalida")
    void testAtendimentoMesa() {
        // Mesa válida
        String resOk = restaurante.abrirAtendimento(1, "Carlos", false, false, 1, 0.0, "", 1);
        assertEquals("OK", resOk);
        assertTrue(restaurante.isMesaOcupada(1));

        // Tentar abrir a mesma mesa ocupada
        String resOcupada = restaurante.abrirAtendimento(1, "Ana", false, false, 1, 0.0, "", 1);
        assertEquals("ERRO_MESA_OCUPADA", resOcupada);

        // Mesa inexistente
        String resInvalida = restaurante.abrirAtendimento(99, "Roberto", false, false, 1, 0.0, "", 1);
        assertEquals("ERRO_MESA_INVALIDA", resInvalida);

        // Garçom inexistente
        String resSemGarcom = restaurante.abrirAtendimento(2, "Julia", false, false, 1, 0.0, "", 99);
        assertEquals("ERRO_GARCOM_INEXISTENTE", resSemGarcom);
    }

    @Test
    @DisplayName("RF03 - Deve adicionar itens ao pedido e preservar observacoes personalizadas")
    void testAdicionarItensAoPedido() {
        restaurante.abrirAtendimento(2, "Beatriz", false, false, 1, 0.0, "", 1);

        // Adiciona 2x Lasanha (48.0 cada) com obs
        String resItem = restaurante.adicionarItemAoPedido(2, 1, 2, "Sem queijo extra");
        assertEquals("OK", resItem);

        // Adiciona 1x Refrigerante (7.5)
        restaurante.adicionarItemAoPedido(2, 4, 1, "Com limão");

        Pedido p = restaurante.getPedido(2);
        assertNotNull(p);
        assertEquals(3, p.getItens().size());
        assertEquals("Sem queijo extra", p.getItens().get(0).getObs());
        assertEquals(103.5, restaurante.calcularSubtotal(2), 0.001);

        // Item inexistente
        String resInexistente = restaurante.adicionarItemAoPedido(2, 999, 1, "");
        assertEquals("ERRO_ITEM_INEXISTENTE", resInexistente);
    }

    @Test
    @DisplayName("RF04 - Linha de producao: Cozinha e Bar devem processar itens e notificar garcom")
    void testLinhaProducaoCozinhaEBar() {
        restaurante.abrirAtendimento(3, "Lucas", false, false, 1, 0.0, "", 1);
        restaurante.adicionarItemAoPedido(3, 1, 1, ""); // Prato: Lasanha
        restaurante.adicionarItemAoPedido(3, 4, 1, ""); // Bebida: Refri

        Pedido p = restaurante.getPedido(3);
        assertEquals(Pedido.STATUS_RECEBIDO, p.getStatus());

        // Processa Bar
        String resBar = restaurante.enviarBarEPreparar(3);
        assertEquals("OK", resBar);
        assertEquals(1, garcom.notificacoesRecebidas);

        // Processa Cozinha
        String resCozinha = restaurante.enviarCozinhaEPreparar(3);
        assertEquals("OK", resCozinha);
        assertEquals(2, garcom.notificacoesRecebidas);

        // Com todos os itens prontos, o pedido deve ficar STATUS_PRONTO
        assertEquals(Pedido.STATUS_PRONTO, p.getStatus());
        for (Item it : p.getItens()) {
            assertTrue(it.isPronto());
        }
    }

    @Test
    @DisplayName("RF05 - Entrega na mesa: Deve alterar status para ENTREGUE apenas se pedido estiver PRONTO")
    void testEntregaNaMesa() {
        restaurante.abrirAtendimento(4, "Fernanda", false, false, 1, 0.0, "", 1);
        restaurante.adicionarItemAoPedido(4, 2, 1, ""); // Risoto

        // Tenta entregar antes de preparar
        String resAntes = restaurante.entregarPedidoNaMesa(4);
        assertEquals("ERRO_PEDIDO_NAO_ESTA_PRONTO", resAntes);

        // Prepara na cozinha
        restaurante.enviarCozinhaEPreparar(4);

        // Agora entrega com sucesso
        String resDepois = restaurante.entregarPedidoNaMesa(4);
        assertEquals("OK", resDepois);

        Pedido p = restaurante.getPedido(4);
        assertEquals(Pedido.STATUS_ENTREGUE, p.getStatus());
    }

    @Test
    @DisplayName("RF06 - Fechamento com Pagamento em Dinheiro e Troco")
    void testFechamentoDinheiroComTroco() {
        restaurante.abrirAtendimento(1, "Marcos", false, false, 1, 0.0, "", 1);
        // Item 1: Lasanha = R$ 48.00
        restaurante.adicionarItemAoPedido(1, 1, 1, "");

        // Sem cupom, com taxa de 10%: Subtotal = 48.00, Taxa = 4.80, Total = 52.80
        // Pago com R$ 60.00 -> Troco = R$ 7.20
        String res = restaurante.processarTudoEPagar(1, 1, 60.0, null, true, 1);
        assertEquals("OK", res);

        Pedido p = restaurante.getPedido(1);
        assertTrue(p.isPago());
        assertEquals(Pedido.STATUS_PAGO, p.getStatus());
        assertFalse(restaurante.isMesaOcupada(1), "A mesa deve ser liberada apos o pagamento");

        // Tentar pagar novamente deve retornar erro
        String resPago = restaurante.processarTudoEPagar(1, 1, 60.0, null, true, 1);
        assertEquals("ERRO_PEDIDO_JA_PAGO", resPago);
    }

    @Test
    @DisplayName("RF06 - Fechamento com Cartao (Taxa da maquinha de 2.5%)")
    void testFechamentoCartaoComTaxaOperadora() {
        restaurante.abrirAtendimento(2, "Renata", false, false, 1, 0.0, "", 1);
        // Item 5: Suco Laranja = R$ 10.00
        restaurante.adicionarItemAoPedido(2, 5, 1, "");

        // Subtotal = 10.00, Taxa servico recusada = 0.0, Total Base = 10.00
        // Forma 2 (Cartao) acrescenta 2.5% -> Total final = 10.25
        String res = restaurante.processarTudoEPagar(2, 2, 0.0, "", false, 1);
        assertEquals("OK", res);
        assertTrue(restaurante.getPedido(2).isPago());
        assertFalse(restaurante.isMesaOcupada(2));
    }

    @Test
    @DisplayName("RF06 - Fechamento com PIX (5% de desconto especial) e Cupom DESC10")
    void testFechamentoPixComDescontoECupom() {
        restaurante.abrirAtendimento(3, "Diego", false, false, 1, 0.0, "", 1);
        // Item 5: Suco Laranja (10.0) x 10 = R$ 100.00
        restaurante.adicionarItemAoPedido(3, 5, 10, "");

        // Subtotal = 100.00
        // Cupom DESC10: -10% = -10.00 -> Base = 90.00
        // Taxa de servico aceita: +10% de 90.00 = +9.00 -> Total com taxa = 99.00
        // Pagamento PIX: -5% de 99.00 = -4.95 -> Total a pagar = 94.05
        String res = restaurante.processarTudoEPagar(3, 3, 0.0, "DESC10", true, 2);
        assertEquals("OK", res);
        assertTrue(restaurante.getPedido(3).isPago());
        assertFalse(restaurante.isMesaOcupada(3));
    }

    @Test
    @DisplayName("RF06 - Cliente VIP deve receber 5% de desconto adicional")
    void testDescontoClienteVip() {
        // Abertura com flag VIP = true
        restaurante.abrirAtendimento(4, "Sofia", true, false, 1, 0.0, "", 1);
        // Item 5: Suco = 10.00 x 10 = 100.00
        restaurante.adicionarItemAoPedido(4, 5, 10, "");

        // VIP ganha 5% = -5.00 -> Base = 95.00
        // Sem taxa de servico -> Total = 95.00
        // Dinheiro entregue = 100.00 -> Troco = 5.00
        String res = restaurante.processarTudoEPagar(4, 1, 100.0, null, false, 1);
        assertEquals("OK", res);
        assertTrue(restaurante.getPedido(4).isPago());
    }

    @Test
    @DisplayName("RF06 - Validacao de pagamento insuficiente em dinheiro")
    void testPagamentoInsuficiente() {
        restaurante.abrirAtendimento(5, "Jose", false, false, 1, 0.0, "", 1);
        restaurante.adicionarItemAoPedido(5, 1, 1, ""); // Lasanha = 48.00

        // Total com taxa (48 + 4.80) = 52.80. Tenta pagar com 50.00
        String res = restaurante.processarTudoEPagar(5, 1, 50.0, null, true, 1);
        assertEquals("ERRO_VALOR_INSUFICIENTE", res);
        assertFalse(restaurante.getPedido(5).isPago());
        assertTrue(restaurante.isMesaOcupada(5));
    }
}
