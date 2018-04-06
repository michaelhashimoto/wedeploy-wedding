package io.wedeploy.wedding;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class GuestUpdaterJob implements Job {

	public void execute(JobExecutionContext context)
		throws JobExecutionException {

		try {
			Guest.updateGuests();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void start() throws Exception {
		if (_scheduler == null || _scheduler.isShutdown()) {
			JobDetail jobDetail = JobBuilder.newJob(GuestUpdaterJob.class)
				.withIdentity("guestUpdater", "group")
				.build();

			Trigger trigger = TriggerBuilder
				.newTrigger().withIdentity("cronTrigger", "group")
				.withSchedule(CronScheduleBuilder.cronSchedule("*/30 * * ? * *"))
				.build();

			_scheduler = new StdSchedulerFactory().getScheduler();

			_scheduler.start();

			_scheduler.scheduleJob(jobDetail, trigger);
		}
	}

	public static void stop() throws Exception {
		_scheduler.shutdown();
	}

	private static Scheduler _scheduler;

}