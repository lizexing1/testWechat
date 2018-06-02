package com.lizx.wechat.service.impl;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.lizx.utils.CommonUtil;
import com.lizx.utils.HttpClientUtils;
import com.lizx.utils.Result;
import com.lizx.utils.WechatConfig;
import com.lizx.wechat.mapper.MenuMapper;
import com.lizx.wechat.service.MenuService;

import net.sf.json.JSONObject;

@Service
public class MenuServiceImpl implements MenuService{

	@Autowired
	private MenuMapper menuMapper;
	
	/**
	 * 调用接口，修改菜单按钮
	 */
	@Override
	public Result changeMenu() {
		
		//获取tocken的url
		String GET_TOCKEN_URL = WechatConfig.GET_TOCKEN_URL.replace("APPID", WechatConfig.APPID)
				.replace("APPSECRET", WechatConfig.APP_SECRECT);
		//执行请求，调用接口获取tocken
		String tockenStr = HttpClientUtils.getMethod(GET_TOCKEN_URL);
		System.out.println(tockenStr);
		JSONObject tockenJson = JSONObject.fromObject(tockenStr);
		String access_token = (String) tockenJson.get("access_token");
		System.out.println(access_token);
		
		//修改按钮的url
		String CHANGE_MENU_URL = WechatConfig.CHANGE_MENU_URL.replace("ACCESS_TOKEN", access_token);
		
		String menuParam = "{\"button\":[{\"type\":\"click\",\"name\":\"今日歌曲\",\"key\":\"V1001_TODAY_MUSIC\"},{\"name\":\"菜单\",\"sub_button\":[{\"type\":\"view\",\"name\":\"搜索\",\"url\":\"http://www.soso.com/\"},{\"type\":\"click\",\"name\":\"赞一下我们\",\"key\":\"V1001_GOOD\"}]}]}";
		
		//所有的父按钮都放在这里
		JSONArray buttonArray = new JSONArray();
		
		List<Map<String, Object>> list = menuMapper.getByPid("0");
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			
			List<Map<String, Object>> childList = menuMapper.getByPid(map.get("id").toString());
			
			
			JSONArray childButtonArry = new JSONArray();
			
			for (int j = 0; j < childList.size(); j++) {
				
				Map<String, Object> childMap = childList.get(j);
				
				JSONObject childButtonJson = new JSONObject();
				
				childButtonJson.put("type", childMap.get("type"));
				childButtonJson.put("name", childMap.get("name"));
				
				if(childMap.get("type").equals("click")) {
					childButtonJson.put("key", childMap.get("key"));
				}
				
				if(childMap.get("type").equals("scancode_push")) {
					JSONArray array = new JSONArray();
					childButtonJson.put("key", childMap.get("key"));
					childButtonJson.put("sub_button", array);
				}
				
				if(childMap.get("type").equals("scancode_waitmsg")) {
					
				}
				
				if(childMap.get("type").equals("pic_sysphoto")) {
					
				}
				
				if(childMap.get("type").equals("view")) {
					childButtonJson.put("url", childMap.get("url"));
				}
				
				if(childMap.get("type").equals("pic_weixin")) {
					
				}
				
				if(childMap.get("type").equals("location_select")) {
					
				}
				
				if(childMap.get("type").equals("media_id")) {
					
				}
				
				if(childMap.get("type").equals("view_limited")) {
					
				}
				childButtonArry.add(childButtonJson);
			}
			
			JSONObject parentJson = new JSONObject();
		
			//父框的按钮名字
			parentJson.put("name", map.get("name"));
			
			if(map.get("type").equals("")) {
				parentJson.put("sub_button", childButtonArry);
			}
			
			if(map.get("type").equals("click")) {
				parentJson.put("type", map.get("type"));
				parentJson.put("key", map.get("key"));
			}
			
			if(map.get("type").equals("scancode_push")) {
				
			}
			
			if(map.get("type").equals("scancode_waitmsg")) {
				
			}
			
			if(map.get("type").equals("pic_sysphoto")) {
				
			}
			
			if(map.get("type").equals("view")) {
				parentJson.put("type", map.get("type"));
				parentJson.put("url", map.get("url"));
			}
			
			if(map.get("type").equals("pic_weixin")) {
				
			}
			
			if(map.get("type").equals("location_select")) {
				
			}
			
			if(map.get("type").equals("media_id")) {
				
			}
			
			if(map.get("type").equals("view_limited")) {
				
			}
			
			buttonArray.add(parentJson);
		}
	
		
		//最大的json字符串button
		JSONObject buttonJson = new JSONObject();
		buttonJson.put("button", buttonArray);
		
		System.out.println("===================");
		System.out.println(buttonJson);
		System.out.println("===============");
		
		JSONObject paramJson = JSONObject.fromObject(buttonJson);
		//执行请求，修改按钮
		String changeMenuResult = CommonUtil.httpsRequestJson(CHANGE_MENU_URL, "POST", paramJson);
		System.out.println(changeMenuResult);
		
		return null;
	}
	/**
	 * 查询按钮表的内容列表
	 */
	@Override
	public Result list() {
		List<Map<String, Object>> list = menuMapper.getByPid("0");
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = list.get(i);
			String id = (String) map.get("id");
			List<Map<String, Object>> childList = menuMapper.getByPid(id);
			list.get(i).put("childList", childList);
		}
		Result result = new Result();
		result.put("list", list);
		return result;
	}
	@Override
	public List<Map<String, Object>> getByPid(String pid) {
		return menuMapper.getByPid(pid);
	}

}
