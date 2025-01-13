import java.io.*;
import java.util.Arrays;
/**
 * This class represents a spreadsheet that can handle numbers, text, and formulas
 * It stores data in a table of cells and can do math operations
 */
public class Ex2Sheet implements Sheet {
    private static Cell[][] table;
    // The number of rows and columns in the spreadsheet
    private int rows;
    private int columns;

    public Ex2Sheet(int x, int y) {
        table = new SCell[x][y];
        rows = y;
        columns = x;
        // Initialize the table with empty cells
        for(int i=0;i<x;i=i+1) {
            for(int j=0;j<y;j=j+1) {
                table[i][j] = new SCell(Ex2Utils.EMPTY_CELL);
            }
        }
        eval();
    }

    public Ex2Sheet() {
        this(Ex2Utils.WIDTH, Ex2Utils.HEIGHT);
    }

    @Override
    public String value(int x, int y) {
        String ans = Ex2Utils.EMPTY_CELL;
        if (isIn(x, y)) { // Check if the coordinates are within the table
            Cell c = get(x,y);

            if(c != null){
                ans = eval(x ,y);
            }
        }
        return ans;
    }

    @Override
    public Cell get(int x, int y) {
        if (isIn(x, y)) {
            return table[x][y];
        }
        return null; // null if out of bounds
    }


    @Override
    public Cell get(String entry) {
        Cell ans = null;

        if (entry == null || entry.length() < 2) { // Invalid entry
            return ans;
        }

        // Extract the column part (letters)
        String columnStr = "";
        for (int i = 0; i < entry.length(); i++) {
            if (Character.isLetter(entry.charAt(i))) {
                columnStr += entry.charAt(i);
            } else {
                break;
            }
        }
        if (columnStr.length() != 1) { // Only single-letter columns are valid
            return ans;
        }

        int col = Arrays.asList(Ex2Utils.ABC).indexOf(columnStr);
        if (col == -1) { // Invalid column
            return ans;
        }

        // Extract the row part (numbers)
        String rowStr = entry.substring(1);
        int row = -1;

        try {
            row = Integer.parseInt(rowStr);
        } catch (NumberFormatException e) {
            return ans; // Invalid row
        }

        if (row < 0 || row >= rows) {
            return ans;
        }

        ans = get(col, row);
        return ans;
    }

    @Override
    public int width() {
        return table.length;
    }
    @Override
    public int height() {
        return table[0].length;
    }
    @Override
    public void set(int x, int y, String s) {
        Cell c = new SCell(s);
        if(isIn(x,y)){
            table[x][y] = c;
        }
    }

