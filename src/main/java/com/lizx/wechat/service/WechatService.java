package com.lizx.wechat.service;

import java.util.Map;

public interface WechatService {

	String wechatOrder(String id, String openid);

	boolean checkPayMessage(Map<String, String> map);

}
