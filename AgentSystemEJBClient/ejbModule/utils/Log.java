package utils;

public class Log {

	public static void out(String msg) {
		System.out.println(msg);

	}

	public static void out(Object obj, String msg) {
		//Random rand = new Random();
		//String time = rand.nextInt(10000) + "";
		String time = "";
		out("###### " + obj.getClass().getName() + " - " + msg + " " + time);
	}
}
