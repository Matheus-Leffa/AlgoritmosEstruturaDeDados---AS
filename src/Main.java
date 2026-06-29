import modelo.Jogador;
import modelo.TipoPersonagem;
import modelo.casa.Casa;
import modelo.casa.CasaImovel;
import repositorio.ImovelRepositorio;
import repositorio.JogadorRepositorio;
import servico.Jogo;
import servico.PreJogoService;
import estrutura.NoTabuleiro;

import java.util.List;
import java.util.Scanner;

/**
 * Classe principal que gerencia o menu interativo em console para configuração
 * e execução do jogo Monopoly Estratégico.
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static JogadorRepositorio jogadorRepo = new JogadorRepositorio();
    private static ImovelRepositorio imovelRepo = new ImovelRepositorio();
    private static PreJogoService preJogoService = new PreJogoService(jogadorRepo, imovelRepo);
    private static Jogo partida = null;

    public static void main(String[] args) {
        // Inicializa alguns dados de teste para facilitar a demonstração/teste rápido
        inicializarDadosPadrao();

        boolean rodando = true;
        while (rodando) {
            exibirMenuPrincipal();
            int opcao = lerInteiro("Escolha uma opção: ");
            switch (opcao) {
                case 1:
                    gerenciarImoveis();
                    break;
                case 2:
                    gerenciarJogadores();
                    break;
                case 3:
                    configurarPartida();
                    break;
                case 4:
                    iniciarPartida();
                    break;
                case 5:
                    mostrarStatus();
                    break;
                case 6:
                    mostrarRanking();
                    break;
                case 7:
                    exibirTabuleiro();
                    break;
                case 0:
                    System.out.println("\nObrigado por jogar! Encerrando a aplicação...");
                    rodando = false;
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        }
    }

    private static void exibirMenuPrincipal() {
        System.out.println("\n=========================================");
        System.out.println("  JOGO DE TABULEIRO ESTRUTURAS DE DADOS  ");
        System.out.println("=========================================");
        System.out.println("1 - Gerenciar Imóveis");
        System.out.println("2 - Gerenciar Jogadores");
        System.out.println("3 - Configurar Partida");
        System.out.println("4 - Iniciar Partida");
        System.out.println("5 - Exibir Status da Partida");
        System.out.println("6 - Exibir Ranking");
        System.out.println("7 - Visualizar Tabuleiro (Estrutura Circular)");
        System.out.println("0 - Sair");
        System.out.println("=========================================");
    }

    // ==========================================
    // SUBMENU - GERENCIAR IMÓVEIS
    // ==========================================
    private static void gerenciarImoveis() {
        boolean noSubmenu = true;
        while (noSubmenu) {
            System.out.println("\n--- SUBMENU - GERENCIAR IMÓVEIS ---");
            System.out.println("1 - Cadastrar imóvel");
            System.out.println("2 - Listar imóveis");
            System.out.println("3 - Atualizar imóvel");
            System.out.println("4 - Remover imóvel");
            System.out.println("0 - Voltar");
            int opcao = lerInteiro("Escolha uma opção: ");
            switch (opcao) {
                case 1:
                    cadastrarImovel();
                    break;
                case 2:
                    listarImoveis();
                    break;
                case 3:
                    atualizarImovel();
                    break;
                case 4:
                    removerImovel();
                    break;
                case 0:
                    noSubmenu = false;
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private static void cadastrarImovel() {
        System.out.println("\n[CADASTRAR IMÓVEL]");
        int id = lerInteiro("Digite o ID do imóvel: ");
        if (imovelRepo.buscarPorId(id) != null) {
            System.out.println("Erro: Já existe um imóvel com o ID " + id);
            return;
        }
        System.out.print("Digite o Nome do imóvel: ");
        String nome = scanner.nextLine().trim();
        System.out.print("Digite a Descrição do imóvel: ");
        String descricao = scanner.nextLine().trim();
        double precoCompra = lerDouble("Digite o Preço de Compra (R$): ");
        double aluguelBase = lerDouble("Digite o Aluguel Base (R$): ");
        double aluguelPorCasa = lerDouble("Digite o Aluguel Adicional por Casa (R$): ");
        double custoConstrucao = lerDouble("Digite o Custo de Construção de Melhoria (R$): ");
        System.out.print("Digite o Grupo/Cor do imóvel: ");
        String corGrupo = scanner.nextLine().trim();

        try {
            CasaImovel novo = new CasaImovel(id, nome, descricao, precoCompra, aluguelBase, aluguelPorCasa, custoConstrucao, corGrupo);
            imovelRepo.adicionar(novo);
            System.out.println("Imóvel cadastrado com sucesso! Total cadastrados: " + imovelRepo.getQuantidade());
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar imóvel: " + e.getMessage());
        }
    }

    private static void listarImoveis() {
        System.out.println("\n[LISTA DE IMÓVEIS CADASTRADOS]");
        List<CasaImovel> lista = imovelRepo.listar();
        if (lista.isEmpty()) {
            System.out.println("Nenhum imóvel cadastrado no momento.");
        } else {
            for (CasaImovel i : lista) {
                System.out.println(" -> ID: " + i.getId() + " | Nome: " + i.getNome() + " | Grupo: " + i.getCorGrupo() + " | Preço: R$ " + i.getPrecoCompra() + " | Aluguel Base: R$ " + i.getAluguelBaseOriginal());
            }
        }
    }

    private static void atualizarImovel() {
        System.out.println("\n[ATUALIZAR IMÓVEL]");
        int id = lerInteiro("Digite o ID do imóvel que deseja atualizar: ");
        CasaImovel existente = imovelRepo.buscarPorId(id);
        if (existente == null) {
            System.out.println("Erro: Imóvel com ID " + id + " não encontrado.");
            return;
        }

        System.out.print("Digite o novo Nome (atual: " + existente.getNome() + "): ");
        String nome = scanner.nextLine().trim();
        System.out.print("Digite a nova Descrição (atual: " + existente.getDescricao() + "): ");
        String descricao = scanner.nextLine().trim();
        double precoCompra = lerDouble("Digite o novo Preço de Compra (atual: R$ " + existente.getPrecoCompra() + "): ");
        double aluguelBase = lerDouble("Digite o novo Aluguel Base (atual: R$ " + existente.getAluguelBaseOriginal() + "): ");
        double aluguelPorCasa = lerDouble("Digite o novo Aluguel Adicional por Casa (atual: R$ " + existente.getAluguelPorCasa() + "): ");
        double custoConstrucao = lerDouble("Digite o novo Custo de Construção (atual: R$ " + existente.getCustoConstrucaoCasa() + "): ");
        System.out.print("Digite a nova Cor/Grupo (atual: " + existente.getCorGrupo() + "): ");
        String corGrupo = scanner.nextLine().trim();

        CasaImovel atualizado = new CasaImovel(id, nome, descricao, precoCompra, aluguelBase, aluguelPorCasa, custoConstrucao, corGrupo);
        if (imovelRepo.atualizar(atualizado)) {
            System.out.println("Imóvel atualizado com sucesso!");
        } else {
            System.out.println("Erro ao atualizar o imóvel.");
        }
    }

    private static void removerImovel() {
        System.out.println("\n[REMOVER IMÓVEL]");
        int id = lerInteiro("Digite o ID do imóvel a ser removido: ");
        if (imovelRepo.remover(id)) {
            System.out.println("Imóvel removido com sucesso!");
        } else {
            System.out.println("Erro: Imóvel com ID " + id + " não encontrado.");
        }
    }


    // ==========================================
    // SUBMENU - GERENCIAR JOGADORES
    // ==========================================
    private static void gerenciarJogadores() {
        boolean noSubmenu = true;
        while (noSubmenu) {
            System.out.println("\n--- SUBMENU - GERENCIAR JOGADORES ---");
            System.out.println("1 - Cadastrar jogador");
            System.out.println("2 - Listar jogadores");
            System.out.println("3 - Atualizar jogador");
            System.out.println("4 - Remover jogador");
            System.out.println("0 - Voltar");
            int opcao = lerInteiro("Escolha uma opção: ");
            switch (opcao) {
                case 1:
                    cadastrarJogador();
                    break;
                case 2:
                    listarJogadores();
                    break;
                case 3:
                    atualizarJogador();
                    break;
                case 4:
                    removerJogador();
                    break;
                case 0:
                    noSubmenu = false;
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private static void cadastrarJogador() {
        System.out.println("\n[CADASTRAR JOGADOR]");
        System.out.print("Digite o ID/Código do jogador: ");
        String id = scanner.nextLine().trim();
        if (jogadorRepo.buscarPorId(id) != null) {
            System.out.println("Erro: Já existe um jogador com o ID " + id);
            return;
        }
        System.out.print("Digite o Nome do jogador: ");
        String nome = scanner.nextLine().trim();

        System.out.println("Selecione a classe/personagem do jogador:");
        System.out.println("1 - Especulador (+20% salário, +10% imposto)");
        System.out.println("2 - Negociante (-10% aluguel pago)");
        System.out.println("3 - Advogado (Imune à mecânica de prisão)");
        System.out.println("4 - Construtor (+15% aluguel base de seus imóveis)");
        int classeOp = lerInteiroRange("Escolha: ", 1, 4);

        TipoPersonagem tipo = TipoPersonagem.ESPECULADOR;
        if (classeOp == 2) tipo = TipoPersonagem.NEGOCIANTE;
        else if (classeOp == 3) tipo = TipoPersonagem.ADVOGADO;
        else if (classeOp == 4) tipo = TipoPersonagem.CONSTRUTOR;

        try {
            Jogador novo = new Jogador(id, nome, preJogoService.getSaldoInicial(), tipo);
            jogadorRepo.adicionar(novo);
            System.out.println("Jogador cadastrado com sucesso! Total cadastrados: " + jogadorRepo.getQuantidade());
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar jogador: " + e.getMessage());
        }
    }

    private static void listarJogadores() {
        System.out.println("\n[LISTA DE JOGADORES CADASTRADOS]");
        List<Jogador> lista = jogadorRepo.listar();
        if (lista.isEmpty()) {
            System.out.println("Nenhum jogador cadastrado no momento.");
        } else {
            for (Jogador j : lista) {
                System.out.println(" -> ID: " + j.getId() + " | Nome: " + j.getNome() + " | Classe: " + j.getTipoPersonagem() + " | Saldo: R$ " + j.getSaldo());
            }
        }
    }

    private static void atualizarJogador() {
        System.out.println("\n[ATUALIZAR JOGADOR]");
        System.out.print("Digite o ID do jogador que deseja atualizar: ");
        String id = scanner.nextLine().trim();
        Jogador existente = jogadorRepo.buscarPorId(id);
        if (existente == null) {
            System.out.println("Erro: Jogador com ID '" + id + "' não encontrado.");
            return;
        }

        System.out.print("Digite o novo Nome (atual: " + existente.getNome() + "): ");
        String nome = scanner.nextLine().trim();

        System.out.println("Selecione a nova classe (atual: " + existente.getTipoPersonagem() + "):");
        System.out.println("1 - Especulador");
        System.out.println("2 - Negociante");
        System.out.println("3 - Advogado");
        System.out.println("4 - Construtor");
        int classeOp = lerInteiroRange("Escolha: ", 1, 4);

        TipoPersonagem tipo = TipoPersonagem.ESPECULADOR;
        if (classeOp == 2) tipo = TipoPersonagem.NEGOCIANTE;
        else if (classeOp == 3) tipo = TipoPersonagem.ADVOGADO;
        else if (classeOp == 4) tipo = TipoPersonagem.CONSTRUTOR;

        Jogador atualizado = new Jogador(id, nome, existente.getSaldo(), tipo);
        atualizado.setVoltasCompletas(existente.getVoltasCompletas());
        atualizado.setFalido(existente.isFalido());
        atualizado.setPreso(existente.isPreso());
        atualizado.setPosicaoAnterior(existente.getPosicaoAnterior());
        atualizado.setPosicaoAtual(existente.getPosicaoAtual());

        if (jogadorRepo.atualizar(atualizado)) {
            System.out.println("Jogador atualizado com sucesso!");
        } else {
            System.out.println("Erro ao atualizar o jogador.");
        }
    }

    private static void removerJogador() {
        System.out.println("\n[REMOVER JOGADOR]");
        System.out.print("Digite o ID do jogador a ser removido: ");
        String id = scanner.nextLine().trim();
        if (jogadorRepo.remover(id)) {
            System.out.println("Jogador removido com sucesso!");
        } else {
            System.out.println("Erro: Jogador com ID '" + id + "' não encontrado.");
        }
    }


    // ==========================================
    // CONFIGURAR PARTIDA
    // ==========================================
    private static void configurarPartida() {
        System.out.println("\n[CONFIGURAR PARTIDA]");
        double saldo = lerDouble("Digite o Saldo Inicial de cada jogador (R$): ");
        double salario = lerDouble("Digite o Salário ganho ao passar pelo início (R$): ");
        int rodadas = lerInteiro("Digite o número máximo de rodadas: ");

        if (saldo <= 0 || salario <= 0 || rodadas <= 0) {
            System.out.println("Valores inválidos! Todas as configurações devem ser maiores que zero.");
            return;
        }

        preJogoService.setSaldoInicial(saldo);
        preJogoService.setSalarioBase(salario);
        preJogoService.setMaxRodadas(rodadas);
        System.out.println("Configurações atualizadas com sucesso!");
    }


    // ==========================================
    // INICIAR PARTIDA
    // ==========================================
    private static void iniciarPartida() {
        try {
            System.out.println("\n[INICIANDO PARTIDA]");
            // Cria a partida e valida as regras de negócios (mínimo de 10 imóveis, mínimo de 2 jogadores)
            partida = preJogoService.criarPartida();
            partida.iniciarPartida();
        } catch (IllegalStateException e) {
            System.out.println("\nErro ao iniciar a partida: " + e.getMessage());
            System.out.println("Por favor, verifique se os requisitos mínimos foram atendidos.");
        }
    }


    // ==========================================
    // EXIBIR STATUS DA PARTIDA
    // ==========================================
    private static void mostrarStatus() {
        if (partida == null) {
            System.out.println("\nNenhuma partida foi iniciada ou configurada ainda.");
            return;
        }

        System.out.println("\n=========================================");
        System.out.println("           STATUS DA PARTIDA             ");
        System.out.println("=========================================");
        System.out.println("Rodada Atual: " + partida.getRodada());
        
        System.out.println("\n--- Jogadores Ativos ---");
        List<Jogador> ativos = partida.getJogadoresAtivos();
        if (ativos.isEmpty()) {
            System.out.println("Nenhum jogador ativo.");
        } else {
            for (Jogador j : ativos) {
                String pos = (j.getPosicaoAtual() != null) ? j.getPosicaoAtual().getCasa().getNome() : "Início";
                System.out.println(" * " + j.getNome() + " (" + j.getTipoPersonagem() + ") | Saldo: R$ " + j.getSaldo() + " | Posição: " + pos);
                System.out.print("   Propriedades: ");
                if (j.getPropriedades().isEmpty()) {
                    System.out.println("Nenhuma");
                } else {
                    for (int k = 0; k < j.getPropriedades().size(); k++) {
                        CasaImovel p = j.getPropriedades().get(k);
                        System.out.print(p.getNome() + " (Casas: " + p.getQuantidadeCasas() + ", Hotel: " + (p.isTemHotel() ? "Sim" : "Não") + ")");
                        if (k < j.getPropriedades().size() - 1) System.out.print(", ");
                    }
                    System.out.println();
                }
            }
        }

        System.out.println("\n--- Imóveis Livres (Para Compra) ---");
        boolean temLivres = false;
        if (partida.getTabuleiro() != null && partida.getTabuleiro().getCabeca() != null) {
            NoTabuleiro atual = partida.getTabuleiro().getCabeca();
            do {
                if (atual.getCasa() instanceof CasaImovel) {
                    CasaImovel imovel = (CasaImovel) atual.getCasa();
                    if (imovel.getProprietario() == null) {
                        System.out.println(" * " + imovel.getNome() + " | Grupo: " + imovel.getCorGrupo() + " | Preço: R$ " + imovel.getPrecoCompra());
                        temLivres = true;
                    }
                }
                atual = atual.getProximo();
            } while (atual != partida.getTabuleiro().getCabeca());
        }
        if (!temLivres) {
            System.out.println("Nenhum imóvel vago no tabuleiro.");
        }
        System.out.println("=========================================");
    }


    // ==========================================
    // EXIBIR RANKING
    // ==========================================
    private static void mostrarRanking() {
        if (partida == null) {
            System.out.println("\nNenhuma partida foi iniciada ainda.");
            return;
        }
        partida.exibirRelatorioFinal();
    }

    private static void exibirTabuleiro() {
        System.out.println("\n=========================================");
        System.out.println("          VISUALIZAÇÃO DO TABULEIRO       ");
        System.out.println("=========================================");
        
        estrutura.ListaDuplamenteLigadaCircular tab;
        
        if (partida != null) {
            tab = partida.getTabuleiro();
        } else {
            // Constrói um tabuleiro temporário com base nas configurações e imóveis cadastrados no momento
            tab = new estrutura.ListaDuplamenteLigadaCircular();
            double salario = preJogoService.getSalarioBase();
            tab.adicionar(new modelo.casa.CasaInicio(0, "Ponto de Partida", "Início do tabuleiro. Receba R$ " + salario + " ao passar.", salario));

            List<CasaImovel> imoveisList = imovelRepo.listar();
            int idEspecial = 1000;

            for (int i = 0; i < imoveisList.size(); i++) {
                tab.adicionar(imoveisList.get(i));

                // Intercala Casa Sorte/Revés a cada 3 imóveis
                if ((i + 1) % 3 == 0) {
                    tab.adicionar(new modelo.casa.CasaSorteReves(idEspecial++, "Sorte ou Revés", "Saca uma carta da pilha de cartas.", true));
                }
                // Intercala Casa de Imposto a cada 5 imóveis
                if ((i + 1) % 5 == 0) {
                    tab.adicionar(new modelo.casa.CasaImposto(idEspecial++, "Imposto de Renda Extra", "Tributo especial cobrado pelo banco.", 150.0));
                }
                // Intercala Casa de Restituição a cada 7 imóveis
                if ((i + 1) % 7 == 0) {
                    tab.adicionar(new modelo.casa.CasaRestituicao(idEspecial++, "Restituição Fiscal", "Reembolso e devolução de créditos de tributos.", 100.0));
                }
            }
        }

        if (tab == null || tab.estaVazia()) {
            System.out.println("Nenhum imóvel ou casa cadastrada para exibir o tabuleiro.");
            return;
        }
        
        estrutura.NoTabuleiro cabeca = tab.getCabeca();
        estrutura.NoTabuleiro atual = cabeca;
        int index = 0;
        do {
            System.out.println(" [" + index + "] " + atual.getCasa().getNome() + " (" + atual.getCasa().getClass().getSimpleName() + ")");
            atual = atual.getProximo();
            index++;
        } while (atual != cabeca);
        
        System.out.println("-----------------------------------------");
        System.out.println(" Conexão Circular:");
        System.out.println(" -> Última casa (" + tab.getCauda().getCasa().getNome() + ") aponta para a Primeira (" + cabeca.getCasa().getNome() + ") [Próximo]");
        System.out.println(" -> Primeira casa (" + cabeca.getCasa().getNome() + ") aponta para a Última (" + tab.getCauda().getCasa().getNome() + ") [Anterior]");
        System.out.println("=========================================");
    }


    // ==========================================
    // HELPER METHODS E ENTRADA DE DADOS
    // ==========================================
    private static int lerInteiro(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String linha = scanner.nextLine().trim();
                return Integer.parseInt(linha);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, digite um número inteiro.");
            }
        }
    }

    private static int lerInteiroRange(String prompt, int min, int max) {
        while (true) {
            int valor = lerInteiro(prompt);
            if (valor >= min && valor <= max) {
                return valor;
            }
            System.out.println("Valor fora do intervalo permitido (" + min + " a " + max + "). Tente novamente.");
        }
    }

    private static double lerDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String linha = scanner.nextLine().trim();
                return Double.parseDouble(linha);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, digite um número decimal válido.");
            }
        }
    }

    /**
     * Inicializa imóveis e jogadores padrão para possibilitar o início imediato de uma partida de teste.
     */
    private static void inicializarDadosPadrao() {
        // Cadastra 4 jogadores iniciais
        jogadorRepo.adicionar(new Jogador("1", "Matheus", 1200.0, TipoPersonagem.ESPECULADOR));
        jogadorRepo.adicionar(new Jogador("2", "Ana", 1200.0, TipoPersonagem.NEGOCIANTE));
        jogadorRepo.adicionar(new Jogador("3", "Bruno", 1200.0, TipoPersonagem.ADVOGADO));
        jogadorRepo.adicionar(new Jogador("4", "Carla", 1200.0, TipoPersonagem.CONSTRUTOR));

        // Cadastra 10 imóveis padrão
        imovelRepo.adicionar(new CasaImovel(1, "Avenida Paulista", "Centro financeiro.", 350.0, 35.0, 50.0, 120.0, "Azul"));
        imovelRepo.adicionar(new CasaImovel(2, "Copacabana", "Praia turística.", 300.0, 25.0, 40.0, 100.0, "Verde"));
        imovelRepo.adicionar(new CasaImovel(3, "Rua Oscar Freire", "Lojas de luxo.", 280.0, 22.0, 35.0, 90.0, "Azul"));
        imovelRepo.adicionar(new CasaImovel(4, "Ipanema", "Bairro nobre no Rio.", 320.0, 28.0, 45.0, 110.0, "Verde"));
        imovelRepo.adicionar(new CasaImovel(5, "Avenida Brigadeiro", "Bairro corporativo.", 240.0, 18.0, 30.0, 80.0, "Amarelo"));
        imovelRepo.adicionar(new CasaImovel(6, "Berrini", "Pólo tecnológico.", 260.0, 20.0, 32.0, 85.0, "Amarelo"));
        imovelRepo.adicionar(new CasaImovel(7, "Pelourinho", "Centro histórico.", 180.0, 12.0, 20.0, 60.0, "Laranja"));
        imovelRepo.adicionar(new CasaImovel(8, "Mercado Modelo", "Comércio tradicional.", 190.0, 14.0, 22.0, 65.0, "Laranja"));
        imovelRepo.adicionar(new CasaImovel(9, "Lagoa da Pampulha", "Ponto turístico em BH.", 220.0, 16.0, 28.0, 75.0, "Vermelho"));
        imovelRepo.adicionar(new CasaImovel(10, "Savassi", "Bairro badalado em BH.", 230.0, 17.0, 29.0, 78.0, "Vermelho"));
    }
}