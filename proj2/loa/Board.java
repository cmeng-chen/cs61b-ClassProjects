package loa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Formatter;
import java.util.NoSuchElementException;

import java.util.regex.Pattern;

import static loa.Piece.*;
import static loa.Direction.*;

/** Represents the state of a game of Lines of Action.
 *  @author Chen Meng
 */
class Board implements Iterable<Move> {

    /** Size of a board. */
    static final int M = 8;
    /** Pattern describing a valid square designator (cr). */
    static final Pattern ROW_COL = Pattern.compile("^[a-h][1-8]$");

    /** A Board whose initial contents are taken from INITIALCONTENTS
     *  and in which the player playing TURN is to move. The resulting
     *  Board has
     *        get(col, row) == INITIALCONTENTS[row-1][col-1]
     *  Assumes that PLAYER is not null and INITIALCONTENTS is MxM.
     *
     *  CAUTION: The natural written notation for arrays initializers puts
     *  the BOTTOM row of INITIALCONTENTS at the top.
     */
    Board(Piece[][] initialContents, Piece turn) {
        initialize(initialContents, turn);
    }

    /** A new board in the standard initial position. */
    Board() {
        clear();
    }

    /** A Board whose initial contents and state are copied from
     *  BOARD. */
    Board(Board board) {
        copyFrom(board);
    }

    /** Set my state to CONTENTS with SIDE to move. */
    void initialize(Piece[][] contents, Piece side) {
        _moves.clear();

        _boardPieces = new Piece[M][M];

        for (int r = 1; r <= M; r += 1) {
            for (int c = 1; c <= M; c += 1) {
                set(c, r, contents[r - 1][c - 1]);
            }
        }
        _turn = side;

    }

    /** Set me to the initial configuration. */
    void clear() {
        initialize(INITIAL_PIECES, BP);
    }

    /** Set my state to a copy of BOARD. */
    void copyFrom(Board board) {
        if (board == this) {
            return;
        }
        _moves.clear();
        _moves.addAll(board._moves);
        _turn = board._turn;
        _boardPieces = board._boardPieces;
    }

    /** Return the contents of column C, row R, where 1 <= C,R <= 8,
     *  where column 1 corresponds to column 'a' in the standard
     *  notation. */
    Piece get(int c, int r) {
        return _boardPieces[r - 1][c - 1];
    }

    /** Return the contents of the square SQ.  SQ must be the
     *  standard printed designation of a square (having the form cr,
     *  where c is a letter from a-h and r is a digit from 1-8). */
    Piece get(String sq) {
        return get(col(sq), row(sq));
    }

    /** Return the column number (a value in the range 1-8) for SQ.
     *  SQ is as for {@link get(String)}. */
    static int col(String sq) {
        if (!ROW_COL.matcher(sq).matches()) {
            throw new IllegalArgumentException("bad square designator");
        }
        return sq.charAt(0) - 'a' + 1;
    }

    /** Return the row number (a value in the range 1-8) for SQ.
     *  SQ is as for {@link get(String)}. */
    static int row(String sq) {
        if (!ROW_COL.matcher(sq).matches()) {
            throw new IllegalArgumentException("bad square designator");
        }
        return sq.charAt(1) - '0';
    }

    /** Set the square at column C, row R to V, and make NEXT the next side
     *  to move, if it is not null. */
    void set(int c, int r, Piece v, Piece next) {
        Piece replaced = _boardPieces[r - 1][c - 1];
        _boardPieces[r - 1][c - 1] = v;
        if (next != null) {
            _turn = next;
        }
    }

    /** Set the square at column C, row R to V. */
    void set(int c, int r, Piece v) {
        set(c, r, v, null);
    }

    /** Assuming isLegal(MOVE), make MOVE. */
    void makeMove(Move move) {
        assert isLegal(move);
        _moves.add(move);
        Piece replaced = move.replacedPiece();
        int c0 = move.getCol0(), c1 = move.getCol1();
        int r0 = move.getRow0(), r1 = move.getRow1();
        if (replaced != EMP) {
            set(c1, r1, EMP);
        }
        set(c1, r1, move.movedPiece());
        set(c0, r0, EMP);
        _turn = _turn.opposite();
    }

    /** Retract (unmake) one move, returning to the state immediately before
     *  that move.  Requires that movesMade () > 0. */
    void retract() {
        assert movesMade() > 0;
        Move move = _moves.remove(_moves.size() - 1);
        Piece replaced = move.replacedPiece();
        int c0 = move.getCol0(), c1 = move.getCol1();
        int r0 = move.getRow0(), r1 = move.getRow1();
        Piece movedPiece = move.movedPiece();
        set(c1, r1, replaced);
        set(c0, r0, movedPiece);
        _turn = _turn.opposite();
    }

