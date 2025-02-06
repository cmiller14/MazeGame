import java.util.ArrayList;
import java.util.Random;

public class Maze {
    private Cell[][] grid;
    private ArrayList<int[]> frontier;
    private ArrayList<int[]> maze;
    private Random rand = new Random();

    public Maze(int rows, int cols) {
        this.grid = new Cell[rows][cols];
        this.frontier = new ArrayList<>();
        this.maze = new ArrayList<>();
        populateGrid();
    }

    public void generateMaze() {
        // randomly pick a cell and add it to the maze
        int[] firstCell = selectRandomCell();
        // add its neighboring cells to the frontier
        ArrayList<int[]> neighbors = getNeighbors(firstCell[0], firstCell[1]);
        frontier.addAll(neighbors);
        while (!frontier.isEmpty()) {
            // randomly choose a cell in the frontier.
            int[] randomFrontierCell = selectRandomFrontier();
            // randomly choose a wall that connects to a cell in the maze
            neighbors = getNeighbors(randomFrontierCell[0], randomFrontierCell[1]);
            neighbors = getNeighborsInMaze(neighbors);
            ArrayList<Cell> neighborsCells = getCells(neighbors);
            // get the shared walls between the neighbors and the random frontier cell then remove a wall
            removeWall(grid[randomFrontierCell[0]][randomFrontierCell[1]], neighborsCells);
            // add the cell to the maze
            maze.add(randomFrontierCell);
            // update the frontier
            frontier.remove(randomFrontierCell);
        }
    }

    private void removeWall(Cell frontierCell, ArrayList<Cell> neighbors) {
        // pick a random neighbor
        int randomIndex = rand.nextInt(neighbors.size());
        Cell neighbor = neighbors.get(randomIndex);

        // above if the col == col and neighbor.row == frontier.row - 1
        if (neighbor.getCol() == frontierCell.getCol() && neighbor.getRow() == (frontierCell.getRow()-1)) {
            frontierCell.setWallUp(false);
            neighbor.setWallDown(false);
        }
        // below if the col == col and neighbor.row == frontier.row + 1
        if (neighbor.getCol() == frontierCell.getCol() && neighbor.getRow() == (frontierCell.getRow()+1)) {
            frontierCell.setWallDown(false);
            neighbor.setWallUp(false);
        }

        // left if the row == row and the neighbor.col == frontier.col - 1
        if (neighbor.getRow() == frontierCell.getRow() && neighbor.getCol() == (frontierCell.getCol()-1)) {
            frontierCell.setWallLeft(false);
            neighbor.setWallRight(false);
        }

        // right if the row == row and the neighbor.col == frontier.col + 1
        if (neighbor.getRow() == frontierCell.getRow() && neighbor.getCol() == (frontierCell.getCol()+1)) {
            frontierCell.setWallRight(false);
            neighbor.setWallLeft(false);
        }
    }

    private ArrayList<Cell> getCells(ArrayList<int[]> cellsInt) {
        ArrayList<Cell> cells = new ArrayList<>();
        for (int[] c : cellsInt) {
            cells.add(grid[c[0]][c[1]]);
        }
        return cells;
    }

    private int[] selectRandomCell() {
        int randomRow = rand.nextInt(grid.length);
        int randomCol = rand.nextInt(grid[randomRow].length);
        int[] randomCell = new int[]{randomRow, randomCol};
        maze.add(randomCell);
        return randomCell;
    }

    private int[] selectRandomFrontier() {
        int randomIndex = rand.nextInt(frontier.size());
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

    private void populateGrid() {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                grid[row][col] = new Cell(row, col, grid.length, grid[0].length);
            }
        }
    }

    private ArrayList<int[]> getNeighbors(int row, int col) {
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
