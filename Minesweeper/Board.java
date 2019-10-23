package Mineswepeer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.time.Instant;

public class Board
{
	private int NUM_ROWS = 9;
	private int NUM_COLS = 9;
	private int NUM_MINES = 10;
	
	private boolean GameOver;
	private Tile[][] Field;
	private Alert alertBox = new Alert(AlertType.INFORMATION);
	private Instant startTime;
		
	public static Color TileColors[] = {
			Color.BLUE,
			Color.GREEN,
			Color.RED,
			Color.PURPLE,		
			Color.BROWN,
			Color.LIGHTGREEN,		
			Color.BLACK,
			Color.GRAY
	};
	
	public Board(Stage primaryStage)
	{		
		/* Draw the layout */
		BorderPane container = new BorderPane();
		Scene scene = new Scene(container);
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("Minesweeper");
		primaryStage.resizableProperty().setValue(false);
		
		VBox layout = new VBox();
		layout.alignmentProperty().set(Pos.CENTER);
		
		/* Draw the menu */
		MenuBar menuBar = new MenuBar();
		Menu newGame = new Menu("New");
		Menu gameDiff = new Menu("Difficulty");
		
		MenuItem itemNew = new MenuItem("New game");
		MenuItem gameEasy = new MenuItem("Easy");
		MenuItem gameMedium = new MenuItem("Medium");
		MenuItem gameHard = new MenuItem("Hard");

		newGame.getItems().add(itemNew);

		gameDiff.getItems().add(gameEasy);
		gameDiff.getItems().add(gameMedium);
		gameDiff.getItems().add(gameHard);

		menuBar.getMenus().add(newGame);
		menuBar.getMenus().add(gameDiff);

		/* The grid that contains all tiles */
		GridPane grid = new GridPane();
		
		/* Add the menu and the grid to the layout */
		layout.getChildren().add(menuBar);
		layout.getChildren().add(grid);
		container.setTop(layout);

		/* Start the game */
		startGame(grid);

		primaryStage.show();
		primaryStage.sizeToScene();
		primaryStage.centerOnScreen();
		
		/* Start a new game on pressing New Game button */
		itemNew.setOnAction(e ->
		{	
			startGame(grid);
		});
		
		/* Start a new game on easy difficulty */
		gameEasy.setOnAction(e ->
		{			
		    NUM_ROWS = 9;
		    NUM_COLS = 9;
		    NUM_MINES = 10;

		    startGame(grid);
		    primaryStage.sizeToScene();
		    primaryStage.centerOnScreen();
		});
		
		/* Start a new game on medium difficulty */
		gameMedium.setOnAction(e ->
		{			
		    NUM_ROWS = 16;
		    NUM_COLS = 16;
		    NUM_MINES = 40;
		    
		    startGame(grid);
		    primaryStage.sizeToScene();
		    primaryStage.centerOnScreen();
		});
		
		/* Start a new game on hard difficulty */
		gameHard.setOnAction(e ->
		{			
		    NUM_ROWS = 16;
		    NUM_COLS = 30;
		    NUM_MINES = 99;
		    
		    startGame(grid);
		    primaryStage.sizeToScene();
		    primaryStage.centerOnScreen();
		});
	}
	
	/* Draw the tiles, set our mines and make the rules */
	private void startGame(GridPane grid)
	{
		/* The unix time when this game has started */
		startTime = Instant.now();
		
		GameOver = false;

		/* Clear the parent object of our tiles */
		grid.getChildren().clear();
		
		/* Create the Field 2d array */
   	 	Field = new Tile[NUM_ROWS][NUM_COLS];
   	 	
		for (int i = 0; i < NUM_ROWS; i++)
		{
			for (int j = 0; j < NUM_COLS; j++)
			{
				Tile tile = new Tile(i, j);
				grid.add(tile, j, i);
				
				tile.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>()
				{
				     @Override
				     public void handle(MouseEvent event)
				     {
				    	 /* If the game is over or the tile was released, the player cannot press this tile */
				    	 if (GameOver || tile.isReleased())
				    	 {
				    		 return;
				    	 }
				    	 
				    	 /* On left click pressed */
				    	 if (event.getButton() == MouseButton.PRIMARY)
				    	 {
				    		 /* If the tile is marked as '?', the player must unmark it before releasing */
				    		 if (tile.isQuestioned())
				    		 {
				    			 return;
				    		 }
				    		 
				    		 /* Check if the player released a mine */
				    		 if (tile.isMine())
				    		 {
				    			 tile.releaseMine();
				    			 
				    			 setGameLost();
				    			 return;
				    		 }
				    		 
				    		 /* Find all neighbors of this tile with value 0 - has no mine as neighbor and release them */
				    		 if (tile.getValue() == 0)
				    		 {
				    			 findEmptyTiles(tile.getX(), tile.getY());
				    		 }
				    		 
				    		 /* Release this tile */
				    		 tile.release();
				    		 
				    		 /* Check if the player won the game */
				    		 if (countRemainingTiles() < 1)
				    		 {
				    			 setGameWon();
				    		 }
				    	 }
				    	 
				    	 /* On right click pressed */
				    	 else if (event.getButton() == MouseButton.SECONDARY)
				    	 {
				    		 /* Mark this tile as '?' */
				    		 tile.setQuestioned();
				    	 }
				     }
				});
				
				/* Put this tile in the Field 2d array - we need this array when we find empty tiles */
				Field[i][j] = tile;
			}
		}
		
