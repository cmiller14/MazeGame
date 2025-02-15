import java.util.*;

public class ShortestPath {
    private final Maze maze;
    private final Queue<Cell> queue;
    private final Set<Cell> visited;
    private final Stack<Cell> stack;

    public ShortestPath(Maze maze) {
        this.maze = maze;
        this.queue = new LinkedList<Cell>();
        this.visited = new HashSet<Cell>();
        this.stack = new Stack<>();;
    }

    public Stack<Cell> getShortestPathStack() {return stack;}

    public Cell getHint() {return stack.peek();}

    public void updateShortestPathStack(Cell cell, Cell previousCell) {
        if (cell == maze.getCell(maze.getRows()-1, maze.getCols()-1)) {
            return;
        }
        if (cell == stack.peek()) {
            stack.pop();
        } else if (cell != stack.peek()) {
            stack.push(previousCell);
        }
    }

    public void findShortestPath(int endRow, int endCol) {
        queue.add(maze.getCell(0,0));
        visited.add(maze.getCell(0,0));
        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            if (current.getRow() == endRow && current.getCol() == endCol) {
                Cell currentCell = this.maze.getCell(maze.getRows()-1, maze.getCols()-1);
                while (currentCell != null) {
                    currentCell.setOnShortestPath(true);
                    stack.push(currentCell);
                    currentCell = currentCell.getParent();
                }
                stack.pop();
                return;
            }
            ArrayList<Cell> neighbors = current.reachableNeighbors(this.maze.getGrid());
            for (Cell neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    neighbor.setParent(current);
                    queue.add(neighbor);
                }
            }
        }

    }

}
