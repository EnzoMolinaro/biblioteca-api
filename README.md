# 📚 Sistema de Gestão de Biblioteca - API REST

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen?style=flat-square&logo=spring)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat-square&logo=mysql)
![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)

API REST completa para gerenciamento de bibliotecas desenvolvida com **Spring Boot 3**, **Java 17** e **MySQL**.

## 🎯 Funcionalidades

- ✅ **CRUD Completo** de livros, usuários e empréstimos
- ✅ **Sistema de Categorias** para organização
- ✅ **Controle de Disponibilidade** em tempo real
- ✅ **Cálculo Automático de Multas** por atraso
- ✅ **Tipos de Usuários** (Estudante, Professor, Funcionário, Externo)
- ✅ **Soft Delete** (exclusão lógica preservando histórico)
- ✅ **Paginação e Ordenação** em todas as listagens
- ✅ **Filtros e Buscas** avançadas
- ✅ **Relatórios Estatísticos** (livros mais emprestados, usuários ativos, etc.)
- ✅ **Validações de Regras de Negócio** (limite de empréstimos, disponibilidade)
- ✅ **Documentação Automática** com Swagger/OpenAPI

## 🚀 Tecnologias

### Backend
- **Java 17** - Linguagem de programação
- **Spring Boot 3.2.0** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **Hibernate** - ORM (Object-Relational Mapping)
- **MySQL 8.0** - Banco de dados relacional

### Ferramentas
- **Flyway** - Migrations e versionamento do banco
- **MapStruct** - Mapeamento automático DTO ↔ Entity
- **Lombok** - Redução de código boilerplate
- **Bean Validation** - Validações declarativas
- **Swagger/OpenAPI** - Documentação interativa da API

## 🏗️ Arquitetura
```
┌─────────────┐
│  Controller │ ← HTTP Requests/Responses
└──────┬──────┘
       │
┌──────▼──────┐
│   Service   │ ← Lógica de Negócio
└──────┬──────┘
       │
┌──────▼──────┐
│ Repository  │ ← Acesso a Dados (JPA)
└──────┬──────┘
       │
┌──────▼──────┐
│   Entity    │ ← Entidades do Banco
└─────────────┘

    DTOs ←→ Mappers ←→ Entities
```

### Camadas

- **Controller**: Recebe requisições HTTP e retorna respostas JSON
- **Service**: Aplica regras de negócio e coordena operações
- **Repository**: Interface com Spring Data JPA para acesso ao banco
- **Entity**: Entidades JPA mapeadas para tabelas do banco
- **DTO**: Data Transfer Objects para comunicação com cliente
- **Mapper**: Conversão automática entre DTOs e Entities (MapStruct)

## 📊 Modelo de Dados
```
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│  Categoria  │       │    Livro    │       │   Usuario   │
├─────────────┤       ├─────────────┤       ├─────────────┤
│ id (PK)     │───┐   │ id (PK)     │   ┌───│ id (PK)     │
│ nome        │   └──<│ categoria_id│   │   │ nome        │
│ descricao   │       │ isbn (UK)   │   │   │ email (UK)  │
└─────────────┘       │ titulo      │   │   │ cpf (UK)    │
                      │ autor       │   │   │ tipo (ENUM) │
                      │ editora     │   │   │ ativo       │
                      │ ano_public. │   │   └─────────────┘
                      │ qtd_total   │   │
                      │ qtd_disp.   │   │
                      │ valor_multa │   │
                      │ ativo       │   │
                      └─────────────┘   │
                             │          │
                             ▼          ▼
                      ┌─────────────────────┐
                      │    Emprestimo       │
                      ├─────────────────────┤
                      │ id (PK)             │
                      │ livro_id (FK)       │
                      │ usuario_id (FK)     │
                      │ data_emprestimo     │
                      │ data_prev_devolucao │
                      │ data_devolucao      │
                      │ status (ENUM)       │
                      │ valor_multa         │
                      └─────────────────────┘
```

## 🔧 Instalação e Configuração

### Pré-requisitos

- **Java JDK 17** ou superior
- **Maven 3.6+**
- **MySQL 8.0+** ou XAMPP

### Passos de Instalação

1. **Clone o repositório**
```bash
git clone https://github.com/EnzoMolinaro/biblioteca-api.git
cd biblioteca-api
```

2. **Configure o banco de dados**

Crie o banco de dados no MySQL:
```sql
CREATE DATABASE biblioteca_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
```

3. **Configure as credenciais**

Edite `src/main/resources/application-dev.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/biblioteca_db
    username: root
    password: sua_senha_aqui
```

> **Nota:** Se usar XAMPP, a porta pode ser `3307`

