# 💈 Barbearia Style - Sistema de Gerenciamento

Sistema desktop desenvolvido em Java para gerenciamento completo de uma barbearia. O projeto utiliza arquitetura MVC, banco de dados H2 e interface gráfica com JavaFX.

Desenvolvido como projeto final da disciplina de **Programação Orientada a Objetos (POO)** da **UFRN**.

---

## 👥 Autores
* **Matheus dos Santos**
* **Ludson Araújo**

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
