package net.jsrbc.entity;

import android.util.Log;

import net.jsrbc.frame.annotation.database.object.Column;
import net.jsrbc.frame.annotation.database.object.Table;
import net.jsrbc.frame.annotation.database.restraint.Default;
import net.jsrbc.frame.annotation.database.restraint.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by ZZZ on 2017-12-22.
 */
@Table("TBL_BRIDGE_DISEASE")
public final class TblBridgeDisease implements Serializable, Cloneable {

    @Column("ID")
    @PrimaryKey
    private String id;

    @Column("TASK_ID")
    private String taskId;

    @Column("BRIDGE_ID")
    private String bridgeId;

    @Column("SIDE_TYPE_ID")
    private String sideTypeId;

    @Column("SIDE_TYPE_NAME")
    private String sideTypeName;

    @Column("SITE_NO")
    private int siteNo;

    @Column("MAX_SITE_NO")
    private int maxSiteNo;

    @Column("JOINT_NO")
    private int jointNo;

    @Column("POSITION_TYPE_ID")
    private String positionTypeId;

    @Column("POSITION_TYPE_NAME")
    private String positionTypeName;

    @Column("PARTS_TYPE_ID")
    private String partsTypeId;

    @Column("PARTS_TYPE_NAME")
    private String partsTypeName;

    @Column("BRIDGE_PARTS_ID")
    private String bridgePartsId;

    @Column("BRIDGE_MEMBER_ID")
    private String bridgeMemberId;

    @Column("MEMBER_TYPE_ID")
    private String memberTypeId;

    @Column("MEMBER_TYPE_NAME")
    private String memberTypeName;

    @Column("MEMBER_HORIZONTAL_NO")
    private int memberHorizontalNo;

    @Column("MEMBER_HORIZONTAL_COUNT")
    private int memberHorizontalCount;

    @Column("MEMBER_VERTICAL_NO")
    private int memberVerticalNo;

    @Column("MEMBER_VERTICAL_COUNT")
    private int memberVerticalCount;

    @Column("MEMBER_STAKE_SIDE")
    private int memberStakeSide;

    @Column("MEMBER_SPECIAL_SECTION")
    private String memberSpecialSection;

    @Column("MEMBER_DESC")
    private String memberDesc;

    @Column("BRIDGE_MEMBER_NAME")
    private String bridgeMemberName;

    @Column("MATERIAL_TYPE_ID")
    private String materialTypeId;

    @Column("DISEASE_TYPE_ID")
    private String diseaseTypeId;

    @Column("DISEASE_TYPE_NAME")
    private String diseaseTypeName;

    @Column("H_POSITION_START")
    private double hPositionStart;

    @Column("H_POSITION_END")
    private double hPositionEnd;

    @Column("L_POSITION_START")
    private double lPositionStart;

    @Column("L_POSITION_END")
    private double lPositionEnd;

    @Column("V_POSITION_START")
    private double vPositionStart;

    @Column("V_POSITION_END")
    private double vPositionEnd;

    @Column("POSITION")
    private String position;

    @Column("COUNT")
    @Default("1")
    private int count;

    @Column("ANGLE")
    private double angle;

    @Column("MIN_LENGTH")
    private double minLength;

    @Column("MAX_LENGTH")
    private double maxLength;

    @Column("MIN_WIDTH")
    private double minWidth;

    @Column("MAX_WIDTH")
    private double maxWidth;

    @Column("MIN_DEPTH")
    private double minDepth;

    @Column("MAX_DEPTH")
    private double maxDepth;

    @Column("PERCENT_DEGREE")
    private double percentDegree;

    @Column("DESC_DEGREE")
    private String descDegree;

    @Column("BEHAVIOR_DESC")
    private String behaviorDesc;

    @Column("IS_SIGNIFICANT")
    private int isSignificant;

    @Column("TREND")
    private int trend;

    @Column("DEDUCTION_SCALE")
    private int deductionScale;

    @Column("DEDUCTION_POINT")
    private int deductionPoint;

    @Column("DISEASE_DESC_ID")
    private String diseaseDescId;

    @Column("DISEASE_DESC")
    private String diseaseDesc;

    @Column("BRIDGE_DISEASE_NAME")
    private String bridgeDiseaseName;

    @Column("PARENT_ID")
    private String parentId;

    @Column("NOTES")
    private String notes;

    @Column("TREATMENT_ID")
    private String treatmentId;

    @Column("RECORD_USER")
    private String recordUser;

    @Column("RECORD_USER_NAME")
    private String recordUserName;

    @Column("RECORD_DATE")
    private String recordDate;

    @Column("UPLOAD")
    private int upload;

