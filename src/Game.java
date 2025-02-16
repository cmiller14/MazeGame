import edu.usu.graphics.*;
import edu.usu.utils.Tuple2;

import java.util.*;

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
    private Texture background;
    private Rectangle backgroundRec;
    private Rectangle finishMessage;
    private Rectangle creditsMessage;
    private Texture star;
    private ArrayList<Tuple2<String,Integer>> highScores;

    private boolean showBreadCrumbs;
    private boolean showShortestPath;
    private boolean playerMoved;
    private boolean showHint;
    private boolean finished;
    private boolean showScores;
    private boolean showCredits;

    public Game(Graphics2D graphics) {
        this.graphics = graphics;
        this.inputKeyboard = new KeyboardInput(graphics.getWindow());
    }

    public void initialize() {
        createMaze(20,20, 1/25.0f);
        initializeKeyboardInput();
        fontTime = new Font("resources/fonts/Roboto-Regular.ttf", 36, false);
        fontInstructions = new Font("resources/fonts/Roboto-Regular.ttf", 24, false);
        initializeTextures();
        highScores = new ArrayList<>();
        initializeFinishMessage();
    }

    private void initializeFinishMessage() {
        float width = (this.maze.getCellSize() * this.maze.getRows()) - 0.05f;
        float height = (this.maze.getCellSize() * this.maze.getCols()) / 2;
        finishMessage = new Rectangle((-0.5f) + (0.05f/2f), (-0.5f) + (0.05f/2f), width, height, 0.9f);
        creditsMessage = new Rectangle((-0.5f) + (0.05f/2f), (-0.5f) + (0.05f/2f), width, height, 0.92f);
    }

    private void initializeTextures() {
        background = new Texture("resources/images/background.jpeg");
        float width = this.maze.getCellSize() * this.maze.getRows();
        float height = this.maze.getCellSize() * this.maze.getCols();
        backgroundRec = new Rectangle(-0.5f, -0.5f, width, height);
        star = new Texture("resources/images/star.png");
    }

    private void initializeKeyboardInput() {
        // Register the inputs we want to have invoked
        inputKeyboard.registerCommand(GLFW_KEY_W, true, (double elapsedTime) -> player.moveUp((float) elapsedTime));
        inputKeyboard.registerCommand(GLFW_KEY_S, true, (double elapsedTime) -> player.moveDown((float) elapsedTime));
        inputKeyboard.registerCommand(GLFW_KEY_A, true, (double elapsedTime) -> player.moveLeft((float) elapsedTime));
        inputKeyboard.registerCommand(GLFW_KEY_D, true, (double elapsedTime) -> player.moveRight((float) elapsedTime));
        inputKeyboard.registerCommand(GLFW_KEY_UP, true, (double elapsedTime) -> player.moveUp((float) elapsedTime));
        inputKeyboard.registerCommand(GLFW_KEY_DOWN, true, (double elapsedTime) -> player.moveDown((float) elapsedTime));
        inputKeyboard.registerCommand(GLFW_KEY_LEFT, true, (double elapsedTime) -> player.moveLeft((float) elapsedTime));
        inputKeyboard.registerCommand(GLFW_KEY_RIGHT, true, (double elapsedTime) -> player.moveRight((float) elapsedTime));
        inputKeyboard.registerCommand(GLFW_KEY_B, true, (double elapsedTime) -> showBreadCrumbs = !showBreadCrumbs);
        inputKeyboard.registerCommand(GLFW_KEY_F1, true, (double elapsedTime) -> createMaze(5,5, 1/6.25f));
        inputKeyboard.registerCommand(GLFW_KEY_F2, true, (double elapsedTime) -> createMaze(10,10, 1/12.5f));
        inputKeyboard.registerCommand(GLFW_KEY_F3, true, (double elapsedTime) -> createMaze(15,15, 1/18.75f));
        inputKeyboard.registerCommand(GLFW_KEY_F4, true, (double elapsedTime) -> createMaze(20,20, 1/25f));
        inputKeyboard.registerCommand(GLFW_KEY_P, true, (double elapsedTime) -> showShortestPath = !showShortestPath);
        inputKeyboard.registerCommand(GLFW_KEY_H, true, (double elapsedTime) -> showHint = !showHint);
        inputKeyboard.registerCommand(GLFW_KEY_F5, true, (double elapsedTime) -> showScores = !showScores);
        inputKeyboard.registerCommand(GLFW_KEY_F6, true, (double elapsedTime) -> showCredits = !showCredits);
        inputKeyboard.registerCommand(GLFW_KEY_I, true, (double elapsedTime) -> player.moveUp((float) elapsedTime));
        inputKeyboard.registerCommand(GLFW_KEY_K, true, (double elapsedTime) -> player.moveDown((float) elapsedTime));
        inputKeyboard.registerCommand(GLFW_KEY_J, true, (double elapsedTime) -> player.moveLeft((float) elapsedTime));
        inputKeyboard.registerCommand(GLFW_KEY_L, true, (double elapsedTime) -> player.moveRight((float) elapsedTime));

    }

    public void shutdown() {
        background.cleanup();
        star.cleanup();
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
        finished = previousPos.equals(maze.getCell(maze.getRows()-1,maze.getCols()-1));
        updateScores();
    }

    private void updateScores() {
        if (finished && playerMoved) {
            String mazeSize = String.format("%dx%d", maze.getRows(), maze.getCols());
            highScores.add(new Tuple2<>(mazeSize, score));
        }
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
        if(!finished) time = time + elapsedTime;
    }

    private void updatePlayerMoved() {
        playerMoved = !maze.getCell(player.getRow(), player.getCol()).equals(previousPos);
    }

    private void updateVisited() {
        maze.getCell(player.getRow(),player.getCol()).setVisited(true);
    }

    private void render(double elapsedTime) {
        graphics.begin();

        renderBackground();
        renderAllCells();
        renderPlayer();
        renderBreadCrumbs();
        renderShortestPath();
        renderHint();
        renderTime();
        renderInstructions();
        renderScore();
        renderFinish();
        renderFinishMessage();
        renderScores();
        renderCredits();

        graphics.end();
    }

    private void renderCredits() {
        if (showCredits) {
            graphics.draw(creditsMessage, Color.BLACK);
            // write out message and stats
            graphics.drawTextByHeight(
                    fontTime,
                    "Made By Chase Miller",
                    finishMessage.left - (finishMessage.left/3),
                    finishMessage.top,
                    finishMessage.height / 8,
                    0.93f,
                    Color.WHITE
            );
        }
    }

    private void renderScores() {
        if (showScores) {
            float top = -0.40f;
            float left = 0.6f;
            float height = 0.04f;
            highScores.sort(Comparator.comparingInt(Tuple2::item2));
            List<Tuple2<String, Integer>> reversedScores = highScores.reversed();
            for (Tuple2<String, Integer> score : reversedScores) {
                String scoreString = String.format("Size: %s Score: %d", score.item1(), score.item2());
                graphics.drawTextByHeight(fontInstructions, scoreString, left, top, height, Color.WHITE);
                top += 0.05f;
            }
        }
    }

    private void renderFinishMessage() {
        if (finished) {
            graphics.draw(finishMessage, Color.BLACK);
            // write out message and stats
            graphics.drawTextByHeight(
                    fontTime,
                    "Congratulations",
                    finishMessage.left - (finishMessage.left/2),
                    finishMessage.top,
                    finishMessage.height / 8,
                    0.91f,
                    Color.WHITE
            );
            graphics.drawTextByHeight(
                    fontTime,
                    "Choose maze size to restart",
                    finishMessage.left - (finishMessage.left/4),
                    finishMessage.top + (finishMessage.height/4),
                    finishMessage.height / 8,
                    0.91f,
                    Color.WHITE
            );
        }
    }

    private void renderFinish() {
        // make the rectangle at the bottom left
        float top = -0.5f + (this.maze.getCols()-1) * this.maze.getCellSize();
        float left = -0.5f + (this.maze.getRows()-1) * this.maze.getCellSize();
        float width = this.maze.getCellSize();
        float height = this.maze.getCellSize();
        Rectangle finish = new Rectangle(left, top, width, height);
        graphics.draw(star, finish, Color.YELLOW);
    }

    private void renderBackground() {
        graphics.draw(background, backgroundRec, Color.WHITE);
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
            graphics.draw(star, r, Color.WHITE);
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
                        graphics.draw(star, r, Color.PURPLE);
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
                graphics.draw(star, r, Color.GREEN);
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
