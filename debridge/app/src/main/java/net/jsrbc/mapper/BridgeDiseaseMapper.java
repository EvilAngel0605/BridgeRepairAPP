package net.jsrbc.mapper;

import net.jsrbc.R;
import net.jsrbc.entity.TblBridgeDisease;
import net.jsrbc.entity.TblParamDiseaseDesc;
import net.jsrbc.entity.TblParamDiseaseType;
import net.jsrbc.entity.TblParamEvaluationIndex;
import net.jsrbc.frame.annotation.database.Param;
import net.jsrbc.frame.annotation.database.dml.Delete;
import net.jsrbc.frame.annotation.database.dml.Insert;
import net.jsrbc.frame.annotation.database.dml.Select;
import net.jsrbc.frame.annotation.database.dml.Update;

import java.util.List;

/**
 * Created by ZZZ on 2017-12-22.
 */
public interface BridgeDiseaseMapper {

    @Insert(R.string.addOrReplaceDisease)
    void addOrReplaceDisease(TblBridgeDisease bridgeDisease);

    @Insert(R.string.addOrReplaceDisease)
    void addOrReplaceDiseaseList(List<TblBridgeDisease> bridgeDiseaseList);

    @Delete(R.string.deleteDisease)
    void deleteDisease(@Param("id") String id);

    @Select(R.string.getOverallSituationList)
    List<TblBridgeDisease> getOverallSituationList(@Param("taskId") String taskId,
                                                   @Param("bridgeId") String bridgeId,
                                                   @Param("sideTypeId") String sideTypeId);

    @Select(R.string.getDiseaseList)
    List<TblBridgeDisease> getDiseaseList(@Param("taskId") String taskId,
                                          @Param("bridgeId") String bridgeId,
                                          @Param("sideTypeId") String sideTypeId);

    @Select(R.string.getDiseaseListForUpload)
    List<TblBridgeDisease> getDiseaseListForUpload(@Param("taskId") String taskId,
                                                   @Param("bridgeId") String bridgeId);

    @Select(R.string.getDiseaseTypeListByMemberType)
    List<TblParamDiseaseType> getDiseaseTypeListByMemberType(@Param("materialTypeId") String materialTypeId,
                                                             @Param("memberTypeId") String memberTypeId);

    @Select(R.string.getDiseaseDescListByDiseaseDescType)
    List<TblParamDiseaseDesc> getDiseaseDescListByDiseaseDescType(@Param("diseaseDescTypeId") String diseaseDescTypeId);

    @Select(R.string.getEvaluationIndex)
    TblParamEvaluationIndex getEvaluationIndex(@Param("evaluationIndexId") String evaluationIndexId);

    @Select(R.string.checkDiseaseDuplication)
    int checkDiseaseDuplication(TblBridgeDisease bridgeDisease);

    @Update(R.string.increaseDiseaseTypeFrequency)
    void increaseDiseaseTypeFrequency(@Param("materialTypeId") String materialTypeId,
                                      @Param("memberTypeId") String memberTypeId,
                                      @Param("diseaseTypeId") String diseaseTypeId);
}
