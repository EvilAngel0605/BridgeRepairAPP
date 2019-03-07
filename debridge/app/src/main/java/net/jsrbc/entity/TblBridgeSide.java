package net.jsrbc.entity;

import android.util.Log;

import net.jsrbc.frame.annotation.database.object.Column;
import net.jsrbc.frame.annotation.database.object.Table;
import net.jsrbc.frame.annotation.database.restraint.PrimaryKey;

import java.io.Serializable;

/**
 * Created by ZZZ on 2017-12-04.
 */
@Table("TBL_BRIDGE_SIDE")
public final class TblBridgeSide implements Serializable, Cloneable {

    @Column("ID")
    @PrimaryKey
    private String id;

    @Column("BRIDGE_ID")
    private String bridgeId;

    @Column("BRIDGE_NAME")
    private String bridgeName;

    @Column("SIDE_TYPE_ID")
    private String sideTypeId;

    @Column("SIDE_TYPE_NAME")
    private String sideTypeName;

    @Column("SIDE_TYPE_SORT_NO")
    private int sideTypeSortNo;

    @Column("STAKE_NO")
    private float stakeNo;

    @Column("BRIDGE_LENGTH")
    private float bridgeLength;

    @Column("BRIDGE_SPAN_COMBINATION")
    private String bridgeSpanCombination;

    @Column("DECK_TOTAL_WIDTH")
    private float deckTotalWidth;

    @Column("LANE_WIDTH")
    private float laneWidth;

    @Column("DECK_EVALUATION")
    private float deckEvaluation;

    @Column("SUB_CLEAR_HEIGHT")
    private float subClearHeight;

    @Column("SUPER_CLEAR_HEIGHT")
    private float superClearHeight;

    @Column("ACCESS_TOTAL_WIDTH")
    private float accessTotalWidth;

    @Column("ACCESS_ROAD_WIDTH")
    private float accessRoadWidth;

    @Column("ACCESS_LINEAR_ID")
    private String accessLinearId;

    @Column("ACCESS_LINEAR_NAME")
    private String accessLinearName;

    /** 任务单ID */
    @Column("TASK_ID")
    private String taskId;

    /** 是否需要上传，0-不需要，1-需要上传 */
    @Column("UPLOAD")
    private int upload;

    @Override
    public TblBridgeSide clone() {
        TblBridgeSide bridgeSide = null;
        try {
            bridgeSide = (TblBridgeSide) super.clone();
        } catch (CloneNotSupportedException e) {
            Log.e(getClass().getName(),"clone bridgeSide failed", e);
        }
        return bridgeSide;
    }

    public static class Builder {

        private String id;

        private String bridgeId;

        private String bridgeName;

        private String sideTypeId;

        private String sideTypeName;

        private int sideTypeSortNo;

        private float stakeNo;

        private float bridgeLength;

        private String bridgeSpanCombination;

        private float deckTotalWidth;

        private float laneWidth;

        private float deckEvaluation;

        private float subClearHeight;

        private float superClearHeight;

        private float accessTotalWidth;

        private float accessRoadWidth;

        private String accessLinearId;

        private String accessLinearName;

        private String taskId;

        private int upload;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder bridgeId(String bridgeId) {
            this.bridgeId = bridgeId;
            return this;
        }

        public Builder bridgeName(String bridgeName) {
            this.bridgeName = bridgeName;
            return this;
        }

        public Builder sideTypeId(String sideTypeId) {
            this.sideTypeId = sideTypeId;
            return this;
        }

        public Builder sideTypeName(String sideTypeName) {
            this.sideTypeName = sideTypeName;
            return this;
        }

        public Builder sideTypeSortNo(int sideTypeSortNo) {
            this.sideTypeSortNo = sideTypeSortNo;
            return this;
        }

        public Builder stakeNo(float stakeNo) {
            this.stakeNo = stakeNo;
            return this;
        }

        public Builder bridgeLength(float bridgeLength) {
            this.bridgeLength = bridgeLength;
            return this;
        }

        public Builder bridgeSpanCombination(String bridgeSpanCombination) {
            this.bridgeSpanCombination = bridgeSpanCombination;
            return this;
        }

        public Builder deckTotalWidth(float deckTotalWidth) {
            this.deckTotalWidth = deckTotalWidth;
            return this;
        }

        public Builder laneWidth(float laneWidth) {
            this.laneWidth = laneWidth;
            return this;
        }

        public Builder deckEvaluation(float deckEvaluation) {
            this.deckEvaluation = deckEvaluation;
            return this;
        }

        public Builder subClearHeight(float subClearHeight) {
            this.subClearHeight = subClearHeight;
            return this;
        }

        public Builder superClearHeight(float superClearHeight) {
            this.superClearHeight = superClearHeight;
            return this;
        }

        public Builder accessTotalWidth(float accessTotalWidth) {
            this.accessTotalWidth = accessTotalWidth;
            return this;
        }

        public Builder accessRoadWidth(float accessRoadWidth) {
            this.accessRoadWidth = accessRoadWidth;
            return this;
        }

        public Builder accessLinearId(String accessLinearId) {
            this.accessLinearId = accessLinearId;
            return this;
        }

        public Builder accessLinearName(String accessLinearName) {
            this.accessLinearName = accessLinearName;
            return this;
        }

        public Builder taskId(String taskId) {
            this.taskId = taskId;
            return this;
        }