		/* Set our mines */
		setBoardMines();
	}
	
	private void setBoardMines()
	{
		/* Put all tiles into an arraylist */
		List<Tile> list = new ArrayList<Tile>();
		
		for (int i = 0; i < NUM_ROWS; i++)
		{
			for (int j = 0; j < NUM_COLS; j++)
			{
				list.add(Field[i][j]);
			}
		}
		
		/* Shuffle the list */
		Collections.shuffle(list);
		
		/* Get first NUM_MINES value from the list */
		for (int i = 0; i < NUM_MINES; i++)
		{
			Tile buffer = list.get(i);
			buffer.setMine();
			
			/* Set a new value for the neighbors of this mine - we increase their value with 1 */
			setValueOfMineNeighbors(buffer.getX(), buffer.getY());
		}
		
		list = null;
	}
	
	private int countRemainingTiles()
	{
		int num = NUM_ROWS * NUM_COLS;
		
		for (int i = 0; i < NUM_ROWS; i++)
		{
			for (int j = 0; j < NUM_COLS; j++)
			{
				if (Field[i][j].isMine())
				{
					num--;
				}
				
				else if (Field[i][j].isReleased())
				{
					num--;
				}
			}
		}
		
		return num;
	}
	
	/* Set the game as lost */
	private void setGameLost()
	{
		GameOver = true;

		/* Release the other mines */
		for (int i = 0; i < NUM_ROWS; i++)
		{
			for (int j = 0; j < NUM_COLS; j++)
			{
				if (Field[i][j].isReleased())
				{
					continue;
				}
				
				if (Field[i][j].isMine())
				{	
					Field[i][j].releaseMine();
				}
			}
		}
		
		alertBox.setTitle("Game over");
		alertBox.setHeaderText("Game lost ...");
		alertBox.show();
	}
	
	/* Set the game as won */
	private void setGameWon()
	{
		GameOver = true;
		
		alertBox.setTitle("Game over");
		alertBox.setHeaderText("Game won in " + (Instant.now().getEpochSecond() - startTime.getEpochSecond()) + " seconds!");
		alertBox.show();
	}
	
	/* Set a new value for the neighbors of the mine with pos (x, y) in Field 2d array */
	private void setValueOfMineNeighbors(int x, int y)
	{
		 int neighbors[] = {-1, 0, 0, -1, -1, -1, -1, 1, 1, -1, 1, 0, 0, 1, 1, 1};
		
		 for (int i = 0; i < neighbors.length; i += 2)
		 {
			 int newx = x + neighbors[i];
			 int newy = y + neighbors[i + 1];

			 if (newx < 0 || newy < 0 || newx >= NUM_ROWS || newy >= NUM_COLS) // Wrong indexes
			 {
				 continue;
			 }
			 
			 /* This tile is a mine - skip it */
			 if (Field[newx][newy].isMine())
			 {
				 continue;
			 }
			 
			 /* We increase the neighbor value with 1 */
			 Field[newx][newy].setValue(Field[newx][newy].getValue() + 1);
		 }
	}
	
	private void findEmptyTiles(int x, int y)
	{
		 int neighbors[] = {-1, 0, 0, -1, -1, -1, -1, 1, 1, -1, 1, 0, 0, 1, 1, 1};
		
		 for (int i = 0; i < neighbors.length; i += 2)
		 {
			 int newx = x + neighbors[i];
			 int newy = y + neighbors[i + 1];

			 if (newx < 0 || newy < 0 || newx == NUM_ROWS || newy == NUM_COLS) // Wrong indexes
			 {
				 continue;
			 }

			 /* Tile already released */
			 if (Field[newx][newy].isReleased())
			 {
				 continue;
			 }
			 
			 /* Tile marked as '?' */
			 if (Field[newx][newy].isQuestioned())
			 {
				 Field[newx][newy].setQuestioned();
			 }
			 
			 /* Release this tile */
			 Field[newx][newy].release();
			 
			 /* This tile is empty, release its neighbors */
			 if (Field[newx][newy].getValue() == 0)
			 {
				 findEmptyTiles(newx, newy);
			 }
		 }
	}
}
