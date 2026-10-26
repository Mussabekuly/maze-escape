import java.util.Arrays;

/**
 * Missions 4 and 5. The slides: a directed graph. Cells are 0 .. n-1, slide i goes from s.from()[i] to s.to()[i]
 * and takes s.time()[i]. There are no slides from a cell to itself, and the exit can always be reached from the start.
 *
 * Ready for you: Graphs.slidesFrom(s)[u] is the list of the slides that leave cell u, and Graphs.slidesInto(s)[v] is the list
 * of the slides that enter cell v (reversed graph). The lists hold slide numbers i; the cell at the other end is s.to()[i]
 * or s.from()[i].
 */
public class Zones {

    /**
     * Mission 4. Kosaraju. Returns zoneOf: zoneOf[cell] = number of the zone, numbers 0 .. zones-1.
     * Number the zones in the order in which the second pass finds them. That order is a topological order
     * of the zone graph: every slide between two zones goes from a smaller number to a larger number.
     *
     * Plan: 1) DFS on the slides, write the cells in the order in which they are finished (use a stack, not recursion);
     * 2) go through the cells from the last finished to the first; if a cell has no zone yet, DFS from it on the reversed
     * slides (slidesInto) and give every cell it reaches the next zone number.
     * Is DAG in Main is 1 when the number of zones equals the number of cells (no cycle), so you do not need a separate check.
     */
    public static int[] kosaraju(Input.Slides s) {
        int n = s.n();
        int[][] out = Graphs.slidesFrom(s);
        int[][] in = Graphs.slidesInto(s);

        // pass 1: DFS on the slides with a stack, cells written in the order they are finished.
        // next[u] = how many slides of u are already tried, so a cell stays on the stack until all its slides are done.
        int[] finished = new int[n];
        int done = 0;
        boolean[] seen = new boolean[n];
        int[] next = new int[n];
        int[] stack = new int[n];
        for (int start = 0; start < n; start++) {
            if (seen[start]) continue;
            int top = 0;
            stack[0] = start;
            seen[start] = true;
            while (top >= 0) {
                int u = stack[top];
                if (next[u] < out[u].length) {
                    int v = s.to()[out[u][next[u]++]];
                    if (!seen[v]) { seen[v] = true; stack[++top] = v; }
                } else {
                    finished[done++] = u;
                    top--;
                }
            }
        }

        // pass 2: from the last finished cell to the first, DFS on the reversed slides;
        // every cell reached that has no zone yet gets the current zone number
        int[] zoneOf = new int[n];
        Arrays.fill(zoneOf, -1);
        int zones = 0;
        for (int i = n - 1; i >= 0; i--) {
            int start = finished[i];
            if (zoneOf[start] != -1) continue;
            int top = 0;
            stack[0] = start;
            zoneOf[start] = zones;
            while (top >= 0) {
                int v = stack[top--];
                for (int e : in[v]) {
                    int u = s.from()[e];
                    if (zoneOf[u] == -1) { zoneOf[u] = zones; stack[++top] = u; }
                }
            }
            zones++;
        }
        return zoneOf;
    }

    /**
     * Mission 5. The graph of zones: the edge X to Y costs the cheapest slide from X to Y, walking inside a zone is free.
     * Go through the zones in the order of their numbers (no priority queue). Returns two numbers:
     * [0] flow stages: the number of zones on the longest chain of zones;
     * [1] the cost of the cheapest route from the zone of cell 0 to the zone of the last cell.
     */
    public static long[] routes(Input.Slides s, int[] zoneOf, int zoneCount) {
        int n = s.n();
        // slides between two different zones, grouped by the zone they start in.
        // Several slides X to Y stay in the list: the minimum over them is the cost of the edge X to Y.
        int[] count = new int[zoneCount];
        for (int i = 0; i < s.count(); i++) if (zoneOf[s.from()[i]] != zoneOf[s.to()[i]]) count[zoneOf[s.from()[i]]]++;
        int[][] out = new int[zoneCount][];
        for (int z = 0; z < zoneCount; z++) out[z] = new int[count[z]];
        int[] filled = new int[zoneCount];
        for (int i = 0; i < s.count(); i++) {
            int x = zoneOf[s.from()[i]], y = zoneOf[s.to()[i]];
            if (x != y) out[x][filled[x]++] = i;
        }

        // every edge goes from a smaller zone number to a larger one, so when we come to zone x
        // all edges into x are already relaxed and chain[x], dist[x] are final
        long inf = Long.MAX_VALUE;
        long[] chain = new long[zoneCount];             // zones on the longest chain that ends in x
        long[] dist = new long[zoneCount];               // cheapest route from the zone of the start to x
        Arrays.fill(chain, 1);
        Arrays.fill(dist, inf);
        dist[zoneOf[0]] = 0;
        long stages = 0;
        for (int x = 0; x < zoneCount; x++) {
            stages = Math.max(stages, chain[x]);
            for (int i : out[x]) {
                int y = zoneOf[s.to()[i]];
                chain[y] = Math.max(chain[y], chain[x] + 1);
                if (dist[x] != inf) dist[y] = Math.min(dist[y], dist[x] + s.time()[i]);
            }
        }
        long route = dist[zoneOf[n - 1]];
        return new long[]{stages, route == inf ? -1 : route};
    }
}
