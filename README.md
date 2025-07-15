# Projeto RPE Pagamentos - Sistema de Gerenciamento de Clientes, Faturas e Pagamentos

## Visão Geral do Projeto

Este projeto é um sistema de gerenciamento de pagamentos , simulando parte de um ecossistema de fintech. Ele permite gerenciar clientes, suas faturas e registrar pagamentos, com um backend RESTful em Spring Boot e uma interface web simples.

## Tecnologias Utilizadas

### Back-End:
* **Java 21**
* **Spring Boot 3.x.x**
* **Spring Data JPA**
* **Maven**
* **MySQL 8.0** (Banco de Dados Relacional)
* **Lombok** (para reduzir boilerplate code)
* **SpringDoc OpenAPI UI / Swagger** (para documentação e teste da API)
* **JUnit 5** (para testes unitários)

### Front-End:
* **HTML5**
* **CSS3** (com variáveis CSS e design personalizado)
* **JavaScript Puro** (ES6+)

### Orquestração e Entrega:
* **Docker**
* **Docker Compose**
* **Git & GitHub** (Controle de Versão)

## Funcionalidades Implementadas

### 1. Banco de Dados
* Tabelas `Cliente` e `Fatura` com chaves primárias e estrangeiras.
* Scripts SQL para população inicial de dados de exemplo.
* Queries para listar clientes com atraso e bloquear (implementadas na camada de serviço/API).

### 2. Back-End (API REST)
* **Endpoints para Clientes:**
    * `GET /clientes`: Lista todos os clientes.
    * `POST /clientes`: Cadastra novo cliente.
    * `GET /clientes/{id}`: Consulta cliente por ID.
    * `PUT /clientes/{id}`: Atualiza/bloqueia cliente.
    * `GET /clientes/bloqueados`: Lista clientes bloqueados.
* **Endpoints para Faturas:**
    * `GET /faturas/cliente/{clienteId}`: Lista todas as faturas de um cliente específico.
    * `PUT /faturas/{id}/pagamento`: Registra pagamento para uma fatura.
    * `GET /faturas/atrasadas`: Lista faturas em atraso.
    * `POST /faturas`: Cria uma nova fatura (útil para testes).
    * `PUT /faturas/verificar-bloqueio-clientes`: Endpoint manual para gatilho de regra de bloqueio (em produção, seria um agendador).
* **Regras de Negócio na API:**
    * Ao registrar um pagamento, o status da fatura muda para "Paga".
    * Se o pagamento não for feito até 3 dias após o vencimento, o cliente é marcado como "Bloqueado" (acionado pelo endpoint de verificação).
    * Clientes bloqueados têm seu limite de crédito atualizado para R$ 0,00.
* **Documentação da API:** Via Swagger UI (`/swagger-ui.html`).

### 3. Front-End (Interface Web)
* **Listagem de Clientes:** Exibe Nome, CPF formatado, idade calculada, status de bloqueio e limite de crédito.
* **Formulário de Cadastro de Cliente:** Permite adicionar novos clientes com validação básica de CPF 
* **Faturas do Cliente:** Lista faturas com Valor, data de vencimento, status, data de pagamento.
* **Registro de Pagamento:** Botão para registrar pagamentos de faturas (desabilitado se já paga).
* **Experiência do Usuário (UX) Aprimorada:**
    * Máscara automática para entrada de CPF no formulário.
    * Indicador de carregamento (loading spinner) em operações assíncronas.
    * Feedback visual (mensagens de sucesso/erro) após ações.
    * Design clean com paleta de cores personalizada.
* **Navegação:** Botões para alternar entre lista de clientes e detalhes de faturas.

## Como Executar o Projeto

Você precisará ter o **Docker Desktop** (ou o Docker Engine e Docker Compose) instalado e rodando em sua máquina.

1.  **Clone o Repositório:**
    bash
    git clone [https://github.com/SEU_USUARIO/SEU_REPOSITORIO.git](https://github.com/SEU_USUARIO/SEU_REPOSITORIO.git)
    cd SEU_REPOSITORIO # Substitua pelo nome do seu repositório, ex: rpe-pagamentos-api
    
    
2.  **Configurar Variáveis de Ambiente Sensíveis:**
    Na raiz do projeto, crie um arquivo chamado `.env` e adicione suas senhas do MySQL. Este arquivo **NÃO** deve ser comitado no Git.
    env
    # .env 
    MYSQL_ROOT_PASSWORD=sua_senha_do_root_do_mysql
    RPE_APP_MYSQL_PASSWORD=sua_senha_do_rpeuser
    
    *Substitua pelas suas senhas reais.*

3.  **Executar com Docker Compose:**
    No terminal, na pasta raiz do projeto, execute:
    bash
    docker-compose up --build -d
    
    Este comando irá:
    * Construir a imagem Docker do backend (baseada no `Dockerfile`).
    * Baixar a imagem do MySQL.
    * Criar e iniciar os contêineres `rpe-mysql` e `rpe-backend`.
    * Mapear as portas 3306 (MySQL) e 8080 (Aplicação) para sua máquina local.

4.  **Acessar a Aplicação:**
    Após os contêineres estarem `Up` (verifique com `docker ps`):
    * **Interface Web:** [http://localhost:8080/](http://localhost:8080/)
    * **Documentação da API (Swagger UI):** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

5.  **Parar e Remover os Contêineres:**
    Quando terminar de usar a aplicação, execute:
    bash
    docker-compose down -v
    ```
    

## Pontos de Melhoria 

Se eu tivesse mais tempo, eu consideraria as seguintes melhorias para o projeto, que o tornariam ainda mais seguro e com uma experiência de usuário superior e mais eficiente :

### *1. Robustez e Segurança do Back-End:*

* *Validação de Entrada de Dados (Bean Validation):* Implementar validações de dados mais rigorosas nas Entidades (ex: @NotBlank, @Email, @Past, @CPF - com uma biblioteca externa) para garantir que os dados recebidos pela API sejam válidos antes de serem processados. Isso previne dados inválidos e ataques.
* **Tratamento Global de Exceções (@ControllerAdvice):** Padronizar as respostas de erro da API. Em vez de 500 Internal Server Error genéricos, retornar 400 Bad Request para validação falha, 404 Not Found para recursos inexistentes.com mensagens claras e formato JSON.


### *2. Funcionalidade e UX do Front-End:*

* *Formulários para Edição e Exclusão:* Adicionar a capacidade de editar informações de clientes existentes (PUT /clientes/{id}) ou até excluí-los (após confirmação)
* *Paginção e Filtros/Busca:* Para lidar com um grande número de clientes/faturas, teria campos de busca/filtro (por nome, CPF, status de fatura) para facilitar a localização de informações.
* *Notificações Mais Amigáveis:* Substituir os alert() e confirm() padrões do navegador por bibliotecas de notificação mais modernas (ex: Toastify, SweetAlert), que oferecem uma experiência visual mais agradável e não bloqueiam a interação do usuário.


### *3. Gerenciamento e Manutenção do Sistema:*

* *Agendamento de Tarefas:* No Back-End, implementar um Scheduler (com @Scheduled do Spring) para rodar a função de verificarEBloquearClientesAtrasados() automaticamente em intervalos regulares, em vez de depender de um endpoint manual.
* *Configuração de Produção:* Separar as configurações de desenvolvimento das de produção (ex: banco de dados externo, variáveis de ambiente de produção).
* *Otimização de Imagem Docker:* Explorar ainda mais otimizações para a imagem Docker (ex: JLink para criar um JRE mínimo customizado) para reduzir o tamanho final.
