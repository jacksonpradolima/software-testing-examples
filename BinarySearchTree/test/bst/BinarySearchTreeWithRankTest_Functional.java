package bst;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class BinarySearchTreeWithRankTest_Functional
{
	private BinarySearchTreeWithRank rankTree;

	@Before
	public void setUp() {
		rankTree = new BinarySearchTreeWithRank();
	}

	@Test
	public void testCaseInsertion1() {
		rankTree.root = rankTree.insert(new Integer(1), rankTree.root);
		rankTree.root = rankTree.insert(new Integer(4), rankTree.root);
		rankTree.root = rankTree.insert(new Integer(2), rankTree.root);
		rankTree.root = rankTree.insert(new Integer(3), rankTree.root);

		assertEquals("3", rankTree.root.right.left.right.element.toString());

		// 1.1
		assertEquals(4, (rankTree.root).size);

		// 1.2
		assertEquals(3, (rankTree.root.right).size);

		// 1.3
		assertEquals(2, (rankTree.root.right.left).size);

		// 1.4
		assertEquals(1,	(rankTree.root.right.left.right).size);
	}


	@Test
	public void testCaseDeletion1() {
		rankTree.root = rankTree.insert(new Integer(1), rankTree.root);
		rankTree.root = rankTree.insert(new Integer(4), rankTree.root);
		rankTree.root = rankTree.insert(new Integer(2), rankTree.root);
		rankTree.root = rankTree.remove(new Integer(4), rankTree.root);

		assertEquals("1", rankTree.root.element.toString());
		assertEquals("2", rankTree.root.right.element.toString());

		// 5.1
		assertEquals(2, (rankTree.root).size);
		// 5.2
		assertEquals(1, (rankTree.root.right).size);
	}

	@Test
	public void testCaseSearching1() {
		Comparable f;
		rankTree.root = rankTree.insert(new Integer(1), rankTree.root);
		rankTree.root = rankTree.insert(new Integer(4), rankTree.root);
		rankTree.root = rankTree.insert(new Integer(2), rankTree.root);

		f = rankTree.findKth(1);
		assertEquals("1", f.toString());

		// 15.1
		f = rankTree.findKth(2);
		assertEquals("2", f.toString());

		// 15.2
		f = rankTree.findKth(3);
		assertEquals("4", f.toString());
	}

	@Test(expected=ItemNotFound.class)
	public void testCaseSearching2() {
		rankTree.root = rankTree.insert(new Integer(1), rankTree.root);
		rankTree.root = rankTree.insert(new Integer(4), rankTree.root);
		rankTree.root = rankTree.insert(new Integer(2), rankTree.root);
		rankTree.findKth(5, rankTree.root);
	}
}