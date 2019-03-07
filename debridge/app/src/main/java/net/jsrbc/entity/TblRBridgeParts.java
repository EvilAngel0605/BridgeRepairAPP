package net.jsrbc.entity;

import net.jsrbc.frame.annotation.database.object.Column;
import net.jsrbc.frame.annotation.database.object.Table;
import net.jsrbc.frame.annotation.database.restraint.PrimaryKey;

/**
 * Created by ZZZ on 2017-12-14.
 */
@Table("TBL_R_BRIDGE_PARTS")
public final class TblRBridgeParts {

    @Column("ID")
    @PrimaryKey
    private String id;

    @Column("BRIDGE_TYPE_ID")
    private String bridgeTypeId;

    @Column("PARTS_TYPE_ID")
    private String partsTypeId;

    @Column("WEIGHT")
    private float weight;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBridgeTypeId() {
        return bridgeTypeId;
    }

    public void setBridgeTypeId(String bridgeTypeId) {
        this.bridgeTypeId = bridgeTypeId;
    }

    public String getPartsTypeId() {
        return partsTypeId;
    }

    public void setPartsTypeId(String partsTypeId) {
        this.partsTypeId = partsTypeId;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}
