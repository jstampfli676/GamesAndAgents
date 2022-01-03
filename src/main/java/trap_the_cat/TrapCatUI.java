package trap_the_cat;

import neural_network.NeuralNet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;

public class TrapCatUI extends JPanel {

    private ArrayList<Hex> board;
    private int catPosition;
    private boolean catTurn = false;

    private static final Color unvisited = new Color(0xCCFF00);
    private static final Color visited = new Color(0x728501);

    private static final long serialVersionUID = 1L;
    private final int WIDTH = 1200;
    private final int HEIGHT = 800;

    private final int W2 = WIDTH / 2;
    private final int H2 = HEIGHT / 2;

    private static boolean firstPaint = true;

    private static final ArrayList<Integer> allowedMoves1 = new ArrayList<>(Arrays.asList(10, 11, 1, -1, -11, -12));
    private static final ArrayList<Integer> allowedMoves2 = new ArrayList<>(Arrays.asList(12, 11, 1, -1, -10, -11));

    private Font font = new Font("Arial", Font.BOLD, 24);
    FontMetrics metrics;

    public TrapCatUI() {
        board = new ArrayList<>();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        addMouseListener(new MouseAdapter() {
           public void mouseClicked(MouseEvent me) {
               int x = me.getX();
               int y = me.getY();
               for (Hex h : board) {
                   if (h.contains(new Point(x, y))) {
                       System.out.println(h.getId() + " " + h.getBlocked());
                       if (!catTurn && !h.getBlocked() && !h.getCat()) {
                           h.setBlocked(true);
                           catTurn = true;
                           System.out.println("tried to blcok tile");
                       } else if (catTurn && !h.getBlocked() && !h.getCat()) {
                           if (h.getId()/11 % 2 == 0) {
                               if (allowedMoves1.contains(h.getId() - catPosition)) {
                                   board.get(catPosition).setCat(false);
                                   catPosition = h.getId();
                                   h.setCat(true);
                                   System.out.println("tried to move cat "+ h.getId());
                                   catTurn = false;
                               }
                           } else {
                               if (allowedMoves2.contains(h.getId() - catPosition)) {
                                   board.get(catPosition).setCat(false);
                                   catPosition = h.getId();
                                   h.setCat(true);
                                   System.out.println("tried to move cat "+ h.getId());
                                   catTurn = false;
                               }
                           }

                       }
                   }
               }
               repaint();
           }
        });
    }

    /*public void playVsAi(boolean catAi) {
        if (catAi) {
            NeuralNet catAgent = new NeuralNet("agents/catAgents/catagent0.txt", 121,64,1);
            while (true) {
                if (catTurn) {

                }
            }
        }
    }*/

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;


        g2d.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
        g2d.setFont(font);
        metrics = g.getFontMetrics();

        //drawCircle(g2d, W2, H2, 660, true, true, 0x4488FF, 0);

       // drawHexGridAdvanced(g2d, 5, 60);
        drawCatGrid(g2d, 11, 35);
        //System.out.println(board);
    }

    private void drawCatGrid(Graphics g, int n, int r) {
        //board.clear();
        //Hex.resetIDCount();
        double ang30 = Math.toRadians(30);
        double xOff = Math.cos(ang30) * r;
        double yOff = Math.sin(ang30) * r;
        int h = n / 2;
        double shift = r-3.5;

        Color curColor;
        if (firstPaint) {
            for (int i = 0; i<n; i++) {
                for (int x = 0; x<n; x++) {
                    Hex temp = new Hex((int) (W2 + xOff * (-n + (x * 2 + 1)) + shift), (int) (H2 - yOff * 3 * (i-h)), r);
                    if (Math.random()<=.07) {
                        curColor = visited;
                        temp.setBlocked(true);
                    } else {
                        curColor = unvisited;
                        temp.setBlocked(false);
                    }
                    if (i == h && x == h) {
                        curColor = new Color(0);
                        catPosition = temp.getId();
                        temp.setCat(true);
                        temp.setBlocked(false);
                    }
                    board.add(temp);
                    drawHex(g, x, i, temp, curColor);
                    firstPaint = false;
                }
                if (shift == r-3.5) {
                    shift = 0;
                } else {
                    shift = r-3.5;
                }
            }
        } else {
            for (Hex hex : board) {
                if (hex.getCat()) {
                    curColor = new Color(0);
                } else if (hex.getBlocked()) {
                    curColor = visited;
                } else {
                    curColor = unvisited;
                }
                drawHex(g, 0, 0, hex, curColor);
            }
        }
    }

    private void drawHex(Graphics g, int posX, int posY, int x, int y, int r, Color c) {
        Hex hex = new Hex(x, y, r);
        drawHex(g, posX, posY, hex, c);
    }

    private void drawHex(Graphics g, int posX, int posY, Hex hex, Color c) {
        String text = String.format("%s", hex.getId());
        int w = metrics.stringWidth(text);
        int h = metrics.getHeight();

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(c);
        g2d.fillPolygon(hex);
        g2d.setStroke(new BasicStroke(8));
        g2d.setColor(new Color(0xFFFFFF));
        g2d.drawPolygon(hex);
        //g2d.setColor(new Color(0xFFFFFF));
        //g2d.drawString(text, (int)hex.center.getX() - w/2, (int)hex.center.getY() + h/2);
    }

    private String coord(int value) {
        return (value > 0 ? "+" : "") + Integer.toString(value);
    }

    public void drawCircle(Graphics2D g, int x, int y, int diameter,
                           boolean centered, boolean filled, int colorValue, int lineThickness) {
        drawOval(g, x, y, diameter, diameter, centered, filled, colorValue, lineThickness);
    }

    public void drawOval(Graphics2D g, int x, int y, int width, int height,
                         boolean centered, boolean filled, int colorValue, int lineThickness) {
        // Store before changing.
        Stroke tmpS = g.getStroke();
        Color tmpC = g.getColor();

        g.setColor(new Color(colorValue));
        g.setStroke(new BasicStroke(lineThickness, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));

        int x2 = centered ? x - (width / 2) : x;
        int y2 = centered ? y - (height / 2) : y;

        if (filled)
            g.fillOval(x2, y2, width, height);
        else
            g.drawOval(x2, y2, width, height);

        // Set values to previous when done.
        g.setColor(tmpC);
        g.setStroke(tmpS);
    }

    public static void main(String[] args) {
        JFrame f = new JFrame();
        f.setBackground(new Color(0xFFFFFF));
        TrapCatUI p = new TrapCatUI();

        f.setContentPane(p);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
