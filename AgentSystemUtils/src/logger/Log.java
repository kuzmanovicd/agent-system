package logger;

public class Log {

    public static void out(String msg) {
		System.out.println(msg);
	}
    
    
	public static void out(Object obj, String msg) {
		out("###### " + obj.getClass().getName() + " - " + msg);
	}
}
