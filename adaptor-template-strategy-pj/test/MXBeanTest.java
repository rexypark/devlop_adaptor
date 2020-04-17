import java.io.File;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;

import com.sun.management.OperatingSystemMXBean;

public class MXBeanTest {

	public static void showCPU() {
		OperatingSystemMXBean osbean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		RuntimeMXBean runbean = (RuntimeMXBean) ManagementFactory.getRuntimeMXBean();

		long bfprocesstime = osbean.getProcessCpuTime();
		long bfuptime = runbean.getUptime();
		long ncpus = osbean.getAvailableProcessors();

		for (int i = 0; i < 1000000; ++i) {
			ncpus = osbean.getAvailableProcessors();
		}

		long afprocesstime = osbean.getProcessCpuTime();
		long afuptime = runbean.getUptime();

		float cal = (afprocesstime - bfprocesstime) / ((afuptime - bfuptime) * 10000f);

		float usage = Math.min(99f, cal);

		System.out.println("Calculation: " + cal);
		System.out.println("CPU Usage: " + usage);

	}

	public static void showDisk() {
		File root = null;

		try {
			root = new File("D:/");

			System.out.println("Total  Space: " + root.getTotalSpace());
			System.out.println("Usable Space: " + root.getUsableSpace());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void showRuntime() {
		RuntimeMXBean runbean = (RuntimeMXBean) ManagementFactory.getRuntimeMXBean();

	}

	public static void showMemory() {
		MemoryMXBean membean = (MemoryMXBean) ManagementFactory.getMemoryMXBean();

		MemoryUsage heap = membean.getHeapMemoryUsage();
		System.out.println("Heap Memory: " + heap.toString());

		MemoryUsage nonheap = membean.getNonHeapMemoryUsage();
		System.out.println("NonHeap Memory: " + nonheap.toString());

	}

	public static void showClassLoading() {
		ClassLoadingMXBean classbean = (ClassLoadingMXBean) ManagementFactory.getClassLoadingMXBean();

		System.out.println("TotalLoadedClassCount: " + classbean.getTotalLoadedClassCount());
		System.out.println("LoadedClassCount: " + classbean.getLoadedClassCount());
		System.out.println("UnloadedClassCount: " + classbean.getUnloadedClassCount());

	}

	public static void showThreadBean() {
		ThreadMXBean tbean = (ThreadMXBean) ManagementFactory.getThreadMXBean();

		long[] ids = tbean.getAllThreadIds();

		System.out.println("Thread Count: " + tbean.getThreadCount());

		for (long id : ids) {
			System.out.println("Thread CPU Time(" + id + ")" + tbean.getThreadCpuTime(id));
			System.out.println("Thread User Time(" + id + ")" + tbean.getThreadCpuTime(id));
		}

	}

	public static void showOSBean() {

		OperatingSystemMXBean osbean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		System.out.println("Available Processors: " + osbean.getAvailableProcessors());
		System.out.println("TotalPhysicalMemorySize: " + osbean.getTotalPhysicalMemorySize());
		System.out.println("FreePhysicalMemorySize: " + osbean.getFreePhysicalMemorySize());
		System.out.println("TotalSwapSpaceSize: " + osbean.getTotalSwapSpaceSize());
		System.out.println("FreeSwapSpaceSize: " + osbean.getFreeSwapSpaceSize());
		System.out.println("CommittedVirtualMemorySize: " + osbean.getCommittedVirtualMemorySize());
		System.out.println("SystemLoadAverage: " + osbean.getSystemLoadAverage());

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MXBeanTest.showOSBean();
		MXBeanTest.showThreadBean();
		MXBeanTest.showClassLoading();
		MXBeanTest.showMemory();
		MXBeanTest.showDisk();
		MXBeanTest.showCPU();
	}

}