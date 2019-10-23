package Mineswepeer;

import javafx.scene.control.Button;
import javafx.scene.paint.Color;

public class Tile extends Button
{
	private boolean isMine, isQuestioned;
	private int value;
	private final int posX, posY;

	/* Tile constructor */
	public Tile(int x, int y)
	{
		posX = x;
		posY = y;
		
		isMine = false;
		isQuestioned = false;
		value = 0;
		
		setMaxSize(30, 30);
		setMinSize(30, 30);
		setFocusTraversable(false);
		setStyle("-fx-font-weight: bold;");
	}
	
	/* Get row position from Board.Field 2d array */
	public int getX()
	{
		return posX;
	}
	
	/* Get column position from Board.Field 2d array */
	public int getY()
	{
		return posY;
	}
	
	/* Set how many neighbors of this are mines */
	public void setValue(int buffer)
	{
		value = buffer;
	}
	
	/* Get how many neighbors of this tile are mines */
	public int getValue()
	{
		return value;
	}
	
	/* Set this tile as a mine */
	public void setMine()
	{
		isMine = true;
	}
	
	/* Check if this tile is a mine */
	public boolean isMine()
	{
		return isMine;
	}
	
	/* Release this tile if its not a mine */
	public void release()
	{
		setDisabled(true);
		
		if (value > 0)
		{
			setText(Integer.toString(value));
			setTextFill(Board.TileColors[value - 1]);
		}
	}
	
	/* Release this tile if its a mine */
	public void releaseMine()
	{
		setDisabled(true);
		setText("X");
		setTextFill(Color.RED);
	}
	
	/* Check if this tile was released */
	public boolean isReleased()
	{
		return isDisabled();
	}
	
	/* Mark this tile as '?' or unmark it if its already marked */
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
	
	/* Check if this tile was marked as '?' */
	public boolean isQuestioned()
	{
		return isQuestioned;
	}
}
