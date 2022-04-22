import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

public class Board implements Cloneable
{
    private Game game;
    private Toolkit toolkit;

    public char[][] board = new char[3][3];

    private double width = 0.0;
    private double xPos = 0.0;
    private double yPos = 0.0;
    private double markSize = 0;
    private double markPad = 0;

    public int playCount = 1;

    public Board(Game g, Toolkit tk)
    {
        game = g;
        toolkit = tk;

        for (int i = 0; i < board.length; i++)
        {
            for (int j = 0; j < board.length; j++)
            {
                if (i == 1 && j == 1)
                {
                    board[i][j] = 'X';
                } else {
                    board[i][j] = '-';
                }
            }
        }

        width = tk.getScreenSize().width / 2;
        yPos = (tk.getScreenSize().height - width) / 2;

        markSize = width / 4;
        markPad = (width / 3 - width / 4) / 2;
    }

    public boolean mark(int x, int y)
    {
        if (x < xPos)
            return false;
        if (x > xPos + width)
            return false;
        if (y < yPos)
            return false;
        if (y > yPos + width)
            return false;

        int i = (int) ((x - xPos) / (width/3));
        int j = (int) ((y - yPos) / (width/3));

        if (board[i][j] == 'X' || board[i][j] == 'O')
            return false;

        board[i][j] = 'O';
        playCount++;
        return true;
    }

    public Board clone()
    {
        Board b = new Board(game, toolkit);
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                b.board[i][j] = board[i][j];
            }
        }
        return b;
    }

    // 0 = game not ended
    // 1 = draw
    // 2 = AI win
    // 3 = player win
    public int check()
    {
        for (int i = 0; i < board.length; i++)
        {
            char mark = board[i][0];
            if (mark == '-')
                continue;
            boolean win = true;
            for (int j = 1; j < board[i].length; j++)
            {
                if (board[i][j] != mark)
                    win = false;
            }
            if (win)
                if (mark == 'X')
                    return 2;
                else
                    return 3;
        }

        for (int j = 0; j < board.length; j++)
        {
            char mark = board[0][j];
            if (mark == '-')
                continue;
            boolean win = true;
            for (int i = 1; i < board[j].length; i++)
            {
                if (board[i][j] != mark)
                    win = false;
            }
            if (win)
                if (mark == 'X')
                    return 2;
                else
                    return 3;
        }

        char mark = board[0][0];
        boolean win = true;
        if (mark == 'X' || mark == 'O')
        {
            for (int i = 1; i < board.length; i++)
            {
                if (board[i][i] != mark)
                    win = false;
            }
            if (win)
                if (mark == 'X')
                    return 2;
                else
                    return 3;
        }

        mark = board[0][2];
        win = true;
        if (mark == 'X' || mark == 'O')
        {
            for (int i = 1; i < board.length; i++)
            {
                if (board[i][2-i] != mark)
                    win = false;
            }
            if (win)
                if (mark == 'X')
                    return 2;
                else
                    return 3;
        }

        if (playCount == 9)
            return 1;

        return 0;
    }

    public void drawBoard(Graphics2D g)
    {
        g.setStroke(new BasicStroke(10));

        if (game.debug) {
            xPos = 0;
        } else {
            xPos = (int) (width / 2);
        }

        g.draw(new Line2D.Double(xPos + width/3, yPos + 0, xPos + width/3, yPos + width));
        g.draw(new Line2D.Double(xPos + 2*width/3, yPos + 0, xPos + 2*width/3, yPos + width));
        g.draw(new Line2D.Double(xPos + 0, yPos + width/3, xPos + width, yPos + width/3));
        g.draw(new Line2D.Double(xPos + 0, yPos + 2*width/3, xPos + width, yPos + 2*width/3));

        for (int i = 0; i < board.length; i++)
        {
            for (int j = 0; j < board[i].length; j++)
            {
                if (board[i][j] == 'X')
                {
                    g.draw(new Line2D.Double(xPos + i*width/3+markPad, yPos + j*width/3+markPad, xPos + (i+1)*width/3-markPad, yPos + (j+1)*width/3-markPad));
                    g.draw(new Line2D.Double(xPos + i*width/3+markPad, yPos + (j+1)*width/3-markPad, xPos + (i+1)*width/3-markPad, yPos + j*width/3+markPad));
                }
                if (board[i][j] == 'O')
                {
                    g.draw(new Ellipse2D.Double(xPos + i*width/3+markPad, yPos + j*width/3+markPad, markSize, markSize));
                }
            }
        }
    }

}
