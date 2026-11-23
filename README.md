# Modelagem de Entidade com JPA e Hibernate

Projeto Java que mapeia a entidade **Produto** para um banco **PostgreSQL** usando **JPA (Jakarta Persistence)** com **Hibernate**. Inclui um DAO com as operações de CRUD e testes automatizados com **JUnit 5**.

Desenvolvido como exercício do Módulo 32 (Modelagem de Entidade) do curso de Back-end da EBAC.

---

## Sumário

- [Tecnologias](#tecnologias)
- [Modelo de dados](#modelo-de-dados)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Pré-requisitos](#pré-requisitos)
- [Configuração do banco](#configuração-do-banco)
- [Executando os testes](#executando-os-testes)
- [Operações do DAO](#operações-do-dao)
- [Dados de exemplo](#dados-de-exemplo)

---

## Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21+ | Linguagem |
| Hibernate ORM | 6.6.53.Final | Implementação do JPA |
| Jakarta Persistence (JPA) | 3.1 | Mapeamento objeto-relacional |
| PostgreSQL | 18 | Banco de dados |
| PostgreSQL JDBC Driver | 42.7.13 | Conexão com o banco |
| JUnit Jupiter | 5.12.2 | Testes automatizados |
| Maven (Wrapper) | 3.9.7 | Build e dependências |

---

## Modelo de dados

A classe `Produto` é mapeada para a tabela `produto`:

| Atributo Java | Coluna | Tipo Java | Tipo no PostgreSQL | Regra |
|---|---|---|---|---|
| `id` | `Prod_ID` | `Long` | `bigint` identity | Chave primária com auto-incremento |
| `nome` | `Prod_Nome` | `String` | `varchar(100)` | Obrigatório |
| `descricao` | `Prod_Descricao` | `String` | `varchar(255)` | Opcional |
| `preco` | `Prod_Preco` | `Double` | `double precision` | Obrigatório |
| `estoque` | `Prod_Estoque` | `Integer` | `integer` | Obrigatório |

O Hibernate cria e atualiza a tabela automaticamente (`hibernate.hbm2ddl.auto=update`). Não é preciso rodar nenhum script SQL.

> O PostgreSQL converte nomes de colunas sem aspas para minúsculas. No banco, as colunas aparecem como `prod_id`, `prod_nome` e assim por diante.

---

## Estrutura do projeto

```
Mod_32_modelagem_entidade
├── pom.xml
├── mvnw / mvnw.cmd                         # Maven Wrapper (dispensa instalar o Maven)
└── src
    ├── main
    │   ├── java/br/com/ebac
    │   │   ├── domain
    │   │   │   └── Produto.java            # Entidade JPA
    │   │   └── dao
    │   │       ├── IProdutoDao.java        # Contrato do DAO
    │   │       ├── ProdutoDao.java         # Implementação do CRUD
    │   │       └── JPAUtil.java            # Criação da EntityManagerFactory
    │   └── resources/META-INF
    │       └── persistence.xml             # Unidade de persistência "ExemploJPA"
    └── test
        └── java/br/com/ebac/dao
            ├── ProdutoDaoTest.java         # Testes do CRUD
            └── CargaEletronicosTest.java   # Carga de 5 produtos eletrônicos
```

---

## Pré-requisitos

- **JDK 21** ou superior
- **PostgreSQL** instalado e em execução na porta `5432`

O Maven não precisa estar instalado: o projeto usa o Maven Wrapper (`mvnw`).

---

## Configuração do banco

### 1. Criar o banco de dados

```bash
psql -U postgres -c "CREATE DATABASE ebac;"
```

### 2. Informar as credenciais

A conexão padrão fica em `src/main/resources/META-INF/persistence.xml`:

| Propriedade | Valor padrão |
|---|---|
| URL | `jdbc:postgresql://localhost:5432/ebac` |
| Usuário | `postgres` |
| Senha | `postgres` |

Para não gravar a senha real no código, a classe `JPAUtil` permite sobrescrever esses valores com variáveis de ambiente:

| Variável | Sobrescreve |
|---|---|
| `DB_URL` | URL JDBC |
| `DB_USER` | Usuário |
| `DB_PASSWORD` | Senha |

**Windows (PowerShell), só para a sessão atual:**
```powershell
$env:DB_PASSWORD = "sua_senha"
```

**Windows, permanente (reinicie a IDE depois):**
```powershell
[Environment]::SetEnvironmentVariable("DB_PASSWORD", "sua_senha", "User")
```

**Linux / macOS:**
```bash
export DB_PASSWORD="sua_senha"
```

---

## Executando os testes

```bash
# Windows
mvnw.cmd test

# Linux / macOS / Git Bash
./mvnw test
```

Resultado esperado:

```
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### O que é testado

**`ProdutoDaoTest`** valida o CRUD completo:

| Teste | Verifica |
|---|---|
| `deveCadastrarProdutoComIdGerado` | O ID é gerado pelo banco ao salvar |
| `deveBuscarProdutoPorId` | Todos os campos são recuperados corretamente |
| `deveAlterarProduto` | Nome, preço e estoque são atualizados |
| `deveExcluirProduto` | O produto deixa de existir após a exclusão |
| `deveBuscarTodosOsProdutos` | A listagem retorna os produtos cadastrados |

Cada teste remove **apenas os registros que ele mesmo criou**, então os dados que já existem no banco são preservados.

**`CargaEletronicosTest`** cadastra 5 produtos eletrônicos e os mantém no banco. A carga não duplica: produtos já cadastrados com o mesmo nome são ignorados.

---

## Operações do DAO

Interface `IProdutoDao`:

| Método | Descrição |
|---|---|
| `Produto cadastrar(Produto produto)` | Insere um novo produto |
| `Produto buscarPorId(Long id)` | Busca pelo ID; retorna `null` se não existir |
| `Optional<Produto> buscarPorNome(String nome)` | Busca pelo nome exato |
| `List<Produto> buscarTodos()` | Lista todos os produtos ordenados por ID |
| `Produto alterar(Produto produto)` | Atualiza um produto existente |
| `void excluir(Produto produto)` | Remove o produto |

As operações de escrita rodam dentro de uma transação. Em caso de erro, a transação é desfeita (rollback).

### Exemplo de uso

```java
IProdutoDao dao = new ProdutoDao();

Produto produto = dao.cadastrar(
        new Produto("Mouse Logitech", "Mouse sem fio", 129.90, 25));

produto.setEstoque(20);
dao.alterar(produto);

dao.buscarTodos().forEach(System.out::println);
```

---

## Dados de exemplo

Depois de rodar os testes, a tabela `produto` contém:

| Prod_ID | Prod_Nome | Prod_Descricao | Prod_Preco | Prod_Estoque |
|---|---|---|---|---|
| 1 | Smartphone Galaxy S24 | Smartphone 256GB, tela 6.2", 8GB RAM | 4299.00 | 15 |
| 2 | Notebook Dell Inspiron 15 | Intel Core i7, 16GB RAM, SSD 512GB | 5499.90 | 8 |
| 3 | Smart TV LG 55" 4K | Smart TV LED 55 polegadas, UHD 4K, webOS | 2899.00 | 12 |
| 4 | Fone JBL Tune 520BT | Fone de ouvido Bluetooth, até 57h de bateria | 249.90 | 40 |
| 5 | Console PlayStation 5 | Console PS5 Slim, 1TB, com controle DualSense | 3799.00 | 6 |

Para conferir direto no banco:

```bash
psql -U postgres -d ebac -c "SELECT * FROM produto ORDER BY prod_id;"
```
