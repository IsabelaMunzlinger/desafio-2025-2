# Locadora - Projeto Spring Boot

Aplicação de uma locadora de exemplares com integração à API do [The Movie Database (TMDb)](https://www.themoviedb.org/).

---

## Tecnologias e Ferramentas Utilizadas

- Java 21  
- Spring Boot 3.4.5  
- Maven  
- MySQL  
- Thymeleaf (para views)  
- Spring Security  
- Hibernate Validator  
- Lombok  
- dotenv-java (para carregar variáveis de ambiente do arquivo `.env`)  

---

## Pré-requisitos

Antes de rodar o projeto, você precisa ter instalado:

- [Java JDK 21](https://jdk.java.net/21/)  
- [Maven](https://maven.apache.org/install.html)  
- [MySQL](https://dev.mysql.com/downloads/mysql/)  
- [Git](https://git-scm.com/downloads)  

Além disso, para a integração funcionar corretamente, é necessário:

- Criar uma conta na plataforma [The Movie Database (TMDb)](https://www.themoviedb.org/)  
- Gerar um **Token de acesso V4** na seção de configurações da sua conta TMDb  
- Inserir este token no arquivo `.env` do projeto

---

## Configuração do Projeto

1. Clone o repositório:

2. Configure o banco de dados MySQL:  
   Crie um banco de dados chamado `locadora`.

3. Crie um arquivo `.env` na raiz do projeto com o seguinte conteúdo:
```bash
DB_URL=jdbc:mysql://localhost:3306/locadora
DB_USERNAME=seu_usuario
DB_PASSWORD=sua_senha
TMDB_TOKEN_V4=seu_token_v4_da_api_tmdb
````

> Ou use as variáveis de ambiente da sua máquina para vincular a senha, usuário e token.

4. Build e execute o projeto:

```bash
mvn clean install
mvn spring-boot:run
````


5. Acesse no navegador com o usuário e senha da classe SecurityConfig do pacote config:
```bash
http://localhost:8081/login
````



## Endpoints Principais

### Página inicial
- **GET /menu**  
  Página de menu, redireciona para outras abas.

### Filmes
- **GET /filmes/listar**  
  Lista todos os filmes.

- **GET /filmes/form**  
  Cadastra novo filme a partir de um ID da TMDB.

- **GET /filmes/buscar**
Exibe formulário com dados aleatórios do endpoint /discover/movie

- **POST /filmes/salvar**  
  Salva novo filme.

- **GET /filmes/editar/{id}**  
  Edita um filme (modificar status e exemplares disponíveis).

- **POST /filmes/editar/{id}**  
  Salva alterações.


### Locações

- **GET /locacoes/novo**  
  Formulário de nova locação.

- **POST /locacoes/salvar**  
  Salva nova locação.

- **GET /locacoes/listar**  
  Lista locações com filtros.

- **GET /locacoes/{id}/qrcode**  
  Detalha uma locação com QR Code.

- **POST /locacoes/{id}/devolver**  
  Realiza devolução de locação.


  ### Locações Públicas

- **GET /publico/consulta**  
  Exibe as locações pendentes.

- **GET /publico/form**  
  Exibe tela para usuário digitar o CPF.

 ### Exemplares

- **GET /exemplares/novo**  
  Exibe o formulário para cadastrar novos exemplares.

- **POST /exemplares/salvar**  
  Salva os exemplares no banco após o formulário ser submetido.

- **GET /exemplares/editar/{id}**  
  Exibe o formulário de edição de um exemplar específico.

- **POST /exemplares/atualizar/{id}**  
  Atualiza o status de um exemplar (ativo/inativo).

- **GET /exemplares/listar**  
  Lista todos os exemplares.

- **GET /exemplares/excluir/{id}**  
  Exclui um exemplar pelo ID (validação aplicada antes da exclusão).






