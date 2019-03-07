package net.jsrbc.mapper;

import net.jsrbc.R;
import net.jsrbc.entity.TblBridge;
import net.jsrbc.entity.TblBridgeMember;
import net.jsrbc.entity.TblBridgeParts;
import net.jsrbc.frame.annotation.database.Param;
import net.jsrbc.frame.annotation.database.dml.Delete;
import net.jsrbc.frame.annotation.database.dml.Insert;
import net.jsrbc.frame.annotation.database.dml.Select;
import net.jsrbc.pojo.BridgePartsForm;

import java.util.List;

/**
 * Created by ZZZ on 2017-12-16.
 */
public interface BridgeMemberMapper {

    @Insert(R.string.addOrReplaceBridgeMemberList)
    void addOrReplaceBridgeMemberList(List<TblBridgeMember> bridgeMemberList);

    @Delete(R.string.deleteSuperMemberRangeOf)
    void deleteSuperMemberRangeOf(BridgePartsForm bridgePartsForm);

    @Delete(R.string.deletePierMemberRangeOf)
    void deletePierMemberRangeOf(BridgePartsForm bridgePartsForm);

    @Delete(R.string.deletePierBaseMemberRangeOf)
    void deletePierBaseMemberRangeOf(BridgePartsForm bridgePartsForm);

    @Delete(R.string.deleteAbutmentMember)
    void deleteAbutmentMember(@Param("bridgeId") String bridgeId,
                              @Param("sideTypeId") String sideTypeId);

    @Delete(R.string.deleteAbutmentBaseMember)
    void deleteAbutmentBaseMember(@Param("bridgeId") String bridgeId,
                                  @Param("sideTypeId") String sideTypeId);

    @Delete(R.string.deleteAbutmentAttachMember)
    void deleteAbutmentAttachMember(@Param("bridgeId") String bridgeId,
                                    @Param("sideTypeId") String sideTypeId);

    @Delete(R.string.deleteMemberRangeOf)
    void deleteMemberRangeOf(@Param("bridgeId") String bridgeId,
                             @Param("sideTypeId") String sideTypeId,
                            @Param("partsTypeId") String partsTypeId,
                            @Param("startSiteNo") int startSiteNo,
                            @Param("endSiteNo") int endSiteNo);

    @Delete(R.string.deleteDeckMemberRangeOf)
    void deleteDeckMemberRangeOf(@Param("bridgeId") String bridgeId,
                                 @Param("sideTypeId") String sideTypeId,
                                 @Param("siteNo") int siteNo);

    @Delete(R.string.deleteExpansionJointMemberRangeOf)
    void deleteExpansionJointMemberRangeOf(@Param("bridgeId") String bridgeId,
                                           @Param("sideTypeId") String sideTypeId,
                                           @Param("siteNo") int siteNo);

    @Delete(R.string.deleteBridgeMemberByParts)
    void deleteBridgeMemberByParts(TblBridgeParts bridgeParts);

    @Select(R.string.getBridgeMemberList)
    List<TblBridgeMember> getBridgeMemberList(@Param("bridgeId") String bridgeId,
                                              @Param("sideTypeId") String sideTypeId,
                                              @Param("siteNo") int siteNo,
                                              @Param("memberTypeId") String memberTypeId);

    @Select(R.string.getBridgeMemberListBySide)
    List<TblBridgeMember> getBridgeMemberListBySide(@Param("bridgeId") String bridgeId,
                                                    @Param("sideTypeId") String sideTypeId);
}
