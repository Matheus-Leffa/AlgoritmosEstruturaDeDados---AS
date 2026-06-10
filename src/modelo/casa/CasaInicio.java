package modelo.casa;

/**
 * Representa a casa de partida (Ponto de Início) do tabuleiro.
 * Ao passar ou cair nesta casa, o jogador geralmente recebe um bônus financeiro.
 */
public class CasaInicio extends Casa {
    private double valorBonificacao;

    public CasaInicio(int id, String nome, String descricao, double valorBonificacao) {
        super(id, nome, descricao);
        this.valorBonificacao = valorBonificacao;
    }

    public double getValorBonificacao() {
        return valorBonificacao;
    }

    public void setValorBonificacao(double valorBonificacao) {
        this.valorBonificacao = valorBonificacao;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" [Bônus de Passagem: R$ %.2f]", valorBonificacao);
    }
}
