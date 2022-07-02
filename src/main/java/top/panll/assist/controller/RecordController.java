package top.panll.assist.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.panll.assist.controller.bean.WVPResult;
import top.panll.assist.dto.MergeOrCutTaskInfo;
import top.panll.assist.dto.SignInfo;
import top.panll.assist.dto.SpaceInfo;
import top.panll.assist.service.VideoFileService;
import top.panll.assist.utils.CustomPage;
import top.panll.assist.utils.PageInfo;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
@Api(tags = "录像管理")
@CrossOrigin
@RestController
@RequestMapping("/api/record")
public class RecordController {

    private final static Logger logger = LoggerFactory.getLogger(RecordController.class);

    @Autowired
    private VideoFileService videoFileService;

    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * 获取app+stream列表
     * @return
     */
    @ApiOperation("分页获取app+stream的列表")
    @GetMapping(value = "/list")
    @ResponseBody
    public List<Map<String, String>> getList(){
        return videoFileService.getList();
    }

    /**
     * 分页获取app列表
     * @return
     */
    @ApiOperation("分页获取app列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="page", value = "当前页", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name="count", value = "每页查询数量", required = true, dataTypeClass = Integer.class),
    })
    @GetMapping(value = "/app/list")
    @ResponseBody
    public WVPResult<PageInfo<String>> getAppList(@RequestParam int page,
                                                  @RequestParam int count){
        WVPResult<PageInfo<String>> result = new WVPResult<>();
        List<String> resultData = new ArrayList<>();
        List<File> appList = videoFileService.getAppList(true);
        if (appList.size() > 0) {
            for (File file : appList) {
                resultData.add(file.getName());
            }
        }
        result.setCode(0);
        result.setMsg("success");

        PageInfo<String> stringPageInfo = new PageInfo<>(resultData);
        stringPageInfo.startPage(page, count);
        result.setData(stringPageInfo);
        return result;
    }

    /**
     * 分页stream列表
     * @return
     */
    @ApiOperation("分页stream列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="page", value = "当前页", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name="count", value = "每页查询数量", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name="app", value = "应用名", required = true, dataTypeClass = String.class),
    })
    @GetMapping(value = "/stream/list")
    @ResponseBody
    public WVPResult<PageInfo<String>> getStreamList(@RequestParam int page,
                                                     @RequestParam int count,
                                                     @RequestParam String app ){
        WVPResult<PageInfo<String>> result = new WVPResult<>();
        List<String> resultData = new ArrayList<>();
        if (app == null) {
            result.setCode(400);
            result.setMsg("app不能为空");
            return result;
        }
        List<File> streamList = videoFileService.getStreamList(app, true);
        if (streamList != null) {
            for (File file : streamList) {
                resultData.add(file.getName());
            }
        }
        result.setCode(0);
        result.setMsg("success");
        PageInfo<String> stringPageInfo = new PageInfo<>(resultData);
        stringPageInfo.startPage(page, count);
        result.setData(stringPageInfo);
        return result;
    }

    /**
     * 获取日期文件夹列表
     * @return
     */
    @ApiOperation("获取日期文件夹列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="year", value = "年", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name="month", value = "月", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name="app", value = "应用名", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name="stream", value = "流ID", required = true, dataTypeClass = String.class),
    })
    @GetMapping(value = "/date/list")
    @ResponseBody
    public WVPResult<List<String>> getDateList( @RequestParam(required = false) Integer year,
                                                @RequestParam(required = false) Integer month,
                                                 @RequestParam String app,
                                                 @RequestParam String stream ){
        WVPResult<List<String>> result = new WVPResult<>();
        List<String> resultData = new ArrayList<>();
        if (app == null) {
            result.setCode(400);
            result.setMsg("app不能为空");
            return result;
        };
        if (stream == null) {
            result.setCode(400);
            result.setMsg("stream不能为空");
            return result;
        }
        List<File> dateList = videoFileService.getDateList(app, stream, year, month, true);
        for (File file : dateList) {
            resultData.add(file.getName());
        }
        result.setCode(0);
        result.setMsg("success");
        result.setData(resultData);
        return result;
    }

    /**
     * 获取视频文件列表
     * @return
     */
    @ApiOperation("获取日期文件夹列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="page", value = "当前页", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name="count", value = "每页查询数量", required = true, dataTypeClass = Integer.class),
            @ApiImplicitParam(name="app", value = "应用名", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name="stream", value = "流ID", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name="startTime", value = "开始时间(yyyy-MM-dd HH:mm:ss)", required = false, dataTypeClass = String.class),
            @ApiImplicitParam(name="endTime", value = "结束时间(yyyy-MM-dd HH:mm:ss)", required = false, dataTypeClass = String.class),
    })
    @GetMapping(value = "/file/list")
    @ResponseBody
    public WVPResult<PageInfo<JSONObject>> getRecordList(@RequestParam int page,
                                                     @RequestParam int count,
                                                     @RequestParam String app,
                                                     @RequestParam String stream,
                                                     @RequestParam(required = false) String startTime,
                                                     @RequestParam(required = false) String endTime
    ){

        WVPResult<PageInfo<JSONObject>> result = new WVPResult<>();
        // 开始时间与结束时间可不传或只传其一
        //List<String> recordList = new ArrayList<>(); 之前的代码，这里我们返回JSON 数组
        List<JSONObject> recordList = new ArrayList<>();
        try {
            Date startTimeDate  = null;
            Date endTimeDate  = null;
            if (startTime != null ) {
                startTimeDate = formatter.parse(startTime);
            }
            if (endTime != null ) {
                endTimeDate = formatter.parse(endTime);
            }

            List<File> filesInTime = videoFileService.getFilesInTime(app, stream, startTimeDate, endTimeDate);

            /*之前的代码，改成我们想要的结果
            if (filesInTime != null && filesInTime.size() > 0) {
                for (File file : filesInTime) {
                    recordList.add(file.getName());
                }
            }*/
            if (filesInTime != null && filesInTime.size() > 0) {
                if (filesInTime.size() > 10)  filesInTime = filesInTime.subList(0, 10);
                filesInTime.forEach(file -> {
                    String[] arr = file.getName().replaceAll("-", ":").split("_");
                    String startTimeStr = arr[0];
                    String endTimeStr = arr[1];
                    String[] tmepArr = arr[2].split("[.]");
                    String duration = tmepArr[0];
                    String videoFormat = tmepArr[1];
                    JSONObject tempJson = new JSONObject();
                    tempJson.put("startTime", startTimeStr);
                    tempJson.put("endTime", endTimeStr);
                    tempJson.put("videoFormat", videoFormat);
                    tempJson.put("originalName", file.getName());
                    tempJson.put("duration", DateUtil.secondToTime(Convert.toInt(duration) / 1000));
                    recordList.add(tempJson);
                });
            }
            result.setCode(0);
            result.setMsg("success");
            PageInfo<JSONObject> stringPageInfo = new PageInfo<>(recordList);
            stringPageInfo.startPage(page, count);
            result.setData(stringPageInfo);
        } catch (ParseException e) {
            logger.error("错误的开始时间[{}]或结束时间[{}]", startTime, endTime);
            result.setCode(400);
            result.setMsg("错误的开始时间或结束时间");
        }
        return result;
    }


    /**
     * 添加视频裁剪合并任务
     */
    @ApiOperation("添加视频裁剪合并任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name="app", value = "应用名", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name="stream", value = "流ID", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name="startTime", value = "开始时间(yyyy-MM-dd HH:mm:ss)", required = false, dataTypeClass = String.class),
            @ApiImplicitParam(name="endTime", value = "结束时间(yyyy-MM-dd HH:mm:ss)", required = false, dataTypeClass = String.class),
            @ApiImplicitParam(name="remoteHost", value = "服务的IP：端口（用于直接返回完整播放地址以及下载地址）", required = false, dataTypeClass = String.class),
    })
    @GetMapping(value = "/file/download/task/add")
    @ResponseBody
    public WVPResult<String> addTaskForDownload(@RequestParam String app,
                                                @RequestParam String stream,
                                                @RequestParam(required = false) String startTime,
                                                @RequestParam(required = false) String endTime,
                                                @RequestParam(required = false) String remoteHost
    ){
        WVPResult<String> result = new WVPResult<>();
        Date startTimeDate  = null;
        Date endTimeDate  = null;
        try {
            if (startTime != null ) {
                startTimeDate = formatter.parse(startTime);
            }
            if (endTime != null ) {
                endTimeDate = formatter.parse(endTime);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String id = videoFileService.mergeOrCut(app, stream, startTimeDate, endTimeDate, remoteHost);
        result.setCode(0);
        result.setMsg(id!= null?"success":"error： 可能未找到视频文件");
        result.setData(id);
        return result;
    }

    /**
     * 查询视频裁剪合并任务列表
     */
    @ApiOperation("查询视频裁剪合并任务列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="app", value = "应用名", required = false, dataTypeClass = String.class),
            @ApiImplicitParam(name="stream", value = "流ID", required = false, dataTypeClass = String.class),
            @ApiImplicitParam(name="taskId", value = "任务ID", required = false, dataTypeClass = String.class),
            @ApiImplicitParam(name="isEnd", value = "是否结束", required = false, dataTypeClass = Boolean.class),
    })
    @GetMapping(value = "/file/download/task/list")
    @ResponseBody
    public WVPResult<List<MergeOrCutTaskInfo>> getTaskListForDownload(
            @RequestParam(required = false) String app,
            @RequestParam(required = false) String stream,
            @RequestParam(required = false) String taskId,
            @RequestParam(required = false) Boolean isEnd){
        List<MergeOrCutTaskInfo> taskList = videoFileService.getTaskListForDownload(isEnd, app, stream, taskId);
        WVPResult<List<MergeOrCutTaskInfo>> result = new WVPResult<>();
        result.setCode(0);
        result.setMsg(taskList !=  null?"success":"error");
        result.setData(taskList);
        return result;
    }

    /**
     * 收藏录像（被收藏的录像不会被清理任务清理）
     */
    @ApiOperation("收藏录像（被收藏的录像不会被清理任务清理）")
    @ApiImplicitParams({
            @ApiImplicitParam(name="type", value = "类型", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name="app", value = "应用名", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name="stream", value = "流ID", required = true, dataTypeClass = String.class),
    })
    @GetMapping(value = "/file/collection/add")
    @ResponseBody
    public WVPResult<String> collection(
            @RequestParam(required = true) String type,
            @RequestParam(required = true) String app,
            @RequestParam(required = true) String stream){

        boolean collectionResult = videoFileService.collection(app, stream, type);
        WVPResult<String> result = new WVPResult<>();
        result.setCode(0);
        result.setMsg(collectionResult ?"success":"error");
        return result;
    }

    /**
     * 移除收藏录像
     */
    @ApiOperation("移除收藏录像")
    @ApiImplicitParams({
            @ApiImplicitParam(name="type", value = "类型", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name="app", value = "应用名", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name="stream", value = "流ID", required = true, dataTypeClass = String.class),
    })
    @GetMapping(value = "/file/collection/remove")
    @ResponseBody
    public WVPResult<String> removeCollection(
            @RequestParam(required = true) String type,
            @RequestParam(required = true) String app,
            @RequestParam(required = true) String stream){

        boolean collectionResult = videoFileService.removeCollection(app, stream, type);
        WVPResult<String> result = new WVPResult<>();
        result.setCode(0);
        result.setMsg(collectionResult ?"success":"error");
        return result;
    }

    /**
     * 收藏录像列表
     */
    @ApiOperation("收藏录像列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="type", value = "类型", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name="app", value = "应用名", required = false, dataTypeClass = String.class),
            @ApiImplicitParam(name="stream", value = "流ID", required = false, dataTypeClass = String.class),
    })
    @GetMapping(value = "/file/collection/list")
    @ResponseBody
    public WVPResult<List<SignInfo>> collectionList(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String app,
            @RequestParam(required = false) String stream){

        List<SignInfo> signInfos = videoFileService.getCollectionList(app, stream, type);
        WVPResult<List<SignInfo>> result = new WVPResult<>();
        result.setCode(0);
        result.setMsg(signInfos != null ?"success":"error");
        result.setData(signInfos);
        return result;
    }

    /**
     * 中止视频裁剪合并任务列表
     */
    @ApiOperation("中止视频裁剪合并任务列表(暂不支持)")
    @GetMapping(value = "/file/download/task/stop")
    @ResponseBody
    public WVPResult<String> stopTaskForDownload(@RequestParam String taskId){
//        WVPResult<String> result = new WVPResult<>();
//        if (taskId == null) {
//            result.setCode(400);
//            result.setMsg("taskId 不能为空");
//            return result;
//        }
//        boolean stopResult = videoFileService.stopTask(taskId);
//        result.setCode(0);
//        result.setMsg(stopResult ? "success": "fail");
        return null;
    }

    /**
     * 录制完成的通知, 对用zlm的hook
     * @return
     */
    @ResponseBody
    @PostMapping(value = "/on_record_mp4", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> onRecordMp4(@RequestBody JSONObject json) {
        JSONObject ret = new JSONObject();
        ret.put("code", 0);
        ret.put("msg", "success");
        String file_path = json.getString("file_path");
        logger.debug("ZLM 录制完成，参数：" + file_path);
        if (file_path == null) return new ResponseEntity<String>(ret.toString(), HttpStatus.OK);
        videoFileService.handFile(new File(file_path));

        return new ResponseEntity<String>(ret.toString(), HttpStatus.OK);
    }

    /**
     * 磁盘空间查询
     */
    @ApiOperation("磁盘空间查询")
    @ResponseBody
    @GetMapping(value = "/space", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> getSpace() {
        JSONObject ret = new JSONObject();
        ret.put("code", 0);
        ret.put("msg", "success");
        SpaceInfo spaceInfo = videoFileService.getSpaceInfo();
        ret.put("data", JSON.toJSON(spaceInfo));
        return new ResponseEntity<>(ret.toString(), HttpStatus.OK);
    }

    /**
     * 录像文件的时长
     */
    @ApiOperation("录像文件的时长")
    @ApiImplicitParams({
            @ApiImplicitParam(name="app", value = "应用名", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name="stream", value = "流ID", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name="recordIng", value = "是否录制中", required = false, dataTypeClass = String.class),
    })
    @ResponseBody
    @GetMapping(value = "/file/duration", produces = "application/json;charset=UTF-8")
    @PostMapping(value = "/file/duration", produces = "application/json;charset=UTF-8")
    public ResponseEntity<String> fileDuration( @RequestParam String app, @RequestParam String stream) {
        JSONObject ret = new JSONObject();
        ret.put("code", 0);
        ret.put("msg", "success");
        long duration = videoFileService.fileDuration(app, stream);
        ret.put("data", duration);
        return new ResponseEntity<>(ret.toString(), HttpStatus.OK);
    }
}
