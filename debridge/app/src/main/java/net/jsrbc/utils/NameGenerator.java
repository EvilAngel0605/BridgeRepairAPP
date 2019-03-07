package net.jsrbc.utils;

import android.util.Log;

import net.jsrbc.entity.TblBridgeDisease;
import net.jsrbc.entity.TblBridgeMember;
import net.jsrbc.enumeration.DimensionUnit;
import net.jsrbc.enumeration.DiseasePositionParam;
import net.jsrbc.enumeration.InspectionDirection;
import net.jsrbc.enumeration.StakeSide;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.jsrbc.enumeration.DiseasePositionParam.*;
import static net.jsrbc.enumeration.MemberDescParam.*;

public final class NameGenerator {

    private final static String EMPTY_PARAM_STR = "@";

    /**
     * 生成构件名称
     * @param bridgeDisease 病害对象
     * @param direction     构件横向编号方向
     * @return              生成的构件名称
     */
    public static String generateMemberName(TblBridgeDisease bridgeDisease, InspectionDirection direction) {
        TblBridgeMember bridgeMember = TblBridgeMember.of(bridgeDisease);
        return generateMemberName(bridgeMember, direction);
    }

    /**
     * 生成构件名称
     * @param bridgeMember  构件对象
     * @param direction     构件横向编号方向
     * @return              生成的构件名称
     */
    public static String generateMemberName(TblBridgeMember bridgeMember, InspectionDirection direction) {
        //检查构件名称
        if (StringUtils.isEmpty(bridgeMember.getMemberDesc())) {
            if (StringUtils.isEmpty(bridgeMember.getPartsTypeId()) && StringUtils.isEmpty(bridgeMember.getMemberTypeId())) return "整跨";
            else return "未知构件";
        }
        //定义一个解析结果
        String result = bridgeMember.getMemberDesc();
        //孔号
        result = insertMemberDescParam(result, SITE_NO.getValue(), bridgeMember.getSiteNo());
        //联号
        result = insertMemberDescParam(result, JOINT_NO.getValue(), bridgeMember.getJointNo());
        //墩台号
        int pierNo = bridgeMember.getSiteNo();
        if (bridgeMember.getStakeSide() == StakeSide.LESS_SIDE.getCode()) pierNo = bridgeMember.getSiteNo() - 1;
        if (pierNo == 0 || pierNo == bridgeMember.getMaxSiteNo()) result = insertMemberDescParam(result, PIER_NO.getValue(), pierNo+"#台");
        else result = insertMemberDescParam(result, PIER_NO.getValue(), pierNo+"#墩");
        //纵桥向编号，如果数量只有一个的话就不去编号了
        if (bridgeMember.getVerticalCount() <= 1) result = insertMemberDescParam(result, VERTICAL_NO.getValue(), 0);
        else result = insertMemberDescParam(result, VERTICAL_NO.getValue(), bridgeMember.getVerticalNo());
        //横桥向编号，如果数量只有一个的话就不去编号了
        if (bridgeMember.getHorizontalCount() <= 1) {
            result = insertMemberDescParam(result, HORIZONTAL_NO.getValue(), 0);
        } else {
            result = insertMemberDescParam(result, HORIZONTAL_NO.getValue(),
                    direction == InspectionDirection.LEFT_TO_RIGHT ?
                            bridgeMember.getHorizontalNo() :
                            bridgeMember.getHorizontalCount() - bridgeMember.getHorizontalNo() + 1);
        }
        //大小桩号侧
        if (bridgeMember.getStakeSide() == StakeSide.LESS_SIDE.getCode()) {
            result = insertMemberDescParam(result, STAKE_SIDE.getValue(), "小桩号侧");
        } else if (bridgeMember.getStakeSide() == StakeSide.LARGER_SIDE.getCode()) {
            result = insertMemberDescParam(result, STAKE_SIDE.getValue(), "大桩号侧");
        } else {
            result = insertMemberDescParam(result, STAKE_SIDE.getValue(), null);
        }
        //特殊节段
        result = insertMemberDescParam(result, SPECIAL_SECTION.getValue(), bridgeMember.getSpecialSection());
        //构件名称
        result = insertMemberDescParam(result, MEMBER_TYPE_NAME.getValue(), bridgeMember.getMemberTypeName());
        //返回注入的结果
        return clearEmptyDesc(result);
    }

