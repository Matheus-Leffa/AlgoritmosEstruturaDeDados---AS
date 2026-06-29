# Jogo Monopoly Estratégico - Sistema de Gerenciamento e Execução

Este projeto consiste em uma implementação em Java de um jogo de tabuleiro de estilo Monopoly Estratégico. O sistema foi desenvolvido focando em boas práticas de engenharia de software, separação clara de responsabilidades, programação orientada a objetos (POO) e implementação manual de estruturas de dados lineares.

---

## 1. Visão Geral

### Objetivo do Projeto
Demonstrar a aplicação prática de conceitos avançados de **Estruturas de Dados** (Listas Duplamente Ligadas Circulares e Pilhas Encadeadas implementadas manualmente) e **Programação Orientada a Objetos** (Abstração, Herança, Polimorfismo, Associação e Encapsulamento) no desenvolvimento de um motor de jogo de simulação estratégica de tabuleiro.

### Descrição do Jogo
O jogo consiste em um tabuleiro onde os jogadores rolam dados, movem-se pelas casas e realizam transações imobiliárias e financeiras. Cada jogador possui um personagem com habilidades passivas únicas que alteram o andamento do jogo:
* **Especulador:** Recebe 20% a mais de salário ao completar voltas, mas paga 10% a mais de impostos.
* **Negociante:** Paga 10% a menos ao pagar aluguel a outros jogadores.
* **Advogado:** É imune à mecânica de prisão (utiliza sua influência para anular prisões).
* **Construtor:** Aumenta em 15% o aluguel base de todas as propriedades que compra.

O tabuleiro é gerado dinamicamente no pré-jogo com base nos imóveis cadastrados, intercalando casas especiais como o Ponto de Partida, Sorte ou Revés (onde cartas são sacadas de uma pilha), Imposto de Renda Extra e Restituição Fiscal.

### Principais Funcionalidades Implementadas
1. **Fase de Pré-Jogo (Módulo de Configuração):**
   * CRUD completo de Imóveis (Cadastro, Listagem, Atualização e Remoção) com validação de limite máximo (40 imóveis).
   * CRUD completo de Jogadores (Cadastro, Listagem, Atualização e Remoção) com limite de participantes (máximo de 6).
   * Validações de consistência para início da partida (mínimo de 2 jogadores e mínimo de 10 imóveis cadastrados).
2. **Motor de Execução do Jogo (Fase Ativa):**
   * Controle de turnos e rodadas sucessivas.
   * Simulação de rolagens de dados (dois dados de 6 lados).
   * Movimentação no tabuleiro circular com detecção de passagem pelo início e pagamento de salário.
   * Compra de propriedades e construção de melhorias (casas e hotéis).
   * Cobrança automática de aluguéis e taxas com aplicação de habilidades especiais de personagens.
   * Sistema de cartas de Sorte ou Revés sacadas de uma pilha dinâmica.
   * Gerenciamento de falência e desapropriação imediata de propriedades de jogadores falidos.
3. **Encerramento da Partida e Relatório Final:**
   * Apresentação do ranking completo de jogadores por patrimônio líquido ($\text{saldo} + \text{valor de compra dos imóveis}$).
   * Exibição das voltas completadas por cada jogador.
   * Identificação da propriedade que gerou mais receita de aluguel durante o jogo.
   * Listagem de jogadores falidos e declaração do grande vencedor.

---

## 2. Arquitetura do Projeto

O projeto adota uma arquitetura multicamadas bem definida que isola o domínio (modelos), a persistência temporária (repositórios), as regras de controle e inicialização (serviços) e as estruturas de suporte (estruturas de dados customizadas).

### Organização de Pastas e Pacotes
```
src/
├── Main.java                             # Ponto de entrada (Simulação e Testes do CRUD)
├── estrutura/                            # Camada de Estruturas de Dados Customizadas
│   ├── ListaDuplamenteLigadaCircular.java
│   ├── NoTabuleiro.java
│   ├── NoPilha.java
│   └── Pilha.java
├── modelo/                               # Camada de Modelos e Entidades do Domínio
│   ├── Jogador.java
│   ├── TipoPersonagem.java
│   ├── casa/                             # Subpacote de Casas do Tabuleiro
│   │   ├── Casa.java
│   │   ├── CasaImovel.java
│   │   ├── CasaImposto.java
│   │   ├── CasaInicio.java
│   │   ├── CasaRestituicao.java
│   │   └── CasaSorteReves.java
│   └── carta/                            # Subpacote de Cartas do Baralho
│       ├── Carta.java
│       ├── Baralho.java
│       ├── TipoCarta.java
│       └── EfeitoCarta.java
├── repositorio/                          # Camada de Armazenamento Temporário (Memory Repositories)
│   ├── JogadorRepositorio.java
│   └── ImovelRepositorio.java
└── servico/                              # Camada de Serviços e Lógica de Negócio
    ├── Jogo.java
    └── PreJogoService.java
```

