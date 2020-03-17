package externalapi.appcall.models;

import externalapi.appinfo.models.ParamType;
import java.util.Objects;

public class KeyValueParam extends CallParam {
    private String value;

    public KeyValueParam(String name, String value) {
        super(name);
        this.value = value;
    }

    @Override
    public ParamType getType() {
        return ParamType.KEY_VALUE;
    }
    
    
    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.value);
        return hash;
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
        final KeyValueParam other = (KeyValueParam) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        return true;
    }
}
