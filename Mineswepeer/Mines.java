package Minesweeper;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Minesweeper extends Application
{
	public void start(Stage primaryStage)
	{
		new Board(primaryStage);
	}

	public void stop()
	{
		Platform.exit();
	}
	
	public static void main(String[] args)
	{
		Application.launch(args);
	}
}
