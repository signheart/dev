package com.keyue.qlm.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.keyue.qlm.R;


import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateApp {
	private Context context;
	private List<Object[]> list;
	private final int SELUPDATESERVERERROR=1;
	private final int SELUPDATESUCCESS=2;
	private String url;
	private String mSavePath;
	private boolean isdownload=true;
	private ProgressBar progressBar;
	private final int DOWNLOAD=3;
	private final int DOWNLOAD_FINISH=4;
	private int count=0;
	private Dialog downloadi;
	private String apkName;
	private TextView jdtext;
	private int length;
	private int bfb;
	public UpdateApp(Context context){
		this.context=context;
	}
	
	
	
	
	public void isUpdate(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Message message = new Message();
				List<Object[]> list=DBHelp.selsql("select versionCode,downloadUrl,name from updateApp");
				if(null==list){
					message.what=SELUPDATESERVERERROR;
				}else{
					if(list.size()>0){
					message.what=SELUPDATESUCCESS;
					message.obj=list;
					}else{
						message.what=1000;
					}
				}
				handler.sendMessage(message);
			}
		}).start();
	}
	
	
	
	 private int getVersionCode(Context context)  
	    {  
	        int versionCode = 0;  
	        try  
	        {  
	            // ��ȡ����汾�ţ���ӦAndroidManifest.xml��android:versionCode  
	            versionCode = context.getPackageManager().getPackageInfo("com.keyue.qlm", 0).versionCode;
	        } catch (NameNotFoundException e)  
	        {  
	            e.printStackTrace();  
	        }  
	        return versionCode;  
	    }  
	 
	 
	 private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case SELUPDATESERVERERROR:
				Toast.makeText(context, "���粻ͨ���޷�������",1).show();
				break;
			case SELUPDATESUCCESS:
				list=(List<Object[]>) msg.obj;
				double serverversion=Double.parseDouble(list.get(0)[0].toString());
				url=list.get(0)[1].toString();
				apkName = list.get(0)[2].toString();
				if(serverversion>getVersionCode(context)){
					Builder builder = new Builder(context);
					builder.setTitle("�汾����");
					builder.setMessage("���°汾����Ҫ������");
					builder.setPositiveButton("����", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							downloadApk();
							dialog.dismiss();
						}
					});
					builder.setNegativeButton("�Ժ����",null);
					builder.show();
				}else{
					Toast.makeText(context, "�Ѿ������°汾��",1).show();
				}
				
				break;
			case DOWNLOAD:
				 progressBar.setProgress(bfb);
				 jdtext.setText(bfb+"%");
				break;
			case DOWNLOAD_FINISH:
				  downloadi.dismiss();
				  installApk();
				break;
			}
		}
		 
		 
	 };
	 
	 
	 private void downloadApk(){
		
		 Builder builder = new Builder(context);
		 builder.setTitle("�������ظ����ļ������Ժ�");
		 final LayoutInflater inflater = LayoutInflater.from(context);  
	       View v = inflater.inflate(R.layout.downloadjdt, null);  
	      progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);  
	      jdtext=(TextView) v.findViewById(R.id.jdtext);
	      builder.setView(v);
	      builder.setNegativeButton("ȡ��", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				isdownload=false;
			}
		});
	      downloadi=builder.create();
	      downloadi.show();
	    
		 new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						 if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			                    // ��ô洢����·��  
							 InputStream  is=null;
			                    String sdpath = Environment.getExternalStorageDirectory() + "/";  
			                    mSavePath = sdpath + "download";  
			               
			                    HttpClient client = new DefaultHttpClient();
								HttpGet httpGet = new HttpGet(url);
								HttpResponse response =client.execute(httpGet);
								int code= response.getStatusLine().getStatusCode();
								if(code==HttpStatus.SC_OK){
									HttpEntity entity = response.getEntity();
									length=(int) entity.getContentLength();
									
									is = entity.getContent();
									
								}
			  
			                    File file = new File(mSavePath);  
			                    // �ж��ļ�Ŀ¼�Ƿ����  
			                    if (!file.exists())  
			                    {  
			                        file.mkdir();  
			                    }  
			                    File apkFile = new File(mSavePath,apkName);  
			                    FileOutputStream fos = new FileOutputStream(apkFile);  
			                    // ����  
			                    byte buf[] = new byte[1024];  
			                    // д�뵽�ļ���  
			                    do  
			                    {  
			                        int numread = is.read(buf);  
			                        count += numread;  
			                        bfb  = (int) (((float) count / length) * 100); 
			                        // ���������λ��  
			                        // ���½���  
			                        handler.sendEmptyMessage(DOWNLOAD);  
			                        if (numread <= 0)  
			                        {  
			                            // �������  
			                        	handler.sendEmptyMessage(DOWNLOAD_FINISH);  
			                            break;  
			                        }  
			                        // д���ļ�  
			                        fos.write(buf, 0, numread);  
			                    } while (isdownload);// ���ȡ����ֹͣ����.  
			                    fos.close();  
			                    is.close();  
						 }
			            } catch (Exception e) {
			            	e.printStackTrace();
			            }
			      
				}
		 	}).start();
	 
	 }
	 private void installApk()  
	    {  
	        File apkfile = new File(mSavePath,apkName);  
	        if (!apkfile.exists())  
	        {  
	            return;  
	        }  
	        // ͨ��Intent��װAPK�ļ�  
	        Intent i = new Intent(Intent.ACTION_VIEW);  
	        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");  
	        context.startActivity(i);  
	    }  
}
