/**
 * Checks of Mission 0. Run:  java -cp out DisjointSetTest
 * Add your own checks below: a large chain, a union of a set with itself, count after many unions.
 */
public class DisjointSetTest {

    static int failed = 0;

    static void check(boolean ok, String what) {
        System.out.println((ok ? "ok    " : "FAIL  ") + what);
        if (!ok) failed++;
    }

    public static void main(String[] args) {
        DisjointSet d = new DisjointSet(6);
        check(d.count() == 6, "6 sets at the start");
        check(!d.connected(0, 1), "0 and 1 are apart at the start");
        check(d.union(0, 1), "union(0, 1) joins two sets");
        check(d.connected(0, 1), "0 and 1 are together");
        check(!d.union(1, 0), "union(1, 0) again returns false");
        check(d.count() == 5, "5 sets after one union");
        d.union(2, 3); d.union(1, 3);
        check(d.connected(0, 2), "0 and 2 are together through 1 and 3");
        check(d.count() == 3, "3 sets: {0,1,2,3} {4} {5}");
        check(d.find(4) == 4 && d.find(5) == 5, "a single cell is its own root");

        // union of a set with itself
        check(!d.union(2, 2), "union(2, 2) returns false");
        check(!d.union(0, 3), "union of two cells of one set returns false");
        check(d.count() == 3, "count does not change after useless unions");

        // a large chain 0-1, 1-2, ...: must not overflow the stack and must end in one set
        int n = 1_000_000;
        DisjointSet big = new DisjointSet(n);
        for (int i = 0; i + 1 < n; i++) big.union(i, i + 1);
        check(big.count() == 1, "chain of 1 000 000 cells is one set");
        check(big.connected(0, n - 1), "the ends of the chain are connected");

        // count after many unions: join pairs (2i, 2i+1), then half of the sets stay
        DisjointSet pairs = new DisjointSet(1000);
        for (int i = 0; i < 1000; i += 2) pairs.union(i, i + 1);
        check(pairs.count() == 500, "500 sets after 500 pair unions");
        check(!pairs.connected(1, 2), "different pairs are apart");
        for (int i = 0; i + 2 < 1000; i += 2) pairs.union(i, i + 2);
        check(pairs.count() == 1, "one set after joining all pairs");

        System.out.println(failed == 0 ? "All checks passed." : failed + " checks failed.");
    }
}
