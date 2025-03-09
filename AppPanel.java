import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.Timer;

class AppPanel extends JPanel {
    int birdX = 100;
    int birdY = 200;
    int birdVelocity = 0;
    int pipeX = 500;
    int pipeGap = 150;
    int pipeWidth = 60;
    int pipeSpeed = 3;
    int pipeHeightTop = 200;
    int score = 0; // Add a score counter
    int highestScore = 0; // Add a highest score counter
    boolean gameOver = false; // Add a game over flag
    static BufferedImage BackImage;
    static BufferedImage BirdImage;
    static BufferedImage PipeUp;
    static BufferedImage PipeDown;
    static BufferedImage PipeDownRotated;

    Timer timer;

    AppPanel() {
        setSize(500, 500);
        loadImages();
        PipeDownRotated = rotateImageBy180(PipeDown);
        timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameOver) {
                    gameLoop();
                }
                repaint();
            }
        });
        timer.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (gameOver) {
                        resetGame();
                    } else {
                        birdVelocity = -10;
                    }
                }
            }
        });
        setFocusable(true);
    }

    private void loadImages() {
        try {
            BackImage = ImageIO.read(AppPanel.class.getResource("back11.png"));
            BirdImage = ImageIO.read(AppPanel.class.getResource("Birdimage.png")).getSubimage(0, 0, 320, 240);
            PipeUp = ImageIO.read(AppPanel.class.getResource("pipeup11.png"));
            PipeDown = ImageIO.read(AppPanel.class.getResource("pipedw1.png"));
        } catch (IOException e) {
            System.out.println("Error loading images: " + e.getMessage());
        }
    }

    private BufferedImage rotateImageBy180(BufferedImage image) {
        AffineTransform transform = AffineTransform.getRotateInstance(Math.PI, image.getWidth() / 2, image.getHeight() / 2);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(image, null);
    }

    private void gameLoop() {
        birdY += birdVelocity;
        birdVelocity += 1;

        pipeX -= pipeSpeed;
        if (pipeX + pipeWidth <= 0) {
            pipeX = 500;
            pipeHeightTop = (int) (Math.random() * 200) + 50;
            score++; // Increase score when bird successfully passes a pipe
        }

        if (birdY < 0 || birdY > 500 || checkPipeCollision()) {
            gameOver = true; // Set gameOver to true
            if (score > highestScore) {
                highestScore = score; // Update highest score
            }
        }
    }

    private boolean checkPipeCollision() {
        if (birdX + 40 > pipeX && birdX < pipeX + pipeWidth && birdY < pipeHeightTop) {
            return true;
        }

        if (birdX + 40 > pipeX && birdX < pipeX + pipeWidth && birdY + 40 > pipeHeightTop + pipeGap) {
            return true;
        }

        return false;
    }

    private void resetGame() {
        birdX = 100;
        birdY = 200;
        birdVelocity = 0;
        pipeX = 500;
        pipeHeightTop = 200;
        score = 0; // Reset score
        gameOver = false; // Reset gameOver status
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(BackImage, 0, 0, 500, 500, null);
        g.drawImage(BirdImage, birdX, birdY, 60, 40, null);
        g.drawImage(PipeUp, pipeX, 0, pipeWidth, pipeHeightTop, null);
        g.drawImage(PipeDownRotated, pipeX, pipeHeightTop + pipeGap, pipeWidth, 500 - (pipeHeightTop + pipeGap), null);

        // Display score in the top left corner
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(java.awt.Color.BLACK);
        g.drawString("Score: " + score, 10, 20);

        if (gameOver) {
            // Display Game Over message in the center
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.setColor(java.awt.Color.RED);
            g.drawString("Game Over", 180, 250);
            
            // Display instruction to restart the game
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.setColor(java.awt.Color.BLACK);
            g.drawString("Press Space To Restart", 150, 270);

            // Display highest score
            g.setColor(java.awt.Color.YELLOW);
            g.drawString("Highest Score: " + highestScore, 180, 300);
        }
    }
}
