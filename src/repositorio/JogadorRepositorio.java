package repositorio;

import modelo.Jogador;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositório responsável pelo gerenciamento de persistência em memória
 * e validações dos jogadores da partida durante a fase de pré-jogo.
 */
public class JogadorRepositorio {
    private final List<Jogador> jogadores;
    private static final int MAX_JOGADORES = 6;

    public JogadorRepositorio() {
        this.jogadores = new ArrayList<>();
    }

    /**
     * Cadastra um novo jogador no repositório.
     * Valida se o limite máximo de 6 jogadores não foi ultrapassado.
     *
     * @param jogador Instância do jogador a ser cadastrado.
     * @throws IllegalArgumentException Caso o limite de jogadores seja excedido ou ID/Nome seja inválido.
     */
    public void adicionar(Jogador jogador) {
        if (jogador == null) {
            throw new IllegalArgumentException("Jogador não pode ser nulo.");
        }
        if (jogadores.size() >= MAX_JOGADORES) {
            throw new IllegalArgumentException("Limite de " + MAX_JOGADORES + " jogadores alcançado. Não é possível cadastrar mais.");
        }
        // Evita IDs duplicados
        for (Jogador j : jogadores) {
            if (j.getId().equals(jogador.getId())) {
                throw new IllegalArgumentException("Jogador com ID '" + jogador.getId() + "' já cadastrado.");
            }
        }
        jogadores.add(jogador);
    }

    /**
     * Retorna a lista contendo todos os jogadores cadastrados.
     *
     * @return Lista de jogadores ativos no pré-jogo.
     */
    public List<Jogador> listar() {
        return new ArrayList<>(jogadores);
    }

    /**
     * Busca um jogador específico pelo seu identificador (ID).
     *
     * @param id Identificador do jogador.
     * @return O jogador encontrado, ou null caso não exista.
     */
    public Jogador buscarPorId(String id) {
        if (id == null) return null;
        for (Jogador j : jogadores) {
            if (j.getId().equals(id)) {
                return j;
            }
        }
        return null;
    }

    /**
     * Atualiza as informações de um jogador cadastrado.
     *
     * @param jogadorAtualizado Objeto contendo os dados atualizados do jogador.
     * @return true se o jogador foi atualizado com sucesso, false caso contrário.
     */
    public boolean atualizar(Jogador jogadorAtualizado) {
        if (jogadorAtualizado == null) return false;
        Jogador existente = buscarPorId(jogadorAtualizado.getId());
        if (existente != null) {
            existente.setNome(jogadorAtualizado.getNome());
            existente.setSaldo(jogadorAtualizado.getSaldo());
            existente.setVoltasCompletas(jogadorAtualizado.getVoltasCompletas());
            existente.setFalido(jogadorAtualizado.isFalido());
            existente.setPreso(jogadorAtualizado.isPreso());
            existente.setPosicaoAtual(jogadorAtualizado.getPosicaoAtual());
            existente.setPosicaoAnterior(jogadorAtualizado.getPosicaoAnterior());
            return true;
        }
        return false;
    }

    /**
     * Remove um jogador cadastrado a partir de seu ID.
     *
     * @param id Identificador do jogador a ser removido.
     * @return true se o jogador foi removido, false caso contrário.
     */
    public boolean remover(String id) {
        Jogador existente = buscarPorId(id);
        if (existente != null) {
            return jogadores.remove(existente);
        }
        return false;
    }

    /**
     * Retorna a quantidade de jogadores cadastrados no momento.
     *
     * @return Número de jogadores na lista.
     */
    public int getQuantidade() {
        return jogadores.size();
    }
}
