import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.stage.Stage;

public class TaskTester extends Application {

	public TaskTester() {
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		YtDownloader dl = new YtDownloader("cXER29tPVow");
		Task<String> t = dl.updateTitlesTask();
		Thread thread = new Thread(t);
		thread.start();
		thread.join();
		
	}

	public static void main(String[] args) {
		launch(args);
	}

}
