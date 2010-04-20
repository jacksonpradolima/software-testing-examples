package bst;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class BinarySearchTreeTest_Structural2 {

	private BinarySearchTree tree;
	private BinarySearchTreeWithRank rankTree;

	@Before
	public void setUp() {
		tree = new BinarySearchTree();
		rankTree = new BinarySearchTreeWithRank();
	}

	@Test
	public void testCase1() { // def-uso da variável sl
		Comparable f;
		rankTree.insert(new Integer(50));
		rankTree.insert(new Integer(40));
		rankTree.insert(new Integer(60));
		rankTree.insert(new Integer(39));
		rankTree.insert(new Integer(41));
		rankTree.insert(new Integer(59));
		rankTree.insert(new Integer(61));
		rankTree.insert(new Integer(38));
		f = rankTree.findKth(6);

		assertEquals("59", f.toString());
	}

	@Test(expected=ItemNotFound.class)
	public void testCase2() { // k < 1
		rankTree.insert(new Integer(50));
		rankTree.findKth(0);
	}

	
	@Test(expected=ItemNotFound.class)
	public void testCase3() { // cobertura do método void removeMin
		rankTree.insert(new Integer(4));
		rankTree.removeMin();
		rankTree.find(new Integer(4));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase4() { // cobertura do método void
								// BinarySearchTreeWithRank::removeMin
		rankTree.insert(new Integer(4));
		rankTree.insert(new Integer(3));
		rankTree.insert(new Integer(2));
		rankTree.removeMin();
		rankTree.find(new Integer(2));
	}

	@Test
	public void testCase5() { // cobertura do método void
								// BinarySearchTree::findMax
		Comparable f;
		tree.insert(new Integer(1));
		tree.insert(new Integer(2));
		tree.insert(new Integer(3));
		tree.insert(new Integer(4));
		f = tree.findMax();
		assertEquals(new Integer(4), f);
	}

	@Test
	public void testCase6() { // cobertura do método void
								// BinarySearchTree::findMax
		Comparable f;
		tree.insert(new Integer(1));
		f = tree.findMax();
		assertEquals(new Integer(1), f);
	}

	@Test(expected=ItemNotFound.class)
	public void testCase7() { // cobertura do método void
								// BinarySearchTree::remove
		tree.insert(new Integer(2));
		tree.insert(new Integer(1));
		tree.insert(new Integer(3));
		tree.insert(new Integer(4));
		tree.remove(new Integer(2));
		tree.find(new Integer(2));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase8() { // cobertura do método void
								// BinarySearchTree::remove
		tree.insert(new Integer(1));
		tree.insert(new Integer(2));
		tree.remove(new Integer(2));
		tree.find(new Integer(2));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase9() { // cobertura do método void
								// BinarySearchTree::remove
		tree.insert(new Integer(1));
		tree.insert(new Integer(2));
		tree.insert(new Integer(3));
		tree.remove(new Integer(2));
		tree.find(new Integer(2));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase10() { // cobertura do método void
								// BinarySearchTreeWithRank::remove
		rankTree.insert(new Integer(1));
		rankTree.insert(new Integer(2));
		rankTree.insert(new Integer(3));
		rankTree.remove(new Integer(2));
		rankTree.find(new Integer(2));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase11() { // cobertura do método void
								// BinarySearchTreeWithRank::remove
		rankTree.insert(new Integer(2));
		rankTree.insert(new Integer(1));
		rankTree.insert(new Integer(5));
		rankTree.insert(new Integer(4));
		rankTree.insert(new Integer(3));
		rankTree.remove(new Integer(2));
		rankTree.find(new Integer(2));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase12() { // cobertura do método void
								// BinarySearchTree::remove
		tree.insert(new Integer(1));
		tree.insert(new Integer(2));
		tree.insert(new Integer(3));
		tree.remove(new Integer(2));
		tree.find(new Integer(2));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase13() { // cobertura do método void
								// BinarySearchTree::remove
		tree.insert(new Integer(2));
		tree.insert(new Integer(1));
		tree.insert(new Integer(5));
		tree.insert(new Integer(4));
		tree.insert(new Integer(3));
		tree.remove(new Integer(2));
		tree.find(new Integer(2));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase14() { // cobertura do método void
								// BinarySearchTree::remove
		rankTree.insert(new Integer(2));
		rankTree.insert(new Integer(1));
		rankTree.insert(new Integer(3));
		rankTree.insert(new Integer(4));
		rankTree.remove(new Integer(2));
		rankTree.find(new Integer(2));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase15() { // cobertura do método void
								// BinarySearchTree::remove
		rankTree.insert(new Integer(1));
		rankTree.insert(new Integer(2));
		rankTree.remove(new Integer(2));
		rankTree.find(new Integer(2));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase16() { // cobertura do método void
								// BinarySearchTree::remove
		rankTree.insert(new Integer(1));
		rankTree.insert(new Integer(2));
		rankTree.insert(new Integer(3));
		rankTree.remove(new Integer(2));
		rankTree.find(new Integer(2));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase17() { // cobertura do método void
								// BinarySearchTree::remove
		tree.insert(new Integer(5));
		tree.insert(new Integer(1));
		tree.insert(new Integer(8));
		tree.insert(new Integer(6));
		tree.insert(new Integer(7));
		tree.remove(new Integer(5));
		tree.find(new Integer(5));
	}
}