package net.jsrbc.entity;

import net.jsrbc.frame.annotation.database.object.Column;
import net.jsrbc.frame.annotation.database.restraint.PrimaryKey;
import net.jsrbc.frame.annotation.database.object.Table;

/**
 * Created by ZZZ on 2017-06-12.
 */
@Table("TBL_PARAM_DISEASE_DESC")
public final class TblParamDiseaseDesc {

    @Column("ID")
    @PrimaryKey
    private String id;

    @Column("DISEASE_DESC_TYPE_ID")
    private String diseaseDescTypeId;

    @Column("DISEASE_DESC")
    private String diseaseDesc;

    @Column("SORT_NO")
    private int sortNo;

    private final static TblParamDiseaseDesc EMPTY = new TblParamDiseaseDesc();

    public static TblParamDiseaseDesc empty() {
        EMPTY.setId("");
        EMPTY.setDiseaseDesc("");
        return EMPTY;
    }

    @Override
    public String toString() {
        return diseaseDesc.replaceAll("\\{.*?\\}", "_").replaceAll("[\\[\\]]", "");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDiseaseDescTypeId() {
        return diseaseDescTypeId;
    }

    public void setDiseaseDescTypeId(String diseaseDescTypeId) {
        this.diseaseDescTypeId = diseaseDescTypeId;
    }

    public String getDiseaseDesc() {
        return diseaseDesc;
    }

    public void setDiseaseDesc(String diseaseDesc) {
        this.diseaseDesc = diseaseDesc;
    }

    public int getSortNo() {
        return sortNo;
    }

    public void setSortNo(int sortNo) {
        this.sortNo = sortNo;
    }
}
