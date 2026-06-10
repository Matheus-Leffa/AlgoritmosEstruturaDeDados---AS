package estrutura;

/**
 * Nó genérico que compõe a estrutura de dados Pilha.
 * @param <T> O tipo do dado armazenado no nó.
 */
public class NoPilha<T> {
    private T dado;
    private NoPilha<T> proximo;

    public NoPilha(T dado) {
        this.dado = dado;
        this.proximo = null;
    }

    public T getDado() {
        return dado;
    }

    public void setDado(T dado) {
        this.dado = dado;
    }

    public NoPilha<T> getProximo() {
        return proximo;
    }

    public void setProximo(NoPilha<T> proximo) {
        this.proximo = proximo;
    }
}
