package modelo.casa;

/**
 * Representa uma casa onde o jogador recebe uma restituição de imposto (crédito financeiro).
 */
public class CasaRestituicao extends Casa {
    private double valorRestituicao;

    public CasaRestituicao(int id, String nome, String descricao, double valorRestituicao) {
        super(id, nome, descricao);
        this.valorRestituicao = valorRestituicao;
    }

    public double getValorRestituicao() {
        return valorRestituicao;
    }

    public void setValorRestituicao(double valorRestituicao) {
        this.valorRestituicao = valorRestituicao;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" [Restituição a receber: R$ %.2f]", valorRestituicao);
    }
}
