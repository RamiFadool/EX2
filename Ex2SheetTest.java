import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Ex2SheetTest {
    private Ex2Sheet sheet;

    @BeforeEach
    public void setUp() {
        sheet = new Ex2Sheet(9, 17); //Create a 9x17 sheet
    }

    @Test
    public void testSheetInitialization() {
        // Test that all cells are initialized to empty
        assertEquals(Ex2Utils.EMPTY_CELL, sheet.value(0, 0));
        assertEquals(Ex2Utils.EMPTY_CELL, sheet.value(1, 1));
        assertEquals(Ex2Utils.EMPTY_CELL, sheet.value(2, 2));
    }

    @Test
    public void testValue() {
        // Test setting and getting cell values
        sheet.set(0, 0, "1211");
        assertEquals("1211.0", sheet.value(0, 0));

        sheet.set(1, 1, "Hello");
        assertEquals("Hello", sheet.value(1, 1));

        sheet.set(2, 2, "=1+2");
        assertEquals("3.0", sheet.value(2, 2));
    }

    @Test
    public void testGet() {
        // Test setting and getting cell values
        sheet.set(0, 0, "100");
        Cell c = sheet.get(0, 0);
        assertEquals("100", c.getData());
        assertEquals(2, c.getType());
        assertEquals(0, c.getOrder());

        sheet.set(1, 1, "Rami");
        Cell c1 = sheet.get(1, 1);
        assertEquals("Rami", c1.getData());
        assertEquals(1, c1.getType());
        assertEquals(0, c1.getOrder());

        sheet.set(2, 2, "=1+2");
        Cell c2 = sheet.get(2, 2);
        assertEquals("=1+2", c2.getData());
        assertEquals(3, c2.getType());
        assertEquals(1, c2.getOrder());


        sheet.set(3, 3, "Hello");
        Cell c3 = sheet.get("D3");
        Cell c4 = sheet.get(3, 3);
        assertEquals(c3.getData(), c4.getData());
    }

    @Test
    public void testVoidEval(){
        // Test double evaluation
        sheet.set(0, 0, "10");
        sheet.set(1, 0, "=A0+5");
        sheet.eval();
        assertEquals("15.0", sheet.value(1, 0));
    }

    @Test
    public void testDepth(){

        sheet.clearSheet();

        sheet.set(0, 0, "10");
        sheet.set(1, 0, "=A0+5");
        sheet.set(2, 0, "=2+10");
        sheet.set(3, 0, "Hello");

        int[][] result = sheet.depth();
        //Check the size of the result array
        assertEquals(sheet.width(), result.length);
        assertEquals(sheet.height(), result[0].length);

        assertEquals(0, result[0][0]);
        assertEquals(1, result[1][0]);
        assertEquals(0, result[2][0]);
        assertEquals(0, result[3][0]);
    }

    @Test
    public void testClearSheet() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);

        sheet.set(0, 0, "10");
        sheet.set(1, 1, "Hello");
        sheet.set(2, 2, "=A1+1");

        sheet.clearSheet();

        // Verify all cells are empty
        for (int i = 0; i < sheet.width(); i++) {
            for (int j = 0; j < sheet.height(); j++) {
                assertEquals(Ex2Utils.EMPTY_CELL, sheet.get(i, j).getData());
            }
        }
    }

    @Test
    public void testStringEval() {
        Ex2Sheet sheet = new Ex2Sheet(3, 3);

        // Test number
        sheet.set(0, 0, "29");
        assertEquals("29.0", sheet.eval(0, 0));

        // Test text
        sheet.set(0, 1, "Hello");
        assertEquals("Hello", sheet.eval(0, 1));

        // Test formula
        sheet.set(1, 0, "=2+3");
        assertEquals("5.0", sheet.eval(1, 0));

        // Test cell reference in formula
        sheet.set(1, 1, "=A0+1");  // References cell (0,0) which contains 42
        assertEquals("30.0", sheet.eval(1, 1));

        // Test form error
        sheet.set(2, 0, "=5**");
        assertEquals(Ex2Utils.ERR_FORM, sheet.eval(2, 0));

        // Test null
        assertEquals(Ex2Utils.EMPTY_CELL, sheet.eval(5, 5));
    }

    @Test
    public void testDoubleEval() {
        sheet.clearSheet();

        sheet.set(0, 0, "10");  // A0 = 10
        sheet.set(0, 1, "20");  // A1 = 20

        // Test formulas
        sheet.set(1, 0, "=2+3");
        assertEquals("5.0", sheet.eval(1, 0));

        sheet.set(1, 1, "=A0*2");
        assertEquals("20.0", sheet.eval(1, 1));

        sheet.set(1, 2, "=(A0+A1)/2");
        assertEquals("15.0", sheet.eval(1, 2));

        sheet.set(2, 0, "=A0+A1+5");
        assertEquals("35.0", sheet.eval(2, 0));
    }

    @Test
    public void testFindMainOperator() {
        assertEquals(1, sheet.findMainOperator("2+3"));
        assertEquals(1, sheet.findMainOperator("2-3"));
        assertEquals(1, sheet.findMainOperator("2*3"));
        assertEquals(1, sheet.findMainOperator("2/3"));

        assertEquals(3, sheet.findMainOperator("2*3+4"));  // Should find '+'


        assertEquals(5, sheet.findMainOperator("(2+3)+4"));  // Should find last '+'
        assertEquals(-1, sheet.findMainOperator("(2+3)"));   // No operator outside the parentheses

    }


}
