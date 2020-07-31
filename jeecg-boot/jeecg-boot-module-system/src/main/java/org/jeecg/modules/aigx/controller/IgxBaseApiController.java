package org.jeecg.modules.aigx.controller;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.aigx.service.IIgxBaseApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Administrator
 * @auth Created by Administrator on 2020/7/15.
 */
@RestController
@RequestMapping("/aigx/IgxBaseApi")
public class IgxBaseApiController {
    @Autowired
    private IIgxBaseApiService igxBaseApiService;
    /**
     * 根据code获取爱广西APP token
     * @param code
     * @return
     */
    @GetMapping("getIgxToken")
    public Result<?> getIgxToken(String code){
        return igxBaseApiService.getIgxToken(code);
    }

    /**
     * 根据accessToken查询用户基本信息
     * @return
     */
    @GetMapping("getIgxUserInfo")
    public Result<?> getIgxUserInfo(){
        return igxBaseApiService.getIgxUserInfo();
    }
}
