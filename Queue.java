import java.util.ArrayList;

public class Queue {
	
	ArrayList<String> queue = new ArrayList<String>();
	
	public void enqueue(String s) {
		queue.add(s);
	}
	
	public String dequeue() {
		String s = queue.get(0);
		queue.remove(s);
		return s;
	}
	
	public boolean isEmpty() {
		return queue.isEmpty();
	}
}
