/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package workers;

import java.util.concurrent.Executor;
import utils.ExecutorUtil;

/**
 *
 * @author cuong
 */
public class ExecuteBatchAppWorker {
    public static Executor exec = ExecutorUtil.newExecutor(0, 3);
    
    public static void submit() {
        
    }
}
