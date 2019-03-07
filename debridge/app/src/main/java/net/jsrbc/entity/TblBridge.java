package net.jsrbc.entity;

import net.jsrbc.frame.annotation.database.object.Column;
import net.jsrbc.frame.annotation.database.object.QueryColumn;
import net.jsrbc.frame.annotation.database.object.Table;
import net.jsrbc.frame.annotation.database.restraint.PrimaryKey;

import java.io.Serializable;

/**
 * Created by ZZZ on 2017-12-04.
 */
@Table("TBL_BRIDGE")
public final class TblBridge implements Serializable {

    @Column("TASK_BRIDGE_ID")
    @PrimaryKey
    private String taskBridgeId;

    @Column("ID")
    private String id;

    @Column("NAME")
    private String name;

    @Column("ROAD_NO")
    private String roadNo;

    @Column("ROAD_NAME")
    private String roadName;

    @Column("STAKE_NO")
    private double stakeNo;

    @Column("ROAD_LEVEL_NAME")
    private String roadLevelName;

    @Column("ADDRESS_NAME")
    private String addressName;

    @Column("FULL_ADDRESS_NAME")
    private String fullAddressName;

    @Column("UNITS_NAME")
    private String unitsName;

    @Column("FUNCTION_TYPE_NAME")
    private String functionTypeName;

    @Column("DNCROSS_NAME")
    private String dncrossName;

    @Column("DNCROSS_STAKE_NO")
    private String dncrossStakeNo;

    @Column("DESIGN_LOAD_NAME")
    private String designLoadName;

    @Column("LOAD_CAPACITY_NAME")
    private String loadCapacityName;

    @Column("SLOP")
    private String slop;

    @Column("BRIDGE_CATEGORY_NAME")
    private String bridgeCategoryName;

    @Column("CROSS_TYPE_NAME")
    private String crossTypeName;

    @Column("LONGITUDE")
    private double longitude;

    @Column("LATITUDE")
    private double latitude;

    @Column("ACCELERATION_FACTOR")
    private double accelerationFactor;

    @Column("PIER_PROTECTOR")
    private String pierProtector;

    @Column("NORMAL_WATER_LEVEL")
    private String normalWaterLevel;

    @Column("DESIGN_WATER_LEVEL")
    private String designWaterLevel;

    @Column("HISTORY_FLOOD_LEVEL")
    private String historyFloodLevel;

    @Column("DECK_DIVIDE_TYPE_ID")
    private String deckDivideTypeId;

    @Column("DECK_DIVIDE_TYPE_NAME")
    private String deckDivideTypeName;

    @Column("BUILD_DATE_STR")
    private String buildDateStr;

    @Column("TASK_ID")
    private String taskId;

    /** 任务单类型 */
    @Column("TASK_TYPE")
    private int taskType;

    /** 正面照 */
    @Column("FRONT_PATH")
    private String frontPath;

    /** 侧面照 */
    @Column("SIDE_PATH")
    private String sidePath;

    /** 仰视照 */
    @Column("UPWARD_PATH")
    private String upwardPath;

    /** 上次评定等级 */
    @Column("BRIDGE_EVALUATION_LEVEL")
    private String bridgeEvaluationLevel;

    /** 上次检查日期 */
    @Column("INSPECTION_DATE")
    private String inspectionDate;

    @QueryColumn("SIDE_COUNT")
    private int sideCount;

    //用于上传的参数
    @Column("UPLOAD_PHOTO")
    private int uploadPhoto;

    //是否上传坐标
    @Column("UPLOAD_LOCATION")
    private int uploadLocation;

    @Column("PROGRESS")
    private int progress;

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

    public String getRoadNo() {
        return roadNo;
    }

