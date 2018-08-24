package functionality;

public class PerformanceMeasurerer {
// very basic performance/ time measurements
	private static long start = 0L;
	private static boolean running = false;
	
	
	public static void start() {
		if (!running) {
			running = true;
			start = System.nanoTime();
		}
	}
	
	public static void stop() {
		if (running) {
			running = false;
			System.out.println((System.nanoTime() - start));
		}
	}

}
