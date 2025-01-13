import org.junit.Test;
import static org.junit.Assert.*;

public class CellEntryTest {

    @Test
    public void testValidEntries() {
        CellEntry c1 = new CellEntry("A0");
        assertTrue(c1.isValid());
        assertEquals(0, c1.getX());
        assertEquals(0, c1.getY());

        CellEntry c2 = new CellEntry("B5");
        assertTrue(c2.isValid());
        assertEquals(1, c2.getX());
        assertEquals(5, c2.getY());
    }

    @Test
    public void testInvalidEntries() {
        CellEntry[] invalid = {
                new CellEntry(null),
                new CellEntry("123"),
                new CellEntry("AA1"),
                new CellEntry("A100"),
                new CellEntry("1A"),
                new CellEntry("A-1")
        };

        for (CellEntry cell : invalid) {
            assertFalse(cell.isValid());
            assertEquals(Ex2Utils.ERR, cell.getX());
            assertEquals(Ex2Utils.ERR, cell.getY());
        }
    }
}
