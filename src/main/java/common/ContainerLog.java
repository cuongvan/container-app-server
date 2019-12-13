/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

/**
 *
 * @author cuong
 */
public class ContainerLog {
    public String stdout;
    public String stderr;

    public ContainerLog(String stdout, String stderr) {
        this.stdout = stdout;
        this.stderr = stderr;
    }
    
    @Override
    public String toString() {
        return String.format("<ContainerLog stdout=[%s] stderr=[%s]>", stdout, stderr);
    }
}
