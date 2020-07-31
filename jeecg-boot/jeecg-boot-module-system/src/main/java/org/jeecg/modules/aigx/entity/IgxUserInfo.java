package org.jeecg.modules.aigx.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author Administrator
 * @auth Created by Administrator on 2020/7/16.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class IgxUserInfo {
    //用户唯一编码
    public String loginAccount;
    //姓名
    public String name;
    //身份证号码
    public String idCardNo;
    //手机号码
    public String mobile;
}
