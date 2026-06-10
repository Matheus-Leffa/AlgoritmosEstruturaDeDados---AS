package modelo.carta;

import estrutura.Pilha;
import modelo.Jogador;
import estrutura.NoTabuleiro;
import estrutura.ListaDuplamenteLigadaCircular;
import modelo.casa.CasaInicio;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa o Baralho de cartas de Sorte ou Revés no jogo.
 * Gerencia as cartas utilizando uma Pilha customizada, recriando-a
 * e re-embaralhando-a automaticamente quando o baralho esvazia.
 */
public class Baralho {
    private Pilha<Carta> pilhaCartas;
    private List<Carta> modeloCartas; // Lista de referência para clonagem/recriação das cartas

    public Baralho() {
        this.pilhaCartas = new Pilha<>();
        this.modeloCartas = new ArrayList<>();
        inicializarModeloCartas();
        reconstituirBaralho();
    }

    /**
     * Inicializa o catálogo de 12 cartas obrigatórias (6 Sorte / 6 Revés)
     * contendo seus respectivos efeitos através de expressões Lambda.
     */
    private void inicializarModeloCartas() {
        // === 6 CARTAS DE GANHO (SORTE) ===

        // 1. Receber dinheiro do banco
        modeloCartas.add(new Carta(1, "Herança Inesperada", 
            "Um parente distante deixou uma herança para você. Receba R$ 150 do banco.", 
            TipoCarta.SORTE, 
            (jogador, todos, tabuleiro) -> {
                jogador.setSaldo(jogador.getSaldo() + 150.0);
                System.out.println("-> [EFEITO] Saldo de " + jogador.getNome() + " aumentado em R$ 150,00.");
            }
        ));

        // 2. Avançar N casas
        modeloCartas.add(new Carta(2, "Passo Rápido", 
            "Caminho livre! Avance 3 casas no tabuleiro.", 
            TipoCarta.SORTE, 
            (jogador, todos, tabuleiro) -> {
                System.out.println("-> [EFEITO] Movendo " + jogador.getNome() + " 3 casas para frente...");
                NoTabuleiro novaPos = tabuleiro.moverEVerificarPassagemInicio(jogador.getPosicaoAtual(), 3, () -> {
                    System.out.println("   [BÔNUS DE INÍCIO] Passou pelo ponto de partida! +R$ 200,00.");
                    jogador.setSaldo(jogador.getSaldo() + 200.0);
                });
                jogador.setPosicaoAtual(novaPos);
            }
        ));

        // 3. Avançar diretamente para o início
        modeloCartas.add(new Carta(3, "Vá para o Início", 
            "Siga diretamente para o ponto de partida e receba a bonificação.", 
            TipoCarta.SORTE, 
            (jogador, todos, tabuleiro) -> {
                System.out.println("-> [EFEITO] Teletransportando " + jogador.getNome() + " para o Ponto de Partida.");
                jogador.setPosicaoAtual(tabuleiro.getCabeca());
                double bonus = 200.0;
                if (tabuleiro.getCabeca().getCasa() instanceof CasaInicio) {
                    bonus = ((CasaInicio) tabuleiro.getCabeca().getCasa()).getValorBonificacao();
                }
                jogador.setSaldo(jogador.getSaldo() + bonus);
                System.out.println("   [BÔNUS DE INÍCIO] Recebeu R$ " + bonus + " por chegar ao Início.");
            }
        ));

        // 4. Receber dinheiro dos demais jogadores
        modeloCartas.add(new Carta(4, "Festa de Aniversário", 
            "Hoje é seu aniversário! Cada jogador lhe dá R$ 50 de presente.", 
            TipoCarta.SORTE, 
            (jogador, todos, tabuleiro) -> {
                double taxa = 50.0;
                int pagantes = 0;
                for (Jogador outro : todos) {
                    if (!outro.getId().equals(jogador.getId())) {
                        outro.setSaldo(outro.getSaldo() - taxa);
                        jogador.setSaldo(jogador.getSaldo() + taxa);
                        System.out.println("   " + outro.getNome() + " pagou R$ 50,00 para " + jogador.getNome());
                        pagantes++;
                    }
                }
                System.out.println("-> [EFEITO] Você arrecadou R$ " + (taxa * pagantes) + " dos adversários.");
            }
        ));

        // 5. Receber dinheiro do banco (Ganho adicional)
        modeloCartas.add(new Carta(5, "Prêmio de Investimento", 
            "Suas ações renderam bons dividendos. Receba R$ 100 do banco.", 
            TipoCarta.SORTE, 
            (jogador, todos, tabuleiro) -> {
                jogador.setSaldo(jogador.getSaldo() + 100.0);
                System.out.println("-> [EFEITO] Saldo de " + jogador.getNome() + " aumentado em R$ 100,00.");
            }
        ));

        // 6. Receber dinheiro dos demais jogadores (Ganho adicional)
        modeloCartas.add(new Carta(6, "Cobrança de Dívidas", 
            "Você cobrou favores antigos. Cada jogador lhe paga R$ 20.", 
            TipoCarta.SORTE, 
            (jogador, todos, tabuleiro) -> {
                double taxa = 20.0;
                int pagantes = 0;
                for (Jogador outro : todos) {
                    if (!outro.getId().equals(jogador.getId())) {
                        outro.setSaldo(outro.getSaldo() - taxa);
                        jogador.setSaldo(jogador.getSaldo() + taxa);
                        System.out.println("   " + outro.getNome() + " pagou R$ 20,00 para " + jogador.getNome());
                        pagantes++;
                    }
                }
                System.out.println("-> [EFEITO] Você arrecadou R$ " + (taxa * pagantes) + " dos adversários.");
            }
        ));

        // === 6 CARTAS DE PENALIDADE (REVÉS) ===

        // 7. Pagar dinheiro ao banco
        modeloCartas.add(new Carta(7, "Conserto do Carro", 
            "Seu veículo quebrou. Pague R$ 100 ao banco pelo conserto.", 
            TipoCarta.REVES, 
            (jogador, todos, tabuleiro) -> {
                jogador.setSaldo(jogador.getSaldo() - 100.0);
                System.out.println("-> [EFEITO] Saldo de " + jogador.getNome() + " reduzido em R$ 100,00.");
            }
        ));

        // 8. Pagar dinheiro ao banco
        modeloCartas.add(new Carta(8, "Imposto de Renda Extra", 
            "Erro na declaração fiscal. Pague R$ 150 ao banco.", 
            TipoCarta.REVES, 
            (jogador, todos, tabuleiro) -> {
                jogador.setSaldo(jogador.getSaldo() - 150.0);
                System.out.println("-> [EFEITO] Saldo de " + jogador.getNome() + " reduzido em R$ 150,00.");
            }
        ));

        // 9. Pagar dinheiro aos demais jogadores
        modeloCartas.add(new Carta(9, "Jantar de Negócios", 
            "Você convidou todos para jantar. Pague R$ 40 para cada um dos outros jogadores.", 
            TipoCarta.REVES, 
            (jogador, todos, tabuleiro) -> {
                double taxa = 40.0;
                int cobrados = 0;
                for (Jogador outro : todos) {
                    if (!outro.getId().equals(jogador.getId())) {
                        jogador.setSaldo(jogador.getSaldo() - taxa);
                        outro.setSaldo(outro.getSaldo() + taxa);
                        System.out.println("   " + jogador.getNome() + " pagou R$ 40,00 para " + outro.getNome());
                        cobrados++;
                    }
                }
                System.out.println("-> [EFEITO] Você gastou R$ " + (taxa * cobrados) + " no total.");
            }
        ));

        // 10. Pagar dinheiro aos demais jogadores (Penalidade adicional)
        modeloCartas.add(new Carta(10, "Vaquinha Beneficente", 
            "É hora de ajudar! Contribua com R$ 30 para cada jogador ativo na mesa.", 
            TipoCarta.REVES, 
            (jogador, todos, tabuleiro) -> {
                double taxa = 30.0;
                int cobrados = 0;
                for (Jogador outro : todos) {
                    if (!outro.getId().equals(jogador.getId())) {
                        jogador.setSaldo(jogador.getSaldo() - taxa);
                        outro.setSaldo(outro.getSaldo() + taxa);
                        System.out.println("   " + jogador.getNome() + " transferiu R$ 30,00 para " + outro.getNome());
                        cobrados++;
                    }
                }
                System.out.println("-> [EFEITO] Despesa de R$ " + (taxa * cobrados) + " efetuada.");
            }
        ));

        // 11. Voltar N casas
        modeloCartas.add(new Carta(11, "Engarrafamento", 
            "Trânsito caótico. Volte 2 casas no tabuleiro.", 
            TipoCarta.REVES, 
            (jogador, todos, tabuleiro) -> {
                System.out.println("-> [EFEITO] Movendo " + jogador.getNome() + " 2 casas para trás...");
                NoTabuleiro pos = jogador.getPosicaoAtual();
                pos = pos.getAnterior().getAnterior(); // Navegação para trás via getAnterior()
                jogador.setPosicaoAtual(pos);
                System.out.println("   Parou em: " + pos.getCasa().getNome());
            }
        ));

        // 12. Voltar para a posição anterior
        modeloCartas.add(new Carta(12, "Esquecimento de Chave", 
            "Você esqueceu suas chaves na última parada. Retorne imediatamente para a sua posição anterior.", 
            TipoCarta.REVES, 
            (jogador, todos, tabuleiro) -> {
                if (jogador.getPosicaoAnterior() != null) {
                    System.out.println("-> [EFEITO] Retornando " + jogador.getNome() + " para a casa: " 
                        + jogador.getPosicaoAnterior().getCasa().getNome());
                    // Define a posição atual como a anterior manualmente para teleportar de volta
                    jogador.setPosicaoAtual(jogador.getPosicaoAnterior());
                } else {
                    System.out.println("-> [EFEITO] " + jogador.getNome() + " não se moveu anteriormente ainda. Permanece no lugar.");
                }
            }
        ));
    }

