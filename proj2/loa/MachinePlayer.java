package loa;
import java.util.ArrayList;

/** An automated Player.
 *  @author Chen Meng
 */
class MachinePlayer extends Player {
    /** The number of level to check for a move before MOVES moves. */
    static final int MAXDEPTH1 = 2;
    /** The number of level to check for a move after MOVES moves. */
    static final int MAXDEPTH2 = 3;
    /** The number of moves as the cutoff
      * between the number of depths to use. */
    static final int MOVES = 20;
    /** The dividing line for choosing to move according to contiguity
      * or according to centralness. */
    static final double LOCCUTOFF = 0.5;
    /** The farthest piece's distance on average. */
    static final double LOCAVE = 15.166666666666666;
    /** The weight preference given to contiguity. */
    static final double WEIGHT = 0.5;
    /** To times it with the original double score.*/
    static final int RD = 1000;
    /** The total number of pieces initially. */
    static final double TOTAL = 12;
    /** The number of moves already taken.*/
    private int m = 0;

    /** A MachinePlayer that plays the SIDE pieces in GAME.
      * Tips:
      * 1. Some combinations of moves might lead to the same game state.
      * 2. Game tree pruning.
      */
    MachinePlayer(Piece side, Game game) {
        super(side, game);
    }

    /** Get the name of this player as eitehr machine player or human player.
      * @return String "MachinePlayer"*/
    String getName() {
        return "MachinePlayer";
    }

    @Override
    Move makeMove() {
        Piece side = side();
        Board b = getBoard();
        Move move = (Move) findBestMove(side, b, 0,
            (double) Integer.MIN_VALUE, (double) Integer.MAX_VALUE).get(0);
        m += 1;
        return move;
    }

    /** Evaluate the board according to its contiguity
      * and return a score for Piece side.
      * @param board the board to eval
      * @param side the side to eval for */
    public static double evalContiguity(Board board, Piece side) {
        int length = Board.M;
        boolean[][] test = new boolean[length][length];
        for (int i = 0; i < length; i += 1) {
            for (int j = 0; j < length; j += 1) {
                test[i][j] = false;
            }
        }
        double eval = 0;
        int num = 0;
        int numOfPieces = board.numPieces(side);
        for (int c = 1; c <= length; c += 1) {
            for (int r = 1; r <= length; r += 1) {
                if (board.get(c, r) == side && !test[r - 1][c - 1]) {
                    num = board.continguousMark(c, r, side, test);
                    eval += (num / (double) numOfPieces)
                        * (num / (double) numOfPieces);
                }
            }
        }
        if (eval == 1) {
            return (double) Integer.MAX_VALUE;
        }
        return (eval + WEIGHT) / numOfPieces * TOTAL;
    }

    /** Evaluate the board according to how central the side's pieces are
      * on the board.
      * @param board the board to eval
      * @param side the side to eval for
      * @return double the score of the board and side*/
    public static double evalLoc(Board board, Piece side) {
        int length = Board.M;
        double val = 0;
        double cRow = 9 / 2;
        double cCol = 9 / 2;
        for (int c = 1; c <= length; c += 1) {
            for (int r = 1; r <= length; r += 1) {
                if (board.get(c, r) == side) {
                    val += (c - cCol) * (c - cCol) + (r - cRow) * (r - cRow);
                }
            }
        }
        int numOfPieces = board.numPieces(side);
        double score = LOCAVE - (val / numOfPieces);
        score = score / LOCAVE;
        return score / numOfPieces * TOTAL;
    }

    /** Combine evalContiguity and evalLoc into one eval method.
      * @param board the board to eval
      * @param side the side to eval for
      * @return double the eval of the board and side*/
    public static double eval(Board board, Piece side) {
        double loc = evalLoc(board, side);
        if (loc > LOCCUTOFF) {
            double con = evalContiguity(board, side);
            return con;
        }
        return loc;
    }

    /** Return the score for the board.
      * @param board the board for the score*/
    public double score(Board board) {
        double me = eval(board, side());
        double opponent = eval(board, side().opposite());
        double score = me - opponent;
        return score * RD;
    }

    /** Return the numer of levels to look before predicting the move.*/
    int levels() {
        if (m > MOVES) {
            return MAXDEPTH2;
        } else {
            return MAXDEPTH1;
        }
    }

