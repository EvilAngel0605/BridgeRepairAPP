package net.jsrbc.client;

import net.jsrbc.entity.TblBridge;
import net.jsrbc.entity.TblBridgeParts;
import net.jsrbc.entity.TblBridgeSide;
import net.jsrbc.entity.TblBridgeSite;
import net.jsrbc.entity.TblSysMobileVersion;
import net.jsrbc.entity.TblTask;
import net.jsrbc.frame.annotation.client.RequestMapping;
import net.jsrbc.frame.annotation.client.RequestParam;
import net.jsrbc.frame.enumeration.RequestMethod;
import net.jsrbc.frame.exception.NonAuthoritativeException;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by ZZZ on 2017-12-03.
 */
public interface DownloadClient {

    @RequestMapping(path = "/mobile/getTaskList", method = RequestMethod.GET)
    List<TblTask> getTaskList() throws IOException, NonAuthoritativeException;

    @RequestMapping(path = "/mobile/getTaskBridgeList", method = RequestMethod.GET)
    List<TblBridge> getTaskBridgeList(@RequestParam("taskId") String taskId) throws IOException, NonAuthoritativeException;

    @RequestMapping(path = "/mobile/getTaskBridgeSideList", method = RequestMethod.GET)
    List<TblBridgeSide> getTaskBridgeSideList(@RequestParam("taskId") String taskId)  throws IOException, NonAuthoritativeException;

    @RequestMapping(path = "/mobile/getTaskBridgeSiteList", method = RequestMethod.GET)
    List<TblBridgeSite> getTaskBridgeSiteList(@RequestParam("taskId") String taskId) throws IOException, NonAuthoritativeException;

    @RequestMapping(path = "/mobile/getTaskBridgePartsList", method = RequestMethod.GET)
    List<TblBridgeParts> getTaskBridgePartsList(@RequestParam("taskId") String taskId) throws IOException, NonAuthoritativeException;

    @RequestMapping(path = "/mobile/getLatestVersion", method = RequestMethod.GET)
    TblSysMobileVersion getLatestVersion() throws IOException, NonAuthoritativeException;

    @RequestMapping(path = "/mobile/downloadInstallFile", method = RequestMethod.DOWNLOAD)
    File downloadInstallFile(@RequestParam("id") String id) throws IOException, NonAuthoritativeException;
}
