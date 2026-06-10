import estrutura.ListaDuplamenteLigadaCircular;
import estrutura.NoTabuleiro;
import modelo.Jogador;
import modelo.casa.*;

/**
 * Classe principal para demonstração e teste das estruturas de dados do tabuleiro.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=========================================================");
        System.out.println("   SIMULADOR DE TABULEIRO - ESTRUTURA DE DADOS JAVA      ");
        System.out.println("=========================================================\n");

        // 1. Instanciando o tabuleiro (Lista Circular Duplamente Ligada)
        ListaDuplamenteLigadaCircular tabuleiro = new ListaDuplamenteLigadaCircular();

        // 2. Inserção de Casas de diferentes tipos (Requisito: Inserção de casas)
        tabuleiro.adicionar(new CasaInicio(0, "Ponto de Partida", "Início do tabuleiro. Receba R$ 200 ao passar.", 200.0));
        tabuleiro.adicionar(new CasaImovel(1, "Avenida Paulista", "Bairro comercial e financeiro.", 350.0, 30.0, 50.0, 100.0, "Azul"));
        tabuleiro.adicionar(new CasaImposto(2, "Imposto sobre Fortuna", "Taxa obrigatória ao passar por aqui.", 150.0));
        tabuleiro.adicionar(new CasaSorteReves(3, "Sorte ou Revés Leste", "Compre uma carta de Sorte ou Revés.", true));
        tabuleiro.adicionar(new CasaRestituicao(4, "Restituição de Imposto", "Restituição de valores tributários excedentes.", 100.0));
        tabuleiro.adicionar(new CasaImovel(5, "Copacabana", "Orla turística charmosa.", 300.0, 25.0, 45.0, 80.0, "Verde"));

        System.out.println("-> Tabuleiro criado com sucesso contendo " + tabuleiro.getTamanho() + " casas.");

        // 3. Percorrer o tabuleiro completo (Requisito: Percorrer o tabuleiro completo)
        tabuleiro.percorrerTabuleiroCompleto();
        tabuleiro.percorrerTabuleiroCompletoReverso();

        // 4. Busca por ID e Nome (Requisito: Busca)
        System.out.println("\n--- TESTANDO BUSCA DE CASAS ---");
        NoTabuleiro buscaId = tabuleiro.buscarPorId(2);
        if (buscaId != null) {
            System.out.println("[Busca por ID 2] Encontrado: " + buscaId.getCasa());
        } else {
            System.out.println("[Busca por ID 2] Casa não encontrada.");
        }

        NoTabuleiro buscaNome = tabuleiro.buscarPorNome("Copacabana");
        if (buscaNome != null) {
            System.out.println("[Busca por Nome 'Copacabana'] Encontrado: " + buscaNome.getCasa());
        } else {
            System.out.println("[Busca por Nome 'Copacabana'] Casa não encontrada.");
        }

        // 5. Navegação para frente e para trás (Requisito: Navegação)
        System.out.println("\n--- TESTANDO NAVEGAÇÃO HORÁRIA/ANTI-HORÁRIA ---");
        NoTabuleiro atual = tabuleiro.getCabeca(); // Ponto de Partida
        System.out.println("Posição Atual: " + atual.getCasa().getNome());

        // Navegar para frente
        atual = atual.getProximo();
        System.out.println("Avançou 1 casa (Próximo): " + atual.getCasa().getNome());

        // Navegar para trás
        atual = atual.getAnterior();
        System.out.println("Recuou 1 casa (Anterior): " + atual.getCasa().getNome());


        // 6. Detectar passagem pelo início (Requisito: Detectar passagem pelo início)
        System.out.println("\n--- TESTANDO DETECÇÃO DE PASSAGEM PELO INÍCIO ---");
        Jogador jogador = new Jogador("1", "Matheus", 1000.0);
        System.out.println("Jogador inicial: " + jogador);

        // Coloca o jogador inicialmente no nó 4 (Restituição de Imposto)
        NoTabuleiro posicaoJogador = tabuleiro.buscarPorId(4);
        System.out.println("Jogador começa em: " + posicaoJogador.getCasa().getNome());

        // Vamos rolar o dado (ex: mover 3 casas para frente)
        // Caminho a percorrer: Restituição de Imposto (4) -> Copacabana (5) -> Ponto de Partida (0) -> Avenida Paulista (1)
        // Deve detectar a passagem pelo Ponto de Partida (ID 0) e adicionar os R$ 200 ao saldo do jogador.
        int passos = 3;
        System.out.println("Jogador rola os dados e avança " + passos + " casas...");

        posicaoJogador = tabuleiro.moverEVerificarPassagemInicio(posicaoJogador, passos, () -> {
            // Ação disparada quando detecta passagem pela CasaInicio
            System.out.println("\n[SISTEMA DE TABULEIRO] -> PASSAGEM PELO INÍCIO DETECTADA!");
            CasaInicio inicio = (CasaInicio) tabuleiro.getCabeca().getCasa();
            double bonus = inicio.getValorBonificacao();
            jogador.setSaldo(jogador.getSaldo() + bonus);
            System.out.println("Jogador " + jogador.getNome() + " recebeu R$ " + bonus + " de bônus! Novo saldo: R$ " + jogador.getSaldo() + "\n");
        });

        System.out.println("Jogador parou na casa: " + posicaoJogador.getCasa().getNome());
        System.out.println("Jogador final: " + jogador);
        System.out.println("\n=========================================================");
    }
}