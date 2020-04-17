package com.indigo.esb.schedule;

import static org.quartz.impl.matchers.EverythingMatcher.allJobs;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.util.Assert;

import com.indigo.esb.signal.OnSignalErrorJobListener;

//import com.indigo.esb.config.InterfaceInfo;

public class OldScheduleBean implements InitializingBean, DisposableBean, ApplicationContextAware {
	private static Log logger = LogFactory.getLog(ScheduleBean.class);

	// private String adaptorName;

	// private Map<String, InterfaceInfo> interfaceMap = new HashMap<String,
	// InterfaceInfo>();

	private SchedulerFactoryBean sfb;

	private List<String> timeList = new ArrayList<String>();

	private Object targetObject;

	private int threadCount;

	private boolean autoReloaing = true;

	private boolean waitForJobsToCompleteOnShutdown = true;

	private int idx = 0;

	private String beanName;

	private ApplicationContext applicationContext;

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(targetObject, "Property 'targetObject' is Required");
		// Assert.notNull(adaptorName, "Property 'adaptorName' is Required");
		refreshSchedule();
	}

	public void refreshSchedule() throws Exception {
		try {
			Properties p = new Properties();
			if (this.threadCount == 0) {
				this.threadCount = this.timeList.size() + 1;
			}
			p.put("org.quartz.threadPool.threadCount", String.valueOf(this.threadCount));

			p.put("org.quartz.scheduler.skipUpdateCheck", "true");

			this.sfb = new SchedulerFactoryBean();
			this.sfb.setQuartzProperties(p);

			List<Trigger> triggerList = new ArrayList<Trigger>();
			for (String scheduleExpression : timeList) {
				if (isCronExpress(scheduleExpression))
					triggerList.add(makeCronTrigger(new CronTriggerParam(scheduleExpression)));
				else {
					triggerList.add(makeSimpleTrigger(new SimpleTriggerParam(scheduleExpression)));
				}
			}

			this.sfb.setTriggers(triggerList.toArray(new Trigger[0]));
			this.sfb.setWaitForJobsToCompleteOnShutdown(getWaitForJobsToCompleteOnShutdown());

			// this.sfb.setStartupDelay(10);
			this.sfb.afterPropertiesSet();

			// if (this.autoReloaing) {
			// regiterScheduleCheckJob();
			// }

			OnSignalErrorJobListener  myJobListener=  null;
			try{
				myJobListener = applicationContext.getBean("onSignalErrorJobListener",
						OnSignalErrorJobListener.class);
			}catch(Exception e){
				myJobListener = null;
			}
			
			if (myJobListener != null)
				getScheduler().getListenerManager().addJobListener(myJobListener, allJobs());
			this.sfb.start();
		} catch (Exception e) {
			logger.error(e);
			throw e;
		}

	}

	// private void regiterScheduleCheckJob() throws Exception {
	//
	// ScheduleConfigFileScanListener scheduleConfigFileScanListener = new
	// ScheduleConfigFileScanListener();
	// scheduleConfigFileScanListener.setOldScheduleBean(this);
	// getScheduler().getContext().put("ScheduleConfigFileScanListener",
	// scheduleConfigFileScanListener);
	// JobDataMap jobDataMap = new JobDataMap();
	// Resource resource = new ClassPathResource("bean.xml");
	// String watchFileName = resource.getFile().getAbsolutePath();
	// int checkIntevalSec = 5;
	// logger.info(checkIntevalSec + " :" + watchFileName);
	// jobDataMap.put(FileScanJob.FILE_NAME, watchFileName);
	// jobDataMap.put(FileScanJob.FILE_SCAN_LISTENER_NAME,
	// "ScheduleConfigFileScanListener");
	// JobDetail job = newJob(FileScanJob.class).withIdentity("ScheduleScanJob",
	// "fileScanGroup")
	// .usingJobData(jobDataMap).build();
	// Trigger trigger = newTrigger()
	// .withIdentity("ScheduleScanTrigger", "fileScanGroup")
	// .startNow()
	// .withSchedule(
	// simpleSchedule().withIntervalInSeconds(checkIntevalSec).repeatForever()
	// .withMisfireHandlingInstructionFireNow()).build();
	// getScheduler().scheduleJob(job, trigger);
	// }

	private Trigger makeCronTrigger(CronTriggerParam cron) throws Exception {
		MethodInvokingJobDetailFactoryBean mijdfb = new MethodInvokingJobDetailFactoryBean();
		mijdfb.setName(this.beanName + "#jobdetail-" + (this.idx++));
		mijdfb.setGroup("INDIGOESB");
		mijdfb.setConcurrent(cron.getConcurrent());
		mijdfb.setTargetObject(targetObject);
		mijdfb.setTargetMethod(cron.getTargetMethod());
		Object[] args = { cron.getArgrument() };
		mijdfb.setArguments(args);
		mijdfb.afterPropertiesSet();

		JobDetail jobDetail = mijdfb.getObject();
		jobDetail.getJobDataMap().put("scheduleExpression", cron.getCronExpression());
		jobDetail.getJobDataMap().put("jobDetail", jobDetail);
		CronTriggerFactoryBean cronTrgr = new CronTriggerFactoryBean();
		// cronTrgr.setBeanName(ifInfo.getInterfaceId() + " CronTrigger");
		cronTrgr.setBeanName(this.beanName + "#jobdetail-" + (this.idx++));
		cronTrgr.setGroup("INDIGOESB");
		cronTrgr.setCronExpression(cron.getCronExpression());
		cronTrgr.setJobDetail(jobDetail);
		cronTrgr.afterPropertiesSet();
		return cronTrgr.getObject();

	}

	private Trigger makeSimpleTrigger(SimpleTriggerParam simple) throws Exception {
		MethodInvokingJobDetailFactoryBean mijdfb = new MethodInvokingJobDetailFactoryBean();
		mijdfb.setName(this.beanName + "#jobdetail-" + (this.idx++));
		mijdfb.setGroup("INDIGOESB");
		mijdfb.setConcurrent(simple.getConcurrent());
		mijdfb.setTargetObject(targetObject);
		mijdfb.setTargetMethod(simple.getTargetMethod());
		Object[] args = { simple.getArgrument() };
		mijdfb.setArguments(args);
		mijdfb.afterPropertiesSet();

		JobDetail jobDetail = mijdfb.getObject();
		jobDetail.getJobDataMap().put("jobDetail", jobDetail);

		SimpleTriggerFactoryBean smplTrgr = new SimpleTriggerFactoryBean();
		// smplTrgr.setBeanName(ifInfo.getInterfaceId() + " SimpleTrigger");
		smplTrgr.setBeanName(this.beanName + "#jobdetail-" + (this.idx++));
		smplTrgr.setGroup("INDIGOESB");
		smplTrgr.setStartDelay(simple.getStartDelay());
		smplTrgr.setRepeatInterval(simple.getInterval());
		smplTrgr.setJobDetail(jobDetail);
		smplTrgr.afterPropertiesSet();
		return smplTrgr.getObject();
	}

	private boolean isCronExpress(String scheduleExpression) {
		if (scheduleExpression.trim().charAt(0) != '[') {
			String[] array = scheduleExpression.trim().split(",");
			String value = array[0];
			return (value.trim().charAt(0) != '[') && (value.split(" ").length > 4);
		}
		return scheduleExpression.indexOf("cronExpression") != -1;
	}

	@Override
	public void destroy() throws Exception {
		this.sfb.destroy();
	}

	public void setWaitForJobsToCompleteOnShutdown(boolean waitForJobsToCompleteOnShutdown) {
		this.waitForJobsToCompleteOnShutdown = waitForJobsToCompleteOnShutdown;
	}

	public boolean getWaitForJobsToCompleteOnShutdown() {
		return this.waitForJobsToCompleteOnShutdown;
	}

	public int getThreadCount() {
		return this.threadCount;
	}

	public void setAutoReloaing(boolean autoReloaing) {
		this.autoReloaing = autoReloaing;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public Scheduler getScheduler() {
		return this.sfb.getScheduler();
	}

	public void setTargetObject(Object targetObject) {
		this.targetObject = targetObject;
	}

	public void setTimeList(List<String> timeList) {
		this.timeList = timeList;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}