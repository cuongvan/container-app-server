package externalapi.appinfo.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AppDetail {
    private String appId;
    private String appName;
    private String avatarUrl;
    private AppType type;
    private String slugName;
    private String codePath;
    private String image;
    private String imageId;
    private String owner;
    private String organization;
    private String description;
    private SupportLanguage language;
    private LocalDateTime createdAt;
    private AppStatus appStatus;
    private SysStatus sysStatus;
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

    public String getOrganization() {
        return organization;
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

    public SysStatus getSysStatus() {
        return sysStatus;
    }
    
    public String getCreatedAt() {
        if (createdAt == null)
            return null;
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(createdAt);
    }
    
    public List<AppParam> getParams() {
        return params;
    }

    public AppDetail setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public AppDetail setAppName(String appName) {
        this.appName = appName;
        return this;
    }

    public AppDetail setAvatarPath(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public AppDetail setType(AppType type) {
        this.type = type;
        return this;
    }

    public AppDetail setSlugName(String slugName) {
        this.slugName = slugName;
        return this;
    }

    public AppDetail setCodePath(String codePath) {
        this.codePath = codePath;
        return this;
    }

    public AppDetail setImage(String image) {
        this.image = image;
        return this;
    }

    public AppDetail setImageId(String imageId) {
        this.imageId = imageId;
        return this;
    }

    public AppDetail setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public AppDetail setOrganization(String organization) {
        this.organization = organization;
        return this;
    }

    public AppDetail setDescription(String description) {
        this.description = description;
        return this;
    }

    public AppDetail setLanguage(SupportLanguage language) {
        this.language = language;
        return this;
    }

    public AppDetail setAppStatus(AppStatus status) {
        this.appStatus = status;
        return this;
    }

    public AppDetail setSysStatus(SysStatus sysStatus) {
        this.sysStatus = sysStatus;
        return this;
    }

    public AppDetail addParam(AppParam param) {
        this.params.add(param);
        return this;
    }

    public AppDetail setCreatedAt(LocalDateTime time) {
        this.createdAt = time;
        return this;
    }

    @Override
    public String toString() {
        return "AppInfo{" + "appId=" + appId + ", appName=" + appName + ", avatarUrl=" + avatarUrl + ", type=" + type + ", slugName=" + slugName + ", codePath=" + codePath + ", image=" + image + ", imageId=" + imageId + ", owner=" + owner + ", description=" + description + ", language=" + language + ", createdAt=" + createdAt + ", appStatus=" + appStatus + ", sysStatus=" + sysStatus + ", params=" + params + '}';
    }
}
