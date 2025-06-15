<!-- English Version -->

# DevUtilities

> 📘 This section is in English. For the Portuguese version, scroll down to the section marked "Versão em Português".

A lightweight and modular set of utilities built with **Java**, designed to automate simple development tasks such as logging activities, simulating GitHub actions, and appending timestamped notes. Ideal for developers looking to improve productivity through small CLI and GUI tools.

## 🚀 Features

* **Activity Simulator (GUI)** – Simulates and logs development or productivity activity, and interacts with GitHub (issues, PRs, releases, etc.).
* **Note Processor** – Adds timestamped notes to a log file (see future utilities).
* **Multilingual Support** – Interface and logs available in English and Portuguese (Brazil).
* **GitHub Integration** – Uses the [github-api](https://github.com/hub4j/github-api) library for real GitHub automation.
* **Customizable UI** – Modern Swing interface with icons and color palette.
* **Unit Test Structure** – Example test class included for future expansion.

## 📦 Dependencies

* Java 8+
* [github-api-1.323.jar](lib/github-api-1.323.jar)
* [commons-io-2.11.0.jar](lib/commons-io-2.11.0.jar)
* [commons-lang3-3.14.0.jar](lib/commons-lang3-3.14.0.jar)
* [jackson-annotations-2.15.2.jar](lib/jackson-annotations-2.15.2.jar)
* [jackson-core-2.15.2.jar](lib/jackson-core-2.15.2.jar)
* [jackson-databind-2.15.2.jar](lib/jackson-databind-2.15.2.jar)

## 🌐 Multilingual Resources

* [src/main/resources/messages_en_US.properties](src/main/resources/messages_en_US.properties)
* [src/main/resources/messages_pt_BR.properties](src/main/resources/messages_pt_BR.properties)
* Icons in [src/main/resources/icons/](src/main/resources/icons/)

## ⚙️ Configuration

Edit your GitHub credentials and repository in [config/github.properties](config/github.properties):

```properties
github.token=YOUR_PERSONAL_ACCESS_TOKEN
github.username=your-github-username
github.repository=your-repository-name
```

Other application settings can be placed in [config/app.properties](config/app.properties).

## 🧪 Testing

A sample test class is provided at [src/test/java/com/devutilities/automation/ActivitySimulatorTest.java](src/test/java/com/devutilities/automation/ActivitySimulatorTest.java).

## 📁 Project Structure

```
DevUtilities/
├── src/
│   ├── main/
│   │   ├── java/com/devutilities/automation/ActivitySimulator.java
│   │   └── resources/
│   │       ├── messages_en_US.properties
│   │       ├── messages_pt_BR.properties
│   │       └── icons/
│   └── test/
│       └── java/com/devutilities/automation/ActivitySimulatorTest.java
├── bin/ (compiled output)
├── lib/ (JAR dependencies)
├── config/
│   ├── app.properties
│   └── github.properties
├── docs/
│   └── setup.md
├── .vscode/
│   └── settings.json
├── .gitignore
├── README.md
└── LICENSE
```

## 🚧 How to Use Locally

1. **Clone the repository:**
   ```bash
   git clone https://github.com/boltreskh/DevUtilities.git
   cd DevUtilities
   ```

2. **Configure GitHub credentials:**
   Edit `config/github.properties` as described above.

3. **Compile the project:**
   ```bash
   javac -cp "lib/*" -d bin src/main/java/com/devutilities/automation/ActivitySimulator.java
   ```

4. **Run the Activity Simulator:**
   ```bash
   java -cp "bin:lib/*:src/main/resources" main.java.com.devutilities.automation.ActivitySimulator
   ```

   > **Note:** The application uses Swing for the GUI and requires access to the `src/main/resources` folder for language files and icons.

5. **Switch Language:**
   Use the language selector in the GUI to switch between English and Portuguese.

## 📅 Sample Use Cases

* Daily productivity logs
* Simulate GitHub issues, PRs, releases, and comments
* CLI and GUI automation examples

---

💡 Crafted with care by Lucas Candido Luiz – [@boltreskh](https://github.com/boltreskh)

---

<!-- Versão em Português -->

# DevUtilities

> 📘 Esta seção está em português. Para a versão em inglês, volte ao topo ou procure pela seção "English Version".

Um conjunto leve e modular de utilitários escritos em **Java**, criado para ajudar na automação de tarefas simples de desenvolvimento, como registro de atividades, simulação de ações no GitHub e anotações com data/hora. Ideal para desenvolvedores que desejam aumentar a produtividade com ferramentas de linha de comando e interface gráfica.

## 🚀 Funcionalidades

* **Simulador de Atividades (GUI)** – Simula e registra atividades de produtividade, além de interagir com o GitHub (issues, PRs, releases, etc.).
* **Processador de Notas** – Adiciona anotações com data/hora a um arquivo de log (veja utilitários futuros).
* **Suporte Multilíngue** – Interface e logs disponíveis em inglês e português (Brasil).
* **Integração com GitHub** – Utiliza a biblioteca [github-api](https://github.com/hub4j/github-api) para automação real no GitHub.
* **UI Personalizável** – Interface Swing moderna com ícones e paleta de cores.
* **Estrutura de Testes** – Classe de teste exemplo incluída para expansão futura.

## 📦 Dependências

* Java 8+
* [github-api-1.323.jar](lib/github-api-1.323.jar)
* [commons-io-2.11.0.jar](lib/commons-io-2.11.0.jar)
* [commons-lang3-3.14.0.jar](lib/commons-lang3-3.14.0.jar)
* [jackson-annotations-2.15.2.jar](lib/jackson-annotations-2.15.2.jar)
* [jackson-core-2.15.2.jar](lib/jackson-core-2.15.2.jar)
* [jackson-databind-2.15.2.jar](lib/jackson-databind-2.15.2.jar)

## 🌐 Recursos Multilíngues

* [src/main/resources/messages_en_US.properties](src/main/resources/messages_en_US.properties)
* [src/main/resources/messages_pt_BR.properties](src/main/resources/messages_pt_BR.properties)
* Ícones em [src/main/resources/icons/](src/main/resources/icons/)

## ⚙️ Configuração

Edite suas credenciais e repositório do GitHub em [config/github.properties](config/github.properties):

```properties
github.token=SEU_TOKEN_PESSOAL
github.username=seu-usuario-github
github.repository=nome-do-repositorio
```

Outras configurações podem ser feitas em [config/app.properties](config/app.properties).

## 🧪 Testes

Uma classe de teste exemplo está disponível em [src/test/java/com/devutilities/automation/ActivitySimulatorTest.java](src/test/java/com/devutilities/automation/ActivitySimulatorTest.java).

## 📁 Estrutura do Projeto

```
DevUtilities/
├── src/
│   ├── main/
│   │   ├── java/com/devutilities/automation/ActivitySimulator.java
│   │   └── resources/
│   │       ├── messages_en_US.properties
│   │       ├── messages_pt_BR.properties
│   │       └── icons/
│   └── test/
│       └── java/com/devutilities/automation/ActivitySimulatorTest.java
├── bin/ (arquivos compilados)
├── lib/ (dependências JAR)
├── config/
│   ├── app.properties
│   └── github.properties
├── docs/
│   └── setup.md
├── .vscode/
│   └── settings.json
├── .gitignore
├── README.md
└── LICENSE
```

## 🚧 Como Usar Localmente

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/boltreskh/DevUtilities.git
   cd DevUtilities
   ```

2. **Configure as credenciais do GitHub:**
   Edite `config/github.properties` conforme descrito acima.

3. **Compile o projeto:**
   ```bash
   javac -cp "lib/*" -d bin src/main/java/com/devutilities/automation/ActivitySimulator.java
   ```

4. **Execute o Simulador de Atividades:**
   ```bash
   java -cp "bin:lib/*:src/main/resources" main.java.com.devutilities.automation.ActivitySimulator
   ```

   > **Nota:** A aplicação utiliza Swing para a interface gráfica e requer acesso à pasta `src/main/resources` para arquivos de idioma e ícones.

5. **Troque o idioma:**
   Use o seletor de idioma na interface para alternar entre inglês e português.

## 📅 Casos de Uso Exemplares

* Registro de produtividade diária
* Simulação de issues, PRs, releases e comentários no GitHub
* Exemplos de automação via CLI e GUI

---

💡 Desenvolvido com carinho por Lucas Candido Luiz