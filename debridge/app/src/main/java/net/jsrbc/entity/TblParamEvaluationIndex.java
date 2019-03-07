package net.jsrbc.entity;

import net.jsrbc.frame.annotation.database.object.Column;
import net.jsrbc.frame.annotation.database.object.Table;
import net.jsrbc.frame.annotation.database.restraint.PrimaryKey;

/**
 * Created by ZZZ on 2017-12-28.
 */
@Table("TBL_PARAM_EVALUATION_INDEX")
public final class TblParamEvaluationIndex {

    @Column("ID")
    @PrimaryKey
    private String id;

    @Column("NAME")
    private String name;

    @Column("MAX_SCALE")
    private int maxScale;

    @Column("POSITION_TYPE_ID")
    private String positionTypeId;

    @Column("NOTES")
    private String notes;

    @Column("SORT_NO")
    private int sortNo;

    private final static TblParamEvaluationIndex EMPTY = new TblParamEvaluationIndex();

    public static TblParamEvaluationIndex empty() {
        EMPTY.setMaxScale(0);
        return EMPTY;
    }

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

    public int getMaxScale() {
        return maxScale;
    }

    public void setMaxScale(int maxScale) {
        this.maxScale = maxScale;
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

    public int getSortNo() {
        return sortNo;
    }

    public void setSortNo(int sortNo) {
        this.sortNo = sortNo;
    }
}
