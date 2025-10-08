# Sistema de Gestão de Estoque

Um sistema robusto para gerenciamento de estoque, desenvolvido com Spring Boot, oferecendo uma API RESTful para manipulação de produtos e categorias, além de funcionalidades de e-mail para notificações.

## 🚀 Tecnologias Utilizadas

Este projeto foi construído utilizando as seguintes tecnologias principais:

*   **Java 17+**: Linguagem de programação.
*   **Spring Boot 3.x**: Framework para construção de aplicações Java robustas e escaláveis.
    *   **Spring Data JPA**: Para abstração e persistência de dados.
    *   **Spring Web**: Para construção de APIs RESTful.
    *   **Spring Mail**: Para integração de serviços de e-mail.
*   **Hibernate**: Implementação da especificação JPA.
*   **MySQL**: Banco de dados relacional para armazenamento de dados.
*   **Swagger (Springdoc OpenAPI)**: Para documentação e teste interativo da API.
*   **Maven**: Ferramenta de gerenciamento de dependências e build do projeto.
*   **HikariCP**: Pool de conexões JDBC de alta performance.

## ✨ Funcionalidades

O sistema de gestão de estoque oferece as seguintes funcionalidades principais:

*   **Gerenciamento de Produtos**:
    *   Criação, leitura, atualização e exclusão (CRUD) de produtos.
    *   Associação de produtos a categorias.
    *   Controle de quantidade em estoque.
*   **Gerenciamento de Categorias**:
    *   Criação, leitura, atualização e exclusão (CRUD) de categorias de produtos.
*   **API RESTful**:
    *   Endpoints bem definidos para todas as operações, facilitando a integração com qualquer frontend ou outro serviço.
*   **Notificações por E-mail**:
    *   [Descreva aqui exemplos de notificações, ex: "Envio de e-mail de confirmação de pedido", "Alerta de estoque baixo para produtos específicos".]
*   **Documentação Interativa da API**:
    *   Através do Swagger UI, é possível explorar e testar todos os endpoints da API.

## 🏁 Primeiros Passos

Siga estas instruções para configurar e executar o projeto em seu ambiente local.

### Pré-requisitos

Antes de começar, certifique-se de ter instalado:

*   **JDK 17 ou superior**: [Link para download do JDK](https://www.oracle.com/java/technologies/downloads/)
*   **Maven**: [Link para download do Maven](https://maven.apache.org/download.cgi)
*   **MySQL Server**: [Link para download do MySQL Community Server](https://dev.mysql.com/downloads/mysql/)
*   **Um IDE (opcional, mas recomendado)**: IntelliJ IDEA, Eclipse ou VS Code.

### Instalação

1.  **Clone o repositório:**

2.  **Configuração do Banco de Dados MySQL:**
    *   Certifique-se de que seu servidor MySQL esteja rodando.
    *   O aplicativo tentará criar o banco de dados `estoque_db` automaticamente se ele não existir, devido à configuração `createDatabaseIfNotExist=true`.
    *   **Crie um usuário se necessário e defina a senha**, ou use o usuário `root` (com senha `1234` conforme seu `application.properties`).

3.  **Configuração do `application.properties`:**
    *   Crie um arquivo `src/main/resources/application.properties` se ele não existir, ou edite o existente.
    *   **Atualize as credenciais do banco de dados e as configurações de e-mail** conforme seu ambiente.
        ```properties
        # Configurações do Banco de Dados MySQL
        spring.datasource.url=jdbc:mysql://localhost:3306/estoque_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
        spring.datasource.username=root
        spring.datasource.password=1234 # <--- ATUALIZE COM SUA SENHA REAL DO MYSQL ROOT
        spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
        spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
        spring.jpa.hibernate.ddl-auto=update
        spring.jpa.show-sql=true
        spring.jpa.properties.hibernate.format_sql=true
        spring.jpa.open-in-view=false

        # Configurações de E-mail (Gmail Exemplo)
        spring.mail.host=smtp.gmail.com
        spring.mail.port=587
        spring.mail.username=[seu_email_aqui@gmail.com] # <--- SEU E-MAIL
        spring.mail.password=[sua_senha_ou_app_password] # <--- SUA SENHA OU SENHA DE APP DO GMAIL
        spring.mail.properties.mail.smtp.auth=true
        spring.mail.properties.mail.smtp.starttls.enable=true
        # Se usar Gmail, você pode precisar gerar uma "App password"
        # para sua conta Google se tiver autenticação de 2 fatores ativada.
        # https://support.google.com/accounts/answer/185833?hl=pt-BR

        # Configurações do Swagger/OpenAPI
        springdoc.api-docs.enabled=true
        springdoc.swagger-ui.enabled=true
        springdoc.swagger-ui.path=/swagger-ui.html

        # Configurações do Servidor
        server.port=8080
        server.error.include-message=always
        ```

4.  **Compilar e Executar o Projeto:**
    *   No diretório raiz do projeto, execute:
        ```bash
        mvn clean install
        mvn spring-boot:run
        ```
    *   A aplicação estará disponível em `http://localhost:8080`.

## 📖 Uso da API (Swagger UI)

Após iniciar a aplicação, você pode acessar a documentação interativa da API através do Swagger UI:

*   **Swagger UI**: `http://localhost:8080/swagger-ui.html`

Nesta interface, você poderá:
*   Visualizar todos os endpoints disponíveis (Produtos, Categorias, etc.).
*   Ver os detalhes de cada endpoint (método HTTP, parâmetros, estrutura de requisição/resposta).
*   Testar os endpoints diretamente clicando em "Try it out" e "Execute".

## ✉️ Funcionalidade de E-mail
Falta implementar...
## 📧 Contato

[Seu Nome/Nome da Equipe] - [Seu Email ou Link para Perfil do GitHub]
Link do Projeto: [https://github.com/[SeuUsuario]/[NomeDoSeuRepositorio]](https://github.com/[SeuUsuario]/[NomeDoSeuRepositorio])