        public Builder upload(int upload) {
            this.upload = upload;
            return this;
        }

        public TblBridgeSide build() {
            return new TblBridgeSide(this);
        }
    }

    public TblBridgeSide() {}

    private TblBridgeSide(Builder builder) {
        this.id = builder.id;
        this.bridgeId = builder.bridgeId;
        this.bridgeName = builder.bridgeName;
        this.sideTypeId = builder.sideTypeId;
        this.sideTypeName = builder.sideTypeName;
        this.sideTypeSortNo = builder.sideTypeSortNo;
        this.stakeNo = builder.stakeNo;
        this.bridgeLength = builder.bridgeLength;
        this.bridgeSpanCombination = builder.bridgeSpanCombination;
        this.deckTotalWidth = builder.deckTotalWidth;
        this.laneWidth = builder.laneWidth;
        this.deckEvaluation = builder.deckEvaluation;
        this.subClearHeight = builder.subClearHeight;
        this.superClearHeight = builder.superClearHeight;
        this.accessTotalWidth = builder.accessTotalWidth;
        this.accessRoadWidth = builder.accessRoadWidth;
        this.accessLinearId = builder.accessLinearId;
        this.accessLinearName = builder.accessLinearName;
        this.taskId = builder.taskId;
        this.upload = builder.upload;
    }

    public TblBridgeSide setId(String id) {
        this.id = id;
        return this;
    }

    public TblBridgeSide setBridgeId(String bridgeId) {
        this.bridgeId = bridgeId;
        return this;
    }

    public TblBridgeSide setBridgeName(String bridgeName) {
        this.bridgeName = bridgeName;
        return this;
    }

    public TblBridgeSide setSideTypeId(String sideTypeId) {
        this.sideTypeId = sideTypeId;
        return this;
    }

    public TblBridgeSide setSideTypeName(String sideTypeName) {
        this.sideTypeName = sideTypeName;
        return this;
    }

    public TblBridgeSide setSideTypeSortNo(int sideTypeSortNo) {
        this.sideTypeSortNo = sideTypeSortNo;
        return this;
    }

    public TblBridgeSide setStakeNo(float stakeNo) {
        this.stakeNo = stakeNo;
        return this;
    }

    public TblBridgeSide setBridgeLength(float bridgeLength) {
        this.bridgeLength = bridgeLength;
        return this;
    }

    public TblBridgeSide setBridgeSpanCombination(String bridgeSpanCombination) {
        this.bridgeSpanCombination = bridgeSpanCombination;
        return this;
    }

    public TblBridgeSide setDeckTotalWidth(float deckTotalWidth) {
        this.deckTotalWidth = deckTotalWidth;
        return this;
    }

    public TblBridgeSide setLaneWidth(float laneWidth) {
        this.laneWidth = laneWidth;
        return this;
    }

    public TblBridgeSide setDeckEvaluation(float deckEvaluation) {
        this.deckEvaluation = deckEvaluation;
        return this;
    }

    public TblBridgeSide setSubClearHeight(float subClearHeight) {
        this.subClearHeight = subClearHeight;
        return this;
    }

    public TblBridgeSide setSuperClearHeight(float superClearHeight) {
        this.superClearHeight = superClearHeight;
        return this;
    }

    public TblBridgeSide setAccessTotalWidth(float accessTotalWidth) {
        this.accessTotalWidth = accessTotalWidth;
        return this;
    }

    public TblBridgeSide setAccessRoadWidth(float accessRoadWidth) {
        this.accessRoadWidth = accessRoadWidth;
        return this;
    }

    public TblBridgeSide setAccessLinearId(String accessLinearId) {
        this.accessLinearId = accessLinearId;
        return this;
    }

    public TblBridgeSide setAccessLinearName(String accessLinearName) {
        this.accessLinearName = accessLinearName;
        return this;
    }

    public TblBridgeSide setTaskId(String taskId) {
        this.taskId = taskId;
        return this;
    }

    public TblBridgeSide setUpload(int upload) {
        this.upload = upload;
        return this;
    }

    public String getId() {
        return id;
    }

    public String getBridgeId() {
        return bridgeId;
    }

    public String getBridgeName() {
        return bridgeName;
    }

    public String getSideTypeId() {
        return sideTypeId;
    }

    public String getSideTypeName() {
        return sideTypeName;
    }

    public int getSideTypeSortNo() {
        return sideTypeSortNo;
    }

    public float getStakeNo() {
        return stakeNo;
    }

    public float getBridgeLength() {
        return bridgeLength;
    }

    public String getBridgeSpanCombination() {
        return bridgeSpanCombination;
    }

    public float getDeckTotalWidth() {
        return deckTotalWidth;
    }

    public float getLaneWidth() {
        return laneWidth;
    }

    public float getDeckEvaluation() {
        return deckEvaluation;
    }

    public float getSubClearHeight() {
        return subClearHeight;
    }

    public float getSuperClearHeight() {
        return superClearHeight;
    }

    public float getAccessTotalWidth() {
        return accessTotalWidth;
    }

    public float getAccessRoadWidth() {
        return accessRoadWidth;
    }

    public String getAccessLinearId() {
        return accessLinearId;
    }

    public String getAccessLinearName() {
        return accessLinearName;
    }

    public String getTaskId() {
        return taskId;
    }

    public int getUpload() {
        return upload;
    }
}
