package com.android.tengfenxiang.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.alibaba.fastjson.JSON;
import com.android.tengfenxiang.R;
import com.android.tengfenxiang.adapter.WithdrawListAdapter;
import com.android.tengfenxiang.application.MainApplication;
import com.android.tengfenxiang.bean.ResponseResult;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.bean.Withdraw;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.RequestManager;
import com.android.tengfenxiang.view.dialog.LoadingDialog;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;
import com.android.tengfenxiang.view.xlistview.XListView;
import com.android.tengfenxiang.view.xlistview.XListView.IXListViewListener;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class WithdrawActivity extends Activity implements IXListViewListener {

	private TitleBar titleBar;
	private XListView recordsList;
	private LoadingDialog dialog;

	private User currentUser;

	/**
	 * 每页显示10条数据
	 */
	private final static int limit = 10;
	/**
	 * 偏移多少条数据
	 */
	private int offset = 0;
	private List<Withdraw> withdraws;
	private WithdrawListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.withdraw_records);

		MainApplication application = ((MainApplication) getApplication());
		currentUser = application.getCurrentUser();

		withdraws = new ArrayList<Withdraw>();
		dialog = new LoadingDialog(this);
		dialog.showDialog();
		getWithdrawRecords(currentUser.getId(), limit, offset);
	}

	private void initView() {
		recordsList = (XListView) findViewById(R.id.records_list);
		recordsList.setXListViewListener(this);
		adapter = new WithdrawListAdapter(WithdrawActivity.this, withdraws);
		recordsList.setAdapter(adapter);

		titleBar = (TitleBar) findViewById(R.id.title_bar);
		titleBar.setOnClickListener(new OnTitleClickListener() {

			@Override
			public void OnClickLeft() {
				finish();
			}

			@Override
			public void OnClickRight() {

			}
		});
	}

	private void getWithdrawRecords(int userId, int limit, int offset) {
		String url = Constant.WITHDRAW_LIST_URL + "?userId=" + userId
				+ "&limit=" + limit + "&offset=" + offset;

		// 请求成功的回调函数
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				ResponseResult result = JSON.parseObject(response,
						ResponseResult.class);
				List<Withdraw> tmp = JSON.parseArray(result.getData()
						.toString(), Withdraw.class);
				withdraws.addAll(tmp);
				if (dialog.isShowing()) {
					dialog.cancelDialog();
					initView();
				}
				loadComplete();
			}
		};
		// 请求失败的回调函数
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				if (dialog.isShowing()) {
					dialog.cancelDialog();
				}
				Toast.makeText(getApplication(), R.string.unknow_error,
						Toast.LENGTH_SHORT).show();
				finish();
			}
		};

		StringRequest request = new StringRequest(url, listener, errorListener);
		RequestManager.getRequestQueue().add(request);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		offset = 0;
		withdraws.clear();
		getWithdrawRecords(currentUser.getId(), limit, offset);
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		offset = offset + limit;
		getWithdrawRecords(currentUser.getId(), limit, offset);
	}

	/**
	 * 刷新完成的回调
	 */
	private void loadComplete() {
		recordsList.stopRefresh();
		recordsList.stopLoadMore();
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss",
				Locale.CHINA);
		Date curDate = new Date(System.currentTimeMillis());
		recordsList.setRefreshTime(formatter.format(curDate));
	}

}