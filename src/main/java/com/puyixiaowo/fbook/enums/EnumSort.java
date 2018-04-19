package com.puyixiaowo.fbook.enums;
/**
 *
 * @author Moses.wei
 * @date 2018-03-14 22:46:13
 *
 */
public enum EnumSort {
    SORT_POSITIVE(1, "正序排列"),
    SORT_REVERSE(0, "倒序排列");

    EnumSort(int sort, String description) {
        this.sort = sort;
        this.description = description;
    }

    public int sort;
    public String description;

}
