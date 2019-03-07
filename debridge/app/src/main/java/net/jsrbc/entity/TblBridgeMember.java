package net.jsrbc.entity;

import android.support.annotation.NonNull;

import net.jsrbc.frame.annotation.database.object.Column;
import net.jsrbc.frame.annotation.database.object.Table;
import net.jsrbc.frame.annotation.database.restraint.PrimaryKey;

/**
 * Created by ZZZ on 2017-12-13.
 */
@Table("TBL_BRIDGE_MEMBER")
public class TblBridgeMember implements Comparable<TblBridgeMember> {

    @Column("ID")
    @PrimaryKey
    private String id;

    @Column("BRIDGE_ID")
    private String bridgeId;

    @Column("SIDE_TYPE_ID")
    private String sideTypeId;

    @Column("SITE_NO")
    private int siteNo;

    @Column("MAX_SITE_NO")
    private int maxSiteNo;

    @Column("JOINT_NO")
    private int jointNo;

    @Column("PARTS_TYPE_ID")
    private String partsTypeId;

    @Column("MEMBER_TYPE_ID")
    private String memberTypeId;

    @Column("MEMBER_TYPE_NAME")
    private String memberTypeName;

    @Column("MEMBER_DESC")
    private String memberDesc;

    @Column("BRIDGE_MEMBER_NAME")
    private String bridgeMemberName;

    @Column("STAKE_SIDE")
    private int stakeSide;

    @Column("SPECIAL_SECTION")
    private String specialSection;

    @Column("HORIZONTAL_NO")
    private int horizontalNo;

    @Column("HORIZONTAL_COUNT")
    private int horizontalCount;

    @Column("VERTICAL_NO")
    private int verticalNo;

    @Column("VERTICAL_COUNT")
    private int verticalCount;

    private final static TblBridgeMember EMPTY = new TblBridgeMember();

    public static TblBridgeMember empty() {
        EMPTY.setBridgeId("");
        EMPTY.setSideTypeId("");
        EMPTY.setMemberTypeId("");
        EMPTY.setBridgeMemberName("");
        EMPTY.setSpecialSection("");
        return EMPTY;
    }

    @Override
    public String toString() {
        return this.bridgeMemberName;
    }

    @Override
    public int compareTo(@NonNull TblBridgeMember bridgeMember) {
        if (!this.specialSection.equals(bridgeMember.getSpecialSection())) return this.specialSection.compareTo(bridgeMember.getSpecialSection());
        if (this.stakeSide > bridgeMember.getStakeSide()) return 1;
        else if (this.stakeSide < bridgeMember.getStakeSide()) return -1;
        if (this.verticalNo > bridgeMember.getVerticalNo()) return 1;
        else if (this.verticalNo < bridgeMember.getVerticalNo()) return -1;
        if (this.horizontalNo > bridgeMember.getHorizontalNo()) return 1;
        else if (this.horizontalNo < bridgeMember.getHorizontalNo()) return -1;
        else return 0;
    }

    public static class Builder {

        private String id;

        private String bridgeId;

        private String sideTypeId;

        private int siteNo;

        private String partsTypeId;

        private String memberTypeId;

        private int stakeSide;

        private String specialSection;

        private int horizontalNo;

        private int verticalNo;

        /** ID */
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

        /** 孔号 */
        public Builder siteNo(int siteNo) {
            this.siteNo = siteNo;
            return this;
        }

        /** 部件类型ID */
        public Builder partsTypeId(String partsTypeId) {
            this.partsTypeId = partsTypeId;
            return this;
        }

        /** 构件类型ID */
        public Builder memberTypeId(String memberTypeId){
            this.memberTypeId = memberTypeId;
            return this;
        }

        /** 大小桩号侧 */
        public Builder stakeSide(int stakeSide) {
            this.stakeSide = stakeSide;
            return this;
        }

        /** 特殊块段 */
        public Builder specialSection(String specialSection) {
            this.specialSection = specialSection;
            return this;
        }

        /** 横向编号 */
        public Builder horizontalNo(int horizontalNo) {
            this.horizontalNo = horizontalNo;
            return this;
        }