    /** Return the Piece representing who is next to move. */
    Piece turn() {
        return _turn;
    }

    /** Return the board. */
    Board board() {
        return this;
    }

    /** Return true iff MOVE is legal for the player currently on move. */
    boolean isLegal(Move move) {
        if (move == null) {
            return false;
        }
        if (blocked(move)) {
            return false;
        }
        int num = pieceCountAlong(move);
        Direction dir = direction(move);
        int row = dir.dr * num;
        int col = dir.dc * num;
        int rowMoved = move.getRow1() - move.getRow0();
        int colMoved = move.getCol1() - move.getCol0();
        if (colMoved != col || rowMoved != row) {
            return false;
        }
        return true;
    }

    /** Return a sequence of all legal moves from this position. */
    Iterator<Move> legalMoves() {
        return new MoveIterator();
    }

    @Override
    public Iterator<Move> iterator() {
        return legalMoves();
    }

    /** Return true if there is at least one legal move for the player
     *  on move. */
    public boolean isLegalMove() {
        return iterator().hasNext();
    }

    /** Return true iff either player has all his pieces continguous. */
    boolean gameOver() {
        return piecesContiguous(BP) || piecesContiguous(WP);
    }

    /** Return true iff SIDE's pieces are continguous. */
    boolean piecesContiguous(Piece side) {
        boolean[][] marked = new boolean[M][M];
        for (int i = 0; i < M; i += 1) {
            for (int j = 0; j < M; j += 1) {
                marked[i][j] = false;
            }
        }
        int col, row;
        col = row = 9;
        for (int j = 0; j < M; j += 1) {
            for (int i = 0; i < M; i += 1) {
                if (_boardPieces[j][i] == side) {
                    row = j + 1;
                    col = i + 1;
                    break;
                }
            }
            if (col != 9) {
                break;
            }
        }
        if (col == 9) {
            return true;
        }
        marked[row - 1][col - 1] = true;
        int markedNum = continguousMark(col, row, side, marked) + 1;
        return numPieces(side) == markedNum;
    }

    /** Return the number of contiguous side pieces on the board.
      * @param c the column
      * @param r the row
      * @param side the side of the piece to count
      * @param marked a boolean matrix to check if the piece
      * is already checked*/
    int continguousMark(int c, int r, Piece side, boolean[][] marked) {
        Direction dir = Direction.values()[1];
        Piece pie = null;
        int cNext, rNext;
        int s = 0;
        while (dir != null) {
            cNext = c + dir.dc;
            rNext = r + dir.dr;
            if (1 <= cNext && cNext <= M && 1 <= rNext && rNext <= M) {
                pie = get(cNext, rNext);
                if (pie == side && !marked[rNext - 1][cNext - 1]) {
                    marked[rNext - 1][cNext - 1] = true;
                    s += 1;
                    s += continguousMark(cNext, rNext, side, marked);
                }
            }
            dir = dir.succ();
        }
        return s;
    }

    /** Return the number of side pieces on the board.
      * Takes in eiter BP or WP.
      * @param side the side to count*/
    int numPieces(Piece side) {
        int sum = 0;
        for (Piece[] i: _boardPieces) {
            for (Piece j: i) {
                if (j == side) {
                    sum += 1;
                }
            }
        }
        return sum;
    }

    /** Return the total number of moves that have been made (and not
     *  retracted).  Each valid call to makeMove with a normal move increases
     *  this number by 1. */
    int movesMade() {
        return _moves.size();
    }

    @Override
    public boolean equals(Object obj) {
        Board b = (Board) obj;
        for (int c = 1; c <= M; c += 1) {
            for (int r = 1; r <= M; r += 1) {
                if (b.get(c, r) != get(c, r)) {
                    return false;
                }
            }
        }
        if (turn() != b.turn()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashcode = 0;
        for (Piece[] j: _boardPieces) {
            for (Piece i: j) {
                hashcode += hashNum(i);
                hashcode *= 3;
            }
        }
        return hashcode;
    }

    /** Assign 0 to EMP, 1 to BP and 2 to WP for hashcode computing.
      * @param side the side of the hashnum.
      * @return int return the number representing the hashnum. */
    int hashNum(Piece side) {
        if (side == EMP) {
            return 0;
        } else if (side == BP) {
            return 1;
        }
        return 2;
    }

    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("===%n");
        for (int r = M; r >= 1; r -= 1) {
            out.format("    ");
            for (int c = 1; c <= M; c += 1) {
                out.format("%s ", get(c, r).abbrev());
            }
            out.format("%n");
        }
        out.format("Next move: %s%n===", turn().fullName());
        return out.toString();
    }

