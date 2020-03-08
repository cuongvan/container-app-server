package externalapi.appinfo.models;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.*;
import java.util.Objects;

@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class AppInfo {
    private String appId;
    private String appName;
    private String avatarUrl;
    private AppType type;
    private String slugName;
    private String image;
    private String owner;
    private String description;
    private int hostPort;
    private int containerPort;
    private SupportLanguage language;
    private AppStatus status;
    

    public String getAppId() {
        return appId;
    }

    public AppInfo setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public String getAppName() {
        return appName;
    }

    public AppInfo setAppName(String appName) {
        this.appName = appName;
        return this;
    }
    
    @JsonProperty("ava_url")
    public String getAvatarUrl() {
        return avatarUrl;
    }

    @JsonProperty("ava_url")
    public AppInfo setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }
    
    public AppType getType() {
        return type;
    }

    public AppInfo setType(AppType type) {
        this.type = type;
        return this;
    }

    public String getSlugName() {
        return slugName;
    }

    public AppInfo setSlugName(String slugName) {
        this.slugName = slugName;
        return this;
    }

    public String getImage() {
        return image;
    }

    public AppInfo setImage(String image) {
        this.image = image;
        return this;
    }

    public String getOwner() {
        return owner;
    }

    public AppInfo setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public AppInfo setDescription(String description) {
        this.description = description;
        return this;
    }

    public int getHostPort() {
        return hostPort;
    }

    public AppInfo setHostPort(int hostPort) {
        this.hostPort = hostPort;
        return this;
    }

    public int getContainerPort() {
        return containerPort;
    }

    public AppInfo setContainerPort(int containerPort) {
        this.containerPort = containerPort;
        return this;
    }

    public SupportLanguage getLanguage() {
        return language;
    }

    public AppInfo setLanguage(SupportLanguage language) {
        this.language = language;
        return this;
    }

    public AppStatus getStatus() {
        return status;
    }

    public AppInfo setStatus(AppStatus status) {
        this.status = status;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AppInfo other = (AppInfo) obj;
        if (this.hostPort != other.hostPort) {
            return false;
        }
        if (this.containerPort != other.containerPort) {
            return false;
        }
        if (!Objects.equals(this.appId, other.appId)) {
            return false;
        }
        if (!Objects.equals(this.appName, other.appName)) {
            return false;
        }
        if (!Objects.equals(this.avatarUrl, other.avatarUrl)) {
            return false;
        }
        if (!Objects.equals(this.slugName, other.slugName)) {
            return false;
        }
        if (!Objects.equals(this.image, other.image)) {
            return false;
        }
        if (!Objects.equals(this.owner, other.owner)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (this.language != other.language) {
            return false;
        }
        if (this.status != other.status) {
            return false;
        }
        return true;
    }
    
}
