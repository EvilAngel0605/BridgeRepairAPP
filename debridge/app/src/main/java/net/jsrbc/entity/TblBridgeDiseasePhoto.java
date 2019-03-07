package net.jsrbc.entity;

import net.jsrbc.enumeration.UploadStatus;
import net.jsrbc.frame.annotation.database.object.Column;
import net.jsrbc.frame.annotation.database.object.Table;
import net.jsrbc.frame.annotation.database.restraint.PrimaryKey;
import net.jsrbc.utils.StringUtils;

import java.io.Serializable;

/**
 * Created by ZZZ on 2017-12-22.
 */
@Table("TBL_BRIDGE_DISEASE_PHOTO")
public final class TblBridgeDiseasePhoto implements Serializable {

    @Column("ID")
    @PrimaryKey
    private String id;

    @Column("DISEASE_ID")
    private String diseaseId;

    @Column("PATH")
    private String path;

    @Column("UPLOAD")
    private int upload;

    public TblBridgeDiseasePhoto() {}

    public TblBridgeDiseasePhoto(String diseaseId, String path) {
        this.id = StringUtils.createId();
        this.diseaseId = diseaseId;
        this.path = path;
        this.upload = UploadStatus.NEED_UPLOAD.getCode();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDiseaseId() {
        return diseaseId;
    }

    public void setDiseaseId(String diseaseId) {
        this.diseaseId = diseaseId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getUpload() {
        return upload;
    }

    public void setUpload(int upload) {
        this.upload = upload;
    }
}