    @Override
    public void eval() {
        int[][] dd = depth();
        int max = rows * columns; // Maximum number of cells

        // Iterate over the cells in depth order
        for (int d = 0; d <= max ; d++) {
            for (int x = 0; x < columns; x++) {
                for (int y = 0; y < rows; y++) {
                    if (dd[x][y] == d) { // If the cell is at the current depth
                        Cell cell = get(x, y);

                        if (cell != null) {
                            String result = eval(x, y); // Evaluate the cell
                            if (cell instanceof SCell) {
                                ((SCell) cell).setValue(result); // Update the cell value
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean isIn(int xx, int yy) {
        return xx >= 0 && yy >= 0 && xx < width() && yy < height();
    }

    @Override
    public int[][] depth() {
        int[][] ans = new int[width()][height()];

        // Initialize all cells to -1
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                ans[i][j] = -1;
            }
        }

        // Calculate depth for each cell
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                if (ans[x][y] == -1) {
                    calculateDepth(x, y, ans, new boolean[width()][height()]);
                }
            }
        }

        // Return the depth of all cells
        return ans;
    }
    /**
     * This function Recursively calculates the depth of a specific cell in the spreadsheet.
     * @param x The column index of the cell.
     * @param y The row index of the cell.
     * @param depths The array storing the depth values for each cell in the spreadsheet.
     * @param visited An array to track visited cells during the depth search, Used to detect cycles.
     * @return The calculated depth of the cell.
     */
    private int calculateDepth(int x, int y, int[][] depths, boolean[][] visited) {
        // If already calculated, return the depth
        if (depths[x][y] != -1) {
            return depths[x][y];
        }

        // Check for cycles
        if (visited[x][y]) {
            depths[x][y] = Ex2Utils.ERR;
            ((SCell)table[x][y]).setType(Ex2Utils.ERR_CYCLE_FORM);
            return Ex2Utils.ERR;
        }

        Cell cell = get(x, y);
        // Empty cell, number, or text -> depth is 0
        if (cell == null || cell.getType() == Ex2Utils.NUMBER || cell.getType() == Ex2Utils.TEXT) {
            depths[x][y] = 0;
            return 0;
        }

        String formula = cell.getData();
        if (!formula.startsWith("=")) {
            depths[x][y] = 0;
            return 0;
        }

        // Remove the "="
        formula = formula.substring(1).trim();
        visited[x][y] = true;
        int maxDepth = -1;

        // calculate the depth for each referenced cell of the formula
        String cellRef = "";
        for (int i = 0; i < formula.length(); i++) {
            char c = formula.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                cellRef += c;
            } else {
                // if current cell ref is valid, calculate the depth of it
                if (!cellRef.isEmpty()) {
                    CellEntry entry = new CellEntry(cellRef);
                    if (entry.isValid() && isIn(entry.getX(), entry.getY())) {
                        int depthOfDep = calculateDepth(entry.getX(), entry.getY(), depths, visited);
                        maxDepth = Math.max(maxDepth, depthOfDep);
                    }
                }
                cellRef = ""; // Reset for the next cell
            }
        }

        // Check last cell reference if exists
        if (!cellRef.isEmpty()) {
            CellEntry entry = new CellEntry(cellRef);
            if (entry.isValid() && isIn(entry.getX(), entry.getY())) {
                int depthOfDep = calculateDepth(entry.getX(), entry.getY(), depths, visited);
                maxDepth = Math.max(maxDepth, depthOfDep);
            }
        }

        visited[x][y] = false; // Mark the current cell as not visited
        if (maxDepth == -1) {
            depths[x][y] = 0;
        } else {
            depths[x][y] = maxDepth + 1;
        }

        return depths[x][y];
    }

    @Override
    public void load(String fileName) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            reader.readLine(); // Skip the first line.
            clearSheet();  // Clear the spreadsheet before loading new data.

            String line;
            // Read the file line by line.
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1); // Split the line into parts by commas.
                if (parts.length >= 3) {
                    try {
                        int x = Integer.parseInt(parts[0].trim()); // Read the x-coordinate.
                        int y = Integer.parseInt(parts[1].trim()); // Read the y-coordinate.
                        String value = parts[2].trim(); //Reead the cell value.
                        // Check if the cell is within the sheet's bounds, if yes, set the value
                        if (isIn(x, y)) {
                            set(x, y, value);
                        }
                    } catch (NumberFormatException ignored) {
                        // Ignore the line if the coordinates are not valid integers.
                    }
                }
            }
        }
    }

    @Override
    public void save(String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Write the header line.
            writer.write("I2CS ArielU: SpreadSheet (Ex2) assignment - this line should be ignored in the load method\n");

            // Loop through all cells and save non-empty ones.
            for (int i = 0; i < width(); i++) {
                for (int j = 0; j < height(); j++) {
                    Cell cell = get(i, j);
                    String value = cell.getData();

                    // Only write cells that are not empty.
                    if (!value.equals(Ex2Utils.EMPTY_CELL)) {
                        writer.write(i + "," + j + "," + value + "\n");
                    }
                }
            }
        }
    }

    /**
     * Clears the entire spreadsheet by resetting all cells to be empty.
     * Used before loading a spreadsheet.
     */
    public void clearSheet() {
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                table[i][j] = new SCell(Ex2Utils.EMPTY_CELL);
            }
        }
    }

    @Override
    public String eval(int x, int y) {
        Cell cell = get(x, y);

        // If the cell is null, return an empty value.
        if (cell == null) {
            return Ex2Utils.EMPTY_CELL;
        }

        // If the cell is a number, return it as a string.
        String data = cell.getData();
        if (cell.getType() == Ex2Utils.NUMBER) {
            try {
                double value = Double.parseDouble(data);
                return String.valueOf(value);
            } catch (NumberFormatException e) {
                return Ex2Utils.ERR_FORM; // Invalid number format.
            }
        }

        // If the cell is text, return it.
        if (cell.getType() == Ex2Utils.TEXT) {
            return data;
        }

        // If the cell contains a formula, evaluate it.
        if (cell.getType() == Ex2Utils.FORM) {
            try {
                double result = eval(cell.getData());
                return String.valueOf(result);
            } catch (IllegalArgumentException e) {
                cell.setType(Ex2Utils.ERR_FORM_FORMAT);
                return Ex2Utils.ERR_FORM;
            } catch (StackOverflowError e) {
                cell.setType(Ex2Utils.ERR_CYCLE_FORM);
                return Ex2Utils.ERR_CYCLE;
            }
        }
        // If the cell type is an error, return the error message.
        if(cell.getType() == Ex2Utils.ERR_CYCLE_FORM) {
            return Ex2Utils.ERR_CYCLE;
        }
        if(cell.getType() == Ex2Utils.ERR_FORM_FORMAT) {
            return Ex2Utils.ERR_FORM;
        }
        return Ex2Utils.EMPTY_CELL;
    }
    /**
     * This function evaluates a string expression and computes its value.
     * It supports the operations (+, -, *, /) and parentheses
     * and references to other cells.
     *
     * @param data the string expression to evaluate. It can be a text, number, a cell reference,
     *             or a formula starting with "=".
     * @return the computed value of the expression as a double.
     * @throws IllegalArgumentException if the expression is invalid.
     */
    private double eval(String data) {
        // If the data starts with '=', remove it
        if (data.startsWith("=")) {
            data = data.substring(1).trim();
        }

        // Check if the data is a number. If yes, parse and return it.
        if (SCell.isNumber(data)) {
            return Double.parseDouble(data);
        }

        // Check if the data is a valid cell reference  (i.e., A0, B1, C12)
        CellEntry cellRef = new CellEntry(data);
        if (cellRef.isValid()) {
            int xx = cellRef.getX(); // Get the row index
            int yy = cellRef.getY(); // Get the column index

            // If the cell is within bounds, evaluate its value using recursion.
            if (isIn(xx, yy)) {
                String cellValue = eval(xx, yy);
                return Double.parseDouble(cellValue);
            }
        }
        // If the data is in parentheses, evaluate the content inside.
        if (data.startsWith("(") && data.endsWith(")")) {
            return eval(data.substring(1, data.length() - 1));
        }

        // Find the main operator in the expression and split it into two parts.
        int opPos = findMainOperator(data);
        if (opPos != -1) {
            String leftPart = data.substring(0, opPos).trim(); // Left part of the expression.
            char operator = data.charAt(opPos); // The operator
            String rightPart = data.substring(opPos + 1).trim(); // Right part of the expression.

            // Recursively evaluate the left and right parts of the expression.
            double left = eval(leftPart);
            double right = eval(rightPart);

            return calculateOperation(left, right, operator);
        }

        // If no valid computation is possible
        throw new IllegalArgumentException();
    }

    /**
     * Finds the position of the main operator.
     * The main operator is the one with the lowest precedence that is not inside parentheses.
     *
     * @param s expression as a string.
     * @return the index of the main operator, or -1 if no operator is found.
     */
    public int findMainOperator(String s) {
        int brackets = 0;
        int lastOperator = -1; // Stores the position of the last operator.

        // Loop through each character in the expression.
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == '('){
                brackets++;
            }
            else if (c == ')'){
                brackets--;
            }
            // If not inside parentheses, check for operators.
            else if (brackets == 0){
                if (c == '+' || c == '-') {
                    lastOperator = i; // Addition and subtraction have the lowest precedence.
                } else if ((c == '*' || c == '/') && lastOperator == -1) {
                    lastOperator = i; // Multiplication and division have higher precedence.
                }
            }
        }
        return lastOperator;
    }

    /**
     * calculates the given math operation.
     *
     * @param left the left expression.
     * @param right the right expression.
     * @param operator the operator character.
     * @return the result of the operation.
     * @throws IllegalArgumentException if the operator is invalid or if there is a division by zero.
     */
    private double calculateOperation(double left, double right, char operator) {
        switch (operator) {
            case '+': return left + right;  // Add
            case '-': return left - right;  // Sub
            case '*': return left * right;  // Multi
            case '/':
                if (right == 0){ throw new IllegalArgumentException();}
                return left / right; //Divide
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * Returns the table of cells in the spreadsheet.
     *
     * @return a 2D array of cells.
     */
    public static Cell[][] getCells(){
        return table;
    }
}