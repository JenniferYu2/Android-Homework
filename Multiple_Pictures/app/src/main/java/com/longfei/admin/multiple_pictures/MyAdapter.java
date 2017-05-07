package com.longfei.admin.multiple_pictures;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


public class MyAdapter extends CommonAdapter<String>
{

	/**
	 * 用户选择的图片，存储为图片的完整路径
	 */
	public static List<String> mSelectedImage = new LinkedList<String>();
	private int maxPicSize;

	/**
	 * 文件夹路径
	 */
	private String mDirPath;

	public MyAdapter(Context context, List<String> mDatas, int itemLayoutId,
			String dirPath, String maxPicSize)
	{
		super(context, mDatas, itemLayoutId);
		this.mDirPath = dirPath;
		mSelectedImage.removeAll(mSelectedImage);
		this.maxPicSize = maxPicSize == null? 0 : Integer.parseInt(maxPicSize); 
		Toast.makeText(mContext, "一次只能选择"+this.maxPicSize+"张图片", Toast.LENGTH_LONG).show();
	}

	@Override
	public void convert(final ViewHolder helper, final String item)
	{
		//设置no_pic
		helper.setImageResource(ImageSelectActivity.id_item_image, ImageSelectActivity.pictures_no);
		//设置no_selected
				helper.setImageResource(ImageSelectActivity.id_item_select,
						ImageSelectActivity.picture_unselected);
		//设置图片
		helper.setImageByUrl(ImageSelectActivity.id_item_image, mDirPath + "/" + item);
		
		final ImageView mImageView = helper.getView(ImageSelectActivity.id_item_image);
		final ImageView mSelect = helper.getView(ImageSelectActivity.id_item_select);
			
		mImageView.setColorFilter(null);
		//设置ImageView的点击事件
		mImageView.setOnClickListener(new OnClickListener()
		{
			//选择，则将图片变暗，反之则反之
			@Override
			public void onClick(View v)
			{

				// 已经选择过该图片
				if (mSelectedImage.contains(mDirPath + "/" + item))
				{
					mSelectedImage.remove(mDirPath + "/" + item);
					mSelect.setImageResource(ImageSelectActivity.picture_unselected);
					mImageView.setColorFilter(null);
				} else
				// 未选择该图片,可限制图片张数
				{
					if(mSelectedImage.size() < maxPicSize){
					mSelectedImage.add(mDirPath + "/" + item);
					mSelect.setImageResource(ImageSelectActivity.pictures_selected);
					mImageView.setColorFilter(Color.parseColor("#77000000"));
					}else{
						Toast.makeText(mContext, "你已选择"+maxPicSize+"张图片", Toast.LENGTH_LONG).show();
					}
				}

			}
		});
		//选择过的图片下次还会显示
		
		/**
		 * 已经选择过的图片，显示出选择过的效果*/
		 
		if (mSelectedImage.contains(mDirPath + "/" + item))
		{
			mSelect.setImageResource(ImageSelectActivity.pictures_selected);
			mImageView.setColorFilter(Color.parseColor("#77000000"));
		
		}

	}
}
