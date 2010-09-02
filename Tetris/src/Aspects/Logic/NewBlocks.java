/* Description: This aspect adds two new types of blocks
 * 
 * 
 * Copyright 2003 Gustav Evertsson All Rights Reserved.
*/

/*

Lägg till två nya typer av block, men egna färger osv.

*/

package Aspects.Logic;

import java.awt.*;
import Logic.*;

public aspect NewBlocks {

	public static final int WHITE	= 7;
	public static final int BLUE	= 8;

	pointcut getBlock(int type) : call(int[][] Logic.Blocks.getBlock(int)) && args(type);

	pointcut typeToString(int type) : call(String Logic.Blocks.typeToString(int)) && args(type);

	pointcut typeToColor(int type) : call(Color Logic.Blocks.typeToColor(int)) && args(type);

	pointcut typeToImage(int type) : call(Image Logic.Blocks.typeToImage(int)) && args(type);

	pointcut numberOfTypes() : get(static int Blocks.NUMBEROFTYPES);
	
	int around() : numberOfTypes() {
		return 9;	
	}

	int[][] around(int type) : getBlock(type) {
		if(type < 7)
			return proceed(type);
		
		int[][] newBlock = new int[4][4];
		for(int x = 0; x < 4; x++) {
			for(int y = 0; y < 4; y++) {
				newBlock[x][y] = Blocks.EMPTY;
			}
		}
		
		if(type == WHITE ) {
			newBlock[0][1] = WHITE;
			newBlock[0][2] = WHITE;
			newBlock[1][1] = WHITE;
			newBlock[2][1] = WHITE;
			newBlock[2][2] = WHITE;
		}
		else if(type == BLUE ) {
			newBlock[1][0] = BLUE;
			newBlock[1][1] = BLUE;
			newBlock[1][2] = BLUE;
			newBlock[2][1] = BLUE;
			newBlock[2][2] = BLUE;
		}
		
		return newBlock;
	}
	
	String around(int type) : typeToString(type) {
		if(type < 7)
			return proceed(type);
		else if(type == WHITE)
			return "white";
		else if(type == BLUE)
			return "blue";
		else
			return "";
			
	}

	Image around(int type) : typeToImage(type) {
		if(type < 7)
			return proceed(type);
		
		if(type == WHITE)
			return TetrisImages.getImage("blue.gif");
		else if(type == BLUE)
			return TetrisImages.getImage("white.gif");
		else
			return null;
		
	}
	
	Color around(int type) : typeToColor(type) {
		if(type < 7)
			return proceed(type);
		else if(type == WHITE)
			return Color.WHITE;
		else if(type == BLUE)
			return Color.BLUE;
		else
			return Color.BLACK;
			
	}
}