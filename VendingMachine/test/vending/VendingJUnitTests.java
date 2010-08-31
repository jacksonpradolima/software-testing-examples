package vending;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(
{
	DispenserTest_Functional.class,
	VendingMachineTest_Functional.class,
	VendingMachineTest_OrsoEtAl.class
})
public class VendingJUnitTests
{
}
