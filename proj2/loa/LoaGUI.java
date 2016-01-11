package loa;

import ucb.gui.TopLevel;
import ucb.gui.LayoutSpec;

import java.awt.event.MouseEvent;

/** A top-level GUI for Line of Actions.
  * @author Chen MENG
  */
class LoaGUI extends TopLevel {
	/** A new window with given TITLE and displaying GAME. */
    LoaGUI(String title, GameGUI game) {
        super(title, true);
        _game = game;
        addLabel("Welcome to Line of Actions!!!!!", new LayoutSpec("y", 1, "x", 0.5));
        addMenuButton("Menu->Start", "start");
        addMenuButton("Menu->New Game", "clear");
        addMenuButton("Menu->Help", "help");
        addMenuButton("Menu->Quit", "quit");
        addMenuButton("Auto->Black", "autoB");
        addMenuButton("Auto->White", "autoW");
        addMenuButton("Manual->Black", "manualB");
        addMenuButton("Manual->White", "manualW");
        addMenuButton("Set->Black", "setB");
        addMenuButton("Set->White", "setW");
        addMenuButton("Set->Empty", "setE");
        addLabel("Black pieces left: 12", "bPieces",
                 new LayoutSpec("y", 1, "x", 1));
        addLabel("White pieces left: 12", "wPieces",
                 new LayoutSpec("y", 2, "x", 1));
        _display = new GameDisplay(game);
        add(_display, new LayoutSpec("y", 2, "width", 2));
        _display.setMouseHandler("click", this, "mouseClicked");
        // _display.setMouseHandler("release", this, "mouseReleased");
        _display.setMouseHandler("drag", this, "mouseDragged");
        display(true);
    }

    /** Get the mouse position.
      * @param pp pp;
      */
    public void getMouse(int[] pp) {
        pp[0] = _xd;
        pp[1] = _yd;
    }

    public void announce(String msg, String title) {
        showMessage(msg, title, "information");
    }

    /** Respond to "Menu - start" button. */
    public void start(String dummy) {
        _game.startCommand();
    }

    /** Respond to "Menu - Clear" button. */
    public void clear(String dummy) {
        _game.clearCommand();
        _display.repaint();
    }

    /** Respond to "Menu - Help" button. */
    public void help(String dummy) {
        String helpText = "Commands: Commands are whitespace-delimited.\n"
            + "Other trailing text on a line "
            + "is ignored. Comment lines begin with # and are ignored.\n"
            + "  b\n"
            + "  board     Display the board, "
            + "showing row and column designations.\n"
            + "  autoprint Toggle mode in which "
            + "the board is printed (as for b) after each "
            + "AI move.\n"
            + "  start     "
            + "Start playing from the current position.\n"
            + "  uv-xy     A move from square uv "
            + "to square xy.  Here u and v are column"
            + "designations (a-h) and v and y are row designations (1-8): \n"
            + "  clear     "
            + "Stop game and return to initial position.\n"
            + "P is white or black; makes P into an AI. Stops game.\n"
            + "  manual P  P is white or black; "
            + "takes moves for P from terminal. Stops game.\n"
            + "  set cr P  Put P ('w', 'b', or "
            + "empty) into square cr. Stops game.\n"
            + "  dump      Display the board in "
            + "standard format.\n"
            + "  quit      End program.\n"
            + "  help\n"
            + "  ?         This text.\n";
        showMessage(helpText, "Help", "instruction");
    }

    /** Repaint the board. */
    public void repaint() {
        _display.repaint();
    }

    /** Respond to "Menu - quit" button. */
    public void quit(String dummy) {
        if (showOptions("Really quit?", "Quit?", "question", "Yes", "Yes",
                "No") == 0) {
            System.exit(0);
        }
    }

    /** Respond to "Manual-Black" button. */
    public void manualB(String dummy) {
        _game.manualCommand(Piece.BP);
    }

    /** Respond to "Manual-White" button. */
    public void manualW(String dummy) {
        _game.manualCommand(Piece.WP);
    }

    /** Respond to "Auto-Black" button. */
    public void autoB(String dummy) {
        _game.autoCommand(Piece.BP);
    }

    /** Respond to "Auto-White" button. */
    public void autoW(String dummy) {
        _game.autoCommand(Piece.WP);
    }

    /** Respond to "Set-Black" button. */
    public void setB(String dummy) {
    	String c = Integer.toString(_yd);
    	char cha = 'a';
    	String r = Integer.toString(_xd + (int)cha);
    	String pos = c + r;
        _game.setCommand(pos, Piece.BP);
    }

    /** Respond to "Set-White" button. */
    public void setW(String dummy) {
		String c = Integer.toString(_yd);
    	char cha = 'a';
    	String r = Integer.toString(_xd + (int)cha);
    	String pos = c + r;
        _game.setCommand(pos, Piece.WP);
    }

    /** Respond to "Set-Empty" button. */
    public void setE(String dummy) {
    	System.out.println("We are here");
		String c = Integer.toString(_yf);
    	char cha = 'a';
    	String r = Integer.toString(_xf + (int)cha);
    	String pos = c + r;
        _game.setCommand(pos, Piece.EMP);
    }

    /** Action in response to mouse-dragging event EVENT. */
    public synchronized void mouseDragged(MouseEvent event) {
    	_xd = event.getX() / GameDisplay.SQUARE_SIZE;
    	_yd = event.getY() / GameDisplay.SQUARE_SIZE;
    	System.out.println("In dragged the xd is " + _xd);
    	System.out.println("In dragged the yd is " + _yd);
        // _display.repaint();
    }

    /** Action in response to mouse-clicking event EVENT. */
    public synchronized void mouseClicked(MouseEvent event) {
        _xf = event.getX() / GameDisplay.SQUARE_SIZE;
        _yf = event.getY() / GameDisplay.SQUARE_SIZE;
        System.out.println("In pressed the xd is " + _xf);
    	System.out.println("In pressed the yd is " + _yf);
    }

    /** The game I am consulting. */
    private final GameGUI _game;

    /** The coordinate of the mouse click. */
    private int _xf, _yf;

    /** The coordinate of the mouse dragged. */
    private int _xd, _yd;

    /** The board widget. */
    private final GameDisplay _display;
}
