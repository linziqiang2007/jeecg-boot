package org.jeecg.modules.aigx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.poi.ss.formula.functions.T;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.aigx.entity.IgxUserInfo;

/**
 * @auth Created by Administrator on 2020/7/15.
 */
public interface IIgxBaseApiService  extends IService<IgxUserInfo>{
    /**
     * 根据code获取爱广西APP token
     * @param code
     * @return
     */
    public Result<?> getIgxToken(String code);
    /**
     * 根据accessToken查询用户基本信息
     * @return
     */
    public Result<?> getIgxUserInfo();
}
