package externalapi.appinfo.models;

import java.util.Objects;

public class AppInfo {
    private final String appId;
    private final String appName;
    private final String avatarUrl;
    private final AppType type;
    private final String slugName;
    private final String image;
    private final String owner;
    private final String description;
    private final SupportLanguage language;
    private final AppStatus status;
    
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

    public String getImage() {
        return image;
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

    public AppInfo(String appId, String appName, String avatarUrl, AppType type, String slugName, String image, String owner, String description, SupportLanguage language, AppStatus status) {
        this.appId = appId;
        this.appName = appName;
        this.avatarUrl = avatarUrl;
        this.type = type;
        this.slugName = slugName;
        this.image = image;
        this.owner = owner;
        this.description = description;
        this.language = language;
        this.status = status;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    
    public static Builder builder(AppInfo other) {
        return new Builder(other);
    }
    
    public static class Builder {
        private String appId;
        private String appName;
        private String avatarUrl;
        private AppType type = AppType.BATCH;
        private String slugName;
        private String image;
        private String owner;
        private String description;
        private SupportLanguage language;
        private AppStatus status;

        public Builder() {
        }
        
        public Builder(AppInfo other) {
            this.appId = other.appId;
            this.appName = other.appName;
            this.avatarUrl = other.avatarUrl;
            this.type = other.type;
            this.slugName = other.slugName;
            this.image = other.image;
            this.owner = other.owner;
            this.description = other.description;
            this.language = other.language;
            this.status = other.status;
        }

        public Builder withAppId(String appId) {
            this.appId = appId;
            return this;
        }

        public Builder withAppName(String appName) {
            this.appName = appName;
            return this;
        }

        public Builder withAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public Builder withType(AppType type) {
            this.type = type;
            return this;
        }

        public Builder withSlugName(String slugName) {
            this.slugName = slugName;
            return this;
        }

        public Builder withImage(String image) {
            this.image = image;
            return this;
        }

        public Builder withOwner(String owner) {
            this.owner = owner;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withLanguage(SupportLanguage language) {
            this.language = language;
            return this;
        }

        public Builder withStatus(AppStatus status) {
            this.status = status;
            return this;
        }
        
        public AppInfo build() {
            return new AppInfo(appId, appName, avatarUrl, type, slugName, image, owner, description, language, status);
        }
    }
}
