package net.jsrbc.entity;

import net.jsrbc.frame.annotation.database.object.Column;
import net.jsrbc.frame.annotation.database.object.Table;
import net.jsrbc.frame.annotation.database.restraint.PrimaryKey;

import java.io.Serializable;

/**
 * Created by ZZZ on 2017-12-04.
 */
@Table("TBL_BRIDGE_PARTS")
public final class TblBridgeParts implements Serializable {

    @Column("ID")
    @PrimaryKey
    private String id;

    @Column("BRIDGE_ID")
    private String bridgeId;

    @Column("SIDE_TYPE_ID")
    private String sideTypeId;

    @Column("SITE_NO")
    private int siteNo;

    @Column("STRUCTURE_TYPE_ID")
    private String structureTypeId;

    @Column("STRUCTURE_TYPE_NAME")
    private String structureTypeName;

    @Column("MATERIAL_TYPE_ID")
    private String materialTypeId;

    @Column("MATERIAL_TYPE_NAME")
    private String materialTypeName;

    @Column("POSITION_TYPE_ID")
    private String positionTypeId;

    @Column("POSITION_TYPE_NAME")
    private String positionTypeName;

    @Column("PARTS_TYPE_ID")
    private String partsTypeId;

    @Column("PARTS_TYPE_NAME")
    private String partsTypeName;

    @Column("MEMBER_TYPE_ID")
    private String memberTypeId;

    @Column("MEMBER_TYPE_NAME")
    private String memberTypeName;

    @Column("HORIZONTAL_COUNT")
    private int horizontalCount;

    @Column("VERTICAL_COUNT")
    private int verticalCount;

    @Column("MEMBER_DESC_ID")
    private String memberDescId;

    @Column("MEMBER_DESC")
    private String memberDesc;

    @Column("STAKE_SIDE")
    private int stakeSide;

    @Column("SPECIAL_SECTION")
    private String specialSection;

    @Column("UPLOAD")
    private int upload;

    private final static TblBridgeParts EMPTY = new TblBridgeParts();

    /** 返回一个空对象 */
    public static TblBridgeParts empty() {
        EMPTY.setMemberTypeName("");
        EMPTY.setMemberTypeId("");
        return EMPTY;
    }

    @Override
    public String toString() {
        return this.memberTypeName;
    }

    public static class Builder {

        private String id;

        private String bridgeId;

        private String sideTypeId;

        private int siteNo;

        private String structureTypeId;

        private String materialTypeId;

        private String partsTypeId;

        private String memberTypeId;

        private int stakeSide;

        private String specialSection;

        private int horizontalCount;

        private int verticalCount;

        private int upload;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder bridgeId(String bridgeId) {
            this.bridgeId = bridgeId;
            return this;
        }

        public Builder sideTypeId(String sideTypeId) {
            this.sideTypeId = sideTypeId;
            return this;
        }

        public Builder siteNo(int siteNo) {
            this.siteNo = siteNo;
            return this;
        }

        public Builder structureTypeId(String structureTypeId) {
            this.structureTypeId = structureTypeId;
            return this;
        }

        public Builder materialTypeId(String materialTypeId) {
            this.materialTypeId = materialTypeId;
            return this;
        }

        public Builder partsTypeId(String partsTypeId) {
            this.partsTypeId = partsTypeId;
            return this;
        }

        public Builder memberTypeId(String memberTypeId) {
            this.memberTypeId = memberTypeId;
            return this;
        }

        public Builder stakeSide(int stakeSide) {
            this.stakeSide = stakeSide;
            return this;
        }

        public Builder specialSection(String specialSection) {
            this.specialSection = specialSection;
            return this;
        }

        public Builder horizontalCount(int horizontalCount) {
            this.horizontalCount = horizontalCount;
            return this;
        }

        public Builder verticalCount(int verticalCount) {
            this.verticalCount = verticalCount;
            return this;
        }

        public Builder upload(int upload) {
            this.upload = upload;
            return this;
        }

        public TblBridgeParts build() {
            return new TblBridgeParts(this);
        }
    }

