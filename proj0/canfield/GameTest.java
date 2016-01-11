package canfield;

import static org.junit.Assert.*;
import org.junit.Test;

/** Tests of the Game class.
 *  @author
 */

public class GameTest {

    /** Example. */
    @Test
    public void testInitialScore() {
        Game g = new Game();
        g.deal();
        assertEquals(5, g.getScore());
    }

    @Test
    public void testundo() {
        Game g = new Game();
        g.deal();
        Game g1 = new Game(g);
        g.stockToWaste();
        Game g2 = new Game(g);
        g.stockToWaste();
        g.undo();
        assertEquals(g2.topWaste(), g.topWaste());
        g.undo();
        assertEquals(g1.topWaste(), g.topWaste());
        g.undo();
        assertEquals(g1.topWaste(), g.topWaste());
    }

}
