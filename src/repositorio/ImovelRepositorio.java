package repositorio;

import modelo.casa.CasaImovel;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositório responsável pelo gerenciamento de persistência em memória
 * e validações dos imóveis do tabuleiro durante a fase de pré-jogo.
 */
public class ImovelRepositorio {
    private final List<CasaImovel> imoveis;
    private static final int MAX_IMOVEIS = 40;

    public ImovelRepositorio() {
        this.imoveis = new ArrayList<>();
    }

    /**
     * Cadastra um novo imóvel no repositório.
     * Valida se o limite máximo de 40 imóveis não foi ultrapassado.
     *
     * @param imovel Instância do imóvel a ser cadastrado.
     * @throws IllegalArgumentException Caso o limite de imóveis seja excedido ou ID/Nome seja inválido.
     */
    public void adicionar(CasaImovel imovel) {
        if (imovel == null) {
            throw new IllegalArgumentException("Imóvel não pode ser nulo.");
        }
        if (imoveis.size() >= MAX_IMOVEIS) {
            throw new IllegalArgumentException("Limite de " + MAX_IMOVEIS + " imóveis cadastrados no tabuleiro já foi atingido.");
        }
        // Evita IDs duplicados
        for (CasaImovel i : imoveis) {
            if (i.getId() == imovel.getId()) {
                throw new IllegalArgumentException("Imóvel com ID '" + imovel.getId() + "' já cadastrado.");
            }
        }
        imoveis.add(imovel);
    }

    /**
     * Retorna a lista contendo todos os imóveis cadastrados.
     *
     * @return Lista de imóveis cadastrados.
     */
    public List<CasaImovel> listar() {
        return new ArrayList<>(imoveis);
    }

    /**
     * Busca um imóvel específico pelo seu identificador (ID).
     *
     * @param id Identificador numérico do imóvel.
     * @return O imóvel encontrado, ou null caso não exista.
     */
    public CasaImovel buscarPorId(int id) {
        for (CasaImovel i : imoveis) {
            if (i.getId() == id) {
                return i;
            }
        }
        return null;
    }

    /**
     * Atualiza as informações de um imóvel cadastrado.
     *
     * @param imovelAtualizado Objeto contendo os dados atualizados do imóvel.
     * @return true se o imóvel foi atualizado com sucesso, false caso contrário.
     */
    public boolean atualizar(CasaImovel imovelAtualizado) {
        if (imovelAtualizado == null) return false;
        CasaImovel existente = buscarPorId(imovelAtualizado.getId());
        if (existente != null) {
            existente.setNome(imovelAtualizado.getNome());
            existente.setDescricao(imovelAtualizado.getDescricao());
            existente.setPrecoCompra(imovelAtualizado.getPrecoCompra());
            existente.setAluguelBase(imovelAtualizado.getAluguelBaseOriginal());
            existente.setAluguelPorCasa(imovelAtualizado.getAluguelPorCasa());
            existente.setCustoConstrucaoCasa(imovelAtualizado.getCustoConstrucaoCasa());
            existente.setCorGrupo(imovelAtualizado.getCorGrupo());
            existente.setQuantidadeCasas(imovelAtualizado.getQuantidadeCasas());
            existente.setTemHotel(imovelAtualizado.isTemHotel());
            existente.setProprietario(imovelAtualizado.getProprietario());
            return true;
        }
        return false;
    }

    /**
     * Remove um imóvel cadastrado a partir de seu ID.
     *
     * @param id Identificador do imóvel a ser removido.
     * @return true se o imóvel foi removido, false caso contrário.
     */
    public boolean remover(int id) {
        CasaImovel existente = buscarPorId(id);
        if (existente != null) {
            return imoveis.remove(existente);
        }
        return false;
    }

    /**
     * Retorna a quantidade de imóveis cadastrados no momento.
     *
     * @return Número de imóveis no repositório.
     */
    public int getQuantidade() {
        return imoveis.size();
    }
}