    private List<TblBridgeDiseasePhoto> diseasePhotoList = new ArrayList<>();

    @Override
    public TblBridgeDisease clone() {
        TblBridgeDisease bridgeDisease = null;
        try {
            bridgeDisease = (TblBridgeDisease) super.clone();
        }catch (CloneNotSupportedException e) {
            Log.e(getClass().getName(),"clone bridgeDisease failed", e);
        }
        return bridgeDisease;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (getClass().isInstance(obj) && getId().equals(((TblBridgeDisease) obj).getId()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    /** 用于构件数据库存储对象的构建器 */
    public static class Builder {

        private String id;

        private String taskId;

        private String bridgeId;

        private String sideTypeId;

        private int siteNo;

        private String partsTypeId;

        private String memberTypeId;

        private int memberHorizontalNo;

        private int memberVerticalNo;

        private int memberStakeSide;

        private String memberSpecialSection;

        private String materialTypeId;

        private String diseaseTypeId;

        private double hPositionStart;

        private double hPositionEnd;

        private double lPositionStart;

        private double lPositionEnd;

        private double vPositionStart;

        private double vPositionEnd;

        private String position;

        private int count;

        private double angle;

        private double minLength;

        private double maxLength;

        private double minWidth;

        private double maxWidth;

        private double minDepth;

        private double maxDepth;

        private double percentDegree;

        private String descDegree;

        private String behaviorDesc;

        private int isSignificant;

        private int trend;

        private int deductionScale;

        private int deductionPoint;

        private String diseaseDescId;

        private String parentId;

        private String notes;

        private String treatmentId;

        private String recordUser;

        private String recordUserName;

        private String recordDate;

        private int upload;

        /** 病害唯一标识符 */
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        /** 任务单ID */
        public Builder taskId(String taskId) {
            this.taskId = taskId;
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
        public Builder memberTypeId(String memberTypeId) {
            this.memberTypeId = memberTypeId;
            return this;
        }

        /** 构件横桥向编号 */
        public Builder memberHorizontalNo(int memberHorizontalNo) {
            this.memberHorizontalNo = memberHorizontalNo;
            return this;
        }

        /** 构件纵桥向编号 */
        public Builder memberVerticalNo(int memberVerticalNo) {
            this.memberVerticalNo = memberVerticalNo;
            return this;
        }

        /** 构件大小桩号侧 */
        public Builder memberStakeSide(int memberStakeSide) {
            this.memberStakeSide = memberStakeSide;
            return this;
        }

        /** 特殊构件 */
        public Builder memberSpecialSection(String memberSpecialSection) {
            this.memberSpecialSection = memberSpecialSection;
            return this;
        }

        /** 构件材料类型 */
        public Builder materialTypeId(String materialTypeId) {
            this.materialTypeId = materialTypeId;
            return this;
        }

        /** 设置病害类型 */
        public Builder diseaseTypeId(String diseaseTypeId) {
            this.diseaseTypeId = diseaseTypeId;
            return this;
        }

        /** 横桥向起始位置 */
        public Builder hPositionStart(double hPositionStart) {
            this.hPositionStart = hPositionStart;
            return this;
        }

        /** 横桥向结束位置 */
        public Builder hPositionEnd(double hPositionEnd) {
            this.hPositionEnd = hPositionEnd;
            return this;
        }

        /** 纵桥向起始位置 */
        public Builder lPositionStart(double lPositionStart) {
            this.lPositionStart = lPositionStart;
            return this;
        }

        /** 纵桥向结束位置 */
        public Builder lPositionEnd(double lPositionEnd) {
            this.lPositionEnd = lPositionEnd;
            return this;
        }

        /** 竖桥向起始位置 */
        public Builder vPositionStart(double vPositionStart) {
            this.vPositionStart = vPositionStart;
            return this;
        }

        /** 竖桥向结束位置 */
        public Builder vPositionEnd(double vPositionEnd) {
            this.vPositionEnd = vPositionEnd;
            return this;
        }

        /** 空间位置 */
        public Builder position(String position) {
            this.position = position;
            return this;
        }

        /** 病害数量 */
        public Builder count(int count) {
            this.count = count;
            return this;
        }

        /** 病害角度 */
        public Builder angle(double angle) {
            this.angle = angle;
            return this;
        }

        /** 最小长度 */
        public Builder minLength(double minLength) {
            this.minLength = minLength;
            return this;
        }

        /** 最大长度 */
        public Builder maxLength(double maxLength) {
            this.maxLength = maxLength;
            return this;
        }

        /** 最小宽度 */
        public Builder minWidth(double minWidth) {
            this.minWidth = minWidth;
            return this;
        }

        /** 最大宽度 */
        public Builder maxWidth(double maxWidth) {
            this.maxWidth = maxWidth;
            return this;
        }

        /** 最小深度 */
        public Builder minDepth(double minDepth) {
            this.minDepth = minDepth;
            return this;
        }

        /** 最大深度 */
        public Builder maxDepth(double maxDepth) {
            this.maxDepth = maxDepth;
            return this;
        }

        /** 百分比程度 */
        public Builder percentDegree(double percentDegree) {
            this.percentDegree = percentDegree;
            return this;
        }

        /** 描述程度 */
        public Builder descDegree(String descDegree) {
            this.descDegree = descDegree;
            return this;
        }

        /** 性状描述 */
        public Builder behaviorDesc(String behaviorDesc) {
            this.behaviorDesc = behaviorDesc;
            return this;
        }

        /** 是否为重点关注病害，0-不是，1-是 */
        public Builder isSignificant(int isSignificant) {
            this.isSignificant = isSignificant;
            return this;
        }

        /** 病害发展趋势,0-未知，1-新增，2-稳定，3-发展，4-维修 */
        public Builder trend(int trend) {
            this.trend = trend;
            return this;
        }

        /** 扣分标度 */
        public Builder deductionScale(int deductionScale) {
            this.deductionScale = deductionScale;
            return this;
        }

        /** 扣分值 */
        public Builder deductionPoint(int deductionPoint) {
            this.deductionPoint = deductionPoint;
            return this;
        }

        /** 病害描述类型ID */
        public Builder diseaseDescId(String diseaseDescId) {
            this.diseaseDescId = diseaseDescId;
            return this;
        }

        /** 父病害ID */
        public Builder parentId(String parentId) {
            this.parentId = parentId;
            return this;
        }

        /** 备注 */
        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        /** 处治措施建议 */
        public Builder treatmentId(String treatmentId) {
            this.treatmentId = treatmentId;
            return this;
        }

        /** 记录人 */
        public Builder recordUser(String recordUser) {
            this.recordUser = recordUser;
            return this;
        }

        public Builder recordUserName(String recordUserName) {
            this.recordUserName = recordUserName;
            return this;
        }

        /** 记录日期 */
        public Builder recordDate(String recordDate) {
            this.recordDate = recordDate;
            return this;
        }

        /** 上传的状态 */
        public Builder upload(int upload) {
            this.upload = upload;
            return this;
        }

        /** 构建桥梁病害对象 */
        public TblBridgeDisease build() {
            return new TblBridgeDisease(this);
        }
    }

    /** 提供一个默认构造器，用于反射 */
    public TblBridgeDisease() {}

    /** 基于构建器的构造器 */
    private TblBridgeDisease(Builder builder) {
        this.id = builder.id;
        this.taskId = builder.taskId;
        this.bridgeId = builder.bridgeId;
        this.sideTypeId = builder.sideTypeId;
        this.siteNo = builder.siteNo;
        this.partsTypeId = builder.partsTypeId;
        this.memberTypeId = builder.memberTypeId;
        this.memberHorizontalNo = builder.memberHorizontalNo;
        this.memberVerticalNo = builder.memberVerticalNo;
        this.memberStakeSide = builder.memberStakeSide;
        this.memberSpecialSection = builder.memberSpecialSection;
        this.materialTypeId = builder.materialTypeId;
        this.diseaseTypeId = builder.diseaseTypeId;
        this.hPositionStart = builder.hPositionStart;
        this.hPositionEnd = builder.hPositionEnd;
        this.lPositionStart = builder.lPositionStart;
        this.lPositionEnd = builder.lPositionEnd;
        this.vPositionStart = builder.vPositionStart;
        this.vPositionEnd = builder.vPositionEnd;
        this.position = builder.position;
        this.count = builder.count;
        this.angle = builder.angle;
        this.minLength = builder.minLength;
        this.maxLength = builder.maxLength;
        this.minWidth = builder.minWidth;
        this.maxWidth = builder.maxWidth;
        this.minDepth = builder.minDepth;
        this.maxDepth = builder.maxDepth;
        this.percentDegree = builder.percentDegree;
        this.descDegree = builder.descDegree;
        this.behaviorDesc = builder.behaviorDesc;
        this.isSignificant = builder.isSignificant;
        this.trend = builder.trend;
        this.deductionScale = builder.deductionScale;
        this.deductionPoint = builder.deductionPoint;
        this.diseaseDescId = builder.diseaseDescId;
        this.parentId = builder.parentId;
        this.notes = builder.notes;
        this.treatmentId = builder.treatmentId;
        this.recordUser = builder.recordUser;
        this.recordUserName = builder.recordUserName;
        this.recordDate = builder.recordDate;
        this.upload = builder.upload;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
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

    public void setSideTypeName(String sideTypeName) {
        this.sideTypeName = sideTypeName;
    }

    public void setSiteNo(int siteNo) {
        this.siteNo = siteNo;
    }

    public void setMaxSiteNo(int maxSiteNo) {
        this.maxSiteNo = maxSiteNo;
    }

    public void setJointNo(int jointNo) {
        this.jointNo = jointNo;
    }

    public void setPositionTypeId(String positionTypeId) {
        this.positionTypeId = positionTypeId;
    }

    public void setPositionTypeName(String positionTypeName) {
        this.positionTypeName = positionTypeName;
    }

    public void setPartsTypeId(String partsTypeId) {
        this.partsTypeId = partsTypeId;
    }

    public void setPartsTypeName(String partsTypeName) {
        this.partsTypeName = partsTypeName;
    }

    public void setBridgePartsId(String bridgePartsId) {
        this.bridgePartsId = bridgePartsId;
    }

    public void setBridgeMemberId(String bridgeMemberId) {
        this.bridgeMemberId = bridgeMemberId;
    }

    public void setMemberTypeId(String memberTypeId) {
        this.memberTypeId = memberTypeId;
    }

    public void setMemberTypeName(String memberTypeName) {
        this.memberTypeName = memberTypeName;
    }

    public void setMemberHorizontalNo(int memberHorizontalNo) {
        this.memberHorizontalNo = memberHorizontalNo;
    }

    public void setMemberHorizontalCount(int memberHorizontalCount) {
        this.memberHorizontalCount = memberHorizontalCount;
    }

    public void setMemberVerticalNo(int memberVerticalNo) {
        this.memberVerticalNo = memberVerticalNo;
    }

    public void setMemberVerticalCount(int memberVerticalCount) {
        this.memberVerticalCount = memberVerticalCount;
    }

    public void setMemberStakeSide(int memberStakeSide) {
        this.memberStakeSide = memberStakeSide;
    }

    public void setMemberSpecialSection(String memberSpecialSection) {
        this.memberSpecialSection = memberSpecialSection;
    }

    public void setMemberDesc(String memberDesc) {
        this.memberDesc = memberDesc;
    }

    public void setBridgeMemberName(String bridgeMemberName) {
        this.bridgeMemberName = bridgeMemberName;
    }

    public void setMaterialTypeId(String materialTypeId) {
        this.materialTypeId = materialTypeId;
    }

    public void setDiseaseTypeId(String diseaseTypeId) {
        this.diseaseTypeId = diseaseTypeId;
    }

    public void setDiseaseTypeName(String diseaseTypeName) {
        this.diseaseTypeName = diseaseTypeName;
    }

    public void sethPositionStart(double hPositionStart) {
        this.hPositionStart = hPositionStart;
    }

    public void sethPositionEnd(double hPositionEnd) {
        this.hPositionEnd = hPositionEnd;
    }

    public void setlPositionStart(double lPositionStart) {
        this.lPositionStart = lPositionStart;
    }

    public void setlPositionEnd(double lPositionEnd) {
        this.lPositionEnd = lPositionEnd;
    }

    public void setvPositionStart(double vPositionStart) {
        this.vPositionStart = vPositionStart;
    }

    public void setvPositionEnd(double vPositionEnd) {
        this.vPositionEnd = vPositionEnd;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void setMinLength(double minLength) {
        this.minLength = minLength;
    }

    public void setMaxLength(double maxLength) {
        this.maxLength = maxLength;
    }

    public void setMinWidth(double minWidth) {
        this.minWidth = minWidth;
    }

    public void setMaxWidth(double maxWidth) {
        this.maxWidth = maxWidth;
    }

    public void setMinDepth(double minDepth) {
        this.minDepth = minDepth;
    }

    public void setMaxDepth(double maxDepth) {
        this.maxDepth = maxDepth;
    }

    public void setPercentDegree(double percentDegree) {
        this.percentDegree = percentDegree;
    }

    public void setDescDegree(String descDegree) {
        this.descDegree = descDegree;
    }

    public void setBehaviorDesc(String behaviorDesc) {
        this.behaviorDesc = behaviorDesc;
    }

    public void setIsSignificant(int isSignificant) {
        this.isSignificant = isSignificant;
    }

    public void setTrend(int trend) {
        this.trend = trend;
    }

    public void setDeductionScale(int deductionScale) {
        this.deductionScale = deductionScale;
    }

    public void setDeductionPoint(int deductionPoint) {
        this.deductionPoint = deductionPoint;
    }

    public void setDiseaseDescId(String diseaseDescId) {
        this.diseaseDescId = diseaseDescId;
    }

    public void setDiseaseDesc(String diseaseDesc) {
        this.diseaseDesc = diseaseDesc;
    }

    public void setBridgeDiseaseName(String bridgeDiseaseName) {
        this.bridgeDiseaseName = bridgeDiseaseName;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setTreatmentId(String treatmentId) {
        this.treatmentId = treatmentId;
    }

    public void setRecordUser(String recordUser) {
        this.recordUser = recordUser;
    }

    public void setRecordUserName(String recordUserName) {
        this.recordUserName = recordUserName;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }

    public String getId() {
        return id;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getSideTypeName() {
        return sideTypeName;
    }

    public int getSiteNo() {
        return siteNo;
    }

    public int getMaxSiteNo() {
        return maxSiteNo;
    }

    public int getJointNo() {
        return jointNo;
    }

    public String getPositionTypeId() {
        return positionTypeId;
    }

    public String getPositionTypeName() {
        return positionTypeName;
    }

    public String getPartsTypeId() {
        return partsTypeId;
    }

    public String getPartsTypeName() {
        return partsTypeName;
    }

    public String getBridgePartsId() {
        return bridgePartsId;
    }

    public String getBridgeMemberId() {
        return bridgeMemberId;
    }

    public String getMemberTypeId() {
        return memberTypeId;
    }

    public String getMemberTypeName() {
        return memberTypeName;
    }

    public int getMemberHorizontalNo() {
        return memberHorizontalNo;
    }

    public int getMemberHorizontalCount() {
        return memberHorizontalCount;
    }

    public int getMemberVerticalNo() {
        return memberVerticalNo;
    }

    public int getMemberVerticalCount() {
        return memberVerticalCount;
    }

    public int getMemberStakeSide() {
        return memberStakeSide;
    }

    public String getMemberSpecialSection() {
        return memberSpecialSection;
    }

    public String getMemberDesc() {
        return memberDesc;
    }

    public String getBridgeMemberName() {
        return bridgeMemberName;
    }

    public String getMaterialTypeId() {
        return materialTypeId;
    }

    public String getDiseaseTypeId() {
        return diseaseTypeId;
    }

    public String getDiseaseTypeName() {
        return diseaseTypeName;
    }

    public double gethPositionStart() {
        return hPositionStart;
    }

    public double gethPositionEnd() {
        return hPositionEnd;
    }

    public double getlPositionStart() {
        return lPositionStart;
    }

    public double getlPositionEnd() {
        return lPositionEnd;
    }

    public double getvPositionStart() {
        return vPositionStart;
    }

    public double getvPositionEnd() {
        return vPositionEnd;
    }

    public String getPosition() {
        return position;
    }

    public int getCount() {
        return count;
    }

    public double getAngle() {
        return angle;
    }

    public double getMinLength() {
        return minLength;
    }

    public double getMaxLength() {
        return maxLength;
    }

    public double getMinWidth() {
        return minWidth;
    }

    public double getMaxWidth() {
        return maxWidth;
    }

    public double getMinDepth() {
        return minDepth;
    }

    public double getMaxDepth() {
        return maxDepth;
    }

    public double getPercentDegree() {
        return percentDegree;
    }

    public String getDescDegree() {
        return descDegree;
    }

    public String getBehaviorDesc() {
        return behaviorDesc;
    }

    public int getIsSignificant() {
        return isSignificant;
    }

    public int getTrend() {
        return trend;
    }

    public int getDeductionScale() {
        return deductionScale;
    }

    public int getDeductionPoint() {
        return deductionPoint;
    }

    public String getDiseaseDescId() {
        return diseaseDescId;
    }

    public String getDiseaseDesc() {
        return diseaseDesc;
    }

    public String getBridgeDiseaseName() {
        return bridgeDiseaseName;
    }

    public String getParentId() {
        return parentId;
    }

    public String getNotes() {
        return notes;
    }

    public String getTreatmentId() {
        return treatmentId;
    }

    public String getRecordUser() {
        return recordUser;
    }

    public String getRecordUserName() {
        return recordUserName;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setDiseasePhotoList(List<TblBridgeDiseasePhoto> diseasePhotoList) {
        this.diseasePhotoList = diseasePhotoList;
    }

    public List<TblBridgeDiseasePhoto> getDiseasePhotoList() {
        return diseasePhotoList;
    }

    public int getUpload() {
        return upload;
    }

    public void setUpload(int upload) {
        this.upload = upload;
    }
}
