package com.lizx.utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;


public class WechatConfig {

	//文老师的公众号
	//微信公众号的appid，登陆后台管理可以看到
	public static String APPID = "wxe4e4c89c099df28b";
	//微信公众号的APP_SECRECT，登陆后台管理可以看到
	public static String APP_SECRECT = "40987d82dd8feeb62a9216d3edb7b7da";
	//开通微信支付分配的商户号 
	public static String MCH_ID = "1288900901";
	//商户API密钥 在商户支付平台设置 
	public final static String API_KEY = "wenjianywenjianywenjianywenjiany";
	//本地服务器的IP
	public final static String SPBILL_CREATE_IP = "59.10.3.1"; 
	//签名加密方式 
	public final static String SIGN_TYPE = "MD5";
	
	//微信支付完成后，通知我们平台支付情况
	public static final String NOTIFY_URL = null;
	
	/**
	 * 获取code后返回的url
	 */
	public final static String REDIRECT_GETCODE_URI = "http://www.lizexing.cn/wechat/wechat/getOpenId";
	
	//微信获取tocken的地址
	public final static String GET_TOCKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	
	//微信修改菜单的地址
	public final static String CHANGE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
	
	//获取code的url
	public final static String GETCODE_URI="https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE#wechat_redirect";

	//获取openid时调用
	public final static String GET_OPENID_URI = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
	
	//获取用户信息
	public final static String GET_USERINFO_URI = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
	
	
	
	public static String getRequestXml(SortedMap<Object,Object> parameters){
	    StringBuffer sb = new StringBuffer();
	    sb.append("<xml>");
	    Set es = parameters.entrySet();
	    Iterator it = es.iterator();
	    while(it.hasNext()) {
	        Map.Entry entry = (Map.Entry)it.next();
	        String k = (String)entry.getKey();
	        String v = (String)entry.getValue();
	        if ("attach".equalsIgnoreCase(k)||"body".equalsIgnoreCase(k)||"sign".equalsIgnoreCase(k)) {
	            sb.append("<"+k+">"+"<![CDATA["+v+"]]></"+k+">");
	        }else {
	            sb.append("<"+k+">"+v+"</"+k+">");
	        }
	    }
	    sb.append("</xml>");
	    return sb.toString();
	}
	
	 public static String CreateNoncestr() {
	        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	        String res = "";
	        for (int i = 0; i < 16; i++) {
	            Random rd = new Random();
	            res += chars.charAt(rd.nextInt(chars.length() - 1));
	        }
	        return res;
	}
	 
		    
	 public static String createSign(SortedMap<Object,Object> parameters){
		    StringBuffer sb = new StringBuffer();
		    Set es = parameters.entrySet();
		    Iterator it = es.iterator();
		    while(it.hasNext()) {
		        Map.Entry entry = (Map.Entry)it.next();
		        String k = (String)entry.getKey();
		        Object v = entry.getValue();
		        if(null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
		            sb.append(k + "=" + v + "&");
		        }
		    }
		    sb.append("key=" + API_KEY);
		    
		    //上面的代码作用是拼接一个字符串，拼接成这个样子：stringA&key=192006250b4c09247ec02edce69f6a2d
		    //MD5Util工具类不要看
		    String sign = MD5Util.MD5Encode(sb.toString(),"UTF-8").toUpperCase();
		    
		    //让字符串升序排列
		    char[] arrayCh = sign.toCharArray();
			Arrays.sort(arrayCh);
			String sortSign=Arrays.toString(arrayCh);
		    
		    return sortSign;
	 }
	 
	 public static String setXML(String return_code, String return_msg) {
	        return "<xml><return_code><![CDATA[" + return_code + "]]></return_code><return_msg><![CDATA[" + return_msg + "]]></return_msg></xml>";
	}
}
