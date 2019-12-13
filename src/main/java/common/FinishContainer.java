/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import com.github.dockerjava.api.command.InspectContainerResponse;
import java.time.Duration;
import java.time.Instant;

/**
 *
 * @author cuong
 */
public class FinishContainer {
    public String appName;
    public String callId;
    public long executionSeconds;

    @Override
    public String toString() {
        return String.format("<%s app=%s id=%s time=%d>", getClass().getSimpleName(), appName, callId, executionSeconds);
    }
    
//    public static FinishContainer fromInspect(InspectContainerResponse inspect) {
//        FinishContainer fc = new FinishContainer();
//                    fc.appName = container.appName;
//                    fc.callId = container.callId;
//                    Instant t1 = Instant.parse(inspect.getState().getStartedAt());
//                    Instant t2 = Instant.parse(inspect.getState().getFinishedAt());
//                    fc.executionSeconds = Duration.between(t1, t2).getSeconds();
//    }
}