    public TblBridgeParts() {}

    private TblBridgeParts(Builder builder) {
        this.id = builder.id;
        this.bridgeId = builder.bridgeId;
        this.sideTypeId = builder.sideTypeId;
        this.siteNo = builder.siteNo;
        this.structureTypeId = builder.structureTypeId;
        this.materialTypeId = builder.materialTypeId;
        this.partsTypeId = builder.partsTypeId;
        this.memberTypeId = builder.memberTypeId;
        this.stakeSide = builder.stakeSide;
        this.specialSection = builder.specialSection;
        this.horizontalCount = builder.horizontalCount;
        this.verticalCount = builder.verticalCount;
        this.upload = builder.upload;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBridgeId() {
        return bridgeId;
    }

    public void setBridgeId(String bridgeId) {
        this.bridgeId = bridgeId;
    }

    public String getSideTypeId() {
        return sideTypeId;
    }

    public void setSideTypeId(String sideTypeId) {
        this.sideTypeId = sideTypeId;
    }

    public int getSiteNo() {
        return siteNo;
    }

    public void setSiteNo(int siteNo) {
        this.siteNo = siteNo;
    }

    public String getStructureTypeId() {
        return structureTypeId;
    }

    public void setStructureTypeId(String structureTypeId) {
        this.structureTypeId = structureTypeId;
    }

    public String getStructureTypeName() {
        return structureTypeName;
    }

    public void setStructureTypeName(String structureTypeName) {
        this.structureTypeName = structureTypeName;
    }

    public String getMaterialTypeId() {
        return materialTypeId;
    }

    public void setMaterialTypeId(String materialTypeId) {
        this.materialTypeId = materialTypeId;
    }

    public String getMaterialTypeName() {
        return materialTypeName;
    }

    public void setMaterialTypeName(String materialTypeName) {
        this.materialTypeName = materialTypeName;
    }

    public String getPositionTypeId() {
        return positionTypeId;
    }

    public void setPositionTypeId(String positionTypeId) {
        this.positionTypeId = positionTypeId;
    }

    public String getPartsTypeId() {
        return partsTypeId;
    }

    public void setPartsTypeId(String partsTypeId) {
        this.partsTypeId = partsTypeId;
    }

    public String getPartsTypeName() {
        return partsTypeName;
    }

    public void setPartsTypeName(String partsTypeName) {
        this.partsTypeName = partsTypeName;
    }

    public String getMemberTypeId() {
        return memberTypeId;
    }

    public void setMemberTypeId(String memberTypeId) {
        this.memberTypeId = memberTypeId;
    }

    public String getMemberTypeName() {
        return memberTypeName;
    }

    public void setMemberTypeName(String memberTypeName) {
        this.memberTypeName = memberTypeName;
    }

    public int getHorizontalCount() {
        return horizontalCount;
    }

    public void setHorizontalCount(int horizontalCount) {
        this.horizontalCount = horizontalCount;
    }

    public int getVerticalCount() {
        return verticalCount;
    }

    public void setVerticalCount(int verticalCount) {
        this.verticalCount = verticalCount;
    }

    public String getMemberDescId() {
        return memberDescId;
    }

    public void setMemberDescId(String memberDescId) {
        this.memberDescId = memberDescId;
    }

    public String getMemberDesc() {
        return memberDesc;
    }

    public void setMemberDesc(String memberDesc) {
        this.memberDesc = memberDesc;
    }

    public int getStakeSide() {
        return stakeSide;
    }

    public void setStakeSide(int stakeSide) {
        this.stakeSide = stakeSide;
    }

    public String getSpecialSection() {
        return specialSection;
    }

    public void setSpecialSection(String specialSection) {
        this.specialSection = specialSection;
    }

    public String getPositionTypeName() {
        return positionTypeName;
    }

    public void setPositionTypeName(String positionTypeName) {
        this.positionTypeName = positionTypeName;
    }

    public int getUpload() {
        return upload;
    }

    public void setUpload(int upload) {
        this.upload = upload;
    }
}
