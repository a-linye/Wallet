package cn.edu.gzhu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class ResponseVo<E> {
        private String message; //操作的提示信息
        private Integer status; //响应状态码
        private E data; //获取数据
}
