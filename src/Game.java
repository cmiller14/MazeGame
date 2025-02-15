import edu.usu.graphics.*;

import java.util.ArrayList;
import java.util.Stack;

import static org.lwjgl.glfw.GLFW.*;

public class Game {
    private final Graphics2D graphics;
    private final KeyboardInput inputKeyboard;

    private Maze maze;
    private Player player;
    private ShortestPath shortestPath;
    private Cell previousPos;
    private double time;
    private Font fontTime;
    private Font fontInstructions;
    private int score;

    private boolean showBreadCrumbs;
    private boolean showShortestPath;
    private boolean playerMoved;
    private boolean showHint;

    private static final float SPRITE_MOVE_RATE_PER_SECOND = 0.40f;

    public Game(Graphics2D graphics) {
        this.graphics = graphics;
        this.inputKeyboard = new KeyboardInput(graphics.getWindow());
    }

    public void initialize() {
        createMaze(20,20, 1/25.0f);
        initializeKeyboardInput();
        fontTime = new Font("resources/fonts/Roboto-Regular.ttf", 36, false);
        fontInstructions = new Font("resources/fonts/Roboto-Regular.ttf", 24, false);
    }

    private void initializeKeyboardInput() {
        // Register the inputs we want to have invoked
        inputKeyboard.registerCommand(GLFW_KEY_W, true, (double elapsedTime) -> player.moveUp((float) elapsedTime * SPRITE_MOVE_RATE_PER_SECOND));
        inputKeyboard.registerCommand(GLFW_KEY_S, true, (double elapsedTime) -> player.moveDown((float) elapsedTime * SPRITE_MOVE_RATE_PER_SECOND));
        inputKeyboard.registerCommand(GLFW_KEY_A, true, (double elapsedTime) -> player.moveLeft((float) elapsedTime * SPRITE_MOVE_RATE_PER_SECOND));
        inputKeyboard.registerCommand(GLFW_KEY_D, true, (double elapsedTime) -> player.moveRight((float) elapsedTime * SPRITE_MOVE_RATE_PER_SECOND));
        inputKeyboard.registerCommand(GLFW_KEY_UP, true, (double elapsedTime) -> player.moveUp((float) elapsedTime * SPRITE_MOVE_RATE_PER_SECOND));
        inputKeyboard.registerCommand(GLFW_KEY_DOWN, true, (double elapsedTime) -> player.moveDown((float) elapsedTime * SPRITE_MOVE_RATE_PER_SECOND));
        inputKeyboard.registerCommand(GLFW_KEY_LEFT, true, (double elapsedTime) -> player.moveLeft((float) elapsedTime * SPRITE_MOVE_RATE_PER_SECOND));
        inputKeyboard.registerCommand(GLFW_KEY_RIGHT, true, (double elapsedTime) -> player.moveRight((float) elapsedTime * SPRITE_MOVE_RATE_PER_SECOND));
        inputKeyboard.registerCommand(GLFW_KEY_B, true, (double elapsedTime) -> showBreadCrumbs = !showBreadCrumbs);
        inputKeyboard.registerCommand(GLFW_KEY_F1, true, (double elapsedTime) -> createMaze(5,5, 1/10f));
        inputKeyboard.registerCommand(GLFW_KEY_F2, true, (double elapsedTime) -> createMaze(10,10, 1/15f));
        inputKeyboard.registerCommand(GLFW_KEY_F3, true, (double elapsedTime) -> createMaze(15,15, 1/20f));
        inputKeyboard.registerCommand(GLFW_KEY_F4, true, (double elapsedTime) -> createMaze(20,20, 1/25f));
        inputKeyboard.registerCommand(GLFW_KEY_F7, true, (double elapsedTime) -> createMaze(25,25, 1/30.0f));
        inputKeyboard.registerCommand(GLFW_KEY_P, true, (double elapsedTime) -> showShortestPath = !showShortestPath);
        inputKeyboard.registerCommand(GLFW_KEY_H, true, (double elapsedTime) -> showHint = !showHint);
    }

    public void shutdown() {
    }

    public void run() {
        // Grab the first time
        double previousTime = glfwGetTime();

        while (!graphics.shouldClose()) {
            double currentTime = glfwGetTime();
            double elapsedTime = currentTime - previousTime;    // elapsed time is in seconds
            previousTime = currentTime;
            processInput(elapsedTime);
            update(elapsedTime);
            render(elapsedTime);
        }
    }

