package com.example.authentication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.TabActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Build;
import android.provider.MediaStore;



//import android.os.Bundle;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
//import android.view.Menu;
//import android.app.AlertDialog;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.Highgui;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
//import org.opencv.android.OpenCVLoader;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

public class NewAadhar extends TabActivity {
	
	
	Button authbutton;
	Button resetbutton;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	public Camera mCamera;
	private CameraPreview mPreview = null;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public boolean scannedBool = false;
	
	private static final String TAG = "Authentication";
	
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) 
	{
			@Override
			public void onManagerConnected(int status) 
			{
				switch (status) 
				{
					case LoaderCallbackInterface.SUCCESS:
					{	
						Log.i(TAG, "OpenCV loaded successfully");
						System.loadLibrary("authentication");
						Log.i(TAG, "BLLAAAAAAAAAAAAAAJHHHHHHHH");
						
						
						
					} break;
				
					default:
					{
						Log.i(TAG, "OpenCV not loaded successfully");
					} break;
				}
			}
		};


      
		public native boolean manipulateimage();
		public void onResume()
		{
			super.onResume();
			OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
		}
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_aadhar);
		// Show the Up button in the action bar.
		setupActionBar();
		
		TabHost tabHost = getTabHost(); 
		tabHost.addTab(tabHost.newTabSpec("tab_test1").setIndicator("Demographic").setContent(R.id.demo));
	      tabHost.addTab(tabHost.newTabSpec("tab_test2").setIndicator("Biometric").setContent(R.id.bio));
	      tabHost.setCurrentTab(0); 
	      
	      mCamera = openFrontFacingCameraGingerbread();
	      mPreview = new CameraPreview(this, mCamera);
	      
	      File standard = new File("/mnt/sdcard/Pictures/MyCameraApp2/c.jpg");
	      ImageView stdImg = (ImageView)findViewById(R.id.standardFinger);
	      stdImg.setImageURI(Uri.fromFile(standard));
	      
	      
	      File scannedStdFinger = new File("/mnt/sdcard/Pictures/MyCameraApp2/c_thin.jpg");
	      ImageView scanStdImg = (ImageView)findViewById(R.id.stadardFingerScanned);
	      scanStdImg.setImageURI(Uri.fromFile(scannedStdFinger));
	      
	      
	      scannedBool = false;
	      
	      final PictureCallback mPicture = new PictureCallback() {

	            public void onPictureTaken(byte[] data, Camera camera) {

	                File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
	                
	                File new_file = new File("/mnt/sdcard/Pictures/MyCameraApp3/c_thin.jpg");
	                
	                try {
	                	
	                  FileOutputStream fos = new FileOutputStream(pictureFile);
	                 
	                	  fos.write(data);
	                  
	                  fos.close();
	                    MediaStore.Images.Media.insertImage(getContentResolver(), pictureFile.getAbsolutePath(), pictureFile.getName(), pictureFile.getName());
	                    //camera.startPreview();
	                    
	                  ImageView img = (ImageView) findViewById(R.id.imageFinger);
	                  img.setImageURI(Uri.fromFile(pictureFile));
	                  
	                  
	                  scannedBool = manipulateimage();
	                  
	                  
	                  if(scannedBool==true)
	                  {
	                  ImageView scannedImg = (ImageView)findViewById(R.id.imageScanned);
	                  scannedImg.setImageURI(Uri.fromFile(new_file));
	                  }
	                   
	                } catch (FileNotFoundException e) {

	                } catch (IOException e) {

	                }
	              }
	            };

	      
	      
	      
	      //ftr = new FtrScanDemoActivity();
	      //ftr.mButtonScan = (Button) findViewById(R.id.btnScan);
	      //ftr.mButtonStop = (Button) findViewById(R.id.btnStop);
	      //ftr.mButtonReset = (Button) findViewById(R.id.btnRst);
	      final Button mButtonscan = (Button)findViewById(R.id.btnScan);
	      final Button mButtonReset = (Button)findViewById(R.id.btnRst);
	      
	      
	      
	      
	      
	      
	      
	      
	      mButtonscan.setOnClickListener(

                  new View.OnClickListener() {

                      public void onClick(View v) {
                      	
                      	
                      		mCamera.takePicture(null,null, mPicture);
                      		mCamera.startPreview();
                      		mButtonscan.setClickable(false);
                      		mButtonReset.setClickable(true);
                    	  
                	      
                      }
                  }
              );
	      
	      mButtonReset.setOnClickListener(

                  new View.OnClickListener() {

                      public void onClick(View v) {
                      	
                      	
                    	  ImageView img = (ImageView) findViewById(R.id.imageFinger);
                    		img.setImageBitmap(null);
                    	ImageView scannedImage = (ImageView)findViewById(R.id.imageScanned);
                    		scannedImage.setImageBitmap(null);
                    			
                    		mButtonReset.setClickable(false);
                    		mButtonscan.setClickable(true);
                      }
                  }
              );
	      
	}

	
	private Camera openFrontFacingCameraGingerbread()
	{
		int cameraCount = 0;
		Camera Cam = null;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras();
		for(int camIdx = 0 ; camIdx < cameraCount ; camIdx++)
		{
			Camera.getCameraInfo(camIdx, cameraInfo);
			if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
			{
				try
				{
					Cam = Camera.open(camIdx);
				}
				catch(RuntimeException e)
				{
					
				}
			}
		}
		return Cam;
	}
	
	
	private  File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "MyCameraApp3");


	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            return null;
	        }
	    }

	    // Create a media file name
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	    	mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	         "c.jpg");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_aadhar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onBackPressed()
	{
		if (mCamera != null){
	        mCamera.release();        // release the camera for other applications
	        mCamera = null;
	    }
		super.onBackPressed();
	}
	
}
