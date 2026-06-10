package modelo.casa;

/**
 * Representa uma casa de Sorte ou Revés no tabuleiro,
 * onde o jogador deve sacar uma carta do baralho correspondente.
 */
public class CasaSorteReves extends Casa {
    private boolean ehSorte; // true para Sorte, false para Revés

    public CasaSorteReves(int id, String nome, String descricao, boolean ehSorte) {
        super(id, nome, descricao);
        this.ehSorte = ehSorte;
    }

    public boolean isEhSorte() {
        return ehSorte;
    }

    public void setEhSorte(boolean ehSorte) {
        this.ehSorte = ehSorte;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" [Tipo: %s]", ehSorte ? "Sorte" : "Revés");
    }
}
