package com.github.jxdong.marble.global.listener;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2015/12/30 13:01
 */
public class MarbleTriggerListener implements TriggerListener{
    //private static final Logger logger = LoggerFactory.getLogger(MarbleTriggerListener.class);

    @Override
    public String getName() {
        return "MarbleTrigger";
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext jobExecutionContext) {
        //logger.info("Trigger fired. {}", trigger);
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext jobExecutionContext) {
       // logger.info("Trigger veto. {}", trigger);
        return false;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
       // logger.info("Trigger misfire. {}", trigger);
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext jobExecutionContext, Trigger.CompletedExecutionInstruction completedExecutionInstruction) {
      //  logger.info("Trigger complete. {}", trigger);
    }
}
