package com.haihao.video.mapper;


import com.haihao.video.pojo.SearchRecords;
import com.haihao.video.utils.MyMapper;

import java.util.List;

public interface SearchRecordsMapper extends MyMapper<SearchRecords> {
	
	public List<String> getHotwords();
}