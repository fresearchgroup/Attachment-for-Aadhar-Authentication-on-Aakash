/*This is the openCV code for manipulating images.
 * This is a cpp code
 * The function manipulateimage() is declared native in NewAadhar.java
 * Developed by Aakash Authentication team
 */


#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <fstream>
#include <opencv/cv.h>
#include <opencv/highgui.h>
#include <opencv/cxcore.h>
#include <android/log.h>
#include<opencv/ml.h>

#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <vector>

#include "opencv2/core/core_c.h"
#include "opencv2/core/core.hpp"
#include "opencv2/flann/miniflann.hpp"
#include "opencv2/imgproc/imgproc_c.h"
#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/video/video.hpp"
#include "opencv2/features2d/features2d.hpp"
#include "opencv2/objdetect/objdetect.hpp"
#include "opencv2/calib3d/calib3d.hpp"
#include "opencv2/ml/ml.hpp"
#include "opencv2/highgui/highgui_c.h"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/contrib/contrib.hpp"




using namespace cv;
using namespace std;

#define ABS(x) (((x) >= 0) ? (x) : (-(x)))

void hist(const Mat &in, Mat &out)
{
    int histSize = 256;
    float range[] = { 0, 256 } ;
    const float* histRange = { range };
    bool uniform = true; bool accumulate = false;
    calcHist(&in, 1, 0, Mat(), out, 1, &histSize, &histRange, uniform, accumulate);
}

void clamp_and_cumsum(Mat &hist, float regularizer)
{
    float total_pixels = sum(hist)[0];
    float clip_limit = (regularizer * total_pixels) / hist.total();

    assert (hist.depth() == CV_32F);
    assert (hist.total() == 256);
    assert (hist.isContinuous());

    float *ptr = hist.ptr<float>();
    float excess = 0;
    float accum = 0;

    // clamp and compute cumulative sum
    for (int i = 0; i < 256; i++, ptr++) {
        float overage = MAX(*ptr - clip_limit, 0);
        if (overage > 0.0) {
            excess += overage;
            *ptr -= overage;
        }
        // make sure 0 maps to 0
        float temp = *ptr;
        *ptr = accum;
        accum += temp;
    }

    // redistribute excess, and normalize to 0-255
    float scale = 255.0 / (hist.at<float>(255) + excess);
    float last = 0.0;
    excess /= 256;
    int i;
    for (i = 0, ptr = hist.ptr<float>(); i < 256; i++, ptr++) {
        *ptr += ((i + 1) * excess); // every bin after 0 has excess added
        *ptr *= scale;
        float tmp = *ptr;
        *ptr = cvRound((last + *ptr) / 2.0); // map to halfway between low and high values, and round to nearest integer
        last = tmp; // update last to pre-averaged value
    }
}
void adapthisteq(const Mat &in, Mat &out, float regularizer)
{
    // contrast limited adapthive histogram equalization.
    // regularizer = max derivative in the remapping function.
    // 1 = no remapping.
    // higher values = more remapping.
    //
    // Uses a fixed 8x8 grid of blocks and interpolates between their
    // remappings. Clips before interpolation. The regularizer is
    // approximate. Some points in the remapping may be slightly
    // higher.

    // matrix of local histograms
    Mat localhists[8][8];

    // slightly overestimate to insure coverage
    int block_width = (in.cols + 7) / 8;
    int block_height = (in.rows + 7) / 8;
    Rect imrect(0, 0, in.cols, in.rows);

    // compute histograms in 8x8 subimages
    for (int i = 0; i < 8; i++)
        for (int j = 0; j < 8; j++) {
            Rect sub(i * block_width, j * block_height,
                     block_width, block_height);
            if ((i == 7) || (j == 7))
                sub &= imrect; // Avoid going off the edge
            hist(in(sub), localhists[i][j]);
        }

    // clamp histograms and compute remapping function (in place)
    for (int i = 0; i < 8; i++)
        for (int j = 0; j < 8; j++)
            clamp_and_cumsum(localhists[i][j], regularizer);

    // weight function for normalization
    Mat weight = Mat::zeros(in.size(), CV_32F);
    Mat output = Mat::zeros(in.size(), CV_32F);

    // region weighting mask
    Mat ROIweight(2 * block_height + 1, 2 * block_width + 1, CV_32F);
    for (int j = 0; j < 2 * block_height + 1; j++)
        for (int i = 0; i < 2 * block_width + 1; i++)
            ROIweight.at<float>(j, i) = (block_height - ABS(j - block_height)) *
                (block_width - ABS(i - block_width));

    for (int i = 0; i < 8; i++)
        for (int j = 0; j < 8; j++) {
            // upper left corner of histogram window
            int corneri = i * block_width;
            int cornerj = j * block_height;
            // bounds of ROI (histogram region of influence)
            int ROI_lo_i = corneri - (block_width / 2);
            int ROI_lo_j = cornerj - (block_height / 2);

            Rect subrect(ROI_lo_i, ROI_lo_j,
                         2 * block_width + 1, 2 * block_height + 1);

            // clip to actual image
            subrect &= imrect;

            // find subimage ROI after clipping
            Rect subROI = Rect(0, 0, 2 * block_width + 1, 2 * block_height + 1) & \
                Rect(-ROI_lo_i, -ROI_lo_j, in.cols, in.rows);

            Mat temp(subrect.height, subrect.width, CV_32F);

            // locally transform
            LUT(in(subrect), localhists[i][j], temp);
            multiply(temp, ROIweight(subROI), temp);
            output(subrect) += temp;
            weight(subrect) += ROIweight(subROI);
        }

    output /= weight;
    output.convertTo(out, CV_8U);
}

