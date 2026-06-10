package modelo.casa;

/**
 * Representa uma casa onde o jogador deve pagar um imposto/taxa ao banco.
 */
public class CasaImposto extends Casa {
    private double valorImposto;

    public CasaImposto(int id, String nome, String descricao, double valorImposto) {
        super(id, nome, descricao);
        this.valorImposto = valorImposto;
    }

    public double getValorImposto() {
        return valorImposto;
    }

    public void setValorImposto(double valorImposto) {
        this.valorImposto = valorImposto;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" [Imposto a pagar: R$ %.2f]", valorImposto);
    }
}
