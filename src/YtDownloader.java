
public class YtDownloader {

	public static final String ytdlBinary_default = "Resources/youtube-dl";
	
	String ytBinaryPath = "";
	String ytid = "";
	
	public YtDownloader(String ytid){
		initialize();
		this.ytid = ytid;
	}
	
	private void initialize(){
		ytBinaryPath = classFilePath()
				+ ytdlBinary_default;
	}
	
	public void setYtdlBinaryPath(String path){
		ytBinaryPath = path;
	}
	
	
	private String classFilePath(){
		String in = this.getClass().getProtectionDomain().getCodeSource()
				.getLocation().getPath();
		in = in.replaceAll("/bin", "");
		in = in.replaceAll("%20", " ");
		String app = ".app/Contents/";
		if (in.contains(app)) {
			int index = in.indexOf(app);
			in = in.substring(0, index + app.length());
		}
		return in;
	}
}