void thinningIteration(cv::Mat& im, int iter)
{

	__android_log_write(ANDROID_LOG_INFO,"opencv","Write_thinning successful");
	cv::Mat marker = cv::Mat::zeros(im.size(), CV_8UC1);
	__android_log_write(ANDROID_LOG_INFO,"opencv","Write_thinning234 successful");
    for (int i = 1; i < im.rows-1; i++)
    {
        for (int j = 1; j < im.cols-1; j++)
        {
            uchar p2 = im.at<uchar>(i-1, j);
            uchar p3 = im.at<uchar>(i-1, j+1);
            uchar p4 = im.at<uchar>(i, j+1);
            uchar p5 = im.at<uchar>(i+1, j+1);
            uchar p6 = im.at<uchar>(i+1, j);
            uchar p7 = im.at<uchar>(i+1, j-1);
            uchar p8 = im.at<uchar>(i, j-1);
            uchar p9 = im.at<uchar>(i-1, j-1);

            int A  = (p2 == 0 && p3 == 1) + (p3 == 0 && p4 == 1) +
                     (p4 == 0 && p5 == 1) + (p5 == 0 && p6 == 1) +
                     (p6 == 0 && p7 == 1) + (p7 == 0 && p8 == 1) +
                     (p8 == 0 && p9 == 1) + (p9 == 0 && p2 == 1);
            int B  = p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9;
            int m1 = iter == 0 ? (p2 * p4 * p6) : (p2 * p4 * p8);
            int m2 = iter == 0 ? (p4 * p6 * p8) : (p2 * p6 * p8);

            if (A == 1 && (B >= 2 && B <= 6) && m1 == 0 && m2 == 0)
                marker.at<uchar>(i,j) = 1;
        }
    }

    im &= ~marker;
}


bool thinning(cv::Mat& im)
{

	  __android_log_write(ANDROID_LOG_INFO,"opencv","in_thin successful");
	im /= 255;

    cv::Mat prev = cv::Mat::zeros(im.size(), CV_8UC1);
     __android_log_write(ANDROID_LOG_INFO,"opencv","in_thin23 successful");

    cv::Mat diff;

    do {
        thinningIteration(im, 0);
        __android_log_write(ANDROID_LOG_INFO,"opencv","Write_thinfcdhfkdshfksdj successful");
        thinningIteration(im, 1);
        cv::absdiff(im, prev, diff);
        im.copyTo(prev);
    }
    while (cv::countNonZero(diff) > 0);

    im *= 255;
bool bool_thin=false;
    bool_thin = imwrite("/mnt/sdcard/Pictures/MyCameraApp3/c_thin.jpg",im);
    			return bool_thin;
}


int Min(int *array){
for(int i = 0; i < sizeof(array);i++)
if(array[i]==1)
return i;

return 0;
}

