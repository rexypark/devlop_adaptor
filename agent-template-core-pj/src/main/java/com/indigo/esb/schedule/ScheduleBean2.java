package com.indigo.esb.schedule;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class ScheduleBean2 implements InitializingBean, DisposableBean {

	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub

	}
	//
	// private static Log log = LogFactory.getLog(ScheduleBean2.class);
	//
	// private String adaptorName;
	//
	// private List<InterfaceInfo> interfaceList = new
	// ArrayList<InterfaceInfo>();
	//
	// private SchedulerFactoryBean sfb;
	//
	// private Object targetObject;
	//
	// private int threadCount;
	//
	// private boolean waitForJobsToCompleteOnShutdown = true;
	//
	// private int idx = 0;
	//
	// @Override
	// public void afterPropertiesSet() throws Exception {
	// Assert.notNull(targetObject, "Property 'targetObject' is Required");
	// Assert.notNull(adaptorName, "Property 'adaptorName' is Required");
	// Assert.notEmpty(interfaceList,
	// "At least one InterfaceInfo needs to be specified");
	// try {
	// Properties p = new Properties();
	// if (this.threadCount == 0) {
	// this.threadCount = this.interfaceList.size();
	// }
	// p.put("org.quartz.threadPool.threadCount",
	// String.valueOf(this.threadCount));
	// p.put("org.quartz.scheduler.skipUpdateCheck","true");
	// this.sfb = new SchedulerFactoryBean();
	// this.sfb.setQuartzProperties(p);
	//
	// List<Trigger> triggerList = new ArrayList<Trigger>();
	// for (InterfaceInfo ifInfo : interfaceList) {
	// if (ifInfo.getInterfaceDirection() == InterfaceDirection.SEND) {
	// triggerList.add(makeTrigger(ifInfo));
	// log.info("������ �߰���:" + ifInfo.toLongString());
	// }
	// }
	//
	// this.sfb.setTriggers(triggerList
	// .toArray(new Trigger[0]));
	// this.sfb.setWaitForJobsToCompleteOnShutdown(getWaitForJobsToCompleteOnShutdown());
	//
	// this.sfb.afterPropertiesSet();
	// this.sfb.start();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	// private Trigger makeTrigger(InterfaceInfo ifInfo) throws Exception {
	// MethodInvokingJobDetailFactoryBean mijdfb = new
	// MethodInvokingJobDetailFactoryBean();
	// mijdfb.setBeanName(this.adaptorName + "#jobdetail-" + this.idx++);
	// mijdfb.setConcurrent(ifInfo.getConcurrent());
	// mijdfb.setTargetObject(getTargetObject());
	// mijdfb.setTargetMethod("onSignal");
	// Object[] args = { ifInfo };
	// mijdfb.setArguments(args);
	// mijdfb.afterPropertiesSet();
	//
	// if (ifInfo.isCronJob()) {
	// CronTriggerBean cronTrgr = new CronTriggerBean();
	// cronTrgr.setBeanName(this.adaptorName + "#cronTriger-" + this.idx);
	// // cronTrgr.setCronExpression(ifInfo.getCronExpression());
	// cronTrgr.setJobDetail(mijdfb.getObject());
	//
	// cronTrgr.afterPropertiesSet();
	// return cronTrgr;
	// } else {
	// SimpleTriggerBean smplTrgr = new SimpleTriggerBean();
	// smplTrgr.setStartDelay(ifInfo.getStartDelay());
	// // smplTrgr.setRepeatInterval(ifInfo.getInterval());
	// smplTrgr.setJobDetail(mijdfb.getObject());
	// smplTrgr.setBeanName(this.adaptorName + "#simpleTrigger-"
	// + this.idx);
	//
	// smplTrgr.afterPropertiesSet();
	// return smplTrgr;
	// }
	//
	// }
	//
	// @Override
	// public void destroy() throws Exception {
	// this.sfb.destroy();
	// }
	//
	// public void setWaitForJobsToCompleteOnShutdown(
	// boolean waitForJobsToCompleteOnShutdown) {
	// this.waitForJobsToCompleteOnShutdown = waitForJobsToCompleteOnShutdown;
	// }
	//
	// public boolean getWaitForJobsToCompleteOnShutdown() {
	// return this.waitForJobsToCompleteOnShutdown;
	// }
	//
	// public void setAdaptorName(String adaptorName) {
	// this.adaptorName = adaptorName;
	// }
	//
	// public Object getTargetObject() {
	// return this.targetObject;
	// }
	//
	// public void setTargetObject(Object targetObject) {
	// this.targetObject = targetObject;
	// }
	//
	// public void setInterfaceList(List<InterfaceInfo> interfaceList) {
	// this.interfaceList = interfaceList;
	// }
	//
	// public List<InterfaceInfo> getInterfaceList() {
	// return interfaceList;
	// }
	//
	// public int getThreadCount() {
	// return this.threadCount;
	// }
	//
	// public void setThreadCount(int threadCount) {
	// this.threadCount = threadCount;
	// }
	//
	// public Scheduler getScheduler() {
	// return this.sfb.getScheduler();
	//
	// }
}