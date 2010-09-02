/* Description: This aspect make the game go faster when the user reach
 * the next level. It use the Counter aspect to count lines.
 * 
 * Copyright 2003 Gustav Evertsson All Rights Reserved.
*/


package Aspects.Highscore;

import java.awt.*;
import javax.swing.JLabel;
import Aspects.Gui.*;
import EventInterface.*;
import Logic.*;

public aspect Levels {
	
	pointcut guiInit() : execution(Gui.TetrisGUI.new(..));

	pointcut timerInit(Timer timer, IEventListner listner, int sleepTime) : execution(Logic.Timer.new(IEventListner, int)) && target(timer) && args(listner, sleepTime);

	pointcut deleteLines(int totalLines) : set(int Aspects.Highscore.Counter.totalLines) && args(totalLines);

	pointcut newgame() : call(* Main.AspectTetris.restartGame());
		
	protected JLabel levelLabel;
	protected int nextLevel = 5;
	protected int currentLevel = 1;
	protected int sleepTime;
	protected Timer timer;
	
	
	after() : guiInit() {
		levelLabel = new JLabel("Level: " + currentLevel);
		if(GameInfo.infoPanel != null)
			GameInfo.infoPanel.add(levelLabel);
	}
	
	after(int totalLines) : deleteLines(totalLines) {
		if(totalLines > nextLevel) {
			currentLevel++;
			levelLabel.setText("Level: " + currentLevel);
			nextLevel = nextLevel * 2;
			sleepTime = (int)((double)sleepTime * 0.8);
			timer.setSleepTime(sleepTime);
			//System.out.println("New sleepTime: " + sleepTime);
		
		}
	}

	before(Timer timer, IEventListner listner, int sleepTime) : timerInit(timer, listner, sleepTime) {
		this.timer = timer;
		this.sleepTime = sleepTime;
	}
	
	before() : newgame() {
		nextLevel = 5;
		currentLevel = 1;
		levelLabel.setText("Level: " + currentLevel);
	}
}
