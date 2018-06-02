package com.lizx.wechat.controller;

import com.lizx.utils.HttpClientUtils;
import com.lizx.utils.WechatConfig;
import com.lizx.utils.XMLUtil;
import com.lizx.wechat.service.WechatService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
@RequestMapping("wechatBack")
public class WechatController {

	private final Logger logger = LoggerFactory.getLogger(WechatController.class);

	private WechatService wechatService;
	
	/**
	 * <pre>
	 * handle(回掉地址进行验证时调用)   
	 * 创建人：李泽兴 lizexing@163.com      
	 * 创建时间：2017年12月16日 上午9:35:59    
	 * 修改人：李泽兴 lizexing@163.com       
	 * 修改时间：2017年12月16日 上午9:35:59    
	 * 修改备注： 
	 * &#64;param request
	 * &#64;param response
	 * &#64;param signature
	 * &#64;param timestamp
	 * &#64;param nonce
	 * &#64;param echostr
	 * </pre>
	 */
	@RequestMapping(value = "/handler", method = { RequestMethod.POST, RequestMethod.GET })
	public void handler(HttpServletRequest request, HttpServletResponse response) {

		System.out.println("==========handlehandle=======handlehandle====");
		 boolean isGet = request.getMethod().toLowerCase().equals("get"); 
		System.out.println("==========isGetisGet=======isGetisGet====" + isGet);
		  try {
              if (isGet) {
	                String signature = request.getParameter("signature");// 微信加密签名  
	                String timestamp = request.getParameter("timestamp");// 时间戳  
	                String nonce = request.getParameter("nonce");// 随机数  
	                String echostr = request.getParameter("echostr");//随机字符串  
	                
	              // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败  
                response.getWriter().write(echostr);  
           }
       } catch (Exception e) {
    	   logger.error("Connect the weixin server is error.");
       }

		/**
		 * 微信一旦开启开发者模式后，用户进行发送消息，发送图片，关注微信公众号操作后，
		 * 将会进入到这个方法里，进来的时候接受到的是一个xml字符串，我们需要根据这个xml字符串的内容进行相应的处理
		 * 
		 * 
		 */
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
			// 得到消息的内容
			String content = map.get("Content");
			// 得到用户执行的事件
			String event = map.get("Event");

			System.out.println("msgType=" + msgType);
			System.out.println("content=" + content);
			System.out.println("event=" + event);

			String responseMessage = "";

			/**
			 * 用户关注公众号后进行的处理
			 */
			if (event != null && event.equals("subscribe")) {

				System.out.println("event!=null && event.equals===================================");
				StringBuffer stringBuffer = new StringBuffer();
				stringBuffer.append("<xml>");
				stringBuffer.append("<ToUserName><![CDATA[" + map.get("FromUserName") + "]]></ToUserName>");
				stringBuffer.append("<FromUserName><![CDATA[" + map.get("ToUserName") + "]]></FromUserName>");
				stringBuffer.append("<CreateTime>" + System.currentTimeMillis() + "</CreateTime>");
				stringBuffer.append("<MsgType><![CDATA[news]]></MsgType>");
				stringBuffer.append("<ArticleCount>2</ArticleCount>");
				
				stringBuffer.append("<Articles>");

				stringBuffer.append("<item>");
				stringBuffer.append("<Title><![CDATA[这是一首歌]]></Title>");
				stringBuffer.append("<Description><![CDATA[这是一首快乐的歌曲！！！]]></Description>");
				stringBuffer.append(
						"<PicUrl><![CDATA[http://g.hiphotos.baidu.com/image/pic/item/78310a55b319ebc4a99197708826cffc1f171669.jpg]]></PicUrl>");
				stringBuffer.append("<Url><![CDATA[www.baidu.com]]></Url>");
				stringBuffer.append("</item>");

				stringBuffer.append("<item>");
				stringBuffer.append("<Title><![CDATA[这是一个美女]]></Title>");
				stringBuffer.append("<Description><![CDATA[你猜是哪个美女？？？]]></Description>");
				stringBuffer.append(
						"<PicUrl><![CDATA[http://b.hiphotos.baidu.com/image/pic/item/54fbb2fb43166d220612c57c4c2309f79152d2f5.jpg]]></PicUrl>");
				stringBuffer
						.append("<Url><![CDATA[https://zhidao.baidu.com/question/1732094237675716987.html]]></Url>");
				stringBuffer.append("</item>");

				stringBuffer.append("</Articles>");
				stringBuffer.append("</xml>");

				responseMessage = stringBuffer.toString();
			}

			/**
			 * 用户发送文本内容进行的处理操作
			 */
			System.out.println(msgType + "----" + content);
			if (msgType.equals("text") && content.equals("你好")) {

				responseMessage = "<xml><ToUserName><![CDATA[" + map.get("FromUserName")
						+ "]]></ToUserName><FromUserName><![CDATA[" + map.get("ToUserName")
						+ "]]></FromUserName><CreateTime>" + System.currentTimeMillis()
						+ "</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[一点也不好]]></Content></xml>";

				// PrintWriter writer = response.getWriter();
				// writer.write(retrunText);
				// writer.flush();
				// inStream.close();
				// writer.close();

			} else if (msgType.equals("text") && content.equals("加好友")) {

				responseMessage = "<xml><ToUserName><![CDATA[" + map.get("FromUserName")
						+ "]]></ToUserName><FromUserName><![CDATA[" + map.get("ToUserName")
						+ "]]></FromUserName><CreateTime>" + System.currentTimeMillis()
						+ "</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[我是李泽兴，你可以加我QQ号：1013926837]]></Content></xml>";
				// PrintWriter writer = response.getWriter();
				// writer.write(retrunText);
				// writer.flush();
				// inStream.close();
				// writer.close();

			} else if (msgType.equals("text") && !content.equals("加好友") && !content.equals("你好")) {
				/**
				 * 用户发送的文本消息或执行的某一个操作等，上面的条件都不满足的时候，需要给微信回复一个success，
				 * 否则的话微信会报错，报什么错？报错内容是该公众号出现异常，请稍后重试
				 */
				responseMessage = "success";
			}
			System.out.println("responseMessage==" + responseMessage);
			response.setCharacterEncoding("UTF-8");
			PrintWriter writer = response.getWriter();
			writer.write(responseMessage);
			writer.flush();
			inStream.close();
			writer.close();

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * <pre>
	 * getCode(获取微信验证的code)   
	 * 创建人：李泽兴 lizexing@163.com      
	 * 创建时间：2017年12月16日 上午9:36:35    
	 * 修改人：李泽兴 lizexing@163.com       
	 * 修改时间：2017年12月16日 上午9:36:35    
	 * 修改备注： 
	 * &#64;param response
	 * </pre>
	 */
	@RequestMapping("redirectCodeUrl")
	public void redirectCodeUrl(HttpServletResponse response) {

		String redirectUrl = "";//URLDecoder.decode(CommonConfig.REDIRECT_GETCODE_URI);
		// String redirectUrl =
		// URLDecoder.decode("http://192.168.199.111:8080/wechatBack/getCode");

//		String codeUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + CommonConfig.APPID
//				+ "&redirect_uri=" + redirectUrl + "&response_type=code" + "&scope=snsapi_userinfo#wechat_redirect";

		String codeUrl = "";
		try {
			response.sendRedirect(codeUrl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("获取code进行转发时出现异常：codeUrl==" + codeUrl);
		}
	}

	/**
	 * <pre>
	 * getCode(在这个方法里根据code获取了access_token和用户信息)   
	 * 创建人：李泽兴 lizexing@163.com      
	 * 创建时间：2017年12月19日 下午4:46:36    
	 * 修改人：李泽兴 lizexing@163.com       
	 * 修改时间：2017年12月19日 下午4:46:36    
	 * 修改备注： 
	 * &#64;param code
	 * &#64;param response
	 * </pre>
	 */
	@RequestMapping("getCode")
	public void getCode(@RequestParam("code") String code, HttpServletResponse response) {

		// 根据返回来的code去访问微信，最后返回accesstoken，里面包含openid和access_token
		String getAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wxe4e4c89c099df28b&secret=40987d82dd8feeb62a9216d3edb7b7da&code="
				+ code + "&grant_type=authorization_code";

		String accessToken = HttpClientUtils.getMethod(getAccessTokenUrl);
		System.out.println("getAccessTokenUrl====" + accessToken);

		// 把相应回来的json字符串转换成json格式，用户键值对获取里面具体的值
		JSONObject openIdJson = JSONObject.fromObject(accessToken);

		// 获取openid
		String openid = (String) openIdJson.get("openid");
		System.out.println("openid===" + openid);
		// 获取access_token
		String access_token = (String) openIdJson.get("access_token");
		System.out.println("access_token===" + access_token);

		// 用户信息返回，获得一个用户信息的json串
		String wechatUserInfo = getWechatUserInfo(access_token, openid);

		// 把用户信息插入数据库中

		// 返回页面，进入个人中心里

		try {
			response.sendRedirect("http://www.lizexing.cn/shopping/page/goods/wechatPay.html?#openId=" + openid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * <pre>getWechatUserInfo(获取微信用户信息)   
	 * 创建人：李泽兴 lizexing@163.com      
	 * 创建时间：2018年5月29日 下午11:48:49    
	 * 修改人：李泽兴 lizexing@163.com       
	 * 修改时间：2018年5月29日 下午11:48:49    
	 * 修改备注： 
	 * @param access_token
	 * @param openid
	 * @return</pre>
	 */
	public String getWechatUserInfo(String access_token, String openid) {

		// 拼接获取用户信息的地址，再httpClient请求下
		String getUserInfo = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openid
				+ "&lang=zh_CN";

		String userInfo = HttpClientUtils.getMethod(getUserInfo);
		System.out.println("userInfo====" + userInfo);
		return userInfo;
	}
	/**
	 * <pre>wechatOrder(调用微信统一下单接口)   
	 * 创建人：李泽兴 lizexing@163.com      
	 * 创建时间：2018年5月29日 下午11:49:01    
	 * 修改人：李泽兴 lizexing@163.com       
	 * 修改时间：2018年5月29日 下午11:49:01    
	 * 修改备注： 
	 * @param id
	 * @param request
	 * @return</pre>
	 */
	@RequestMapping("wechatOrder")
	@ResponseBody
	public String wechatOrder(@RequestParam("id")String id,HttpServletRequest request) {
		String openid = (String) request.getSession().getAttribute("openid");
		String result = wechatService.wechatOrder(id,openid);
		return result;
	}
	/**
	 * <pre>wechatNotify(支付完成后微信回调通知)   
	 * 创建人：李泽兴 lizexing@163.com      
	 * 创建时间：2018年5月29日 下午11:49:46    
	 * 修改人：李泽兴 lizexing@163.com       
	 * 修改时间：2018年5月29日 下午11:49:46    
	 * 修改备注： 
	 * @param request
	 * @param response</pre>
	 */
	@RequestMapping("wechatNotify")
	public void wechatNotify(HttpServletRequest request,HttpServletResponse response) {
		 
		 System.out.println("===wechatNotifywechatNotifywechatNotify===");
		 
		 try {
			InputStream inStream = request.getInputStream();
			    ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
			    byte[] buffer = new byte[1024];
			    int len = 0;
			    while ((len = inStream.read(buffer)) != -1) {
			        outSteam.write(buffer, 0, len);
			    }
			    outSteam.close();
			    inStream.close();
			    String result = new String(outSteam.toByteArray(), "utf-8");
			    Map<String, String> map = null;
			    try {
			        map = XMLUtil.doXMLParse(result);
			    } catch (JDOMException e) {
			        e.printStackTrace();
			    }
			    // 此处调用订单查询接口验证是否交易成功
			    boolean isSucc = wechatService.checkPayMessage(map);
			    
			    // 支付成功，商户处理后同步返回给微信参数
			    PrintWriter writer = response.getWriter();
			    if (!isSucc) {
			        // 支付失败， 记录流水失败
			    	logger.info("===============支付失败==============");
			    } else {
			    	logger.info("===============付款成功，业务处理完毕==============");
			        // 通知微信已经收到消息，不要再给我发消息了，否则微信会8连击调用本接口
			        String noticeStr = WechatConfig.setXML("SUCCESS", "");
			        writer.write(noticeStr);
			        writer.flush();
			    }
			    String noticeStr = WechatConfig.setXML("FAIL", "");
			    writer.write(noticeStr);
			    writer.flush();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * <pre>getWechatOrderStatus(根据商品id把商品的待支付状态拿回来)   
	 * 创建人：李泽兴 lizexing@163.com      
	 * 创建时间：2018年5月30日 上午10:51:53    
	 * 修改人：李泽兴 lizexing@163.com       
	 * 修改时间：2018年5月30日 上午10:51:53    
	 * 修改备注： 
	 * @param request
	 * @param response
	 * @param goodsId
	 * @return</pre>
	 */
	@RequestMapping("getWechatOrderStatus")
	public String getWechatOrderStatus(HttpServletRequest request,HttpServletResponse response,
			@RequestParam("goodsId")String goodsId) {
		
		String openid = (String) request.getSession().getAttribute("openid");
		String result = "";//orderService.getOrderStatus(openid,goodsId);
		
		/**
		 * 调用接口，查询微信订单状态的接口，看看是否已经支付过了，
		 * 如果支付过了，返回返回支付成功，开始执行微信支付完成后未通知成功方法里的处理方式
		 * 如果未支付，返回数据库中的数据到前台让用户支付
		 * 查询订单接口：https://api.mch.weixin.qq.com/pay/orderquery
		 */
		
		return result;
	}
	/**
	 * <pre>getRePay(根据商品id查找回待支付订单的信息，进行支付操作)   
	 * 创建人：李泽兴 lizexing@163.com      
	 * 创建时间：2018年5月30日 上午11:03:46    
	 * 修改人：李泽兴 lizexing@163.com       
	 * 修改时间：2018年5月30日 上午11:03:46    
	 * 修改备注： 
	 * @param request
	 * @param response
	 * @param goodsId
	 * @return</pre>
	 */
	@RequestMapping("getRePay")
	public String getRePay(HttpServletRequest request,HttpServletResponse response,
			@RequestParam("goodsId")String goodsId) {
		
		String openid = (String) request.getSession().getAttribute("openid");
		String result = "";//orderService.getRePay(openid,goodsId);
		return result;
	}

}
