package modelo;

import estrutura.NoTabuleiro;

/**
 * Representação completa de um Jogador, integrando seu saldo financeiro,
 * posição atual no tabuleiro e histórico de movimentação para o sistema de cartas.
 */
public class Jogador {
    private String id;
    private String nome;
    private double saldo;
    private NoTabuleiro posicaoAtual;
    private NoTabuleiro posicaoAnterior;

    public Jogador(String id, String nome, double saldo) {
        this.id = id;
        this.nome = nome;
        this.saldo = saldo;
        this.posicaoAtual = null;
        this.posicaoAnterior = null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public NoTabuleiro getPosicaoAtual() {
        return posicaoAtual;
    }

    /**
     * Define a nova posição atual do jogador, guardando a posição antiga
     * como a posição anterior (histórico de movimentação).
     */
    public void setPosicaoAtual(NoTabuleiro novaPosicao) {
        this.posicaoAnterior = this.posicaoAtual;
        this.posicaoAtual = novaPosicao;
    }

    public NoTabuleiro getPosicaoAnterior() {
        return posicaoAnterior;
    }

    public void setPosicaoAnterior(NoTabuleiro posicaoAnterior) {
        this.posicaoAnterior = posicaoAnterior;
    }

    @Override
    public String toString() {
        String nomePosicao = (posicaoAtual != null) ? posicaoAtual.getCasa().getNome() : "Fora do tabuleiro";
        return String.format("%s [Saldo: R$ %.2f | Pos: %s]", nome, saldo, nomePosicao);
    }
}
