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

    public SuggestedGppPatch() {
    }

    public SuggestedGppPatch(String name, List<String> btIds, String dependsOn, String path, String value,
            String op, String description) {
        this.name = name;
        this.btIds = btIds;
        this.dependsOn = dependsOn;
        this.path = path;
        this.value = value;
        this.op = op;
        this.description = description;
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
                '}';
    }
}
