package vending;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(
{
	DispenserTest_Functional.class,
	VendingMachineTest_Functional.class,
	VendingMachineTest_Harrold.class
})
public class AllTests
{
}
