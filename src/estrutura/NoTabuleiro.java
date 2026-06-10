package estrutura;

import modelo.casa.Casa;

/**
 * Nó que compõe a Lista Duplamente Ligada Circular do tabuleiro.
 * Contém uma referência para a Casa de tabuleiro e referências para os nós vizinhos.
 */
public class NoTabuleiro {
    private Casa casa;
    private NoTabuleiro proximo;
    private NoTabuleiro anterior;

    public NoTabuleiro(Casa casa) {
        this.casa = casa;
        this.proximo = null;
        this.anterior = null;
    }

    public Casa getCasa() {
        return casa;
    }

    public void setCasa(Casa casa) {
        this.casa = casa;
    }

    public NoTabuleiro getProximo() {
        return proximo;
    }

    public void setProximo(NoTabuleiro proximo) {
        this.proximo = proximo;
    }

    public NoTabuleiro getAnterior() {
        return anterior;
    }

    public void setAnterior(NoTabuleiro anterior) {
        this.anterior = anterior;
    }

    @Override
    public String toString() {
        return casa != null ? casa.toString() : "Nó Vazio";
    }
}
