import java.util.ArrayList;
import java.util.Random;

public class Maze {
    private Cell[][] grid;
    private ArrayList<int[]> frontier;
    private ArrayList<int[]> maze;

    public Maze(int rows, int cols) {
        this.grid = new Cell[rows][cols];
        this.frontier = new ArrayList<>();
        this.maze = new ArrayList<>();
    }

    public void generateMaze() {
        // randomly pick a cell and add it to the maze
        int[] firstCell = selectRandomCell();
        // add its neighboring cells to the frontier
        ArrayList<int[]> neighbors = getNeighbors(firstCell[0], firstCell[1]);
        frontier.addAll(neighbors);
        // randomly choose a cell in the frontier.
        int[] randomFrontierCell = selectRandomFrontier();
        // randomly choose a wall that connects to a cell in the maze
        neighbors = getNeighbors(randomFrontierCell[0], randomFrontierCell[1]);
        neighbors = getNeighborsInMaze(neighbors);

    }

    private int[] selectRandomCell() {
        Random rand = new Random();
        int randomRow = rand.nextInt(grid.length);
        int randomCol = rand.nextInt(grid[randomRow].length);
        int[] randomCell = new int[]{randomRow, randomCol};
        maze.add(randomCell);
        return randomCell;
    }

    private int[] selectRandomFrontier() {
        Random random = new Random();
        int randomIndex = random.nextInt(frontier.size());
        return frontier.get(randomIndex);
    }

    private ArrayList<int[]> getNeighborsInMaze(ArrayList<int[]> neighbors) {
        ArrayList<int[]> neighborsInMaze = new ArrayList<>();
        for (int[] neighbor : neighbors) {
            if (maze.contains(neighbor)) {
                neighborsInMaze.add(neighbor);
            }
        }
        return neighborsInMaze;
    }

    public ArrayList<int[]> getNeighbors(int row, int col) {
        int rowLength = grid.length;
        int colLength = grid[0].length;
        ArrayList<int[]> neighbors = new ArrayList<>();
        // Up
        if (row - 1 >= 0) {
            neighbors.add(new int[]{row - 1, col});
        }
        // Down
        if (row + 1 < rowLength) {
            neighbors.add(new int[]{row + 1, col});
        }
        // Left
        if (col - 1 >= 0) {
            neighbors.add(new int[]{row, col - 1});
        }
        // Right
        if (col + 1 < colLength) {
            neighbors.add(new int[]{row, col + 1});
        }
        return neighbors;
    }

}
