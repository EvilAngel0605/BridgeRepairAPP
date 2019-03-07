package net.jsrbc.utils;

import net.jsrbc.entity.TblBridgeMember;
import net.jsrbc.entity.TblBridgeParts;
import net.jsrbc.enumeration.MemberDescParam;
import net.jsrbc.enumeration.PartsType;
import net.jsrbc.enumeration.PositionType;
import net.jsrbc.enumeration.StakeSide;
import net.jsrbc.enumeration.UploadStatus;
import net.jsrbc.mapper.BridgeMemberMapper;
import net.jsrbc.mapper.BridgePartsMapper;
import net.jsrbc.pojo.BridgePartsForm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static net.jsrbc.utils.NumericUtils.zeroThen;
import static net.jsrbc.utils.StringUtils.isEmpty;
import static net.jsrbc.utils.StringUtils.toInt;

/**
 * Created by ZZZ on 2017-12-16.
 */
public final class MemberUtils {

    @FunctionalInterface
    public interface Cleaner {
        void clean();
    }

    @FunctionalInterface
    public interface Consumer {
        void apply(List<TblBridgeParts> bridgePartsList, List<TblBridgeMember> bridgeMemberList);
    }

    /** 解析孔跨字符串 */
    public static HashSet<Integer> parseSiteRange(String siteRange) {
        if (isEmpty(siteRange)) return null;
        if (!siteRange.matches("(\\d+(,\\d+|(-\\d+))?,?)+")) return null;
        //将输入的孔跨解析出来
        HashSet<Integer> siteNoSet = new HashSet<>();
        for (String siteNoStr : siteRange.split(",")) {
            //遍历的前提为字符串不为空
            if (isEmpty(siteNoStr)) continue;
            //解析孔跨字符串
            if (siteNoStr.contains("-")) {
                for (int i=toInt(siteNoStr.split("-")[0]); i<=toInt(siteNoStr.split("-")[1]); i++) {
                    siteNoSet.add(i);
                }
            }else {
                siteNoSet.add(toInt(siteNoStr));
            }
        }
        return siteNoSet;
    }

    /** 是否有描述参数 */
    public static boolean containDescParam(TblBridgeParts bridgeParts, MemberDescParam descParam) {
        String descStr = "{" + descParam.getValue() + "}";
        return bridgeParts.getMemberDesc().contains(descStr);
    }

    /** 根据部件表单，批量添加部件及构件 */
    public static void addBridgeParts(BridgePartsForm bridgePartsForm,
                                      Stream<Integer> siteStream,
                                      Cleaner cleaner,
                                      Consumer consumer) {
        //如果起始孔跨大于结束孔跨，判为不合法，退出
        if (bridgePartsForm.getStartSiteNo() > bridgePartsForm.getEndSiteNo()) return;
        //清扫工作
        cleaner.clean();
        //批量添加部构件，每孔批量保存
        siteStream.forEach(i->{
            List<TblBridgeParts> bridgePartsList = new ArrayList<>();
            List<TblBridgeMember> bridgeMemberList = new ArrayList<>();
            for (TblBridgeParts bridgeParts : bridgePartsForm.getPartsList()) {
                //无构件的话则跳过这个部件，不添加
                if (bridgeParts.getVerticalCount() == 0 && bridgeParts.getHorizontalCount() == 0 && isEmpty(bridgeParts.getSpecialSection()) && bridgeParts.getStakeSide() == StakeSide.NONE.getCode()) continue;
                //批量添加部件
                bridgePartsList.add(new TblBridgeParts.Builder()
                        .id(StringUtils.createId())
                        .bridgeId(bridgePartsForm.getBridgeId())
                        .sideTypeId(bridgePartsForm.getSideTypeId())
                        .siteNo(zeroThen(i, 1))
                        .structureTypeId(bridgeParts.getStructureTypeId())
                        .materialTypeId(bridgeParts.getMaterialTypeId())
                        .partsTypeId(bridgeParts.getPartsTypeId())
                        .memberTypeId(bridgeParts.getMemberTypeId())
                        .stakeSide(i == 0 ? StakeSide.LESS_SIDE.getCode() : bridgeParts.getStakeSide())
                        .specialSection(bridgeParts.getSpecialSection())
                        .horizontalCount(bridgeParts.getHorizontalCount())
                        .verticalCount(bridgeParts.getVerticalCount())
                        .upload(UploadStatus.NEED_UPLOAD.getCode())
                        .build()
                );
                //批量添加构件
                bridgeParts.setBridgeId(bridgePartsForm.getBridgeId());
                bridgeParts.setSideTypeId(bridgePartsForm.getSideTypeId());
                bridgeParts.setSiteNo(i);
                bridgeMemberList.addAll(generateBridgeMembers(bridgeParts));
            }
            //保存至数据库
            consumer.apply(bridgePartsList, bridgeMemberList);
        });
    }

