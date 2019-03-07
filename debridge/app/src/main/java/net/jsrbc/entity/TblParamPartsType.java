package net.jsrbc.entity;

import net.jsrbc.frame.annotation.database.object.Column;
import net.jsrbc.frame.annotation.database.object.Table;
import net.jsrbc.frame.annotation.database.restraint.PrimaryKey;

/**
 * Created by ZZZ on 2017-12-14.
 */
@Table("TBL_PARAM_PARTS_TYPE")
public final class TblParamPartsType {

    @Column("ID")
    @PrimaryKey
    private String id;

    @Column("NAME")
    private String name;

    @Column("POSITION_TYPE_ID")
    private String positionTypeId;

    @Column("NOTES")
    private String notes;

    @Column("MAIN_PARTS")
    private int mainParts;

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

    public String getPositionTypeId() {
        return positionTypeId;
    }

    public void setPositionTypeId(String positionTypeId) {
        this.positionTypeId = positionTypeId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getMainParts() {
        return mainParts;
    }

    public void setMainParts(int mainParts) {
        this.mainParts = mainParts;
    }

    public int getSortNo() {
        return sortNo;
    }

    public void setSortNo(int sortNo) {
        this.sortNo = sortNo;
    }
}
