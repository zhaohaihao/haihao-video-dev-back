package com.haihao.video.service;

import com.haihao.video.pojo.Videos;
import com.haihao.video.utils.PagedResult;

import java.util.List;

/**
 * Created by zhh on 2018/7/11 0011.
 */
public interface VideoService {

    /**
     * 保存视频
     * @param videos
     */
    String saveVideo(Videos videos);

    /**
     * 修改视频的封面
     * @param videoId
     * @param coverPath
     * @return
     */
    void updateVideo(String videoId, String coverPath);

    /**
     * 分页查询视频列表
     * @param page
     * @param pageSize
     * @return
     */
    PagedResult getAllVideos(Videos videos, Integer isSaveRecord, Integer page, Integer pageSize);

    /**
     * 获取热搜词列表
     * @return
     */
    List<String> getHotWords();

    /**
     * 用户喜欢/点赞视频
     * @param userId
     * @param videoId
     * @param videoCreaterId
     */
    void userLikeVideo(String userId, String videoId, String videoCreaterId);

    /**
     * 用户不喜欢/取消点赞视频
     * @param userId
     * @param videoId
     * @param videoCreaterId
     */
    void userUnLikeVideo(String userId, String videoId, String videoCreaterId);
}