    /** 根据传入的部件批量生成构件 */
    public static List<TblBridgeMember> generateBridgeMembers(TblBridgeParts bridgeParts) {
        List<TblBridgeMember> bridgeMemberList = new ArrayList<>();
        //先弄一个构建器，方便后面添加
        TblBridgeMember.Builder bridgeMemberBuilder = new TblBridgeMember.Builder()
                .bridgeId(bridgeParts.getBridgeId())
                .sideTypeId(bridgeParts.getSideTypeId())
                .siteNo(zeroThen(bridgeParts.getSiteNo(), 1))
                .partsTypeId(bridgeParts.getPartsTypeId())
                .memberTypeId(bridgeParts.getMemberTypeId());
        //开始遍历添加
        if (bridgeParts.getVerticalCount() != 0 || bridgeParts.getHorizontalCount() != 0) {
            for (int v = 1; v <= zeroThen(bridgeParts.getVerticalCount(), 1); v++) {
                for (int h = 1; h <= zeroThen(bridgeParts.getHorizontalCount(), 1); h++) {
                    bridgeMemberBuilder.horizontalNo(bridgeParts.getHorizontalCount() == 0?0:h).verticalNo(bridgeParts.getVerticalCount() == 0?0:v);
                    //如果选择的是大小桩号侧对称，则小桩号侧和大桩号侧分别生成一个
                    if (bridgeParts.getStakeSide() == StakeSide.BOTH_SIDE.getCode()) {
                        bridgeMemberList.add(bridgeMemberBuilder.id(StringUtils.createId()).stakeSide(StakeSide.LESS_SIDE.getCode()).build());
                        bridgeMemberList.add(bridgeMemberBuilder.id(StringUtils.createId()).stakeSide(StakeSide.LARGER_SIDE.getCode()).build());
                    } else {
                        //孔号为0，自动补上小桩号侧
                        if (bridgeParts.getSiteNo() == 0) {
                            bridgeMemberList.add(bridgeMemberBuilder.id(StringUtils.createId()).stakeSide(StakeSide.LESS_SIDE.getCode()).build());
                        }else {
                            bridgeMemberList.add(bridgeMemberBuilder.id(StringUtils.createId()).stakeSide(bridgeParts.getStakeSide()).build());
                        }
                    }
                }
            }
        }
        //如果有特殊节段的话，继续添加
        if (!isEmpty(bridgeParts.getSpecialSection())) {
            for (String specialSection : bridgeParts.getSpecialSection().split(",")) {
                for (int h = 1; h <= zeroThen(bridgeParts.getHorizontalCount(), 1); h++) {
                    bridgeMemberBuilder.verticalNo(0).horizontalNo(bridgeParts.getHorizontalCount() == 0?0:h).specialSection(specialSection);
                    if (bridgeParts.getStakeSide() == StakeSide.BOTH_SIDE.getCode() && !bridgeParts.getPartsTypeName().equals(PartsType.SUPER_BEARING.getValue())) {
                        bridgeMemberList.add(bridgeMemberBuilder.id(StringUtils.createId()).stakeSide(StakeSide.LESS_SIDE.getCode()).build());
                        bridgeMemberList.add(bridgeMemberBuilder.id(StringUtils.createId()).stakeSide(StakeSide.LARGER_SIDE.getCode()).build());
                    }else {
                        if (bridgeParts.getSiteNo() == 0) {
                            bridgeMemberList.add(bridgeMemberBuilder.id(StringUtils.createId()).stakeSide(StakeSide.LESS_SIDE.getCode()).build());
                        }else {
                            bridgeMemberList.add(bridgeMemberBuilder.id(StringUtils.createId()).stakeSide(bridgeParts.getStakeSide()).build());
                        }
                    }
                }
            }
        }
        //考虑只有大小桩号侧的情况
        if (bridgeParts.getVerticalCount() == 0 && bridgeParts.getHorizontalCount() == 0 && StringUtils.isEmpty(bridgeParts.getSpecialSection()) && bridgeParts.getStakeSide() != StakeSide.NONE.getCode()) {
            if (bridgeParts.getStakeSide() == StakeSide.BOTH_SIDE.getCode()) {
                bridgeMemberList.add(bridgeMemberBuilder.id(StringUtils.createId()).stakeSide(StakeSide.LESS_SIDE.getCode()).build());
                bridgeMemberList.add(bridgeMemberBuilder.id(StringUtils.createId()).stakeSide(StakeSide.LARGER_SIDE.getCode()).build());
            }else {
                bridgeMemberList.add(bridgeMemberBuilder.id(StringUtils.createId()).stakeSide(bridgeParts.getStakeSide()).build());
            }
        }
        return bridgeMemberList;
    }

    /** 封闭构造函数 */
    private MemberUtils() {}
}
