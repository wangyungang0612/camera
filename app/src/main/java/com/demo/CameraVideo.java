package com.demo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CameraVideo extends Activity implements    
Callback, OnClickListener, AutoFocusCallback{   
	Button del,c_recode,c_stop; //-----
	String exerciseId="mycamera";
//	EditText upfilename;
    SurfaceView mySurfaceView;//surfaceView声明    
    SurfaceHolder holder;//surfaceHolder声明    
    Camera myCamera;//相机声明    
    String filePath=Environment.getExternalStorageDirectory().toString()+"/"+exerciseId+"/" ;// 照片保存文件夹   
    String fileName="";
    
    
    private MediaRecorder mediaRecorder; //-----
    private boolean record;		//-----
    String path = Environment.getExternalStorageDirectory()+"/"+exerciseId; //保存路径//-----
    
    SimpleDateFormat format1;
    
    //创建jpeg图片回调数据对象      
    PictureCallback jpeg = new PictureCallback() {    
            
        @Override    
        public void onPictureTaken(byte[] data, Camera camera) {    
            // TODO Auto-generated method stub    
            try    
            {// 获得图片    
            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);    
            File file = new File(filePath+fileName);    
            BufferedOutputStream bos =    
                new BufferedOutputStream(new FileOutputStream(file));    
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);//将图片压缩到流中    
            bos.flush();//输出    
            bos.close();//关闭    
            }catch(Exception e)    
            {    
                e.printStackTrace();    
            }    
                
        }    
    };    
    /** Called when the activity is first created. */    
    @Override    
    public void onCreate(Bundle savedInstanceState) {    
        super.onCreate(savedInstanceState);    
        format1 = new SimpleDateFormat("yyyyMMddHHmmss");
        /*在SD卡上创建目录*/
        File eis = new File(Environment.getExternalStorageDirectory().toString()+"/"+exerciseId+"/");
		try
		{
			 if(!eis.exists())
			 {      
				 eis.mkdir();   
		     } 
		}
		catch(Exception e)
		{
			
		}

        requestWindowFeature(Window.FEATURE_NO_TITLE);//无标题       
        
        setContentView(R.layout.camera_layout);    
        //获得控件    
        mySurfaceView = (SurfaceView)findViewById(R.id.surfaceView1);    
        //获得句柄    
        holder = mySurfaceView.getHolder();    
        //添加回调    
        holder.addCallback(this);    
        //设置类型    
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);    
        //设置监听    
        mySurfaceView.setOnClickListener(this);  
        del = (Button)findViewById(R.id.c_del);						//删除按钮
        c_recode  = (Button)findViewById(R.id.c_recode); //开始 录像//-----
        c_stop  = (Button)findViewById(R.id.c_stop); 		//停止 录像//-----
      //开始
        c_recode.setOnClickListener(new OnClickListener() {  
	            @Override  
	            public void onClick(View v) { 
	            	//关闭预览并释放资源    
	                myCamera.stopPreview();    
	                myCamera.release();    
	                myCamera = null;  
	                if(mediaRecorder == null)
	                {
	                	mediaRecorder = new MediaRecorder();	//-----
	                }
	                fileName = format1.format(new Date());
					mediaRecorder.reset();
					mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA); //从照相机采集视频
					mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); 
					mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
					/*引用android.util.DisplayMetrics 获取分辨率*/ 
		            DisplayMetrics dm = new DisplayMetrics(); 
		            getWindowManager().getDefaultDisplay().getMetrics(dm); 
//					mediaRecorder.setVideoSize(dm.widthPixels, dm.heightPixels);
					mediaRecorder.setVideoFrameRate(15); //每秒3帧
					mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP); //设置视频编码方式
					mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); //设置音频编码方式
					File videoFile = new File(path, fileName+".mp4"); //保存路径及名称
					mediaRecorder.setOutputFile(videoFile.getAbsolutePath());
					mediaRecorder.setPreviewDisplay(holder.getSurface());
					try {
						mediaRecorder.prepare();//预期准备
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
					mediaRecorder.start();//开始刻录
					record = true;
	            }});
	      //停止      
        c_stop.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) { 
//            	CameraVideo.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
				if(record){
					mediaRecorder.stop();
					mediaRecorder.release();
					mediaRecorder=null;
					
					record = false;
					
					//开启相机    
			        if(myCamera == null)    
			        {    
			            myCamera = Camera.open();    
			            try {    
			                myCamera.setPreviewDisplay(holder);    
			            } catch (IOException e) {    
			                e.printStackTrace();    
			            }    
			        }  
			        Camera.Parameters params = myCamera.getParameters();    
			        params.setPictureFormat(PixelFormat.JPEG);   
//			        myCamera.setDisplayOrientation(90);
			        myCamera.setParameters(params);    
			        myCamera.startPreview(); //开启预览     
					
				}
            	
            }});
        
       
        //删除
        del.setOnClickListener( new OnClickListener() {
			public void onClick(View v) {
				//删除录像
				
					File file = new File(path+fileName+".mp4");  
					try
					{
						file.delete();
						Toast.makeText(CameraVideo.this, "删除完成", 1).show();
						myCamera.startPreview();//开启预览
						del.setVisibility(View.GONE);
						//刷新媒体库
		             	 sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"  
		                        + Environment.getExternalStorageDirectory()+"/"+exerciseId))); 
					}
					catch(Exception e)
					{
						
					}
				
			}});
        
    }    
    
     
    @Override    
    public void surfaceChanged(SurfaceHolder holder, int format, int width,    
            int height) {    
        // TODO Auto-generated method stub  
        //设置参数并开始预览    
        Camera.Parameters params = myCamera.getParameters();    
        params.setPictureFormat(PixelFormat.JPEG);   
       
        myCamera.setParameters(params);    
        myCamera.startPreview();    
            
    }    
    @Override    
    public void surfaceCreated(SurfaceHolder holder) {   
        // TODO Auto-generated method stub    
        //开启相机    
        if(myCamera == null)    
        {    
            myCamera = Camera.open();    
            try {    
                myCamera.setPreviewDisplay(holder);    
            } catch (IOException e) {    
                // TODO Auto-generated catch block    
                e.printStackTrace();    
            }    
        }    
            
    }    
    @Override    
    public void surfaceDestroyed(SurfaceHolder holder) {    
        // TODO Auto-generated method stub    
    
    	
        //关闭预览并释放资源    
    	if(myCamera!=null)
    	{
	        myCamera.stopPreview();    
	        myCamera.release();    
	        myCamera = null;   
    	}
    	if(mediaRecorder!=null)
    	{
	        mediaRecorder.release(); //----
	        mediaRecorder = null;
    	}
        
            
    }    
    @Override    
    public void onClick(View v) {    
       
    }   
    @Override    
    public void onAutoFocus(boolean success, Camera camera) {    
        // TODO Auto-generated method stub    
        if(success)    
        {    
            //设置参数,并拍照    
            Camera.Parameters params = myCamera.getParameters();    
            params.setPictureFormat(PixelFormat.JPEG);    
            myCamera.setParameters(params);    
            myCamera.takePicture(null, null, jpeg);    
        }    
            
    }    
}   