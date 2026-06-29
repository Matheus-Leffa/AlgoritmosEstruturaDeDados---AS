package servico;

import estrutura.ListaDuplamenteLigadaCircular;
import estrutura.NoTabuleiro;
import modelo.Jogador;
import modelo.casa.*;
import modelo.carta.Baralho;
import modelo.carta.Carta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Motor central de execução e controle da partida do jogo Monopoly estratégico.
 * Gerencia rodadas, turnos, rolagem de dados, movimentações, efeitos de casas
 * e transações financeiras (compras, aluguéis, impostos e falências).
 */
public class Jogo {
    private ListaDuplamenteLigadaCircular tabuleiro;
    private Baralho baralho;
    private List<Jogador> jogadoresAtivos;
    private List<Jogador> jogadoresFalidos;
    private int rodada;
    private Random random;
    
    // Configurações dinâmicas da partida
    private double salarioBase = 200.0;
    private int maxRodadas = 100;
    private boolean interativo = false;

    private static final java.util.Scanner scanner = new java.util.Scanner(System.in);

    public Jogo(ListaDuplamenteLigadaCircular tabuleiro, List<Jogador> jogadoresIniciais) {
        this.tabuleiro = tabuleiro;
        this.baralho = new Baralho();
        this.jogadoresAtivos = new ArrayList<>(jogadoresIniciais);
        this.jogadoresFalidos = new ArrayList<>();
        this.rodada = 1;
        this.random = new Random();

        // Aloca todos os jogadores no ponto inicial do tabuleiro (Cabeça da lista circular)
        for (Jogador j : jogadoresAtivos) {
            j.setPosicaoAtual(tabuleiro.getCabeca());
        }
    }

    // Getters e Setters para configurações dinâmicas
    public double getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(double salarioBase) {
        this.salarioBase = salarioBase;
    }

    public int getMaxRodadas() {
        return maxRodadas;
    }

    public void setMaxRodadas(int maxRodadas) {
        this.maxRodadas = maxRodadas;
    }

    public boolean isInterativo() {
        return interativo;
    }

    public void setInterativo(boolean interativo) {
        this.interativo = interativo;
    }

    /**
     * Inicia o loop principal de execução da partida, rodando rodadas sucessivas
     * até que reste apenas 1 jogador ativo no jogo.
     */
    public void iniciarPartida() {
        System.out.println("\n=========================================================");
        System.out.println("   PARTIDA INICIADA! QUE VENÇA O MELHOR ESTRATEGISTA!    ");
        System.out.println("=========================================================\n");

        while (!isPartidaEncerrada()) {
            jogarRodada();
        }

        exibirRelatorioFinal();
    }

    /**
     * Verifica se a partida foi encerrada.
     */
    public boolean isPartidaEncerrada() {
        return jogadoresAtivos.size() <= 1 || rodada > maxRodadas;
    }

    /**
     * Executa uma rodada completa da partida.
     * Cada jogador ativo realiza o seu respectivo turno na ordem da lista.
     */
    private void jogarRodada() {
        System.out.println("\n---------------------------------------------------------");
        System.out.println(" >>> INICIANDO RODADA #" + rodada + " <<<");
        System.out.println("---------------------------------------------------------");

        // Cria uma cópia da lista para iteração segura caso ocorram falências no turno
        List<Jogador> copiaJogadores = new ArrayList<>(jogadoresAtivos);

        for (Jogador jogador : copiaJogadores) {
            if (jogador.isFalido()) {
                continue;
            }
            executarTurno(jogador);

            if (isPartidaEncerrada()) {
                break;
            }
        }
        rodada++;
    }

