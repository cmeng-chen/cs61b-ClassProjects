package canfield;

import ucb.gui.TopLevel;
import ucb.gui.LayoutSpec;

import java.awt.event.MouseEvent;

/** A top-level GUI for Canfield solitaire.
* @author Chen MENG
*/
class CanfieldGUI extends TopLevel {

    /** A new window with given TITLE and displaying GAME. */
    CanfieldGUI(String title, Game game) {
        super(title, true);
        _game = game;
        addLabel("Game Canfield", new LayoutSpec("y", 1, "x", 0.5));
        addMenuButton("Menu->Quit", "quit");
        addMenuButton("Menu->Undo", "undo");
        addMenuButton("Menu->New Game", "newgame");
        addLabel("Score: 0", "Score",
                 new LayoutSpec("y", 1, "x", 1));
        _display = new GameDisplay(game);
        add(_display, new LayoutSpec("y", 2, "width", 2));
        _display.setMouseHandler("click", this, "mouseClicked");
        _display.setMouseHandler("release", this, "mouseReleased");
        _display.setMouseHandler("drag", this, "mouseDragged");
        _display.setMouseHandler("press", this, "mousePressed");
        display(true);
    }

    /** Coordination for waste pile.*/
    private static final int WASTELEFT = 150, WASTERIGHT = 240,
        WASTEBOTTOM = 340, WASTETOP = 220;

    /** Coordination for stock pile.*/
    private static final int STOCKLEFT = 30, STOCKRIGHT = 120;

    /** Coordination for reserve pile.*/
    private static final int RESERVETOP = 50, RESERVEBOTTOM = 170;

    /** Coordination for first pile.*/
    private static final int FIRSTPILE = 1, FIRSTLEFT = 330, FIRSTRIGHT = 420;

    /** Coordination for second pile.*/
    private static final int SECONDPILE = 2, SECONDLEFT = 450,
        SECONDRIGHT = 540;

    /** Coordination for third pile.*/
    private static final int THIRDPILE = 3, THIRDLEFT = 570, THIRDRIGHT = 660;

    /** Coordination for fourth pile.*/
    private static final int FOURTHPILE = 4, FOURTHLEFT = 690,
        FOURTHRIGHT = 780;

    /** Displayed dimensions of a card image. */
    private static final int CARD_HEIGHT = 125, CARD_WIDTH = 90;

    /** Displayed difference in a stack of tableau. */
    private static final int DIFFERENCE = 20;

    /** I don't understand this number as well. */
    private static final int MAGICNUMBER = 48;

    /** Respond to "Quit" button. */
    public void quit(String dummy) {
        if (showOptions("Really quit?", "Quit?", "question",
                "Yes", "Yes", "No") == 0) {
            System.exit(1);
        }
    }

    /** Respond to "Undo" button. */
    public void undo(String dummy) {
        _game.undo();
        _display.repaint();
        updateScore();
    }

    /** Respond to "New Game" button. */
    public void newgame(String dummy) {
        _game.deal();
        _display.repaint();
        updateScore();
    }

    /** Display score. */
    void updateScore() {
        setLabel("Score",
                 String.format("Score: %d", _game.getScore()));
    }

    /** Determine if the player wins. */
    void win() {
        if (_game.isWon()) {
            showMessage("Congratulations you win!!", "WIN", "plain");
        }
    }

    /** Return the corresponding pile of the location.
    @param locx locx
    @param locy locy */
    public String location(int locx, int locy) {
        if ((WASTELEFT <= locx && locx < WASTERIGHT)
                && (WASTETOP <= locy && locy < WASTEBOTTOM)) {
            return "waste";
        }
        if ((STOCKLEFT <= locx && locx < STOCKRIGHT)
                && (RESERVETOP <= locy && locy < RESERVEBOTTOM)) {
            return "reserve";
        }
        if (RESERVETOP <= locy && locy < RESERVEBOTTOM) {
            if (FIRSTLEFT <= locx && locx < FIRSTRIGHT) {
                return "f1";
            }
            if (SECONDLEFT <= locx && locx < SECONDRIGHT) {
                return "f2";
            }
            if (THIRDLEFT <= locx && locx < THIRDRIGHT) {
                return "f3";
            }
            if (FOURTHLEFT <= locx && locx < FOURTHRIGHT) {
                return "f4";
            }
        }
        if ((FIRSTLEFT <= locx && locx < FIRSTRIGHT)
            && (WASTETOP <= locy && locy <= WASTETOP
                + (_game.tableauSize(FIRSTPILE) - 1)
                    * DIFFERENCE + CARD_HEIGHT)) {
            return "t1";
        }
        if ((SECONDLEFT <= locx && locx < SECONDRIGHT)
            && (WASTETOP <= locy && locy <= WASTETOP
                + (_game.tableauSize(SECONDPILE) - 1)
                    * DIFFERENCE + CARD_HEIGHT)) {
            return "t2";
        }
        if ((THIRDLEFT <= locx && locx < THIRDRIGHT)
            && (WASTETOP <= locy && locy <= WASTETOP
                + (_game.tableauSize(THIRDPILE) - 1)
                    * DIFFERENCE + CARD_HEIGHT)) {
            return "t3";
        }
        if ((FOURTHLEFT <= locx && locx < FOURTHRIGHT)
            && (WASTETOP <= locy && locy <= WASTETOP
                + (_game.tableauSize(FOURTHPILE) - 1)
                    * DIFFERENCE + CARD_HEIGHT)) {
            return "t4";
        }
        return "";
    }

