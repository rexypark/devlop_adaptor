public class TestMemory {

	public static void main(String[] args) {

		int mb = 1024 * 1024;
		Runtime runtime = Runtime.getRuntime();
		System.out.println("##### Heap utilization statistics [MB] #####");
		System.out.println("Used Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / mb);
		System.out.println("Free Memory:" + runtime.freeMemory() / mb);
		System.out.println("Total Memory:" + runtime.totalMemory() / mb);
		System.out.println("Max Memory:" + runtime.maxMemory() / mb);
		
	}
}