    /** Find the best move for the Board start according to game tree.
      * @param side the side the best move is found for
      * @param start the board to start with
      * @param depth the level of the current check
      * @param alpha the lower bound
      * @param beta the higher bound
      * @return ArrayList<Object> the first element is the max score
      * and the second element is the move*/
    ArrayList<Object> findBestMove(Piece side, Board start, int depth,
        double alpha, double beta) {
        ArrayList<Object> result = new ArrayList<Object>();
        if (start.gameOver()) {
            return gameOver(result, side);
        }
        if (depth == levels()) {
            ArrayList<Object> test = guessBestMove(start, side, alpha, beta);
            return test;
        }
        Board copy = new Board(start);
        double maxScore = (double) Integer.MIN_VALUE;
        double minScore = (double) Integer.MAX_VALUE;
        Move bestMove = null;
        for (Move mm: copy) {
            if (checkEating(mm, side, copy)) {
                copy.makeMove(mm);
                double score = (double) findBestMove(side.opposite(),
                    copy, depth + 1, alpha, beta).get(1);
                if (score == (double) Integer.MAX_VALUE) {
                    result.add(mm);
                    result.add((double) Integer.MAX_VALUE);
                    copy.retract();
                    return result;
                }
                if (side() == side) {
                    if (score > alpha) {
                        alpha = score;
                    }
                    if (alpha >= beta) {
                        result.add(null);
                        maxScore = (double) Integer.MAX_VALUE - 1;
                        result.add(maxScore);
                        copy.retract();
                        return result;
                    }
                    if (maxScore < score) {
                        bestMove = mm;
                        maxScore = score;
                    }
                } else {
                    if (score < beta) {
                        beta = score;
                    }
                    if (alpha >= beta) {
                        result.add(null);
                        minScore = (double) Integer.MIN_VALUE + 1;
                        result.add(minScore);
                        copy.retract();
                        return result;
                    }
                    if (minScore > score) {
                        bestMove = mm;
                        minScore = score;
                    }
                }
                copy.retract();
            }
        }
        return addMove(result, bestMove, side, maxScore, minScore);
    }

    /** Add the move to the result.
          * @param result the first element is the max score
          * and the second element is the move
          * @param bestMove the move to add to the result
          * @param side the piece to add the move
          * @param maxScore which score to add depending on the side
          * @param minScore which score to add depending on the side
          * @return ArrayList<Object> the first element is the max score
          * and the second element is the move*/
    ArrayList<Object> addMove(ArrayList<Object> result, Move bestMove,
        Piece side, double maxScore, double minScore) {
        result.add(bestMove);
        if (side() == side) {
            result.add(maxScore);
        } else {
            result.add(minScore);
        }
        return result;
    }


    /** Return if the move should be taken based on if it's eating a piece
      * and whether it should eat that piece.
      * @param result the result to return
      * @param side the side to check
      * @return ArrayList<Object> the first element is the max score
      * and the second element is the move*/
    ArrayList<Object> gameOver(ArrayList<Object> result, Piece side) {
        result.add(null);
        if (side() == side) {
            result.add((double) Integer.MIN_VALUE);
            return result;
        } else {
            result.add((double) Integer.MAX_VALUE);
            return result;
        }
    }

    /** Return if the move should be taken based on if it's eating a piece
      * and whether it should eat that piece.
      * @param move the move
      * @param side the side to check
      * @param b the board to act on*/
    boolean checkEating(Move move, Piece side, Board b) {
        if (move.replacedPiece() == side.opposite()) {
            int c = move.getCol1();
            int r = move.getRow1();
            int length = Board.M;
            boolean[][] test = new boolean[length][length];
            for (int i = 0; i < length; i += 1) {
                for (int j = 0; j < length; j += 1) {
                    test[i][j] = false;
                }
            }
            int num = b.continguousMark(c, r, side, test);
            if (num < 4) {
                return false;
            }
        }
        return true;
    }

    /** At THIS's turn, guess the best move based on static evaluation function.
      * Return the score of that board.
      * We can consider to not let the player to eat the opponent's piece.
      * initial alpha = min. initial beta = max.
      * @param board the board to act on
      * @param side the side the best move is for
      * @param alpha the lower bound
      * @param beta the upper bound */
    ArrayList<Object> guessBestMove(Board board,
        Piece side, double alpha, double beta) {
        ArrayList<Object> result = new ArrayList<Object>();
        double maxScore = (double) Integer.MIN_VALUE;
        double minScore = (double) Integer.MAX_VALUE;
        Move bestMove = null;
        for (Move move: board) {
            board.makeMove(move);
            double score = score(board);
            if (side() == side) {
                if (score > alpha) {
                    alpha = score;
                }
                if (alpha >= beta) {
                    result.add(null);
                    maxScore = (double) Integer.MAX_VALUE - 1;
                    result.add(maxScore);
                    board.retract();
                    return result;
                }
                if (maxScore < score) {
                    bestMove = move;
                    maxScore = score;
                }
            } else {
                if (score < beta) {
                    beta = score;
                }
                if (alpha >= beta) {
                    result.add(null);
                    minScore = (double) Integer.MIN_VALUE + 1;
                    result.add(minScore);
                    board.retract();
                    return result;
                }
                if (minScore > score) {
                    bestMove = move;
                    minScore = score;
                }
            }
            board.retract();
        }
        result.add(bestMove);
        if (side() == side) {
            result.add(maxScore);
        } else {
            result.add(minScore);
        }
        return result;
    }

}
