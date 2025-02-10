import edu.usu.graphics.*;

import static org.lwjgl.glfw.GLFW.*;

public class Game {
    private final Graphics2D graphics;

    private Maze maze;

    public Game(Graphics2D graphics) {
        this.graphics = graphics;
    }

    public void initialize() {
        this.maze = new Maze(25,25);
        this.maze.generateMaze();
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
        // Poll for window events: required in order for window, keyboard, etc events are captured.
        glfwPollEvents();

        // If user presses ESC, then exit the program
        if (glfwGetKey(graphics.getWindow(), GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwSetWindowShouldClose(graphics.getWindow(), true);
        }
    }

    private void update(double elapsedTime) {
    }

    private void render(double elapsedTime) {
        graphics.begin();

        renderAllCells();
        renderPlayer(maze.getCell(0,0));
        graphics.end();
    }

    private void renderAllCells() {
        for (int row = 0; row < maze.getRows(); row++) {
            for (int col = 0; col < maze.getCols(); col++) {
                renderCell(maze.getCell(row,col), Color.YELLOW);
            }

        }
    }

    private void renderPlayer(Cell cell) {
        final float PLAYER_LEFT = -0.49f;
        final float PLAYER_SIZE = 1/30.0f;
        final float PLAYER_TOP = -0.49f;
        final float PLAYER_THICKNESS = PLAYER_SIZE;

        float top = PLAYER_TOP + cell.getRow() * PLAYER_SIZE;
        float left = PLAYER_LEFT + cell.getCol() * PLAYER_SIZE;

        Rectangle myBox = new Rectangle(left, top, PLAYER_SIZE, PLAYER_THICKNESS);
        graphics.draw(myBox, Color.RED);
    }

    private void renderCell(Cell cell, Color color) {
        final float MAZE_LEFT = -0.5f;
        final float MAZE_TOP = -0.5f;
        final float CELL_SIZE = 1 / 20.0f;
        final float CELL_WALL_THICKNESS = CELL_SIZE * 0.025f;

        if (cell.getWallUp()) {
            float left = MAZE_LEFT + cell.getCol() * CELL_SIZE;
            float top = MAZE_TOP + cell.getRow() * CELL_SIZE;
            Rectangle r = new Rectangle(left, top, CELL_SIZE, CELL_WALL_THICKNESS);
            graphics.draw(r, color);
        }
        if (cell.getWallDown()) {
            float left = MAZE_LEFT + cell.getCol() * CELL_SIZE;
            float top = MAZE_TOP + (cell.getRow() + 1) * CELL_SIZE;
            Rectangle r = new Rectangle(left, top, CELL_SIZE, CELL_WALL_THICKNESS);
            graphics.draw(r, color);
        }
        if (cell.getWallLeft()) {
            float left = MAZE_LEFT + cell.getCol() * CELL_SIZE;
            float top = MAZE_TOP + cell.getRow() * CELL_SIZE;
            Rectangle r = new Rectangle(left, top, CELL_WALL_THICKNESS, CELL_SIZE);
            graphics.draw(r, color);
        }
        if (cell.getWallRight()) {
            float left = MAZE_LEFT + (cell.getCol() + 1) * CELL_SIZE;
            float top = MAZE_TOP + cell.getRow() * CELL_SIZE;
            Rectangle r = new Rectangle(left, top, CELL_WALL_THICKNESS, CELL_SIZE);
            graphics.draw(r, color);
        }
    }
}
