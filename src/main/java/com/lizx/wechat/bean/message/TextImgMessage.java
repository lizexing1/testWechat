package com.lizx.wechat.bean.message;
/**
 * 
 * <pre>项目名称：efilm    
 * 类名称：TextImgMessage    
 * 类描述：    关注完成微信后推送模板信息
 * 创建人：李泽兴 lizexing@huiyihuiying.com    
 * 创建时间：2017年6月8日 下午4:43:40    
 * 修改人：李泽兴 lizexing@huiyihuiying.com
 * 修改时间：2017年6月8日 下午4:43:40    
 * 修改备注：       
 * @version </pre>
 */
public class TextImgMessage extends BaseMessage{
	
	private String title;
	private String description;
	private String picUrl;
	private String url;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String toXML() {
		
		StringBuffer xml = new StringBuffer();
		xml.append("<xml>");
		xml.append(super.toXML());
		xml.append("<ArticleCount>1</ArticleCount>");
		xml.append("<Articles>");
		xml.append("<item>");
		xml.append("<Title><![CDATA["+title+"]]></Title> ");
		xml.append("<Description><![CDATA["+description+"]]></Description>");
		xml.append("<PicUrl><![CDATA["+picUrl+"]]></PicUrl>");
		xml.append("<Url><![CDATA["+url+"]]></Url>");
		xml.append("</item>");
		xml.append("</Articles>");
		xml.append("</xml>");
		
		System.out.println("**********************");
		System.out.println(xml.toString());
		System.out.println("**********************");
		return xml.toString();
	}
	
}
