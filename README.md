# PsiCare

Sistema web de gestão para clínicas de psicologia, desenvolvido com **Java 21**, **Spring Boot** e **PostgreSQL**. Controla pacientes, profissionais (psicólogos e estagiários), salas e sessões de atendimento, com regras de negócio automáticas e um dashboard de indicadores.

Projeto criado para a disciplina de Tecnologias Web e mantido como projeto pessoal, com foco em uma modelagem inspirada no funcionamento de uma clínica-escola.

**Demo:** https://psicare-0yg6.onrender.com

> A demo roda no plano gratuito do Render. Se ficar sem acessos, o serviço "dorme" e o primeiro carregamento pode levar de 1 a 2 minutos. Os dados exibidos são fictícios.

<!--
Adicione aqui capturas de tela, por exemplo:
![Tela inicial](docs/img/home.png)
![Dashboard](docs/img/dashboard.png)
-->

## Funcionalidades

- **Cadastros (CRUD)** de pacientes, profissionais e salas.
- **Agendamento de sessões** vinculadas a paciente, profissional e sala.
- **Idade calculada automaticamente** pela data de nascimento; para menores de 18 anos, o responsável é obrigatório.
- **Validação de CPF duplicado** com mensagem amigável.
- **Valor da sessão preenchido automaticamente** a partir do valor do profissional (psicólogo ou estagiário).
- **Bloqueio de conflito de horário**: impede duas sessões sobrepostas para o mesmo profissional ou para a mesma sala. Sessões canceladas liberam o horário.
- **Status automático**: sessões agendadas com horário vencido aparecem como *Pendente*.
- **Dashboard** com sessões do mês, taxa de faltas, faturamento, valor em aberto, gráfico de sessões por profissional e repasse por profissional.
- **Alerta de abandono**: lista pacientes ativos sem sessão nos últimos 30 dias.

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 4.1.2 (Spring Web, Spring Data JPA, Validation) |
| Persistência | Hibernate, PostgreSQL |
| Front-end | Thymeleaf, Bootstrap 5 (CSS), JavaScript, Chart.js |
| Produtividade | Lombok, Spring Boot DevTools |
| Build | Maven |
| Deploy | Docker, Render (serviço web e banco PostgreSQL) |

## Arquitetura

O projeto segue uma arquitetura em camadas:

```
Controller  ->  Service  ->  Repository  ->  PostgreSQL
    |
Templates Thymeleaf
```

```
src/main/java/com/tecweb/psicare
├── model/        Entidades JPA e enums (Paciente, Profissional, Sala, Sessao)
├── repository/   Interfaces Spring Data JPA, incluindo consultas JPQL do dashboard
├── service/      Regras de negócio (SessaoService, DashboardService)
└── controller/   Rotas e telas (Home, Paciente, Profissional, Sala, Sessao, Dashboard)

src/main/resources
├── templates/    Páginas Thymeleaf (com fragmento de navbar reutilizado)
└── application.properties
```

## Modelo de dados

```
paciente 1 ----< N sessao N >---- 1 profissional
                     N
                     |
                     1
                   sala
```

| Tabela | Campos principais |
|---|---|
| `paciente` | id, nome, cpf (único), telefone, data_nascimento, responsavel, ativo |
| `profissional` | id, nome, crp, tipo (PSICOLOGO/ESTAGIARIO), valor_sessao |
| `sala` | id, nome, capacidade |
| `sessao` | id, data_hora, duracao_min, status, valor, pago, observacao, paciente_id, profissional_id, sala_id |

As tabelas são geradas automaticamente pelo Hibernate (`spring.jpa.hibernate.ddl-auto=update`).

## Como executar localmente

### Pré-requisitos

- JDK 21
- PostgreSQL em execução
- Maven (ou use o wrapper `./mvnw` incluído)

### Passos

1. Clone o repositório:

   ```bash
   git clone https://github.com/mikaelrms/PsiCare-.git
   cd PsiCare-
   ```

2. Crie o banco de dados:

   ```sql
   CREATE DATABASE psicare;
   ```

3. Crie o arquivo `src/main/resources/application-local.properties` (ele não é versionado) com os dados do seu banco:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/psicare
   spring.datasource.username=postgres
   spring.datasource.password=SUA_SENHA

   spring.jpa.show-sql=true
   spring.thymeleaf.cache=false
   ```

4. Execute a aplicação:

   ```bash
   ./mvnw spring-boot:run
   ```

5. Acesse http://localhost:8080

Os scripts de apoio ficam na pasta `sql/`, incluindo dados de exemplo para testes.

## Configuração por variáveis de ambiente

Em produção, a conexão com o banco é feita por variáveis de ambiente, sem credenciais no código:

| Variável | Descrição |
|---|---|
| `DB_URL` | URL JDBC, por exemplo `jdbc:postgresql://host:5432/banco` |
| `DB_USER` | Usuário do banco |
| `DB_PASSWORD` | Senha do banco |
| `PORT` | Porta HTTP (padrão 8080) |
| `TZ` | Fuso horário, por exemplo `America/Sao_Paulo` |

## Deploy

O projeto inclui um `Dockerfile` com build em duas etapas (Maven para compilar e uma imagem JRE leve para executar). No Render, basta apontar o serviço web para este repositório, escolher o ambiente Docker e definir as variáveis acima.

## Roteiro de evolução

- [ ] Autenticação e perfis de acesso (estagiário, supervisor, administrador) com Spring Security
- [ ] Tratamento amigável ao excluir registros que possuem sessões (ou inativação em vez de exclusão)
- [ ] Prontuário e controle de pagamentos em tabelas próprias
- [ ] Filtros e busca nas listas (paciente, profissional, período)
- [ ] Agenda semanal visual por profissional e por sala
- [ ] Testes automatizados (unitários e de integração)
- [ ] Migrações de banco com Flyway
- [ ] Exportação de relatórios em PDF

## Aviso sobre dados sensíveis

Informações de saúde mental são dados sensíveis e protegidos por sigilo profissional e pela LGPD. Este projeto é acadêmico e usa apenas dados fictícios. Para uso real, é necessário implementar autenticação, controle de acesso, criptografia e demais medidas de conformidade.

**Autor**: [@mikaelrms](https://github.com/mikaelrms)
