import javafx.application.Application;
import javafx.stage.Stage;

public class TaskTester extends Application {

	public TaskTester() {
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		String[] ids = new String[] { "NMXW9Q-yAAw", "uozgE83uG4o",
				"eKH46ff1roc", "VgKFmC_YgyU", "aADqFviP7wU", "rabOaM2kh9c",
				"6nRy10_iOPc", "uUo6r0EWycw", "f3WxjIJRKXE", "Y8rnmRYHHwI" };
		long init = System.currentTimeMillis();
		for (String ele : ids) {
			System.out.println(YoutubeValidator.validateId(ele));
			System.out.println(YoutubeValidator.getVideoTitle(ele));
		}
		long end = System.currentTimeMillis();
		System.out.println(end - init);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
