/* Description: This is a Panel that draws all the blocks
 * from an internal array. It use TetrisImages to get the imgaes
 * that is used.
 * 
 * Copyright 2003 Gustav Evertsson All Rights Reserved.
*/

package Gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import Logic.*;

public class BlockPanel extends JComponent {

	protected final int BLOCKSIZE = 15;

	protected int lines;
	protected int columns;
	protected int[][] blockLines;
	protected String backGroundFile;
	
	public BlockPanel(int columns, int lines, String backGroundFile) {
		this.lines = lines;
		this.columns = columns;
		this.backGroundFile = backGroundFile;
		
		blockLines = new int[columns][lines];
		for(int x = 0; x < columns; x++) {
			for(int y = 0; y < lines; y++) {
				blockLines[x][y] = Blocks.EMPTY;
			}
		}
		//setBackground(Color.gray);
		//setSize( 100, 100);
		
		
	}
		
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if(blockLines == null)
			return;
		
		//clarkv
		TetrisGUI.d.firstPaint();
		if (Gui.Driver.fileName==null)
			System.out.println("pt:"+System.currentTimeMillis());

		if(!backGroundFile.equals("")){
			Image img = TetrisImages.getImage(backGroundFile);
			g.drawImage(img, 0, 0, (ImageObserver)this);
		}
		else {
			//setBackground(Color.BLACK);
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, columns * BLOCKSIZE + 10, lines * BLOCKSIZE + 10);
					
		}
		
		for(int x = 0; x < columns; x++) {
			for(int y = 0; y < lines; y++) {
				if(blockLines[x][y] != Blocks.EMPTY){
					// Draw colored blocks
					//g.setColor(Blocks.typeToColor(blockLines[x][y]));
					//g.fill3DRect( x * BLOCKSIZE, y * BLOCKSIZE, BLOCKSIZE, BLOCKSIZE, true);
					
					// Draw images
					Image img = Blocks.typeToImage(blockLines[x][y]);
					g.drawImage(img, x * BLOCKSIZE + 5, y * BLOCKSIZE + 5, (ImageObserver)this);
				}
			}
		}
		
	}
	
	
	/**
	* Takes an array and update the panel from it.
	*/
	public void setBlocks(int[][] blockLines) {
		this.blockLines = blockLines;
		repaint(new Rectangle(getPreferredSize()));
	}
	
	public void setBlock(int col, int line, int type) {
		blockLines[col][line] = type;
		repaint(new Rectangle(getPreferredSize()));		
	}
	
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	public Dimension getPreferredSize() {
		return new Dimension(columns * BLOCKSIZE + 10, lines * BLOCKSIZE + 10);
	}
}
