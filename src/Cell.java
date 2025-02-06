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

    public void setWallUp(boolean state) {wallUp = state;}
    public void setWallDown(boolean state) {wallDown = state;}
    public void setWallLeft(boolean state) {wallLeft = state;}
    public void setWallRight(boolean state) {wallRight = state;}
    public boolean getWallUp() {return wallUp;}
    public boolean getWallDown() {return wallDown;}
    public boolean getWallRight() {return wallRight;}
    public boolean getWallLeft() {return wallLeft;}


}
