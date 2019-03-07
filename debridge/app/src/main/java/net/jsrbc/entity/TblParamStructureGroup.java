package net.jsrbc.entity;

import net.jsrbc.frame.annotation.database.object.Column;
import net.jsrbc.frame.annotation.database.object.Table;
import net.jsrbc.frame.annotation.database.restraint.PrimaryKey;

/**
 * Created by ZZZ on 2017-12-13.
 */
@Table("TBL_PARAM_STRUCTURE_GROUP")
public final class TblParamStructureGroup {

    @Column("ID")
    @PrimaryKey
    private String id;

    @Column("NAME")
    private String name;

    @Column("FIELD")
    private String field;

    @Column("PARENT_ID")
    private String parentId;

    @Column("NOTES")
    private String notes;

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

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
