package net.jsrbc.mapper;

import net.jsrbc.R;
import net.jsrbc.entity.TblBridgeDiseasePhoto;
import net.jsrbc.frame.annotation.database.Param;
import net.jsrbc.frame.annotation.database.dml.Delete;
import net.jsrbc.frame.annotation.database.dml.Insert;
import net.jsrbc.frame.annotation.database.dml.Select;
import net.jsrbc.frame.annotation.database.dml.Update;

import java.util.List;

/**
 * Created by ZZZ on 2017-12-22.
 */
public interface BridgeDiseasePhotoMapper {

    @Insert(R.string.addOrReplaceDiseasePhoto)
    void addOrReplaceDiseasePhoto(TblBridgeDiseasePhoto photo);

    @Delete(R.string.deleteDiseasePhoto)
    void deleteDiseasePhoto(@Param("id") String id);

    @Select(R.string.getDiseasePhotoList)
    List<TblBridgeDiseasePhoto> getDiseasePhotoList(@Param("diseaseId") String diseaseId);

    @Select(R.string.getDiseasePhotoListForUpload)
    List<TblBridgeDiseasePhoto> getDiseasePhotoListForUpload(@Param("bridgeId") String bridgeId);

    @Select(R.string.getLatestPhoto)
    TblBridgeDiseasePhoto getLatestPhoto(@Param("diseaseId") String diseaseId);

    @Update(R.string.setDiseasePhotoUploadSuccess)
    void setDiseasePhotoUploadSuccess(@Param("id") String id);

}
