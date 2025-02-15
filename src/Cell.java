import java.util.ArrayList;

public class Cell {
    private boolean wallUp;
    private boolean wallDown;
    private boolean wallLeft;
    private boolean wallRight;
    private boolean inFrontier;
    private boolean inMaze;
    private boolean visited;
    private boolean onShortestPath;
    private Cell parent;
    private final int row;
    private final int col;


    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        wallUp = true;
        wallDown = true;
        wallLeft = true;
        wallRight = true;
        visited = false;
    }

    public boolean getInMaze() {return inMaze;}
    public boolean getInFrontier() {return inFrontier;}

    public void setInFrontier(boolean inFrontier) {this.inFrontier = inFrontier;}
    public void setInMaze(boolean inMaze) {this.inMaze = inMaze;}

    public void setOnShortestPath(boolean onShortestPath) {this.onShortestPath = onShortestPath;}
    public boolean getOnShortestPath() {return this.onShortestPath;}

    public boolean getVisited() {return visited;}
    public void setVisited(boolean visited) {this.visited = visited;}

    public Cell getParent() {return this.parent;}
    public void setParent(Cell parent) {this.parent = parent;}

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

    public ArrayList<Cell> getNeighbors(Cell[][] grid) {
        int rowLength = grid.length;
        int colLength = grid[0].length;
        ArrayList<Cell> neighbors = new ArrayList<>();
        // Up
        if (row - 1 >= 0) {
            neighbors.add(grid[row - 1][col]);
        }
        // Down
        if (row + 1 < rowLength) {
            neighbors.add(grid[row + 1][col]);
        }
        // Left
        if (col - 1 >= 0) {
            neighbors.add(grid[row][col - 1]);
        }
        // Right
        if (col + 1 < colLength) {
            neighbors.add(grid[row][col + 1]);
        }
        return neighbors;
    }

    public ArrayList<Cell> reachableNeighbors(Cell[][] grid) {
        int rowLength = grid.length;
        int colLength = grid[0].length;
        ArrayList<Cell> neighbors = new ArrayList<>();
        // Up
        if (row - 1 >= 0 && !getWallUp()) {
            neighbors.add(grid[row - 1][col]);
        }
        // Down
        if (row + 1 < rowLength && !getWallDown()) {
            neighbors.add(grid[row + 1][col]);
        }
        // Left
        if (col - 1 >= 0 && !getWallLeft()) {
            neighbors.add(grid[row][col - 1]);
        }
        // Right
        if (col + 1 < colLength && !getWallRight()) {
            neighbors.add(grid[row][col + 1]);
        }
        return neighbors;
    }


}
