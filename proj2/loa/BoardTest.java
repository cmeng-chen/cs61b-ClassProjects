package loa;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Iterator;
import static loa.Piece.*;
import static loa.Direction.*;

/** Test Board.java:
  * 1. Get()
  * 2. Set()
  * 3. pieceCountAlong()
  * 4. pieceCountAlong(Move)
  * 5. blocked()
  * 6. isLegal()
  * 7. piecesContiguous()
  * @ Chen Meng
  */

public class BoardTest {
    @Test
    public void testGet() {
        Piece[][] contents = {
                { EMP, BP,  BP,  BP,  EMP,  EMP,  BP,  EMP },
                { BP,  WP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, BP, EMP, EMP, BP  },
                { EMP, EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, WP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP }
        };
        Board a = new Board(contents, BP);
        assertEquals(BP, a.get(1, 2));
        assertEquals(WP, a.get(4, 6));
        assertEquals(EMP, a.get(8, 1));
        assertEquals(BP, a.get(8, 3));
        assertEquals(EMP, a.get(2, 7));
    }

    @Test
    public void testSet() {
        Piece[][] contents = {
                { EMP, BP,  BP,  BP,  EMP,  EMP,  BP,  EMP },
                { BP,  WP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, BP, EMP, EMP, BP  },
                { EMP, EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, WP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP }
        };
        Board a = new Board(contents, BP);
        a.set(2, 1, EMP);
        assertEquals(EMP, a.get(2, 1));
        a.set(2, 1, WP);
        assertEquals(WP, a.get(2, 1));
        a.set(8, 1, WP);
        assertEquals(WP, a.get(8, 1));
    }

    @Test
    public void testPieceCountAlong() {
        Piece[][] contents = {
                { EMP, BP,  BP,  BP,  EMP,  EMP,  BP,  EMP },
                { BP,  WP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, BP, EMP, EMP, BP  },
                { EMP, EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, WP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP }
        };
        Board a = new Board(contents, BP);
        int n = a.pieceCountAlong(2, 1, NE);
        assertEquals(2, n);
        n = a.pieceCountAlong(4, 6, N);
        assertEquals(3, n);
        n = a.pieceCountAlong(1, 7, S);
        assertEquals(5, n);
    }

    @Test
    public void testPieceCountAlong2() {
        Piece[][] contents = {
                { EMP, BP,  BP,  BP,  EMP, EMP, BP,  EMP },
                { BP,  WP,  EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, BP,  EMP, EMP, BP  },
                { EMP, EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, WP,  EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP }
        };
        Board a = new Board(contents, BP);
        Move m = Move.create(2, 2, 2, 5, a);
        int n = a.pieceCountAlong(m);
        assertEquals(3, n);
        m = Move.create(1, 6, 3, 8, a);
        n = a.pieceCountAlong(m);
        assertEquals(2, n);
        m = Move.create(3, 1, 8, 6, a);
        n = a.pieceCountAlong(m);
        assertEquals(3, n);
    }

