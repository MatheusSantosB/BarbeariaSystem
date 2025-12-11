# 💈 Barbearia Style - Sistema de Gerenciamento

Sistema desktop desenvolvido em Java para gerenciamento completo de uma barbearia. O projeto utiliza arquitetura MVC, banco de dados H2 e interface gráfica com JavaFX.

Desenvolvido como projeto final da disciplina de **Programação Orientada a Objetos (POO)** da **UFRN**.

---

## 👥 Autores
* **Matheus dos Santos**
* **Ludson Araújo**

---

## 🧩 Diagramas do Sistema

### 🔷 **Diagrama UML (Classes do Sistema)**  
![Diagrama UML](https://raw.githubusercontent.com/MatheusSantosB/BarbeariaSystem/8fc5e7fe0477dd37d6c6bdfe92b5855c5d1c5a51/diagrama_uml_classes.svg)

### 🔶 **Diagrama ER (Modelo Entidade-Relacionamento)**  
![Diagrama ER](https://github.com/MatheusSantosB/BarbeariaSystem/blob/main/barbearia_er_diagram.png?raw=true)

---
# 📚 Dicionário de Dados Completo

## 🟪 1. Entidade: **PESSOA**
| Campo | Tipo | Restrições | Descrição |
|------|------|-------------|-----------|
| id_pessoa | INT | PK, NOT NULL | Identificador único da pessoa |
| nome | VARCHAR(100) | NOT NULL | Nome completo |
| cpf | VARCHAR(11) | UNIQUE, NOT NULL | CPF sem máscara |
| telefone | VARCHAR(15) | NOT NULL | Telefone de contato |
| email | VARCHAR(100) | UNIQUE, NOT NULL | E-mail da pessoa |
| data_nascimento | DATE | — | Data de nascimento |
| endereco | VARCHAR(200) | — | Endereço completo |

---

## 🟩 2. Entidade: **CLIENTE**
| Campo | Tipo | Restrições | Descrição |
|------|------|-------------|-----------|
| id_cliente | INT | PK | Identificador do cliente |
| id_pessoa | INT | FK → PESSOA | Referência à pessoa |
| data_cadastro | DATETIME | NOT NULL | Data de cadastro |
| observacoes | TEXT | — | Notas sobre o cliente |
| ativo | BOOLEAN | DEFAULT TRUE | Indica se está ativo |

---

## 🟩 3. Entidade: **PROFISSIONAL**
| Campo | Tipo | Restrições | Descrição |
|------|------|-------------|-----------|
| id_profissional | INT | PK | Identificador do profissional |
| id_pessoa | INT | FK → PESSOA | Dados pessoais |
| especialidade | VARCHAR(100) | NOT NULL | Especialidade (corte, barba, etc.) |
| data_contratacao | DATE | NOT NULL | Data de contratação |
| comissao_percentual | DECIMAL(5,2) | NOT NULL | Percentual de comissão |
| ativo | BOOLEAN | DEFAULT TRUE | Indica se está ativo |

---

## 🟦 4. Entidade: **SERVICO**
| Campo | Tipo | Restrições | Descrição |
|------|------|-------------|-----------|
| id_servico | INT | PK | Identificador do serviço |
| nome | VARCHAR(100) | NOT NULL | Nome do serviço |
| descricao | TEXT | — | Detalhes |
| duracao_minutos | INT | NOT NULL | Duração do serviço |
| preco | DECIMAL(10,2) | NOT NULL | Preço base |
| ativo | BOOLEAN | DEFAULT TRUE | Disponível no sistema |

---

## 🟧 5. Entidade: **AGENDAMENTO**
| Campo | Tipo | Restrições | Descrição |
|------|------|-------------|-----------|
| id_agendamento | INT | PK | Identificador |
| id_cliente | INT | FK → CLIENTE | Cliente atendido |
| id_profissional | INT | FK → PROFISSIONAL | Profissional responsável |
| data_hora | DATETIME | NOT NULL | Data e hora |
| status | VARCHAR(20) | FK → STATUS_AGENDAMENTO | Status atual |
| observacoes | TEXT | — | Anotações |
| data_criacao | DATETIME | NOT NULL | Data de criação |
| data_atualizacao | DATETIME | NOT NULL | Última atualização |

---

## 🟥 6. Entidade: **AGENDAMENTO_SERVICO**
| Campo | Tipo | Restrições | Descrição |
|------|------|-------------|-----------|
| id_agendamento_servico | INT | PK | Identificador |
| id_agendamento | INT | FK → AGENDAMENTO | Agendamento |
| id_servico | INT | FK → SERVICO | Serviço selecionado |
| preco_praticado | DECIMAL(10,2) | NOT NULL | Preço no momento do agendamento |

---

## 🟪 7. Entidade: **FORMA_PAGAMENTO**
| Campo | Tipo | Restrições | Descrição |
|------|------|-------------|-----------|
| codigo | VARCHAR(20) | PK | Código da forma |
| descricao | VARCHAR(50) | NOT NULL | Nome da forma de pagamento |

---

## 🟦 8. Entidade: **STATUS_AGENDAMENTO**
| Campo | Tipo | Restrições | Descrição |
|------|------|-------------|-----------|
| codigo | VARCHAR(20) | PK | Código |
| descricao | VARCHAR(50) | NOT NULL | Significado do status |
| ordem | INT | — | Ordenação lógica |

---

## 🟨 9. Entidade: **STATUS_PAGAMENTO**
| Campo | Tipo | Restrições | Descrição |
|------|------|-------------|-----------|
| codigo | VARCHAR(20) | PK | Código |
| descricao | VARCHAR(50) | NOT NULL | Status financeiro |

---

## 🟩 10. Entidade: **PAGAMENTO**
| Campo | Tipo | Restrições | Descrição |
|------|------|-------------|-----------|
| id_pagamento | INT | PK | Identificador |
| id_agendamento | INT | FK, UNIQUE | Agendamento pago |
| valor_total | DECIMAL(10,2) | NOT NULL | Valor final |
| forma_pagamento | VARCHAR(20) | FK | Tipo de pagamento |
| status_pagamento | VARCHAR(20) | FK | Status atual |
| data_pagamento | DATETIME | — | Data do pagamento |
| observacoes | TEXT | — | Notas sobre o pagamento |

---
## 🚀 Funcionalidades

O sistema permite o controle total das operações da barbearia:

* **👥 Clientes:** Cadastro, edição, exclusão e busca de clientes.
* **✂️ Profissionais:** Gerenciamento da equipe, especialidades e status (ativo/inativo).
* **💈 Serviços:** Catálogo de serviços com preços e duração estimada.
* **📅 Agendamentos:**
    * Agendar horário vinculando cliente, profissional e serviços.
    * Cálculo automático do valor total e duração.
    * Controle de status (Agendado, Confirmado, Realizado, Cancelado).
* **📊 Relatórios:** Visualização básica de faturamento e estatísticas.

---

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** Java 17 (ou superior)
* **Interface:** JavaFX 21
* **Gerenciador de Dependências:** Maven
* **Banco de Dados:** H2 Database (Embutido/Local)
* **Arquitetura:** MVC (Model-View-Controller) com DAO (Data Access Object)

---

## 📋 Pré-requisitos

Para rodar o projeto, você precisa ter instalado na sua máquina:

1.  **Java JDK 17** ou superior.
2.  **Maven** (geralmente já vem embutido em IDEs como IntelliJ, mas pode ser instalado via terminal).
3.  **Git** (opcional, para clonar o repositório).

---

## ⚙️ Como Rodar o Projeto

### Opção 1: Via Linha de Comando (Terminal)

1.  **Clone o repositório (ou baixe e extraia o ZIP):**
    ```bash
    git clone [https://github.com/seu-usuario/BarbeariaSystem.git](https://github.com/seu-usuario/BarbeariaSystem.git)
    cd BarbeariaSystem
    ```

2.  **Limpe e Instale as dependências:**
    Este comando baixa as bibliotecas do JavaFX e compila o projeto.
    ```bash
    mvn clean install
    ```
    *Aguarde a mensagem "BUILD SUCCESS".*

3.  **Execute o Sistema:**
    ```bash
    mvn javafx:run
    ```

### Opção 2: Via IntelliJ IDEA (Recomendado)

1.  Abra a pasta do projeto no IntelliJ.
2.  Aguarde o Maven carregar as dependências (barrinha inferior direita).
3.  Vá na aba lateral direita **Maven**.
4.  Navegue em: `BarbeariaSystem` > `Lifecycle` > Clique duplo em **`install`**.
5.  Após finalizar, navegue em: `BarbeariaSystem` > `Plugins` > `javafx` > Clique duplo em **`javafx:run`**.

---

## 🗄️ Sobre o Banco de Dados

O sistema utiliza o **H2 Database**, um banco leve que roda localmente.
* O arquivo do banco será criado automaticamente na primeira execução na pasta: `./database/barbearia.mv.db`.
* Não é necessário instalar nenhum servidor SQL (MySQL/PostgreSQL), pois o H2 é embutido.

---

## 📝 Estrutura do Projeto

```text
src/main/java/com/barbearia
├── controller/       # Controladores das telas (Lógica da UI)
├── model/
│   ├── dao/          # Acesso ao Banco de Dados (SQL)
│   ├── entity/       # Classes de Domínio (Cliente, Pessoa, etc.)
│   └── service/      # Regras de Negócio e Validações
├── util/             # Utilitários (Datas, Logs, Inicialização de DB)
└── view/             # Arquivos visuais
    ├── fxml/         # Telas (.fxml)
    ├── css/          # Estilos (.css)
    └── images/       # Ícones e Logos
