package tool;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import fdtmc.FDTMC;


public class RDGNode {

	//This reference is used to store all the RDGnodes created during the evaluation
	private static Map<String, RDGNode> rdgNodes = new HashMap<String, RDGNode>();

	// Node identifier
	private String id;
	//This attribute is used to store the FDTMC for the RDG node.
	private FDTMC fdtmc;
	/**
	 * The node must have an associated presence condition, which is
	 * a boolean expression over features.
	 */
	private String presenceCondition;
	// Nodes on which this one depends
	private Collection<RDGNode> dependencies;
	/**
	 * Height of the RDGNode.
	 */
	private int height;


	/**
	 * The id, presence condition and model (FDTMC) of an RDG node must
	 * be immutable, so there must be no setters for them. Hence, they
	 * must be set at construction-time.
	 *
	 * @param id Node's identifier. It is preferably a valid Java identifier.
	 * @param presenceCondition Boolean expression over features (using Java operators).
	 * @param fdtmc Stochastic model of the piece of behavioral model represented by
	 *             this node.
	 */
	public RDGNode(String id, String presenceCondition, FDTMC fdtmc) {
	    this.id = id;
	    this.presenceCondition = presenceCondition;
	    this.fdtmc = fdtmc;
		this.dependencies = new HashSet<RDGNode>();
		this.height = 0;

		rdgNodes.put(id, this);
	}

    public FDTMC getFDTMC() {
        return this.fdtmc;
    }

    public void addDependency(RDGNode child) {
        this.dependencies.add(child);
        height = Math.max(height, child.height + 1);
    }

    public Collection<RDGNode> getDependencies() {
        return dependencies;
    }

    public String getPresenceCondition() {
        return presenceCondition;
    }

    public String getId() {
        return id;
    }

    /**
     * Height of the RDGNode. This metric is defined in the same way as
     * the height of a tree node, i.e., the maximum number of nodes in a path
     * from this one to a leaf (node with no dependencies).
     */
    public int getHeight() {
        return height;
    }

    public static RDGNode getById(String id) {
        return rdgNodes.get(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof RDGNode) {
            return ((RDGNode) obj).id.equals(id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Retrieves the transitive closure of the RDGNode dependency relation.
     * The node itself is part of the returned list.
     *
     * It implements the Cormen et al.'s topological sort algorithm.
     *
     * @return The descendant RDG nodes ordered bottom-up (depended-upon to dependent).
     * @throws CyclicRdgException if there is a path with a cycle starting from this node.
     */
    public List<RDGNode> getDependenciesTransitiveClosure() throws CyclicRdgException {
        List<RDGNode> transitiveDependencies = new LinkedList<RDGNode>();
        Map<RDGNode, Boolean> marks = new HashMap<RDGNode, Boolean>();
        topoSortVisit(this, marks, transitiveDependencies);
        return transitiveDependencies;
    }

    /**
     * Topological sort {@code visit} function (Cormen et al.'s algorithm).
     * @param node
     * @param marks
     * @param sorted
     * @throws CyclicRdgException
     */
    private void topoSortVisit(RDGNode node, Map<RDGNode, Boolean> marks, List<RDGNode> sorted) throws CyclicRdgException {
        if (marks.containsKey(node) && marks.get(node) == false) {
            // Visiting temporarily marked node -- this means a cyclic dependency!
            throw new CyclicRdgException();
        } else if (!marks.containsKey(node)) {
            // Mark node temporarily (cycle detection)
            marks.put(node, false);
            for (RDGNode child: node.getDependencies()) {
                topoSortVisit(child, marks, sorted);
            }
            // Mark node permanently (finished sorting branch)
            marks.put(node, true);
            sorted.add(node);
        }
    }

}
