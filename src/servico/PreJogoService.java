package servico;

import estrutura.ListaDuplamenteLigadaCircular;
import modelo.Jogador;
import modelo.casa.*;
import repositorio.ImovelRepositorio;
import repositorio.JogadorRepositorio;
import java.util.List;

/**
 * Serviço responsável por validar as condições pré-jogo e iniciar a partida
 * montando o tabuleiro de forma dinâmica com base nos imóveis e jogadores cadastrados.
 */
public class PreJogoService {
    private final JogadorRepositorio jogadorRepositorio;
    private final ImovelRepositorio imovelRepositorio;

    public PreJogoService(JogadorRepositorio jogadorRepositorio, ImovelRepositorio imovelRepositorio) {
        if (jogadorRepositorio == null || imovelRepositorio == null) {
            throw new IllegalArgumentException("Os repositórios de jogador e imóvel não podem ser nulos.");
        }
        this.jogadorRepositorio = jogadorRepositorio;
        this.imovelRepositorio = imovelRepositorio;
    }

    public JogadorRepositorio getJogadorRepositorio() {
        return jogadorRepositorio;
    }

    public ImovelRepositorio getImovelRepositorio() {
        return imovelRepositorio;
    }

    /**
     * Valida se a partida cumpre todos os requisitos mínimos e máximos de configuração.
     *
     * @throws IllegalStateException Caso os requisitos não sejam atendidos.
     */
    public void validarConfiguracao() {
        int totalImoveis = imovelRepositorio.getQuantidade();
        int totalJogadores = jogadorRepositorio.getQuantidade();

        if (totalJogadores < 2) {
            throw new IllegalStateException("Configuração inválida: Mínimo de 2 jogadores necessários para iniciar a partida (Atualmente cadastrados: " + totalJogadores + ").");
        }
        if (totalJogadores > 6) {
            throw new IllegalStateException("Configuração inválida: Máximo de 6 jogadores permitidos (Atualmente cadastrados: " + totalJogadores + ").");
        }
        if (totalImoveis < 10) {
            throw new IllegalStateException("Configuração inválida: Mínimo de 10 imóveis necessários para montar o tabuleiro e iniciar a partida (Atualmente cadastrados: " + totalImoveis + ").");
        }
        if (totalImoveis > 40) {
            throw new IllegalStateException("Configuração inválida: Máximo de 40 imóveis permitidos no tabuleiro (Atualmente cadastrados: " + totalImoveis + ").");
        }
    }

    /**
     * Executa a montagem dinâmica do tabuleiro circular e inicializa o objeto do Jogo.
     * Intercala de forma inteligente as casas especiais no tabuleiro com base no número de imóveis cadastrados.
     *
     * @return Instância de Jogo configurada e pronta para rodar.
     * @throws IllegalStateException Caso a validação de pré-jogo falhe.
     */
    public Jogo criarPartida() {
        // 1. Executa validações de pré-jogo
        validarConfiguracao();

        // 2. Constrói o tabuleiro circular
        ListaDuplamenteLigadaCircular tabuleiro = new ListaDuplamenteLigadaCircular();

        // Sempre inicia com o Ponto de Partida
        tabuleiro.adicionar(new CasaInicio(0, "Ponto de Partida", "Início do tabuleiro. Receba R$ 200 ao passar.", 200.0));

        List<CasaImovel> imoveisList = imovelRepositorio.listar();
        int idEspecial = 1000; // Intervalo de IDs reservado para casas dinâmicas especiais

        for (int i = 0; i < imoveisList.size(); i++) {
            // Adiciona o imóvel cadastrado
            tabuleiro.adicionar(imoveisList.get(i));

            // Intercala Casa Sorte/Revés a cada 3 imóveis
            if ((i + 1) % 3 == 0) {
                tabuleiro.adicionar(new CasaSorteReves(idEspecial++, "Sorte ou Revés", "Saca uma carta da pilha de cartas.", true));
            }
            // Intercala Casa de Imposto a cada 5 imóveis
            if ((i + 1) % 5 == 0) {
                tabuleiro.adicionar(new CasaImposto(idEspecial++, "Imposto de Renda Extra", "Tributo especial cobrado pelo banco.", 150.0));
            }
            // Intercala Casa de Restituição a cada 7 imóveis
            if ((i + 1) % 7 == 0) {
                tabuleiro.adicionar(new CasaRestituicao(idEspecial++, "Restituição Fiscal", "Reembolso e devolução de créditos de tributos.", 100.0));
            }
        }

        // 3. Cria a instância de controle da partida com o tabuleiro e a lista de jogadores ativos
        return new Jogo(tabuleiro, jogadorRepositorio.listar());
    }
}
