package bst;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(
{
	BinarySearchTreeTest_Functional.class,
	BinarySearchTreeTest_Andre.class,
	BinarySearchTreeTest_Structural.class,
	BinarySearchTreeTest_Structural2.class
})
public class AllTests
{
}
