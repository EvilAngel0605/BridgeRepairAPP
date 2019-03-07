package net.jsrbc.entity;

import net.jsrbc.frame.annotation.database.object.Column;
import net.jsrbc.frame.annotation.database.object.Table;
import net.jsrbc.frame.annotation.database.restraint.PrimaryKey;
import net.jsrbc.utils.StringUtils;

/**
 * Created by ZZZ on 2017-12-12.
 */
@Table("TBL_PARAM_TYPE")
public final class TblParamType {

    @Column("ID")
    @PrimaryKey
    private String id;

    @Column("NAME")
    private String name;

    @Column("GROUP_ID")
    private String groupId;

    @Column("NOTES")
    private String notes;

    @Column("SORT_NO")
    private int sortNo;

    private final static TblParamType EMPTY = new TblParamType();

    /** 返回一个空的对象 */
    public static TblParamType empty() {
        EMPTY.setName("");
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

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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

    @Override
    public String toString() {
        return StringUtils.isEmpty(this.name)?"":this.name;
    }
}
