/* Description: This is the Panel that the rest of the aspects use
 * to show different info.
 * 
 * Copyright 2003 Gustav Evertsson All Rights Reserved.
*/

package Aspects.Gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import Gui.*;

public aspect GameInfo {
	
	// clarkv
	//declare dominates: Aspects.Highscore.*, Aspects.Gui.*;
	declare precedence: Aspects.Highscore.*, Aspects.Gui.*;
	//initialization
	pointcut guiInit() : execution(Gui.TetrisGUI.new(..));

	public static JPanel infoPanel;

	before() : guiInit() {
		//System.out.println("Target: " + thisJoinPoint.getTarget());
		//System.out.println("This  : " + thisJoinPoint.getThis());
		TetrisGUI theGui = (TetrisGUI)thisJoinPoint.getThis();
		theGui.setLayout(new BorderLayout());
		theGui.setBorder(new EtchedBorder());
		
		infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		theGui.add(infoPanel, BorderLayout.WEST);
	}
}
