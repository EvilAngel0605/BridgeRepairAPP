package net.jsrbc.entity;

import net.jsrbc.frame.annotation.database.object.Column;
import net.jsrbc.frame.annotation.database.object.Table;
import net.jsrbc.frame.annotation.database.restraint.PrimaryKey;

/**
 * Created by ZZZ on 2017-12-14.
 */
@Table("TBL_R_ABUTMENT_MEMBER")
public final class TblRAbutmentMember {

    @Column("ID")
    @PrimaryKey
    private String id;

    @Column("ABUTMENT_TYPE_ID")
    private String abutmentTypeId;

    @Column("MEMBER_TYPE_ID")
    private String memberTypeId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAbutmentTypeId() {
        return abutmentTypeId;
    }

    public void setAbutmentTypeId(String abutmentTypeId) {
        this.abutmentTypeId = abutmentTypeId;
    }

    public String getMemberTypeId() {
        return memberTypeId;
    }

    public void setMemberTypeId(String memberTypeId) {
        this.memberTypeId = memberTypeId;
    }
}
