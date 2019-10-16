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
		// Layout
		BorderPane container = new BorderPane();
		Scene scene = new Scene(container);
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("Minesweeper");
		primaryStage.resizableProperty().setValue(false);
		
		VBox layout = new VBox();
		layout.alignmentProperty().set(Pos.CENTER);
		// Menu
		
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

		// Game table
		GridPane grid = new GridPane();
		
		// Add items to layout
		layout.getChildren().add(menuBar);
		//layout.getChildren().add(headerButton);
		layout.getChildren().add(grid);
		container.setTop(layout);

		// Init game
		InitGame(grid);

		primaryStage.show();
		primaryStage.sizeToScene();
		primaryStage.centerOnScreen();
		
		itemNew.setOnAction(e ->
		{	
		    InitGame(grid);
		});
		
		gameEasy.setOnAction(e ->
		{			
		    NUM_ROWS = 9;
		    NUM_COLS = 9;
		    NUM_MINES = 10;

		    InitGame(grid);
		    primaryStage.sizeToScene();
		    primaryStage.centerOnScreen();
		});
		
		gameMedium.setOnAction(e ->
		{			
		    NUM_ROWS = 16;
		    NUM_COLS = 16;
		    NUM_MINES = 40;
		    
		    InitGame(grid);
		    primaryStage.sizeToScene();
		    primaryStage.centerOnScreen();
		});
		
		gameHard.setOnAction(e ->
		{			
		    NUM_ROWS = 16;
		    NUM_COLS = 30;
		    NUM_MINES = 99;
		    
		    InitGame(grid);
		    primaryStage.sizeToScene();
		    primaryStage.centerOnScreen();
		});
	}
	
	private void InitGame(GridPane grid)
	{
		startTime = Instant.now();
		GameOver = false;

		grid.getChildren().clear();
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
				    	 if (GameOver || tile.isReleased())
				    	 {
				    		 return;
				    	 }
				    	 
				    	 if (event.getButton() == MouseButton.PRIMARY)
				    	 {
				    		 if (tile.isQuestioned())
				    		 {
				    			 return;
				    		 }
				    		 
				    		 if (tile.isMine())
				    		 {
				    			 tile.releaseMine();
				    			 
				    			 GameLost();
				    			 return;
				    		 }
				    		 
				    		 if (tile.getValue() == 0)
				    		 {
				    			 FindEmptyTiles(tile.getX(), tile.getY());
				    		 }
				    		 
				    		 tile.release();
				    		 
				    		 if (CountRemainingTiles() < 1)
				    		 {
				    			 GameWon();
				    		 }
				    	 }
				    	 
				    	 else if (event.getButton() == MouseButton.SECONDARY)
				    	 {
				    		 tile.setQuestioned();
				    	 }
				     }
				});
				
				Field[i][j] = tile;
			}
		}
		
		SetMines();
	}
	
	private void SetMines()
	{
		List<Tile> list = new ArrayList<Tile>();
		
		for (int i = 0; i < NUM_ROWS; i++)
		{
			for (int j = 0; j < NUM_COLS; j++)
			{
				list.add(Field[i][j]);
			}
		}
		
		Collections.shuffle(list);
		
		for (int i = 0; i < NUM_MINES; i++)
		{
			Tile buffer = list.get(i);
			buffer.setMine();
			RefreshField(buffer.getX(), buffer.getY());
		}
		
		list = null;
	}
	
	private int CountRemainingTiles()
	{
		int num = NUM_ROWS * NUM_COLS;
		
		for (int i = 0; i < NUM_ROWS; i++)
		{
			for (int j = 0; j < NUM_COLS; j++)
			{
				if (Field[i][j].isMine())
				{
					num--;
					continue;
				}
				
				if (Field[i][j].isReleased())
				{
					num--;
				}
			}
		}
		
		return num;
	}
	
	private void GameLost()
	{
		GameOver = true;

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
	
	private void GameWon()
	{
		GameOver = true;
		
		alertBox.setTitle("Game over");
		alertBox.setHeaderText("Game won in " + (Instant.now().getEpochSecond() - startTime.getEpochSecond()) + " seconds!");
		alertBox.show();
	}
	
	private void RefreshField(int x, int y)
	{
		 int Neighbors[] = {-1, 0, 0, -1, -1, -1, -1, 1, 1, -1, 1, 0, 0, 1, 1, 1};
		
		 for (int i = 0; i < Neighbors.length; i += 2)
		 {
			 int newx = x + Neighbors[i];
			 int newy = y + Neighbors[i + 1];

			 if (newx < 0 || newy < 0 || newx >= NUM_ROWS || newy >= NUM_COLS) // Wrong indexes
			 {
				 continue;
			 }
			
			 if (Field[newx][newy].isMine())
			 {
				 continue;
			 }
			 
			 Field[newx][newy].setValue(Field[newx][newy].getValue() + 1);
		 }
	}
	
	private void FindEmptyTiles(int x, int y)
	{
		 int Neighbors[] = {-1, 0, 0, -1, -1, -1, -1, 1, 1, -1, 1, 0, 0, 1, 1, 1};
		
		 for (int i = 0; i < Neighbors.length; i += 2)
		 {
			 int newx = x + Neighbors[i];
			 int newy = y + Neighbors[i + 1];

			 if (newx < 0 || newy < 0 || newx == NUM_ROWS || newy == NUM_COLS) // Wrong indexes
			 {
				 continue;
			 }

			 if (Field[newx][newy].isReleased())
			 {
				 continue;
			 }
			 
			 if (Field[newx][newy].isQuestioned())
			 {
				 Field[newx][newy].setQuestioned();
			 }
			 
			 Field[newx][newy].release();
			 
			 if (Field[newx][newy].getValue() == 0)
			 {
				 FindEmptyTiles(newx, newy);
			 }
		 }
	}
}
