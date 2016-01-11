package db61b;
import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.*;
/** Test basic functionalities":
* 1. The Table class
* 2. The Conditions
* */

public class BasicTests {

    @Test
    public void testTableColumnIndex() {
        Table t = new Table("Test",
            new String[]{"1", "2", "3", "4", "5"});
        assertEquals(3, t.columnIndex("4"));
        assertEquals(-1, t.columnIndex("10"));
    }

    @Test
    public void testTableSize() {
        Table t = new Table("Test",
            new String[]{"1", "2", "3", "4", "5"});
        boolean b = t.add(new Row(
            new String[]{"one", "two", "three", "four", "five"}));
        assertEquals(1, t.size());
        assertEquals(true, b);
        b = t.add(new Row(new String[]
            {"sechs", "sieben", "acht", "neun", "zehn"}));
        assertEquals(2, t.size());
        assertEquals(true, b);
        b = t.add(new Row(new String[]
            {"sechs", "sieben", "acht", "neun", "zehn"}));
        assertEquals(2, t.size());
        assertEquals(false, b);
    }

    @Test
    public void testTablePrint() {
        Table t = new Table("Test",
            new String[]{"1", "2", "3", "4", "5"});
        t.add(new Row(new String[]
            {"one", "two", "three", "four", "five"}));
        t.add(new Row(new String[]
            {"sechs", "sieben", "acht", "neun", "zehn"}));
        t.print();
    }

    @Test
    public void testTableReadTable() {
        Table t = Table.readTable("schedule");
        t.print();
    }

    @Test
    public void testTableInterator() {
        Table t = new Table("Test",
            new String[]{"1", "2", "3", "4", "5"});
        t.add(new Row(new String[]
            {"one", "two", "three", "four", "five"}));
        t.add(new Row(new String[]
            {"sechs", "sieben", "acht", "neun", "zehn"}));
        t.add(new Row(new String[]
            {"11", "12", "13", "14", "15"}));
        TableIterator tableite = t.tableIterator();
        Column c1 = new Column(t, "1");
        Column c2 = new Column(t, "2");
        ArrayList<TableIterator> tables =
            new ArrayList<TableIterator>();
        tables.add(tableite);
        c1.resolve(tables);
        c2.resolve(tables);
        while (tableite.hasRow()) {
            System.out.println("c1: " + c1.value());
            System.out.println("c2: " + c2.value());
            tableite.next();
        }
    }

    @Test
    public void testConditions() {
        Table t = new Table("Test",
            new String[]{"1", "2", "3", "4", "5"});
        t.add(new Row(new String[]
            {"one", "two", "three", "four", "five"}));
        t.add(new Row(new String[]
            {"sechs", "sieben", "acht", "neun", "zehn"}));
        TableIterator tableite = t.tableIterator();
        Column c1 = new Column(t, "1");
        Column c2 = new Column(t, "2");
        ArrayList<TableIterator> tables =
            new ArrayList<TableIterator>();
        tables.add(tableite);
        c1.resolve(tables);
        c2.resolve(tables);
        Condition c = new Condition(c1, "<=", c2);
        assertTrue(c.test());
        c = new Condition(c1, "=", c2);
        assertFalse(c.test());
    }

    @Test
    public void testTableWriteTable() {
        Table t = new Table("Test",
            new String[]{"1", "2", "3", "4", "5"});
        t.add(new Row(new String[]
            {"one", "two", "three", "four", "five"}));
        t.add(new Row(new String[]
            {"sechs", "sieben", "acht", "neun", "zehn"}));
        t.writeTable(t.name());
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(BasicTests.class));
    }
}
