/**
 * Mission 3. Escape: DFS from cell 0 to the last cell (rows * cols - 1) in the maze of Mission 1.
 * Two neighbouring cells are connected when the wall between them is broken.
 * The neighbours are tried in this order: up, right, down, left (cell - cols, cell + 1, cell + cols, cell - 1).
 * The ready method Graphs.open(w, broken) gives you the open neighbours of every cell, in no particular order:
 * to visit them in the required order, go through the four cells above one by one and take those that are in the list.
 * The program must not overflow the stack on a maze of 1000 by 1000 cells.
 */
public class Escape {

    /**
     * path: the cells of the way out, from 0 to the last cell.
     * visited: how many cells are marked as visited at the moment the exit is marked (the start and the exit are counted).
     * deadEnds: how many cells of the maze have exactly one open neighbour.
     */
    public record Result(int[] path, int visited, int deadEnds) {}

    public static Result escape(Input.Walls w, int[] broken) {
        int n = w.cells(), cols = w.cols();
        int[][] open = Graphs.open(w, broken);

        int deadEnds = 0;
        for (int c = 0; c < n; c++) if (open[c].length == 1) deadEnds++;

        int exit = n - 1;
        int[] step = {-cols, 1, cols, -1};              // up, right, down, left

        // DFS with our own stack instead of recursion (no StackOverflowError on 1000 x 1000).
        // stack[0..top] is the current way from the start, dir[cell] is the next direction to try from the cell.
        int[] stack = new int[n];
        int[] dir = new int[n];
        boolean[] visited = new boolean[n];
        int top = 0, marked = 1;
        stack[0] = 0;
        visited[0] = true;
        while (top >= 0 && stack[top] != exit) {
            int cell = stack[top];
            if (dir[cell] == 4) { top--; continue; }    // all four directions tried: go back
            int next = cell + step[dir[cell]++];
            if (next < 0 || next >= n || visited[next] || !isOpen(open[cell], next)) continue;
            visited[next] = true;
            marked++;
            stack[++top] = next;
        }
        if (top < 0) return new Result(new int[0], marked, deadEnds);
        return new Result(java.util.Arrays.copyOf(stack, top + 1), marked, deadEnds);
    }

    /** True when the wall between the cell and next is broken, that is next is in the list of open neighbours (at most 4). */
    private static boolean isOpen(int[] neighbours, int next) {
        for (int x : neighbours) if (x == next) return true;
        return false;
    }
}
