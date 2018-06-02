package com.lizx.wechat.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.JDOMException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.lizx.utils.HttpClientUtils;
import com.lizx.utils.WechatConfig;
import com.lizx.utils.XMLUtil;
import com.lizx.wechat.bean.message.Event;
import com.lizx.wechat.bean.message.QRCodeMessage;
import com.lizx.wechat.bean.message.TextImgMessage;
import com.lizx.wechat.bean.message.TextMessage;
import com.lizx.wechat.bean.message.ViewMessage;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/wechat")
public class WechatHandler {
	
	private Logger LOGGER = LogManager.getLogger(WechatHandler.class);
	
	@Value("${wechat.welcome}")
	private String wechatWecome;
	@Value("${wechat.welcome.title}")
	private String title;
	@Value("${wechat.welcome.description}")
	private String description;
	@Value("${wechat.welcome.picUrl}")
	private String picUrl;
	@Value("${wechat.welcome.url}")
	private String url;
	
	@RequestMapping(value="/connect",method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void connectWeixin(HttpServletRequest request, HttpServletResponse response) throws IOException{
		
        // 将请求、响应的编码均设置为UTF-8（防止中文乱码）  
        request.setCharacterEncoding("UTF-8");  //微信服务器POST消息时用的是UTF-8编码，在接收时也要用同样的编码，否则中文会乱码；
        response.setCharacterEncoding("UTF-8"); //在响应消息（回复消息给用户）时，也将编码方式设置为UTF-8，原理同上；
        boolean isGet = request.getMethod().toLowerCase().equals("get"); 
      
        PrintWriter out = response.getWriter();
        
        try {
               if (isGet) {
	                String signature = request.getParameter("signature");// 微信加密签名  
	                String timestamp = request.getParameter("timestamp");// 时间戳  
	                String nonce = request.getParameter("nonce");// 随机数  
	                String echostr = request.getParameter("echostr");//随机字符串  
	                
	              // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败  
                 response.getWriter().write(echostr);  
                 out.close();
            }
        } catch (Exception e) {
            LOGGER.error("Connect the weixin server is error.");
        }
        
        
        try {
			//获取微信发来的信息流
			InputStream inStream = request.getInputStream();
			ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outSteam.write(buffer, 0, len);
			}
			outSteam.close();
			//把微信发来的xml文件信息转成String类型
			String result = new String(outSteam.toByteArray(), "UTF-8");
			
			System.out.println("result==" + result);
			Map<String, String> map = null;
			try {
				//通过sax解析xml技术，把xml文本解析成map对象
				map = XMLUtil.doXMLParse(result);
			} catch (JDOMException e) {
				e.printStackTrace();
			}

			// 得到消息的类型
			String msgType = map.get("MsgType");
			String event = map.get("Event");

			System.out.println("msgType=" + msgType);
			System.out.println("event=" + event);

			String responseMessage = "";

			/**
			 * 用户关注公众号后进行的处理
			 */
			/**
			 * 用户发送文本内容进行的处理操作
			 */
			if(event==null && msgType.equals("text")){
				responseMessage = processMsgType(map);
	       	}else if(event!=null && event.equals("scancode_push") && msgType.equals("event") || msgType.equals("text")){
	       		responseMessage = processMsgType(map);
	        }else if(event!=null){
	        	responseMessage = processMsg(map);
	        }
	        System.out.println("******responseMessage*****responseMessage***************"+responseMessage);
	        if(responseMessage!=null){
	        	LOGGER.info(responseMessage);
	            PrintWriter writer = response.getWriter();
	            
	            writer.write(responseMessage);
	            writer.flush();
	            inStream.close();
	            writer.close();
	        }
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String processMsgType(Map<String, String> message) {
		String result = null;
		String eventType = message.get("MsgType");
		
		switch (eventType) {
		case Event.TEXT:
			result = processText(message);
			break;
		case Event.EVENT:
			result = processEvent(message);
			break;
		default:
			break;
		}
		return result;
	}

	/**
	 * <pre>processEvent(用户扫码后的处理方式)   
	 * 创建人：李泽兴 lizexing@163.com      
	 * 创建时间：2018年5月27日 下午8:55:08    
	 * 修改人：李泽兴 lizexing@163.com       
	 * 修改时间：2018年5月27日 下午8:55:08    
	 * 修改备注： 
	 * @param message
	 * @return</pre>
	 */
	private String processEvent(Map<String, String> message) {
		
		String toUserName = message.get("ToUserName");
		String fromUserName = message.get("FromUserName");
		String scanCodeInfo = message.get("ScanCodeInfo");
		
		String[] scanCodeInfoSplit = scanCodeInfo.split("<ScanResult>");
		String view = "";
		if( scanCodeInfoSplit != null && scanCodeInfoSplit.length >0 ){
			String[] split = scanCodeInfoSplit[1].split("</ScanResult>");
			 view = split[0];
		}
		QRCodeMessage viewMessage = new QRCodeMessage();
		viewMessage.setView(view);
		viewMessage.setToUserName(fromUserName);
		viewMessage.setFromUserName(toUserName);
		viewMessage.setMsgType("event");
		return viewMessage.toXML();
	}
	/**
	 * <pre>processText(用户发送文本后的处理方式)   
	 * 创建人：李泽兴 lizexing@163.com      
	 * 创建时间：2018年5月27日 下午8:56:27    
	 * 修改人：李泽兴 lizexing@163.com       
	 * 修改时间：2018年5月27日 下午8:56:27    
	 * 修改备注： 
	 * @param message
	 * @return</pre>
	 */
	private String processText(Map<String, String> message) {
		
		String content = message.get("Content");
		//调用solr查询数据
		String result = "";//solrService.search(content);
		if(!result.equals("")) {
			TextMessage text = new TextMessage();
			text.setContent(result);
			text.setCreateTime(System.currentTimeMillis());
			text.setMsgType("text");
			text.setFromUserName(message.get("ToUserName"));
			text.setToUserName(message.get("FromUserName"));
			return text.toXML();
		}
		return "success";
	}
	
	/**
	 * <pre>processMsg(接受事件类型，根据不同类型判断进入不同方法)   
	 * 创建人：李泽兴 lizexing@163.com      
	 * 创建时间：2018年5月27日 下午8:57:28    
	 * 修改人：李泽兴 lizexing@163.com       
	 * 修改时间：2018年5月27日 下午8:57:28    
	 * 修改备注： 
	 * @param message
	 * @return</pre>
	 */
	private String processMsg(Map<String, String> message){
		String result = null;
		String eventType = message.get("Event");
		switch (eventType) {
		case Event.SUBSCRIBE:
			result = processSubscribe(message);
			break;
		case Event.VIEW:
			result = processView(message);
			break;
		default:
			break;
		}
		return result;
	}
	/**
	 * <pre>processView(点击按钮触发的事件)   
	 * 创建人：李泽兴 lizexing@163.com      
	 * 创建时间：2018年5月27日 下午8:58:05    
	 * 修改人：李泽兴 lizexing@163.com       
	 * 修改时间：2018年5月27日 下午8:58:05    
	 * 修改备注： 
	 * @param message
	 * @return</pre>
	 */
	private String processView(Map<String, String> message) {
		String toUserName = message.get("ToUserName");
		String fromUserName = message.get("FromUserName");
		String eventKey = message.get("EventKey");
		
		ViewMessage viewMessage = new ViewMessage();
		
		viewMessage.setView(eventKey);
		viewMessage.setToUserName(fromUserName);
		viewMessage.setFromUserName(toUserName);
		return viewMessage.toXML();
	}
	
	private String processSubscribe(Map<String, String> message){
		String toUserName = message.get("ToUserName");
		String fromUserName = message.get("FromUserName");
		
		TextImgMessage textImgMessage = new TextImgMessage();
		textImgMessage.setToUserName(fromUserName);
		textImgMessage.setFromUserName(toUserName);
		textImgMessage.setMsgType("news");
		
		textImgMessage.setTitle(title);
		textImgMessage.setDescription(description);
		textImgMessage.setPicUrl(picUrl);
		textImgMessage.setUrl(url);
		Date date = new Date();
		textImgMessage.setCreateTime(date.getTime());
		return textImgMessage.toXML();
	}
	
	/**
	 * <pre>getCode(获取code信息)   
	 * 创建人：李泽兴 lizexing@163.com      
	 * 创建时间：2018年5月28日 上午9:19:40    
	 * 修改人：李泽兴 lizexing@163.com       
	 * 修改时间：2018年5月28日 上午9:19:40    
	 * 修改备注： 
	 * @param response</pre>
	 */
	@RequestMapping("getCode")
	public void getCode(HttpServletResponse response) {

		String redirectUrl = URLDecoder.decode(WechatConfig.REDIRECT_GETCODE_URI);
		
		String codeUrl = WechatConfig.GETCODE_URI.replace("APPID", WechatConfig.APPID)
				.replace("REDIRECT_URI", redirectUrl).replace("SCOPE", "snsapi_userinfo");

		System.out.println("codeUrl====="+codeUrl);
		try {
			response.sendRedirect(codeUrl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("获取code进行转发时出现异常：codeUrl==" + codeUrl);
		}
	}
	/**
	 * <pre>getOpenId(微信调用，返回code，获取openid)   
	 * 创建人：李泽兴 lizexing@163.com      
	 * 创建时间：2018年5月28日 下午1:49:21    
	 * 修改人：李泽兴 lizexing@163.com       
	 * 修改时间：2018年5月28日 下午1:49:21    
	 * 修改备注： 
	 * @param code
	 * @param response</pre>
	 */
	@RequestMapping("getOpenId")
	public void getOpenId(@RequestParam("code") String code, HttpServletResponse response,
			HttpServletRequest request) {

		// 根据返回来的code去访问微信，最后返回accesstoken，里面包含openid和access_token
		String getAccessTokenUrl = WechatConfig.GET_OPENID_URI.replace("APPID", WechatConfig.APPID)
				.replace("SECRET", WechatConfig.APP_SECRECT).replace("CODE", code);
		
		String accessToken = HttpClientUtils.getMethod(getAccessTokenUrl);
		System.out.println("getAccessTokenUrl====" + accessToken);

		// 把相应回来的json字符串转换成json格式，用户键值对获取里面具体的值
		JSONObject openIdJson = JSONObject.fromObject(accessToken);

		/**
		 * 对返回的数据一定要做容错机制处理，防止获取openid时报错
		 */
		// 获取openid
		String openid = (String) openIdJson.get("openid");
		System.out.println("openid===" + openid);
		// 获取access_token
		String access_token = (String) openIdJson.get("access_token");
		System.out.println("access_token===" + access_token);

		request.getSession().setAttribute("openid", openid);
		request.getSession().setAttribute("access_token", access_token);
		
		// 用户信息返回，获得一个用户信息的json串
		String wechatUserInfo = getWechatUserInfo(access_token, openid);
		
		// 把用户信息插入数据库中,跟第三方登陆一样，该怎么处理就怎么处理

		// 返回页面，进入个人中心里
		try {
			response.sendRedirect("http://www.lizexing.cn/wechat/list.html");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * <pre>getWechatUserInfo(获取用户信息)   
	 * 创建人：李泽兴 lizexing@163.com      
	 * 创建时间：2018年5月28日 下午2:01:02    
	 * 修改人：李泽兴 lizexing@163.com       
	 * 修改时间：2018年5月28日 下午2:01:02    
	 * 修改备注： 
	 * @param access_token
	 * @param openid
	 * @return</pre>
	 */
	public String getWechatUserInfo(String access_token, String openid) {

		// 拼接获取用户信息的地址，再httpClient请求下
		String getUserInfo = WechatConfig.GET_USERINFO_URI.replace("ACCESS_TOKEN", access_token).replace("OPENID", openid);

		String userInfo = HttpClientUtils.getMethod(getUserInfo);
		System.out.println("userInfo====" + userInfo);
		return userInfo;
	}
	
	
	
}
