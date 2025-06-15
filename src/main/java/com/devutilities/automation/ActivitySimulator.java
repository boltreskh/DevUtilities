package main.java.com.devutilities.automation;

import org.kohsuke.github.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ActivitySimulator extends JFrame {

    private JTextPane logArea;
    private ResourceBundle messages;

    private final List<LogEntry> logHistory = new ArrayList<>();
    private record LogEntry(String key, LogLevel level, Object[] args) {}

    private JLabel logLabel;
    private JLabel languageLabel;
    private JButton btnCommits, btnIssue, btnPR, btnComment, btnMerge, btnApprove, btnRelease;
    private TitledBorder createSectionBorder, interactSectionBorder, manageSectionBorder;

    private static final Random RANDOM = new Random();
    private static Properties githubProps = new Properties();
    private static GitHub github;

    private static final String[] COMMIT_MESSAGES = {"Update", "Refactor", "Bug fix", "Doc update", "Style fix"};
    private static final String[] TASK_TITLES = {"update-api-docs", "fix-login-bug", "refactor-database-module", "add-user-auth"};
    private static final String[] ISSUE_COMMENTS = {"I'm looking into this.", "Good point! I'll add tests to cover this case.", "Investigating the root cause of the issue.", "Can anyone else reproduce this behavior?", "I think I have a fix for this, will open a PR soon."};
    private static final String[] MERGE_MESSAGES = {"Good work! Merging now.", "Merged. Thanks for the contribution!", "Feature integrated successfully."};
    private static final String[] APPROVE_COMMENTS = {"Looks good to me!", "Great work, approved.", "LGTM!"};
    private static final String[] RELEASE_NOTES = {"This version includes performance improvements and bug fixes.", "New API features and database query optimization.", "Maintenance release with security updates."};

    private static final Color COLOR_BACKGROUND = new Color(30, 31, 34);
    private static final Color COLOR_PANEL = new Color(43, 43, 43);
    private static final Color COLOR_TEXT_SUCCESS = new Color(0, 197, 105);
    private static final Color COLOR_TEXT_ERROR = new Color(255, 107, 107);
    private static final Color COLOR_TEXT_INFO = new Color(0, 179, 255);
    private static final Color COLOR_BORDER = new Color(60, 63, 65);
    private static final Color COLOR_ACCENT = new Color(13, 134, 255);

    enum LogLevel { INFO, SUCCESS, ERROR, GIT_CMD }

    public ActivitySimulator() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(650, 750));
        setLocationRelativeTo(null);
        
        loadLocale(Locale.of("pt", "BR"));

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(COLOR_BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(mainPanel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("DevUtilities", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(0, 10, 10, 0));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        JComboBox<String> langSelector = new JComboBox<>(new String[]{"Português (BR)", "English"});
        langSelector.setSelectedIndex(0);
        langSelector.addActionListener(e -> {
            int selectedIndex = langSelector.getSelectedIndex();
            Locale newLocale = (selectedIndex == 0) ? Locale.of("pt", "BR") : Locale.of("en", "US");
            loadLocale(newLocale);
            updateUIText();
            refreshLogArea();
        });
        JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        langPanel.setOpaque(false);
        languageLabel = new JLabel(); 
        languageLabel.setForeground(Color.WHITE);
        langPanel.add(languageLabel);
        langPanel.add(langSelector);
        topPanel.add(langPanel, BorderLayout.EAST);
        mainPanel.add(topPanel, gbc);

        gbc.gridy++;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JPanel actionsContainerPanel = new JPanel();
        actionsContainerPanel.setLayout(new BoxLayout(actionsContainerPanel, BoxLayout.Y_AXIS));
        actionsContainerPanel.setOpaque(false);
        
        btnCommits = createStyledButton("", "icons/commit.png");
        btnIssue = createStyledButton("", "icons/issue.png");
        btnPR = createStyledButton("", "icons/pr.png");
        actionsContainerPanel.add(createSectionPanel("section.create", btnCommits, btnIssue, btnPR));
        
        btnComment = createStyledButton("", "icons/comment.png");
        btnApprove = createStyledButton("", "icons/approve.png");
        btnMerge = createStyledButton("", "icons/merge.png");
        actionsContainerPanel.add(createSectionPanel("section.interact", btnComment, btnApprove, btnMerge));
        
        btnRelease = createStyledButton("", "icons/release.png");
        actionsContainerPanel.add(createSectionPanel("section.manage", btnRelease));
        mainPanel.add(actionsContainerPanel, gbc);

        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        JPanel logPanel = new JPanel(new BorderLayout(0, 5));
        logPanel.setOpaque(false);
        logLabel = new JLabel();
        logLabel.setForeground(Color.WHITE);
        logLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        logPanel.add(logLabel, BorderLayout.NORTH);

        logArea = new JTextPane();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        logArea.setBackground(new Color(30, 30, 30));
        logArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(new LineBorder(COLOR_BORDER));
        logPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(logPanel, gbc);
        
        updateUIText(); 

        btnCommits.addActionListener(e -> showCommitDateDialog());
        btnIssue.addActionListener(e -> runTask(this::simulateIssueWorkflow));
        btnPR.addActionListener(e -> runTask(this::simulatePullRequestWorkflow));
        btnComment.addActionListener(e -> runTask(this::simulateAddComment));
        btnMerge.addActionListener(e -> runTask(this::simulateMergePullRequest));
        btnApprove.addActionListener(e -> runTask(this::simulateApprovePullRequest));
        btnRelease.addActionListener(e -> runTask(this::simulateCreateRelease));

        runTask(() -> {
            try {
                loadGitHubProperties();
                initializeGitHubAPI();
                initializeRepository();
                createDummyFileIfNotExists();
            } catch (Exception ex) {
                log("fatal_error", LogLevel.ERROR, ex.getMessage());
            }
        });
    }

    private JPanel createSectionPanel(String titleKey, JComponent... components) {
        JPanel sectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        sectionPanel.setOpaque(false);
        
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_BORDER), messages.getString(titleKey));
        border.setTitleColor(Color.WHITE);
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        
        switch (titleKey) {
            case "section.create" -> createSectionBorder = border;
            case "section.interact" -> interactSectionBorder = border;
            case "section.manage" -> manageSectionBorder = border;
        }
        
        sectionPanel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(10, 0, 10, 0), border));
        
        for (JComponent comp : components) {
            sectionPanel.add(comp);
        }
        return sectionPanel;
    }

    private void loadLocale(Locale locale) {
        String resourcePath = "src/main/resources/messages_" + locale.toString() + ".properties";
        File resourceFile = new File(resourcePath);

        if (!resourceFile.exists()) {
             JOptionPane.showMessageDialog(this, "Ficheiro de idioma não encontrado: " + resourceFile.getAbsolutePath() + "\nVerifique se o ficheiro existe e se a aplicação está a ser executada a partir da raiz do projeto (a pasta DevUtilities).", "Erro Fatal de Configuração", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        try (InputStream input = new FileInputStream(resourceFile)) {
            messages = new PropertyResourceBundle(new InputStreamReader(input, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Não foi possível carregar o ficheiro de idioma: " + resourcePath, "Erro Fatal", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void updateUIText() {
        setTitle(messages.getString("app.title"));
        logLabel.setText(messages.getString("log.title"));
        
        setButtonText(btnCommits, messages.getString("button.generate_commits"));
        setButtonText(btnIssue, messages.getString("button.create_issue"));
        setButtonText(btnPR, messages.getString("button.create_pr"));
        setButtonText(btnComment, messages.getString("button.comment_issue"));
        setButtonText(btnMerge, messages.getString("button.merge_pr"));
        setButtonText(btnApprove, messages.getString("button.approve_pr"));
        setButtonText(btnRelease, messages.getString("button.create_release"));

        languageLabel.setText(messages.getString("lang.selector"));

        if (createSectionBorder != null) {
            createSectionBorder.setTitle(messages.getString("section.create"));
        }
        if (interactSectionBorder != null) {
            interactSectionBorder.setTitle(messages.getString("section.interact"));
        }
        if (manageSectionBorder != null) {
            manageSectionBorder.setTitle(messages.getString("section.manage"));
        }
        
        getContentPane().repaint();
    }
    
    private void setButtonText(JButton button, String text) {
        button.setText("<html><div style='text-align: center;'>" + text.replace(" ", "<br>") + "</div></html>");
    }
    
    private ImageIcon createIcon(String path, int width, int height) {
        String fullPath = "src/main/resources/" + path;
        File iconFile = new File(fullPath);
        
        if (!iconFile.exists()) {
            log("log.icon_not_found", LogLevel.ERROR, fullPath);
            return null;
        }

        try {
            ImageIcon icon = new ImageIcon(fullPath);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            log("log.icon_error_loading", LogLevel.ERROR, path);
            return null;
        }
    }
    
    private JButton createStyledButton(String text, String iconPath) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                ButtonModel model = getModel();
                
                Color color1, color2;
                if (!isEnabled()) {
                    color1 = COLOR_PANEL.darker();
                    color2 = COLOR_BACKGROUND;
                } else if (model.isPressed()) {
                    color1 = new Color(70, 75, 80);
                    color2 = new Color(50, 55, 60);
                } else if (model.isRollover()) {
                    color1 = COLOR_BORDER.brighter();
                    color2 = COLOR_PANEL.brighter();
                } else {
                    color1 = COLOR_PANEL;
                    color2 = COLOR_PANEL.darker();
                }

                g2.setPaint(new GradientPaint(0, 0, color1, 0, getHeight(), color2));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                if (model.isRollover() && isEnabled()) {
                    g2.setColor(COLOR_ACCENT);
                    g2.setStroke(new BasicStroke(2));
                } else {
                    g2.setColor(COLOR_BORDER);
                    g2.setStroke(new BasicStroke(1));
                }
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 18, 18);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        if (iconPath != null && !iconPath.isEmpty()) {
            button.setIcon(createIcon(iconPath, 32, 32));
        }

        button.setRolloverEnabled(true);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setPreferredSize(new Dimension(140, 85));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void runTask(Runnable task) {
        Thread thread = new Thread(() -> {
            try {
                task.run();
            } catch (Exception e) {
                log("task_exception", LogLevel.ERROR);
                log(e.getClass().getSimpleName() + ": " + e.getMessage(), LogLevel.ERROR);
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void log(String key, LogLevel level, Object... args) {
        String message = formatMessage(key, args);
        if (SwingUtilities.isEventDispatchThread()) {
            appendMessageToLog(message, level);
        } else {
            SwingUtilities.invokeLater(() -> appendMessageToLog(message, level));
        }
    }
    
    private void appendMessageToLog(String message, LogLevel level) {
        StyledDocument doc = logArea.getStyledDocument();
        Color textColor = switch (level) {
            case SUCCESS -> COLOR_TEXT_SUCCESS;
            case ERROR -> COLOR_TEXT_ERROR;
            case INFO -> COLOR_TEXT_INFO;
            default -> Color.ORANGE;
        };
        SimpleAttributeSet aset = new SimpleAttributeSet();
        StyleConstants.setForeground(aset, textColor);
        StyleConstants.setFontFamily(aset, "Consolas");
        StyleConstants.setFontSize(aset, 13);
        try {
            doc.insertString(doc.getLength(), message + "\n", aset);
            logArea.setCaretPosition(doc.getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String formatMessage(String key, Object... args) {
        try {
            return MessageFormat.format(messages.getString(key), args);
        } catch (Exception e) {
            return key;
        }
    }

    private void refreshLogArea() {
        logArea.setText("");
        for (LogEntry entry : logHistory) {
            String translatedMessage = formatMessage(entry.key(), entry.args());
            appendMessageToLog(translatedMessage, entry.level());
        }
    }

    private void loadGitHubProperties() throws IOException {
        log("log.reading_config", LogLevel.INFO);
        try (InputStream input = new FileInputStream("config/github.properties")) {
            githubProps.load(input);
        }
        log("log.config_loaded", LogLevel.SUCCESS);
        if (githubProps.getProperty("github.email") == null || githubProps.getProperty("github.email").isEmpty()) {
            log("log.config.missing_email", LogLevel.ERROR);
        }
    }

    private void initializeGitHubAPI() throws IOException {
        log("log.connecting_github", LogLevel.INFO);
        String token = githubProps.getProperty("github.token");
        github = new GitHubBuilder().withOAuthToken(token).build();
        log("log.connected_success", LogLevel.SUCCESS, github.getMyself().getLogin());
    }

    private void simulateApprovePullRequest() {
        log("log.approve_pr.looking", LogLevel.INFO);
        try {
            GHRepository repo = getRepository();
            String currentUserLogin = github.getMyself().getLogin();
            
            List<GHPullRequest> openPRs = repo.getPullRequests(GHIssueState.OPEN)
                .stream()
                .filter(pr -> {
                    try {
                        return !pr.getUser().getLogin().equals(currentUserLogin);
                    } catch (IOException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());

            if (openPRs.isEmpty()) {
                log("log.approve_pr.none_found", LogLevel.ERROR);
                return;
            }
            
            GHPullRequest randomPR = openPRs.get(RANDOM.nextInt(openPRs.size()));
            String comment = APPROVE_COMMENTS[RANDOM.nextInt(APPROVE_COMMENTS.length)];
            
            log("log.approve_pr.approving", LogLevel.INFO, randomPR.getNumber(), comment);
            randomPR.createReview().event(GHPullRequestReviewEvent.APPROVE).body(comment).create();
            log("log.approve_pr.success", LogLevel.SUCCESS);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void simulateCreateRelease() {
        log("log.create_release.starting", LogLevel.INFO);
        try {
            GHRepository repo = getRepository();
            String latestTag = getLatestReleaseTag(repo);
            String newTag = getNextVersionTag(latestTag);
            
            String releaseTitle = "Release " + newTag;
            String releaseNotes = RELEASE_NOTES[RANDOM.nextInt(RELEASE_NOTES.length)];
            
            log("log.create_release.creating", LogLevel.INFO, releaseTitle, newTag);
            
            repo.createRelease(newTag)
                .name(releaseTitle)
                .body(releaseNotes)
                .commitish("main")
                .prerelease(false)
                .create();

            log("log.create_release.success", LogLevel.SUCCESS, releaseTitle);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getLatestReleaseTag(GHRepository repo) throws IOException {
        PagedIterable<GHRelease> releases = repo.listReleases();
        Pattern pattern = Pattern.compile("v(\\d+)\\.(\\d+)\\.(\\d+)");

        Optional<String> latestTag = releases.toList().stream()
            .map(GHRelease::getTagName)
            .filter(tag -> pattern.matcher(tag).matches())
            .max(Comparator.comparing(tag -> {
                Matcher m = pattern.matcher(tag);
                if (m.matches()) {
                    return Integer.parseInt(m.group(1)) * 1000000 + 
                           Integer.parseInt(m.group(2)) * 1000 + 
                           Integer.parseInt(m.group(3));
                }
                return 0;
            }));

        return latestTag.orElse("v0.0.0");
    }
    
    private String getNextVersionTag(String latestTag) {
        Pattern pattern = Pattern.compile("v(\\d+)\\.(\\d+)\\.(\\d+)");
        Matcher matcher = pattern.matcher(latestTag);

        if (matcher.matches()) {
            int major = Integer.parseInt(matcher.group(1));
            int minor = Integer.parseInt(matcher.group(2));
            int patch = Integer.parseInt(matcher.group(3)) + 1;
            return "v" + major + "." + minor + "." + patch;
        }
        
        return "v1.0.0";
    }

    private void simulateAddComment() {
        log("log.add_comment.looking", LogLevel.INFO);
        try {
            GHRepository repo = getRepository();
            List<GHIssue> openIssues = repo.getIssues(GHIssueState.OPEN);
            if (openIssues.isEmpty()) {
                log("log.add_comment.none_found", LogLevel.ERROR);
                return;
            }
            GHIssue randomIssue = openIssues.get(RANDOM.nextInt(openIssues.size()));
            String comment = ISSUE_COMMENTS[RANDOM.nextInt(ISSUE_COMMENTS.length)];
            
            log("log.add_comment.adding", LogLevel.INFO, randomIssue.getNumber(), comment);
            randomIssue.comment(comment);
            log("log.add_comment.success", LogLevel.SUCCESS);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void simulateMergePullRequest() {
        log("log.merge_pr.looking", LogLevel.INFO);
        try {
            GHRepository repo = getRepository();
            List<GHPullRequest> openPRs = repo.getPullRequests(GHIssueState.OPEN);

            if (openPRs.isEmpty()) {
                log("log.merge_pr.none_found", LogLevel.ERROR);
                return;
            }

            GHPullRequest randomPR = openPRs.get(RANDOM.nextInt(openPRs.size()));
            log("log.merge_pr.checking_status", LogLevel.INFO, randomPR.getNumber(), randomPR.getTitle());
            
            randomPR = repo.getPullRequest(randomPR.getNumber());
            
            Boolean mergeable = randomPR.getMergeable();

            if (mergeable == null) {
                log("log.merge_pr.status_unknown", LogLevel.ERROR, randomPR.getNumber());
                return;
            }
            
            if (!mergeable) {
                 log("log.merge_pr.not_mergeable", LogLevel.ERROR, randomPR.getNumber());
                 return;
            }

            String mergeMessage = MERGE_MESSAGES[RANDOM.nextInt(MERGE_MESSAGES.length)];
            randomPR.merge(mergeMessage);
            log("log.merge_pr.success", LogLevel.SUCCESS, randomPR.getNumber());

        } catch (IOException e) {
             throw new RuntimeException(e);
        }
    }

    private void showCommitDateDialog() {
        JTextField startDateField = new JTextField(LocalDate.now().minusYears(1).format(DateTimeFormatter.ISO_LOCAL_DATE), 10);
        JTextField endDateField = new JTextField(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE), 10);
        JSpinner minCommitsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
        JSpinner maxCommitsSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 50, 1));
        String[] speedOptions = {
            messages.getString("speed.fast"), 
            messages.getString("speed.medium"), 
            messages.getString("speed.slow")
        };
        JComboBox<String> speedComboBox = new JComboBox<>(speedOptions);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.add(new JLabel(messages.getString("label.start_date")));
        panel.add(startDateField);
        panel.add(new JLabel(messages.getString("label.end_date")));
        panel.add(endDateField);
        panel.add(new JLabel(messages.getString("label.min_commits")));
        panel.add(minCommitsSpinner);
        panel.add(new JLabel(messages.getString("label.max_commits")));
        panel.add(maxCommitsSpinner);
        panel.add(new JLabel(messages.getString("label.speed")));
        panel.add(speedComboBox);

        int result = JOptionPane.showConfirmDialog(this, panel, 
                messages.getString("dialog.commit_dates.title"), 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String startDateStr = startDateField.getText();
            String endDateStr = endDateField.getText();
            int minCommits = (int) minCommitsSpinner.getValue();
            int maxCommits = (int) maxCommitsSpinner.getValue();
            String speed = (String) speedComboBox.getSelectedItem();
            
            if (minCommits > maxCommits) {
                JOptionPane.showMessageDialog(this, messages.getString("error.min_max_commits"), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            btnCommits.setEnabled(false);
            CommitHistoryWorker worker = new CommitHistoryWorker(startDateStr, endDateStr, minCommits, maxCommits, speed);
            worker.execute();
        }
    }
    
    private class CommitHistoryWorker extends SwingWorker<Void, String> {
        private final String startDateStr, endDateStr;
        private final int minCommits, maxCommits;
        private final String speed;

        public CommitHistoryWorker(String start, String end, int min, int max, String speed) {
            this.startDateStr = start;
            this.endDateStr = end;
            this.minCommits = min;
            this.maxCommits = max;
            this.speed = speed;
        }

        @Override
        protected Void doInBackground() throws Exception {
            publish(formatMessage("log.commit_history.executing"));
            
            LocalDate startDate, endDate;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            try {
                startDate = LocalDate.parse(startDateStr, formatter);
                endDate = LocalDate.parse(endDateStr, formatter);
            } catch (DateTimeParseException e) {
                throw new Exception(formatMessage("log.date_parse_error", e.getMessage()));
            }

            int baseDelay, randomDelay;
            if (speed.equals(messages.getString("speed.fast"))) {
                baseDelay = 50; randomDelay = 100;
            } else if (speed.equals(messages.getString("speed.medium"))) {
                baseDelay = 200; randomDelay = 300;
            } else {
                baseDelay = 500; randomDelay = 500;
            }

            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate) && !isCancelled()) {
                int commitsToday = (maxCommits > minCommits) ? RANDOM.nextInt(maxCommits - minCommits + 1) + minCommits : minCommits;
                Set<LocalTime> usedTimes = new HashSet<>();
                
                publish(formatMessage("log.commit_history.processing", currentDate.format(formatter), commitsToday));
                
                for (int i = 0; i < commitsToday; i++) {
                    if (isCancelled()) break;

                    LocalTime commitTime;
                    do {
                        int hour = RANDOM.nextInt(24);
                        int minute = RANDOM.nextInt(60);
                        int second = RANDOM.nextInt(60);
                        commitTime = LocalTime.of(hour, minute, second);
                    } while (usedTimes.contains(commitTime));
                    
                    usedTimes.add(commitTime);
                    LocalDateTime commitDateTime = LocalDateTime.of(currentDate, commitTime);
                    
                    createCommit("Log entry for " + commitDateTime.format(DateTimeFormatter.ISO_DATE_TIME), commitDateTime);
                    Thread.sleep(baseDelay + RANDOM.nextInt(randomDelay));
                }
                currentDate = currentDate.plusDays(1);
            }
            
            if (!isCancelled()) {
                publish(formatMessage("log.commit_history.pushing"));
                executeGitCommand("push", "origin", "main");
            }
            return null;
        }

        @Override
        protected void process(List<String> chunks) {
            for (String message : chunks) {
                appendMessageToLog(message, LogLevel.INFO);
            }
        }

        @Override
        protected void done() {
            try {
                get();
                log("log.commit_history.push_success", LogLevel.SUCCESS);
            } catch (InterruptedException e) {
                log("task_interrupted", LogLevel.ERROR);
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                log("task_exception", LogLevel.ERROR, e.getCause().getMessage());
            } finally {
                btnCommits.setEnabled(true);
            }
        }
    }

    private void simulateIssueWorkflow() {
        try {
            log("log.create_issue.executing", LogLevel.INFO);
            String taskTitle = "Task: " + TASK_TITLES[RANDOM.nextInt(TASK_TITLES.length)] + " #" + (RANDOM.nextInt(900) + 100);
            String taskBody = "This issue was auto-generated.\n- [ ] Task 1\n- [ ] Task 2";
            log("log.create_issue.creating", LogLevel.INFO, taskTitle);
            GHRepository repo = getRepository();
            GHIssue issue = repo.createIssue(taskTitle)
                                .body(taskBody)
                                .label(RANDOM.nextBoolean() ? "bug" : "enhancement")
                                .create();
            log("log.create_issue.success", LogLevel.SUCCESS, issue.getNumber(), issue.getHtmlUrl());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void simulatePullRequestWorkflow() {
        try {
            log("log.create_pr.executing", LogLevel.INFO);
            String originalBranch = "main";
            String taskName = TASK_TITLES[RANDOM.nextInt(TASK_TITLES.length)];
            String newBranch = (RANDOM.nextBoolean() ? "feature" : "fix") + "/pr-" + (RANDOM.nextInt(900) + 100) + "-" + taskName;
            log("log.create_pr.creating_branch", LogLevel.INFO, newBranch);
            executeGitCommand("checkout", "-b", newBranch);
            int numCommits = RANDOM.nextInt(3) + 2;
            for (int i = 0; i < numCommits; i++) {
                createCommit("Commit " + (i + 1) + " for PR on branch " + newBranch, LocalDateTime.now().minusMinutes(numCommits - i));
            }
            log("log.create_pr.pushing_branch", LogLevel.INFO);
            executeGitCommand("push", "origin", newBranch);
            log("log.create_pr.creating_pr", LogLevel.INFO);
            String prTitle = "PR: " + Character.toUpperCase(taskName.charAt(0)) + taskName.substring(1).replace('-', ' ');
            GHRepository repo = getRepository();
            GHPullRequest pullRequest = repo.createPullRequest(prTitle, newBranch, originalBranch, "Auto-generated PR.");
            log("log.create_pr.success", LogLevel.SUCCESS, pullRequest.getNumber(), pullRequest.getHtmlUrl());
            executeGitCommand("checkout", originalBranch);
            executeGitCommand("branch", "-D", newBranch);
            log("log.create_pr.finished", LogLevel.SUCCESS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private GHRepository getRepository() throws IOException {
        String repoName = githubProps.getProperty("github.username") + "/" + githubProps.getProperty("github.repository");
        return github.getRepository(repoName);
    }

    private void createCommit(String content, LocalDateTime commitDateTime) {
        try {
            Path filePath = Paths.get(".", "notes.txt");
            appendToFile(filePath, content + "\n");
            executeGitCommand("add", "notes.txt");
            String commitMessage = COMMIT_MESSAGES[RANDOM.nextInt(COMMIT_MESSAGES.length)];
            String formattedDate = commitDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            
            ProcessBuilder pb = new ProcessBuilder("git", "commit", "-m", commitMessage);
            pb.directory(new File("."));
            
            String authorName = githubProps.getProperty("github.username");
            String authorEmail = githubProps.getProperty("github.email");

            if (authorName != null && authorEmail != null && !authorEmail.isEmpty()) {
                pb.environment().put("GIT_AUTHOR_NAME", authorName);
                pb.environment().put("GIT_AUTHOR_EMAIL", authorEmail);
                pb.environment().put("GIT_COMMITTER_NAME", authorName);
                pb.environment().put("GIT_COMMITTER_EMAIL", authorEmail);
            }
            
            pb.environment().put("GIT_AUTHOR_DATE", formattedDate);
            pb.environment().put("GIT_COMMITTER_DATE", formattedDate);
            
            executeProcess(pb);
        } catch (IOException e) {
            log("log.commit.error", LogLevel.ERROR, e.getMessage());
        }
    }
    
    private void initializeRepository() {
        if (!new File(".", ".git").exists()) {
            log("log.repo_init", LogLevel.INFO);
            executeGitCommand("init");
            executeGitCommand("branch", "-M", "main");
        }
    }
    
    private void createDummyFileIfNotExists() {
        Path filePath = Paths.get(".", "notes.txt");
        if (!Files.exists(filePath)) {
            try {
                Files.createFile(filePath);
            } catch (IOException e) {
                log("log.file_error", LogLevel.ERROR, e.getMessage());
            }
        }
    }

    private void appendToFile(Path filePath, String content) throws IOException {
        try (FileWriter writer = new FileWriter(filePath.toFile(), true)) {
            writer.append(content);
        }
    }
    
    private void executeGitCommand(String... commands) {
        String[] fullCommand = new String[commands.length + 1];
        fullCommand[0] = "git";
        System.arraycopy(commands, 0, fullCommand, 1, commands.length);
        executeProcess(new ProcessBuilder(fullCommand));
    }
    
    private void executeProcess(ProcessBuilder processBuilder) {
        try {
            processBuilder.directory(new File("."));
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            try (Scanner s = new Scanner(process.getInputStream())) {
                while (s.hasNextLine()) {
                    String line = s.nextLine();
                    log(line, LogLevel.GIT_CMD);
                }
            }
            if (process.waitFor() != 0) {
                log("log.git_command_failed", LogLevel.ERROR, String.join(" ", processBuilder.command()));
            }
        } catch (IOException | InterruptedException e) {
            log("log.git_command_error", LogLevel.ERROR, e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ActivitySimulator().setVisible(true);
        });
    }
}
