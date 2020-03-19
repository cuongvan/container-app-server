package externalapi.appinfo.models;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
    private AppStatus status;
    private List<AppParam> params = new ArrayList<>();

    public String getAppId() {
        return appId;
    }

    public String getAppName() {
        return appName;
    }
    
    public String getAvatarUrl() {
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

    public AppStatus getStatus() {
        return status;
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
        if (this.status != other.status) {
            return false;
        }
        if (!Objects.equals(this.params, other.params)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return "AppInfo{" + "appId=" + appId + ", appName=" + appName + ", avatarUrl=" + avatarUrl + ", type=" + type + ", slugName=" + slugName + ", image=" + image + ", owner=" + owner + ", description=" + description + ", language=" + language + ", status=" + status + ", params=" + params + '}';
    }

    public AppInfo withAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public AppInfo withAppName(String appName) {
        this.appName = appName;
        return this;
    }

    public AppInfo withAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public AppInfo withType(AppType type) {
        this.type = type;
        return this;
    }

    public AppInfo withSlugName(String slugName) {
        this.slugName = slugName;
        return this;
    }

    public AppInfo withCodePath(String codePath) {
        this.codePath = codePath;
        return this;
    }

    public AppInfo withImage(String image) {
        this.image = image;
        return this;
    }

    public AppInfo withImageId(String imageId) {
        this.imageId = imageId;
        return this;
    }

    public AppInfo withOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public AppInfo withDescription(String description) {
        this.description = description;
        return this;
    }

    public AppInfo withLanguage(SupportLanguage language) {
        this.language = language;
        return this;
    }

    public AppInfo withStatus(AppStatus status) {
        this.status = status;
        return this;
    }

    public AppInfo addParam(AppParam param) {
        this.params.add(param);
        return this;
    }
}
