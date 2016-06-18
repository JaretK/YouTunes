import java.io.InputStream;
import java.util.concurrent.ConcurrentLinkedQueue;



public abstract class AbstractStreamGobbler extends Thread{
 
	InputStream is;
	ConcurrentLinkedQueue<String> q;
	boolean terminate;
	
	public AbstractStreamGobbler(InputStream is){
		this.is = is;
		initialize();
	}
	
	private void initialize(){
		q = new ConcurrentLinkedQueue<>();
		terminate = false;
	}
	
	public boolean isEmpty(){
		return q.isEmpty();
	};
	public String poll(){
		return q.poll();
	};
	
	public void terminate(){
		terminate = true;
	}
	
	/**
	 * Required due to thread inheritance
	 */
	public abstract void run();
}
