package com.clubank.meeting.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 计算部门级别
 */
public class DepartmentLevelUtil {

    public final static String SEPERATOR = "-";

    public final static String ROOT = "0";

    /**
     * 计算部门级别
     *  0
     *  0-1
     *  0-1-1
     * @param parentLevel
     * @param parentId
     * @return
     */
    public static String calculateLevel(String parentLevel, long parentId) {
        if (StringUtils.isBlank(parentLevel)) {
            return ROOT;
        }
        return StringUtils.join(parentLevel, SEPERATOR, parentId);
    }
}