    /** Return the number of pieces in the line of action indicated by MOVE. */
    int pieceCountAlong(Move move) {
        return pieceCountAlong(move.getCol0(), move.getRow0(), direction(move));
    }

    /** Return the direction of a move.
      * @param m the direction of that move*/
    Direction direction(Move m) {
        if (m.getCol0() == m.getCol1()) {
            if (m.getRow0() < m.getRow1()) {
                return N;
            }
            return S;
        }
        if (m.getRow0() == m.getRow1()) {
            if (m.getCol0() < m.getCol1()) {
                return E;
            }
            return W;
        }
        if (m.getRow0() < m.getRow1()) {
            if (m.getCol0() < m.getCol1()) {
                return NE;
            }
            return NW;
        }
        if (m.getRow0() > m.getRow1()) {
            if (m.getCol0() < m.getCol1()) {
                return SE;
            }
            return SW;
        }
        return NOWHERE;
    }

    /** Return the number of pieces in the line of action in direction DIR and
     *  containing the square at column C and row R. */
    int pieceCountAlong(int c, int r, Direction dir) {
        int c1, c2, r1, r2;
        c1 = c2 = c;
        r1 = r2 = r;
        c1 += dir.dc;
        r1 += dir.dr;
        c2 -= dir.dc;
        r2 -= dir.dr;
        int sum = 1;
        while (1 <= c1 && c1 <= M && 1 <= r1 && r1 <= M) {
            if (get(c1, r1) != EMP) {
                sum += 1;
            }
            c1 += dir.dc;
            r1 += dir.dr;
        }
        while (1 <= c2 && c2 <= M && 1 <= r2 && r2 <= M) {
            if (get(c2, r2) != EMP) {
                sum += 1;
            }
            c2 -= dir.dc;
            r2 -= dir.dr;
        }
        return sum;
    }

    /** Return true iff MOVE is blocked by an opposing piece or by a
     *  friendly piece on the target square. */
    boolean blocked(Move move) {
        Direction d = direction(move);
        int c = move.getCol0();
        int r = move.getRow0();
        int total = move.length();
        while (1 <= c && c <= M && 1 <= r && r <= M && total > 1) {
            c += d.dc;
            r += d.dr;
            if (get(c, r) == move.movedPiece().opposite()) {
                return true;
            }
            total -= 1;
        }
        c += d.dc;
        r += d.dr;
        if (get(c, r) == move.movedPiece()) {
            return true;
        }
        return false;
    }

    /** The standard initial configuration for Lines of Action. */
    static final Piece[][] INITIAL_PIECES = {
        { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
        { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP }
    };

    /** List of all unretracted moves on this board, in order. */
    private final ArrayList<Move> _moves = new ArrayList<>();
    /** Current side on move. */
    private Piece _turn;
    /** The array representation of the board. */
    private Piece[][] _boardPieces;

    /** An iterator returning the legal moves from the current board. */
    private class MoveIterator implements Iterator<Move> {
        /** Current piece under consideration. */
        private int _c, _r;
        /** Next direction of current piece to return. */
        private Direction _dir;
        /** Next move. */
        private Move _move;

        /** A new move iterator for turn(). */
        MoveIterator() {
            _c = 1; _r = 1; _dir = NOWHERE;
            incr();
        }

        @Override
        public boolean hasNext() {
            return _move != null;
        }

        @Override
        public Move next() {
            if (_move == null) {
                throw new NoSuchElementException("no legal move");
            }

            Move move = _move;
            incr();
            return move;
        }

        @Override
        public void remove() {
        }

        /** Advance to the next legal move. */
        private void incr() {
            _move = null;
            for (; _r <= M; _r += 1) {
                for (; _c <= M; _c += 1) {
                    if (get(_c, _r) == turn()) {
                        while (_dir.succ() != null) {
                            _dir = _dir.succ();
                            int length = pieceCountAlong(_c, _r, _dir);
                            _move = Move.create(_c, _r, length, _dir, board());
                            if (isLegal(_move)) {
                                return;
                            }
                        }
                        if (!isLegal(_move)) {
                            _dir = NOWHERE;
                        }
                    }
                }
                _c = 1;
            }
            _move = null;
        }
    }
}
