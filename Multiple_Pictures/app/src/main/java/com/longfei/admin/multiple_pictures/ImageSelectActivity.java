package com.longfei.admin.multiple_pictures;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class ImageSelectActivity extends Activity implements ListImageDirPopupWindow.OnImageDirSelected
{
	@Override
	public boolean shouldShowRequestPermissionRationale(String permission) {
		// TODO Auto-generated method stub
		return super.shouldShowRequestPermissionRationale(permission);
	}
	
	

	private ProgressDialog mProgressDialog;

	//layout
	public static int grid_item,list_dir_item,list_dir,main,titlebtn;
	public static int id_list_dir,id_dir_item_image,id_dir_item_count,id_dir_item_name,id_item_image,id_item_select,id_titlebtn_check,id_titlebtn_cancel; 
	public static int pictures_selected,pictures_no,picture_unselected;

	/**
	 * 图片最大选择数
	 */
	private int mPicsSize;
	
	private String maxPicSize = "10"; // the default is 10
	/**
	 * 图片路径
	 */
	private File mImgDir;
	/**
	 * 保存图片路径
	 */
	private List<String> mImgs;

	private GridView mGirdView;
	private MyAdapter mAdapter;

	private HashSet<String> mDirPaths = new HashSet<String>();

	/**
	 *保存文件夹头文件
	 */
	private List<CommonAdapter.ImageFloder> mImageFloders = new ArrayList<CommonAdapter.ImageFloder>();

	private RelativeLayout mBottomLy;
	private TextView mOkImageButton;
	private ImageButton mCancelImageButton;

	private TextView mChooseDir;
	private TextView mImageCount;
	int totalCount = 0;

	private int mScreenHeight;

	private ListImageDirPopupWindow mListImageDirPopupWindow;
	
	private static String[] PERMISSIONS_GROUP = {
	      Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
	  };
	
	  // 状态码、标志位
	private static final int REQUEST_STATUS_CODE = 0x001;

	public int getViewId(){
		
		return id_list_dir;
	}

	//线程加载图片
	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			mProgressDialog.dismiss();
			data2View();
			initListDirPopupWindw();
		}
	};

	/**
	 * 加载图片
	 */
	private void data2View()
	{
		if (mImgDir == null)
		{
			Toast.makeText(getApplicationContext(), "\u64e6\uff0c\u4e00\u5f20\u56fe\u7247\u6ca1\u626b\u63cf\u5230",
					Toast.LENGTH_SHORT).show();
			return;
		}
		grid_item = getResources().getIdentifier("grid_item", "layout", getPackageName());//=findViewById(R.layout.grid_item)
		mImgs = Arrays.asList(mImgDir.list());

		mAdapter = new MyAdapter(getApplicationContext(), mImgs,
				grid_item, mImgDir.getAbsolutePath(), maxPicSize);
		mGirdView.setAdapter(mAdapter);
		mImageCount.setText(totalCount + "\u5f20");
	};

	/**
	 * 弹出框
	 */
	private void initListDirPopupWindw()
	{
		mListImageDirPopupWindow = new ListImageDirPopupWindow(
				LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
				mImageFloders, LayoutInflater.from(getApplicationContext())
						.inflate(list_dir, null));

		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener()
		{

			@Override
			public void onDismiss()
			{
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		mListImageDirPopupWindow.setOnImageDirSelected(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//取消显示标题栏
		main = getResources().getIdentifier("image_activity_main", "layout", getPackageName());//
		list_dir = getResources().getIdentifier("list_dir", "layout", getPackageName());
		list_dir_item = getResources().getIdentifier("list_dir_item", "layout", getPackageName());
		titlebtn = getResources().getIdentifier("titlebtn", "layout", getPackageName());
		
		pictures_no = getResources().getIdentifier("pictures_no", "drawable", getPackageName());
		picture_unselected = getResources().getIdentifier("picture_unselected", "drawable", getPackageName());
		pictures_selected = getResources().getIdentifier("pictures_selected", "drawable", getPackageName());
		
		
		id_list_dir = getResources().getIdentifier("id_list_dir", "id", getPackageName());
		id_dir_item_image = getResources().getIdentifier("id_dir_item_image", "id", getPackageName());
		id_dir_item_name = getResources().getIdentifier("id_dir_item_name", "id", getPackageName());
		id_dir_item_count = getResources().getIdentifier("id_dir_item_count", "id", getPackageName());
		
		id_item_image = getResources().getIdentifier("id_item_image", "id", getPackageName());
		id_item_select = getResources().getIdentifier("id_item_select", "id", getPackageName());
		
		id_titlebtn_check = getResources().getIdentifier("id_titlebtn_check", "id", getPackageName());
		id_titlebtn_cancel = getResources().getIdentifier("id_titlebtn_cancel", "id", getPackageName());
		
		Bundle bundle = this.getIntent().getExtras();
		
		Log.d("before------>", maxPicSize+"");
		if(bundle != null){
			maxPicSize = bundle.getString("maxPicSize");
		}
		Log.d("after------>", maxPicSize+"");
		setContentView(main);
		
		if(Build.VERSION.SDK_INT>=23){
			requestPermissions(PERMISSIONS_GROUP, REQUEST_STATUS_CODE);//安卓6.0以上动态申请权限
		}else{
			initView();
    		getImages();
    		initEvent();
		}
		

		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		mScreenHeight = outMetrics.heightPixels;

	}

	//申请权限的回调
	@Override
	public void onRequestPermissionsResult(int requestCode,
			String[] permissions, int[] grantResults) {
		// TODO Auto-generated method stub
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if(requestCode==REQUEST_STATUS_CODE){
			  if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){//判断目前的系统是不是大于6.0
		            if(grantResults[0]!=PackageManager.PERMISSION_GRANTED||grantResults[1]!=PackageManager.PERMISSION_GRANTED){//是否仍然没有给权限
		                boolean b=shouldShowRequestPermissionRationale(permissions[0]);
		                boolean c=shouldShowRequestPermissionRationale(permissions[1]);
		                if(!b||!c){
		                    //用户是否选择了不再提醒
		                  //  showDialogTipUserGoToAppSetting();
		                }else{
		                    finish();
		                }
		            }else{
		            	initView();
		        		getImages();
		        		initEvent();
		            }
			  }
		}
	}

	/**
     根据路径筛选图片
	 */
	private void getImages()
	{
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
		{

			Toast.makeText(this, "\u6682\u65e0\u5916\u90e8\u5b58\u50a8", Toast.LENGTH_SHORT).show();
			return;
		}

		mProgressDialog = ProgressDialog.show(this, null, "\u6b63\u5728\u52a0\u8f7d...");

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{

				String firstImage = null;

				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = ImageSelectActivity.this
						.getContentResolver();

				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" },
						MediaStore.Images.Media.DATE_MODIFIED);

				Log.e("TAG", mCursor.getCount() + "");
				while (mCursor.moveToNext())
				{
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));

					Log.e("TAG", path);

					if (firstImage == null)
						firstImage = path;

					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dirPath = parentFile.getAbsolutePath();
					CommonAdapter.ImageFloder imageFloder = null;

					if (mDirPaths.contains(dirPath))
					{
						continue;
					} else
					{
						mDirPaths.add(dirPath);
						imageFloder = new CommonAdapter.ImageFloder();
						imageFloder.setDir(dirPath);
						imageFloder.setFirstImagePath(path);
					}
					if(parentFile.list()==null)continue;
					int picSize = parentFile.list(new FilenameFilter()
					{
						@Override
						public boolean accept(File dir, String filename)
						{
							if (filename.endsWith(".jpg")
									|| filename.endsWith(".png")
									|| filename.endsWith(".jpeg"))
								return true;
							return false;
						}
					}).length;
					totalCount += picSize;

					imageFloder.setCount(picSize);
					mImageFloders.add(imageFloder);

					if (picSize > mPicsSize)
					{
						mPicsSize = picSize;
						mImgDir = parentFile;
					}
				}
				mCursor.close();

				// 重置父路径
				mDirPaths = null;

				// 通知Handler重新扫描图片
				mHandler.sendEmptyMessage(0x110);

			}
		}).start();

	}

	/**
	 * 初始化绑定界面
	 */
	private void initView()
	{
		int id_gridView = getResources().getIdentifier("id_gridView", "id", getPackageName());
		int id_choose_dir = getResources().getIdentifier("id_choose_dir", "id", getPackageName());
		int id_total_count = getResources().getIdentifier("id_total_count", "id", getPackageName());
		int id_bottom_ly = getResources().getIdentifier("id_bottom_ly", "id", getPackageName());
		
		mOkImageButton = (TextView)findViewById(id_titlebtn_check);
		mCancelImageButton = (ImageButton)findViewById(id_titlebtn_cancel);
		mGirdView = (GridView) findViewById(id_gridView);
		mChooseDir = (TextView) findViewById(id_choose_dir);
		mImageCount = (TextView) findViewById(id_total_count);
		mBottomLy = (RelativeLayout) findViewById(id_bottom_ly);

	}

	private void initEvent()
	{
		mBottomLy.setOnClickListener(new OnClickListener()
		{
			int anim_popup_dir = getResources().getIdentifier("anim_popup_dir", "style", getPackageName());
			@Override
			public void onClick(View v)
			{
				mListImageDirPopupWindow
						.setAnimationStyle(anim_popup_dir);
				mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

				// 锟斤拷锟矫憋拷锟斤拷锟斤拷色锟戒暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = .3f;
				getWindow().setAttributes(lp);
			}
		});
		//确定
		mOkImageButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v){
				
				String[] strArr = new String[MyAdapter.mSelectedImage.size()];
				MyAdapter.mSelectedImage.toArray(strArr);

//				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//				intent.setClass(ImageSelectActivity.this,ImagePicker.class);
//				Bundle bundle = new Bundle();
//				bundle.putStringArray("imageItem",strArr);
//				intent.putExtras(bundle);
//				setResult(RESULT_OK,intent);
				Toast.makeText(ImageSelectActivity.this,strArr[0].toString(), Toast.LENGTH_SHORT).show();
				//finish();
			}
		});
		//返回
		mCancelImageButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v){
				
//				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//				intent.setClass(ImageSelectActivity.this,ImagePicker.class);
//				setResult(0,intent);
				finish();
			}
			
		});
	}

	@Override
	public void selected(CommonAdapter.ImageFloder floder)
	{

		mImgDir = new File(floder.getDir());
		mImgs = Arrays.asList(mImgDir.list(new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String filename)
			{
				if (filename.endsWith(".jpg") || filename.endsWith(".png")
						|| filename.endsWith(".jpeg"))
					return true;
				return false;
			}
		}));
		/**
		 * 重新加载图片
		 */
		mAdapter = new MyAdapter(getApplicationContext(), mImgs,
				grid_item, mImgDir.getAbsolutePath(), maxPicSize);
		mGirdView.setAdapter(mAdapter);
		// mAdapter.notifyDataSetChanged();
		mImageCount.setText(floder.getCount() + "张");
		mChooseDir.setText(floder.getName());
		mListImageDirPopupWindow.dismiss();

	}

}
