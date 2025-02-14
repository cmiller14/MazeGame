public class Cell {
    private boolean wallUp;
    private boolean wallDown;
    private boolean wallLeft;
    private boolean wallRight;
    private boolean inFrontier;
    private boolean inMaze;
    private boolean visited;
    private boolean shortestPath;
    private final int row;
    private final int col;


    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        wallUp = true;
        wallDown = true;
        wallLeft = true;
        wallRight = true;
    }

    public boolean getInMaze() {return inMaze;}
    public boolean getInFrontier() {return inFrontier;}

    public void setInFrontier(boolean inFrontier) {this.inFrontier = inFrontier;}
    public void setInMaze(boolean inMaze) {this.inMaze = inMaze;}

    public boolean getVisited() {return visited;}
    public void setVisited(boolean visited) {this.visited = visited;}

    public boolean getShortestPath() {return shortestPath;}
    public void setShortestPath(boolean shortestPath) {this.shortestPath = shortestPath;}

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
