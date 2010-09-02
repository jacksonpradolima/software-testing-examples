/* Description: This aspects checks so the AspectTetris class is not called
 * directly (it must be done with the interface). 
 * It also checks so the BlockPanel class is not created outside of the Gui package.
 * 
 * Copyright 2003 Gustav Evertsson All Rights Reserved.
*/


package Aspects.Development;

public aspect DesignCheck {

	declare warning: call(Gui.BlockPanel.new(..)) && !within(Gui.*) && !within(Aspects..*): "Do not create BlockPanel outside the Gui package!";

	declare warning: call(* Main.AspectTetris.*(..)) && !within(Main.AspectTetris) && !within(Aspects..*): "Do not call Main.AspectTetris outside the class, use the IEventListner interface!";

}
