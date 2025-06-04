package main.java.com.devutilities.automation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Esta classe simula atividades de desenvolvimento gerando commits Git aleatórios
 * para preencher o gráfico de contribuições do GitHub.
 * Os commits são gerados dentro de um período de datas especificado, com um número aleatório
 * de commits por dia e mensagens de commit genéricas para manter a discrição.
 */
public class ActivitySimulator {

    // Caminho do repositório. '.' significa o diretório atual onde o programa está sendo executado.
    private static final String REPO_PATH = ".";
    // Nome do arquivo que será modificado para cada commit.
    // Este arquivo será o 'notes.txt' dentro da raiz do repositório.
    private static final String FILE_NAME = "notes.txt";
    // Mensagens de commit genéricas para evitar padrões óbvios.
    private static final String[] COMMIT_MESSAGES = {
            "Update", "Refactor", "Bug fix", "Performance improvement",
            "Feature addition", "Minor adjustments", "Code cleanup",
            "Documentation update", "Style corrections", "Optimization"
    };

    public static void main(String[] args) {
        // Define as datas de início e fim para a geração de commits.
        LocalDate startDate = LocalDate.of(2025, 6, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 4);

        // Inicializa o repositório Git se ele ainda não existir.
        initializeRepository();

        // Cria o arquivo dummy se ele não existir.
        Path filePath = Paths.get(REPO_PATH, FILE_NAME);
        if (!Files.exists(filePath)) {
            try {
                Files.createFile(filePath);
                System.out.println("File '" + FILE_NAME + "' created.");
            } catch (IOException e) {
                System.err.println("Error creating file: " + e.getMessage());
                return;
            }
        }

        Random random = new Random();
        LocalDate currentDate = startDate;

        // Loop através de cada dia no intervalo de datas.
        while (!currentDate.isAfter(endDate)) {
            // Gera um número aleatório de commits para o dia atual (entre 1 e 5).
            int commitsToday = random.nextInt(5) + 1;
            System.out.println("Processing " + currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + " with " + commitsToday + " commits.");

            // Loop para criar commits para o dia atual.
            for (int i = 0; i < commitsToday; i++) {
                try {
                    // Simula uma modificação no arquivo, adicionando uma nova linha.
                    appendToFile(filePath, "Entry for " + currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + " - Commit " + (i + 1) + "\n");

                    // Adiciona o arquivo ao staging area do Git.
                    executeGitCommand("add", FILE_NAME);

                    // Seleciona uma mensagem de commit aleatória.
                    String commitMessage = COMMIT_MESSAGES[random.nextInt(COMMIT_MESSAGES.length)];
                    // Gera um horário aleatório para o commit (hora, minuto, segundo).
                    String commitTime = String.format("%02d:%02d:%02d", random.nextInt(24), random.nextInt(60), random.nextInt(60));
                    // Executa o comando git commit com a data e hora especificadas.
                    executeGitCommand("commit", "-m", commitMessage, "--date", currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + " " + commitTime);

                    // Pequeno atraso para simular um trabalho mais realista e evitar picos de CPU.
                    TimeUnit.MILLISECONDS.sleep(random.nextInt(500) + 100);

                } catch (IOException | InterruptedException e) {
                    System.err.println("Error during commit process: " + e.getMessage());
                }
            }
            // Move para o próximo dia.
            currentDate = currentDate.plusDays(1);
        }

        System.out.println("\nCommit generation completed. Remember to push to GitHub: 'git push origin main'");
    }

    /**
     * Inicializa um novo repositório Git no diretório especificado se ele ainda não existir.
     * Também configura a branch principal para 'main'.
     */
    private static void initializeRepository() {
        File gitDir = new File(REPO_PATH, ".git");
        if (!gitDir.exists()) {
            System.out.println("Initializing new Git repository...");
            executeGitCommand("init");
            executeGitCommand("branch", "-M", "main"); // Define a branch principal como 'main'
            System.out.println("Repository initialized. Please configure your user.name and user.email for Git.");
            System.out.println("Example: git config user.name \"Your Name\"");
            System.out.println("Example: git config user.email \"your.email@example.com\"");
        } else {
            System.out.println("Git repository already exists.");
        }
    }

    /**
     * Adiciona conteúdo ao final do arquivo especificado.
     * @param filePath O caminho para o arquivo.
     * @param content O conteúdo a ser adicionado.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    private static void appendToFile(Path filePath, String content) throws IOException {
        try (FileWriter writer = new FileWriter(filePath.toFile(), true)) {
            writer.append(content);
        }
    }

    /**
     * Executa um comando Git no diretório do repositório.
     * @param commands Os argumentos do comando Git (ex: "add", "file.txt").
     */
    private static void executeGitCommand(String... commands) {
        try {
            // Constrói o comando completo, prefixando com "git".
            String[] fullCommand = new String[commands.length + 1];
            fullCommand[0] = "git";
            System.arraycopy(commands, 0, fullCommand, 1, commands.length);

            ProcessBuilder processBuilder = new ProcessBuilder(fullCommand);
            processBuilder.directory(new File(REPO_PATH)); // Define o diretório de trabalho para o repositório.
            processBuilder.redirectErrorStream(true); // Redireciona o stream de erro para o stream de saída.

            Process process = processBuilder.start();
            int exitCode = process.waitFor(); // Aguarda a conclusão do processo.

            // Lê a saída do comando Git.
            String output = new String(process.getInputStream().readAllBytes());
            if (!output.isEmpty()) {
                System.out.println("Git Output: " + output.trim());
            }

            // Verifica o código de saída para erros.
            if (exitCode != 0) {
                System.err.println("Git command failed with exit code " + exitCode + ": " + String.join(" ", fullCommand));
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error executing Git command: " + e.getMessage());
        }
    }
}
