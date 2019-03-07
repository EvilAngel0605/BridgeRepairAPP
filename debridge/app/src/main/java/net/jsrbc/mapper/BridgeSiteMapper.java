package net.jsrbc.mapper;

import net.jsrbc.R;
import net.jsrbc.entity.TblBridgeSide;
import net.jsrbc.entity.TblBridgeSite;
import net.jsrbc.frame.annotation.database.Param;
import net.jsrbc.frame.annotation.database.dml.Delete;
import net.jsrbc.frame.annotation.database.dml.Insert;
import net.jsrbc.frame.annotation.database.dml.Select;
import net.jsrbc.frame.annotation.database.dml.Update;

import java.util.List;

/**
 * Created by ZZZ on 2017-12-04.
 */
public interface BridgeSiteMapper {

    @Insert(R.string.addOrReplaceBridgeSiteList)
    void addOrReplaceBridgeSiteList(List<TblBridgeSite> bridgeSiteList);

    @Delete(R.string.deleteBridgeSiteBySide)
    void deleteBridgeSiteBySide(@Param("bridgeId") String bridgeId,
                                @Param("sideTypeId") String sideTypeId);

    @Select(R.string.getSiteCountBySide)
    int getSiteCountBySide(@Param("bridgeId") String bridgeId,
                           @Param("sideTypeId") String sideTypeId);

    @Select(R.string.getBridgeSiteListBySide)
    List<TblBridgeSite> getBridgeSiteListBySide(@Param("bridgeId") String bridgeId,
                                                @Param("sideTypeId") String sideTypeId);

    @Select(R.string.getSiteCountForUpload)
    int getSiteCountForUpload(TblBridgeSide bridgeSide);

    @Update(R.string.setSiteUploadSuccess)
    void setSiteUploadSuccess(TblBridgeSide bridgeSide);
}
