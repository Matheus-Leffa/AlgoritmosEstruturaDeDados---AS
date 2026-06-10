package estrutura;

import modelo.casa.Casa;
import modelo.casa.CasaInicio;

/**
 * Estrutura de dados que representa o tabuleiro do jogo utilizando uma
 * Lista Duplamente Ligada Circular de casas.
 */
public class ListaDuplamenteLigadaCircular {
    private NoTabuleiro cabeca;
    private NoTabuleiro cauda;
    private int tamanho;

    public ListaDuplamenteLigadaCircular() {
        this.cabeca = null;
        this.cauda = null;
        this.tamanho = 0;
    }

    public boolean estaVazia() {
        return cabeca == null;
    }

    public int getTamanho() {
        return tamanho;
    }

    public NoTabuleiro getCabeca() {
        return cabeca;
    }

    public NoTabuleiro getCauda() {
        return cauda;
    }

    /**
     * Insere uma nova casa no tabuleiro (ao final da lista).
     * Reconecta automaticamente a cauda de volta à cabeça para manter a circularidade.
     */
    public void adicionar(Casa casa) {
        NoTabuleiro novoNo = new NoTabuleiro(casa);
        if (estaVazia()) {
            cabeca = novoNo;
            cauda = novoNo;
            novoNo.setProximo(novoNo);
            novoNo.setAnterior(novoNo);
        } else {
            cauda.setProximo(novoNo);
            novoNo.setAnterior(cauda);
            novoNo.setProximo(cabeca);
            cabeca.setAnterior(novoNo);
            cauda = novoNo;
        }
        tamanho++;
    }

    /**
     * Busca um nó no tabuleiro pelo ID da casa correspondente.
     */
    public NoTabuleiro buscarPorId(int id) {
        if (estaVazia()) {
            return null;
        }
        NoTabuleiro atual = cabeca;
        do {
            if (atual.getCasa().getId() == id) {
                return atual;
            }
            atual = atual.getProximo();
        } while (atual != cabeca);

        return null;
    }

    /**
     * Busca um nó no tabuleiro pelo nome exato (desconsiderando maiúsculas e minúsculas).
     */
    public NoTabuleiro buscarPorNome(String nome) {
        if (estaVazia() || nome == null) {
            return null;
        }
        NoTabuleiro atual = cabeca;
        do {
            if (atual.getCasa().getNome().equalsIgnoreCase(nome)) {
                return atual;
            }
            atual = atual.getProximo();
        } while (atual != cabeca);

        return null;
    }

    /**
     * Remove uma casa do tabuleiro pelo seu ID, atualizando os ponteiros da lista circular.
     */
    public boolean removerPorId(int id) {
        if (estaVazia()) {
            return false;
        }

        NoTabuleiro alvo = buscarPorId(id);
        if (alvo == null) {
            return false;
        }

        if (tamanho == 1) {
            cabeca = null;
            cauda = null;
        } else {
            NoTabuleiro anterior = alvo.getAnterior();
            NoTabuleiro proximo = alvo.getProximo();

            anterior.setProximo(proximo);
            proximo.setAnterior(anterior);

            if (alvo == cabeca) {
                cabeca = proximo;
            }
            if (alvo == cauda) {
                cauda = anterior;
            }
        }
        tamanho--;
        return true;
    }

    /**
     * Percorre e imprime o tabuleiro completo no sentido horário (avançando).
     */
    public void percorrerTabuleiroCompleto() {
        if (estaVazia()) {
            System.out.println("Tabuleiro vazio.");
            return;
        }
        NoTabuleiro atual = cabeca;
        System.out.println("\n--- TABULEIRO COMPLETO (SENTIDO HORÁRIO) ---");
        do {
            System.out.println(atual.getCasa());
            atual = atual.getProximo();
        } while (atual != cabeca);
        System.out.println("--------------------------------------------");
    }

    /**
     * Percorre e imprime o tabuleiro completo no sentido anti-horário (recuando).
     * Demonstra a navegação para trás via ponteiro anterior.
     */
    public void percorrerTabuleiroCompletoReverso() {
        if (estaVazia()) {
            System.out.println("Tabuleiro vazio.");
            return;
        }
        NoTabuleiro atual = cauda;
        System.out.println("\n--- TABULEIRO COMPLETO (SENTIDO ANTI-HORÁRIO) ---");
        do {
            System.out.println(atual.getCasa());
            atual = atual.getAnterior();
        } while (atual != cauda);
        System.out.println("-------------------------------------------------");
    }

    /**
     * Move o jogador de um nó a outro por N passos e detecta passagem pelo Início (CasaInicio).
     *
     * @param atual A posição inicial do jogador
     * @param passos Quantidade de posições a andar para a frente
     * @param acaoAoPassar Ação a ser executada a cada passagem pela CasaInicio (ex: receber bônus)
     * @return O nó correspondente à posição final de parada
     */
    public NoTabuleiro moverEVerificarPassagemInicio(NoTabuleiro atual, int passos, Runnable acaoAoPassar) {
        if (atual == null || passos <= 0) {
            return atual;
        }

        NoTabuleiro ponteiro = atual;
        for (int i = 0; i < passos; i++) {
            ponteiro = ponteiro.getProximo();
            // Se passar ou cair em uma CasaInicio
            if (ponteiro.getCasa() instanceof CasaInicio) {
                if (acaoAoPassar != null) {
                    acaoAoPassar.run();
                }
            }
        }
        return ponteiro;
    }
}
