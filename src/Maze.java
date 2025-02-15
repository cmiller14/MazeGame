import java.util.ArrayList;
import java.util.Random;

public class Maze {
    private Cell[][] grid;
    private ArrayList<Cell> frontier;
    private Random rand = new Random();
    private int rows;
    private int cols;
    private float cellSize;


    public Maze(int rows, int cols, float cellSize) {
        this.grid = new Cell[rows][cols];
        this.frontier = new ArrayList<>();
        this.rows = rows;
        this.cols = cols;
        this.cellSize = cellSize;
        populateGrid();
        generateMaze();
    }

    public int getRows() {return rows;}
    public int getCols() {return cols;}

    public Cell[][] getGrid() {return this.grid;}

    public Cell getCell(int row, int col) {return grid[row][col];}

    public float getCellSize() {return this.cellSize;}

    public boolean wallNotExists(int row, int col, String direction) {
        return !switch (direction) {
            case "up" -> grid[row][col].getWallUp();
            case "down" -> grid[row][col].getWallDown();
            case "left" -> grid[row][col].getWallLeft();
            case "right" -> grid[row][col].getWallRight();
            default -> false;
        };
    }

    private void generateMaze() {
        // randomly pick a cell and add it to the maze
        Cell firstCell = selectRandomCell();
        // add its neighboring cells to the frontier
        ArrayList<Cell> neighbors = firstCell.getNeighbors(this.grid);
        addCellsToFrontier(neighbors);
        while (!frontier.isEmpty()) {
            // randomly choose a cell in the frontier.
            Cell randomFrontier = selectRandomFrontier();
            // randomly choose a wall that connects to a cell in the maze
            // get the shared walls between the neighbors and the random frontier cell then remove a wall
            neighbors = randomFrontier.getNeighbors(this.grid);
            neighbors = getNeighborsInMaze(neighbors);
            removeWall(randomFrontier, neighbors);
            // add the cell to the maze
            randomFrontier.setInMaze(true);
            // update the frontier
            updateFrontier(randomFrontier);
        }
    }

    private void addCellsToFrontier(ArrayList<Cell> cells) {
        for (Cell cell : cells) {
            frontier.add(cell);
            cell.setInFrontier(true);
        }
    }

    private void updateFrontier(Cell selectedCell) {
        frontier.remove(selectedCell);
        selectedCell.setInFrontier(false);
        ArrayList<Cell> neighbors = selectedCell.getNeighbors(grid);
        for (Cell neighbor : neighbors) {
            if (!neighbor.getInFrontier() && !neighbor.getInMaze()) {
                frontier.add(neighbor);
                neighbor.setInFrontier(true);
            }
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

    private Cell selectRandomCell() {
        int randomRow = rand.nextInt(grid.length);
        int randomCol = rand.nextInt(grid[randomRow].length);
        Cell randomCell = grid[randomRow][randomCol];
        randomCell.setInMaze(true);
        return randomCell;
    }

    private Cell selectRandomFrontier() {
        int randomIndex = rand.nextInt(frontier.size());
        return frontier.get(randomIndex);
    }

    private ArrayList<Cell> getNeighborsInMaze(ArrayList<Cell> neighbors) {
        ArrayList<Cell> neighborsInMaze = new ArrayList<>();
        for (Cell neighbor : neighbors) {
            if (neighbor.getInMaze()) {
                neighborsInMaze.add(neighbor);
            }
        }
        return neighborsInMaze;
    }

    private void populateGrid() {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                grid[row][col] = new Cell(row, col);
            }
        }
    }
}

