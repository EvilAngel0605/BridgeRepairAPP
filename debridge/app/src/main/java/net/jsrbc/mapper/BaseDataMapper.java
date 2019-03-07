package net.jsrbc.mapper;

import net.jsrbc.R;
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
import net.jsrbc.enumeration.ParamGroupField;
import net.jsrbc.enumeration.StructureGroupField;
import net.jsrbc.frame.annotation.database.Param;
import net.jsrbc.frame.annotation.database.dml.Delete;
import net.jsrbc.frame.annotation.database.dml.Insert;
import net.jsrbc.frame.annotation.database.dml.Select;

import java.util.List;

/**
 * Created by ZZZ on 2017-12-12.
 */
public interface BaseDataMapper {

    @Delete(R.string.clearBaseData)
    void clearBaseData();

    @Insert(R.string.addOrReplaceParamGroupList)
    void addOrReplaceParamGroupList(List<TblParamGroup> paramGroupList);

    @Insert(R.string.addOrReplaceParamTypeList)
    void addOrReplaceParamTypeList(List<TblParamType> paramTypeList);

    @Insert(R.string.addOrReplaceStructureGroupList)
    void addOrReplaceStructureGroupList(List<TblParamStructureGroup> structureGroupList);

    @Insert(R.string.addOrReplaceStructureTypeList)
    void addOrReplaceStructureTypeList(List<TblParamStructureType> structureTypeList);

    @Insert(R.string.addOrReplacePositionTypeList)
    void addOrReplacePositionTypeList(List<TblParamPositionType> positionTypeList);

    @Insert(R.string.addOrReplacePartsTypeList)
    void addOrReplacePartsTypeList(List<TblParamPartsType> partsTypeList);

    @Insert(R.string.addOrReplaceMemberTypeList)
    void addOrReplaceMemberTypeList(List<TblParamMemberType> memberTypeList);

    @Insert(R.string.addOrReplaceMemberDescList)
    void addOrReplaceMemberDescList(List<TblParamMemberDesc> memberDescList);

    @Insert(R.string.addOrReplaceBridgePartsRelationList)
    void addOrReplaceBridgePartsRelationList(List<TblRBridgeParts> bridgePartsRelationList);

    @Insert(R.string.addOrReplaceAbutmentMemberRelationList)
    void addOrReplaceAbutmentMemberRelationList(List<TblRAbutmentMember> abutmentMemberRelationList);

    @Insert(R.string.addOrReplaceSuperstructureMemberRelationList)
    void addOrReplaceSuperstructureMemberRelationList(List<TblRSuperstructureMember> superstructureMemberRelationList);

    @Insert(R.string.addOrReplaceDiseaseDescList)
    void addOrReplaceDiseaseDescList(List<TblParamDiseaseDesc> diseaseDescList);

    @Insert(R.string.addOrReplaceDiseaseTypeList)
    void addOrReplaceDiseaseTypeList(List<TblParamDiseaseType> diseaseTypeList);

    @Insert(R.string.addOrReplaceEvaluationIndexList)
    void addOrReplaceEvaluationIndexList(List<TblParamEvaluationIndex> evaluationIndexList);

    @Insert(R.string.addOrReplaceMemberDiseaseRelationList)
    void addOrReplaceMemberDiseaseRelationList(List<TblRMemberDisease> memberDiseaseList);

    @Select(R.string.getParamTypeListByGroupField)
    List<TblParamType> getParamTypeListByGroupField(@Param("field") ParamGroupField field);

    @Select(R.string.getStructureTypeByGroupField)
    List<TblParamStructureType> getStructureTypeByGroupField(@Param("field") StructureGroupField field);

}