    /**
     * Executa as ações de turno de um jogador específico.
     */
    private void executarTurno(Jogador jogador) {
        System.out.println("\n[TURNO] Vez de: " + jogador.getNome() + " (" + jogador.getTipoPersonagem() + ")");
        System.out.println("   Saldo Atual: R$ " + jogador.getSaldo() + " | Pos: " + jogador.getPosicaoAtual().getCasa().getNome());

        if (interativo) {
            System.out.println("Pressione ENTER para lançar os dados...");
            scanner.nextLine();
        }

        // Tratamento da prisão
        if (jogador.isPreso()) {
            processarTentativaFugaPrisao(jogador);
            verificarFalenciasGerais();
            // Se continuar preso ou faliu após a tentativa, encerra o turno dele aqui
            if (jogador.isPreso() || jogador.isFalido()) {
                if (interativo) {
                    System.out.println("Pressione ENTER para continuar para o próximo jogador.");
                    scanner.nextLine();
                }
                return;
            }
        }

        // 1. Rolagem de dois dados
        int dado1 = rolarDado();
        int dado2 = rolarDado();
        int totalCasas = dado1 + dado2;
        System.out.println("   [DADOS] Rolo de dados: [" + dado1 + ", " + dado2 + "] -> Total: " + totalCasas + " casas.");

        // 2. Movimentação no tabuleiro (com passagem pelo início e salário)
        moverJogador(jogador, totalCasas);

        // 3. Execução dos efeitos da casa onde o jogador pousou
        aplicarEfeitoCasa(jogador, jogador.getPosicaoAtual().getCasa());

        // 4. Verificação pós-turno de falência geral (afeta o jogador da vez e outros)
        verificarFalenciasGerais();

        if (interativo) {
            System.out.println("Pressione ENTER para continuar para o próximo jogador.");
            scanner.nextLine();
        }
    }

    /**
     * Rola um dado padrão de 6 lados.
     */
    private int rolarDado() {
        return random.nextInt(6) + 1;
    }

    /**
     * Move o jogador pelo tabuleiro e aciona a callback de passagem pelo início.
     */
    private void moverJogador(Jogador jogador, int passos) {
        NoTabuleiro antigaPosicao = jogador.getPosicaoAtual();
        NoTabuleiro novaPosicao = tabuleiro.moverEVerificarPassagemInicio(antigaPosicao, passos, () -> {
            // Callback: jogador cruza o início do tabuleiro
            System.out.println("   [TABULEIRO] " + jogador.getNome() + " cruzou a linha de partida!");
            jogador.incrementarVoltas();
            jogador.receberSalario(salarioBase);
        });
        jogador.setPosicaoAtual(novaPosicao);
        System.out.println("   [TABULEIRO] " + jogador.getNome() + " moveu-se de " + antigaPosicao.getCasa().getNome() + " para " + novaPosicao.getCasa().getNome());
    }

    /**
     * Distribui a execução do efeito com base no tipo concreto da Casa.
     */
    private void aplicarEfeitoCasa(Jogador jogador, Casa casa) {
        if (casa instanceof CasaInicio) {
            System.out.println("   [CASA] Parou no Ponto de Partida para descansar.");
        } else if (casa instanceof CasaImovel) {
            processarCasaImovel(jogador, (CasaImovel) casa);
        } else if (casa instanceof CasaImposto) {
            processarCasaImposto(jogador, (CasaImposto) casa);
        } else if (casa instanceof CasaRestituicao) {
            processarCasaRestituicao(jogador, (CasaRestituicao) casa);
        } else if (casa instanceof CasaSorteReves) {
            processarCasaSorteReves(jogador, (CasaSorteReves) casa);
        }
    }

    /**
     * Trata a tentativa de fuga da prisão.
     */
    private void processarTentativaFugaPrisao(Jogador jogador) {
        System.out.println("   [PRISÃO] " + jogador.getNome() + " está preso. Tentando rolar dados duplos para fuga livre...");
        int d1 = rolarDado();
        int d2 = rolarDado();
        System.out.println("   [PRISÃO] Dados: [" + d1 + ", " + d2 + "]");
        
        if (d1 == d2) {
            System.out.println("   [PRISÃO] Fuga de sucesso! Dados idênticos. Jogador livre!");
            jogador.setPreso(false);
        } else {
            System.out.println("   [PRISÃO] Falhou em rolar duplos. Pagando fiança compulsória de R$ 50,00.");
            jogador.pagar(50.0);
            if (!jogador.isFalido()) {
                jogador.setPreso(false);
            }
        }
    }

