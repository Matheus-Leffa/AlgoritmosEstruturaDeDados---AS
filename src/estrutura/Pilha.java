package estrutura;

import java.util.Random;

/**
 * Estrutura de dados Pilha Genérica (LIFO - Last In, First Out).
 * Implementada dinamicamente utilizando encadeamento de nós.
 * @param <T> O tipo do dado armazenado na pilha.
 */
public class Pilha<T> {
    private NoPilha<T> topo;
    private int tamanho;

    public Pilha() {
        this.topo = null;
        this.tamanho = 0;
    }

    public boolean estaVazia() {
        return topo == null;
    }

    public int getTamanho() {
        return tamanho;
    }

    /**
     * Empilha um novo elemento no topo da pilha.
     */
    public void empilhar(T dado) {
        NoPilha<T> novoNo = new NoPilha<>(dado);
        novoNo.setProximo(topo);
        topo = novoNo;
        tamanho++;
    }

    /**
     * Desempilha e retorna o elemento que está no topo da pilha.
     * Retorna null caso a pilha esteja vazia.
     */
    public T desempilhar() {
        if (estaVazia()) {
            return null;
        }
        T dado = topo.getDado();
        topo = topo.getProximo();
        tamanho--;
        return dado;
    }

    /**
     * Retorna o elemento do topo da pilha sem removê-lo.
     * Retorna null caso a pilha esteja vazia.
     */
    public T espiar() {
        if (estaVazia()) {
            return null;
        }
        return topo.getDado();
    }

    /**
     * Esvazia completamente a pilha.
     */
    public void limpar() {
        this.topo = null;
        this.tamanho = 0;
    }

    /**
     * Embaralha os elementos atualmente presentes na pilha utilizando o algoritmo de Fisher-Yates.
     */
    @SuppressWarnings("unchecked")
    public void embaralhar() {
        if (tamanho <= 1) {
            return;
        }

        // 1. Converte a pilha para um vetor temporário
        Object[] temp = new Object[tamanho];
        NoPilha<T> atual = topo;
        int i = 0;
        while (atual != null) {
            temp[i++] = atual.getDado();
            atual = atual.getProximo();
        }

        // 2. Aplica Fisher-Yates Shuffle no vetor
        Random rand = new Random();
        for (int j = temp.length - 1; j > 0; j--) {
            int indiceAleatorio = rand.nextInt(j + 1);
            Object backup = temp[j];
            temp[j] = temp[indiceAleatorio];
            temp[indiceAleatorio] = backup;
        }

        // 3. Reconstrói a pilha a partir do vetor embaralhado
        this.topo = null;
        this.tamanho = 0;
        for (Object item : temp) {
            this.empilhar((T) item);
        }
    }
}
