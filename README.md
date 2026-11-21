# Pomodoro-GUI

Java Swing desktop Pomodoro timer.

![Preview](asset/howItLooks.PNG)

## Features

- 25-minute work intervals.
- Session tracker with audio alert on completion.
- Session presets (2, 4, 6, 8) and custom count via menu bar.
- Start, pause, resume, and reset controls.

## Architecture

- **UI Components:** `JFrame` top-level container, `JPanel` layouts (`BorderLayout`, `GridLayout`, `FlowLayout`), `JLabel`, `JButton`, `JMenuBar`.
- **Patterns:** Interface separation (`PomodoroController`, `PomodoroListener`) and event-driven engine (`PomodoroEngine` using `javax.swing.Timer`).

## Run

```bash
javac PomodoroApp.java
java PomodoroApp
```
