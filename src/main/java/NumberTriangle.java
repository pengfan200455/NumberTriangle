import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * NumberTriangle is a minimally defined structure used for this task.
 *
 * The shape is a full triangle; each row has exactly one more node than the row above.
 * Internal nodes are shared: for example, the node "e" has parents "b" and "c".
 *
 *                  a
 *                b   c
 *              d   e   f
 *            h   i   j   k
 *
 * The triangle is intended to be constructed via {@link #loadTriangle(String)}.
 */
public class NumberTriangle {

    private int root;
    private NumberTriangle left;
    private NumberTriangle right;

    public NumberTriangle(int root) {
        this.root = root;
    }

    public void setLeft(NumberTriangle left) {
        this.left = left;
    }

    public void setRight(NumberTriangle right) {
        this.right = right;
    }

    public int getRoot() {
        return root;
    }

    /**
     * [not for credit]
     * Optional extension: mutate this triangle so that each node stores the
     * max path sum from that node to a leaf (Project Euler #18 idea),
     * leaving each node as a leaf afterwards.
     */
    public void maxSumPath() {
        // optional, not required for this assignment
    }

    /** @return true if this node has no children. */
    public boolean isLeaf() {
        return right == null && left == null;
    }

    /**
     * Follow a path through this NumberTriangle using a sequence of characters:
     * 'l' means go left, 'r' means go right. The method starts at this node
     * and navigates according to the path string. An empty path returns this root.
     *
     * @param path a string of 'l' and 'r' characters (may be empty)
     * @return the integer value stored at the node reached by following the path
     * @throws IllegalArgumentException if the path contains invalid characters,
     *                                  or attempts to navigate beyond a leaf
     */
    public int retrieve(String path) {
        if (path == null || path.isEmpty()) {
            return this.root;
        }
        NumberTriangle cur = this;
        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            if (c == 'l') {
                if (cur.left == null) {
                    throw new IllegalArgumentException("Path goes left beyond a leaf at index " + i);
                }
                cur = cur.left;
            } else if (c == 'r') {
                if (cur.right == null) {
                    throw new IllegalArgumentException("Path goes right beyond a leaf at index " + i);
                }
                cur = cur.right;
            } else {
                throw new IllegalArgumentException("Invalid path character: " + c + " (only 'l' or 'r')");
            }
        }
        return cur.root;
    }

    /**
     * Load a NumberTriangle from a classpath text resource. Each line represents
     * one row of integers separated by spaces. Example:
     *
     * 3
     * 7 4
     * 2 4 6
     * 8 5 9 3
     *
     * Construction is bottom-up: for row r and column j,
     * node(r, j).left  = node(r+1, j)
     * node(r, j).right = node(r+1, j+1)
     *
     * @param fname resource file name located on the classpath (e.g., "input_tree.txt")
     * @return the root node at the top of the constructed triangle
     * @throws IOException if the resource cannot be found or read
     */
    public static NumberTriangle loadTriangle(String fname) throws IOException {
        // Open the resource from the classpath (src/main/resources is included at runtime)
        InputStream inputStream = NumberTriangle.class.getClassLoader().getResourceAsStream(fname);
        if (inputStream == null) {
            throw new FileNotFoundException("Resource not found on classpath: " + fname);
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        // 1) Read all rows of integers
        List<List<Integer>> rows = new ArrayList<>();
        String line = br.readLine();
        while (line != null) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                String[] parts = trimmed.split("\\s+");
                List<Integer> ints = new ArrayList<>(parts.length);
                for (String p : parts) {
                    ints.add(Integer.parseInt(p));
                }
                rows.add(ints);
            }
            line = br.readLine();
        }
        br.close();

        if (rows.isEmpty()) {
            throw new IllegalArgumentException("Triangle file must contain at least one number");
        }

        // 2) Bottom-up construction: keep the list of nodes from the row below
        List<NumberTriangle> below = null;
        for (int r = rows.size() - 1; r >= 0; r--) {
            List<Integer> level = rows.get(r);
            List<NumberTriangle> current = new ArrayList<>(level.size());
            for (int j = 0; j < level.size(); j++) {
                NumberTriangle node = new NumberTriangle(level.get(j));
                if (below != null) {
                    node.setLeft(below.get(j));
                    node.setRight(below.get(j + 1));
                }
                current.add(node);
            }
            below = current; // move upward
        }

        // The top row has exactly one node
        return below.get(0);
    }

    public static void main(String[] args) throws IOException {
        NumberTriangle mt = NumberTriangle.loadTriangle("input_tree.txt");
        // not for credit
        mt.maxSumPath();
        System.out.println(mt.getRoot());
    }
}
