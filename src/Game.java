import edu.usu.graphics.*;

import static org.lwjgl.glfw.GLFW.*;

public class Game {
    private final Graphics2D graphics;
    private final KeyboardInput inputKeyboard;

    private Maze maze;
    private Player player;

    private boolean showBreadCrumbs;

    private static final float SPRITE_MOVE_RATE_PER_SECOND = 0.40f;

    public Game(Graphics2D graphics) {
        this.graphics = graphics;
        this.inputKeyboard = new KeyboardInput(graphics.getWindow());
    }

    public void initialize() {
        createMaze(25,25, 1/30.0f);
        // Register the inputs we want to have invoked
        inputKeyboard.registerCommand(GLFW_KEY_W, true, (double elapsedTime) -> {
            player.moveUp((float) elapsedTime * SPRITE_MOVE_RATE_PER_SECOND);
        });
        inputKeyboard.registerCommand(GLFW_KEY_S, true, (double elapsedTime) -> {
            player.moveDown((float) elapsedTime * SPRITE_MOVE_RATE_PER_SECOND);
        });
        inputKeyboard.registerCommand(GLFW_KEY_A, true, (double elapsedTime) -> {
            player.moveLeft((float) elapsedTime * SPRITE_MOVE_RATE_PER_SECOND);
        });
        inputKeyboard.registerCommand(GLFW_KEY_D, true, (double elapsedTime) -> {
            player.moveRight((float) elapsedTime * SPRITE_MOVE_RATE_PER_SECOND);
        });
        inputKeyboard.registerCommand(GLFW_KEY_UP, true, (double elapsedTime) -> {
            player.moveUp((float) elapsedTime * SPRITE_MOVE_RATE_PER_SECOND);
        });
        inputKeyboard.registerCommand(GLFW_KEY_DOWN, true, (double elapsedTime) -> {
            player.moveDown((float) elapsedTime * SPRITE_MOVE_RATE_PER_SECOND);
        });
        inputKeyboard.registerCommand(GLFW_KEY_LEFT, true, (double elapsedTime) -> {
            player.moveLeft((float) elapsedTime * SPRITE_MOVE_RATE_PER_SECOND);
        });
        inputKeyboard.registerCommand(GLFW_KEY_RIGHT, true, (double elapsedTime) -> {
            player.moveRight((float) elapsedTime * SPRITE_MOVE_RATE_PER_SECOND);
        });
        inputKeyboard.registerCommand(GLFW_KEY_B, true, (double elapsedTime) -> {
            showBreadCrumbs = !showBreadCrumbs;
        });
        inputKeyboard.registerCommand(GLFW_KEY_F1, true, (double elapsedTime) -> {
            createMaze(5,5, 1/10f);
        });
        inputKeyboard.registerCommand(GLFW_KEY_F2, true, (double elapsedTime) -> {
            createMaze(10,10, 1/15f);
        });
        inputKeyboard.registerCommand(GLFW_KEY_F3, true, (double elapsedTime) -> {
            createMaze(15,15, 1/20f);
        });
        inputKeyboard.registerCommand(GLFW_KEY_F4, true, (double elapsedTime) -> {
            createMaze(20,20, 1/25f);
        });
        inputKeyboard.registerCommand(GLFW_KEY_F7, true, (double elapsedTime) -> {
            createMaze(25,25, 1/30.0f);
        });

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
        inputKeyboard.update(elapsedTime);

    }

    private void update(double elapsedTime) {
        updateVisited();
    }

    private void render(double elapsedTime) {
        graphics.begin();

        renderAllCells();
        renderPlayer();
        renderBreadCrumbs();

        graphics.end();
    }

    private void createMaze(int x, int y, float cellSize) {
        this.maze = new Maze(x, y, cellSize);
        this.maze.generateMaze();
        this.player = new Player(0,0, maze);
        this.showBreadCrumbs = false;

    }

    private void updateVisited() {
        maze.getCell(player.getRow(),player.getCol()).setVisited(true);
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


    private void renderAllCells() {
        for (int row = 0; row < maze.getRows(); row++) {
            for (int col = 0; col < maze.getCols(); col++) {
                renderCell(maze.getCell(row,col), Color.YELLOW);
            }

        }
    }

    private void renderPlayer() {
        graphics.draw(player.getRect(), Color.RED);
    }

    private void renderCell(Cell cell, Color color) {
        final float MAZE_LEFT = -0.5f;
        final float MAZE_TOP = -0.5f;
        final float CELL_SIZE = this.maze.getCellSize();
        final float CELL_WALL_THICKNESS = CELL_SIZE * 0.025f;
        final float CELL_Z = 0.3f;

        if (cell.getWallUp()) {
            float left = MAZE_LEFT + cell.getCol() * CELL_SIZE;
            float top = MAZE_TOP + cell.getRow() * CELL_SIZE;
            Rectangle r = new Rectangle(left, top, CELL_SIZE, CELL_WALL_THICKNESS, CELL_Z);
            graphics.draw(r, color);
        }
        if (cell.getWallDown()) {
            float left = MAZE_LEFT + cell.getCol() * CELL_SIZE;
            float top = MAZE_TOP + (cell.getRow() + 1) * CELL_SIZE;
            Rectangle r = new Rectangle(left, top, CELL_SIZE, CELL_WALL_THICKNESS, CELL_Z);
            graphics.draw(r, color);
        }
        if (cell.getWallLeft()) {
            float left = MAZE_LEFT + cell.getCol() * CELL_SIZE;
            float top = MAZE_TOP + cell.getRow() * CELL_SIZE;
            Rectangle r = new Rectangle(left, top, CELL_WALL_THICKNESS, CELL_SIZE, CELL_Z);
            graphics.draw(r, color);
        }
        if (cell.getWallRight()) {
            float left = MAZE_LEFT + (cell.getCol() + 1) * CELL_SIZE;
            float top = MAZE_TOP + cell.getRow() * CELL_SIZE;
            Rectangle r = new Rectangle(left, top, CELL_WALL_THICKNESS, CELL_SIZE, CELL_Z);
            graphics.draw(r, color);
        }
    }
}
