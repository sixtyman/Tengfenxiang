package com.android.tengfenxiang.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.tengfenxiang.R;
import com.android.tengfenxiang.activity.X5WebActivity;
import com.android.tengfenxiang.bean.Article;
import com.android.tengfenxiang.bean.User;
import com.android.tengfenxiang.view.viewpager.DynamicPagerAdapter;
import com.bumptech.glide.Glide;

public class BannerAdapter extends DynamicPagerAdapter {

	private List<Article> banners;
	private Activity context;
	private User user;

	public BannerAdapter(Activity context, List<Article> banners, User user) {
		this.banners = banners;
		this.context = context;
		this.user = user;
	}

	@Override
	public View getView(ViewGroup container, final int position) {
		ImageView view = new ImageView(container.getContext());
		view.setScaleType(ImageView.ScaleType.FIT_XY);
		view.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		// 利用缓存框架加载图片
		Glide.with(context.getApplicationContext())
				.load(banners.get(position).getThumbnails()).centerCrop()
				.crossFade().into(view);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context, X5WebActivity.class);
				intent.putExtra("title",
						context.getString(R.string.article_detail));
				String url = banners.get(position).getShareUrl();
				if (null != user.getToken() && !user.getToken().equals("")) {
					url = url + "&token=" + user.getToken();
				}
				intent.putExtra("url", url);
				intent.putExtra("article_id", banners.get(position).getId());
				intent.putExtra("web_title", banners.get(position).getTitle());
				intent.putExtra("web_content", banners.get(position)
						.getContent());
				intent.putExtra("image", banners.get(position).getThumbnails());
				context.startActivity(intent);
			}
		});
		return view;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return banners.size();
	}

}