package bst;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class BinarySearchTreeTest_Andre {

	private BinarySearchTreeWithRank bstwrank;

	private BinarySearchTree bstwrankb;

	@Before
	public void setUp() {
		bstwrank = new BinarySearchTreeWithRank();
		bstwrankb = new BinarySearchTree();
	}

	@Test
	public void test_insert_01() {
		bstwrank.insert(new Integer(10));
		assertEquals(new Integer(10), bstwrank.find(new Integer(10)));
	}

	@Test
	public void test_insert_02() {
		bstwrank.insert(new Integer(10));
		bstwrank.insert(new Integer(5));
		assertEquals(new Integer(5), bstwrank.find(new Integer(5)));
	}

	@Test
	public void test_insert_03() {
		bstwrank.insert(new Integer(10));
		bstwrank.insert(new Integer(15));
		assertEquals(new Integer(15), bstwrank.find(new Integer(15)));
	}

	@Test(expected = DuplicateItem.class)
	public void test_insert_04() {
		bstwrank.insert(new Integer(10));
		bstwrank.insert(new Integer(10));
	}

	@Test
	public void test_insert_01b() {
		bstwrankb.insert(new Integer(10));
		assertEquals(new Integer(10), bstwrankb.find(new Integer(10)));
	}

	@Test
	public void test_insert_02b() {
		bstwrankb.insert(new Integer(10));
		bstwrankb.insert(new Integer(5));
		assertEquals(new Integer(5), bstwrankb.find(new Integer(5)));
	}

	@Test
	public void test_insert_03b() {
		bstwrankb.insert(new Integer(10));
		bstwrankb.insert(new Integer(15));
		assertEquals(new Integer(15), bstwrankb.find(new Integer(15)));
	}

	@Test(expected = DuplicateItem.class)
	public void test_insert_04b() {
		bstwrankb.insert(new Integer(10));
		bstwrankb.insert(new Integer(10));
	}

	@Test(expected=ItemNotFound.class)
	public void test_remove_01() {
		bstwrank.remove(new Integer(10));
	}

	@Test
	public void test_remove_02() {
		bstwrank.insert(new Integer(10));
		bstwrank.remove(new Integer(10));
		assertTrue(bstwrank.isEmpty());
	}

	@Test
	public void test_remove_03() {
		bstwrank.insert(new Integer(10));
		bstwrank.insert(new Integer(5));
		bstwrank.remove(new Integer(5));
	}

	@Test
	public void test_remove_04() {
		bstwrank.insert(new Integer(10));
		bstwrank.insert(new Integer(15));
		bstwrank.remove(new Integer(15));
	}

	@Test(expected=ItemNotFound.class)
	public void test_remove_05() {
		bstwrank.insert(new Integer(10));
		bstwrank.remove(new Integer(15));
	}

	@Test
	public void test_remove_06() {
		bstwrank.insert(new Integer(10));
		bstwrank.insert(new Integer(5));
		bstwrank.remove(new Integer(10));
	}

	@Test
	public void test_remove_07() {
		bstwrank.insert(new Integer(10));
		bstwrank.insert(new Integer(15));
		bstwrank.remove(new Integer(10));
	}

	@Test
	public void test_remove_08() {
		bstwrank.insert(new Integer(10));
		bstwrank.insert(new Integer(5));
		bstwrank.insert(new Integer(15));
		bstwrank.remove(new Integer(10));
	}

	@Test(expected=ItemNotFound.class)
	public void test_remove_01b() {
		bstwrankb.remove(new Integer(10));
	}

	@Test
	public void test_remove_02b() {
		bstwrankb.insert(new Integer(10));
		bstwrankb.remove(new Integer(10));
		assertTrue(bstwrankb.isEmpty());
	}

	@Test(expected=ItemNotFound.class)
	public void test_remove_03b() {
		bstwrankb.insert(new Integer(10));
		bstwrankb.insert(new Integer(5));
		bstwrankb.remove(new Integer(5));
		assertEquals(null, bstwrankb.find(new Integer(5)));
	}

	@Test(expected=ItemNotFound.class)
	public void test_remove_04b() {
		bstwrankb.insert(new Integer(10));
		bstwrankb.insert(new Integer(15));
		bstwrankb.remove(new Integer(15));
		assertEquals(null, bstwrankb.find(new Integer(15)));
	}

	@Test(expected=ItemNotFound.class)
	public void test_remove_05b() {
		bstwrankb.insert(new Integer(10));
		bstwrankb.remove(new Integer(15));
	}

	@Test(expected=ItemNotFound.class)
	public void test_remove_06b() {
		bstwrankb.insert(new Integer(10));
		bstwrankb.insert(new Integer(5));
		bstwrankb.remove(new Integer(10));
		assertEquals(null, bstwrankb.find(new Integer(10)));
	}

	@Test(expected=ItemNotFound.class)
	public void test_remove_07b() {
		bstwrankb.insert(new Integer(10));
		bstwrankb.insert(new Integer(15));
		bstwrankb.remove(new Integer(10));
		assertEquals(null, bstwrankb.find(new Integer(10)));
	}

	@Test(expected=ItemNotFound.class)
	public void test_remove_08b() {
		bstwrankb.insert(new Integer(10));
		bstwrankb.insert(new Integer(5));
		bstwrankb.insert(new Integer(15));
		bstwrankb.remove(new Integer(10));
		assertEquals(null, bstwrankb.find(new Integer(10)));
	}

	@Test
	public void test_find_01() {
		bstwrank.insert(new Integer(10));
		bstwrank.find(new Integer(10));
		assertEquals(new Integer(10), bstwrank.find(new Integer(10)));
	}

	@Test(expected=ItemNotFound.class)
	public void test_find_02() {
		bstwrank.find(new Integer(5));
		assertEquals(null, bstwrank.find(new Integer(5)));
	}

	@Test(expected=ItemNotFound.class)
	public void test_find_03() {
		bstwrank.insert(new Integer(10));
		bstwrank.find(new Integer(5));
		assertFalse(bstwrank.isEmpty());
		assertEquals(null, bstwrank.find(new Integer(5)));
	}

	@Test
	public void test_find_04() { // structural
		bstwrank.insert(new Integer(10));
		bstwrank.insert(new Integer(20));
		bstwrank.insert(new Integer(15));
		assertEquals(new Integer(15), bstwrank.find(new Integer(15)));
	}

	@Test(expected=ItemNotFound.class)
	public void test_findMin_01() {
		bstwrank.findMin();
		assertTrue(bstwrank.isEmpty());
		assertEquals(null, bstwrank.findMin());
	}

	@Test
	public void test_findMin_02() {
		bstwrank.insert(new Integer(10));
		bstwrank.findMin();
		assertFalse(bstwrank.isEmpty());
		assertEquals(new Integer(10), bstwrank.findMin());
	}

	@Test(expected=ItemNotFound.class)
	public void test_findMin_01b() {
		bstwrankb.findMin();
		assertTrue(bstwrankb.isEmpty());
		assertEquals(null, bstwrankb.findMin());
	}

	@Test
	public void test_findMin_02b() {
		bstwrankb.insert(new Integer(10));
		bstwrankb.findMin();
		assertFalse(bstwrankb.isEmpty());
		assertEquals(new Integer(10), bstwrankb.findMin());
	}

	@Test
	public void test_findMin_03b() { // structural
		bstwrankb.insert(new Integer(10));
		bstwrankb.insert(new Integer(5));
		bstwrankb.findMin();
		assertEquals(new Integer(5), bstwrankb.findMin());
	}

	@Test
	public void test_findMin_04b() { // structural
		bstwrankb.insert(new Integer(10));
		bstwrankb.insert(new Integer(5));
		bstwrankb.insert(new Integer(1));
		bstwrankb.findMin();
		assertEquals(new Integer(1), bstwrankb.findMin());
	}


	@Test(expected=ItemNotFound.class)
	public void test_findMax_01() {
		bstwrank.findMax();
		assertTrue(bstwrank.isEmpty());
		assertEquals(null, bstwrank.findMax());
	}

	@Test
	public void test_findMax_02() {
		bstwrank.insert(new Integer(10));
		bstwrank.findMax();
		assertFalse(bstwrank.isEmpty());
		assertEquals(new Integer(10), bstwrank.findMax());
	}

	@Test(expected=ItemNotFound.class)
	public void test_findMax_01b() {
		bstwrankb.findMax();
		assertTrue(bstwrankb.isEmpty());
		assertEquals(null, bstwrankb.findMax());
	}

	@Test
	public void test_findMax_02b() {
		bstwrankb.insert(new Integer(10));
		bstwrankb.findMax();
		assertFalse(bstwrankb.isEmpty());
		assertEquals(new Integer(10), bstwrankb.findMax());
	}

	@Test
	public void test_findMax_03b() { // structural
		bstwrankb.insert(new Integer(10));
		bstwrankb.insert(new Integer(15));
		bstwrankb.findMax();
		assertEquals(new Integer(15), bstwrankb.findMax());
	}

	@Test
	public void test_findMax_04b() { // structural
		bstwrankb.insert(new Integer(10));
		bstwrankb.insert(new Integer(15));
		bstwrankb.insert(new Integer(20));
		bstwrankb.findMax();
		assertEquals(new Integer(20), bstwrankb.findMax());
	}

	@Test(expected=ItemNotFound.class)
	public void test_findKth_01() {
		assertTrue(bstwrank.isEmpty());
		bstwrank.findKth(1);
	}

	@Test
	public void test_findKth_02() {
		bstwrank.insert(new Integer(10));
		bstwrank.findKth(1);
		assertFalse(bstwrank.isEmpty());
		assertEquals(new Integer(10), bstwrank.findKth(1));
	}

	@Test
	public void test_findKth_03() {
		bstwrank.insert(new Integer(10));
		bstwrank.insert(new Integer(5));
		bstwrank.findKth(2);
		assertFalse(bstwrank.isEmpty());
		assertEquals(new Integer(10), bstwrank.findKth(2));
	}

	@Test(expected=ItemNotFound.class)
	public void test_findKth_04() {
		bstwrank.insert(new Integer(10));
		bstwrank.insert(new Integer(5));
		assertFalse(bstwrank.isEmpty());
		bstwrank.findKth(3);
	}

	@Test(expected=ItemNotFound.class)
	public void test_findKth_05() { // structural
		bstwrank.insert(new Integer(10));
		bstwrank.insert(new Integer(5));
		assertFalse(bstwrank.isEmpty());
		bstwrank.findKth(-1);
	}


	@Test
	public void test_isEmpty_01() {
		bstwrank.isEmpty();
		assertTrue(bstwrank.isEmpty());
	}

	@Test
	public void test_isEmpty_02() {
		bstwrank.insert(new Integer(10));
		bstwrank.isEmpty();
		assertFalse(bstwrank.isEmpty());
	}


	@Test
	public void test_makeEmpty_01() {
		bstwrank.makeEmpty();
		assertTrue(bstwrank.isEmpty());
	}

	@Test
	public void test_makeEmpty_02() {
		bstwrank.insert(new Integer(10));
		bstwrank.makeEmpty();
		assertTrue(bstwrank.isEmpty());
	}

	@Test(expected=ItemNotFound.class)
	public void test_removeMin_01() { // structural
		bstwrank.removeMin();
	}

	@Test(expected=ItemNotFound.class)
	public void test_removeMin_02() { // structural
		bstwrank.insert(new Integer(10));
		bstwrank.removeMin();
		assertTrue(bstwrank.isEmpty());
		bstwrank.find(new Integer(10));
	}

	@Test(expected=ItemNotFound.class)
	public void test_removeMin_03() { // structural
		bstwrank.insert(new Integer(10));
		bstwrank.insert(new Integer(5));
		bstwrank.removeMin();
		bstwrank.find(new Integer(5));
	}

	@Test(expected=ItemNotFound.class)
	public void test_removeMin_01b() { // structural
		bstwrankb.removeMin();
	}

	public void test_removeMin_02b() { // structural
		bstwrankb.insert(new Integer(10));
		bstwrankb.removeMin();
		assertTrue(bstwrankb.isEmpty());
		assertEquals(null, bstwrankb.find(new Integer(10)));
	}

	public void test_removeMin_03b() { // structural
		bstwrankb.insert(new Integer(10));
		bstwrankb.insert(new Integer(5));
		bstwrankb.removeMin();
		assertEquals(null, bstwrankb.find(new Integer(5)));
	}
}
