package Mineswepeer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Minesweeper extends Application
{
	/* Draw the board and make the rules when the application is launched */
	public void start(Stage primaryStage)
	{
		new Board(primaryStage);
	}

	/* When the application is closed */
	public void stop()
	{
		Platform.exit();
	}
	
	/* Launch the application */
	public static void main(String[] args)
	{
		launch(args);
	}
}
