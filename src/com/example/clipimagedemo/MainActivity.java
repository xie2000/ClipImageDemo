package com.example.clipimagedemo;

import java.io.File;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

/**
 * @ClassName: MainActivity
 * @Description:
 * @author xiechengfa2000@163.com
 * @date 2015-5-10 ����11:14:08
 */
public class MainActivity extends Activity implements OnClickListener {
	private final int START_ALBUM_REQUESTCODE = 1;
	private final int CAMERA_WITH_DATA = 2;
	private final int CROP_RESULT_CODE = 3;
	public static final String TMP_PATH = "clip_temp.jpg";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.albumBtn).setOnClickListener(this);
		findViewById(R.id.captureBtn).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.albumBtn) {
			startAlbum();
		} else if (v.getId() == R.id.captureBtn) {
			startCapture();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// String result = null;
		if (resultCode != RESULT_OK) {
			return;
		}

		switch (requestCode) {
		case CROP_RESULT_CODE:
			String path = data.getStringExtra(ClipImageActivity.RESULT_PATH);
			Bitmap photo = BitmapFactory.decodeFile(path);
			ImageView imageView = (ImageView) findViewById(R.id.imageView);
			imageView.setImageBitmap(photo);
			break;
		case START_ALBUM_REQUESTCODE:
			startCropImageActivity(getFilePath(data.getData()));
			break;
		case CAMERA_WITH_DATA:
			// 照相机程序返回的,再次调用图片剪辑程序去修剪图片
			startCropImageActivity(Environment.getExternalStorageDirectory()
					+ "/" + TMP_PATH);
			break;
		}
	}

	// 裁剪图片的Activity
	private void startCropImageActivity(String path) {
		ClipImageActivity.startActivity(this, path, CROP_RESULT_CODE);
	}

	private void startAlbum() {
		try {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			intent.setType("image/*");
			startActivityForResult(intent, START_ALBUM_REQUESTCODE);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
			try {
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent, START_ALBUM_REQUESTCODE);
			} catch (Exception e2) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	private void startCapture() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
				Environment.getExternalStorageDirectory(), TMP_PATH)));
		startActivityForResult(intent, CAMERA_WITH_DATA);
	}

	/**
	 * 通过uri获取文件路径
	 * 
	 * @param mUri
	 * @return
	 */
	public String getFilePath(Uri mUri) {
		try {
			if (mUri.getScheme().equals("file")) {
				return mUri.getPath();
			} else {
				return getFilePathByUri(mUri);
			}
		} catch (FileNotFoundException ex) {
			return null;
		}
	}

	// 获取文件路径通过url
	private String getFilePathByUri(Uri mUri) throws FileNotFoundException {
		Cursor cursor = getContentResolver()
				.query(mUri, null, null, null, null);
		cursor.moveToFirst();
		return cursor.getString(1);
	}

}
