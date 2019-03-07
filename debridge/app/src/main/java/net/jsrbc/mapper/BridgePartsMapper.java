package net.jsrbc.mapper;

import net.jsrbc.R;
import net.jsrbc.entity.TblBridgeParts;
import net.jsrbc.frame.annotation.database.Param;
import net.jsrbc.frame.annotation.database.dml.Delete;
import net.jsrbc.frame.annotation.database.dml.Insert;
import net.jsrbc.frame.annotation.database.dml.Select;
import net.jsrbc.frame.annotation.database.dml.Update;
import net.jsrbc.pojo.BridgePartsForm;

import java.util.List;

/**
 * Created by ZZZ on 2017-12-04.
 */
public interface BridgePartsMapper {

    @Insert(R.string.addOrReplaceBridgePartsList)
    void addOrReplaceBridgePartsList(List<TblBridgeParts> bridgePartsList);

    @Delete(R.string.deleteSuperPartsRangeOf)
    void deleteSuperPartsRangeOf(BridgePartsForm bridgePartsForm);

    @Delete(R.string.deletePierPartsRangeOf)
    void deletePierPartsRangeOf(BridgePartsForm bridgePartsForm);

    @Delete(R.string.deletePierBasePartsRangeOf)
    void deletePierBasePartsRangeOf(BridgePartsForm bridgePartsForm);

    @Delete(R.string.deleteAbutmentParts)
    void deleteAbutmentParts(@Param("bridgeId") String bridgeId,
                             @Param("sideTypeId") String sideTypeId);

    @Delete(R.string.deleteAbutmentBaseParts)
    void deleteAbutmentBaseParts(@Param("bridgeId") String bridgeId,
                                 @Param("sideTypeId") String sideTypeId);

    @Delete(R.string.deleteAbutmentAttachParts)
    void deleteAbutmentAttachParts(@Param("bridgeId") String bridgeId,
                                   @Param("sideTypeId") String sideTypeId);

    @Delete(R.string.deletePartsRangeOf)
    void deletePartsRangeOf(@Param("bridgeId") String bridgeId,
                            @Param("sideTypeId") String sideTypeId,
                            @Param("partsTypeId") String partsTypeId,
                            @Param("startSiteNo") int startSiteNo,
                            @Param("endSiteNo") int endSiteNo);

    @Delete(R.string.deleteDeckPartsRangeOf)
    void deleteDeckPartsRangeOf(@Param("bridgeId") String bridgeId,
                                @Param("sideTypeId") String sideTypeId,
                                @Param("siteNo") int siteNo);

    @Delete(R.string.deleteExpansionJointPartsRangeOf)
    void deleteExpansionJointPartsRangeOf(@Param("bridgeId") String bridgeId,
                                          @Param("sideTypeId") String sideTypeId,
                                          @Param("siteNo") int siteNo);

    @Delete(R.string.deleteBridgePartsById)
    void deleteBridgePartsById(@Param("id") String id);

    @Select(R.string.getBridgePartsListBySide)
    List<TblBridgeParts> getBridgePartsListBySide(@Param("bridgeId") String bridgeId, @Param("sideTypeId") String sideTypeId);

    @Select(R.string.getBridgePartsListBySuperstructure)
    List<TblBridgeParts> getBridgePartsListBySuperstructure(@Param("superstructureTypeId") String superstructureTypeId);

    @Select(R.string.getBridgePartsListByPier)
    List<TblBridgeParts> getBridgePartsListByPier();

    @Select(R.string.getBridgePartsListByAbutment)
    List<TblBridgeParts> getBridgePartsListByAbutment(@Param("abutmentTypeId") String abutmentTypeId);

    @Select(R.string.getBridgePartsListByAbutmentAttach)
    List<TblBridgeParts> getBridgePartsListByAbutmentAttach();

    @Select(R.string.getBridgePartsListByRiverbed)
    List<TblBridgeParts> getBridgePartsListByRiverbed();

    @Select(R.string.getBridgePartsListByRegulatingStructure)
    List<TblBridgeParts> getBridgePartsListByRegulatingStructure();

    @Select(R.string.getBridgePartsListByDeck)
    List<TblBridgeParts> getBridgePartsListByDeck();

    @Select(R.string.getBridgePartsListByExpansionJoint)
    List<TblBridgeParts> getBridgePartsListByExpansionJoint();

    @Select(R.string.getBridgePartsListBySiteNo)
    List<TblBridgeParts> getBridgePartsListBySiteNo(@Param("bridgeId") String bridgeId,
                                                    @Param("sideTypeId") String sideTypeId,
                                                    @Param("siteNo") int siteNo);

    @Select(R.string.getBridgePartsListBySiteNoAndPosition)
    List<TblBridgeParts> getBridgePartsListBySiteNoAndPosition(@Param("bridgeId") String bridgeId,
                                                               @Param("sideTypeId") String sideTypeId,
                                                               @Param("siteNo") int siteNo,
                                                               @Param("positionTypeName") String positionTypeName);

    @Select(R.string.getBridgePartsListForUpload)
    List<TblBridgeParts> getBridgePartsListForUpload(@Param("bridgeId") String bridgeId);

    @Update(R.string.updateBridgeParts)
    void updateBridgeParts(TblBridgeParts bridgeParts);
}
