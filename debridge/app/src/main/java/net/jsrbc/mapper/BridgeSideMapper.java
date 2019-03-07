package net.jsrbc.mapper;

import net.jsrbc.R;
import net.jsrbc.entity.TblBridgeSide;
import net.jsrbc.frame.annotation.database.Param;
import net.jsrbc.frame.annotation.database.dml.Delete;
import net.jsrbc.frame.annotation.database.dml.Insert;
import net.jsrbc.frame.annotation.database.dml.Select;
import net.jsrbc.frame.annotation.database.dml.Update;

import java.util.List;

/**
 * Created by ZZZ on 2017-12-11.
 */
public interface BridgeSideMapper {

    @Insert(R.string.addOrReplaceBridgeSide)
    void addOrReplaceBridgeSide(TblBridgeSide bridgeSide);

    @Insert(R.string.addOrReplaceBridgeSideList)
    void addOrReplaceBridgeSideList(List<TblBridgeSide> bridgeSideList);

    @Select(R.string.getBridgeSideListByBridgeId)
    List<TblBridgeSide> getBridgeSideListByBridgeId(@Param("bridgeId") String bridgeId);

    @Select(R.string.getBridgeSideBySideType)
    TblBridgeSide getBridgeSideBySideType(@Param("bridgeId") String bridgeId, @Param("sideTypeId") String sideTypeId);

    @Select(R.string.getBridgeSideById)
    TblBridgeSide getBridgeSideById(@Param("id") String id);

    @Update(R.string.updateBridgeSpanCombination)
    void updateBridgeSpanCombination(TblBridgeSide bridgeSide);

    @Select(R.string.getBridgeSideListForUpload)
    List<TblBridgeSide> getBridgeSideListForUpload(@Param("bridgeId") String bridgeId);

    @Select(R.string.getBridgeSideListForUploadSite)
    List<TblBridgeSide> getBridgeSideListForUploadSite(@Param("bridgeId") String bridgeId);
}
