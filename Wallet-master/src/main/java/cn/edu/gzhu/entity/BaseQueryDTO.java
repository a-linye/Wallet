package cn.edu.gzhu.entity;

import lombok.Data;

/**
 * @author Li zhihao
 * @version V1.0
 * @description: 基础查询DTO
 * @creat 2022-10-21-16:55
 */
@Data
public abstract class BaseQueryDTO {
	/**
	 * 分页号，当前页码
	 */
	private Integer pageNumber=1;
	/**
	 * 每页多少条记录
	 */
	private Integer pageSize=10;

	/**
	 * 校准数据，防止分页数据异常。
	 */
	public void checkData(){
		if(pageNumber<=0)
			pageNumber=1;
		if(pageSize<=0)
			pageSize=10;
	}
}
