<!-- English Version -->

# DevUtilities

> 📘 This section is in English. For the Portuguese version, scroll down to the section marked "Versão em Português".

A lightweight and modular set of utilities built with **Java**, created to help automate simple development tasks such as logging activities and appending timestamped notes. Ideal for developers looking to improve productivity through small CLI tools.

## 🚀 Features

* **Activity Manager** – Simulates and logs development or productivity activity.
* **Note Processor** – Adds timestamped notes to a log file.

## 📆 Technologies Used

* Java 8+
* Command-line execution (CLI)
* Java Standard Library (File I/O)

## 📁 Project Structure

```
DevUtilities/
├── .git/                     # Git repository files
├── src/                      # Source code
│   └── main/
│       └── java/
│           └── com/
│               └── devutilities/
│                   └── automation/
│                       └── ActivitySimulator.java
├── src/                      # Test source code
│   └── test/
│       └── java/
│           └── com/
│               └── devutilities/
│                   └── automation/
│                       └── ActivitySimulatorTest.java
├── docs/
│   └── setup.md
├── config/
│   └── app.properties
├── .gitignore
├── README.md
└── LICENSE
```

## 🚧 How to Use Locally

Compile and run the utilities from the command line:

```bash
git clone https://github.com/boltreskh/DevUtilities.git
cd DevUtilities
javac src/main/java/com/devutilities/automation/ActivitySimulator.java
java com.devutilities.automation.ActivitySimulator
```

Replace `ActivitySimulator` with `NoteProcessor` to run the note-logging tool.

## 📅 Sample Use Cases

* Daily productivity logs
* Simple journaling via terminal
* CLI automation examples

---

💡 Crafted with care by Lucas Candido Luiz – [@boltreskh](https://github.com/boltreskh)

---

<!-- Versão em Português -->

# DevUtilities

> 📘 Esta seção está em português. Para a versão em inglês, volte ao topo ou procure pela seção "English Version".

Um conjunto leve e modular de utilitários escritos em **Java**, criado para ajudar na automação de tarefas simples de desenvolvimento, como registro de atividades e anotações com carimbo de data/hora. Ideal para desenvolvedores que desejam aumentar a produtividade com pequenas ferramentas de linha de comando.

## 🚀 Funcionalidades

* **Gerenciador de Atividades** – Simula e registra atividades de produtividade.
* **Processador de Notas** – Adiciona anotações com data/hora a um arquivo de log.

## 📆 Tecnologias Utilizadas

* Java 8+
* Execução via linha de comando (CLI)
* Biblioteca padrão do Java (I/O de arquivos)

## 📁 Estrutura do Projeto

```
DevUtilities/
├── .git/                     # Arquivos do repositório Git
├── src/                      # Código-fonte
│   └── main/
│       └── java/
│           └── com/
│               └── devutilities/
│                   └── automation/
│                       └── ActivitySimulator.java
├── src/                      # Código de teste
│   └── test/
│       └── java/
│           └── com/
│               └── devutilities/
│                   └── automation/
│                       └── ActivitySimulatorTest.java
├── docs/
│   └── setup.md
├── config/
│   └── app.properties
├── .gitignore
├── README.md
└── LICENSE
```

## 🚧 Como Usar Localmente

Compile e execute os utilitários pela linha de comando:

```bash
git clone https://github.com/boltreskh/DevUtilities.git
cd DevUtilities
javac src/main/java/com/devutilities/automation/ActivitySimulator.java
java com.devutilities.automation.ActivitySimulator
```

Substitua `ActivitySimulator` por `NoteProcessor` para usar o utilitário de anotações.

## 📅 Casos de Uso Exemplares

* Registro de produtividade diária
* Journaling simples via terminal
* Exemplos de automação com CLI

---

💡 Desenvolvido com carinho por Lucas Candido Luiz – [@boltreskh](https://github.com/boltreskh)
