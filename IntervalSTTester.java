package intervalSearchTree;

public class IntervalSTTester {
	public static void main(String[] args) {
		int N = 10;
		IntervalSearchTree<String> st = new IntervalSearchTree<String> ();
		for (int i = 0; i < N; i++) {
			int low = (int)(Math.random() * 100);
			int high = (int) (Math.random() * 50) + low;
			Interval interval = new Interval(low, high);
			System.out.println(interval.toString());
			st.put(interval, String.valueOf(i));
		}
		st.put(new Interval(89, 123), String.valueOf(N));
		System.out.println("height:  " + st.height());
		System.out.println("size:  " + st.size());
		System.out.println("integrity check: " + st.check());
		System.out.println();
		st.remove(new Interval(89, 123));
		System.out.println("height:  " + st.height());
		System.out.println("size:  " + st.size());
		System.out.println("integrity check: " + st.check());
		System.out.println();
		
		/*for (int i = 0; i < N; i++) {
			int low = (int)(Math.random() * 1000);
			int high = (int) (Math.random() * 10) + low;
			Interval interval = new Interval(low, high);
			System.out.println(interval.toString() + ": ");
			for (Interval in : st.searchAll(interval)) 
				System.out.println(in.toString() + "  ");
			System.out.println();
		}*/
	}
}