4. **Execute a aplicação**
```bash
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

5. **Acesse a documentação**
```
http://localhost:8080/swagger-ui.html
```

## 📡 Endpoints da API

### 📚 Livros

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/livros` | Listar todos os livros (paginado) |
| GET | `/api/livros/{id}` | Buscar livro por ID |
| GET | `/api/livros/buscar?termo={termo}` | Buscar por título, autor ou ISBN |
| GET | `/api/livros/autor?autor={nome}` | Buscar por autor |
| GET | `/api/livros/categoria/{id}` | Listar por categoria |
| GET | `/api/livros/disponiveis` | Listar apenas disponíveis |
| POST | `/api/livros` | Cadastrar novo livro |
| PUT | `/api/livros/{id}` | Atualizar livro |
| DELETE | `/api/livros/{id}` | Deletar livro (soft delete) |

### 👥 Usuários

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/usuarios` | Listar todos os usuários |
| GET | `/api/usuarios/{id}` | Buscar usuário por ID |
| GET | `/api/usuarios/buscar?termo={termo}` | Buscar por nome, email ou CPF |
| GET | `/api/usuarios/tipo/{tipo}` | Listar por tipo |
| POST | `/api/usuarios` | Cadastrar novo usuário |
| PUT | `/api/usuarios/{id}` | Atualizar usuário |
| DELETE | `/api/usuarios/{id}` | Deletar usuário |

### 📖 Empréstimos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/emprestimos` | Listar todos os empréstimos |
| GET | `/api/emprestimos/{id}` | Buscar empréstimo por ID |
| GET | `/api/emprestimos/usuario/{id}` | Listar por usuário |
| GET | `/api/emprestimos/atrasados` | Listar atrasados |
| POST | `/api/emprestimos` | Criar novo empréstimo |
| PATCH | `/api/emprestimos/{id}/devolver` | Devolver livro |
| PATCH | `/api/emprestimos/{id}/renovar?dias={n}` | Renovar empréstimo |

### 📊 Relatórios

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/relatorios/geral` | Estatísticas gerais do sistema |
| GET | `/api/relatorios/livros-mais-emprestados` | Top 10 livros |
| GET | `/api/relatorios/usuarios-mais-ativos` | Usuários com mais empréstimos |
| GET | `/api/relatorios/livros-por-categoria` | Distribuição por categoria |
| GET | `/api/relatorios/periodo?dataInicio={data}&dataFim={data}` | Empréstimos em período |

## 📝 Exemplos de Uso

### Criar um Livro
```bash
curl -X POST http://localhost:8080/api/livros \
  -H "Content-Type: application/json" \
  -d '{
    "isbn": "9788544001639",
    "titulo": "O Senhor dos Anéis",
    "autor": "J.R.R. Tolkien",
    "editora": "HarperCollins",
    "anoPublicacao": 2019,
    "quantidadeTotal": 7,
    "valorMultaDiaria": 3.50,
    "categoriaId": 1
  }'
```

### Criar um Empréstimo
```bash
curl -X POST http://localhost:8080/api/emprestimos \
  -H "Content-Type: application/json" \
  -d '{
    "livroId": 1,
    "usuarioId": 1,
    "observacoes": "Primeiro empréstimo"
  }'
```

### Devolver um Livro
```bash
curl -X PATCH http://localhost:8080/api/emprestimos/1/devolver
```

## 🧪 Executando Testes
```bash
mvn test
```

## 📦 Build para Produção
```bash
mvn clean package
```

O arquivo `.jar` estará em `target/biblioteca-api-1.0.0.jar`

Para executar:
```bash
java -jar target/biblioteca-api-1.0.0.jar
```

## 🐳 Docker (Futuro)
```bash
docker-compose up -d
```

## 🚀 Deploy

### Railway (Recomendado)

1. Crie conta em https://railway.app
2. Conecte seu repositório GitHub
3. Adicione MySQL database
4. Configure variáveis de ambiente
5. Deploy automático!

### Heroku
```bash
heroku create biblioteca-api-seu-nome
heroku addons:create cleardb:ignite
git push heroku main
```

## 📖 Documentação

Toda a documentação da API está disponível via Swagger:
```
http://localhost:8080/swagger-ui.html
```

Você pode testar todos os endpoints diretamente pela interface!

## 🎯 Roadmap

- [ ] Autenticação e Autorização (JWT)
- [ ] Testes de Integração
- [ ] Docker Compose
- [ ] CI/CD (GitHub Actions)
- [ ] Notificações por Email
- [ ] Upload de Capas de Livros
- [ ] Sistema de Reservas
- [ ] Frontend (React)
- [ ] Aplicativo Mobile

## 🤝 Contribuindo

Contribuições são bem-vindas! Por favor:

1. Faça um Fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 👤 Autor

**Enzo Molinaro**

- GitHub: [@EnzoMolinaro](https://github.com/EnzoMolinaro)
- LinkedIn: [Enzo Molinaro Magalhães](https://www.linkedin.com/in/enzomolinaromagalhaes/)
- Email: emolinaromagalhaes@gmail.com

## 🙏 Agradecimentos

- Spring Boot Community
- Baeldung Tutorials
- Stack Overflow

---

⭐ Se este projeto te ajudou, considere dar uma estrela!

📄 Licença  
Este projeto está sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.

Copyright (c) 2026 Enzo Molinaro
