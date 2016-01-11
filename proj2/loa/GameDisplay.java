package loa;

import ucb.gui.Pad;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

import javax.imageio.ImageIO;

import java.io.InputStream;
import java.io.IOException;

/** A widget that displays a the board for Line of Actions.
 *  @author Chen Meng
 */

class GameDisplay extends Pad {

    /** Color of display field. */
    private static final Color[] BACKGROUND_COLOR = {Color.orange, Color.GRAY };
    /** Preferred dimensions of the unit square. */
    public static final int SQUARE_SIZE = 50;

    /** Preferred dimensions of the unit square. */
    private static final int PIECE_WIDTH = 50;

    /** The number of unit squares on each side.*/
    private static final int N = 8;

    /** A graphical representation of GAME. */
    public GameDisplay(GameGUI game) {
        _game = game;
        setPreferredSize(SQUARE_SIZE * N, SQUARE_SIZE * N);
    }

    /* ========== Paint Method. ========== */
    private void paintPiece(Graphics2D g, Piece side, int x, int y) {
    	if (side == Piece.BP) {
    		g.setColor(Color.black);
    	} else {
    		g.setColor(Color.white);
    	}
    	g.fillOval(x * SQUARE_SIZE, y * SQUARE_SIZE, PIECE_WIDTH, PIECE_WIDTH);
    }


    // /* ========== Method to drag piece. ========== */
    // /** The piece to drag. */
    // private static Piece _Piece;
    // /** Coordinate for the movement. */
    // private static int _xFrom, _yFrom, _xTo, _yTo;

    // public void setDPiece(Piece piece, int x, int y) {
    //     _Piece = piece;
    //     _xFrom = x;
    //     _yFrom = y;
    // }

    // public void moveDraggedPiece(int x, int y) {
    //     _dToX = x;
    //     _dToY = y;
    // }

    /* ========== Paint the Game. ========== */
    @Override
    public synchronized void paintComponent(Graphics2D g) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                g.setColor(BACKGROUND_COLOR[(i + j) % 2]);
                g.fillRect(i * SQUARE_SIZE, j * SQUARE_SIZE, SQUARE_SIZE,
                        SQUARE_SIZE);
                Piece p = _game.getBoard().get(i + 1, j + 1);
                System.out.println("We are at " + i + "-" + j + "for chess" + p);
                if (p != Piece.EMP) {
                    paintPiece(g, p, i, j);
                }
            }
        }
    }

    /** Game I am displaying. */
    private final Game _game;

}
