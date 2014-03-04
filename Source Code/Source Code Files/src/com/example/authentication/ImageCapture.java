package com.example.authentication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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


public class ImageCapture 
{
	public Mat mimage;
	public ImageCapture()
	{
		//File file=new File("mnt/sdcard/external_sd/c.jpg");
		//try 
		//{
			//FileInputStream fis=new FileInputStream(file);
			//byte[] buffer=new byte[300000];
			//long bytesread = fis.read(buffer);
			//if(buffer.length==0)
			//{
				//Log.i("ImageCapture", "Java:Empty image");
			//}
	//	} 
		
		//catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			/*e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		
		
		mimage=Highgui.imread("/mnt/sdcard/external_sd/c.jpg");
		
	};
	public native void manipulateimage();
}
