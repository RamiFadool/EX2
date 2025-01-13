import java.util.Arrays;
/**
 * This class represents a cell reference in a spreadsheet, such as "A1" or "B23".
 * It implements the `Index2D` interface to check validation and to extract
 * the coordinates of the referenced cell.
 */
public class CellEntry  implements Index2D {
    // Stores the cell reference as a string
    private String index;

    public CellEntry(String index) {
        this.index = index;
    }

    @Override
    public boolean isValid() {
        if (index == null || index.isEmpty() || index.length() > 3) {
            return false;
        }

        char letter = index.charAt(0); // Check the first character.
        if (!Character.isLetter(letter)) {
            return false;
        }

        String numPart = index.substring(1); // Extract the number part of the reference.
        try {
            int number = Integer.parseInt(numPart);
            if (number < 0 || number > 99) { // Ensure the number is in the valid range.
                return false;
            }
        } catch (NumberFormatException e) {
            return false; // if the number part is not a valid integer.
        }
        return true;
    }

    @Override
    public int getX() {
        if (!isValid()) {
            return Ex2Utils.ERR;
        }
        char column = index.charAt(0);
        int x = Character.toUpperCase(column) - 'A'; // Convert the letter to a column index.

        return x;
    }

    @Override
    public int getY() {
        if (!isValid()) {
            return Ex2Utils.ERR;
        }
        String number = index.substring(1);

        try {
            int y = Integer.parseInt(number); // Convert the number part to an integer.
            return y;
        } catch (NumberFormatException e) {
            return Ex2Utils.ERR; // if the number part is not a valid integer.
        }
    }
}