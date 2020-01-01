/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workers;

import common.AppCallInfo;
import common.AppCallInfo.*;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import notifications.Event;
import notifications.EventType;
import notifications.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HttpUtil;

/**
 *
 * @author cuong
 */
public class ScheduleAppWorker {
    private final Logger LOGGER = LoggerFactory.getLogger(ScheduleAppWorker.class);
    public static final ScheduleAppWorker Instance = new ScheduleAppWorker();
    private Executor exec = Executors.newSingleThreadExecutor();
    
    private ScheduleAppWorker() {
    }
    
    public void submit(AppCallInfo callInfo, Runnable startContainer) {
        //TODO schedule
        exec.execute(() -> appWrapper(callInfo, startContainer));
    }
    
    public void appWrapper(AppCallInfo callInfo, Runnable startContainer) {
        startContainer.run();
        try {
            if (callInfo instanceof ServerAppCallInfo) {
                ServerAppCallInfo info = (ServerAppCallInfo) callInfo;
                HttpUtil.post("http://localhost:5000/notify/server/" + info.appId,
                    new Event(EventType.Build, Status.Success));
            }
            else if (callInfo instanceof BatchAppCallInfo) {
                BatchAppCallInfo info = (BatchAppCallInfo) callInfo;
                HttpUtil.post("http://localhost:5000/notify/batch/" + info.callId,
                    new Event(EventType.Execute, Status.Started));
            }
            LOGGER.info("Execution notification sent");
        } catch (IOException ex) {
            LOGGER.warn("Exception send http notification", ex);
        }
    }
}
