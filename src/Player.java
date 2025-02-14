import edu.usu.graphics.Rectangle;

public class Player {
    private final Maze maze;
    private int row;
    private int col;
    private float cellSize;
    private final int maxRow;
    private final int maxCol;
    private final Rectangle playerRec;
    final float PLAYER_LEFT = -0.4935f;
    private float playerSize;
    final float PLAYER_TOP = -0.4935f;

    public Player(int row, int col, Maze maze) {
        this.row = row;
        this.col = col;
        this.maze = maze;
        this.maxCol = maze.getCols();
        this.maxRow = maze.getRows();
        this.cellSize = maze.getCellSize();
        this.playerSize = this.cellSize * (30/48f);
        float top = PLAYER_TOP + row * playerSize;
        float left = PLAYER_LEFT + col * playerSize;
        this.playerRec = new Rectangle(left, top, playerSize, playerSize, 0.2f);
    }

    public int getRow() {return row;}
    public int getCol() {return col;}

    public Rectangle getRect() {return this.playerRec;}

    public void moveUp(float distance) {
        if (this.row > 0 && maze.wallNotExists(this.row, this.col, "up")) {
            this.row--;
            playerRec.top = PLAYER_TOP + this.row  * cellSize;
        }
    }

    public void moveDown(float distance) {
        if (this.row < this.maxRow - 1 && maze.wallNotExists(this.row, this.col, "down")) {
            this.row++;
            playerRec.top = PLAYER_TOP + this.row  * cellSize;
        }
    }

    public void moveLeft(float distance) {
        if (this.col > 0 && maze.wallNotExists(this.row, this.col, "left")) {
            this.col--;
            playerRec.left = PLAYER_LEFT + this.col * cellSize;
        }
    }

    public void moveRight(float distance) {
        if (this.col < this.maxCol - 1 && maze.wallNotExists(this.row, this.col, "right")) {
            this.col++;
            playerRec.left = PLAYER_LEFT + this.col * cellSize;
        }
    }
}
