/* Description: This class handles the blocks and have methods
 * for manipulation of arrays of blocks.
 * 
 * Copyright 2003 Gustav Evertsson All Rights Reserved.
*/

package Logic;

import java.awt.*;

public class Blocks {
	public static final int EMPTY		= -1;
	public static final int RED		= 0;
	public static final int DARKBLUE	= 1;
	public static final int CYAN		= 2;
	public static final int GREEN		= 3;
	public static final int GRAY		= 4;
	public static final int YELLOW	= 5;
	public static final int MAGENTA	= 6;
	
	
	public static int NUMBEROFTYPES = 7;
		
	protected static int blockSizeX;
	protected static int blockSizeY;
	protected static int gameSizeX;
	protected static int gameSizeY;

	public Blocks(int blockSizeX, int blockSizeY, int gameSizeX, int gameSizeY) {
		this.blockSizeX = blockSizeX;
		this.blockSizeY = blockSizeY;
		this.gameSizeX = gameSizeX;
		this.gameSizeY = gameSizeY;
	}

	public int[][] combineBlocks(int[][] block, int[][] board, int x, int y) {
		for(int i = 0; i < blockSizeX; i++) {
			for(int j = 0; j < blockSizeY; j++) {
				try {
					if(block[i][j] != Blocks.EMPTY) {
						board[i + x][j + y] = block[i][j];
					}
				}
				catch(Exception e) {
					System.out.println("Com Error " + e);
				}
				
			}
		}
		return board;
	}

	public int[][] deleteBlocks(int[][] block, int[][] board, int x, int y) {
		for(int i = 0; i < blockSizeX; i++) {
			for(int j = 0; j < blockSizeY; j++) {
				try {
					if(block[i][j] != Blocks.EMPTY) {
						board[i + x][j + y] = Blocks.EMPTY;
					}
				}
				catch(Exception e) {
					System.out.println("Del Error: " + e);
				}
				
			}
		}
		return board;
	}
		
	public boolean checkCombineBlocks(int[][] block, int[][] board, int x, int y) {
		for(int i = 0; i < blockSizeX; i++) {
			for(int j = 0; j < blockSizeY; j++) {
				try {
					if(block[i][j] != Blocks.EMPTY) {
						if(j + y >= gameSizeY){
							//System.out.println("Y SIZE FEL, y:" + y + ", j:" + j);
							return false;
						}
						else if(i + x < 0 || i + x >= gameSizeX){
							//System.out.println("X SIZE FEL, x:" + x + ", i:" + i);
							return false;
						}
						else if(board[i + x][j + y] != Blocks.EMPTY) {
							//System.out.println("BLOCK FEL");
							return false;
						}
					}
				}
				catch(Exception e) {
					System.out.println("Check Error: " + e);
					return false;
				}
				
			}
		}
		return true;
	}
	
	public int[][] turnBlock(int[][] oldBlock) {
		int[][] newBlock = new int[blockSizeX][blockSizeY];
		
		for(int i = 0; i < blockSizeX; i++) {
			for(int j = 0; j < blockSizeY; j++){
				newBlock[j][i] = oldBlock[3-i][j];
			}
		}
		return newBlock;
	}
	
	public void deleteLine(int line, int[][] gameBoard) {
		for(int j = line; j >= 0; j--){
			for(int i = 0; i < gameSizeX; i++) {
				if(j == 0)
					gameBoard[i][j] = EMPTY;
				else
					gameBoard[i][j] = gameBoard[i][j - 1];
			}
		}
	}
	

	public static int[][] getBlock(int type) {
		// Reset current block first;
		int[][] newBlock = new int[blockSizeX][blockSizeY];
		for(int x = 0; x < blockSizeX; x++) {
			for(int y = 0; y < blockSizeY; y++) {
				newBlock[x][y] = Blocks.EMPTY;
			}
		}
		
		if(type == RED ) {
			newBlock[1][0] = RED;
			newBlock[1][1] = RED;
			newBlock[1][2] = RED;
			newBlock[1][3] = RED;
		}
		else if(type == DARKBLUE ) {
			newBlock[1][2] = DARKBLUE;
			newBlock[2][1] = DARKBLUE;
			newBlock[2][2] = DARKBLUE;
			newBlock[3][1] = DARKBLUE;
		}
		else if(type == CYAN ) {
			newBlock[1][1] = CYAN;
			newBlock[2][1] = CYAN;
			newBlock[1][2] = CYAN;
			newBlock[2][2] = CYAN;
		}
		else if(type == GREEN ) {
			newBlock[1][1] = GREEN;
			newBlock[2][1] = GREEN;
			newBlock[2][2] = GREEN;
			newBlock[3][2] = GREEN;
		}
		else if(type == GRAY ) {
			newBlock[0][1] = GRAY;
			newBlock[1][1] = GRAY;
			newBlock[1][2] = GRAY;
			newBlock[2][1] = GRAY;
		}		
		else if(type == YELLOW ) {
			newBlock[0][1] = YELLOW;
			newBlock[0][2] = YELLOW;
			newBlock[1][1] = YELLOW;
			newBlock[2][1] = YELLOW;
		}
		else if(type == MAGENTA ) {
			newBlock[0][1] = MAGENTA;
			newBlock[1][1] = MAGENTA;
			newBlock[2][1] = MAGENTA;
			newBlock[2][2] = MAGENTA;
		}
		return newBlock;
	}
	
	public static String typeToString(int type) {
		if(type == Blocks.RED)
			return "red";
		else if(type == Blocks.DARKBLUE)
			return "blue";
		else if(type == Blocks.CYAN)
			return "cyan";
		else if(type == Blocks.GREEN)
			return "green";
		else if(type == Blocks.GRAY)
			return "gray";
		else if(type == Blocks.YELLOW)
			return "yellow";
		else if(type == Blocks.MAGENTA)
			return "magenta";
		else
			return "";
	}
	
	public static Color typeToColor(int type) {
		if(type == Blocks.RED)
			return Color.red;
		else if(type == Blocks.DARKBLUE)
			return Color.blue;
		else if(type == Blocks.CYAN)
			return Color.cyan;
		else if(type == Blocks.GREEN)
			return Color.green;
		else if(type == Blocks.GRAY)
			return Color.gray;
		else if(type == Blocks.YELLOW)
			return Color.yellow;
		else if(type == Blocks.MAGENTA)
			return Color.magenta;
		else
			return Color.black;
	}
	
	public static Image typeToImage(int type) {
		if(type == Blocks.RED)
			return TetrisImages.getImage("red.gif");
		else if(type == Blocks.DARKBLUE)
			return TetrisImages.getImage("darkblue.gif");
		else if(type == Blocks.CYAN)
			return TetrisImages.getImage("cyan.gif");
		else if(type == Blocks.GREEN)
			return TetrisImages.getImage("green.gif");
		else if(type == Blocks.GRAY)
			return TetrisImages.getImage("gray.gif");
		else if(type == Blocks.YELLOW)
			return TetrisImages.getImage("yellow.gif");
		else if(type == Blocks.MAGENTA)
			return TetrisImages.getImage("magenta.gif");
		else
			return null;
	}
}