    public void setRoadNo(String roadNo) {
        this.roadNo = roadNo;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public double getStakeNo() {
        return stakeNo;
    }

    public void setStakeNo(double stakeNo) {
        this.stakeNo = stakeNo;
    }

    public String getRoadLevelName() {
        return roadLevelName;
    }

    public void setRoadLevelName(String roadLevelName) {
        this.roadLevelName = roadLevelName;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getFullAddressName() {
        return fullAddressName;
    }

    public void setFullAddressName(String fullAddressName) {
        this.fullAddressName = fullAddressName;
    }

    public String getUnitsName() {
        return unitsName;
    }

    public void setUnitsName(String unitsName) {
        this.unitsName = unitsName;
    }

    public String getFunctionTypeName() {
        return functionTypeName;
    }

    public void setFunctionTypeName(String functionTypeName) {
        this.functionTypeName = functionTypeName;
    }

    public String getDncrossName() {
        return dncrossName;
    }

    public void setDncrossName(String dncrossName) {
        this.dncrossName = dncrossName;
    }

    public String getDncrossStakeNo() {
        return dncrossStakeNo;
    }

    public void setDncrossStakeNo(String dncrossStakeNo) {
        this.dncrossStakeNo = dncrossStakeNo;
    }

    public String getDesignLoadName() {
        return designLoadName;
    }

    public void setDesignLoadName(String designLoadName) {
        this.designLoadName = designLoadName;
    }

    public String getLoadCapacityName() {
        return loadCapacityName;
    }

    public void setLoadCapacityName(String loadCapacityName) {
        this.loadCapacityName = loadCapacityName;
    }

    public String getSlop() {
        return slop;
    }

    public void setSlop(String slop) {
        this.slop = slop;
    }

    public String getBridgeCategoryName() {
        return bridgeCategoryName;
    }

    public void setBridgeCategoryName(String bridgeCategoryName) {
        this.bridgeCategoryName = bridgeCategoryName;
    }

    public String getCrossTypeName() {
        return crossTypeName;
    }

    public void setCrossTypeName(String crossTypeName) {
        this.crossTypeName = crossTypeName;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getAccelerationFactor() {
        return accelerationFactor;
    }

    public void setAccelerationFactor(double accelerationFactor) {
        this.accelerationFactor = accelerationFactor;
    }

    public String getPierProtector() {
        return pierProtector;
    }

    public void setPierProtector(String pierProtector) {
        this.pierProtector = pierProtector;
    }

    public String getNormalWaterLevel() {
        return normalWaterLevel;
    }

    public void setNormalWaterLevel(String normalWaterLevel) {
        this.normalWaterLevel = normalWaterLevel;
    }

    public String getDesignWaterLevel() {
        return designWaterLevel;
    }

    public void setDesignWaterLevel(String designWaterLevel) {
        this.designWaterLevel = designWaterLevel;
    }

    public String getHistoryFloodLevel() {
        return historyFloodLevel;
    }

    public void setHistoryFloodLevel(String historyFloodLevel) {
        this.historyFloodLevel = historyFloodLevel;
    }

    public String getDeckDivideTypeId() {
        return deckDivideTypeId;
    }

    public void setDeckDivideTypeId(String deckDivideTypeId) {
        this.deckDivideTypeId = deckDivideTypeId;
    }

    public String getDeckDivideTypeName() {
        return deckDivideTypeName;
    }

    public void setDeckDivideTypeName(String deckDivideTypeName) {
        this.deckDivideTypeName = deckDivideTypeName;
    }

    public String getBuildDateStr() {
        return buildDateStr;
    }

    public void setBuildDateStr(String buildDateStr) {
        this.buildDateStr = buildDateStr;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskBridgeId() {
        return taskBridgeId;
    }

    public void setTaskBridgeId(String taskBridgeId) {
        this.taskBridgeId = taskBridgeId;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public String getFrontPath() {
        return frontPath;
    }

    public void setFrontPath(String frontPath) {
        this.frontPath = frontPath;
    }

    public String getSidePath() {
        return sidePath;
    }

    public void setSidePath(String sidePath) {
        this.sidePath = sidePath;
    }

    public String getUpwardPath() {
        return upwardPath;
    }

    public void setUpwardPath(String upwardPath) {
        this.upwardPath = upwardPath;
    }

    public String getBridgeEvaluationLevel() {
        return bridgeEvaluationLevel;
    }

    public void setBridgeEvaluationLevel(String bridgeEvaluationLevel) {
        this.bridgeEvaluationLevel = bridgeEvaluationLevel;
    }

    public String getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(String inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public int getSideCount() {
        return sideCount;
    }

    public void setSideCount(int sideCount) {
        this.sideCount = sideCount;
    }

    public int getUploadPhoto() {
        return uploadPhoto;
    }

    public void setUploadPhoto(int uploadPhoto) {
        this.uploadPhoto = uploadPhoto;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getUploadLocation() {
        return uploadLocation;
    }

    public void setUploadLocation(int uploadLocation) {
        this.uploadLocation = uploadLocation;
    }
}
