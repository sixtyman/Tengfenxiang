package com.android.tengfenxiang.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.tengfenxiang.R;
import com.android.tengfenxiang.bean.Recommend;
import com.android.tengfenxiang.bean.Setting;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.receiver.SaveShareRecordReceiver;
import com.android.tengfenxiang.receiver.SaveShareRecordReceiver.OnSaveRecordsListener;
import com.android.tengfenxiang.util.BitmapCompressUtil;
import com.android.tengfenxiang.util.Constant;
import com.android.tengfenxiang.util.FormatTools;
import com.android.tengfenxiang.util.JsBridge;
import com.android.tengfenxiang.util.JsBridge.JsBridgeListener;
import com.android.tengfenxiang.util.NetworkUtil;
import com.android.tengfenxiang.util.RequestUtil;
import com.android.tengfenxiang.util.ResponseUtil;
import com.android.tengfenxiang.util.VolleyErrorUtil;
import com.android.tengfenxiang.view.dialog.SharePopupWindow;
import com.android.tengfenxiang.view.titlebar.TitleBar;
import com.android.tengfenxiang.view.titlebar.TitleBar.OnTitleClickListener;
import com.android.tengfenxiang.view.webview.X5WebView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class X5WebActivity extends BaseActivity implements JsBridgeListener {

	private TitleBar titleBar;
	private X5WebView webView;
	private WebChromeClient webChromeClient;
	private RelativeLayout webviewLayout;

	private Setting setting;
	private SharedPreferences preferences;
	private String appId;

	/**
	 * activity显示的标题
	 */
	private String title;

	/**
	 * 要打开的网页
	 */
	private String url;

	/**
	 * 如果是某个文章链接，则将文章id保存起来
	 */
	private int articleId;

	/**
	 * 如果是某个任务链接，则将任务id保存起来
	 */
	private int taskId;

	/**
	 * 分享到微信时候显示的标题
	 */
	private String webTitle;

	/**
	 * 分享到微信时候显示的内容
	 */
	private String webContent;

	/**
	 * 缩略图，用于显示在分享对话框
	 */
	private Bitmap imageBitmap;

	/**
	 * 缩略图路径
	 */
	private String thumbnails;

	/**
	 * 微信API的实例对象
	 */
	private IWXAPI wxApi;

	/**
	 * 弹出菜单
	 */
	private SharePopupWindow window;

	/**
	 * 记录分享的类型，朋友圈或微信好友
	 */
	private String destination;

	/**
	 * 广播管理对象
	 */
	private LocalBroadcastManager localBroadcastManager;

	/**
	 * 广播过滤
	 */
	private IntentFilter intentFilter;

	/**
	 * 保存分享信息的广播接收器
	 */
	private SaveShareRecordReceiver receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 避免视频时候闪烁
		this.getWindow().setFormat(PixelFormat.TRANSLUCENT);
		this.getWindow().requestFeature(Window.FEATURE_PROGRESS);

		setContentView(R.layout.vedio_web_activity);

		preferences = getSharedPreferences(getPackageName(),
				Context.MODE_PRIVATE);
		setting = application.getSetting();
		if (setting != null && setting.getSnsConfig() != null) {
			appId = application.getSetting().getSnsConfig().getWechatKey();
		} else {
			appId = preferences.getString("wechatKey",
					Constant.DEFAULT_WX_APP_ID);
		}
		wxApi = WXAPIFactory.createWXAPI(this, appId);
		wxApi.registerApp(appId);

		Intent intent = getIntent();
		title = intent.getStringExtra("title");
		url = intent.getStringExtra("url");
		taskId = intent.getIntExtra("task_id", -1);
		articleId = intent.getIntExtra("article_id", -1);
		webTitle = intent.getStringExtra("web_title");
		webContent = intent.getStringExtra("web_content");
		thumbnails = intent.getStringExtra("image");

		initView();
		initReceiver();
	}

	private void initView() {
		webviewLayout = (RelativeLayout) findViewById(R.id.webview_layout);
		webView = (X5WebView) findViewById(R.id.web_filechooser);
		initWebView();

		titleBar = (TitleBar) findViewById(R.id.title_bar);
		titleBar.setOnClickListener(new OnTitleClickListener() {

			@Override
			public void OnClickRight() {
				if (NetworkUtil.isNetworkAvailable(getApplicationContext())) {
					window = new SharePopupWindow(X5WebActivity.this,
							itemsOnClick);
					window.showAtLocation(webView, Gravity.BOTTOM
							| Gravity.CENTER_HORIZONTAL, 0, 0);
				} else {
					showNetworkDialog();
				}
			}

			@Override
			public void OnClickLeft() {
				finish();
			}
		});
	}

	private void initReceiver() {
		intentFilter = new IntentFilter(Constant.SAVE_RETWEET_RECORD);
		receiver = new SaveShareRecordReceiver();
		receiver.setOnSaveRecordsListener(new OnSaveRecordsListener() {

			@Override
			public void onSaveShareRecords() {
				// TODO Auto-generated method stub
				saveRetweetRecord(destination);
			}
		});

		localBroadcastManager = LocalBroadcastManager.getInstance(this);
		localBroadcastManager.registerReceiver(receiver, intentFilter);
	}

	private JsBridge bridge;

	private void initWebView() {
		// TODO Auto-generated method stub
		if (null != url && !url.equals("")) {
			webView.getSettings().setSupportZoom(false);
			webView.getSettings()
					.setJavaScriptCanOpenWindowsAutomatically(true);
			// 设置下载监听
			webView.setDownloadListener(new WebViewDownLoadListener());
			bridge = new JsBridge(application);
			bridge.setJsBridgeListener(this);
			webView.addJavascriptInterface(bridge, "androidJSUtil");
			webView.setWebViewClient(new WebViewClient() {

				@Override
				public void onPageStarted(WebView view, String url,
						Bitmap favicon) {
					super.onPageStarted(view, url, favicon);
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					// 如果当前还没有加载缩略图，则需要加载新闻缩略图
					if (null == imageBitmap) {
						loadThumbnails(thumbnails);
					}
				}

				@Override
				public void onReceivedError(WebView view, int errorCode,
						String description, String failingUrl) {
					Toast.makeText(getApplication(),
							R.string.fail_to_load_webpage, Toast.LENGTH_SHORT)
							.show();
				}

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.loadUrl(url);
					return true;
				}
			});
			initChromeClient();
			webView.loadUrl(url);
		}
	}

	/**
	 * 初始化ChromeClient
	 */
	private void initChromeClient() {
		// TODO Auto-generated method stub
		webChromeClient = new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int progress) {
				titleBar.setTitleText(getString(R.string.web_loading)
						+ progress + "%");
				setProgress(progress * 100);
				if (progress == 100) {
					titleBar.setTitleText(title);
				}
			}
		};
		webView.setWebChromeClient(webChromeClient);
	}

	/**
	 * 为弹出窗口实现监听类
	 */
	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			window.dismiss();
			switch (v.getId()) {
			case R.id.wechat_btn:
				wechatShare(0);
				destination = "wechat_friend";
				break;
			case R.id.moment_btn:
				wechatShare(1);
				destination = "wechat_moment";
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 保存分享记录到后台
	 * 
	 * @param destination
	 */
	private void saveRetweetRecord(String destination) {
		if (taskId == -1 && articleId == -1) {
			return;
		}
		if (taskId == -1) {
			saveArticleRetweet(application.getCurrentUser().getId(), articleId,
					destination);
		} else {
			saveTaskRetweet(application.getCurrentUser().getId(), taskId,
					destination);
		}
	}

	/**
	 * 微信分享
	 * 
	 * @param flag
	 *            0:分享到微信好友，1：分享到微信朋友圈
	 */
	private void wechatShare(int flag) {
		// 网页对象
		WXWebpageObject webpage = new WXWebpageObject();
		// 分享的网页链掿
		webpage.webpageUrl = url;

		WXMediaMessage msg = new WXMediaMessage(webpage);
		// 分享对话框显示的标题
		msg.title = webTitle;
		// 分享对话框显示的文字内容
		msg.description = webContent;
		// 分享对话框显示的图片
		if (null != imageBitmap) {
			msg.thumbData = Util.bmpToByteArray(imageBitmap, false);
		} else {
			msg.thumbData = FormatTools.getInstance().Drawable2Bytes(
					getResources().getDrawable(R.drawable.ic_launcher));
		}

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = String.valueOf(System.currentTimeMillis());
		req.message = msg;
		req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession
				: SendMessageToWX.Req.WXSceneTimeline;
		wxApi.sendReq(req);
	}

	/**
	 * 文章分享记录
	 * 
	 * @param userId
	 *            用户ID
	 * @param articleId
	 *            文章ID
	 * @param destination
	 *            分享的类垿
	 */
	private void saveArticleRetweet(final int userId, final int articleId,
			final String destination) {

		String postUrl = Constant.ARTICLE_RETWEET_URL;
		// 请求成功的回调函敿
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				Object result = ResponseUtil.handleResponse(getApplication(),
						response, null);
				if (null != result) {
					Toast.makeText(getApplication(), R.string.share_success,
							Toast.LENGTH_SHORT).show();
				}
			}
		};
		// 请求失败的回调函敿
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyErrorUtil.handleVolleyError(getApplication(), error);
			}
		};
		StringRequest stringRequest = new StringRequest(Method.POST, postUrl,
				listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", userId + "");
				map.put("articleId", articleId + "");
				map.put("destination", destination);
				return map;
			}
		};
		RequestUtil.getRequestQueue(getApplication()).add(stringRequest);
	}

	/**
	 * 保存任务分享记录
	 * 
	 * @param userId
	 *            用户ID
	 * @param taskId
	 *            任务ID
	 * @param destination
	 *            分享的类垿
	 */
	private void saveTaskRetweet(final int userId, final int taskId,
			final String destination) {

		String postUrl = Constant.TASK_RETWEET_URL;
		// 请求成功的回调函敿
		Listener<String> listener = new Listener<String>() {
			@Override
			public void onResponse(String response) {
				Object result = ResponseUtil.handleResponse(getApplication(),
						response, null);
				if (null != result) {
					Toast.makeText(getApplication(), R.string.share_success,
							Toast.LENGTH_SHORT).show();
				}
			}
		};
		// 请求失败的回调函敿
		ErrorListener errorListener = new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyErrorUtil.handleVolleyError(getApplication(), error);
			}
		};
		StringRequest stringRequest = new StringRequest(Method.POST, postUrl,
				listener, errorListener) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", userId + "");
				map.put("taskId", taskId + "");
				map.put("destination", destination);
				return map;
			}
		};
		RequestUtil.getRequestQueue(getApplication()).add(stringRequest);
	}

	/**
	 * 加载缩略图
	 * 
	 * @param imageUrl
	 *            缩略图URL
	 */
	private void loadThumbnails(String imageUrl) {
		if (null != imageUrl && !imageUrl.equals("")) {
			Glide.with(this.getApplicationContext()).load(imageUrl).asBitmap()
					.centerCrop().into(new SimpleTarget<Bitmap>() {
						@Override
						public void onResourceReady(Bitmap arg0,
								GlideAnimation<? super Bitmap> arg1) {
							// TODO Auto-generated method stub
							imageBitmap = BitmapCompressUtil.compressImage(
									arg0, 32);
						}

						@Override
						public void onLoadFailed(Exception e,
								Drawable errorDrawable) {
							// TODO Auto-generated method stub
							super.onLoadFailed(e, errorDrawable);
							Toast.makeText(getApplication(),
									R.string.fail_to_load_webpage,
									Toast.LENGTH_SHORT).show();
						}
					});
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 注销广播
		localBroadcastManager.unregisterReceiver(receiver);
		// 在退出前先销毁WebView
		if (null != webView) {
			webviewLayout.removeView(webView);
			webView.removeAllViews();
			webView.destroy();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void getAppVersion() {
		// TODO Auto-generated method stub

	}

	@Override
	public void shareWechat() {
		// TODO Auto-generated method stub
		wechatShare(0);
		destination = "wechat_friend";
	}

	@Override
	public void shareWechatSession() {
		// TODO Auto-generated method stub
		wechatShare(1);
		destination = "wechat_moment";
	}

	@Override
	public void showRecommend(String data) {
		// TODO Auto-generated method stub
		Recommend recommend = JSON.parseObject(data, Recommend.class);
		if (null != recommend) {
			User user = application.getCurrentUser();
			Intent intent = new Intent(X5WebActivity.this, X5WebActivity.class);
			intent.putExtra("title", getString(R.string.article_detail));
			String url = recommend.getUrl();
			if (null != user.getToken() && !user.getToken().equals("")) {
				url = url + "&token=" + user.getToken();
			}
			intent.putExtra("url", url);
			intent.putExtra("article_id", recommend.getInfoId());
			intent.putExtra("web_title", recommend.getTitle());
			intent.putExtra("web_content", recommend.getIntro());
			intent.putExtra("image", recommend.getThumbnails());
			startActivity(intent);
			finish();
		}
	}

	/**
	 * 下载监听，如果webview要执行下载操作则跳转系统浏览器
	 * 
	 * @author 陈楚昭
	 * 
	 */
	class WebViewDownLoadListener implements DownloadListener {

		@Override
		public void onDownloadStart(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength) {
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
	}

	private void showNetworkDialog() {
		Builder builder = new Builder(this);
		builder.setMessage(R.string.network_error_dialog_message);
		builder.setTitle(R.string.dialog_title);
		builder.setPositiveButton(R.string.setting_network,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent(
								Settings.ACTION_DATA_ROAMING_SETTINGS);
						startActivity(intent);
					}
				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}
}