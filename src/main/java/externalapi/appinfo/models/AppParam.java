package externalapi.appinfo.models;

import java.util.Objects;

public class AppParam {
    private final String name;
    private final ParamType type;
    private final String label;
    private final String description;

    public AppParam(String name, ParamType type, String label, String description) {
        this.name = name;
        this.type = type;
        this.label = label;
        this.description = description;
    }
    
    public String getName() {
        return name;
    }

    public ParamType getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
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
        final AppParam other = (AppParam) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.label, other.label)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AppParam{" + "name=" + name + ", type=" + type + ", label=" + label + ", description=" + description + '}';
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String name;
        private ParamType type;
        private String label;
        private String description;
        
        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setType(ParamType type) {
            this.type = type;
            return this;
        }

        public Builder setLabel(String label) {
            this.label = label;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }
        
        public AppParam build() {
            return new AppParam(name, type, label, description);
        }
    }
}
