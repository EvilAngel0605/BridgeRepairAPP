package net.jsrbc.pojo;

import net.jsrbc.entity.TblBridgeParts;

import java.util.List;

/**
 * Created by ZZZ on 2017-12-14.
 */
public final class BridgePartsForm {

    private String bridgeId;

    private String sideTypeId;

    private int startSiteNo;

    private int endSiteNo;

    private String siteRange;

    private String structureTypeId;

    private List<TblBridgeParts> partsList;

    /** 构建器 */
    public static class Builder {

        private String bridgeId;

        private String sideTypeId;

        private int startSiteNo;

        private int endSiteNo;

        private String siteRange;

        private String structureTypeId;

        private List<TblBridgeParts> partsList;

        public Builder bridgeId(String bridgeId) {
            this.bridgeId = bridgeId;
            return this;
        }

        public Builder sideTypeId(String sideTypeId) {
            this.sideTypeId = sideTypeId;
            return this;
        }

        /** 起始孔号 */
        public Builder startSiteNo(int startSiteNo) {
            this.startSiteNo = startSiteNo;
            return this;
        }

        /** 结束孔号 */
        public Builder endSiteNo(int endSiteNo) {
            this.endSiteNo = endSiteNo;
            return this;
        }

        /** 孔跨范围 */
        public Builder siteRange(String siteRange) {
            this.siteRange = siteRange;
            return this;
        }

        /** 结构类型，用于桥面系添加 */
        public Builder structureTypeId(String structureTypeId) {
            this.structureTypeId = structureTypeId;
            return this;
        }

        /** 部件集合 */
        public Builder partsList(List<TblBridgeParts> partsList) {
            this.partsList = partsList;
            return this;
        }

        public BridgePartsForm build() {
            return new BridgePartsForm(this);
        }
    }

    private BridgePartsForm(Builder builder) {
        this.bridgeId = builder.bridgeId;
        this.sideTypeId = builder.sideTypeId;
        this.startSiteNo = builder.startSiteNo;
        this.endSiteNo = builder.endSiteNo;
        this.siteRange = builder.siteRange;
        this.structureTypeId = builder.structureTypeId;
        this.partsList = builder.partsList;
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

    public void setStartSiteNo(int startSiteNo) {
        this.startSiteNo = startSiteNo;
    }

    public void setEndSiteNo(int endSiteNo) {
        this.endSiteNo = endSiteNo;
    }

    public void setSiteRange(String siteRange) {
        this.siteRange = siteRange;
    }

    public void setStructureTypeId(String structureTypeId) {
        this.structureTypeId = structureTypeId;
    }

    public void setPartsList(List<TblBridgeParts> partsList) {
        this.partsList = partsList;
    }

    public int getStartSiteNo() {
        return startSiteNo;
    }

    public int getEndSiteNo() {
        return endSiteNo;
    }

    public String getSiteRange() {
        return siteRange;
    }

    public String getStructureTypeId() {
        return structureTypeId;
    }

    public List<TblBridgeParts> getPartsList() {
        return partsList;
    }
}
