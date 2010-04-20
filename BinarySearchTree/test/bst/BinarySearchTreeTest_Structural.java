package bst;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class BinarySearchTreeTest_Structural {

	private BinarySearchTree tree;
	
	private BinarySearchTreeWithRank rankTree;

	@Before
	public void setUp() {
		tree = new BinarySearchTree();
		rankTree = new BinarySearchTreeWithRank();
	}

	// structural test cases
	@Test(expected=ItemNotFound.class)
	public void testCase() { // testa remoção para elemento que não está na
								// árvore
		tree.root = tree.insert(new Integer(1), tree.root);
		tree.root = tree.insert(new Integer(4), tree.root);
		tree.root = tree.insert(new Integer(2), tree.root);
		tree.root = tree.remove(new Integer(6), tree.root);
	}

	@Test
	public void testCase1() { // cobertura do método void insert
		tree.insert(new Integer(1));
		assertEquals("1", tree.root.element.toString());
	}

	@Test
	public void testCase2() { // cobertura do método void remove
		tree.insert(new Integer(1));
		tree.remove(new Integer(1));
		assertEquals(null, tree.root);
	}

	@Test(expected=ItemNotFound.class)
	public void testCase3() { // cobertura do método void findMin
		tree.root = tree.insert(new Integer(1), tree.root);
		tree.root = tree.insert(new Integer(4), tree.root);
		tree.root = tree.insert(new Integer(2), tree.root);

		tree.removeMin();
		tree.find(new Integer(1));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase4() { // cobertura do caso da folha ser filho direito no
								// remove
		tree.insert(new Integer(1));
		tree.insert(new Integer(2));
		tree.remove(new Integer(2));
		tree.find(new Integer(2));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase5_1() { // cobertura do caso do removido com um filho ser
								// filho direito
		tree.insert(new Integer(4));
		tree.insert(new Integer(3));
		tree.insert(new Integer(2));
		tree.remove(new Integer(3));
		tree.find(new Integer(3));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase5_2() { // cobertura do caso do removido com um filho ser
								// filho direito
		tree.insert(new Integer(4));
		tree.insert(new Integer(3));
		tree.insert(new Integer(2));
		tree.remove(new Integer(3));
		tree.remove(new Integer(4));
		tree.find(new Integer(4));
	}

	
	@Test(expected=ItemNotFound.class)
	public void testCase5_3() { // cobertura do caso do removido com um filho ser
								// filho direito
		tree.insert(new Integer(4));
		tree.insert(new Integer(3));
		tree.insert(new Integer(2));
		tree.remove(new Integer(3));
		tree.remove(new Integer(4));
		tree.insert(new Integer(4));
		tree.remove(new Integer(2));
		tree.find(new Integer(2));
	}

	
	@Test(expected=ItemNotFound.class)
	public void testCase6() { // cobertura do caso do removido com um filho ser
								// filho direito
		tree.insert(new Integer(3));
		tree.insert(new Integer(1));
		tree.insert(new Integer(2));
		tree.remove(new Integer(1));
		tree.find(new Integer(1));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase7() { // cobertura do caso do removido com um filho ser
								// filho direito
		tree.insert(new Integer(1));
		tree.insert(new Integer(3));
		tree.insert(new Integer(2));
		tree.remove(new Integer(3));
		tree.find(new Integer(3));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase8() { // cobertura do caso do removido com um filho ser
								// filho direito
		tree.insert(new Integer(1));
		tree.insert(new Integer(2));
		tree.insert(new Integer(3));
		tree.remove(new Integer(2));
		tree.find(new Integer(2));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase9() {
		tree.insert(new Integer(2));
		tree.insert(new Integer(1));
		tree.insert(new Integer(4));
		tree.insert(new Integer(3));
		tree.remove(new Integer(2));
		tree.find(new Integer(2));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase10() {
		tree.insert(new Integer(2));
		tree.insert(new Integer(4));
		tree.insert(new Integer(3));
		tree.insert(new Integer(5));
		tree.remove(new Integer(4));
		tree.find(new Integer(4));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase11() {
		tree.insert(new Integer(6));
		tree.insert(new Integer(4));
		tree.insert(new Integer(3));
		tree.insert(new Integer(5));
		tree.remove(new Integer(4));
		tree.find(new Integer(4));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase12() {
		tree.insert(new Integer(6));
		tree.insert(new Integer(4));
		tree.insert(new Integer(3));
		tree.insert(new Integer(5));
		tree.remove(new Integer(4));
		tree.find(new Integer(4));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase13() {
		tree.insert(new Integer(2));
		tree.insert(new Integer(1));
		tree.insert(new Integer(3));
		tree.remove(new Integer(2));
		tree.find(new Integer(2));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase14() {
		tree.insert(new Integer(5));
		tree.insert(new Integer(10));
		tree.insert(new Integer(7));
		tree.insert(new Integer(13));
		tree.insert(new Integer(12));
		tree.insert(new Integer(14));
		tree.remove(new Integer(10));
		tree.find(new Integer(2));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase15() {
		tree.insert(new Integer(14));
		tree.insert(new Integer(10));
		tree.insert(new Integer(7));
		tree.insert(new Integer(12));
		tree.insert(new Integer(11));
		tree.insert(new Integer(13));
		tree.remove(new Integer(10));
		tree.find(new Integer(10));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase16() {
		tree.insert(new Integer(3));
		tree.insert(new Integer(2));
		tree.insert(new Integer(1));
		tree.removeMin();
		tree.find(new Integer(1));
	}

	@Test
	public void testCase17() {
		tree.insert(new Integer(3));
		tree.insert(new Integer(2));
		tree.insert(new Integer(1));
		Comparable f = tree.findMin();
		assertEquals("1", f.toString());
	}

	// Testes para a BinarySearchTreeWithRank - pegando dos casos para a
	// BinarySearchTree
	@Test
	public void testCase18() {
		rankTree.insert(new Integer(1));
		rankTree.insert(new Integer(4));
		rankTree.insert(new Integer(2));
		assertEquals("2", rankTree.root.right.left.element.toString());
	}

	@Test
	public void testCase19() {
		rankTree.root = rankTree.insert(new Integer(1), rankTree.root);
		assertEquals("1", rankTree.root.element.toString());
	}

	@Test(expected=DuplicateItem.class)
	public void testCase20() {
		rankTree.insert(new Integer(1));
		rankTree.insert(new Integer(4));
		rankTree.insert(new Integer(1), rankTree.root);
	}

	// test cases for deletion
	@Test
	public void testCase21() {
		rankTree.insert(new Integer(1));
		rankTree.insert(new Integer(4));
		rankTree.insert(new Integer(2));
		rankTree.remove(new Integer(2));

		assertEquals("1", rankTree.root.element.toString());
		assertEquals("4", rankTree.root.right.element.toString());
	}

	@Test(expected=ItemNotFound.class)
	public void testCase22() {
		rankTree.insert(new Integer(1));
		rankTree.insert(new Integer(4));
		rankTree.remove(new Integer(2));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase23() {
		rankTree.insert(new Integer(1));
		rankTree.remove(new Integer(2));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase24() { // cobertura do método void removeMin
		rankTree.insert(new Integer(4));
		rankTree.insert(new Integer(1));
		rankTree.insert(new Integer(2));
		rankTree.removeMin();
		rankTree.find(new Integer(1));
	}

	// testa outros casos da remoção para BinarySearchTreeWithRank
	@Test(expected=ItemNotFound.class)
	public void testCase25_1() {
		rankTree.insert(new Integer(5));
		rankTree.insert(new Integer(10));
		rankTree.insert(new Integer(7));
		rankTree.insert(new Integer(13));
		rankTree.insert(new Integer(12));
		rankTree.insert(new Integer(14));
		rankTree.remove(new Integer(10));
		rankTree.find(new Integer(10));
	}

	// testa outros casos da remoção para BinarySearchTreeWithRank
	@Test
	public void testCase25_2() {
		rankTree.insert(new Integer(5));
		rankTree.insert(new Integer(10));
		rankTree.insert(new Integer(7));
		rankTree.insert(new Integer(13));
		rankTree.insert(new Integer(12));
		rankTree.insert(new Integer(14));
		rankTree.remove(new Integer(10));

		assertEquals(4, (rankTree.root.right).size);
		assertEquals(1, (rankTree.root.right.left).size);
		assertEquals(2, (rankTree.root.right.right).size);
	}

	
	@Test(expected=ItemNotFound.class)
	public void testCase26() {
		rankTree.insert(new Integer(14));
		rankTree.insert(new Integer(10));
		rankTree.insert(new Integer(7));
		rankTree.insert(new Integer(12));
		rankTree.insert(new Integer(11));
		rankTree.insert(new Integer(13));
		rankTree.remove(new Integer(10));
		rankTree.find(new Integer(10));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase27() { // testa remoção para elemento que não está na
								// árvore
		rankTree.insert(new Integer(1));
		rankTree.insert(new Integer(4));
		rankTree.insert(new Integer(2));
		rankTree.remove(new Integer(6));
	}

	@Test
	public void testCase28() { // cobertura do método void insert
		rankTree.insert(new Integer(1));
		assertEquals("1", rankTree.root.element.toString());
	}

	@Test
	public void testCase29() { // cobertura do método void remove
		rankTree.insert(new Integer(1));
		rankTree.remove(new Integer(1));
		assertEquals(null, rankTree.root);
	}

	@Test(expected=ItemNotFound.class)
	public void testCase30() { // cobertura do caso da folha ser filho direito
								// no remove
		rankTree.insert(new Integer(1));
		rankTree.insert(new Integer(2));
		rankTree.remove(new Integer(2));
		assertEquals(1, (rankTree.root).size);
		rankTree.find(new Integer(2));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase31() { // cobertura do caso do removido com um filho ser
								// filho direito
		rankTree.insert(new Integer(4));
		rankTree.insert(new Integer(3));
		rankTree.insert(new Integer(2));
		rankTree.remove(new Integer(3));
		assertEquals(2, (rankTree.root).size);

		rankTree.remove(new Integer(4));
		assertEquals(1, (rankTree.root).size);

		rankTree.insert(new Integer(4));
		rankTree.remove(new Integer(2));
		rankTree.find(new Integer(2));
		
	}

	@Test(expected=ItemNotFound.class)
	public void testCase32() { // cobertura do caso do removido com um filho ser
								// filho direito
		rankTree.insert(new Integer(3));
		rankTree.insert(new Integer(1));
		rankTree.insert(new Integer(2));
		rankTree.remove(new Integer(1));
		rankTree.find(new Integer(1));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase33() { // cobertura do caso do removido com um filho ser
								// filho direito
		rankTree.insert(new Integer(1));
		rankTree.insert(new Integer(3));
		rankTree.insert(new Integer(2));
		rankTree.remove(new Integer(3));
		rankTree.find(new Integer(3));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase34() { // cobertura do caso do removido com um filho ser
								// filho direito
		rankTree.insert(new Integer(1));
		rankTree.insert(new Integer(2));
		rankTree.insert(new Integer(3));
		rankTree.remove(new Integer(2));
		rankTree.find(new Integer(2));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase35() {
		rankTree.insert(new Integer(2));
		rankTree.insert(new Integer(1));
		rankTree.insert(new Integer(4));
		rankTree.insert(new Integer(3));
		rankTree.remove(new Integer(2));
		rankTree.find(new Integer(2));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase36() {
		rankTree.insert(new Integer(2));
		rankTree.insert(new Integer(4));
		rankTree.insert(new Integer(3));
		rankTree.insert(new Integer(5));
		rankTree.remove(new Integer(4));
		rankTree.find(new Integer(4));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase37() {
		rankTree.insert(new Integer(6));
		rankTree.insert(new Integer(4));
		rankTree.insert(new Integer(3));
		rankTree.insert(new Integer(5));
		rankTree.remove(new Integer(4));
		rankTree.find(new Integer(4));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase38() {
		rankTree.insert(new Integer(6));
		rankTree.insert(new Integer(4));
		rankTree.insert(new Integer(3));
		rankTree.insert(new Integer(5));
		rankTree.remove(new Integer(4));
		rankTree.find(new Integer(4));
	}

	@Test(expected=ItemNotFound.class)
	public void testCase39() {
		rankTree.insert(new Integer(2));
		rankTree.insert(new Integer(1));
		rankTree.insert(new Integer(3));
		rankTree.remove(new Integer(2));
		rankTree.find(new Integer(2));
	}
}