    /** Action in response to mouse-clicking event EVENT. */
    public synchronized void mouseClicked(MouseEvent event) {
        int x = event.getX(), y = event.getY();
        String loc = location(x, y);
        if ((STOCKLEFT <= x && x < STOCKRIGHT)
                && (WASTETOP <= y && y < WASTEBOTTOM)) {
            _game.stockToWaste();
        }
        updateScore();
        _display.repaint();
        win();
    }

    /** Action in response to mouse-pressing event EVENT. */
    public synchronized void mousePressed(MouseEvent event) {
        int x = event.getX(), y = event.getY();
        String loc = location(x, y);
        if (loc.equals("waste")) {
            _pressedCard = _game.topWaste();
            fr = "waste";
        } else if (loc.equals("reserve")) {
            _pressedCard = _game.topReserve();
            fr = "reserve";
        } else {
            char[] lochar = new char[2];
            loc.getChars(0, loc.length(), lochar, 0);
            if (lochar[0] == new Character('f')) {
                _pressedCard = _game.topFoundation(lochar[1] - MAGICNUMBER);
                fr = "foundation";
                index = lochar[1] - MAGICNUMBER;
            }
            if (lochar[0] == new Character('t')) {
                _pressedCard = _game.topTableau(lochar[1] - MAGICNUMBER);
                fr = "tableau";
                index = lochar[1] - MAGICNUMBER;
            }
        }
        _display.repaint();
    }

    /** Action in response to mouse-released event EVENT. */
    public synchronized void mouseReleased(MouseEvent event) {
        int x = event.getX(), y = event.getY();
        String loc = location(x, y);
        if (!(loc.equals("") || loc.equals("waste")
            || loc.equals("reserve"))) {
            char[] lochar = new char[2];
            loc.getChars(0, loc.length(), lochar, 0);
            if (lochar[0] == new Character('f')) {
                try {
                    if (fr.equals("waste")) {
                        _game.wasteToFoundation();
                    }
                    if (fr.equals("tableau")) {
                        _game.tableauToFoundation(index);
                    }
                    if (fr.equals("reserve")) {
                        _game.reserveToFoundation();
                    }
                } catch (IllegalArgumentException err) {
                    showMessage(err.getMessage(), "ERROR", "error");
                }
            } else if (lochar[0] == new Character('t')) {
                try {
                    if (fr.equals("waste")) {
                        _game.wasteToTableau(lochar[1] - MAGICNUMBER);
                    }
                    if (fr.equals("tableau")) {
                        _game.tableauToTableau(index, lochar[1] - MAGICNUMBER);
                    }
                    if (fr.equals("reserve")) {
                        _game.reserveToTableau(lochar[1] - MAGICNUMBER);
                    }
                    if (fr.equals("foundation")) {
                        _game.foundationToTableau(index,
                            lochar[1] - MAGICNUMBER);
                    }
                } catch (IllegalArgumentException err) {
                    showMessage(err.getMessage(), "ERROR", "error");
                }
            }
        }
        _display.release();
        _pressedCard = null;
        fr = null;
        index = -1;
        updateScore();
        _display.repaint();
        win();
    }

    /** Action in response to mouse-dragging event EVENT. */
    public synchronized void mouseDragged(MouseEvent event) {
        if (_pressedCard != null) {
            _display.drag(event.getX(), event.getY(), _pressedCard);
        }
        _display.repaint();
    }


    /** The pressed card that should be dragged. */
    private Card _pressedCard = null;

    /** Recording the first pile being . */
    private String fr = null;

    /** Recording the index of tablequ or foundation . */
    private int index = -1;

    /** The board widget. */
    private final GameDisplay _display;

    /** The game I am consulting. */
    private final Game _game;

}
