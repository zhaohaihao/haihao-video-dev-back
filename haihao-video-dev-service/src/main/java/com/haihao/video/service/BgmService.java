package com.haihao.video.service;

import com.haihao.video.pojo.Bgm;

import java.util.List;

/**
 * Created by zhh on 2018/7/11 0011.
 */
public interface BgmService {

    /**
     * 查询背景音乐列表
     * @return
     */
    List<Bgm> queryBgmList();

    /**
     * 根据id查询bgm信息
     * @param bgmId
     * @return
     */
    Bgm queryBgmById(String bgmId);
}
