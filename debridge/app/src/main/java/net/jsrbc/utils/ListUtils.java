package net.jsrbc.utils;

import java.util.Comparator;
import java.util.List;

/**
 * Created by ZZZ on 2017-12-12.
 */
public final class ListUtils {

    /** 简单的索引查找，没找到则返回0，满足Spinner要求 */
    public static <T> int indexOf(List<T> list, T target, Comparator<T> comparator) {
        if (list == null) return -1;
        for (int i=0; i<list.size(); i++) {
            if (comparator.compare(list.get(i), target) == 0) return i;
        }
        return 0;
    }

    /** 判断集合是否为空 */
    public static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    /** 封闭构造函数 */
    private ListUtils() {}
}
