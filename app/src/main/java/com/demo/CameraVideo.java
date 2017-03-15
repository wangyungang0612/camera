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
    SurfaceView mySurfaceView;//surfaceView����    
    SurfaceHolder holder;//surfaceHolder����    
    Camera myCamera;//�������    
    String filePath=Environment.getExternalStorageDirectory().toString()+"/"+exerciseId+"/" ;// ��Ƭ�����ļ���   
    String fileName="";
    
    
    private MediaRecorder mediaRecorder; //-----
    private boolean record;		//-----
    String path = Environment.getExternalStorageDirectory()+"/"+exerciseId; //����·��//-----
    
    SimpleDateFormat format1;
    
    //����jpegͼƬ�ص����ݶ���      
    PictureCallback jpeg = new PictureCallback() {    
            
        @Override    
        public void onPictureTaken(byte[] data, Camera camera) {    
            // TODO Auto-generated method stub    
            try    
            {// ���ͼƬ    
            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);    
            File file = new File(filePath+fileName);    
            BufferedOutputStream bos =    
                new BufferedOutputStream(new FileOutputStream(file));    
            bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);//��ͼƬѹ��������    
            bos.flush();//���    
            bos.close();//�ر�    
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
        /*��SD���ϴ���Ŀ¼*/
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

        requestWindowFeature(Window.FEATURE_NO_TITLE);//�ޱ���       
        
        setContentView(R.layout.camera_layout);    
        //��ÿؼ�    
        mySurfaceView = (SurfaceView)findViewById(R.id.surfaceView1);    
        //��þ��    
        holder = mySurfaceView.getHolder();    
        //��ӻص�    
        holder.addCallback(this);    
        //��������    
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);    
        //���ü���    
        mySurfaceView.setOnClickListener(this);  
        del = (Button)findViewById(R.id.c_del);						//ɾ����ť
        c_recode  = (Button)findViewById(R.id.c_recode); //��ʼ ¼��//-----
        c_stop  = (Button)findViewById(R.id.c_stop); 		//ֹͣ ¼��//-----
      //��ʼ
        c_recode.setOnClickListener(new OnClickListener() {  
	            @Override  
	            public void onClick(View v) { 
	            	//�ر�Ԥ�����ͷ���Դ    
	                myCamera.stopPreview();    
	                myCamera.release();    
	                myCamera = null;  
	                if(mediaRecorder == null)
	                {
	                	mediaRecorder = new MediaRecorder();	//-----
	                }
	                fileName = format1.format(new Date());
					mediaRecorder.reset();
					mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA); //��������ɼ���Ƶ
					mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); 
					mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
					/*����android.util.DisplayMetrics ��ȡ�ֱ���*/ 
		            DisplayMetrics dm = new DisplayMetrics(); 
		            getWindowManager().getDefaultDisplay().getMetrics(dm); 
//					mediaRecorder.setVideoSize(dm.widthPixels, dm.heightPixels);
					mediaRecorder.setVideoFrameRate(15); //ÿ��3֡
					mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP); //������Ƶ���뷽ʽ
					mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); //������Ƶ���뷽ʽ
					File videoFile = new File(path, fileName+".mp4"); //����·��������
					mediaRecorder.setOutputFile(videoFile.getAbsolutePath());
					mediaRecorder.setPreviewDisplay(holder.getSurface());
					try {
						mediaRecorder.prepare();//Ԥ��׼��
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
					mediaRecorder.start();//��ʼ��¼
					record = true;
	            }});
	      //ֹͣ      
        c_stop.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) { 
//            	CameraVideo.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
				if(record){
					mediaRecorder.stop();
					mediaRecorder.release();
					mediaRecorder=null;
					
					record = false;
					
					//�������    
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
			        myCamera.startPreview(); //����Ԥ��     
					
				}
            	
            }});
        
       
        //ɾ��
        del.setOnClickListener( new OnClickListener() {
			public void onClick(View v) {
				//ɾ��¼��
				
					File file = new File(path+fileName+".mp4");  
					try
					{
						file.delete();
						Toast.makeText(CameraVideo.this, "ɾ�����", 1).show();
						myCamera.startPreview();//����Ԥ��
						del.setVisibility(View.GONE);
						//ˢ��ý���
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
        //���ò�������ʼԤ��    
        Camera.Parameters params = myCamera.getParameters();    
        params.setPictureFormat(PixelFormat.JPEG);   
       
        myCamera.setParameters(params);    
        myCamera.startPreview();    
            
    }    
    @Override    
    public void surfaceCreated(SurfaceHolder holder) {   
        // TODO Auto-generated method stub    
        //�������    
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
    
    	
        //�ر�Ԥ�����ͷ���Դ    
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
            //���ò���,������    
            Camera.Parameters params = myCamera.getParameters();    
            params.setPictureFormat(PixelFormat.JPEG);    
            myCamera.setParameters(params);    
            myCamera.takePicture(null, null, jpeg);    
        }    
            
    }    
}   