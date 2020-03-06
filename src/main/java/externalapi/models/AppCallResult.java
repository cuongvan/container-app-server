package externalapi.models;

public class AppCallResult {
    private String containerId;
    private boolean success;
    private long duration;
    private String stdout;
    private String stderr;

    public AppCallResult(String containerId, boolean success, long duration, String stdout, String stderr) {
        this.containerId = containerId;
        this.success = success;
        this.duration = duration;
        this.stdout = stdout;
        this.stderr = stderr;
    }

    @Override
    public String toString() {
        return "AppCallResult{" + "containerId=" + containerId + ", success=" + success + ", duration=" + duration + ", stdout=" + stdout.trim() + ", stderr=" + stderr.trim() + '}';
    }
    
    
    public String getContainerId() {
        return containerId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getStdout() {
        return stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public long getDuration() {
        return duration;
    }
}
