import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Game implements Runnable
{
    private GameCanvas canvas;

    public double waitTime = 10.0;
    public double rate = 1000 / waitTime;

    public Board board;
    public double[][] plays = new double[3][3];
    private double winVal = 1000.0;
    private double loseVal = -1000.0;

    public Toolkit tk;
    public boolean debug = true;
    public boolean running = true;
    public double volume = 0.3;

    private boolean turn = true;
    private double aiWin = 1000.0;
    private double aiLose = -1000.0;
    private double invalid = -10000.0;

    public Game()
    {
        JFrame frame = new JFrame("Game");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        frame.setUndecorated(true);

        tk = Toolkit.getDefaultToolkit();

        frame.setVisible(true);
        frame.requestFocus();

        board = new Board(this, tk);

        canvas = new GameCanvas(this, frame.getGraphics(), tk);
        frame.add(canvas);

        Thread drawLoop = new Thread(canvas);
        drawLoop.start();

        frame.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (running && turn) {
                    if (board.mark(e.getX(), e.getY())) {
                        turn = false;
                        if (board.check() > 0) {
                            reset();
                        } else {
                            aiMove();
                            if (board.check() > 0)
                            {
                                reset();
                                return;
                            }
                            turn = true;
                        }
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        frame.addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
                {
                    running = !running;
                }
                if(e.getKeyCode() == KeyEvent.VK_UP)
                {
                    if (!running)
                    {
                        canvas.cursor--;
                        canvas.cursor = Math.max(canvas.cursor, 0);
                    }
                }
                if(e.getKeyCode() == KeyEvent.VK_DOWN)
                {
                    if (!running)
                    {
                        canvas.cursor++;
                        canvas.cursor = Math.min(canvas.cursor, 3);
                    }
                }
                if(e.getKeyCode() == KeyEvent.VK_RIGHT)
                {
                    if (!running)
                    {
                        if (canvas.cursor == 2) {
                            volume += 0.1;
                            volume = Math.min(volume, 1.0);
                        }
                    }
                }
                if(e.getKeyCode() == KeyEvent.VK_LEFT)
                {
                    if (!running)
                    {
                        if (canvas.cursor == 2) {
                            volume -= 0.1;
                            volume = Math.max(volume, 0.0);
                        }
                    }
                }
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    if (!running)
                    {
                        if (canvas.cursor == 0)
                            reset();
                        if (canvas.cursor == 1)
                            System.exit(0);
                        if (canvas.cursor == 3)
                            debug = !debug;
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
            }
        });
    }

    @Override
    public void run()
    {
        while(true)
        {
            long startTime = System.nanoTime();

            if (running)
            {
            }

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

    private void aiMove()
    {
        // Set all possible plays to -10,000 and set possible moves to their heuristic value
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                plays[i][j] = invalid;
                if (board.board[i][j] == '-')
                {
                    Board b = board.clone();
                    b.board[i][j] = 'X';
                    b.playCount++;
                    plays[i][j] = min(b);
                }
            }
        }

        // Find the move that has the highest heuristic value
        int x = 0;
        int y = 0;
        double value = invalid * 2;
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                if (plays[i][j] > value)
                {
                    x = i;
                    y = j;
                    value = plays[i][j];
                }
            }
        }

        // Set the current move to the highest-heuristic-value move
        board.board[x][y] = 'X';
        board.playCount++;
    }

    private double max(Board board)
    {
        double result = board.check();
        if (result == 2)
            return winVal;
        if (result == 3)
            return loseVal;
        if (result == 1)
            return 0;

        double total = 0.0;
        double count = 0.0;
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                if (board.board[i][j] == '-')
                {
                    Board b = board.clone();
                    b.board[i][j] = 'X';
                    b.playCount++;
                    total += min(b);
                    count += 1.0;
                }
            }
        }
        return count == 0.0 ? -10.0 : total / count;
    }

    private double min(Board board)
    {
        double result = board.check();
        if (result == 2)
            return winVal;
        if (result == 3)
            return loseVal;
        if (result == 1)
            return 0;

        double total = 0.0;
        double count = 0.0;
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                if (board.board[i][j] == '-')
                {
                    Board b = board.clone();
                    b.board[i][j] = 'O';
                    b.playCount++;
                    total += max(b);
                    count += 1.0;
                }
            }
        }
        return count == 0.0 ? -10.0 : total / count;
    }

    public void reset()
    {
        board = new Board(this, tk);
        turn = true;

        running = true;
    }

    public static void main(String[] args)
    {
        Game game = new Game();

        Thread logicLoop = new Thread(game);
        logicLoop.start();
    }
}
