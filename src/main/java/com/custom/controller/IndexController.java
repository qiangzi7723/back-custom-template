package com.custom.controller;

import com.alibaba.fastjson.JSONObject;
import com.custom.params.BaseList;
import com.custom.params.GameCommit;
import com.custom.params.GameRank;
import com.custom.params.GameStart;
import com.custom.service.common.raffle.impl.RaffleServiceImpl;
import com.custom.request.Context;
import com.custom.service.IndexService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@Api(value = "调试用接口", tags = { "勿对接" })
@RequestMapping("/index")
public class IndexController {
    @Autowired
    IndexService indexService;

    @Autowired
    RaffleServiceImpl raffleService;

    @ApiOperation(value="测试GET接口 以及参数信息 返回结果", notes="")
    @GetMapping("/status/get")
    @ApiImplicitParam(name="activityId",value="活动ID",dataType="int", paramType = "query")
    public JSONObject getStatus(){
        return indexService.getStatus();
    }

    @ApiOperation(value="测试POST接口 以及参数信息 返回结果", notes="")
    @PostMapping("/status/post")
    public JSONObject postStatus(@RequestBody @ApiParam(name="参数",value="传入JSON格式",required=true) BaseList template){
        return indexService.postStatus();
    }

    @ApiOperation(value="抽奖接口", notes="仅供测试使用，生产环境需要注释")
    @GetMapping("/raffle")
    @ApiImplicitParam(name="activityId",value="活动ID",dataType="int", paramType = "query")
    public JSONObject raffle(){
        return indexService.raffle();
    }

    @ApiOperation(value="初始化奖品token", notes="")
    @GetMapping(value = "addGiftToken")
    @ApiImplicitParam(name="activityId",value="活动ID",dataType="int", paramType = "query")
    public JSONObject addGiftToken() {
        // TODO 需要加入Token，不能随意调用
        return raffleService.autoCheck(Integer.valueOf(Context.get().getIntValue("activityId")));
    }

    @ApiOperation(value="黑名单检测", notes="仅供测试使用，生产环境需要注释")
    @GetMapping("/illegalCheck")
    public JSONObject illegalCheck(){
        return indexService.illegalCheck();
    }

    @ApiOperation(value="黑名单检测", notes="仅供测试使用，生产环境需要注释")
    @GetMapping("/illegalLevel")
    public JSONObject illegalLevel(){
        return indexService.illegalLevel();
    }

    @ApiOperation(value="游戏排行榜", notes="可以传入JWT，也可以不传入，如果传入，会返回个人排名")
    @PostMapping("/rank")
    public JSONObject rank(@RequestBody @ApiParam(name="参数",value="传入JSON格式",required=true) GameRank template){
        return indexService.rank();
    }

    @ApiOperation(value="提交分数", notes="")
    @PostMapping("/commit")
    public JSONObject commit(@RequestBody @ApiParam(name="参数",value="传入JSON格式",required=true) GameCommit template){
        return indexService.commit();
    }

    @ApiOperation(value="开始游戏", notes="该接口会颁发游戏token，提交分数时需要带上此token（返回的recordId，在提交分数时候也需要带上）；<br/>" +
            "每一个游戏token，设定了10分钟超时，10分钟内不提交分数，会被判定为失效")
    @PostMapping("/start")
    public JSONObject start(@RequestBody @ApiParam(name="参数",value="传入JSON格式",required=true) GameStart template){
        return indexService.start();
    }
}