### Divisão em Camadas e Responsabilidades
* **Camada de Estruturas (`estrutura`):** Contém as estruturas de dados lineares implementadas de forma genérica ou específica para o tabuleiro. O objetivo é garantir independência de coleções utilitárias do Java (como `LinkedList` ou `Stack` nativas) para as operações estruturais fundamentais do tabuleiro e do baralho.
* **Camada de Modelos (`modelo`):** Contém as entidades que possuem dados e comportamentos próprios do jogo. As classes deste pacote não sabem como o jogo é coordenado ou como as classes são persistidas; elas apenas gerenciam seus próprios estados internos (ex: um jogador sabe pagar e receber saldo, mas não sabe de quem é a vez no turno).
* **Camada de Repositórios (`repositorio`):** Funciona como uma camada de abstração de dados (in-memory). Ela gerencia o armazenamento das instâncias de jogadores e imóveis durante a fase de configuração, realizando validações locais (como chaves duplicadas e limites de capacidade máxima).
* **Camada de Serviços (`servico`):** Responsável por orquestrar o fluxo do sistema. O `PreJogoService` valida as condições e constrói o tabuleiro dinamicamente. O `Jogo` controla o loop principal de rodadas, turnos, movimentações e transações financeiras entre os jogadores e o tabuleiro.

### Relacionamento entre as Camadas (Diagrama de Dependências)

```
       ┌────────────────────────┐
       │          Main          │
       └───────────┬────────────┘
                   │ Usa
                   ▼
       ┌────────────────────────┐
       │     PreJogoService     ├────────────────────────┐
       └─────┬────────────┬─────┘                        │ Instancia e Inicia
             │            │                              ▼
             │ Usa        │ Usa                ┌──────────────────┐
             ▼            ▼                    │       Jogo       │
  ┌──────────────┐    ┌──────────────┐         └────────┬─────────┘
  │JogadorReposit│    │ImovelReposito│                  │
  └──────┬───────┘    └──────┬───────┘                  │ Controla e Usa
         │                   │                          ▼
         ▼                   ▼                 ┌──────────────────┐
  ┌──────────────┐    ┌──────────────┐         │   Estruturas     │
  │   Jogador    │    │  CasaImovel  │         │ (Lista & Pilha)  │
  └──────────────┘    └──────────────┘         └──────────────────┘
```

---

## 3. Estruturas de Dados Utilizadas

Nesta seção justificamos a escolha das estruturas de dados personalizadas utilizadas no projeto.

### Lista Duplamente Ligada Circular
* **Qual estrutura é:** Uma lista encadeada composta por nós (`NoTabuleiro`), onde cada nó possui ponteiros para o `proximo` e para o `anterior`. O último nó (`cauda`) aponta de volta para o primeiro (`cabeca`), fechando um anel contínuo de nós.
* **Onde é utilizada:** No mapeamento e representação física das casas do tabuleiro ([ListaDuplamenteLigadaCircular.java](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/estrutura/ListaDuplamenteLigadaCircular.java)).
* **Por que é a melhor escolha:** O tabuleiro de um jogo de estilo Monopoly é, por definição, um circuito circular fechado. Ao utilizar uma lista circular, a movimentação do jogador para frente torna-se uma simples iteração utilizando `ponteiro.getProximo()` sem a necessidade de realizar cálculos manuais de módulo aritmético sobre índices de arrays. A característica *duplamente ligada* permite que cartas de penalidade movam os jogadores para trás de forma imediata e elegante usando `ponteiro.getAnterior()`.
* **Complexidade das Operações:**
  * **Adicionar (Inserção no final):** $O(1)$ - Mantemos uma referência direta para o nó `cauda`, permitindo inserções instantâneas.
  * **Buscar por ID/Nome:** $O(N)$ - No pior caso, percorre todo o anel do tabuleiro uma única vez.
  * **Remover por ID:** $O(N)$ - Busca o nó correspondente e ajusta os ponteiros dos vizinhos em $O(1)$.

### Pilha do Baralho
* **Qual estrutura é:** Uma pilha linear dinâmica encadeada utilizando nós ([Pilha.java](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/estrutura/Pilha.java)).
* **Onde é utilizada:** Na implementação do baralho de cartas de Sorte ou Revés ([Baralho.java](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/modelo/carta/Baralho.java)).
* **Por que é a melhor escolha:** O regulamento de cartas de sorte ou revés dita que as cartas devem ser sacadas do topo. A pilha garante o comportamento LIFO (*Last In, First Out*) de forma nativa e segura. Além disso, a implementação dinâmica evita desperdício de memória e permite o embaralhamento dinâmico do deck com facilidade.
* **Complexidade das Operações:**
  * **Empilhar (Push):** $O(1)$ - Inserção sempre no topo da pilha.
  * **Desempilhar (Pop):** $O(1)$ - Remoção e retorno do elemento do topo da pilha.
  * **Espiar (Peek):** $O(1)$ - Consulta do topo sem remoção.
  * **Embaralhar:** $O(N)$ - Realizado através da cópia temporária dos dados para um vetor e aplicação do algoritmo de embaralhamento de *Fisher-Yates*, seguido de re-empilhamento.

---

## 4. Explicação de Todas as Classes

Abaixo está detalhado o papel de cada classe presente no projeto e a justificativa de sua existência.

