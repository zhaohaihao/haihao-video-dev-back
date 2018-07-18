package com.haihao.video.controller;

import com.haihao.video.enums.VideoStatusEnum;
import com.haihao.video.pojo.Bgm;
import com.haihao.video.pojo.Videos;
import com.haihao.video.service.BgmService;
import com.haihao.video.service.VideoService;
import com.haihao.video.utils.FetchVideoCover;
import com.haihao.video.utils.IMoocJSONResult;
import com.haihao.video.utils.MergeVideoMp3;
import com.haihao.video.utils.PagedResult;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

/**
 * Created by zhh on 2018/7/10 0010.
 */
@RestController
@Api(value = "视频相关业务的接口", tags = {"视频相关业务的 Controller"})
@RequestMapping("/video")
public class VideoController extends BasicController {

    @Autowired
    private BgmService bgmService;

    @Autowired
    private VideoService videoService;

    @ApiOperation(value = "上传视频", notes = "上传视频的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "bgmId", value = "背景音乐id", required = false, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoSeconds", value = "背景音乐播放长度", required = true, dataType = "double", paramType = "form"),
            @ApiImplicitParam(name = "videoWidth", value = "视频宽度", required = true, dataType = "Integer", paramType = "form"),
            @ApiImplicitParam(name = "videoHeight", value = "视频高度", required = true, dataType = "Integer", paramType = "form"),
            @ApiImplicitParam(name = "desc", value = "视频描述", required = false, dataType = "String", paramType = "form")
    })
    @PostMapping(value = "/uploadVideo", headers = "content-type=multipart/form-data")
    public IMoocJSONResult uploadVideo(String userId,
                                       String bgmId, double videoSeconds,
                                       Integer videoWidth, Integer videoHeight,
                                       String desc,
                                       @ApiParam(value = "短视频", required = true)
                                       MultipartFile file) throws Exception {

        // 1.判断用户名和密码必须不为空
        if (StringUtils.isEmpty(userId)) {
            return IMoocJSONResult.errorMsg("用户ID不能为空!");
        }

        // 文件保存的命名空间
        //String fileSpace = "C:/Users/Administrator/Desktop/haihao-video";
        // 保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/video";
        String coverPathDB = "/" + userId + "/cover";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        // 文件上传的最终保存路径
        String finalVideoPath = "";
        String finalCoverPath = "";
        try {
            if (file != null) {
                String filename = file.getOriginalFilename();
                String filenamePrefix = filename.split("\\.")[0];

                if (!StringUtils.isEmpty(filename)) {
                    finalVideoPath = FILE_SPACE + uploadPathDB + "/" + filename;
                    finalCoverPath = FILE_SPACE + coverPathDB;
                    // 设置数据库保存的路径
                    uploadPathDB += ("/" + filename);
                    coverPathDB += ("/" + filenamePrefix + ".jpg");

                    File outFile = new File(finalVideoPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    File outCoverFile = new File(finalCoverPath);
                    if (!outCoverFile.exists()) {
                        outCoverFile.mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }
            } else {
                return IMoocJSONResult.errorMsg("短视频上传异常!");
            }
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        // 判断bgmId是否为空, 若不为空, 就查询bgm的信息, 并且合并视频, 生成新的视频
        if (!StringUtils.isEmpty(bgmId)) {
            Bgm bgm = bgmService.queryBgmById(bgmId);
            String mp3InputPath = FILE_SPACE + bgm.getPath();

            MergeVideoMp3 tool = new MergeVideoMp3(FFMPEG_EXE);
            String videoInputPath = finalVideoPath;
            String videoOutputName = UUID.randomUUID().toString() + ".mp4";
            uploadPathDB = "/" + userId + "/video/" + videoOutputName;
            finalVideoPath = FILE_SPACE + uploadPathDB;
            tool.convertor(videoInputPath, mp3InputPath, videoSeconds, finalVideoPath);
        }
        System.out.println("uploadPathDB=" + uploadPathDB);
        System.out.println("finalVideoPath=" + finalVideoPath);

        // 对视频进行截图
        FetchVideoCover videoInfo = new FetchVideoCover(FFMPEG_EXE);
        videoInfo.getCover(finalVideoPath, FILE_SPACE + coverPathDB);

        // 保存视频信息到数据库
        Videos videos = new Videos();
        videos.setAudioId(bgmId);
        videos.setUserId(userId);
        videos.setVideoSeconds((float) videoSeconds);
        videos.setVideoHeight(videoHeight);
        videos.setVideoWidth(videoWidth);
        videos.setVideoDesc(desc);
        videos.setVideoPath(uploadPathDB);
        videos.setCoverPath(coverPathDB);
        videos.setStatus(VideoStatusEnum.SUCCESS.getValue());
        videos.setCreateTime(new Date());
        String videoId = videoService.saveVideo(videos);

        return IMoocJSONResult.ok(videoId);
    }

    @ApiOperation(value = "上传封面", notes = "上传封面的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoId", value = "视频主键id", required = true, dataType = "String", paramType = "form")
    })
    @PostMapping(value = "/uploadCover", headers = "content-type=multipart/form-data")
    public IMoocJSONResult uploadCover(String userId, String videoId,
                                       @ApiParam(value = "视频封面", required = true)
                                               MultipartFile file) throws Exception {

        // 1.判断用户名和密码必须不为空
        if (StringUtils.isEmpty(videoId) || StringUtils.isEmpty(userId)) {
            return IMoocJSONResult.errorMsg("视频主键ID和用户ID不能为空!");
        }

        // 文件保存的命名空间
        //String fileSpace = "C:/Users/Administrator/Desktop/haihao-video";
        // 保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/video";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        // 文件上传的最终保存路径
        String finalCoverPath = "";
        try {
            if (file != null) {
                String filename = file.getOriginalFilename();
                if (!StringUtils.isEmpty(filename)) {
                    finalCoverPath = FILE_SPACE + uploadPathDB + "/" + filename;
                    // 设置数据库保存的路径
                    uploadPathDB += ("/" + filename);

                    File outFile = new File(finalCoverPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }
            } else {
                return IMoocJSONResult.errorMsg("短视频上传异常!");
            }
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        videoService.updateVideo(videoId, uploadPathDB);

        return IMoocJSONResult.ok();
    }

    @PostMapping(value = "/showAll")
    public IMoocJSONResult showAll(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "5") Integer pageSize) {
        PagedResult result = videoService.getAllVideos(page, pageSize);
        return IMoocJSONResult.ok(result);
    }
}
