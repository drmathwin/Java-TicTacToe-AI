import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GameCanvas extends JPanel implements Runnable
{

    private Game game;
    private Graphics graphics;
    Toolkit tk;

    private double waitTime = 25.0;

    private double rate = 1000.0 / waitTime;

    public int cursor = 0;

    public GameCanvas(Game game, Graphics g, Toolkit tk)
    {
        this.game = game;
        graphics = g;
        this.tk = tk;
    }

    @Override
    public void run() {
        while(true)
        {
            long startTime = System.nanoTime();

            int width = tk.getScreenSize().width;
            int height = tk.getScreenSize().height;

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();

            g2d.setColor(Color.CYAN);
            g2d.fillRect(0, 0, width, height);

            g2d.setColor(Color.BLACK);
            game.board.drawBoard(g2d);

            if (!game.running) {
                g2d.drawString(String.format("%s Reset Game", cursor == 0 ? ">" : " "), 25, 25);
                g2d.drawString(String.format("%s Exit Game", cursor == 1 ? ">" : " "), 25, 50);
                String vol = "";
                for (int i = 0; i < 11; i++)
                {
                    if ((int) (game.volume * 10) == i)
                    {
                        vol += "|";
                    } else {
                        vol += "-";
                    }
                }
                g2d.drawString(String.format("%s Volume %s", cursor == 2 ? ">" : " ", vol), 25, 75);
                g2d.drawString(String.format("%s Debug Mode %s", cursor == 3 ? ">" : " ", game.debug ? "(ON)" : "(OFF)"), 25, 100);
            }
            if (game.debug) {
                g2d.drawString(String.format("FPS = %.1f", rate), 200, 25);
                g2d.drawString(String.format("UPS = %.1f", game.rate), 200, 50);

                for (int i = 0; i < 3; i++)
                {
                    for (int j = 0; j < 3; j++)
                    {
                        g2d.drawString(String.format("%.2f", game.plays[i][j]), width / 2 + i * 100, 100 + j * 100);
                    }
                }
            }

            graphics.drawImage(image, 0, 0, null);

            long sleep = (long) waitTime - (System.nanoTime() - startTime) / 1000000;
            rate = 1000.0 / Math.max(waitTime - sleep, waitTime);

            try
            {
                Thread.sleep(Math.max(sleep, 0));
            } catch (InterruptedException ex)
            {

            }
        }
    }
}
