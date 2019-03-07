package net.jsrbc.mapper;

import net.jsrbc.R;
import net.jsrbc.entity.TblBridge;
import net.jsrbc.entity.TblBridgeSide;
import net.jsrbc.frame.annotation.database.Param;
import net.jsrbc.frame.annotation.database.dml.Delete;
import net.jsrbc.frame.annotation.database.dml.Insert;
import net.jsrbc.frame.annotation.database.dml.Select;
import net.jsrbc.frame.annotation.database.dml.Update;

import java.util.List;

/**
 * Created by ZZZ on 2017-12-04.
 */
public interface BridgeMapper {

    @Insert(R.string.addOrReplaceBridgeList)
    void addOrReplaceBridgeList(List<TblBridge> bridgeList);

    @Select(R.string.getBridgeListByTaskType)
    List<TblBridge> getBridgeListByTaskType(@Param("taskType") int taskType);

    @Select(R.string.getBridgeListByTaskId)
    List<TblBridge> getBridgeListByTaskId(@Param("taskId") String taskId);

    @Select(R.string.getBridgeById)
    TblBridge getBridgeById(@Param("id") String id);

    @Select(R.string.getBridgeListForUpload)
    List<TblBridge> getBridgeListForUpload();

    @Select(R.string.getBridgeCountForUpload)
    int getBridgeCountForUpload();

    @Update(R.string.updateBridgePhoto)
    void updateBridgePhoto(TblBridge bridge);

    @Update(R.string.updateBridgeLocation)
    void updateBridgeLocation(TblBridge bridge);

    @Update(R.string.updateBridgeProgress)
    void updateBridgeProgress(TblBridge bridge);

    @Update(R.string.setBridgePhotoUploadSuccess)
    void setBridgePhotoUploadSuccess(@Param("id") String bridgeId);

    @Update(R.string.setBridgeLocationUploadSuccess)
    void setBridgeLocationUploadSuccess(@Param("id") String bridgeId);
}
