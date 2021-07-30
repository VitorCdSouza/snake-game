import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 30; // measure of the game
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static final int DELAY = 70;
    final int x[] = new int[GAME_UNITS]; // defines the snake position
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6; // snake'll start with 6 parts
    int applesEaten;
    int appleX; // defines the apple position
    int appleY;
    char direction = 'R';
    boolean running = false;
    static boolean gameOn = false;
    Timer timer;
    Random random;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }
    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }
    public void pause() {
        GamePanel.gameOn = false;
        timer.stop();
    }
    public void resume() {
        GamePanel.gameOn = true;
        timer.start();
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // will turn the background color on
        draw(g);
    }
    public void draw(Graphics g) {
        if (running) {
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
            }
            g.setColor(Color.green);
            g.fillRect(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(new Color(0, 92, 255));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(0, 0, 255));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.red);
            g.setFont(new Font("Times New Roman", Font.BOLD, 25));
            FontMetrics fontMetrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - fontMetrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }
    public void newApple() {
        appleX = random.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE; // turns random the apple spawn point
        appleY = random.nextInt((int)(SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }
    public void move() {
        for(int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U': y[0] = y[0] - UNIT_SIZE; break;
            case 'D': y[0] = y[0] + UNIT_SIZE; break;
            case 'R': x[0] = x[0] + UNIT_SIZE; break;
            case 'L': x[0] = x[0] - UNIT_SIZE; break;
        }
    }
    public void checkApple() {
        if((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollision() {
        // head collision with body
        for(int i = bodyParts; i > 0; i --) {
            if((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        // check left border
        if(x[0] <= - UNIT_SIZE) running = false;
        // check right border
        if(x[0] >= SCREEN_WIDTH) running = false;
        // check top border
        if(y[0] <= - UNIT_SIZE) running = false;
        // check bottom border
        if(y[0] >= SCREEN_HEIGHT) running = false;

        if(!running) timer.stop();
    }
    public void gameOver(Graphics g) {
        // Game Over Text
        g.setColor(Color.red);
        g.setFont(new Font("Times New Roman", Font.BOLD, 65));
        FontMetrics fontMetrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - fontMetrics.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
        // Score
        g.setColor(Color.red);
        g.setFont(new Font("Times New Roman", Font.BOLD, 35));
        FontMetrics fontMetrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - fontMetrics1.stringWidth("Score: " + applesEaten)) / 2, SCREEN_HEIGHT / 2 + SCREEN_HEIGHT / 12);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(running) {
            move();
            checkApple();
            checkCollision();
        }
        repaint();
    }
    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT: if (direction != 'R') direction = 'L'; break;
                case KeyEvent.VK_RIGHT: if (direction != 'L') direction = 'R'; break;
                case KeyEvent.VK_UP: if (direction != 'D') direction = 'U'; break;
                case KeyEvent.VK_DOWN: if (direction != 'U') direction = 'D'; break;
                case KeyEvent.VK_SPACE:
                case KeyEvent.VK_P:
                    if(GamePanel.gameOn) {
                        pause();
                    } else {
                        resume();
                    }
                    break;
            }
        }
    }
}
