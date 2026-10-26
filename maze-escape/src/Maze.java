import java.util.*;

/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k]. Indexes start from 0,
 * the line number in walls.txt is the index + 1. The cells are connected by DisjointSet.
 */
public class Maze {

    /**
     * Mission 1. Take the walls in the order of the file. Break a wall when its two cells are in different sets.
     * Returns the indexes of the broken walls, in the order of breaking.
     */
    public static int[] breakWalls(Input.Walls w) {
        int[] order = new int[w.count()];
        for (int k = 0; k < order.length; k++) order[k] = k;
        return breakInOrder(w, order);
    }

    /**
     * Mission 2. Kruskal: the same, but take the walls from the cheapest to the most expensive.
     * Returns the indexes of the broken walls.
     */
    public static int[] cheapestMaze(Input.Walls w) {
        // sort the wall indexes by cost; Integer[] because Arrays.sort with a comparator needs objects
        Integer[] sorted = new Integer[w.count()];
        for (int k = 0; k < sorted.length; k++) sorted[k] = k;
        int[] cost = w.cost();
        Arrays.sort(sorted, (x, y) -> Integer.compare(cost[x], cost[y]));
        int[] order = new int[sorted.length];
        for (int k = 0; k < order.length; k++) order[k] = sorted[k];
        return breakInOrder(w, order);
    }

    /** Goes through the walls in the given order and breaks a wall when it joins two different sets. */
    private static int[] breakInOrder(Input.Walls w, int[] order) {
        DisjointSet dsu = new DisjointSet(w.cells());
        int[] broken = new int[Math.max(0, w.cells() - 1)];
        int count = 0;
        for (int k : order) {
            if (count == broken.length) break;          // a tree has cells - 1 edges, nothing more can be broken
            if (dsu.union(w.a()[k], w.b()[k])) broken[count++] = k;
        }
        return Arrays.copyOf(broken, count);
    }

    /**
     * Mission 2. Prim with a priority queue. Returns the total cost of the cheapest maze.
     * It must be equal to the total cost of cheapestMaze.
     */
    public static long primCost(Input.Walls w) {
        int n = w.cells();
        if (n == 0) return 0;
        // adj[cell] = indexes of the walls around this cell
        int[] degree = new int[n];
        for (int k = 0; k < w.count(); k++) { degree[w.a()[k]]++; degree[w.b()[k]]++; }
        int[][] adj = new int[n][];
        for (int i = 0; i < n; i++) adj[i] = new int[degree[i]];
        int[] filled = new int[n];
        for (int k = 0; k < w.count(); k++) {
            adj[w.a()[k]][filled[w.a()[k]]++] = k;
            adj[w.b()[k]][filled[w.b()[k]]++] = k;
        }

        // lazy Prim: the queue holds walls, cheapest first; a wall whose both cells are already in the tree is skipped
        int[] cost = w.cost();
        PriorityQueue<Integer> pq = new PriorityQueue<>((x, y) -> Integer.compare(cost[x], cost[y]));
        boolean[] inTree = new boolean[n];
        inTree[0] = true;
        int added = 1;
        long total = 0;
        for (int k : adj[0]) pq.add(k);
        while (!pq.isEmpty() && added < n) {
            int k = pq.poll();
            int a = w.a()[k], b = w.b()[k];
            if (inTree[a] && inTree[b]) continue;
            int next = inTree[a] ? b : a;
            inTree[next] = true;
            added++;
            total += cost[k];
            for (int e : adj[next]) {
                int other = w.a()[e] == next ? w.b()[e] : w.a()[e];
                if (!inTree[other]) pq.add(e);
            }
        }
        return total;
    }
}
