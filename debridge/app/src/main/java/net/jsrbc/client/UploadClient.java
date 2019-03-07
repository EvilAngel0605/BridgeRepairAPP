package net.jsrbc.client;

import net.jsrbc.entity.TblBridge;
import net.jsrbc.entity.TblBridgeDisease;
import net.jsrbc.entity.TblBridgeParts;
import net.jsrbc.entity.TblBridgeSide;
import net.jsrbc.frame.annotation.client.RequestFile;
import net.jsrbc.frame.annotation.client.RequestMapping;
import net.jsrbc.frame.annotation.client.RequestParam;
import net.jsrbc.frame.enumeration.RequestMethod;
import net.jsrbc.frame.exception.NonAuthoritativeException;
import net.jsrbc.pojo.RequestResult;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by ZZZ on 2018-01-03.
 */
public interface UploadClient {

    /** 返回照片上传结果 */
    @RequestMapping(path = "/mobile/uploadBridgePhoto", method = RequestMethod.UPLOAD)
    RequestResult uploadBridgePhoto(@RequestFile File file,
                                    @RequestParam("id") String bridgeId,
                                    @RequestParam("taskBridgeId") String taskBridgeId,
                                    @RequestParam("photoType") String photoType)
            throws IOException, NonAuthoritativeException;

    @RequestMapping(path = "/mobile/uploadBridgeLocation", method = RequestMethod.POST)
    RequestResult uploadBridgeLocation(TblBridge bridge) throws IOException, NonAuthoritativeException;

    /** 返回的是上传成功的分幅列表 */
    @RequestMapping(path = "/mobile/uploadBridgeSide", method = RequestMethod.POST)
    List<TblBridgeSide> uploadBridgeSide(List<TblBridgeSide> bridgeSideList) throws IOException, NonAuthoritativeException;

    /** 上传桥梁孔跨 */
    @RequestMapping(path = "/mobile/uploadSpanCombination", method = RequestMethod.POST)
    RequestResult uploadSpanCombination(List<TblBridgeSide> bridgeSideList) throws IOException, NonAuthoritativeException;

    @RequestMapping(path = "/mobile/uploadBridgeParts", method = RequestMethod.POST)
    List<TblBridgeParts> uploadBridgeParts(List<TblBridgeParts> bridgePartsList) throws IOException, NonAuthoritativeException;

    @RequestMapping(path = "/mobile/uploadBridgeDisease", method = RequestMethod.POST)
    List<TblBridgeDisease> uploadBridgeDisease(List<TblBridgeDisease> bridgeDiseaseList) throws IOException, NonAuthoritativeException;

    @RequestMapping(path = "/mobile/uploadDiseasePhoto", method = RequestMethod.UPLOAD)
    RequestResult uploadDiseasePhoto(@RequestFile File file,
                                     @RequestParam("id") String id,
                                     @RequestParam("bridgeId") String bridgeId,
                                     @RequestParam("taskId") String taskId);
}
