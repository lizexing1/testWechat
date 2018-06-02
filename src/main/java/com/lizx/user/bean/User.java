package com.lizx.user.bean;

import java.sql.Date;
/**
 * 
 * <pre>项目名称：1511BWechatTest    
 * 类名称：User    sdff
 * 类描述：    用户信息类，对应用户表
 * 创建人：李泽兴 lizexing@163.com    
 * 创建时间：2018年5月25日 下午7:36:18    
 * 修改人：李泽兴 lizexing@163.com       
 * 修改时间：2018年5月25日 下午7:36:18    
 * 修改备注：       
 * @version </pre>
 */
public class User {

	/****/
	private String id;
	/** 用户名	**/
	private String name;
	/**用户昵称**/
	private String nick_name;
	/**性别**/
	private int sex;
	/**年龄**/
	private int age;
	/**手机号	**/
	private String phone_num;
	/**用户积分	**/
	private int score;
	/**微信用户的唯一标识**/
	private String openid;
	/*权限字段，抽奖时使用***/
	private String auth;
	/**用户要进行逻辑删除，不能物理删除；0删除，1没删除**/
	private int status;
	/**用户注册时间**/
	private Date create_time;
	/**用户信息更新时间**/
	private Date update_time;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNick_name() {
		return nick_name;
	}
	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getPhone_num() {
		return phone_num;
	}
	public void setPhone_num(String phone_num) {
		this.phone_num = phone_num;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getAuth() {
		return auth;
	}
	public void setAuth(String auth) {
		this.auth = auth;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public Date getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}

	
}
