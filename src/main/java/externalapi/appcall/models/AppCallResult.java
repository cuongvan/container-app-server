package externalapi.appcall.models;

public class AppCallResult {
    private String containerId;
    private boolean success;
    private long elapsedSeconds;
    private String stdout;
    private String stderr;

    public AppCallResult(String containerId, boolean success, long elapsedSeconds, String stdout, String stderr) {
        this.containerId = containerId;
        this.success = success;
        this.elapsedSeconds = elapsedSeconds;
        this.stdout = stdout;
        this.stderr = stderr;
    }

    @Override
    public String toString() {
        return "AppCallResult{" + "containerId=" + containerId + ", success=" + success + ", duration=" + elapsedSeconds + ", stdout=" + stdout.trim() + ", stderr=" + stderr.trim() + '}';
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

    public long getElapsedSeconds() {
        return elapsedSeconds;
    }
}
