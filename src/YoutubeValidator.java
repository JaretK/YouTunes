import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;

public class YoutubeValidator {

	// file containing the api key used for youtube api requests
	private static final String PROPERTIES_FILENAME = "youtube.properties";
	// identifier in the properties file
	private static final String API_KEY_ID = "youtube.apikey";

	private static YouTube youtube;
	private static String api_key;

	// Map for storing ytid, snippet mappings
	private static Map<String, VideoSnippet> cache;

	// initialize all accessed variables
	static {
		HttpRequestInitializer HttpInit = new HttpRequestInitializer() {
			public void initialize(HttpRequest arg0) throws IOException {
			}
		};
		youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY,
				HttpInit).setApplicationName("YoutubeValidator").build();
		api_key = loadApiKey();
		cache = new HashMap<>();
	}

	/**
	 * Loads api key from the properties file into the api_key variable
	 * 
	 * @return
	 */
	private static String loadApiKey() {
		Properties properties = new Properties();
		String basePath = classFilePath();
		try {
			InputStream in = new FileInputStream(basePath + "Resources/"
					+ PROPERTIES_FILENAME);
			properties.load(in);
		} catch (IOException e) {
			System.err.println("Error reading " + PROPERTIES_FILENAME + " : "
					+ e.getCause() + " : " + e.getMessage());
			System.exit(1);
		}
		return properties.getProperty(API_KEY_ID);
	}

	/**
	 * 
	 * @return The file path of the compiled program or the execution
	 *         environment (pre-deploy, testing, etc)
	 */
	public static String classFilePath() {
		// workaround for disallowance of this keyword in static method
		// get the method handle of this class and then lookup the current
		// execution class, which is static
		String in = MethodHandles.lookup().lookupClass().getProtectionDomain()
				.getCodeSource().getLocation().getPath();
		in = in.replaceAll("/bin", "");
		in = in.replaceAll("%20", " ");
		final String app = ".app/Contents/";
		if (in.contains(app)) {
			int index = in.indexOf(app);
			in = in.substring(0, index + app.length());
		}
		return in;
	}

	/**
	 * 
	 * @param ytid
	 *            YouTube unique ID
	 * @return True iff the id maps to one video
	 */
	public static boolean validateId(String ytid) {
		try {
			YouTube.Videos.List video = youtube.videos().list("snippet");
			video.setKey(api_key);
			video.setId(ytid);
			VideoListResponse response = video.execute();
			List<Video> responseList = response.getItems();

			// ids are unique
			if (responseList.size() == 1) {
				cache.put(ytid, responseList.get(0).getSnippet());
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param ytid
	 *            YouTube unique video ID
	 * @return title of the video
	 */
	public static String getVideoTitle(String ytid) {
		// cache to improve performance
		// validateID should be called first to benefit from performance boost
		if (cache.containsKey(ytid)) {
			return cache.get(ytid).getTitle();
		}
		try {
			YouTube.Videos.List video = youtube.videos().list("snippet");
			video.setKey(api_key);
			video.setId(ytid);
			VideoListResponse response = video.execute();
			List<Video> responseList = response.getItems();
			if (responseList.size() == 1) {
				VideoSnippet snippet = responseList.get(0).getSnippet();
				return snippet.getTitle();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	private static class Auth {
		public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
		public static final JsonFactory JSON_FACTORY = new JacksonFactory();
	}
}