    @Test
    public void testBlocked() {
        Piece[][] contents = {
                { EMP, BP,  BP,  BP,  EMP,  EMP,  BP,  EMP },
                { BP,  WP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, BP, EMP, EMP, BP  },
                { EMP, EMP, EMP, BP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  WP, EMP, WP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { EMP, BP,  EMP,  WP,  BP,  BP,  BP,  EMP }
        };
        Board a = new Board(contents, BP);
        Move m = Move.create(1, 5, 3, 7, a);
        assertFalse(a.blocked(m));
        m = Move.create(5, 8, 3, 8, a);
        assertTrue(a.blocked(m));
    }

    @Test
    public void testisLegal() {
        Piece[][] contents = {
                { EMP, BP,  BP,  BP,  EMP, EMP, BP,  EMP },
                { BP,  WP,  EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, BP,  EMP, EMP, BP  },
                { EMP, EMP, EMP, BP,  EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  WP,  EMP, WP,  EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { EMP, BP,  EMP, WP,  BP,  BP,  BP,  EMP }
        };
        Board a = new Board(contents, BP);
        Move m = Move.create(1, 5, 3, 7, a);
        assertFalse(a.isLegal(m));
        m = Move.create(5, 8, 3, 8, a);
        assertFalse(a.isLegal(m));
        m = Move.create(3, 1, 6, 4, a);
        assertTrue(a.isLegal(m));
    }

    @Test
    public void testpiecesContiguous() {
        Piece[][] contents1 = {
                { EMP, EMP, EMP,  BP, EMP, EMP, EMP, EMP },
                { EMP,  WP,  WP,  BP, EMP, EMP, EMP, EMP },
                { EMP, EMP,  BP,  BP,  WP,  WP, EMP,  WP },
                { EMP,  WP,  BP,  WP,  WP, EMP, EMP, EMP },
                { EMP,  BP,  WP,  BP,  BP,  BP, EMP, EMP },
                { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
                { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
                { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP }
        };
        Board a = new Board(contents1, BP);
        assertTrue(a.piecesContiguous(BP));
        assertFalse(a.piecesContiguous(WP));
        Piece[][] contents2 = {
                { EMP, EMP, EMP,  BP, EMP, EMP,  BP, EMP },
                { EMP, EMP,  BP, EMP,  BP, EMP, EMP, EMP },
                { EMP, EMP,  BP,  BP,  WP,  WP, EMP, EMP },
                { EMP,  WP,  WP,  WP,  WP, EMP, EMP, EMP },
                { EMP,  WP,  BP, EMP,  BP,  BP, EMP, EMP },
                { EMP,  BP, EMP, EMP, EMP, EMP,  BP, EMP },
                { EMP,  BP, EMP, EMP, EMP, EMP, EMP, EMP },
                { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP }
        };
        Board b = new Board(contents2, BP);
        assertFalse(b.piecesContiguous(BP));
        assertTrue(b.piecesContiguous(WP));
    }

    @Test
    public void testMoveIterator() {
        Piece[][] contents1 = {
                {  WP, EMP, EMP,  BP, EMP, EMP, EMP, EMP },
                { EMP,  WP,  WP,  BP, EMP, EMP, EMP, EMP },
                { EMP, EMP,  BP,  BP,  WP,  WP, EMP,  WP },
                { EMP,  WP,  BP,  WP,  WP, EMP, EMP, EMP },
                { EMP,  BP,  WP,  BP,  BP,  BP, EMP, EMP },
                { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
                { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
                { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP }
        };
        Board a = new Board(contents1, BP);
        Iterator<Move> itr = a.iterator();
        while (itr.hasNext()) {
            System.out.println(itr.next());
        }
        Piece[][] contents2 = {
                {  BP,  BP, EMP,  EMP, BP, EMP, EMP, EMP },
                { EMP, EMP,  WP,  BP, EMP, EMP, EMP, EMP },
                { EMP, EMP,  BP,  BP,  WP,  WP, EMP,  WP },
                { EMP,  WP,  BP, EMP,  WP, EMP, EMP, EMP },
                {  WP,  BP,  WP,  BP,  BP,  BP, EMP, EMP },
                {  WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
                { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
                { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP }
        };
        Board b = new Board(contents2, BP);
        itr = b.iterator();
        while (itr.hasNext()) {
            System.out.println(itr.next());
        }
    }

    @Test
    public void testEquals() {
        Piece[][] contents1 = {
                {  WP, EMP, EMP,  BP, EMP, EMP, EMP, EMP },
                { EMP,  WP,  WP,  BP, EMP, EMP, EMP, EMP },
                { EMP, EMP,  BP,  BP,  WP,  WP, EMP,  WP },
                { EMP,  WP,  BP,  WP,  WP, EMP, EMP, EMP },
                { EMP,  BP,  WP,  BP,  BP,  BP, EMP, EMP },
                { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
                { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
                { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP }
        };
        Board a = new Board(contents1, BP);
        Piece[][] contents2 = {
                {  BP,  BP, EMP,  EMP, BP, EMP, EMP, EMP },
                { EMP, EMP,  WP,  BP, EMP, EMP, EMP, EMP },
                { EMP, EMP,  BP,  BP,  WP,  WP, EMP,  WP },
                { EMP,  WP,  BP, EMP,  WP, EMP, EMP, EMP },
                {  WP,  BP,  WP,  BP,  BP,  BP, EMP, EMP },
                {  WP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
                { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
                { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP }
        };
        Board b = new Board(contents2, BP);
        assertFalse(a.equals(b));
        Board c = new Board(contents1, WP);
        assertFalse(a.equals(c));
        Board d = new Board(contents2, BP);
        assertTrue(b.equals(d));
    }

    public static void main(String[] args) {
        System.out.print(ucb.junit.textui.runClasses(BoardTest.class));
    }

}
