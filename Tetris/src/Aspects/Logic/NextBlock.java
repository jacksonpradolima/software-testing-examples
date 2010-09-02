/* Description: This aspect adds the "next block" functionality.
 * 
 * 
 * Copyright 2003 Gustav Evertsson All Rights Reserved.
*/

package Aspects.Logic;

import java.util.Random;
import java.awt.*;
import javax.swing.*;
import Aspects.Gui.*;
import Gui.*;
import Logic.*;

public aspect NextBlock {

	pointcut guiInit() : execution(Gui.TetrisGUI.new(..));

	pointcut getNextBlock() : call(* Main.AspectTetris.getRandomBlock());

	protected BlockPanel nextBlockPanel;
	protected int nextBlock;
	
	after() : guiInit() {
		//clarkv
		//Random rn = new Random();
		//nextBlock = rn.nextInt(Blocks.NUMBEROFTYPES);
		nextBlock = Driver.rand.nextInt(Blocks.NUMBEROFTYPES);
		
	
		nextBlockPanel = new BlockPanel(4, 4, "");
		if(GameInfo.infoPanel != null)
			GameInfo.infoPanel.add(nextBlockPanel);
		nextBlockPanel.setBlocks(Blocks.getBlock(nextBlock));
	}
	
	int[][] around() : getNextBlock() {
		int currentBlock = nextBlock;

		// clarkv
		//Random rn = new Random();
		//nextBlock = rn.nextInt(Blocks.NUMBEROFTYPES);
		nextBlock = Driver.rand.nextInt(Blocks.NUMBEROFTYPES);
		nextBlockPanel.setBlocks(Blocks.getBlock(nextBlock));
		
		
		return Blocks.getBlock(currentBlock);
	}
}

