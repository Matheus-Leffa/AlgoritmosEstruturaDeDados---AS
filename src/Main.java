import modelo.Jogador;
import modelo.TipoPersonagem;
import modelo.casa.CasaImovel;
import repositorio.ImovelRepositorio;
import repositorio.JogadorRepositorio;
import servico.Jogo;
import servico.PreJogoService;
import java.util.List;

/**
 * Classe principal para execução e simulação automática de uma partida completa
 * demonstrando o funcionamento dos módulos de gerenciamento da fase pré-jogo (CRUD e validações).
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=========================================================");
        System.out.println("          SISTEMA DE GERENCIAMENTO PRÉ-JOGO             ");
        System.out.println("=========================================================\n");

        // 1. Inicializando repositórios e serviços do pré-jogo
        JogadorRepositorio jogadorRepo = new JogadorRepositorio();
        ImovelRepositorio imovelRepo = new ImovelRepositorio();
        PreJogoService preJogoService = new PreJogoService(jogadorRepo, imovelRepo);

        // ==========================================
        // 2. DEMONSTRAÇÃO DO CRUD DE JOGADORES
        // ==========================================
        System.out.println("--- [CRUD JOGADORES] ---");

        // C - Cadastro
        Jogador j1 = new Jogador("1", "Matheus", 1200.0, TipoPersonagem.ESPECULADOR);
        Jogador j2 = new Jogador("2", "Ana", 1200.0, TipoPersonagem.NEGOCIANTE);
        Jogador j3 = new Jogador("3", "Bruno", 1200.0, TipoPersonagem.ADVOGADO);
        Jogador j4 = new Jogador("4", "Carla", 1200.0, TipoPersonagem.CONSTRUTOR);

        jogadorRepo.adicionar(j1);
        jogadorRepo.adicionar(j2);
        jogadorRepo.adicionar(j3);
        jogadorRepo.adicionar(j4);
        System.out.println("Cadastrados 4 jogadores com sucesso.");

        // R - Listagem
        System.out.println("\nJogadores Cadastrados:");
        for (Jogador j : jogadorRepo.listar()) {
            System.out.println(" -> ID: " + j.getId() + " | Nome: " + j.getNome() + " | Personagem: " + j.getTipoPersonagem());
        }

        // U - Atualização
        Jogador jogadorParaAtualizar = new Jogador("3", "Bruno Henrique", 1300.0, TipoPersonagem.ADVOGADO);
        boolean atualizouJ = jogadorRepo.atualizar(jogadorParaAtualizar);
        System.out.println("\nAtualizou Bruno para Bruno Henrique? " + (atualizouJ ? "Sim" : "Não"));

        // D - Remoção (Temporária para demonstração)
        Jogador jTemp = new Jogador("99", "Jogador Temporario", 1000.0, TipoPersonagem.NEGOCIANTE);
        jogadorRepo.adicionar(jTemp);
        System.out.println("Adicionado jogador temporário. Total agora: " + jogadorRepo.getQuantidade());
        jogadorRepo.remover("99");
        System.out.println("Removido jogador temporário. Total agora: " + jogadorRepo.getQuantidade());

        // Validação: Máximo de 6 jogadores
        try {
            System.out.println("\nTentando forçar cadastro de mais de 6 jogadores...");
            jogadorRepo.adicionar(new Jogador("5", "Jogador 5", 1000.0, TipoPersonagem.ESPECULADOR));
            jogadorRepo.adicionar(new Jogador("6", "Jogador 6", 1000.0, TipoPersonagem.NEGOCIANTE));
            // Tentativa de adicionar o 7º jogador
            jogadorRepo.adicionar(new Jogador("7", "Jogador 7", 1000.0, TipoPersonagem.CONSTRUTOR));
        } catch (IllegalArgumentException e) {
            System.out.println("Sucesso! Capturou erro esperado: " + e.getMessage());
        }

        // Deixa a lista limpa com 4 jogadores ativos (removendo os excedentes 5 e 6 para a simulação)
        jogadorRepo.remover("5");
        jogadorRepo.remover("6");


        // ==========================================
        // 3. DEMONSTRAÇÃO DO CRUD DE IMÓVEIS
        // ==========================================
        System.out.println("\n--- [CRUD IMÓVEIS] ---");

        // C - Cadastro
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
        System.out.println("Cadastrados 10 imóveis com sucesso.");

        // R - Listagem
        System.out.println("\nImóveis Cadastrados:");
        for (CasaImovel i : imovelRepo.listar()) {
            System.out.println(" -> ID: " + i.getId() + " | Nome: " + i.getNome() + " | Grupo: " + i.getCorGrupo() + " | Preço: R$ " + i.getPrecoCompra());
        }

        // U - Atualização
        CasaImovel imovelAtualizado = new CasaImovel(1, "Avenida Paulista", "Centro financeiro reformulado.", 380.0, 40.0, 55.0, 130.0, "Azul Escuro");
        boolean atualizouI = imovelRepo.atualizar(imovelAtualizado);
        System.out.println("\nAtualizou Avenida Paulista? " + (atualizouI ? "Sim" : "Não"));

        // D - Remoção (Temporária para demonstração)
        CasaImovel imovelTemp = new CasaImovel(999, "Terreno Baldio", "Sem valor comercial.", 50.0, 2.0, 5.0, 10.0, "Cinza");
        imovelRepo.adicionar(imovelTemp);
        System.out.println("Adicionado imóvel temporário. Total agora: " + imovelRepo.getQuantidade());
        imovelRepo.remover(999);
        System.out.println("Removido imóvel temporário. Total agora: " + imovelRepo.getQuantidade());

        // Validação: Máximo de 40 imóveis
        try {
            System.out.println("\nTentando forçar cadastro de mais de 40 imóveis...");
            for (int k = 11; k <= 50; k++) {
                imovelRepo.adicionar(new CasaImovel(k, "Imóvel Extra " + k, "Propriedade extra.", 100.0, 10.0, 15.0, 50.0, "Branco"));
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Sucesso! Capturou erro esperado: " + e.getMessage());
        }

        // Limpa a lista de volta para os 10 imóveis originais de teste
        List<CasaImovel> todosImoveis = imovelRepo.listar();
        for (CasaImovel i : todosImoveis) {
            if (i.getId() > 10) {
                imovelRepo.remover(i.getId());
            }
        }


        // ==========================================
        // 4. DEMONSTRAÇÃO DAS VALIDAÇÕES PRÉ-JOGO
        // ==========================================
        System.out.println("\n--- [VALIDAÇÕES PRÉ-JOGO DE INICIALIZAÇÃO] ---");

        // Caso 1: Testar com menos de 2 jogadores
        jogadorRepo.remover("1");
        jogadorRepo.remover("2");
        jogadorRepo.remover("3"); // Deixa apenas Carla (1 jogador)
        try {
            System.out.println("Tentando iniciar jogo com apenas 1 jogador cadastrado...");
            preJogoService.validarConfiguracao();
        } catch (IllegalStateException e) {
            System.out.println("Sucesso! Capturou erro esperado: " + e.getMessage());
        }

        // Adiciona os jogadores de volta
        jogadorRepo.adicionar(j1);
        jogadorRepo.adicionar(j2);
        jogadorRepo.adicionar(j3);

        // Caso 2: Testar com menos de 10 imóveis
        imovelRepo.remover(10); // Remove savassi (sobrando 9 imóveis)
        try {
            System.out.println("\nTentando iniciar jogo com apenas 9 imóveis cadastrados...");
            preJogoService.validarConfiguracao();
        } catch (IllegalStateException e) {
            System.out.println("Sucesso! Capturou erro esperado: " + e.getMessage());
        }

        // Adiciona savassi de volta para bater o mínimo de 10
        imovelRepo.adicionar(new CasaImovel(10, "Savassi", "Bairro badalado em BH.", 230.0, 17.0, 29.0, 78.0, "Vermelho"));


        // ==========================================
        // 5. INICIALIZAÇÃO E SIMULAÇÃO DA PARTIDA
        // ==========================================
        System.out.println("\n=========================================================");
        System.out.println("   VALIDAÇÕES APROVADAS! CONFIGURANDO E INICIANDO JOGO    ");
        System.out.println("=========================================================\n");

        // Cria a partida montando o tabuleiro circular com os imóveis cadastrados e intercalando especiais
        Jogo partida = preJogoService.criarPartida();

        System.out.println("Tabuleiro gerado de forma dinâmica com os imóveis cadastrados.");
        System.out.println("Iniciando a partida simulada...");

        partida.iniciarPartida();
    }
}