        /** 纵向编号 */
        public Builder verticalNo(int verticalNo) {
            this.verticalNo = verticalNo;
            return this;
        }

        /** 构建构件对象 */
        public TblBridgeMember build() {
            return new TblBridgeMember(this);
        }
    }

    /** 提供一个默认构造器，不然反射没法用了 */
    public TblBridgeMember() {}

    /** 带构建器的构造器，外部不可见 */
    private TblBridgeMember(Builder builder) {
        this.id = builder.id;
        this.bridgeId = builder.bridgeId;
        this.sideTypeId = builder.sideTypeId;
        this.siteNo = builder.siteNo;
        this.partsTypeId = builder.partsTypeId;
        this.memberTypeId = builder.memberTypeId;
        this.stakeSide = builder.stakeSide;
        this.specialSection = builder.specialSection;
        this.horizontalNo = builder.horizontalNo;
        this.verticalNo = builder.verticalNo;
    }

    /** 将病害对象转换为构件对象 */
    public static TblBridgeMember of(TblBridgeDisease bridgeDisease) {
        TblBridgeMember bridgeMember = new TblBridgeMember();
        bridgeMember.setBridgeId(bridgeDisease.getBridgeId());
        bridgeMember.setSideTypeId(bridgeDisease.getSideTypeId());
        bridgeMember.setSiteNo(bridgeDisease.getSiteNo());
        bridgeMember.setMaxSiteNo(bridgeDisease.getMaxSiteNo());
        bridgeMember.setJointNo(bridgeDisease.getJointNo());
        bridgeMember.setPartsTypeId(bridgeDisease.getPartsTypeId());
        bridgeMember.setMemberTypeId(bridgeDisease.getMemberTypeId());
        bridgeMember.setMemberTypeName(bridgeDisease.getMemberTypeName());
        bridgeMember.setMemberDesc(bridgeDisease.getMemberDesc());
        bridgeMember.setStakeSide(bridgeDisease.getMemberStakeSide());
        bridgeMember.setSpecialSection(bridgeDisease.getMemberSpecialSection());
        bridgeMember.setHorizontalNo(bridgeDisease.getMemberHorizontalNo());
        bridgeMember.setHorizontalCount(bridgeDisease.getMemberHorizontalCount());
        bridgeMember.setVerticalNo(bridgeDisease.getMemberVerticalNo());
        bridgeMember.setVerticalCount(bridgeDisease.getMemberVerticalCount());
        return bridgeMember;
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

    public int getMaxSiteNo() {
        return maxSiteNo;
    }

    public void setMaxSiteNo(int maxSiteNo) {
        this.maxSiteNo = maxSiteNo;
    }

    public int getJointNo() {
        return jointNo;
    }

    public void setJointNo(int jointNo) {
        this.jointNo = jointNo;
    }

    public String getPartsTypeId() {
        return partsTypeId;
    }

    public void setPartsTypeId(String partsTypeId) {
        this.partsTypeId = partsTypeId;
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

    public String getMemberDesc() {
        return memberDesc;
    }

    public void setMemberDesc(String memberDesc) {
        this.memberDesc = memberDesc;
    }

    public String getBridgeMemberName() {
        return bridgeMemberName;
    }

    public void setBridgeMemberName(String bridgeMemberName) {
        this.bridgeMemberName = bridgeMemberName;
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

    public int getHorizontalNo() {
        return horizontalNo;
    }

    public void setHorizontalNo(int horizontalNo) {
        this.horizontalNo = horizontalNo;
    }

    public int getHorizontalCount() {
        return horizontalCount;
    }

    public void setHorizontalCount(int horizontalCount) {
        this.horizontalCount = horizontalCount;
    }

    public int getVerticalNo() {
        return verticalNo;
    }

    public void setVerticalNo(int verticalNo) {
        this.verticalNo = verticalNo;
    }

    public int getVerticalCount() {
        return verticalCount;
    }

    public void setVerticalCount(int verticalCount) {
        this.verticalCount = verticalCount;
    }
}