### Pacote `estrutura`

#### [NoTabuleiro](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/estrutura/NoTabuleiro.java)
* **Objetivo & Responsabilidade:** Funcionar como célula de encapsulamento para a lista do tabuleiro. Guarda a referência para a instância de uma `Casa` e para os nós adjacentes (`proximo` e `anterior`).
* **Justificativa:** Necessário para permitir o encadeamento dinâmico sem expor os detalhes de ponteiros diretamente dentro da entidade de modelo `Casa`.
* **Relações:** Guarda uma `Casa`. É utilizado por `ListaDuplamenteLigadaCircular` e mantido como atributo de posição dentro de `Jogador`.

#### [ListaDuplamenteLigadaCircular](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/estrutura/ListaDuplamenteLigadaCircular.java)
* **Objetivo & Responsabilidade:** Representar a coleção encadeada circular das casas do tabuleiro. Prover métodos para inserção, remoção, busca e movimentação de jogadores com callbacks de passagem.
* **Justificativa:** Isolar a lógica de ponteiros e travessia circular da lógica de regras do jogo.
* **Relações:** Gerencia nós `NoTabuleiro`. Usada por `PreJogoService` para montar o tabuleiro e por `Jogo` para movimentar jogadores e buscar estatísticas de aluguel.

#### [NoPilha](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/estrutura/NoPilha.java)
* **Objetivo & Responsabilidade:** Célula de encapsulamento para a pilha. Armazena o dado genérico tipo `T` e a referência para o nó de baixo.
* **Justificativa:** Garantir o encadeamento dinâmico da pilha de forma genérica.
* **Relações:** Utilizado exclusivamente pela classe `Pilha`.

#### [Pilha](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/estrutura/Pilha.java)
* **Objetivo & Responsabilidade:** Implementação de uma estrutura de dados de pilha genérica (`Pilha<T>`). Responsável pelas operações clássicas de `empilhar`, `desempilhar`, `espiar`, `limpar` e pelo algoritmo de embaralhamento.
* **Justificativa:** Permitir que o baralho de cartas seja estruturado com o comportamento LIFO utilizando alocação dinâmica.
* **Relações:** Gerencia objetos do tipo `NoPilha<T>`. É instanciada como `Pilha<Carta>` na classe `Baralho`.

---

### Pacote `modelo`

#### [Jogador](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/modelo/Jogador.java)
* **Objetivo & Responsabilidade:** Representar a entidade de um jogador na partida. Controla dados cadastrais, saldo financeiro, voltas dadas no tabuleiro, estado de falência, estado de prisão, tipo de personagem e lista de propriedades adquiridas. Possui métodos para aplicar transações financeiras (pagamentos, aluguéis, bônus e impostos) considerando regras passivas de seu personagem.
* **Justificativa:** Centralizar o estado financeiro e de progresso do jogador e garantir que regras de modificadores passivos (como descontos de aluguel do Negociante ou imunidade à prisão do Advogado) sejam aplicadas no momento em que as ações financeiras ou penais acontecem.
* **Relações:** Utiliza `NoTabuleiro` para rastrear sua posição e `CasaImovel` para gerenciar sua lista de propriedades compradas. É utilizado por todas as classes da camada de serviço e repositório.

#### [TipoPersonagem](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/modelo/TipoPersonagem.java)
* **Objetivo & Responsabilidade:** Enumeração que lista as 4 especializações de personagens disponíveis para escolha dos jogadores (`ESPECULADOR`, `NEGOCIANTE`, `ADVOGADO`, `CONSTRUTOR`).
* **Justificativa:** Fornecer constantes tipadas para evitar o uso de strings mágicas na identificação das habilidades passivas.
* **Relações:** Atributo identificador na classe `Jogador`.

---

### Subpacote `modelo.casa`

#### [Casa](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/modelo/casa/Casa.java)
* **Objetivo & Responsabilidade:** Classe abstrata base para todas as casas do tabuleiro. Armazena atributos comuns como `id`, `nome` e `descricao`.
* **Justificativa:** Evitar duplicação de código e permitir o tratamento polimórfico das casas no tabuleiro circular.
* **Relações:** Superclasse de `CasaInicio`, `CasaImovel`, `CasaImposto`, `CasaRestituicao` e `CasaSorteReves`.

#### [CasaInicio](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/modelo/casa/CasaInicio.java)
* **Objetivo & Responsabilidade:** Representar o ponto de partida do tabuleiro, onde os jogadores ganham um bônus salarial ao passar ou pousar.
* **Justificativa:** Especializar o comportamento do ponto inicial do tabuleiro de forma separada das propriedades e impostos.

#### [CasaImovel](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/modelo/casa/CasaImovel.java)
* **Objetivo & Responsabilidade:** Representar um imóvel no tabuleiro. Armazena preço de compra, aluguel base, aluguel por casa construída, custo de construção, quantidade de casas, presença de hotel, grupo de cores, referência para o proprietário atual e o acumulado de aluguel gerado.
* **Justificativa:** Centralizar as regras de melhoria de propriedades e o cálculo dinâmico de aluguel com base no número de casas e no tipo de personagem do proprietário (ex: acréscimo de 15% para o Construtor).
* **Relações:** Associa-se a um `Jogador` (proprietário).

