package net.jsrbc.client;

import net.jsrbc.entity.TblParamDiseaseDesc;
import net.jsrbc.entity.TblParamDiseaseType;
import net.jsrbc.entity.TblParamEvaluationIndex;
import net.jsrbc.entity.TblParamGroup;
import net.jsrbc.entity.TblParamMemberDesc;
import net.jsrbc.entity.TblParamMemberType;
import net.jsrbc.entity.TblParamPartsType;
import net.jsrbc.entity.TblParamPositionType;
import net.jsrbc.entity.TblParamStructureGroup;
import net.jsrbc.entity.TblParamStructureType;
import net.jsrbc.entity.TblParamType;
import net.jsrbc.entity.TblRAbutmentMember;
import net.jsrbc.entity.TblRBridgeParts;
import net.jsrbc.entity.TblRMemberDisease;
import net.jsrbc.entity.TblRSuperstructureMember;
import net.jsrbc.frame.annotation.client.RequestMapping;
import net.jsrbc.frame.enumeration.RequestMethod;
import net.jsrbc.frame.exception.NonAuthoritativeException;

import java.io.IOException;
import java.util.List;

/**
 * 用于获取基础数据
 * Created by ZZZ on 2017-12-01.
 */
public interface BaseDataClient {

    @RequestMapping(path = "/mobile/getParamGroupList", method = RequestMethod.GET)
    List<TblParamGroup> getParamGroupList() throws IOException, NonAuthoritativeException;

    @RequestMapping(path = "/mobile/getParamTypeList", method = RequestMethod.GET)
    List<TblParamType> getParamTypeList() throws IOException, NonAuthoritativeException;

    @RequestMapping(path = "/mobile/getParamStructureGroupList", method = RequestMethod.GET)
    List<TblParamStructureGroup> getParamStructureGroupList() throws IOException, NonAuthoritativeException;

    @RequestMapping(path = "/mobile/getParamStructureTypeList", method = RequestMethod.GET)
    List<TblParamStructureType> getParamStructureTypeList() throws IOException, NonAuthoritativeException;

    @RequestMapping(path = "/mobile/getParamPositionTypeList", method = RequestMethod.GET)
    List<TblParamPositionType> getParamPositionTypeList() throws IOException, NonAuthoritativeException;

    @RequestMapping(path = "/mobile/getParamPartsTypeList", method = RequestMethod.GET)
    List<TblParamPartsType> getParamPartsTypeList() throws IOException, NonAuthoritativeException;

    @RequestMapping(path = "/mobile/getParamMemberTypeList", method = RequestMethod.GET)
    List<TblParamMemberType> getParamMemberTypeList() throws IOException, NonAuthoritativeException;

    @RequestMapping(path = "/mobile/getParamMemberDescList", method = RequestMethod.GET)
    List<TblParamMemberDesc> getParamMemberDescList() throws IOException, NonAuthoritativeException;

    @RequestMapping(path = "/mobile/getBridgePartsRelationList", method = RequestMethod.GET)
    List<TblRBridgeParts> getBridgePartsRelationList() throws IOException, NonAuthoritativeException;

    @RequestMapping(path = "/mobile/getAbutmentMemberRelationList", method = RequestMethod.GET)
    List<TblRAbutmentMember> getAbutmentMemberRelationList() throws IOException, NonAuthoritativeException;

    @RequestMapping(path = "/mobile/getSuperstructureMemberRelationList", method = RequestMethod.GET)
    List<TblRSuperstructureMember> getSuperstructureMemberRelationList() throws IOException, NonAuthoritativeException;

    @RequestMapping(path = "/mobile/getParamDiseaseDescList", method = RequestMethod.GET)
    List<TblParamDiseaseDesc> getParamDiseaseDescList() throws IOException, NonAuthoritativeException;

    @RequestMapping(path = "/mobile/getParamDiseaseTypeList", method = RequestMethod.GET)
    List<TblParamDiseaseType> getParamDiseaseTypeList() throws IOException, NonAuthoritativeException;

    @RequestMapping(path = "/mobile/getParamEvaluationIndexList", method = RequestMethod.GET)
    List<TblParamEvaluationIndex> getParamEvaluationIndexList() throws IOException, NonAuthoritativeException;

    @RequestMapping(path = "/mobile/getMemberDiseaseRelationList", method = RequestMethod.GET)
    List<TblRMemberDisease> getMemberDiseaseRelationList() throws IOException, NonAuthoritativeException;
}
