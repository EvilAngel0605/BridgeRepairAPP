package net.jsrbc.entity;

import net.jsrbc.frame.annotation.database.object.Column;
import net.jsrbc.frame.annotation.database.object.Table;
import net.jsrbc.frame.annotation.database.restraint.PrimaryKey;

/**
 * Created by ZZZ on 2017-12-14.
 */
@Table("TBL_R_SUPERSTRUCTURE_MEMBER")
public final class TblRSuperstructureMember {

    @Column("ID")
    @PrimaryKey
    private String id;

    @Column("SUPERSTRUCTURE_TYPE_ID")
    private String superstructureTypeId;

    @Column("MEMBER_TYPE_ID")
    private String memberTypeId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSuperstructureTypeId() {
        return superstructureTypeId;
    }

    public void setSuperstructureTypeId(String superstructureTypeId) {
        this.superstructureTypeId = superstructureTypeId;
    }

    public String getMemberTypeId() {
        return memberTypeId;
    }

    public void setMemberTypeId(String memberTypeId) {
        this.memberTypeId = memberTypeId;
    }
}
