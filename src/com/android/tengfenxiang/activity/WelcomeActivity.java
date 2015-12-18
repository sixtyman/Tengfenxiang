package com.android.tengfenxiang.activity;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.android.tengfenxiang.R;
import com.android.tengfenxiang.bean.ResponseResult;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.DeviceInfoUtil;
import com.android.tengfenxiang.util.RequestUtil;
import com.android.tengfenxiang.util.ResponseUtil;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class WelcomeActivity extends BaseActivity {

	private final int SPLASH_DISPLAY_LENGHT = 2000;
	private SharedPreferences preferences;
	// private Editor editor;
	private LoadingDialog dialog;
	private TextView versionTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		initView();

		dialog = new LoadingDialog(this);
		preferences = getSharedPreferences(getPackageName(),
				Context.MODE_PRIVATE);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// 去除掉启动界面，如果后期需要启动界面
				// 则将这个类注释掉的代码还原，删除本段代码即可
				String phone = preferences.getString("phone", "");
				String password = preferences.getString("password", "");

				if (phone.equals("") || password.equals("")) {
					Intent intent = new Intent();
					intent.setClass(WelcomeActivity.this, LoginActivity.class);
					startActivity(intent);
					finish();
				} else {
					dialog.showDialog();
					login(phone, password);
				}
				// if (preferences.getBoolean("firststart", true)) {
				// editor = preferences.edit();
				// editor.putBoolean("firststart", false);
				// editor.commit();
				// Intent intent = new Intent();
				// intent.setClass(WelcomeActivity.this, IntrudeActivity.class);
				// startActivity(intent);
				// finish();
				// } else {
				// String phone = preferences.getString("phone", "");
				// String password = preferences.getString("password", "");
				//
				// if (phone.equals("") || password.equals("")) {
				// Intent intent = new Intent();
				// intent.setClass(WelcomeActivity.this,
				// LoginActivity.class);
				// startActivity(intent);
				// finish();
				// } else {
				// dialog.showDialog();
				// login(phone, password);
				// }
				// }
			}
		}, SPLASH_DISPLAY_LENGHT);
	}

	private void initView() {
		versionTextView = (TextView) findViewById(R.id.version);
		DeviceInfoUtil util = DeviceInfoUtil.getInstance(getApplication());
		versionTextView.setText(getString(R.string.version_text)
				+ util.getAppVersion());
	}

	private void login(final String phone, final String password) {
		String url = Constant.LOGIN_URL;
		// 请求成功的回调函数
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				if (dialog.isShowing()) {
					dialog.cancelDialog();
				}
				Intent intent = new Intent();
				ResponseResult result = JSON.parseObject(response,
						ResponseResult.class);

				// 如果返回code=200说明登录成功，跳转主页面
				if (null != result && result.getCode() == 200) {
					currentUser = (User) ResponseUtil.handleResponse(
							getApplication(), response, User.class);
					application.setCurrentUser(currentUser);
					intent.setClass(WelcomeActivity.this, MainActivity.class);
				} else {
					// 登录失败，给出提示并跳转到登录界面
					Toast.makeText(application, R.string.login_fail,
							Toast.LENGTH_SHORT).show();
					intent.setClass(WelcomeActivity.this, LoginActivity.class);
				}
				startActivity(intent);
				finish();
			}
		};
		// 请求失败的回调函数
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (dialog.isShowing()) {
					dialog.cancelDialog();
				}

				// 登录失败设置空的User对象
				currentUser = new User();
				application.setCurrentUser(currentUser);

				Intent intent = new Intent(WelcomeActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
				Toast.makeText(getApplication(), R.string.unknow_error,
						Toast.LENGTH_SHORT).show();
			}
		};
		StringRequest stringRequest = new StringRequest(Method.POST, url,
				listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				DeviceInfoUtil util = DeviceInfoUtil
						.getInstance(getApplication());
				map.put("phone", phone);
				map.put("password", password);
				map.put("deviceId", util.getDeviceId());
				map.put("deviceInfo", util.getDeviceInfo());
				if (util.getPushToken() != null
						&& !util.getPushToken().equals("")) {
					map.put("pushToken", util.getPushToken());
				}
				map.put("appVersion", util.getAppVersion());
				map.put("os", util.getOs());
				map.put("osVersion", util.getOsVersion());
				map.put("model", util.getModel());
				return map;
			}
		};
		RequestUtil.getRequestQueue(getApplication()).add(stringRequest);
	}
}