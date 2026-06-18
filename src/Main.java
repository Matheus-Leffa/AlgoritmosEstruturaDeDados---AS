import estrutura.ListaDuplamenteLigadaCircular;
import modelo.Jogador;
import modelo.TipoPersonagem;
import modelo.casa.*;
import servico.Jogo;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe principal para execução e simulação automática de uma partida completa
 * do Monopoly Estratégico utilizando as estruturas de dados personalizadas.
 */
public class Main {
    public static void main(String[] args) {
        // 1. Instanciando o Tabuleiro Circular
        ListaDuplamenteLigadaCircular tabuleiro = new ListaDuplamenteLigadaCircular();

        tabuleiro.adicionar(new CasaInicio(0, "Ponto de Partida", "Início do tabuleiro. Receba R$ 200 ao passar.", 200.0));
        tabuleiro.adicionar(new CasaImovel(1, "Avenida Paulista", "Área comercial nobre.", 350.0, 35.0, 50.0, 120.0, "Azul"));
        tabuleiro.adicionar(new CasaImposto(2, "Imposto de Renda", "Tributo compulsório.", 150.0));
        tabuleiro.adicionar(new CasaSorteReves(3, "Cartas de Sorte ou Revés", "Saca uma carta da pilha.", true));
        tabuleiro.adicionar(new CasaRestituicao(4, "Restituição Fiscal", "Estorno de impostos pagos.", 100.0));
        tabuleiro.adicionar(new CasaImovel(5, "Copacabana", "Orla marítima turística.", 300.0, 25.0, 40.0, 100.0, "Verde"));

        // 2. Criando os Jogadores e definindo seus Personagens Estratégicos
        List<Jogador> jogadores = new ArrayList<>();
        jogadores.add(new Jogador("1", "Matheus", 1200.0, TipoPersonagem.ESPECULADOR));
        jogadores.add(new Jogador("2", "Ana", 1200.0, TipoPersonagem.NEGOCIANTE));
        jogadores.add(new Jogador("3", "Bruno", 1200.0, TipoPersonagem.ADVOGADO));
        jogadores.add(new Jogador("4", "Carla", 1200.0, TipoPersonagem.CONSTRUTOR));

        System.out.println("=========================================================");
        System.out.println("         MONOPOLY ESTRATÉGICO - CONFIGURAÇÃO             ");
        System.out.println("=========================================================");
        System.out.println("Tabuleiro carregado com " + tabuleiro.getTamanho() + " casas.");
        System.out.println("Jogadores prontos na mesa:");
        for (Jogador j : jogadores) {
            System.out.println(" - " + j.getNome() + " | Classe: " + j.getTipoPersonagem() + " | Saldo Inicial: R$ " + j.getSaldo());
        }

        // 3. Inicializando a Partida e iniciando a simulação do jogo
        Jogo partida = new Jogo(tabuleiro, jogadores);
        partida.iniciarPartida();
    }
}