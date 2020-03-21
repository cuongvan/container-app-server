package externalapi.appinfo.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class AppInfo {
    private String appId;
    private String appName;
    private String avatarUrl;
    private AppType type;
    private String slugName;
    private String codePath;
    private String image;
    private String imageId;
    private String owner;
    private String description;
    private SupportLanguage language;
    private AppStatus appStatus;
    private LocalDateTime createdAt;
    private List<AppParam> params = new ArrayList<>();

    public String getAppId() {
        return appId;
    }

    public String getAppName() {
        return appName;
    }
    
    public String getAvatarPath() {
        return avatarUrl;
    }

    public AppType getType() {
        return type;
    }

    public String getSlugName() {
        return slugName;
    }
    
    public String getCodePath() {
        return codePath;
    }

    public String getImage() {
        return image;
    }

    public String getImageId() {
        return imageId;
    }
    
    public String getOwner() {
        return owner;
    }

    public String getDescription() {
        return description;
    }

    public SupportLanguage getLanguage() {
        return language;
    }

    public AppStatus getAppStatus() {
        return appStatus;
    }

    public String getCreatedAt() {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(createdAt);
    }
    
    public List<AppParam> getParams() {
        return params;
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
        if (!Objects.equals(this.codePath, other.codePath)) {
            return false;
        }
        if (!Objects.equals(this.image, other.image)) {
            return false;
        }
        if (!Objects.equals(this.imageId, other.imageId)) {
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
        if (this.appStatus != other.appStatus) {
            return false;
        }
        if (!Objects.equals(this.params, other.params)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "AppInfo{" + "appId=" + appId + ", appName=" + appName + ", avatarUrl=" + avatarUrl + ", type=" + type + ", slugName=" + slugName + ", image=" + image + ", owner=" + owner + ", description=" + description + ", language=" + language + ", status=" + appStatus + ", params=" + params + '}';
    }

    public AppInfo setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public AppInfo setAppName(String appName) {
        this.appName = appName;
        return this;
    }

    public AppInfo setAvatarPath(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public AppInfo setType(AppType type) {
        this.type = type;
        return this;
    }

    public AppInfo setSlugName(String slugName) {
        this.slugName = slugName;
        return this;
    }

    public AppInfo setCodePath(String codePath) {
        this.codePath = codePath;
        return this;
    }

    public AppInfo setImage(String image) {
        this.image = image;
        return this;
    }

    public AppInfo setImageId(String imageId) {
        this.imageId = imageId;
        return this;
    }

    public AppInfo setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public AppInfo setDescription(String description) {
        this.description = description;
        return this;
    }

    public AppInfo setLanguage(SupportLanguage language) {
        this.language = language;
        return this;
    }

    public AppInfo setAppStatus(AppStatus status) {
        this.appStatus = status;
        return this;
    }

    public AppInfo addParam(AppParam param) {
        this.params.add(param);
        return this;
    }

    public AppInfo setCreatedAt(LocalDateTime time) {
        this.createdAt = time;
        return this;
    }
}
