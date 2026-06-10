import estrutura.ListaDuplamenteLigadaCircular;
import estrutura.NoTabuleiro;
import modelo.Jogador;
import modelo.casa.*;
import modelo.carta.Baralho;
import modelo.carta.Carta;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe principal para demonstração e teste das estruturas de dados do tabuleiro
 * e do sistema de cartas baseado em Pilha.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=========================================================");
        System.out.println("   SIMULADOR DE TABULEIRO E BARALHO - ESTRUTURA DE DADOS ");
        System.out.println("=========================================================\n");

        // ==========================================
        // PARTE 1: TABULEIRO (LISTA CIRCULAR DUPLAMENTE LIGADA)
        // ==========================================
        System.out.println(">>> PARTE 1: TESTE DO TABULEIRO (LISTA CIRCULAR DUPLAMENTE LIGADA) <<<\n");
        ListaDuplamenteLigadaCircular tabuleiro = new ListaDuplamenteLigadaCircular();

        tabuleiro.adicionar(new CasaInicio(0, "Ponto de Partida", "Início do tabuleiro. Receba R$ 200 ao passar.", 200.0));
        tabuleiro.adicionar(new CasaImovel(1, "Avenida Paulista", "Bairro comercial e financeiro.", 350.0, 30.0, 50.0, 100.0, "Azul"));
        tabuleiro.adicionar(new CasaImposto(2, "Imposto sobre Fortuna", "Taxa obrigatória ao passar por aqui.", 150.0));
        tabuleiro.adicionar(new CasaSorteReves(3, "Sorte ou Revés Leste", "Compre uma carta de Sorte ou Revés.", true));
        tabuleiro.adicionar(new CasaRestituicao(4, "Restituição de Imposto", "Restituição de valores tributários excedentes.", 100.0));
        tabuleiro.adicionar(new CasaImovel(5, "Copacabana", "Orla turística charmosa.", 300.0, 25.0, 45.0, 80.0, "Verde"));

        System.out.println("-> Tabuleiro criado com sucesso contendo " + tabuleiro.getTamanho() + " casas.");

        // Percorrer tabuleiro completo
        tabuleiro.percorrerTabuleiroCompleto();
        tabuleiro.percorrerTabuleiroCompletoReverso();

        // Busca
        System.out.println("\n--- TESTANDO BUSCA DE CASAS ---");
        NoTabuleiro buscaId = tabuleiro.buscarPorId(2);
        if (buscaId != null) {
            System.out.println("[Busca por ID 2] Encontrado: " + buscaId.getCasa());
        }
        NoTabuleiro buscaNome = tabuleiro.buscarPorNome("Copacabana");
        if (buscaNome != null) {
            System.out.println("[Busca por Nome 'Copacabana'] Encontrado: " + buscaNome.getCasa());
        }

        // Navegação
        System.out.println("\n--- TESTANDO NAVEGAÇÃO HORÁRIA/ANTI-HORÁRIA ---");
        NoTabuleiro atual = tabuleiro.getCabeca();
        System.out.println("Posição Inicial: " + atual.getCasa().getNome());
        atual = atual.getProximo();
        System.out.println("Avançou 1 casa (Próximo): " + atual.getCasa().getNome());
        atual = atual.getAnterior();
        System.out.println("Recuou 1 casa (Anterior): " + atual.getCasa().getNome());


        // ==========================================
        // PARTE 2: SISTEMA DE CARTAS (PILHA)
        // ==========================================
        System.out.println("\n\n>>> PARTE 2: TESTE DO BARALHO DE CARTAS (PILHA) <<<\n");

        // 1. Criando os jogadores ativos para teste
        List<Jogador> jogadores = new ArrayList<>();
        Jogador j1 = new Jogador("1", "Matheus", 1000.0);
        Jogador j2 = new Jogador("2", "Ana", 1000.0);
        Jogador j3 = new Jogador("3", "Bruno", 1000.0);
        
        jogadores.add(j1);
        jogadores.add(j2);
        jogadores.add(j3);

        // Inicializa a posição de todos no Ponto de Partida
        for (Jogador j : jogadores) {
            j.setPosicaoAtual(tabuleiro.getCabeca());
        }

        System.out.println("--- JOGADORES INICIAIS ---");
        for (Jogador j : jogadores) {
            System.out.println(j);
        }

        // 2. Instanciando o Baralho (Pilhas automáticas e embaralhamento)
        Baralho baralho = new Baralho();

        // 3. Realizando saques sucessivos (14 vezes) para testar os efeitos de ganho,
        // penalidade, movimentação e a RECRAÇÃO automática do baralho quando vazio (12 cartas no deck).
        System.out.println("\nIniciando rodadas de saques de cartas (demonstração de efeitos e recarga do deck)...");
        
        for (int rodada = 1; rodada <= 14; rodada++) {
            System.out.println("\n=========================================================");
            System.out.println(" RODADA DE SAQUE #" + rodada + " | Cartas no deck antes do saque: " + baralho.getCartasRestantes());
            System.out.println("=========================================================");

            // Alterna o jogador ativo a cada rodada
            Jogador jogadorAtivo = jogadores.get((rodada - 1) % jogadores.size());
            System.out.println("Jogador da vez: " + jogadorAtivo.getNome() + " (Saldo: R$ " + jogadorAtivo.getSaldo() + ")");

            // Saca a carta
            Carta carta = baralho.sacar();

            // Executa o efeito da carta
            carta.executar(jogadorAtivo, jogadores, tabuleiro);

            // Exibe a situação atualizada de todos os jogadores pós-carta
            System.out.println("\n--- ESTADO DOS JOGADORES APÓS A CARTA ---");
            for (Jogador j : jogadores) {
                System.out.println(j);
            }
        }
        System.out.println("\n=========================================================");
        System.out.println("   FIM DA DEMONSTRAÇÃO E TESTES DAS ESTRUTURAS            ");
        System.out.println("=========================================================");
    }
}