    /**
     * 生成病害描述
     * @param bridgeDisease  病害对象
     * @return               病害描述
     */
    public static String generateDiseaseName(TblBridgeDisease bridgeDisease) {
        //病害描述为空，则返回备注
        if (StringUtils.isEmpty(bridgeDisease.getDiseaseDesc())) return StringUtils.isEmpty(bridgeDisease.getNotes())?"":bridgeDisease.getNotes();
        //注入病害的描述参数
        Class<?> clazz = TblBridgeDisease.class;
        Matcher m = Pattern.compile("\\{(.+?)\\}").matcher(bridgeDisease.getDiseaseDesc());
        //空描述字段
        final StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String[] prop = m.group(1).split(",");
            try {
                Field field = clazz.getDeclaredField(prop[0]);
                field.setAccessible(true);
                Object value = field.get(bridgeDisease);
                if (value instanceof Integer) {
                    if ((int) value == 0) {
                        m.appendReplacement(sb, EMPTY_PARAM_STR);
                    } else {
                        m.appendReplacement(sb, String.valueOf(value));
                    }
                } else if (value instanceof Double) {
                    if (DiseasePositionParam.contains(field.getName()) && (double)value > 0) {
                        StringBuilder position = new StringBuilder();
                        String minPierNo = getPierNo(bridgeDisease.getSiteNo()-1, bridgeDisease.getMaxSiteNo());
                        position.append(minPierNo).append(value);
                        m.appendReplacement(sb, position.toString());
                    } else if ((double)value == 0D) {
                        m.appendReplacement(sb, EMPTY_PARAM_STR);
                    } else if (prop.length == 2) {
                        m.appendReplacement(sb, String.valueOf(convertMMToTargetUnit((double)value, DimensionUnit.of(prop[1]))));
                    } else {
                        m.appendReplacement(sb, String.valueOf(value));
                    }
                } else {
                    if (value == null) m.appendReplacement(sb, EMPTY_PARAM_STR);
                    else m.appendReplacement(sb, String.valueOf(value));
                }
            }catch (NoSuchFieldException | IllegalAccessException e) {
                Log.e(NameGenerator.class.getName(), "disease param is not valid", e);
                m.appendReplacement(sb, EMPTY_PARAM_STR);
            }
        }
        m.appendTail(sb);
        //返回结果
        String result = sb.toString();
        //最后做一点清理工作，去除'均'、'各'等'
        if (bridgeDisease.getCount() <= 1) result = result.replaceAll("[均各]", "");
        //清理空白域及多余的[]符号
        result = clearEmptyDesc(result);
        //检查是否有备注，若有则加上
        if (!StringUtils.isEmpty(bridgeDisease.getNotes())) result += String.format("(%s)", bridgeDisease.getNotes());
        return result;
    }

    /** 获取墩台名称 */
    private static String getPierNo(int pierNo, int maxSiteNo) {
        if (pierNo == 0) return "0#台";
        if (pierNo == maxSiteNo) return maxSiteNo+"#台";
        return pierNo+"#墩";
    }

    /** 注入构件描述参数，字符串 */
    private static String insertMemberDescParam(String memberDesc, String prop, String value) {
        if (StringUtils.isEmpty(value)) return memberDesc.replaceAll("\\{"+prop+"\\}", EMPTY_PARAM_STR);
        else return memberDesc.replaceAll("\\{"+prop+"\\}", String.valueOf(value));
    }

    /** 注入构件描述参数，整数 */
    private static String insertMemberDescParam(String memberDesc, String prop, int value) {
        //变截面连续箱梁编号是从0#块开始
        if(prop.equals(VERTICAL_NO.getValue()) && memberDesc.contains("#块")) {
            if (value == 0) return memberDesc.replaceAll("\\{"+prop+"\\}", EMPTY_PARAM_STR);
            else return memberDesc.replaceAll("\\{"+prop+"\\}", String.valueOf(value-1));
        }else {
            if (value == 0) return memberDesc.replaceAll("\\{"+prop+"\\}", EMPTY_PARAM_STR);
            else return memberDesc.replaceAll("\\{"+prop+"\\}", String.valueOf(value));
        }
    }

    /** 清理构件描述，去除参数为空的作用域 */
    private static String clearEmptyDesc(String desc) {
        return desc.replaceAll("\\[[^\\[\\]]*?" + EMPTY_PARAM_STR + "[^\\[\\]]*?\\]", "")
                .replaceAll("[\\[\\]]", "")
                .replaceAll(EMPTY_PARAM_STR, "");
    }

    /**
     * 将毫米值转化为指定单位值
     * @param value       毫米值
     * @param targetUnit  目标单位
     * @return            转化后的值
     */
    private static Double convertMMToTargetUnit(Double value, DimensionUnit targetUnit) {
        if (value == null) return null;
        if (targetUnit == null) return value;
        switch (targetUnit) {
            case M:
                return NumericUtils.getPrecisionValue(value / 1000D, 2);
            case CM:
                return NumericUtils.getPrecisionValue(value / 10D, 2);
            default:
                return NumericUtils.getPrecisionValue(value, 2);
        }
    }

    /** 封闭构造函数 */
    private NameGenerator() {}
}
