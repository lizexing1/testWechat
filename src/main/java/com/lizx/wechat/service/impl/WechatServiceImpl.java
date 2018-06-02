package com.lizx.wechat.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import org.jdom2.JDOMException;

import com.lizx.utils.CommonUtil;
import com.lizx.utils.WechatConfig;
import com.lizx.utils.XMLUtil;
import com.lizx.wechat.service.WechatService;

import net.sf.json.JSONObject;

public class WechatServiceImpl implements WechatService{

	@Override
	public String wechatOrder(String id,String openid) {
		/**
		 * 从数据库查出来的订单号，或是从前台传入进来的订单号，并且根据id查出来价格，
		 * 跟我传入价格进行比对，防止用户传入的价格有问题
		 */
		//这是一个订单号，应该从数据库查询出来的
		String out_trade_no = UUID.randomUUID().toString().substring(0, 30);
		
		String nonceStr = WechatConfig.CreateNoncestr();
		String body = "JSAPI支付测试";
		String trade_type = "JSAPI";
		
		/**
		 * 生成签名前的准备
		 */
		SortedMap<Object,Object> parameters = new TreeMap<Object,Object>();
	    parameters.put("appid", WechatConfig.APPID);
	    parameters.put("mch_id", WechatConfig.MCH_ID);
	    parameters.put("spbill_create_ip", WechatConfig.SPBILL_CREATE_IP);
	    parameters.put("nonce_str", nonceStr);
	    parameters.put("openid", openid);
	    parameters.put("body", body);
	    parameters.put("out_trade_no", out_trade_no);
	    parameters.put("total_fee", "100");
	    parameters.put("notify_url", WechatConfig.NOTIFY_URL);
	    parameters.put("trade_type", "JSAPI");
		//生成签名
		String sign= WechatConfig.createSign(parameters);
		
		parameters.put("sign", sign);
		
		String requestXml = WechatConfig.getRequestXml(parameters);
		
		String orderUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
		
		System.out.println("stringBuffer===="+requestXml);
		
		String preparyIdXml = CommonUtil.httpRequestResult(orderUrl, "POST", requestXml);
		
		 Map<String, String> prepayIdMap =new HashMap<String, String>();
	    try {
	    	//
	    	prepayIdMap = XMLUtil.doXMLParse(preparyIdXml);
	    } catch (JDOMException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    System.out.println("****************************"+prepayIdMap.toString());
	    
	    String prepayId = "";
	    //解析微信返回的信息，以Map形式存储便于取值
	    if (null != prepayIdMap) {
	    	prepayId =prepayIdMap.get("prepay_id");
		}
	    System.out.println("============prepayId==prepayId===prepayId======"+prepayId);
		
	    /**
	     * 给前台返回参数，让前台支付
	     */
	    JSONObject jsonObject = new JSONObject();
	    //时间戳得转换成秒
	    long timeStamp = System.currentTimeMillis() /1000;
	    String newNonceStr = WechatConfig.CreateNoncestr();
	    //重新生成一个签名，否则签名无效，必须新生成一个签名
	    SortedMap<Object,Object> signParams = new TreeMap<Object,Object>();  
	    signParams.put("appId", WechatConfig.APPID);  
	    signParams.put("nonceStr",newNonceStr);  
	    signParams.put("package", "prepay_id="+prepayId);  
	    signParams.put("timeStamp", timeStamp);  
	    signParams.put("signType", "MD5");  
		String newSign = WechatConfig.createSign(signParams);
	    
		System.out.println("newSignnewSignnewSign="+newSign);
		
		
		System.out.println("newSign.toString()--"+newSign);
		
	    jsonObject.put("appId", WechatConfig.APPID);
	    jsonObject.put("timeStamp", String.valueOf(timeStamp));
	    jsonObject.put("nonceStr", newNonceStr);
	    jsonObject.put("packageVal", "prepay_id="+prepayId);
	    jsonObject.put("paySign", newSign);
	    
	    //调用service往数据库中插入支付字段
	    
	    
		return jsonObject.toString();
	}

	@Override
	public boolean checkPayMessage(Map<String, String> map) {
		//商品id拿到
	    String orderId = map.get("out_trade_no");
	    //商品价格
        String totalFee = map.get("total_fee");
        //商品的状态
        String returnCode = map.get("return_code");
        //返回一个用户的openId
        String openid = map.get("openid");
        /**
         * 拿着返回的orderId去order表里查数据，并拿回用户的openId，目的是防止支付金额与订单本身金额不一致
         */
        Map<String, Object> orderMap = null;//this.findById(orderId);
		String rorderId = (String) orderMap.get("orderId");
		int price = (int) orderMap.get("price");
		int openId = (int) orderMap.get("openId");
		
		//判断，如果支付人和支付的订单和支付的价格全部都不一致，说明支付条件不成立，执行下面操作，返回错误
		if(!orderId.equals(rorderId) && !totalFee.equals(price) && !openid.equals(openId)) {
			return false;
		}
		
		String doctorName = "";
		String doctorMobile = "";
		String patientName = "";
		//根据订单id拿到患者手机号、患者姓名和医生姓名
//		Map<String, Object> paidQuestion = iPaidQuestionMapper.getByQuestionid(rorderId);
		Map<String, Object> paidQuestion = new HashMap<String, Object>();
		if(paidQuestion!=null) {
			doctorName = (String) paidQuestion.get("doctorName");
			doctorMobile = (String) paidQuestion.get("doctorMobile");
			patientName = (String) paidQuestion.get("patientName");
		}
		
        if(returnCode.equals("SUCCESS") && orderId.equals(rorderId) && totalFee.equals(String.valueOf(price))){
        	//调用云之讯接口 发送验证码
        	//短信发送内容是：刘医生您好，患者小明向您发起了咨询，请您及时为小明诊断。
//        	smsUtil.patientAdvisorySendNotify(doctorMobile, patientName, doctorName);
//        	smsUtil.patientAdvisorySendNotify("13243432764", patientName, "刘老师");
        	return true;
        }else{
        	return false;
        }
	}

}
