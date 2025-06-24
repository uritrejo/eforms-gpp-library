package it.polimi.gpplib.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * Represents a suggested GPP patch, based on GppPatch but with additional
 * fields.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuggestedGppPatch {
    private String name;
    private List<String> btIds;
    private String dependsOn;
    private String path;
    private String value;
    private String op;
    private String description;
    private String lotId;

    public SuggestedGppPatch() {
    }

    public SuggestedGppPatch(String name, List<String> btIds, String dependsOn, String path, String value,
            String op, String description, String lotId) {
        this.name = name;
        this.btIds = btIds;
        this.dependsOn = dependsOn;
        this.path = path;
        this.value = value;
        this.op = op;
        this.description = description;
        this.lotId = lotId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getBtIds() {
        return btIds;
    }

    public void setBtIds(List<String> btIds) {
        this.btIds = btIds;
    }

    public String getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(String dependsOn) {
        this.dependsOn = dependsOn;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String pathInLot) {
        this.path = pathInLot;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLotId() {
        return lotId;
    }

    public void setLotId(String lotId) {
        this.lotId = lotId;
    }

    @Override
    public String toString() {
        return "SuggestedGppPatch{" +
                "name='" + name + '\'' +
                ", btIds=" + btIds +
                ", dependsOn='" + dependsOn + '\'' +
                ", pathInLot='" + path + '\'' +
                ", value='" + value + '\'' +
                ", op='" + op + '\'' +
                ", description='" + description + '\'' +
                ", lotId='" + lotId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SuggestedGppPatch that = (SuggestedGppPatch) o;

        // we only care about the name, path, value, op and lotId

        if (name != null ? !name.equals(that.name) : that.name != null)
            return false;
        if (btIds != null ? !btIds.equals(that.btIds) : that.btIds != null)
            return false;
        if (dependsOn != null ? !dependsOn.equals(that.dependsOn) : that.dependsOn != null)
            return false;
        if (path != null ? !path.equals(that.path) : that.path != null)
            return false;
        if (value != null ? !value.equals(that.value) : that.value != null)
            return false;
        if (op != null ? !op.equals(that.op) : that.op != null)
            return false;
        return lotId != null ? lotId.equals(that.lotId) : that.lotId == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (btIds != null ? btIds.hashCode() : 0);
        result = 31 * result + (dependsOn != null ? dependsOn.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (op != null ? op.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (lotId != null ? lotId.hashCode() : 0);
        return result;
    }
}
