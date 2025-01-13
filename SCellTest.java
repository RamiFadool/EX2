import org.junit.Test;
import static org.junit.Assert.*;

public class SCellTest {

    @Test
    public void testCellTypes() {
        // Test number
        SCell numberCell = new SCell("42");
        assertEquals(Ex2Utils.NUMBER, numberCell.getType());
        assertEquals("42", numberCell.getData());

        // Test text
        SCell textCell = new SCell("Hello");
        assertEquals(Ex2Utils.TEXT, textCell.getType());
        assertEquals("Hello", textCell.getData());

        // Test formula
        SCell formulaCell = new SCell("=1+2");
        assertEquals(Ex2Utils.FORM, formulaCell.getType());
        assertEquals("=1+2", formulaCell.getData());

        // Test empty cell
        SCell emptyCell = new SCell("");
        assertEquals(0, emptyCell.getType());

        // Test Format Error
        SCell formErrorCell = new SCell(Ex2Utils.ERR_FORM);
        assertEquals(Ex2Utils.ERR_FORM_FORMAT, formErrorCell.getType());

        //Test Cycle Error
        SCell cycleErrorCell = new SCell(Ex2Utils.ERR_CYCLE);
        assertEquals(Ex2Utils.ERR_CYCLE_FORM, cycleErrorCell.getType());
    }

    @Test
    public void testIsNumber() {
        assertTrue(SCell.isNumber("42"));
        assertTrue(SCell.isNumber("3.14"));
        assertTrue(SCell.isNumber("-123"));
        assertFalse(SCell.isNumber("abc"));
    }
}
