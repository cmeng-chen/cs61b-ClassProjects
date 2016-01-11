package loa;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

import static loa.Piece.*;
import static loa.Main.*;

/**
 * Represents one game of Lines of Action.
 *
 * @author Amy Ge
 */
class GameGUI extends Game {

    /** Magic number. */
    private final int magic1 = 50, magic2 = 200;

    /** A new series of Games. */
    GameGUI() {
        _randomSource = new Random();
        _players = new Player[2];
        _input = new BufferedReader(new InputStreamReader(System.in));
        _players[0] = new HumanPlayer(BP, this);
        _players[1] = new MachinePlayer(WP, this);
        _playing = false;
    }

    /** Return the current board. */
    Board getBoard() {
        return _board;
    }

    /**
     * Return a move. Processes any other intervening commands as well.
     * Exits with null if the value of _playing changes.
     */
    Move getMove() {
        int[] p0 = new int[2];
        int[] p1 = new int[3];

        while (_playing) {
            while (_playing) {
                while (_playing) {
                    _command.getMouse(p0);
                    System.out.println("_command.getMouse 0");
                    if (p0[0] >= 0 && p0[1] >= 0) {
                        System.out.println("We got a mouse move on the board for 0");
                        break;
                    }
                    try {
                        Thread.sleep(magic1);
                        System.out.println("This is the thread.sleep.");
                    } catch (InterruptedException ex) {
                        System.out.println("This is the interruption.");
                        return null;
                    }
                }
                if (!_playing) {
                    return null;
                }
                if (_board.get(p0[0] + 1, p0[1] + 1) == _board.turn()) {
                    System.out.print("Warning!!!");
                    break;
                }
            }
            if (!_playing) {
                return null;
            }
            while (_playing) {
                _command.getMouse(p1);
                System.out.println("_command.getMouse 1");
                if (p1[0] >= 0 && p1[1] >= 0) {
                    System.out.println("We got a mouse move on the board for 1");
                    break;
                }
                try {
                    Thread.sleep(magic1);
                    System.out.println("This is the thread.sleep.");
                } catch (InterruptedException ex) {
                    System.out.println("This is the interruption.");
                    return null;
                }
            }
            if (!_playing) {
                return null;
            }
            if (p0[0] == p1[0] || p0[1] == p1[1]
                    || Math.abs(p1[0] - p0[0]) == Math.abs(p1[1] - p0[1])) {
                Move m = Move.create(p0[0] + 1, p0[1] + 1, p1[0] + 1, p1[1] + 1,
                        _board);
                if (m != null && _board.isLegal(m)) {
                    return m;
                }
            }
        }
        return null;
    }

    /** Command to start the game. */
    public void startCommand() {
        _playing = true;
    }

    /** Command to clear the board to original state. */
    public void clearCommand() {
        _board = new Board();
        _playing = false;
    }

    /** Command to dump the board as text. */
    public void dumpCommand() {
        System.out.println(_board);
    }

    /** Command to set new piece to the board.
     * @param sq sq;
     * @param p p;
     */
    public void setCommand(String sq, Piece p) {
        if (!_board.ROW_COL.matcher(sq).matches()) {
            error("invalid square: %s%n", sq);
            return;
        }
        _board.set(sq.charAt(0) - 'a' + 1, sq.charAt(1) - '0', p, p.opposite());
        _playing = false;
        _command.repaint();
    }

    /** Set player p (WP or BP) to be a manual player.
     * @param p p;
     */
    public void manualCommand(Piece p) {
        _playing = false;
        _players[p.ordinal()] = new HumanPlayer(p, this);
    }

    /** Set player p (WP or BP) to be an automated player.
     * @param p p;
     */
    public void autoCommand(Piece p) {
        _playing = false;
        _players[p.ordinal()] = new MachinePlayer(p, this);
    }

    /** Seed random-number generator with SEED (as a long). */
    public void seedCommand(String seed) {
        try {
            _randomSource.setSeed(Long.parseLong(seed));
        } catch (NumberFormatException excp) {
            error("Invalid number: %s", seed);
        }
    }

    /** Play this game, printing any results. */
    public void play() {
        _board = new Board();
        _command = new LoaGUI("Lines of Action", this);

        while (true) {
            int playerInd = _board.turn().ordinal();
            if (_playing) {
                if (_board.gameOver()) {
                    announceWinner();
                    _playing = false;
                    continue;
                }
                Move next = _players[playerInd].makeMove();
                System.out.println("The move is get");
                if (next != null) {
                    assert _board.isLegal(next);
                    _board.makeMove(next);
                    _command.repaint();
                    System.out.println("Repaint");
                    if (_board.gameOver()) {
                        announceWinner();
                        _playing = false;
                    }
                }
            } else {
                try {
                    Thread.sleep(magic2);
                } catch (InterruptedException ex) {
                    return;
                }
            }
        }
    }

    /** Print an announcement of the winner. */
    public void announceWinner() {
        Piece winer;
        if (_board.piecesContiguous(_board.turn().opposite())) {
            winer = _board.turn().opposite();
        } else {
            winer = _board.turn();
        }
        if (winer == WP) {
            _command.announce("White wins.", "Game Over");
            System.out.println("White wins.");
        } else {
            _command.announce("Black wins.", "Game Over");
            System.out.println("Black wins.");
        }
    }

    /**
     * Return an integer r, 0 <= r < N, randomly chosen from a uniform
     * distribution using the current random source.
     */
    int randInt(int n) {
        return _randomSource.nextInt(n);
    }

    /** The official game board. */
    private Board _board;

    /** The _players of this game. */
    private Player[] _players = new Player[2];

    /**
     * A source of random numbers, primed to deliver the same sequence in any
     * Game with the same seed value.
     */
    private Random _randomSource;

    /** Input source. */
    private BufferedReader _input;

    /** True if actually playing (game started and not stopped or finished). */
    private boolean _playing;
    /** GUI Command. */
    private LoaGUI _command;
}
