import java.util.ArrayList;

public class Cell {
    private boolean wallUp;
    private boolean wallDown;
    private boolean wallLeft;
    private boolean wallRight;
    private boolean inFrontier;
    private final int row;
    private final int col;
    private final int gridX;
    private final int gridY;

    public Cell(int row, int col, int gridX, int gridY) {
        this.row = row;
        this.col = col;
        this.gridX = gridX;
        this.gridY = gridY;
        wallUp = true;
        wallDown = true;
        wallLeft = true;
        wallRight = true;
        inFrontier = false;
    }
    public int getRow() {return row;}
    public int getCol() {return col;}

}