    /**
     * Trata o pouso sobre um espaço de Imóvel (Compra, Construção ou Cobrança de Aluguel).
     */
    private void processarCasaImovel(Jogador jogador, CasaImovel imovel) {
        Jogador proprietario = imovel.getProprietario();

        if (proprietario == null) {
            System.out.println("   [CASA] " + imovel.getNome() + " está à venda por R$ " + imovel.getPrecoCompra());
            boolean comprar = true;
            if (interativo) {
                System.out.println("   Deseja comprar o imóvel?");
                System.out.println("   1 - Sim");
                System.out.println("   2 - Não");
                comprar = lerOpcaoInterativa(1, 2) == 1;
            }
            if (comprar) {
                if (jogador.getSaldo() >= imovel.getPrecoCompra()) {
                    jogador.comprarPropriedade(imovel);
                } else {
                    System.out.println("   [CASA] Saldo insuficiente para efetuar a compra.");
                }
            } else {
                System.out.println("   [CASA] Você escolheu não comprar o imóvel.");
            }
        } else if (proprietario == jogador) {
            System.out.println("   [CASA] Você parou na sua própria propriedade: " + imovel.getNome());
            if (interativo) {
                double custo = imovel.getCustoConstrucaoCasa();
                if (!imovel.isTemHotel()) {
                    if (imovel.getQuantidadeCasas() < 4) {
                        System.out.println("   Deseja construir a " + (imovel.getQuantidadeCasas() + 1) + "ª casa por R$ " + custo + "?");
                    } else {
                        System.out.println("   Deseja construir um HOTEL por R$ " + custo + "?");
                    }
                    System.out.println("   1 - Sim");
                    System.out.println("   2 - Não");
                    if (lerOpcaoInterativa(1, 2) == 1) {
                        tentarConstruirNoImovel(jogador, imovel);
                    } else {
                        System.out.println("   [MELHORIA] Você escolheu não construir melhorias.");
                    }
                }
            } else {
                tentarConstruirNoImovel(jogador, imovel);
            }
        } else {
            double valorAluguel = imovel.calcularAluguelAtual();
            System.out.println("   [CASA] Propriedade de " + proprietario.getNome() + ". Taxa de aluguel: R$ " + valorAluguel);
            double valorPago = jogador.pagarAluguel(proprietario, valorAluguel);
            imovel.adicionarAluguelGerado(valorPago);
        }
    }

