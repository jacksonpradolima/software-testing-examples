/* Description: This aspect counts deleted lines.
 * 
 * 
 * Copyright 2003 Gustav Evertsson All Rights Reserved.
*/

package Aspects.Highscore;

import java.awt.*;
import javax.swing.*;
import Aspects.Gui.*;

public aspect Counter {
	
	pointcut guiInit() : execution(Gui.TetrisGUI.new(..));
	
	pointcut deleteLines() : call(* Main.AspectTetris.deleteLines());
	
	pointcut deleteLine() : call(* Logic.Blocks.deleteLine(..));
	
	pointcut newgame() : call(* Main.AspectTetris.restartGame());
	
	pointcut gameOver() : call(* Main.AspectTetris.gameOver());
	
	protected int currentLines = 0;
	protected int totalLines = 0;
	
	protected JLabel lineLabel;
	
	after() : guiInit() {
		lineLabel = new JLabel("Lines: 0");
		if(GameInfo.infoPanel != null)
			GameInfo.infoPanel.add(lineLabel);
	}
	
	before() : deleteLines() {
		currentLines = 0;
	}
	
	after() : deleteLines() {
		totalLines += currentLines;
		if(currentLines != 0)
			//System.out.println("Deleted " + currentLines + " lines (Total: " + totalLines + ").");
			lineLabel.setText("Lines: " + totalLines);
	}
	
	before() : deleteLine() {
		currentLines++;
	}
	
	before() : newgame() {
		totalLines = 0;
		currentLines = 0;
		lineLabel = new JLabel("Lines: 0");
	}
	
	after() : gameOver() {
		System.out.println("Deleted totally " + totalLines + " lines.");	
	}
}