#### [CasaImposto](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/modelo/casa/CasaImposto.java)
* **Objetivo & Responsabilidade:** Representar um espaço de penalidade tributária tributada pelo banco.
* **Justificativa:** Isolar o valor da taxa tributária em uma classe especializada.

#### [CasaRestituicao](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/modelo/casa/CasaRestituicao.java)
* **Objetivo & Responsabilidade:** Representar um espaço de benefício fiscal onde o jogador recebe uma devolução de dinheiro do banco.
* **Justificativa:** Isolar a regra de bonificação fiscal do ponto de partida.

#### [CasaSorteReves](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/modelo/casa/CasaSorteReves.java)
* **Objetivo & Responsabilidade:** Representar a casa do tabuleiro que engatilha o sorteio de uma carta do baralho.
* **Justificativa:** Identificar de forma polimórfica o momento em que o jogador deve interagir com o baralho de cartas.

---

### Subpacote `modelo.carta`

#### [TipoCarta](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/modelo/carta/TipoCarta.java)
* **Objetivo & Responsabilidade:** Enumeração que distingue as cartas entre `SORTE` (vantagem) e `REVES` (desvantagem).
* **Justificativa:** Classificar e rotular as cartas para fins informativos e estatísticos no console.

#### [EfeitoCarta](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/modelo/carta/EfeitoCarta.java)
* **Objetivo & Responsabilidade:** Interface funcional que define o contrato para a aplicação do efeito de uma carta através de expressões Lambda:
  `void aplicar(Jogador jogadorAtivo, List<Jogador> todosJogadores, ListaDuplamenteLigadaCircular tabuleiro);`
* **Justificativa:** Evitar a criação de 12 subclasses diferentes de cartas. O uso de uma interface funcional permite definir o comportamento de cada carta de forma inline e dinâmica no momento de sua criação.

#### [Carta](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/modelo/carta/Carta.java)
* **Objetivo & Responsabilidade:** Representar uma carta individual. Armazena `id`, `titulo`, `descricao`, `tipo` e a referência para o comportamento `efeito`.
* **Justificativa:** Encapsular as informações textuais e o gatilho de execução de cada carta.
* **Relações:** Contém uma referência para a interface `EfeitoCarta`.

#### [Baralho](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/modelo/carta/Baralho.java)
* **Objetivo & Responsabilidade:** Representar o conjunto de cartas de Sorte ou Revés. Inicializa as 12 cartas padrão do jogo (6 Sorte e 6 Revés), gerencia o estoque de cartas utilizando a estrutura de `Pilha` e reconstitui o deck quando as cartas chegam ao fim.
* **Justificativa:** Encapsular toda a complexidade de manipulação de cartas (como o embaralhamento e a reciclagem de cartas descartadas) longe do motor central do jogo.
* **Relações:** Utiliza `Pilha<Carta>`. É instanciada por `Jogo`.

---

### Pacote `repositorio`

#### [JogadorRepositorio](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/repositorio/JogadorRepositorio.java) & [ImovelRepositorio](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/repositorio/ImovelRepositorio.java)
* **Objetivo & Responsabilidade:** Prover persistência em memória para jogadores e imóveis durante a fase de configuração pré-jogo. Implementam as operações clássicas de CRUD e aplicam as restrições de limites de capacidade (máximo de 6 jogadores e máximo de 40 imóveis).
* **Justificativa:** Garantir a separação de responsabilidades. O jogo não deve gerenciar cadastros de forma direta em seu motor de execução. A persistência em memória simula um banco de dados real.
* **Relações:** Gerenciam listas internas de `Jogador` e `CasaImovel`. São consumidos por `PreJogoService`.

---

### Pacote `servico`

#### [PreJogoService](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/servico/PreJogoService.java)
* **Objetivo & Responsabilidade:** Validar as condições necessárias de pré-jogo e realizar a montagem física inicial do tabuleiro circular. O algoritmo intercala inteligentemente imóveis cadastrados com as casas especiais a taxas regulares (Sorte/Revés a cada 3 imóveis, Imposto a cada 5, Restituição a cada 7).
* **Justificativa:** Isolar o algoritmo de construção e validação de partida do loop de turnos do jogo.
* **Relações:** Depende de `JogadorRepositorio` e `ImovelRepositorio` para obter os dados de configuração, e instancia a classe `Jogo` ao iniciar a partida.

#### [Jogo](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/servico/Jogo.java)
* **Objetivo & Responsabilidade:** Atuar como o motor central da execução da partida. Controla o andamento do jogo, executa as rodadas, realiza rolagens de dados, move os jogadores na lista circular, gerencia transações financeiras e falências, e gera o relatório final consolidado.
* **Justificativa:** Centralizar o fluxo dinâmico da partida após a validação das configurações iniciais.
* **Relações:** Contém o `tabuleiro` (`ListaDuplamenteLigadaCircular`), o `baralho` (`Baralho`), a lista de jogadores ativos e a lista de jogadores eliminados por falência.

---

