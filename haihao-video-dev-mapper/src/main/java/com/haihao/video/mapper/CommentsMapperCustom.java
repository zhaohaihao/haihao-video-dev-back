package com.haihao.video.mapper;


import com.haihao.video.pojo.Comments;
import com.haihao.video.pojo.vo.CommentsVO;
import com.haihao.video.utils.MyMapper;

import java.util.List;

public interface CommentsMapperCustom extends MyMapper<Comments> {
	
	public List<CommentsVO> queryComments(String videoId);
}