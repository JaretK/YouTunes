import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;

public class YtDownloader{

	public static final String YTDLBINARY_DEFAULT = "Binaries/youtube-dl";
	public static final String YOUTUBE_PREFIX = "https://www.youtube.com/watch?v=";
	public static final String SPLITTING_SEQUENCE = "?v=";

	//TODO: rewrite to use youtube api v3
	
	// contains the song metadata
	private StringProperty artistProperty;
	private StringProperty songTitleProperty;
	private StringProperty videoTitleProperty;

	private String delimiter = "-";

	private String ytBinaryPath = "";

	public DownloaderStatus status;

	/**
	 * Constructor for ytDownloader
	 */
	public YtDownloader() {
		initialize();
	}

	private void initialize() {
		ytBinaryPath = classFilePath() + YTDLBINARY_DEFAULT;
		status = DownloaderStatus.IDLE;
		artistProperty = new SimpleStringProperty();
		videoTitleProperty = new SimpleStringProperty();
		songTitleProperty = new SimpleStringProperty();

	}

	/**
	 * Creates a task object that downloads the video title from
	 * 
	 * @return
	 */
	public Task<String> updateTitlesTask(String ytid) {
		status = DownloaderStatus.RUNNING;
		final String titleFlag = "--get-title";
		Task<String> titleTask = new Task<String>() {
			@Override
			protected String call() throws Exception {
				ProcessBuilder pb = new ProcessBuilder();
				// build command for youtube-dl call
				pb.command(ytBinaryPath, titleFlag, YOUTUBE_PREFIX + ytid);
				Process p = pb.start();
				AbstractStreamGobbler inputGob = new RawStreamGobbler(
						p.getInputStream());
				AbstractStreamGobbler errorGob = new RawStreamGobbler(
						p.getErrorStream());
				StringBuilder sb = new StringBuilder();
				inputGob.start();
				errorGob.start();
				while (p.isAlive()) {
					if (isCancelled()) {
						return "";
					}
					// pass while process is active
				}
				inputGob.terminate();
				errorGob.terminate();
				while (!inputGob.isEmpty()) {
					sb.append(inputGob.poll());
				}
				while(!errorGob.isEmpty()){
					System.err.println(errorGob.poll());
				}
				System.out.println();
				return sb.toString();
			}
		};

		titleTask.setOnSucceeded((event) -> {
			updateTitle(titleTask.getValue(), delimiter);
			System.out.println(this.getVideoTitleProperty());
			status = DownloaderStatus.IDLE;
		});

		return titleTask;
	}

	private void updateTitle(String title, String delimiter) {
		//delimiter found once, probably a good choice as delimiter
		if (countMatches(title, delimiter) == 1) {
			// 0 - artist, 1 - title
			String[] titleArray = title.split(delimiter);
			String artist = titleArray[0].trim();
			String songTitle = titleArray[1].trim();
			artistProperty.set(artist);
			songTitleProperty.set(songTitle);
		}
		//delimiter absent or occurs too frequently, don't auto parse
		else{
			artistProperty.set("");
			songTitleProperty.set("");
		}
		videoTitleProperty.set(title);
	}

	/**
	 * Counts number of occurrences of substr in str
	 * 
	 * @param str
	 *            String sequence to search
	 * @param substr
	 *            String subsequence to find in str
	 * @return number of occurrences of substr in str
	 */
	private int countMatches(String str, String subStr) {
		int lastIndex = 0;
		int count = 0;
		while (lastIndex != -1) {
			lastIndex = str.indexOf(subStr, lastIndex);
			if (lastIndex != -1) {
				count++;
				lastIndex += subStr.length();
			}
		}
		return count;
	}

	private String classFilePath() {
		String in = this.getClass().getProtectionDomain().getCodeSource()
				.getLocation().getPath();
		in = in.replaceAll("/bin", "");
		in = in.replaceAll("%20", " ");
		String app = ".app/Contents/";
		if (in.contains(app)) {
			int index = in.indexOf(app);
			in = in.substring(0, index + app.length());
		}
		System.out.println(in);
		return in;
	}

	public void setYtdlBinaryPath(String path) {
		ytBinaryPath = path;
	}

	public String getYtdlBinaryPath() {
		return ytBinaryPath;
	}

	public StringProperty getArtistProperty() {
		return artistProperty;
	}

	public StringProperty getSongTitleProperty() {
		return songTitleProperty;
	}

	public StringProperty getVideoTitleProperty() {
		return videoTitleProperty;
	}

	public void setDelimiter(String newDelimiter) {
		delimiter = newDelimiter;
	}

	public enum DownloaderStatus {
		IDLE, RUNNING;
	}
}
