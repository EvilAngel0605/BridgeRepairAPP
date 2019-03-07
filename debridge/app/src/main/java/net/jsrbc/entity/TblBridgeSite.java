package net.jsrbc.entity;

import net.jsrbc.frame.annotation.database.object.Column;
import net.jsrbc.frame.annotation.database.object.Table;
import net.jsrbc.frame.annotation.database.restraint.PrimaryKey;

import java.io.Serializable;

/**
 * Created by ZZZ on 2017-12-04.
 */
@Table("TBL_BRIDGE_SITE")
public final class TblBridgeSite implements Serializable {

    @Column("ID")
    @PrimaryKey
    private String id;

    @Column("BRIDGE_ID")
    private String bridgeId;

    @Column("SIDE_TYPE_ID")
    private String sideTypeId;

    @Column("SITE_NO")
    private int siteNo;

    @Column("JOINT_NO")
    private int jointNo;

    @Column("SPAN")
    private float span;

    @Column("SUPERSTRUCTURE_TYPE_NAME")
    private String superstructureTypeName;

    @Column("SUPERSTRUCTURE_MATERIAL_TYPE_NAME")
    private String superstructureMaterialTypeName;

    @Column("MEMBER_COUNT")
    private int memberCount;

    @Column("UPLOAD")
    private int upload;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (this.jointNo > 0) builder.append("第").append(this.jointNo).append("联");
        builder.append("第").append(this.siteNo).append("孔");
        return builder.toString();
    }

    /** ID */
    public TblBridgeSite id(String id) {
        this.id = id;
        return this;
    }

    public TblBridgeSite bridgeId(String bridgeId) {
        this.bridgeId = bridgeId;
        return this;
    }

    public TblBridgeSite sideTypeId(String sideTypeId) {
        this.sideTypeId = sideTypeId;
        return this;
    }

    /** 孔号 */
    public TblBridgeSite siteNo(int siteNo) {
        this.siteNo = siteNo;
        return this;
    }

    /** 联号 */
    public TblBridgeSite jointNo(int jointNo) {
        this.jointNo = jointNo;
        return this;
    }

    /** 跨径 */
    public TblBridgeSite span(float span) {
        this.span = span;
        return this;
    }

    public TblBridgeSite upload(int upload) {
        this.upload = upload;
        return this;
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

    public int getJointNo() {
        return jointNo;
    }

    public void setJointNo(int jointNo) {
        this.jointNo = jointNo;
    }

    public float getSpan() {
        return span;
    }

    public void setSpan(float span) {
        this.span = span;
    }

    public String getSuperstructureTypeName() {
        return superstructureTypeName;
    }

    public void setSuperstructureTypeName(String superstructureTypeName) {
        this.superstructureTypeName = superstructureTypeName;
    }

    public String getSuperstructureMaterialTypeName() {
        return superstructureMaterialTypeName;
    }

    public void setSuperstructureMaterialTypeName(String superstructureMaterialTypeName) {
        this.superstructureMaterialTypeName = superstructureMaterialTypeName;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public int getUpload() {
        return upload;
    }

    public void setUpload(int upload) {
        this.upload = upload;
    }
}
