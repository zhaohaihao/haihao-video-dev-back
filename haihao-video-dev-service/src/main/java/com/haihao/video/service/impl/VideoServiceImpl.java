package com.haihao.video.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.haihao.video.mapper.SearchRecordsMapper;
import com.haihao.video.mapper.VideosMapper;
import com.haihao.video.mapper.VideosMapperCustom;
import com.haihao.video.pojo.SearchRecords;
import com.haihao.video.pojo.Videos;
import com.haihao.video.pojo.vo.VideosVO;
import com.haihao.video.service.VideoService;
import com.haihao.video.utils.PagedResult;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by zhh on 2018/7/11 0011.
 */
@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideosMapper videosMapper;

    @Autowired
    private VideosMapperCustom videosMapperCustom;

    @Autowired
    private SearchRecordsMapper searchRecordsMapper;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String saveVideo(Videos videos) {
        String videoId = sid.nextShort();
        videos.setId(videoId);
        videosMapper.insertSelective(videos);
        return videoId;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateVideo(String videoId, String coverPath) {
        Videos videos = new Videos();
        videos.setId(videoId);
        videos.setCoverPath(coverPath);
        videosMapper.updateByPrimaryKeySelective(videos);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public PagedResult getAllVideos(Videos videos, Integer isSaveRecord, Integer page, Integer pageSize) {

        // 保存热搜的词语
        String desc = videos.getVideoDesc();
        if (isSaveRecord != null && isSaveRecord == 1) {
            SearchRecords searchRecords = new SearchRecords();
            searchRecords.setId(sid.nextShort());
            searchRecords.setContent(desc);
            searchRecordsMapper.insert(searchRecords);
        }

        PageHelper.startPage(page, pageSize);
        List<VideosVO> list = videosMapperCustom.queryAllVideos(desc);

        PageInfo<VideosVO> pageList = new PageInfo<>(list);
        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<String> getHotWords() {
        return searchRecordsMapper.getHotwords();
    }
}
