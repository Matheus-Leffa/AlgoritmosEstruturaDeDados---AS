package modelo;

/**
 * Representação simplificada (stub) de um Jogador para fins de compilação
 * e associação de propriedades.
 */
public class Jogador {
    private String id;
    private String nome;
    private double saldo;

    public Jogador(String id, String nome, double saldo) {
        this.id = id;
        this.nome = nome;
        this.saldo = saldo;
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

    @Override
    public String toString() {
        return String.format("%s (Saldo: R$ %.2f)", nome, saldo);
    }
}
