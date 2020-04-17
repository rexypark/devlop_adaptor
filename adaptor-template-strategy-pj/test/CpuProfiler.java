import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
 
/**
 * Purpose: get CPU usage of whole java application (in percents).
 * 
 * @author Igar Sushko
 */
public class CpuProfiler extends Thread
{
    private final long interval;
    private final double processorTimeAvailable;
    private boolean continueProfiling = true;
    private static final int MILLISEC_IN_SEC = 1000;
    private static final int NANOSEC_IN_SEC = 1000000000;
    private ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    private Map<Long, Stat> threadStats = new HashMap<Long, Stat>();
    private UpdateHandler updateHandler;
 
    private static class Stat
    {
        long prevCpuTime;
        long currCpuTime;
    }
 
    /** CPU load percentage will be passed to onUpdate method */
    public interface UpdateHandler
    {
        /**
         * @param cpuLoad
         *            CPU load in percents.
         */
        void onUpdate(double cpuLoad);
    }
 
    /**
     * Constructor.
     * 
     * @param interval
     *            how often update stats, in milliseconds.
     */
    public CpuProfiler(long interval)
    {
        super("Thread timing monitor");
        this.interval = interval;
        setDaemon(true);
 
        OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
        int processorsCount = osMXBean.getAvailableProcessors();
 
        processorTimeAvailable = (double) this.interval / MILLISEC_IN_SEC * processorsCount;
    }
 
    /** Thread meat. */
    @Override
    public void run()
    {
        while (continueProfiling)
        {
            updateStats();
 
            double usedCpu = (double) getUsedCpuTimeForLastPeriod() / NANOSEC_IN_SEC;
            double cpuLoad = (double) usedCpu * 100 / processorTimeAvailable;
 
            if (updateHandler != null)
            {
                updateHandler.onUpdate(cpuLoad);
            }
 
            try
            {
                Thread.sleep(interval);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
 
    /**
     * Update stats, current monitor thread is also included into stats.
     */
    private void updateStats()
    {
        long[] ids = threadMXBean.getAllThreadIds();
        for (long id : ids)
        {
            long cpuTime = threadMXBean.getThreadCpuTime(id);
            if (cpuTime == -1)// thread is not alive
            {
                threadStats.remove(id);
                continue;
            }
 
            Stat stat = threadStats.get(id);
            if (stat == null)// thread is not monitored yet
            {
                stat = new Stat();
                stat.prevCpuTime = cpuTime;
                stat.currCpuTime = cpuTime;
                threadStats.put(id, stat);
            }
            else
            // thread is already monitored
            {
                stat.prevCpuTime = stat.currCpuTime;
                stat.currCpuTime = cpuTime;
            }
        }
    }
 
    /** Get total used CPU time in nanoseconds for last interval. */
    private long getUsedCpuTimeForLastPeriod()
    {
        long time = 0;
        for (Stat threadStat : threadStats.values())
        {
            time += threadStat.currCpuTime - threadStat.prevCpuTime;
        }
 
        return time;
    }
 
    public void setUpdateHandler(UpdateHandler updateHandler)
    {
        this.updateHandler = updateHandler;
    }
 
    public void stopProfiling()
    {
        this.continueProfiling = false;
    }
    
    
    public static void main(String[] args) {
    	CpuProfiler profiler = new CpuProfiler(1000);// get update each 1 sec
    	profiler.setUpdateHandler(new CpuProfiler.UpdateHandler()
    	{
    	    public void onUpdate(double cpuLoad)
    	    {
    	    	System.out.println(cpuLoad);
    	    	String p = String.valueOf(cpuLoad * 100.0D);

    			int ix = p.indexOf(".") + 1;
    			String percent = p.substring(0, ix) + p.substring(ix, ix + 1);
    	    	
    	       System.out.println("CPU Load: " + percent + "%");
    	    }
    	});
    	new Thread(profiler).start();
	}
    
    public static String format(double val) {
		String p = String.valueOf(val * 100.0D);

		int ix = p.indexOf(".") + 1;
		String percent = p.substring(0, ix) + p.substring(ix, ix + 1);

		return percent;
	}
}