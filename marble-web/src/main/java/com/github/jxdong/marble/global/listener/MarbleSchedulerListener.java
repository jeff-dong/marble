package com.github.jxdong.marble.global.listener;

import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/11/16 21:19
 */
public class MarbleSchedulerListener implements SchedulerListener{
    private static final Logger logger = LoggerFactory.getLogger(MarbleSchedulerListener.class);

    @Override
    public void jobScheduled(Trigger trigger) {
        logger.info("Job scheduled. {}", trigger);
    }

    @Override
    public void jobUnscheduled(TriggerKey triggerKey) {
        logger.info("Job unscheduled. {}", triggerKey);
    }

    @Override
    public void triggerFinalized(Trigger trigger) {
        logger.info("Trigger finalized. {}", trigger);
    }

    @Override
    public void triggerPaused(TriggerKey triggerKey) {
        logger.info("Trigger paused. {}", triggerKey);
    }

    @Override
    public void triggersPaused(String s) {
        logger.info("Triggers paused. {}", s);
    }

    @Override
    public void triggerResumed(TriggerKey triggerKey) {
        logger.info("Trigger resumed. {}", triggerKey);
    }

    @Override
    public void triggersResumed(String s) {
        logger.info("Triggers resumed. {}", s);
    }

    @Override
    public void jobAdded(JobDetail jobDetail) {
        logger.info("Job added. {}", jobDetail);
    }

    @Override
    public void jobDeleted(JobKey jobKey) {
        logger.info("Job deleted. {}", jobKey);
    }

    @Override
    public void jobPaused(JobKey jobKey) {
        logger.info("Job paused. {}", jobKey);
    }

    @Override
    public void jobsPaused(String s) {
        logger.info("Jobs paused. {}", s);
    }

    @Override
    public void jobResumed(JobKey jobKey) {
        logger.info("Job resumed. {}", jobKey);
    }

    @Override
    public void jobsResumed(String s) {
        logger.info("Jobs resumed. {}", s);
    }

    @Override
    public void schedulerError(String s, SchedulerException e) {
        logger.info("Scheduler error. {}, {}", s, e);
    }

    @Override
    public void schedulerInStandbyMode() {
        logger.info("Scheduler in standBy.");
    }

    @Override
    public void schedulerStarted() {
        logger.info("Scheduler started.");
    }

    @Override
    public void schedulerStarting() {
        logger.info("Scheduler starting.");
    }

    @Override
    public void schedulerShutdown() {
        logger.info("Scheduler shutdown.");
    }

    @Override
    public void schedulerShuttingdown() {
        logger.info("Scheduler shutting down.");
    }

    @Override
    public void schedulingDataCleared() {
        logger.info("Scheduling data cleared.");
    }
}
