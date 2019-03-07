package net.jsrbc.entity;

import net.jsrbc.frame.annotation.database.object.Column;
import net.jsrbc.frame.annotation.database.object.Table;
import net.jsrbc.frame.annotation.database.restraint.PrimaryKey;

/**
 * Created by ZZZ on 2017-12-14.
 */
@Table("TBL_PARAM_POSITION_TYPE")
public final class TblParamPositionType {

    @Column("ID")
    @PrimaryKey
    private String id;

    @Column("NAME")
    private String name;

    @Column("WEIGHT")
    private float weight;

    @Column("NOTES")
    private String notes;

    @Column("SORT_NO")
    private int sortNo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getSortNo() {
        return sortNo;
    }

    public void setSortNo(int sortNo) {
        this.sortNo = sortNo;
    }
}
