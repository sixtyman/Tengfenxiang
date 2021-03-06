package com.android.tengfenxiang.bean;

/**
 * 用户实体类
 * 
 * @author ccz
 * 
 */
public class User {

	/**
	 * 用户的id
	 */
	private int id;

	/**
	 * 用户昵称
	 */
	private String nickName;

	/**
	 * 手机号码
	 */
	private String phone;

	/**
	 * 支付宝账号
	 */
	private String alipay;

	/**
	 * 头像URL地址
	 */
	private String avatar;

	/**
	 * 性别
	 */
	private int gender;

	/**
	 * 省份
	 */
	private int province;

	/**
	 * 城市
	 */
	private int city;

	/**
	 * 区
	 */
	private int district;

	/**
	 * 街道信息
	 */
	private String streetInfo;

	/**
	 * 微信账号
	 */
	private String wechat;

	/**
	 * QQ账号
	 */
	private String qq;

	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 邀请码
	 */
	private String inviteCode;

	/**
	 * 在用户访问页面的时候标识用户
	 */
	private String token;

	/**
	 * 获取签到状态接口返回格式
	 */
	private SigninStatus signinStatus;

	/**
	 * 当前可提现的金额
	 */
	private int withdrawableCash;

	public User() {

	}

	public User(int id, String nickName, String phone, String alipay,
			String avatar, int gender, int province, int city, int district,
			String streetInfo, String wechat, String qq, String email,
			String inviteCode, int withdrawableCash) {
		super();
		this.id = id;
		this.nickName = nickName;
		this.phone = phone;
		this.alipay = alipay;
		this.avatar = avatar;
		this.gender = gender;
		this.province = province;
		this.city = city;
		this.district = district;
		this.streetInfo = streetInfo;
		this.wechat = wechat;
		this.qq = qq;
		this.email = email;
		this.inviteCode = inviteCode;
		this.withdrawableCash = withdrawableCash;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAlipay() {
		return alipay;
	}

	public void setAlipay(String alipay) {
		this.alipay = alipay;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getProvince() {
		return province;
	}

	public void setProvince(int province) {
		this.province = province;
	}

	public int getCity() {
		return city;
	}

	public void setCity(int city) {
		this.city = city;
	}

	public String getWechat() {
		return wechat;
	}

	public void setWechat(String wechat) {
		this.wechat = wechat;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getInviteCode() {
		return inviteCode;
	}

	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public SigninStatus getSigninStatus() {
		return signinStatus;
	}

	public void setSigninStatus(SigninStatus signinStatus) {
		this.signinStatus = signinStatus;
	}

	public int getWithdrawableCash() {
		return withdrawableCash;
	}

	public void setWithdrawableCash(int withdrawableCash) {
		this.withdrawableCash = withdrawableCash;
	}

	public int getDistrict() {
		return district;
	}

	public void setDistrict(int district) {
		this.district = district;
	}

	public String getStreetInfo() {
		return streetInfo;
	}

	public void setStreetInfo(String streetInfo) {
		this.streetInfo = streetInfo;
	}

}