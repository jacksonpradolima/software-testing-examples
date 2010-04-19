package bst;

/**
 * Basic node stored in all binary search trees. Includes fields for all
 * variations.
 */
class BinaryNode {
	// Constructors
	BinaryNode(Comparable theElement) {
		this(theElement, null, null);
	}

	BinaryNode(Comparable theElement, BinaryNode lt, BinaryNode rt) {
		element = theElement;
		left = lt;
		right = rt;
	}

	// Friendly data; accessible by other package routines
	Comparable element; // The data in the node
	BinaryNode left; // Left child
	BinaryNode right; // Right child

	// Balancing information; only one is used at a time
	int size = 1; // For BinarySearchTreeWithRank
	int color = 1; // For red-black tree
	int level = 1; // For AA-tree
}
