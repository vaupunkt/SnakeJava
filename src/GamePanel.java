
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.sound.midi.*;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE; // 900

    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    int DELAY = 75;

    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;
    JButton restartButton;


    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.darkGray);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        this.setFocusable(true);
        restartButton = new JButton("Restart");
        restartButton.addActionListener(this); // Add action listener
        this.add(restartButton);  // Add button to the panel
        restartButton.setVisible(false); // Hide button
        startGame();
    }

    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void newApple() {
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE)) * UNIT_SIZE;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    
    public void draw(Graphics g) {
        if (running) {
            for (int i = 0; i<SCREEN_HEIGHT/UNIT_SIZE; i++) {
                g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
            }
            
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
            
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.white);
            g.setFont(new Font("Ink Free", Font.BOLD, 32));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        }  else {
            gameOver(g);
        } 
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch (direction) {
            case 'R' -> x[0] = x[0] + UNIT_SIZE;
            case 'U' -> y[0] = y[0] - UNIT_SIZE;
            case 'L' -> x[0] = x[0] - UNIT_SIZE;
            case 'D' -> y[0] = y[0] + UNIT_SIZE;
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            playBeep("apple");
            newApple();
        }
    }

    public void checkCollisions() {
        // check if head collides with body
        for (int i = bodyParts; i > 0; i--){
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }

        // check if head touches left border
        if (x[0] < 0) {
            running = false;
        }

        // check if head touches right border
        if (x[0] > SCREEN_WIDTH) {
            running = false;
        }

        // check if head touches top border
        if (y[0] < 0) {
            running = false;
        }

        // check if head touches bottom border
        if (y[0] > SCREEN_HEIGHT) {
            running = false;
        }
        if (!running) {
            timer.stop();
        }

    }

    public void gameOver(Graphics g) {
        // Game Over Text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics1.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
        g.setColor(Color.white);
        g.setFont(new Font("Ink Free", Font.BOLD, 32));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics2.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());

        if (!running) {
             playBeep("gameover");

            restartButton.setVisible(true); // Make button visible
            restartButton.setBounds(SCREEN_WIDTH / 2 - 50, SCREEN_HEIGHT / 2 + 100, 100, 50); 
        } else {
            restartButton.setVisible(false); // Hide button during gameplay
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            checkApple();
            move();
            checkCollisions();
        } else if (e.getSource() == restartButton) { 
            // Check if restart button clicked
            for (int i = bodyParts; i > 0; i--) {
                x[i] = 0;
                y[i] = 0;
            }
            x[0] = UNIT_SIZE;
            y[0] = 0;
            bodyParts = 6;
            applesEaten = 0;
            direction = 'R';
            restartButton.setVisible(false); // Hide button
            newApple();
            running = true;
            timer = new Timer(DELAY, this);
            timer.start();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;

                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;

                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }

        }
    }

    public void playBeep(String variant) {
       new Thread(() -> {
            try (Synthesizer synthesizer = MidiSystem.getSynthesizer()) {
               synthesizer.open();
               MidiChannel[] channels = synthesizer.getChannels();
               if ("apple".equals(variant)) {
                   channels[0].noteOn(60, 600); // Note C4
                   Thread.sleep(200); // Duration of the beep
                   channels[0].noteOff(60);
                } else if ("gameover".equals(variant)) {
                   channels[0].noteOn(48, 600); // Note C3
                   Thread.sleep(200); // Duration of the beep
                   channels[0].noteOff(48);
                   channels[0].noteOn(36, 600); // Note C2
                    Thread.sleep(200); // Duration of the beep
                   channels[0].noteOff(36);
               }
           }
            catch (MidiUnavailableException | InterruptedException e) {
                 e.printStackTrace();
            }
       }).start();
    }

}
