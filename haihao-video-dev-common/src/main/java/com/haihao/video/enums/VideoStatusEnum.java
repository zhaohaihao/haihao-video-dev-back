package com.haihao.video.enums;

/**
 * Created by zhh on 2018/7/13 0013.
 */
public enum VideoStatusEnum {

    SUCCESS(1),     // 发布成功
    FORBID(2);      // 禁止播放, 管理员操作

    private int value;

    private VideoStatusEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
