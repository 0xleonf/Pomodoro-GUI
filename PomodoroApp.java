import javax.swing.*;
import java.awt.*;

interface PomodoroListener {
    void onTick(int secondsLeft);
    void onStateChange(boolean isRunning, boolean isPaused);
    void onSessionComplete(int currentSession, int totalSessions);
}

interface PomodoroController {
    void start();
    void pause();
    void reset();
    void setTotalSessions(int total);
    int getTotalSessions();
    int getCurrentSession();
}

class PomodoroEngine implements PomodoroController {
    private static final int WORK_TIME = 25 * 60;
    private int secondsLeft = WORK_TIME;
    private int totalSessions = 4;
    private int currentSession = 1;
    private boolean isRunning = false;
    private boolean isPaused = false;
    private final Timer timer;
    private final PomodoroListener listener;

    public PomodoroEngine(PomodoroListener listener) {
        this.listener = listener;
        this.timer = new Timer(1000, e -> tick());
    }

    private void tick() {
        if (secondsLeft > 0) {
            secondsLeft--;
            listener.onTick(secondsLeft);
        } else {
            Toolkit.getDefaultToolkit().beep();
            listener.onSessionComplete(currentSession, totalSessions);
            if (currentSession < totalSessions) {
                currentSession++;
                secondsLeft = WORK_TIME;
                listener.onTick(secondsLeft);
            } else {
                reset();
            }
        }
    }

    @Override
    public void start() {
        isRunning = true;
        isPaused = false;
        timer.start();
        listener.onStateChange(isRunning, isPaused);
    }

    @Override
    public void pause() {
        if (!isRunning) return;
        if (isPaused) {
            isPaused = false;
            timer.start();
        } else {
            isPaused = true;
            timer.stop();
        }
        listener.onStateChange(isRunning, isPaused);
    }

    @Override
    public void reset() {
        timer.stop();
        isRunning = false;
        isPaused = false;
        secondsLeft = WORK_TIME;
        currentSession = 1;
        listener.onTick(secondsLeft);
        listener.onStateChange(isRunning, isPaused);
    }

    @Override
    public void setTotalSessions(int total) {
        this.totalSessions = Math.max(1, total);
        if (this.currentSession > this.totalSessions) {
            this.currentSession = 1;
        }
        listener.onSessionComplete(currentSession - 1, totalSessions);
    }

    @Override
    public int getTotalSessions() { return totalSessions; }

    @Override
    public int getCurrentSession() { return currentSession; }
}

public class PomodoroApp extends JFrame implements PomodoroListener {
    private final JLabel timeLabel = new JLabel("25:00", SwingConstants.CENTER);
    private final JLabel sessionLabel = new JLabel("Sesi: 1 / 4", SwingConstants.CENTER);
    private final JButton toggleBtn = new JButton("Mulai");
    private final JButton resetBtn = new JButton("Reset");
    private final PomodoroController controller;

    public PomodoroApp() {
        this.controller = new PomodoroEngine(this);
        initUI();
    }

    private void initUI() {
        setTitle("Pomodoro Timer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(320, 240);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        setJMenuBar(createMenuBar());

        timeLabel.setFont(new Font("Monospaced", Font.BOLD, 46));
        sessionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        centerPanel.add(timeLabel);
        centerPanel.add(sessionLabel);
        add(centerPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(toggleBtn);
        btnPanel.add(resetBtn);
        add(btnPanel, BorderLayout.SOUTH);

        toggleBtn.addActionListener(e -> {
            JButton btn = (JButton) e.getSource();
            if (btn.getText().equals("Mulai")) {
                controller.start();
            } else {
                controller.pause();
            }
        });

        resetBtn.addActionListener(e -> controller.reset());
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu sessionMenu = new JMenu("Pengaturan Sesi");

        int[] presets = {2, 4, 6, 8};
        for (int count : presets) {
            JMenuItem item = new JMenuItem(count + " Sesi");
            item.addActionListener(e -> {
                controller.setTotalSessions(count);
                sessionLabel.setText("Sesi: " + controller.getCurrentSession() + " / " + count);
            });
            sessionMenu.add(item);
        }

        JMenuItem customItem = new JMenuItem("Custom...");
        customItem.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Masukkan jumlah sesi:", "Set Sesi", JOptionPane.QUESTION_MESSAGE);
            if (input != null && input.matches("\\d+")) {
                int count = Integer.parseInt(input);
                if (count > 0) {
                    controller.setTotalSessions(count);
                    sessionLabel.setText("Sesi: " + controller.getCurrentSession() + " / " + count);
                }
            }
        });
        sessionMenu.addSeparator();
        sessionMenu.add(customItem);

        menuBar.add(sessionMenu);
        return menuBar;
    }

    @Override
    public void onTick(int secondsLeft) {
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft % 60;
        timeLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    @Override
    public void onStateChange(boolean isRunning, boolean isPaused) {
        if (!isRunning) {
            toggleBtn.setText("Mulai");
        } else if (isPaused) {
            toggleBtn.setText("Lanjut");
        } else {
            toggleBtn.setText("Pause");
        }
    }

    @Override
    public void onSessionComplete(int currentSession, int totalSessions) {
        sessionLabel.setText("Sesi: " + currentSession + " / " + totalSessions);
        if (currentSession >= totalSessions) {
            JOptionPane.showMessageDialog(this, "Semua sesi pomodoro selesai!", "Selesai", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PomodoroApp().setVisible(true));
    }
}