## 5. Explicação dos Principais Métodos

Abaixo estão explicados detalhadamente os principais métodos que regem o comportamento dinâmico do sistema.

### `moverEVerificarPassagemInicio(NoTabuleiro atual, int passos, Runnable acaoAoPassar)`
* **Onde está:** [ListaDuplamenteLigadaCircular.java](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/estrutura/ListaDuplamenteLigadaCircular.java#L171)
* **Função no sistema:** Faz o ponteiro de posição do jogador caminhar passo a passo para a frente na lista circular. A cada passo dado, o método verifica se o nó alcançado contém uma instância de `CasaInicio`. Se sim, executa a ação enviada via parâmetro `Runnable` (que no fluxo do jogo representa o pagamento de salário ao jogador por completar voltas).
* **Importância:** Garante que o jogador receba seu salário mesmo se a rolagem dos dados o faça pousar depois da casa de início, simulando com precisão a regra física do jogo de tabuleiro.

### `criarPartida()`
* **Onde está:** [PreJogoService.java](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/servico/PreJogoService.java#L64)
* **Função no sistema:** É o construtor dinâmico do tabuleiro. Ele primeiro chama `validarConfiguracao()` para validar os limites mínimos de jogadores e imóveis. Em seguida, cria a `ListaDuplamenteLigadaCircular`, insere a `CasaInicio` na cabeça e percorre a lista de imóveis cadastrados no repositório inserindo-os no tabuleiro. A cada intervalo fixado (passos matemáticos), insere de forma intercalada casas especiais de Sorte/Revés, Impostos e Restituições.
* **Importância:** Permite que o tabuleiro se adapte a qualquer quantidade de imóveis cadastrados pelo usuário (entre 10 e 40), gerando uma experiência de jogo única e balanceada a cada nova partida.

### `iniciarPartida()`
* **Onde está:** [Jogo.java](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/servico/Jogo.java#L46)
* **Função no sistema:** Método que dispara o loop principal de execução. Enquanto a partida não estiver encerrada (ou seja, enquanto restarem 2 ou mais jogadores ativos e a contagem de rodadas for menor ou igual a 100), ele chama o método `jogarRodada()`. Ao sair do loop, dispara a geração do relatório consolidado final.

### `executarTurno(Jogador jogador)`
* **Onde está:** [Jogo.java](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/servico/Jogo.java#L93)
* **Função no sistema:** Executa o fluxo de ações de um único jogador:
  1. Verifica se o jogador está preso; se sim, processa sua tentativa de fuga (rolagem de dados idênticos ou pagamento de fiança).
  2. Rola dois dados de 6 lados e calcula o deslocamento.
  3. Move o jogador chamando `moverJogador`.
  4. Identifica a casa de parada e chama `aplicarEfeitoCasa`.
  5. Verifica o estado financeiro de todos os jogadores ativos e declara falências caso necessário.

### `processarCasaImovel(Jogador jogador, CasaImovel imovel)`
* **Onde está:** [Jogo.java](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/servico/Jogo.java#L186)
* **Função no sistema:** Define o comportamento do jogador ao pousar em uma propriedade:
  * Se não possui dono: O jogador compra o imóvel se tiver saldo suficiente.
  * Se o dono é o próprio jogador: Oferece a oportunidade de comprar melhorias (adicionar casas ou hotel) pagando o custo de construção.
  * Se o dono é um oponente: Calcula o aluguel devido (com base nas melhorias e tipo de personagem) e realiza a transferência financeira entre os jogadores, registrando o valor pago como estatística da propriedade.

---

## 6. Fluxo Completo da Execução

O diagrama abaixo descreve a ordem cronológica de interações entre as classes desde a inicialização do programa até o encerramento do jogo:

```
[Main]                [Repo/Service]              [Tabuleiro/Baralho]           [Jogador]
  │                          │                            │                         │
  │── 1. Cadastra dados ────>│                            │                         │
  │   (Imóveis/Jogadores)    │                            │                         │
  │                          │                            │                         │
  │── 2. Chame criarPartida()│                            │                         │
  │                          │─── 3. Valida e monta ─────>│                         │
  │                          │      tabuleiro dinâmico    │                         │
  │                          │                            │                         │
  │── 4. Iniciar Partida ───>│                            │                         │
  │                          │─── 5. Coloca jogadores na ──────────────────────────>│ Define posição
  │                          │      casa inicial          │                         │ como Cabeça
  │                          │                            │                         │
  │                          │─── 6. Loop de Rodadas      │                         │
  │                          │      (Turno do Jogador)    │                         │
  │                          │                            │                         │
  │                          │─── 7. Rola dados e move ──>│                         │
  │                          │                            │─── 8. Cruza Início? ───>│ Recebe salário
  │                          │                            │                         │
  │                          │─── 9. Aplica efeito casa ─>│                         │
  │                          │      (Ex: Casa Sorte)      │                         │
  │                          │                            │─── 10. Saca Carta ─────>│ Executa efeito
  │                          │                            │    (Pilha LIFO)         │ da carta
  │                          │                            │                         │
  │                          │─── 11. Verifica falências ──────────────────────────>│ Se saldo < 0,
  │                          │                                                      │ perde posses e
  │                          │                                                      │ sai do jogo
  │                          │                            │                         │
  │── 12. Fim de Jogo ──────>│                            │                         │
  │                          │─── 13. Exibe Relatório ────┼────────────────────────>│ Calcula patrimônio
  │                          │        Final Consolidado   │                         │ e monta ranking
```

---

## 7. Principais Conceitos de Orientação a Objetos

O projeto utiliza amplamente os pilares da programação orientada a objetos para garantir flexibilidade e fácil manutenção do sistema.

### Encapsulamento
Os atributos das classes (como o saldo do `Jogador` ou a quantidade de casas da `CasaImovel`) são mantidos como `private`. O acesso e alteração desses estados ocorrem estritamente por métodos seletores e modificadores (`getters`/`setters`) ou por métodos de negócio que validam as transações, garantindo que o estado interno do objeto não seja corrompido.
* **Exemplo:** A alteração de saldo do jogador é feita de forma segura pelos métodos `adicionarSaldo(double)` e `pagar(double)`, impedindo alterações diretas e inconsistentes no atributo `saldo`.

### Herança
Utilizada para especializar as divisões do tabuleiro de jogo. A classe abstrata `Casa` define as características fundamentais de qualquer espaço física do tabuleiro.
* **Exemplo:** `CasaImovel`, `CasaInicio`, `CasaImposto`, `CasaRestituicao` e `CasaSorteReves` herdam diretamente de [Casa.java](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/modelo/casa/Casa.java).

### Polimorfismo
Permite tratar de forma genérica os diferentes tipos de casas no tabuleiro. No motor do jogo ([Jogo.java](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/servico/Jogo.java)), a movimentação do jogador resulta em uma referência genérica do tipo `Casa`. Através do polimorfismo, o jogo distribui o comportamento correto sem precisar duplicar a lógica de movimentação para cada tipo de casa.
* **Exemplo:** O método `aplicarEfeitoCasa(Jogador, Casa)` utiliza verificação de tipos em tempo de execução para direcionar o comportamento correto de cada subclasse da classe `Casa`.

### Abstração
A classe [Casa](file:///c:/Users/mathe/AlgoritmosEEstruturasDeDados/AS/src/modelo/casa/Casa.java) é declarada como `abstract`. Ela serve puramente como um molde conceitual, pois no tabuleiro real não existem casas genéricas, apenas suas especializações concretas (imóveis, impostos, etc.).

### Composição
Utilizada quando uma classe é composta por outras instâncias que não fariam sentido ou não teriam ciclo de vida independente fora dela.
* **Exemplo:** A classe `Baralho` é composta por uma instância de `Pilha<Carta>`. A pilha de cartas é parte intrínseca do funcionamento interno do baralho.

### Associação
Representa o relacionamento no qual as classes se conhecem e interagem, mas mantêm seus ciclos de vida totalmente independentes.
* **Exemplo:** A classe `Jogador` possui uma associação com `NoTabuleiro` (para saber onde está parado) e com `CasaImovel` (para gerenciar sua carteira de propriedades). Da mesma forma, `CasaImovel` guarda uma referência para o `Jogador` que a possui atualmente (proprietário).

---

## 8. Decisões de Projeto

### 1. Separação dos Repositórios da Classe `Jogo`
* **Decisão:** Criar `JogadorRepositorio` e `ImovelRepositorio` ao invés de gerenciar listas diretamente em `Jogo`.
* **Justificativa:** A classe `Jogo` é o motor da simulação ativa. Misturar a lógica de turnos com operações de criação, validação de chaves duplicadas e remoção de registros violaria o **Princípio da Responsabilidade Única (SRP)**. Com os repositórios isolados, a fase de pré-jogo pode evoluir (ex: ganhar uma interface gráfica ou salvar em banco de dados) sem que nenhuma linha de código da simulação da partida precise ser modificada.

### 2. Uso de Interface Funcional para Efeitos de Cartas
* **Decisão:** Mapear os efeitos das cartas utilizando a interface funcional `EfeitoCarta` e expressões Lambda.
* **Justificativa:** Se optássemos por herança tradicional, precisaríamos criar 12 classes adicionais (ex: `CartaHeranca`, `CartaConsertoCarro`, etc.). Isso inflaria o número de arquivos do projeto desnecessariamente. A abordagem por interface funcional permite que o comportamento dinâmico de alteração de saldo, movimentação e pagamentos coletivos seja injetado como dado puro na inicialização do `Baralho`, simplificando a arquitetura.

### 3. Posição do Jogador como Referência de Nó (`NoTabuleiro`)
* **Decisão:** O jogador guarda uma referência direta para o `NoTabuleiro` em vez de armazenar apenas um índice numérico (ex: `posicao = 5`).
* **Justificativa:** Ao guardar a referência para o nó da lista encadeada, o acesso à casa atual, à casa seguinte (`getProximo()`) e à casa anterior (`getAnterior()`) é feito em tempo constante $O(1)$. Se guardássemos um índice inteiro, toda movimentação ou consulta exigiria percorrer a lista a partir da cabeça para localizar o nó correspondente, gerando um custo computacional desnecessário de $O(N)$.

### 4. Implementação Manual de Lista Duplamente Ligada Circular para o Tabuleiro
* **Decisão:** Não utilizar classes utilitárias do ecossistema do Java Collections (`ArrayList` ou `LinkedList`).
* **Justificativa:** Coleções padrão do Java não possuem comportamento circular nativo. Usar um `ArrayList` exigiria controles constantes de índices e tratamento de transbordo (como `indice % tamanho`). A lista encadeada circular modela perfeitamente a topologia física de um tabuleiro de Monopoly, eliminando a necessidade de validações de limites de arrays e simplificando a navegação para frente e para trás.

---

## 9. Tutorial: Como Jogar

Este sistema simula uma partida completa de forma automatizada no console. Siga os passos abaixo para compreender o funcionamento:

### Passo 1: Iniciar o Programa
1. Abra o terminal na pasta raiz do projeto.
2. Compile os arquivos e execute a classe `Main` (consulte a seção de execução rápida no final deste documento).
3. O programa iniciará exibindo a mensagem: `========================================================= SISTEMA DE GERENCIAMENTO PRÉ-JOGO =========================================================`.

### Passo 2: Fase de Cadastro (Pré-Jogo)
Durante a inicialização da classe `Main`, os cadastros são realizados automaticamente através dos repositórios:
* **Cadastrar Jogadores:** Instancie objetos do tipo `Jogador` informando um ID, nome, saldo inicial (sugerido: R$ 1200) e o seu `TipoPersonagem`. Adicione-os chamando `jogadorRepo.adicionar(jogador)`.
* **Cadastrar Imóveis:** Instancie objetos `CasaImovel` informando ID, nome, descrição, preço de compra, aluguel base, aluguel por casa, custo de construção de casa e grupo de cores. Adicione-os chamando `imovelRepo.adicionar(imovel)`.

### Passo 3: Configuração e Validação da Partida
O `PreJogoService` fará as verificações de segurança:
* O jogo impede o início se houver menos de 2 ou mais de 6 jogadores.
* O jogo impede o início se houver menos de 10 ou mais de 40 imóveis.
* Se aprovado, o serviço monta o tabuleiro circular distribuindo os imóveis e intercalando as casas especiais.

### Passo 4: O Fluxo dos Turnos
Após o início da partida, cada rodada simula o turno de cada jogador ativo de forma sequencial:
1. **Rolagem de Dados:** O sistema rola dois dados de 6 lados.
2. **Movimentação:** O jogador avança o número de casas correspondente à soma dos dados no tabuleiro circular. Se passar pela casa de início, recebe R$ 200,00 de salário (ou R$ 240,00 se for o personagem *Especulador*).
3. **Interação com a Casa:**
   * **Imóvel Livre:** Se o jogador tiver saldo suficiente, compra o imóvel automaticamente.
   * **Imóvel Próprio:** O jogador avalia e constrói uma melhoria (adiciona uma casa ou transforma 4 casas em um hotel) pagando o custo de construção.
   * **Imóvel de Outro Jogador:** Paga o valor de aluguel calculado ao proprietário (o personagem *Negociante* ganha 10% de desconto no pagamento).
   * **Sorte ou Revés:** Saca uma carta da pilha e aplica imediatamente o seu efeito (pode ser receber ou pagar dinheiro para o banco ou para os outros jogadores, avançar ou voltar casas).
   * **Imposto:** Paga a taxa estipulada ao banco (*Especulador* paga 10% a mais).
   * **Restituição:** Recebe um bônus financeiro do banco.

### Passo 5: Condição de Vitória e Fim de Jogo
* **Eliminação:** Se o saldo de um jogador cair abaixo de zero em qualquer pagamento e ele não puder cumprir a obrigação, ele é declarado falido. Suas propriedades são desapropriadas (limpas e devolvidas ao tabuleiro) e ele é removido do jogo.
* **Vitória por W.O.:** A partida encerra imediatamente se sobrar apenas 1 jogador ativo no tabuleiro.
* **Vitória por Limite de Rodadas:** Caso a partida atinja a rodada 100, o jogo é interrompido e o jogador ativo com o maior patrimônio acumulado ($\text{saldo} + \text{valor de compra das propriedades}$) é coroado o vencedor.

---

## 10. Exemplo Completo de Uma Partida Simulada

Abaixo está um exemplo conceitual de como os logs do console registram a execução passo a passo do jogo:

### 1. Configuração Inicial e Tabuleiro Gerado
* **Jogadores cadastrados:** 
  * Matheus (Especulador) - Saldo: R$ 1200.00
  * Ana (Negociante) - Saldo: R$ 1200.00
  * Bruno (Advogado) - Saldo: R$ 1200.00
* **Tabuleiro montado:** 10 imóveis cadastrados + Casas Especiais intercaladas.
  * *Posições:* [0] Ponto de Partida ➔ [1] Avenida Paulista ➔ [2] Copacabana ➔ [3] Rua Oscar Freire ➔ [1000] Sorte ou Revés ➔ [4] Ipanema ➔ ...

### 2. Primeira Rodada
* **Turno de Matheus:**
  * Posição inicial: Ponto de Partida
  * Rolagem de dados: [3, 4] ➔ Total: 7 casas.
  * Movimentação: Matheus move-se de *Ponto de Partida* para *Pelourinho*.
  * Efeito: *Pelourinho* está à venda por R$ 180.00.
  * Ação: Matheus compra o imóvel. Saldo restante: R$ 1020.00.

* **Turno de Ana:**
  * Posição inicial: Ponto de Partida
  * Rolagem de dados: [1, 2] ➔ Total: 3 casas.
  * Movimentação: Ana move-se para *Rua Oscar Freire*.
  * Ação: Ana compra o imóvel por R$ 280.00. Saldo restante: R$ 920.00.

### 3. Segunda Rodada
* **Turno de Matheus:**
  * Posição inicial: *Pelourinho* (ID 7)
  * Rolagem de dados: [2, 1] ➔ Total: 3 casas.
  * Movimentação: Matheus cai na casa *Sorte ou Revés* (ID 1000).
  * Ação: Saca a carta: **"Herança Inesperada"** (*Sorte*).
  * Efeito da Carta: *"Um parente distante deixou uma herança para você. Receba R$ 150 do banco."*
  * Resultado: Saldo de Matheus aumenta para R$ 1170.00.

* **Turno de Ana:**
  * Posição inicial: *Rua Oscar Freire*
  * Rolagem de dados: [4, 1] ➔ Total: 5 casas.
  * Movimentação: Ana move-se para *Lagoa da Pampulha*.
  * Ação: Ana compra o imóvel por R$ 220.00. Saldo restante: R$ 700.00.

### 4. Rodadas Avançadas (Exemplo de Aluguel e Falência)
* **Turno de Bruno:**
  * Bruno rola os dados e cai na *Rua Oscar Freire* (que pertence à Ana).
  * O aluguel da *Rua Oscar Freire* (sem melhorias) é de R$ 22.00.
  * Ação: Bruno paga R$ 22.00 para Ana. 
  * Estatísticas: A propriedade registra `totalAluguelGerado = R$ 22.00`.

* **Turno de Matheus (Passagem pelo início):**
  * Matheus rola os dados e o movimento ultrapassa o início do tabuleiro.
  * Ação: A callback de travessia é disparada. Matheus (Especulador) recebe R$ 240.00 de salário (R$ 200 + 20% de bônus de personagem).

---

## 11. Conclusão

A arquitetura desenvolvida para este jogo demonstra como a seleção correta de estruturas de dados e a aplicação rigorosa de padrões orientados a objetos tornam o desenvolvimento de sistemas complexos de simulação robusto e legível.

### Resumo dos Benefícios da Arquitetura Adotada
* **Manutenibilidade:** Cada classe possui uma única responsabilidade. Se for necessário alterar o valor do imposto de renda ou adicionar uma nova carta de sorte ou revés, as alterações ficam restritas a arquivos específicos, sem impacto no motor de turnos do jogo.
* **Uso Inteligente de Estruturas de Dados:** A **Lista Duplamente Ligada Circular** eliminou a complexidade de controle de limites físicos do tabuleiro, permitindo que a movimentação do jogador no tabuleiro seja tratada de forma contínua e natural. A **Pilha** garantiu o comportamento clássico de cartas de baralho LIFO com alta performance e baixo consumo de memória.
* **Extensibilidade:** Através do polimorfismo de `Casa` e do uso de interfaces funcionais em `EfeitoCarta`, novos tipos de casas de tabuleiro e novas regras de cartas podem ser adicionados ao jogo com pouquíssimas linhas de código, mantendo o sistema preparado para futuras expansões.
* **Separação de Camadas (Decoupling):** A clara separação entre a camada de persistência (Repositórios), a camada de execução (Serviços) e a camada de interface (Console/Main) garante que o jogo possa ser facilmente portado para uma interface gráfica (JavaFX, Swing ou Web) no futuro.

---

## 🛠️ Compilação e Execução Rápida

Para compilar e executar o projeto diretamente do terminal, utilize os comandos abaixo na pasta raiz do projeto:

```bash
# Compilar todas as classes do projeto
javac -encoding UTF-8 -d bin src/modelo/TipoPersonagem.java src/modelo/Jogador.java src/modelo/casa/Casa.java src/modelo/casa/CasaImovel.java src/modelo/casa/CasaInicio.java src/modelo/casa/CasaImposto.java src/modelo/casa/CasaRestituicao.java src/modelo/casa/CasaSorteReves.java src/modelo/carta/TipoCarta.java src/modelo/carta/EfeitoCarta.java src/modelo/carta/Carta.java src/modelo/carta/Baralho.java src/repositorio/JogadorRepositorio.java src/repositorio/ImovelRepositorio.java src/estrutura/NoPilha.java src/estrutura/Pilha.java src/estrutura/NoTabuleiro.java src/estrutura/ListaDuplamenteLigadaCircular.java src/servico/Jogo.java src/servico/PreJogoService.java src/Main.java

# Executar a simulação
java -cp bin Main
```