    /**
     * Recria o baralho empilhando as cartas modelos de forma embaralhada.
     */
    public void reconstituirBaralho() {
        System.out.println("\n[SISTEMA DE BARALHO] O baralho de cartas esvaziou! Reconstituindo e embaralhando o deck...");
        pilhaCartas.limpar();
        for (Carta carta : modeloCartas) {
            pilhaCartas.empilhar(carta);
        }
        pilhaCartas.embaralhar();
        System.out.println("[SISTEMA DE BARALHO] Deck pronto com " + pilhaCartas.getTamanho() + " cartas empilhadas.");
    }

    /**
     * Saca uma carta do topo do baralho (Pilha).
     * Se a pilha esvaziar após o saque ou estiver vazia no momento, ela é reabastecida automaticamente.
     * 
     * @return A carta sacada do topo.
     */
    public Carta sacar() {
        if (pilhaCartas.estaVazia()) {
            reconstituirBaralho();
        }
        Carta cartaSacada = pilhaCartas.desempilhar();
        // Verifica se com esse saque esvaziou, preparando para o próximo saque
        if (pilhaCartas.estaVazia()) {
            System.out.println("[SISTEMA DE BARALHO] Alerta: A última carta foi sacada!");
        }
        return cartaSacada;
    }

    public int getCartasRestantes() {
        return pilhaCartas.getTamanho();
    }
}
