package modelo.carta;

import modelo.Jogador;
import estrutura.ListaDuplamenteLigadaCircular;
import java.util.List;

/**
 * Interface funcional que define o contrato para aplicação de efeitos das cartas.
 * Permite o uso de expressões Lambda para criar efeitos variados de forma limpa.
 */
@FunctionalInterface
public interface EfeitoCarta {
    /**
     * Aplica o efeito específico da carta sobre o jogador e/ou tabuleiro.
     * 
     * @param jogadorAtivo O jogador que sacou a carta.
     * @param todosJogadores A lista de todos os jogadores na partida (para efeitos coletivos).
     * @param tabuleiro O tabuleiro circular do jogo (para efeitos de movimentação).
     */
    void aplicar(Jogador jogadorAtivo, List<Jogador> todosJogadores, ListaDuplamenteLigadaCircular tabuleiro);
}
