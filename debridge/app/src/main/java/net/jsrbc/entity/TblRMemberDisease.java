package net.jsrbc.entity;

import net.jsrbc.frame.annotation.database.object.Column;
import net.jsrbc.frame.annotation.database.object.Table;
import net.jsrbc.frame.annotation.database.restraint.PrimaryKey;

/**
 * Created by ZZZ on 2017-12-28.
 */
@Table("TBL_R_MEMBER_DISEASE")
public final class TblRMemberDisease {

    @Column("ID")
    @PrimaryKey
    private String id;

    @Column("MATERIAL_TYPE_ID")
    private String materialTypeId;

    @Column("MEMBER_TYPE_ID")
    private String memberTypeId;

    @Column("DISEASE_TYPE_ID")
    private String diseaseTypeId;

    @Column("DISEASE_DESC_TYPE_ID")
    private String diseaseDescTypeId;

    @Column("SORT_NO")
    private long sortNo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMaterialTypeId() {
        return materialTypeId;
    }

    public void setMaterialTypeId(String materialTypeId) {
        this.materialTypeId = materialTypeId;
    }

    public String getMemberTypeId() {
        return memberTypeId;
    }

    public void setMemberTypeId(String memberTypeId) {
        this.memberTypeId = memberTypeId;
    }

    public String getDiseaseTypeId() {
        return diseaseTypeId;
    }

    public void setDiseaseTypeId(String diseaseTypeId) {
        this.diseaseTypeId = diseaseTypeId;
    }

    public String getDiseaseDescTypeId() {
        return diseaseDescTypeId;
    }

    public void setDiseaseDescTypeId(String diseaseDescTypeId) {
        this.diseaseDescTypeId = diseaseDescTypeId;
    }

    public long getSortNo() {
        return sortNo;
    }

    public void setSortNo(long sortNo) {
        this.sortNo = sortNo;
    }
}
