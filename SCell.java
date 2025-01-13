// Add your documentation below:

public class SCell implements Cell {
    private String line;
    private int type;

    private int order; // The evaluation order for dependent cells.
    private String value; // The computed value of the cell.

    public SCell(String s) {
        setData(s);
        type = whatType(s);
        order = getOrder();
    }

    @Override
    public int getOrder() {
        if (type == Ex2Utils.NUMBER || type == Ex2Utils.TEXT) {
            return 0; // Number and text cells have no dependencies.
        }
        if (type == Ex2Utils.FORM) {
            int maxOrder = 0;
            String formulaData = line.substring(1).trim(); // Extract the formula part.

            String cell = "";
            for (int i = 0; i < formulaData.length(); i++) {
                char current = formulaData.charAt(i);

                if (Character.isLetterOrDigit(current)) {
                    cell += current; // Take cell references.
                }
                else { // If an op
                    if(!cell.isEmpty()){
                        // Check dependencies for the current cell reference.
                        CellEntry index = new CellEntry(cell);
                        if(index.isValid()){
                            int col = index.getX();
                            int row = index.getY();

                            Cell[][] cells = Ex2Sheet.getCells();
                            Cell dependentCell = cells[col][row];

                            if (dependentCell != null) {
                                maxOrder = Math.max(maxOrder, dependentCell.getOrder());
                            }
                        }
                        cell = "";
                    }
                }
            }

            // Check the last cell reference, if there is one.
            CellEntry index = new CellEntry(cell);
            if (!cell.isEmpty() && index.isValid()) {
                int col = index.getX();
                int row = index.getY();

                Cell[][] cells = Ex2Sheet.getCells();
                Cell dependentCell = cells[col][row];

                if (dependentCell != null) {
                    maxOrder = Math.max(maxOrder, dependentCell.getOrder());
                }
            }
            return 1 + maxOrder;
        }
        return 0;
    }

    //@Override
    @Override
    public String toString() {
        return getData();
    }

    @Override
    public void setData(String s) {
        line = s;
    }

    @Override
    public String getData() {
        return line;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int t) {
        type = t;
    }

    @Override
    public void setOrder(int t) {
        order = t;

    }

    /**
     * This helper function determines the type of the cell based on the string input.
     *
     * @param s the content of the cell.
     * @return the type code of the cell's content.
     */
    private int whatType(String s){
        if(s == null || s.isEmpty()){
            return 0; // Empty cell is invalid.
        }
        else
        if(isNumber(s)){
            return Ex2Utils.NUMBER;
        }
        else if(s.charAt(0) == '='){
            return Ex2Utils.FORM;
        }
        if(s.equals(Ex2Utils.ERR_FORM)){
            return Ex2Utils.ERR_FORM_FORMAT;
        }
        if(s.equals(Ex2Utils.ERR_CYCLE)){
            return Ex2Utils.ERR_CYCLE_FORM;
        }
        return Ex2Utils.TEXT;
    }

    /**
     * Checks if a string represents a valid number.
     *
     * @param s the string to check.
     * @return true if the string can be parsed as a number, false otherwise.
     */
    public static boolean isNumber(String s){
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Sets the computed value of the cell.
     *
     * @param value the computed value to set for the cell.
     */
    public void setValue(String value){
        this.value = value;
    }

}