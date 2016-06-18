import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Generic steam gobbler class that stores all information passed into it to a
 * buffer, which can then be accessed by a simple API
 * 
 * @author jkarnuta
 *
 */
public class RawStreamGobbler extends AbstractStreamGobbler {

	public RawStreamGobbler(InputStream is) {
		super(is);
	}

	@Override
	public void run() {
		try {
			InputStreamReader reader = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(reader);
			String line;
			while (true) {
				while ((line = br.readLine()) != null) {
					q.add(line);
					if (terminate) {
						return;
					}
				}
			}
		} catch (IOException ioe) {
			q.add(ioe.getMessage());
		}
	}
}
