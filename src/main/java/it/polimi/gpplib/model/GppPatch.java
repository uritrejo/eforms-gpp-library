package it.polimi.gpplib.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * Represents a patch/template for GPP XML insertion, as defined in
 * gpp_patches_data.json.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GppPatch {
    private String name;
    private List<String> btIds;
    private String dependsOn;
    private String pathInLot;
    private String value;

    public GppPatch() {
    }

    public GppPatch(String name, List<String> btIds, String dependsOn, String pathInLot, String value) {
        this.name = name;
        this.btIds = btIds;
        this.dependsOn = dependsOn;
        this.pathInLot = pathInLot;
        this.value = value;
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

    public String getPathInLot() {
        return pathInLot;
    }

    public void setPathInLot(String pathInLot) {
        this.pathInLot = pathInLot;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "GppPatch{" +
                "name='" + name + '\'' +
                ", btIds=" + btIds +
                ", dependsOn='" + dependsOn + '\'' +
                ", pathInLot='" + pathInLot + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}