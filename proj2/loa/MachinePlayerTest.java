package loa;

import org.junit.Test;
import static org.junit.Assert.*;

import static loa.Piece.*;
import static loa.Direction.*;

/** Test MachinePlayer.java:
  * 1. Get()
  * 2. evalLoc()
  * 3. evalLoc()
  * @ Chen Meng
  */

public class MachinePlayerTest {
    @Test
    public void testGet() {
        Piece[][] contents1 = {
                { EMP, BP,  BP,  BP,  EMP,  EMP,  BP,  EMP },
                { BP,  WP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, BP, EMP, EMP, BP  },
                { EMP, EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, WP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP }
        };
        Board a = new Board(contents1, BP);
        System.out.println(MachinePlayer.evalContiguity(a, BP));
        a = new Board(contents1, WP);
        System.out.println(MachinePlayer.evalContiguity(a, WP));
        Piece[][] contents2 = {
                {  WP, EMP, EMP,  BP, EMP, EMP, EMP, EMP },
                { EMP,  WP,  WP,  BP, EMP, EMP, EMP, EMP },
                { EMP, EMP,  BP,  BP,  WP,  WP, EMP,  WP },
                { EMP,  WP,  BP,  WP,  WP, EMP, EMP, EMP },
                { EMP,  BP,  WP,  BP,  BP,  BP, EMP, EMP },
                { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
                { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
                { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP }
        };
        a = new Board(contents2, BP);
        System.out.println(MachinePlayer.evalContiguity(a, BP));
        a = new Board(contents2, WP);
        System.out.println(MachinePlayer.evalContiguity(a, BP));
    }

    @Test
    public void testevalLoc() {
        Piece[][] contents1 = {
                { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP }
        };
        Board a = new Board(contents1, BP);
        System.out.println("The BP location value is "
            + MachinePlayer.evalLoc(a, BP));
        System.out.println("The WP location value is "
            + MachinePlayer.evalLoc(a, WP));
        Piece[][] contents2 = {
                { EMP, EMP, EMP, EMP,  BP,  BP,  BP,  EMP },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,   BP,  BP,  BP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP }
        };
        Board b = new Board(contents2, BP);
        System.out.println("The BP location value is "
            + MachinePlayer.evalLoc(b, BP));
        System.out.println("The WP location value is "
            + MachinePlayer.evalLoc(b, WP));
        Piece[][] contents3 = {
                { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,   BP,  BP,  BP, EMP, EMP, EMP, WP  },
                { WP,   BP,  BP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP,  BP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP }
        };
        Board c = new Board(contents3, BP);
        System.out.println("The BP location value is "
            + MachinePlayer.evalLoc(c, BP));
        Piece[][] contents4 = {
                { EMP, BP,  BP,  BP,  BP,  BP,  BP,  EMP },
                { WP,  WP,  EMP, EMP, EMP, EMP, EMP, EMP  },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP,  BP, EMP,  BP, EMP, WP  },
                { WP,  BP, EMP,  EMP, EMP, EMP, EMP, WP  },
                { WP,  EMP, EMP, EMP, EMP, EMP, EMP, WP  },
                { EMP, EMP,  BP, EMP,  BP, EMP,  BP, EMP }
        };
        Board d = new Board(contents4, BP);
        System.out.println("The WP location value is "
            + MachinePlayer.evalLoc(d, WP));
    }


    @Test
    public void testevalContiguity() {
        Piece[][] contents = {
                { EMP, EMP, EMP, EMP,  BP, EMP, EMP, EMP },
                { EMP, EMP,  BP, EMP, EMP, EMP, EMP, WP  },
                { EMP,  BP,  BP, EMP, EMP, EMP, EMP, WP  },
                { EMP,  BP, EMP,  BP, EMP,  WP,  WP, WP  },
                { EMP, EMP,  BP, EMP,  WP, EMP, EMP, EMP },
                {  WP,  BP, EMP,  BP,  WP, EMP, EMP, EMP },
                { EMP, EMP, EMP, EMP, EMP,  WP, EMP, EMP },
                { EMP, EMP, EMP, EMP, EMP, EMP, EMP, EMP }
        };
        Board b = new Board(contents, BP);
        MachinePlayer m = new MachinePlayer(BP, new Game());
        System.out.println("testevalContiguity result is "
            + MachinePlayer.evalContiguity(b, BP));
        System.out.println("Find the best move "
            + m.findBestMove(BP, b, 2, 10000, 100000));
    }

    public static void main(String[] args) {
        System.out.print(ucb.junit.textui.runClasses(MachinePlayerTest.class));
    }

}