    private int lerOpcaoInterativa(int min, int max) {
        while (true) {
            try {
                System.out.print("   Escolha uma opção: ");
                String linha = scanner.nextLine().trim();
                int opcao = Integer.parseInt(linha);
                if (opcao >= min && opcao <= max) {
                    return opcao;
                }
                System.out.println("   Opção inválida. Digite um valor entre " + min + " e " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("   Entrada inválida. Digite um número inteiro.");
            }
        }
    }

    /**
     * Tenta construir casas ou hotel em uma propriedade já possuída.
     */
    private void tentarConstruirNoImovel(Jogador jogador, CasaImovel imovel) {
        double custo = imovel.getCustoConstrucaoCasa();
        if (!imovel.isTemHotel()) {
            if (imovel.getQuantidadeCasas() < 4) {
                if (jogador.getSaldo() >= custo) {
                    jogador.pagar(custo);
                    imovel.setQuantidadeCasas(imovel.getQuantidadeCasas() + 1);
                    System.out.println("   [MELHORIA] Construiu a " + imovel.getQuantidadeCasas() + "ª casa em " + imovel.getNome() + " por R$ " + custo);
                } else {
                    System.out.println("   [MELHORIA] Saldo insuficiente para construir.");
                }
            } else {
                if (jogador.getSaldo() >= custo) {
                    jogador.pagar(custo);
                    imovel.setQuantidadeCasas(0);
                    imovel.setTemHotel(true);
                    System.out.println("   [MELHORIA] Construiu um HOTEL em " + imovel.getNome() + " por R$ " + custo);
                } else {
                    System.out.println("   [MELHORIA] Saldo insuficiente para construir.");
                }
            }
        }
    }

    private void processarCasaImposto(Jogador jogador, CasaImposto imposto) {
        double taxa = imposto.getValorImposto();
        System.out.println("   [CASA] Cobrança de imposto no valor de: R$ " + taxa);
        jogador.pagarImposto(taxa);
    }

    private void processarCasaRestituicao(Jogador jogador, CasaRestituicao restituicao) {
        double valor = restituicao.getValorRestituicao();
        System.out.println("   [CASA] Restituição fiscal recebida: R$ " + valor);
        jogador.adicionarSaldo(valor);
    }

    private void processarCasaSorteReves(Jogador jogador, CasaSorteReves casaSorteReves) {
        Carta carta = baralho.sacar();
        carta.executar(jogador, jogadoresAtivos, tabuleiro);
        verificarFalenciasGerais();
    }

    private void verificarFalenciasGerais() {
        List<Jogador> copia = new ArrayList<>(jogadoresAtivos);
        for (Jogador j : copia) {
            if (j.getSaldo() < 0 || j.isFalido()) {
                if (j.getSaldo() < 0) {
                    j.setSaldo(0);
                    j.setFalido(true);
                }
                declararFalenciaJogador(j);
            }
        }
    }

    private void declararFalenciaJogador(Jogador jogador) {
        System.out.println("\n[SISTEMA] Falência decretada para: " + jogador.getNome() + "!");
        jogador.desapropriarTudo();
        jogadoresAtivos.remove(jogador);
        jogadoresFalidos.add(jogador);
    }

    /**
     * Auxiliar para calcular o patrimônio total do jogador (saldo + valor de compra das propriedades possuídas).
     */
    public double calcularPatrimonio(Jogador jogador) {
        double patrimonio = jogador.getSaldo();
        for (CasaImovel p : jogador.getPropriedades()) {
            patrimonio += p.getPrecoCompra();
        }
        return patrimonio;
    }

    /**
     * Exibe estatísticas consolidadas e classificados da partida após a conclusão.
     */
    public void exibirRelatorioFinal() {
        System.out.println("\n=========================================================");
        System.out.println("               RELATÓRIO CONSOLIDADO DO JOGO             ");
        System.out.println("=========================================================\n");

        // 1. Ranking dos jogadores por patrimônio total (Ativos + Falidos)
        List<Jogador> todosJogadores = new ArrayList<>();
        todosJogadores.addAll(jogadoresAtivos);
        todosJogadores.addAll(jogadoresFalidos);
        todosJogadores.sort((a, b) -> Double.compare(calcularPatrimonio(b), calcularPatrimonio(a)));

        System.out.println("🏆 --- RANKING DE JOGADORES POR PATRIMÔNIO ---");
        for (int i = 0; i < todosJogadores.size(); i++) {
            Jogador j = todosJogadores.get(i);
            double pat = calcularPatrimonio(j);
            System.out.printf(" %dº Lugar: %s (%s) | Patrimônio Total: R$ %.2f (Saldo: R$ %.2f | Imóveis: R$ %.2f)\n",
                    (i + 1), j.getNome(), j.getTipoPersonagem(), pat, j.getSaldo(), (pat - j.getSaldo()));
        }

        // 2. Número de voltas completas de cada jogador
        System.out.println("\n🔄 --- VOLTAS COMPLETAS PELO TABULEIRO ---");
        for (Jogador j : todosJogadores) {
            System.out.println(" - " + j.getNome() + ": " + j.getVoltasCompletas() + " volta(s) completa(s).");
        }

        // 3. Imóvel que gerou o maior valor de aluguel durante a partida
        System.out.println("\n🏠 --- IMÓVEL COM MAIOR ALUGUEL GERADO ---");
        CasaImovel imovelTopAluguel = null;
        double maxAluguel = 0.0;
        if (tabuleiro.getCabeca() != null) {
            NoTabuleiro atual = tabuleiro.getCabeca();
            do {
                if (atual.getCasa() instanceof CasaImovel) {
                    CasaImovel imovel = (CasaImovel) atual.getCasa();
                    if (imovel.getTotalAluguelGerado() > maxAluguel) {
                        maxAluguel = imovel.getTotalAluguelGerado();
                        imovelTopAluguel = imovel;
                    }
                }
                atual = atual.getProximo();
            } while (atual != tabuleiro.getCabeca());
        }
        if (imovelTopAluguel != null && maxAluguel > 0) {
            String donoInfo = imovelTopAluguel.getProprietario() != null 
                    ? "Dono atual: " + imovelTopAluguel.getProprietario().getNome() 
                    : "Sem dono";
            System.out.printf(" - %s (%s) | Aluguel Total Acumulado: R$ %.2f | %s\n",
                    imovelTopAluguel.getNome(), imovelTopAluguel.getCorGrupo(), maxAluguel, donoInfo);
        } else {
            System.out.println(" - Nenhum aluguel foi gerado durante a partida.");
        }

        // 4. Jogadores falidos
        System.out.println("\n💀 --- JOGADORES FALIDOS ---");
        if (jogadoresFalidos.isEmpty()) {
            System.out.println(" - Nenhum jogador faliu nesta partida.");
        } else {
            for (int i = 0; i < jogadoresFalidos.size(); i++) {
                Jogador f = jogadoresFalidos.get(i);
                System.out.println(" - " + f.getNome() + " (" + f.getTipoPersonagem() + ") - Eliminado após " + f.getVoltasCompletas() + " volta(s).");
            }
        }

        // 5. Jogador vencedor
        System.out.println("\n⭐ --- JOGADOR VENCEDOR ---");
        Jogador vencedor = null;
        if (jogadoresAtivos.size() == 1) {
            vencedor = jogadoresAtivos.get(0);
            System.out.println(" 🏆 VENCEDOR POR ELIMINAÇÃO: " + vencedor.getNome().toUpperCase() + " (" + vencedor.getTipoPersonagem() + ")!");
        } else if (jogadoresAtivos.size() > 1) {
            double maiorPatrimonio = -1.0;
            for (Jogador j : jogadoresAtivos) {
                double pat = calcularPatrimonio(j);
                if (pat > maiorPatrimonio) {
                    maiorPatrimonio = pat;
                    vencedor = j;
                }
            }
            if (vencedor != null) {
                System.out.printf(" 🏆 VENCEDOR POR PATRIMÔNIO (Limite de %d rodadas): %s (%s) com Patrimônio de R$ %.2f!\n",
                        (rodada - 1), vencedor.getNome().toUpperCase(), vencedor.getTipoPersonagem(), calcularPatrimonio(vencedor));
            }
        } else {
            System.out.println(" Não houve vencedor na partida.");
        }
        System.out.println("\n=========================================================");
    }

    // Getters para monitoramento
    public List<Jogador> getJogadoresAtivos() {
        return jogadoresAtivos;
    }

    public List<Jogador> getJogadoresFalidos() {
        return jogadoresFalidos;
    }

    public int getRodada() {
        return rodada;
    }

    public ListaDuplamenteLigadaCircular getTabuleiro() {
        return tabuleiro;
    }

}
