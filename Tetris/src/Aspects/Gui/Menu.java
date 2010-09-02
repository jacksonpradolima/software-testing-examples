/* Description: This aspect adds a menu to the gui.
 * The menu has this alternatives:
 * 	New Game
 * 	Pause
 * 	----
 * 	Exit
 *  
 * Copyright 2003 Gustav Evertsson All Rights Reserved.
*/

package Aspects.Gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import Gui.*;
import EventInterface.*;

public aspect Menu  implements ActionListener {
	protected JMenuItem newGameMI;
	protected JMenuItem pauseMI;
	protected JMenuItem exitMI;
	protected IEventListner tetris;
	
	//initialization
	pointcut guiInit() : execution(Gui.TetrisGUI.new(..));

	pointcut tetrisInit(IEventListner tetris) : execution(Main.AspectTetris.new(..)) && target(tetris);
	
	after() : guiInit() {
		TetrisGUI theGui = (TetrisGUI)thisJoinPoint.getThis();
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = (JMenu) menuBar.add(new JMenu("File"));
		newGameMI = (JMenuItem) fileMenu.add(new JMenuItem("New Game"));
      pauseMI = (JMenuItem) fileMenu.add(new JMenuItem("Pause"));
		fileMenu.add(new JSeparator());
		exitMI = (JMenuItem) fileMenu.add(new JMenuItem("Exit"));
      
		newGameMI.addActionListener(this);
		pauseMI.addActionListener(this);
		exitMI.addActionListener(this);

		theGui.add(menuBar, BorderLayout.NORTH);	
		
	}
   
	before(IEventListner tetris) : tetrisInit(tetris) {
		this.tetris = tetris;
   }
   
   public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(newGameMI)) {
			tetris.incomingEvent(IEventListner.NEWGAME);
   	}
   	else if (e.getSource().equals(pauseMI)) {
			tetris.incomingEvent(IEventListner.PAUSE);
   	}
   	else if (e.getSource().equals(exitMI)) {
			System.exit(0);
   	}
   }
 
}
