package com.leyou.common.auth.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 黑马程序员
 */
@Data
@NoArgsConstructor//无参构造方法
@AllArgsConstructor//全参构造方法
public class AppInfo {
    private Long id;
    private String serviceName;
    private List<Long> targetList;
}