    private void processInput(double elapsedTime) {
        // Poll for window events: required in order for window, keyboard, etc. events are captured.
        glfwPollEvents();

        // If user presses ESC, then exit the program
        if (glfwGetKey(graphics.getWindow(), GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwSetWindowShouldClose(graphics.getWindow(), true);
        }
        inputKeyboard.update(elapsedTime);

    }

    private void update(double elapsedTime) {
        updateTime(elapsedTime);
        updatePlayerMoved();
        if (playerMoved) {
            updateScore();
            shortestPath.updateShortestPathStack(maze.getCell(player.getRow(),player.getCol()), previousPos);
        }
        updateVisited();
        previousPos = maze.getCell(player.getRow(),player.getCol());
    }

    private void updateScore() {
        // if the move is not visited
        if (!maze.getCell(player.getRow(),player.getCol()).getVisited()) {
            // on the shortest path add 5
            if (maze.getCell(player.getRow(),player.getCol()).getOnShortestPath()) {
                score += 5;
                return;
            }
            ArrayList<Cell> neighbors = maze.getCell(player.getRow(),player.getCol()).getNeighbors(this.maze.getGrid());
            for (Cell neighbor : neighbors) {
                // one away from the shortest path then subtract 1
                if (neighbor.getOnShortestPath()) {
                    score -= 1;
                    return;
                }
            }
            // two or more away from the shortest path then subtract 1
            score -= 2;
        }

    }

    private void updateTime(double elapsedTime) {
        time = time + elapsedTime;
    }

    private void updatePlayerMoved() {
        playerMoved = !maze.getCell(player.getRow(), player.getCol()).equals(previousPos);
    }

    private void updateVisited() {
        maze.getCell(player.getRow(),player.getCol()).setVisited(true);
    }

    private void render(double elapsedTime) {
        graphics.begin();

        renderAllCells();
        renderPlayer();
        renderBreadCrumbs();
        renderShortestPath();
        renderHint();
        renderTime();
        renderInstructions();
        renderScore();

        graphics.end();
    }

    private void renderScore() {
        String scoreString = String.format("Score: %02d", score);
        graphics.drawTextByHeight(fontTime,  scoreString, 0.6f, -0.46f, 0.050f, Color.WHITE);
    }

    private void renderInstructions() {
        float top = -0.5f;
        graphics.drawTextByWidth(fontTime, "Instructions:", -1.0f, top, 0.30f, Color.WHITE);
        top += 0.08f;
        graphics.drawTextByWidth(fontInstructions, "Use arrow keys or awsd to navigate", -1.0f, top, 0.40f, Color.WHITE);
        top += 0.06f;
        graphics.drawTextByWidth(fontInstructions, "F1 - New Game 5x5 ", -1.0f, top, 0.30f, Color.WHITE);
        top += 0.06f;
        graphics.drawTextByWidth(fontInstructions, "F2 - New Game 10x10 ", -1.0f, top, 0.30f, Color.WHITE);
        top += 0.06f;
        graphics.drawTextByWidth(fontInstructions, "F3 - New Game 15x15 ", -1.0f, top, 0.30f, Color.WHITE);
        top += 0.06f;
        graphics.drawTextByWidth(fontInstructions, "F4 - New Game 20x20 ", -1.0f, top, 0.30f, Color.WHITE);
        top += 0.06f;
        graphics.drawTextByWidth(fontInstructions, "F5 - Display High Scores ", -1.0f, top, 0.30f, Color.WHITE);
        top += 0.06f;
        graphics.drawTextByWidth(fontInstructions, "F6 - Display Credits", -1.0f, top, 0.30f, Color.WHITE);
    }

    private void renderTime() {

        // Convert to minutes, seconds, and milliseconds
        int minutes = (int) (time / 60);       // Get whole minutes
        int seconds = (int) (time % 60);       // Get whole seconds
        int milliseconds = (int) ((time % 1) * 1000); // Get milliseconds

        // Format time as 00:00:00 (mm:ss:SSS)
        String formattedTime = String.format("Time: %02d:%02d:%03d", minutes, seconds, milliseconds);
        graphics.drawTextByHeight(fontTime, formattedTime, 0.6f, -0.50f, 0.05f, Color.WHITE);
    }

    private void createMaze(int x, int y, float cellSize) {
        this.maze = new Maze(x, y, cellSize);
        this.shortestPath = new ShortestPath(maze);
        this.shortestPath.findShortestPath(maze.getRows()-1,maze.getCols()-1);
        this.player = new Player(0,0, maze);
        this.showBreadCrumbs = false;
        this.time = 0.0;
        this.previousPos = maze.getCell(0,0);
        this.score = 0;
    }

    private void renderHint() {
        final float HINT_LEFT = -0.5f;
        final float HINT_TOP = -0.5f;
        final float HINT_SIZE = this.maze.getCellSize();
        if (showHint) {
            Cell cell = shortestPath.getHint();
            float left = HINT_LEFT + cell.getCol() * HINT_SIZE;
            float top = HINT_TOP + cell.getRow() * HINT_SIZE;
            Rectangle r = new Rectangle(left, top, HINT_SIZE, HINT_SIZE, 0.1f);
            graphics.draw(r, Color.WHITE);
        }
    }

    private void renderBreadCrumbs() {
        final float BREAD_CRUMB_LEFT = -0.5f;
        final float BREAD_CRUMB_TOP = -0.5f;
        final float BREAD_CRUMB_SIZE = this.maze.getCellSize();
        if (showBreadCrumbs) {
            for (int row = 0; row < maze.getRows(); row++) {
                for (int col = 0; col < maze.getCols(); col++) {
                    if (maze.getCell(row, col).getVisited()) {
                        // render the breadcrumb
                        float left = BREAD_CRUMB_LEFT + maze.getCell(row,col).getCol() * BREAD_CRUMB_SIZE;
                        float top = BREAD_CRUMB_TOP + maze.getCell(row,col).getRow() * BREAD_CRUMB_SIZE;
                        Rectangle r = new Rectangle(left, top, BREAD_CRUMB_SIZE, BREAD_CRUMB_SIZE, 0.1f);
                        graphics.draw(r, Color.GREEN);
                    }
                }
            }
        }
    }

    private void renderShortestPath() {
        final float SHORTEST_PATH_LEFT = -0.5f;
        final float SHORTEST_PATH_TOP = -0.5f;
        final float SHORTEST_PATH_SIZE = this.maze.getCellSize();
        if (showShortestPath) {
            Stack<Cell> shortestPathStack = shortestPath.getShortestPathStack();
            for (Cell cell : shortestPathStack) {
                float left = SHORTEST_PATH_LEFT + cell.getCol() * SHORTEST_PATH_SIZE;
                float top = SHORTEST_PATH_TOP + cell.getRow() * SHORTEST_PATH_SIZE;
                Rectangle r = new Rectangle(left, top, SHORTEST_PATH_SIZE, SHORTEST_PATH_SIZE, 0.1f);
                graphics.draw(r, Color.PURPLE);
            }
        }
    }


    private void renderAllCells() {
        for (int row = 0; row < maze.getRows(); row++) {
            for (int col = 0; col < maze.getCols(); col++) {
                renderCell(maze.getCell(row,col));
            }

        }
    }

    private void renderPlayer() {
        graphics.draw(player.getRect(), Color.RED);
    }

    private void renderCell(Cell cell) {
        final float MAZE_LEFT = -0.5f;
        final float MAZE_TOP = -0.5f;
        final float CELL_SIZE = this.maze.getCellSize();
        final float CELL_WALL_THICKNESS = CELL_SIZE * 0.025f;
        final float CELL_Z = 0.3f;

        if (cell.getWallUp()) {
            float left = MAZE_LEFT + cell.getCol() * CELL_SIZE;
            float top = MAZE_TOP + cell.getRow() * CELL_SIZE;
            Rectangle r = new Rectangle(left, top, CELL_SIZE, CELL_WALL_THICKNESS, CELL_Z);
            graphics.draw(r, Color.YELLOW);
        }
        if (cell.getWallDown()) {
            float left = MAZE_LEFT + cell.getCol() * CELL_SIZE;
            float top = MAZE_TOP + (cell.getRow() + 1) * CELL_SIZE;
            Rectangle r = new Rectangle(left, top, CELL_SIZE, CELL_WALL_THICKNESS, CELL_Z);
            graphics.draw(r, Color.YELLOW);
        }
        if (cell.getWallLeft()) {
            float left = MAZE_LEFT + cell.getCol() * CELL_SIZE;
            float top = MAZE_TOP + cell.getRow() * CELL_SIZE;
            Rectangle r = new Rectangle(left, top, CELL_WALL_THICKNESS, CELL_SIZE, CELL_Z);
            graphics.draw(r, Color.YELLOW);
        }
        if (cell.getWallRight()) {
            float left = MAZE_LEFT + (cell.getCol() + 1) * CELL_SIZE;
            float top = MAZE_TOP + cell.getRow() * CELL_SIZE;
            Rectangle r = new Rectangle(left, top, CELL_WALL_THICKNESS, CELL_SIZE, CELL_Z);
            graphics.draw(r, Color.YELLOW);
        }
    }
}
