# Olimpo APP🏛️💙💙

## Sobre o Projeto

*Olimpo* é uma plataforma desenvolvida para facilitar a comunicação, gestão e convivência de comunidades, promovendo interações entre os membros e incentivando o comércio local entre eles. Através do aplicativo, os usuários podem criar comunidades e participar de outras, possibilitando um ambiente de compartilhamento e colaboração.

### Funcionalidades Principais

- *Criação e Gerenciamento de Comunidades*: Crie comunidades para grupos com interesses comuns e gerencie as solicitações de membros que desejam fazer parte.
- *Feed de Publicações*: Compartilhe e visualize publicações dos membros da comunidade, com a possibilidade de interagir e acessar o perfil dos usuários.
- *Chat da Comunidade*: Comunicação interna entre os membros da comunidade, facilitando a troca de mensagens em um ambiente dedicado.
- *Anúncios e Comércio Local*: Espalhe ofertas de venda, doação ou serviços para os membros da comunidade, incentivando o comércio e a colaboração.

### Tecnologias Utilizadas

- *Kotlin* para o desenvolvimento do aplicativo.
- *Retrofit* para chamadas de API assíncronas.
- *Firebase* para armazenamento de imagens e gerenciamento de chat.
- *XML* para o design e interface do aplicativo.

## Estrutura do Projeto

O projeto está organizado em diferentes pacotes, não utilizamos nenhuma arquitetura em específico, mas o projeto está organizado para facilitar a compreensão do código


com
└── example
    └── olimpo_app
        ├── data
        │   ├── firebase          # Gerenciamento de dados e comunicação com o Firebase
        │   └── model             # Models de dados organizados por fluxo de funcionalidades
        │       ├── accessFlow    # Models e dados relacionados ao fluxo de acesso
        │       ├── feedFlow      # Models para o fluxo do feed de publicações
        │       ├── messageFlow   # Models para o fluxo de mensagens e chat
        │       └── negotiationFlow # Models para o fluxo de anúncios e negociações
        ├── network               # Configuração de APIs e Retrofit
        ├── repository            # Repositórios para acesso aos dados da API
        ├── presentation          # Camada de apresentação com atividades, fragments e adaptadores
        │   ├── activity
        │   │   ├── accessFlow    # Activity para o fluxo de acesso
        │   │   ├── feedFlow      # Activity para o fluxo do feed
        │   │   ├── messageFlow   # Activity para o fluxo de mensagens
        │   │   └── negotiationFlow # Activity para o fluxo de negociações
        │   ├── adapters          # Adapters para RecyclerViews e listas
        │   ├── fragment
        │   │   ├── accessFlow    # Fragments para o fluxo de acesso
        │   │   ├── feedFlow      # Fragments para o fluxo do feed
        │   │   ├── messageFlow   # Fragments para o fluxo de mensagens
        │   │   └── negotiationFlow # Fragments para o fluxo de negociações
        │   ├── listeners         # Listeners para eventos e interações com a UI
        │   └── ui                # Componentes e views da interface de usuário
        └── utils                 # Utilitários e helpers do projeto



### Configuração do Ambiente

1. Clone o repositório e abra o projeto no *Android Studio*.
2. Configure o Firebase seguindo as instruções no console do Firebase, colocando o Json do Google.
3. Sincronize o projeto e faça o build para garantir que todas as dependências estejam configuradas corretamente.

### Ou… Use o APK!
