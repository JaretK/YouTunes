import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.stage.Stage;

public class TaskTester extends Application {

	public TaskTester() {
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		long init = System.currentTimeMillis();
		YtDownloader dl = new YtDownloader();
		Task<String> t = dl.updateTitlesTask("SHHknspIQ8Q");
		Thread thread = new Thread(t);
		thread.start();
		thread.join();
		long mid = System.currentTimeMillis();
		System.out.println((mid - init));

		YoutubeValidator ytv = new YoutubeValidator();
		System.out.println(ytv.validateId("SHHknspIQ8Q"));
		System.out.println(ytv.getVideoTitle("SHHknspIQ8Q"));
		
		long end = System.currentTimeMillis();
		System.out.println((end-mid));
	}

	public static void main(String[] args) {
		launch(args);
	}

}
