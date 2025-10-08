package com.puxinxiaolin.ai.robot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 避免了返回给前端时导致的空格丢失问题
 * @Author: YCcLin
 * @Date: 2025/10/2 22:47
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AIResponse {

    /**
     * 流式响应数据
     */
    private String v;

}