int Max(int *array){
for(int i = sizeof(array)-1; i >=0 ; i--)
if(array[i]==1)
return i;

return (sizeof(array)-1) ;
}


//............................................Cropping..................................................................
void cropping(Mat &image)
{
	int left=0;
	int top=0;
	int right=0;
	int bottom=0;


	int w = image.cols;
	    int h = image.rows;

	    for (int i = 0; i < h; i++) {
	      for (int j = 0; j < w; j++) {
	        int b = image.at<cv::Vec3b>(i,j)[0];
	        int g = image.at<cv::Vec3b>(i,j)[1];
	        int r = image.at<cv::Vec3b>(i,j)[2];
	if(b>100 && g>100 && r>100)
	{
	     cout<<b<<" "<<g<<" "<<r<<" ";
	    top=i;
	    break;
	}
	      }
	      if(top > 0)
	    {
	        break;
	    }
	    }


	 for (int i = h-1; i >=0 ; i--) {
	      for (int j = 0; j < w; j++) {
	        int b = image.at<cv::Vec3b>(i,j)[0];
	        int g = image.at<cv::Vec3b>(i,j)[1];
	        int r = image.at<cv::Vec3b>(i,j)[2];
	if(b>100 && g>100 && r>100)
	{bottom=i;
	break;
	}}
	if(bottom > 0)
	    {
	        break;
	    }}



	for (int i = 0; i <w ; i++) {
	      for (int j = 0; j <h ; j++) {
	        int b = image.at<cv::Vec3b>(j,i)[0];
	        int g = image.at<cv::Vec3b>(j,i)[1];
	        int r = image.at<cv::Vec3b>(j,i)[2];
	if(b>100 && g>100 && r>100)
	{left=i;
	break;
	}}
	if(left > 0)
	    {
	        break;
	    }}


	for (int i = w-1; i >=0 ; i--) {
	      for (int j = 0; j <h ; j++) {
	       // int pixel = image.getRGB(j, i);
	        int b = image.at<cv::Vec3b>(j,i)[0];
	        int g = image.at<cv::Vec3b>(j,i)[1];
	        int r = image.at<cv::Vec3b>(j,i)[2];
	        if(b>100 && g>100 && r>100)
	        {
	            right=i;
	            break;
	        }
	    }
	    if(right > 0)
	    {
	        break;
	    }
	    }



	image= image(Rect(left,top,right-left,bottom-top));
}



extern "C"
{

	JNIEXPORT bool JNICALL Java_com_example_authentication_NewAadhar_manipulateimage (JNIEnv *env, jobject obj)
	{

		bool flag = false;

		Mat img=cv::imread("/mnt/sdcard/Pictures/MyCameraApp3/c.jpg");

		Mat img1;
		Mat img_crop;
		Mat imgLaplacian;
		Mat imgSharpen,imgThreshold;
		int rows = img.rows;
		int cols = img.cols;

		if(img.data==NULL)
		{
			__android_log_write(ANDROID_LOG_INFO,"opencv","Empty Image");
		}
		else
		{
			__android_log_write(ANDROID_LOG_INFO,"opencv","Processing Starts");
		}
		cropping(img);
		Size s = img.size();
		rows = s.height;
		cols = s.width;


		bool bool_res=false;
		resize(img,img,Size(img.cols/2,img.rows/2),0,0,INTER_LINEAR);

		Mat im_gray;
		cvtColor(img,im_gray,CV_RGB2GRAY);

		adapthisteq(im_gray, img1,50.0);

/*.................................................... Sharpening..................................*/
		Mat  kernel = (Mat_<float>(3,3) <<
				-1, -1, -1,
				-1, 9, -1,
				-1,  -1, -1);
		filter2D(img1, imgLaplacian, CV_32F, kernel);
		img1.convertTo(img1, CV_32F);
		imgSharpen = img1 - imgLaplacian;

		// convert back to 8bits gray scale
		imgSharpen.convertTo(imgSharpen, CV_8U);
		imgLaplacian.convertTo(imgLaplacian, CV_8U);

		cv::threshold(imgSharpen, imgThreshold, 0, 255, THRESH_OTSU || THRESH_BINARY_INV);

		flag=thinning(imgThreshold);
		return	flag;

	}
}
