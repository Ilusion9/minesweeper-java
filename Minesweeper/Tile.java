package Minesweeper;

import javafx.scene.control.Button;
import javafx.scene.paint.Color;

public class Tile extends Button
{
	private boolean isMine, isQuestioned;
	private int value;
	private final int posX, posY;

	public Tile(int x, int y)
	{
		// The position of this tile in Board.Field[][]
		posX = x;
		posY = y;
		
		isMine = false;
		isQuestioned = false;
		
		// How many mines this tile has as neighbors
		value = 0;
		
		setMaxSize(30, 30);
		setMinSize(30, 30);
		setFocusTraversable(false);
		setStyle("-fx-font-weight: bold;");
	}
	
	public int getX()
	{
		return posX;
	}
	
	public int getY()
	{
		return posY;
	}
	
	public void setValue(int buffer)
	{
		value = buffer;
	}
	
	public int getValue()
	{
		return value;
	}
	
	public void setMine()
	{
		isMine = true;
	}
	
	public boolean isMine()
	{
		return isMine;
	}
	
	public void release()
	{
		setDisabled(true);
		
		if (value > 0)
		{
			setText(Integer.toString(value));
			setTextFill(Board.TileColors[value - 1]);
		}
	}
	
	public boolean isReleased()
	{
		return isDisabled();
	}
	
	public boolean isQuestioned()
	{
		return isQuestioned;
	}
	
	public void setQuestioned()
	{
		if (isQuestioned)
		{
			setText("");
			isQuestioned = false;
		}
		else
		{
			setText("?");
			setTextFill(Color.RED);
			isQuestioned = true;
		}
	}
	
	public void releaseMine()
	{
		setDisabled(true);
		setText("X");
		setTextFill(Color.RED);
	}
}
