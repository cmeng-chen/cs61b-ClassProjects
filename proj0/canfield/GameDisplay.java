package canfield;

import ucb.gui.Pad;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

import javax.imageio.ImageIO;

import java.io.InputStream;
import java.io.IOException;

/** A widget that displays a Pinball playfield.
 *  @author P. N. Hilfinger
 */
class GameDisplay extends Pad {

    /** Color of display field. */
    private static final Color BACKGROUND_COLOR = Color.white;

    /* Coordinates and lengths in pixels unless otherwise stated. */

    /** Preferred dimensions of the playing surface. */
    private static final int BOARD_WIDTH = 810, BOARD_HEIGHT = 700,
        WIDTHBLANK = 30, HEIGHTBLANK = 50;

    /** Displayed dimensions of a card image. */
    private static final int CARD_HEIGHT = 125, CARD_WIDTH = 90;

    /** Coordination for first pile.*/
    private static final int FIRSTLEFT = 330;

    /** Coordination for waste pile.*/
    private static final int WASTELEFT = 150, WASTETOP = 220;

    /** Coordination for stock pile.*/
    private static final int STOCKRIGHT = 120;

    /** Displayed difference in a stack of tableau. */
    private static final int DIFFERENCE = 20;

    /** Coordination for reserve pile.*/
    private static final int RESERVETOP = 50;


    /** A graphical representation of GAME. */
    public GameDisplay(Game game) {
        _game = game;
        setPreferredSize(BOARD_WIDTH, BOARD_HEIGHT);
    }

    /** Return an Image read from the resource named NAME. */
    private Image getImage(String name) {
        InputStream in =
            getClass().getResourceAsStream("/canfield/resources/" + name);
        try {
            return ImageIO.read(in);
        } catch (IOException excp) {
            return null;
        }
    }

    /** Return an Image of CARD. */
    private Image getCardImage(Card card) {
        return getImage("playing-cards/" + card + ".png");
    }

    /** Return an Image of the back of a card. */
    private Image getBackImage() {
        return getImage("playing-cards/blue-back.png");
    }

    /** Draw CARD at X, Y on G. */
    private void paintCard(Graphics2D g, Card card, int x, int y) {
        if (card != null) {
            g.drawImage(getCardImage(card), x, y,
                        CARD_WIDTH, CARD_HEIGHT, null);
        }
    }

    /** Draw card back at X, Y on G. */
    private void paintBack(Graphics2D g, int x, int y) {
        g.drawImage(getBackImage(), x, y, CARD_WIDTH, CARD_HEIGHT, null);
    }

    /** Draw a blank card back at X, Y on G. */
    private void paintBlank(Graphics2D g, int x, int y) {
        g.draw(new Rectangle(x, y, CARD_WIDTH, CARD_HEIGHT));
    }

    /** The card C being dragged to X, Y. */
    void drag(int x, int y, Card c) {
        _cardx = x;
        _cardy = y;
        _carddragged = c;
    }

    /** The card dragged is released. */
    void release() {
        _cardx = 0;
        _cardy = 0;
        _carddragged = null;
    }

    @Override
    public synchronized void paintComponent(Graphics2D g) {
        g.setColor(BACKGROUND_COLOR);
        Rectangle b = g.getClipBounds();
        g.fillRect(0, 0, b.width, b.height);
        g.setColor(Color.black);
        paintCard(g, _game.topReserve(),
                        WIDTHBLANK, HEIGHTBLANK);
        for (int i = 1, x = FIRSTLEFT, y = RESERVETOP;
                i < 5; i += 1, x += WIDTHBLANK + CARD_WIDTH) {
            if (_game.topFoundation(i) == null) {
                paintBlank(g, x, y);
            } else {
                paintCard(g, _game.topFoundation(i), x, y);
            }
        }
        if (_game.stockEmpty()) {
            paintBlank(g, WIDTHBLANK, WASTETOP);
        } else {
            paintBack(g, WIDTHBLANK, WASTETOP);
        }
        if (_game.topWaste() != null) {
            paintCard(g, _game.topWaste(), WASTELEFT, WASTETOP);
        } else {
            paintBlank(g, WASTELEFT, WASTETOP);
        }
        for (int i = 1, x = FIRSTLEFT, y = WASTETOP;
            i < 5; i += 1, x += STOCKRIGHT) {
            if (_game.topTableau(i) == null) {
                paintBlank(g, x, y);
            } else {
                for (int k = _game.tableauSize(i) - 1, j = y;
                    k >= 0; k -= 1, j += DIFFERENCE) {
                    paintCard(g, _game.getTableau(i, k), x, j);
                }
            }
        }
        if (_cardx != _cardy && _carddragged != null) {
            paintCard(g, _carddragged, _cardx, _cardy);
        }
    }


    /** Game I am displaying. */
    private final Game _game;

    /** X coordinate of the card being dragged. */
    private int _cardx = 0;

    /** Y coordinate of the card being dragged. */
    private int _cardy = 0;

    /** The card being dragged. */
    private Card _carddragged = null;

}
