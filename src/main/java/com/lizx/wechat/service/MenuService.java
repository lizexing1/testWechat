package com.lizx.wechat.service;

import java.util.List;
import java.util.Map;

import com.lizx.utils.Result;

public interface MenuService {

	/**
	 * <pre>changeMenu(调用接口，修改菜单按钮)   
	 * 创建人：李泽兴 lizexing@163.com      
	 * 创建时间：2018年5月25日 下午7:48:51    
	 * 修改人：李泽兴 lizexing@163.com       
	 * 修改时间：2018年5月25日 下午7:48:51    
	 * 修改备注： 
	 * @return</pre>
	 */
	Result changeMenu();
	/**
	 * <pre>list(查询按钮表的内容列表)   
	 * 创建人：李泽兴 lizexing@163.com      
	 * 创建时间：2018年5月25日 下午11:08:36    
	 * 修改人：李泽兴 lizexing@163.com       
	 * 修改时间：2018年5月25日 下午11:08:36    
	 * 修改备注： 
	 * @return</pre>
	 */
	Result list();
	/**
	 * <pre>getById(根据父id获取数据集合)   
	 * 创建人：李泽兴 lizexing@163.com      
	 * 创建时间：2018年5月26日 下午2:45:11    
	 * 修改人：李泽兴 lizexing@163.com       
	 * 修改时间：2018年5月26日 下午2:45:11    
	 * 修改备注： 
	 * @param id
	 * @return</pre>
	 */
	List<Map<String, Object>> getByPid(String pid);

}
