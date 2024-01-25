package com.github.xiaolyuh.utils;

import java.util.Collection;

/**
 * 集合工具类
 *
 * @author yuhao.wang3
 * @since 2020/3/31 15:47
 */
public class CollectionUtils {
    public static boolean isNotEmpty(Collection coll) {
        return !isEmpty(coll);
    }

    public static boolean isEmpty(Collection coll) {
        return coll == null || coll.isEmpty();
    }
}
