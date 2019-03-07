package net.jsrbc.entity;

import net.jsrbc.frame.annotation.database.object.Column;
import net.jsrbc.frame.annotation.database.object.Table;
import net.jsrbc.frame.annotation.database.restraint.PrimaryKey;
import net.jsrbc.utils.StringUtils;

/**
 * Created by ZZZ on 2017-12-28.
 */
@Table("TBL_PARAM_DISEASE_TYPE")
public final class TblParamDiseaseType {

    @Column("ID")
    @PrimaryKey
    private String id;

    @Column("NAME")
    private String name;

    @Column("PARTS_TYPE_ID")
    private String partsTypeId;

    @Column("EVALUATION_INDEX_ID")
    private String evaluationIndexId;

    @Column("NOTES")
    private String notes;

    @Column("MEMBER_TYPE_ID")
    private String memberTypeId;

    @Column("DISEASE_DESC_TYPE_ID")
    private String diseaseDescTypeId;

    @Column("EVALUATION_INDEX_NAME")
    private String evaluationIndexName;

    @Column("MAX_SCALE")
    private int maxScale;

    private final static TblParamDiseaseType EMPTY = new TblParamDiseaseType();

    /** 返回一个空对象 */
    public static TblParamDiseaseType empty() {
        EMPTY.setId("");
        EMPTY.setName("");
        return EMPTY;
    }

    @Override
    public String toString() {
        if (!StringUtils.isEmpty(this.notes)) return String.format("%s(%s)", this.name, this.notes);
        else return this.name;
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

    public String getPartsTypeId() {
        return partsTypeId;
    }

    public void setPartsTypeId(String partsTypeId) {
        this.partsTypeId = partsTypeId;
    }

    public String getEvaluationIndexId() {
        return evaluationIndexId;
    }

    public void setEvaluationIndexId(String evaluationIndexId) {
        this.evaluationIndexId = evaluationIndexId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getMemberTypeId() {
        return memberTypeId;
    }

    public void setMemberTypeId(String memberTypeId) {
        this.memberTypeId = memberTypeId;
    }

    public String getDiseaseDescTypeId() {
        return diseaseDescTypeId;
    }

    public void setDiseaseDescTypeId(String diseaseDescTypeId) {
        this.diseaseDescTypeId = diseaseDescTypeId;
    }

    public String getEvaluationIndexName() {
        return evaluationIndexName;
    }

    public void setEvaluationIndexName(String evaluationIndexName) {
        this.evaluationIndexName = evaluationIndexName;
    }

    public int getMaxScale() {
        return maxScale;
    }

    public void setMaxScale(int maxScale) {
        this.maxScale = maxScale;
    }
}
