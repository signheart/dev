package com.keyue.qlm.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;

import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.keyue.qlm.R;
import com.keyue.qlm.bean.Gs;
import com.keyue.qlm.util.DBHelp;
import com.keyue.qlm.util.DBManager;
import com.keyue.qlm.util.ImageUtil;
import com.keyue.qlm.util.MyDialog;
import com.keyue.qlm.util.Prototypes;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AddGs extends Activity{
	private DBManager dbManager;
	private String user_id="2";
	private LocationClient mLocationClient = null;//��λ����
	private BDLocationListener myListener = new MyLocationListener();//��λ����
	private LocationClientOption clientOption = null;//��λ����
	private BDLocation location = null;//��ǰλ����Ϣ
	private Cursor cursor;
	BMapManager mBMapMan = null;
	MKSearch mkSearch=null;
	private String statusstr="";
	private String statusstr2="";
	private EditText gsmced;
	private EditText gsdzed;
	private EditText gsjjed;
	private ImageView photoimage;
	private ImageView photoimage2;
	private MyDialog dialog;
	private LinearLayout loadll;
	private ScrollView sc;
	private Button addgs;
	private final int ADDGSSUCCESS=5;
	private final int ADDGSSERVERERROR=7;
	private final int UPDATEIMAGE=8;
	private int status=0;
	private ListView menulist;
	private Gs gs;
	private String gs_id;
	private TextView titletext;
	private ImageUtil imageUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.addgs_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebyaddgs_layout);
		imageUtil=ImageUtil.getDefaultUtil();
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AddGs.this.finish();
			}
		});
		dbManager= new DBManager(this);
		dbManager.opendb();
    	List<Object[]> objects= dbManager.sel("select * from user", new Object[]{"userid","username","userimage"});
    	dbManager.closedb();
    	user_id=objects.get(0)[0].toString();
    	gsdzed = (EditText) this.findViewById(R.id.gsdz);
    	gsjjed=(EditText) this.findViewById(R.id.gsjj);
    	photoimage=(ImageView) this.findViewById(R.id.photoimage);
    	photoimage2=(ImageView) this.findViewById(R.id.photoimage2);
    	gsmced=(EditText) this.findViewById(R.id.gsmc);
    	loadll=(LinearLayout) this.findViewById(R.id.loadll);
    	sc=(ScrollView) this.findViewById(R.id.mainsc);
    	addgs=(Button) this.findViewById(R.id.addgs);
    	titletext=(TextView) this.findViewById(R.id.titletext);
    	gs_id=getIntent().getStringExtra("gs_id");
    	if(!gs_id.equals("-1")){
    		gs=(Gs) getIntent().getSerializableExtra("gs");
    		titletext.setText("�޸Ĺ�˾");
    		addgs.setText("�޸�");
    		gsmced.setText(gs.getGsmc());
    		gsdzed.setText(gs.getGsdz());
    		gsjjed.setText(gs.getGsjj());
    	}
    	mBMapMan = new BMapManager(getApplication());
		mBMapMan.init(Prototypes.BaiduMapKey, new MKGeneralListener() {

			@Override
			public void onGetPermissionState(int state) {
				if (state == 300) {
					Toast.makeText(AddGs.this, "��Ȩ��֤ʧ��",
							Toast.LENGTH_SHORT).show();
				}
			}


			@Override
			public void onGetNetworkState(int state) {
				Toast.makeText(AddGs.this, "�������  ״̬�룺   " + state,
						Toast.LENGTH_SHORT).show();
			}
		});
		mkSearch = new MKSearch();
		mkSearch.init(mBMapMan, new MKSearchListener() {
			
			@Override
			public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
			
		
			
			@Override
			public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onGetPoiDetailSearchResult(int arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onGetAddrResult(final MKAddrInfo arg0, int arg1) {
				if (null == arg0) {
					Toast.makeText(AddGs.this, "��������ȷ�Ĺ�˾��ַ", 0)
							.show();
				} else {
					if(gsmced.getText().toString().trim().equals("")){
						Toast.makeText(AddGs.this, "�����빫˾ȫ��",0).show();
						return;
					}
				if(gs_id.equals("-1")){
					if(statusstr.equals("")){
						Toast.makeText(AddGs.this, "��ѡ��һ�Ź�˾��Ƭ",0).show();
						return;
					}
				}
					View popprobar=getLayoutInflater().inflate(R.layout.popprobar, null);
					dialog=new MyDialog(AddGs.this,popprobar,R.style.MyDialog,"������...");
					dialog.show();
					new Thread(new Runnable() {
						
						@Override
						public void run() {
						
							String gsjj="��";
						
							if(!gsjjed.getText().toString().trim().equals("")){
								gsjj=gsjjed.getText().toString();
							}
							Message message = new Message();
							if(gs_id.equals("-1")){
								String result= imageUtil.uploadFile("UploadFileServlet.servlet", Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"photo.jpg");
								if(null!=result){
									String result2="";
									if(!statusstr2.equals("")){
										result2=imageUtil.uploadFile("UploadFileServlet.servlet", Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"photo2.jpg");
									}
									if(null!=result2){
										if(!result2.equals("")){
											result2="upload/"+result2;
										}
										int result1=DBHelp.savesql("insert into gs (user_id,gsmc,zpimage,zzimage,gsdz,gswd,gsjd,gsjj,gscreatedate) " +
												"values ("+user_id+",'"+gsmced.getText().toString()+"','upload/"+result+"','"+result2+"','"+gsdzed.getText().toString()+"',"+arg0.geoPt.getLatitudeE6()/1E6+","+arg0.geoPt.getLongitudeE6()/1E6+",'"+gsjj+"',NOW())");
										if(result1>0){
											message.what=ADDGSSUCCESS;
										}else{
											message.what=ADDGSSERVERERROR;
										}
									}else{
										message.what=ADDGSSERVERERROR;
									}
								}else{
									message.what=ADDGSSERVERERROR;
								}
							}else{
								String sql="update gs set gsmc='"+gsmced.getText().toString()+"',gsdz='"+gsdzed.getText().toString()+"',gsjj='"+gsjj+"',gswd="+arg0.geoPt.getLatitudeE6()/1E6+",gsjd="+arg0.geoPt.getLongitudeE6()/1E6;
								String result="true";
								String result2="true";
								if(!statusstr.equals("")){
									result= imageUtil.uploadFile("UploadFileServlet.servlet", Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"photo.jpg");
									sql+=",zpimage='upload/"+result+"'";
								}
								if(!statusstr2.equals("")){
									result2=imageUtil.uploadFile("UploadFileServlet.servlet", Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"photo2.jpg");
									sql+=",zzimage='upload/"+result2+"'";
								}
								sql+=" where gs_id="+gs_id;
								if(null!=result){
									if(null!=result2){
										int result1=DBHelp.savesql(sql);
										if(result1>0){
											message.what=ADDGSSUCCESS;
										}else{
											message.what=ADDGSSERVERERROR;
										}
									}else{
										message.what=ADDGSSERVERERROR;
									}
								}else{
									message.what=ADDGSSERVERERROR;
								}
								
								
							}		
							
							handler.sendMessage(message);
						}
					}).start();
				}
			}

			@Override
			public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
					int arg2) {
				// TODO Auto-generated method stub
				
			}
		});
		addgs.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(gsdzed.getText().toString().trim().equals("")){
					Toast.makeText(AddGs.this, "�����빫˾��ַ",0).show();
					return;
				}
				mkSearch.geocode(gsdzed.getText().toString().trim(), null);
			}
		});
    	Runnable updatetest = new Runnable() {

			@Override
			public void run() {
				// �õ�activity�еĸ�Ԫ��
				View view = findViewById(R.id.addgsrev);
				// ��θ�Ԫ�ص�width��height����0˵��activity�Ѿ���ʼ�����
				if (view != null && view.getWidth() > 0 && view.getHeight() > 0) {
					//new UpdateApp(NearByJob.this).isUpdate();
					if(gs_id.equals("-1")){
						onCreateGps();
					}else{
						sc.setVisibility(View.VISIBLE);
						loadll.setVisibility(View.GONE);
						imageUtil.loadImagebynoSize(photoimage, gs.getZpimage());
						imageUtil.loadImagebynoSize(photoimage2, gs.getZzimage());
					}
					
					//initdialoginfo();
					handler.removeCallbacks(this);
					
				} else {
					// ���activityû�г�ʼ�������ȴ�5�����ٴμ��
					handler.postDelayed(this, 100);
				}
			}
		};

		handler.post(updatetest);
	}
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			
			case ADDGSSUCCESS:
				dialog.dismiss();
				Intent intent = new Intent(AddGs.this,MainActivity.class);
				AddGs.this.setResult(RESULT_OK, intent);
				AddGs.this.finish();
				break;
			case ADDGSSERVERERROR:
				Toast.makeText(AddGs.this, "���ӳ�ʱ", 1).show();
				dialog.dismiss();
			case UPDATEIMAGE:
				if(status==0){
					 Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"photo.jpg");
						Drawable drawable = new BitmapDrawable(bitmap);
						int height = (int) ((float) photoimage.getWidth()/drawable.getMinimumWidth() * drawable.getMinimumHeight());
						photoimage.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height));
						photoimage.setImageBitmap(bitmap);
				}else{
					 Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"photo2.jpg");
						Drawable drawable = new BitmapDrawable(bitmap);
						int height = (int) ((float) photoimage.getWidth()/drawable.getMinimumWidth() * drawable.getMinimumHeight());
						photoimage2.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height));
						photoimage2.setImageBitmap(bitmap);
				}
				break;
			}
		}
	};
	
	public void xzphoto(View view){
		View menuview=getLayoutInflater().inflate(R.layout.dialogmenu_layout, null);
		menulist=(ListView) menuview.findViewById(R.id.menulist);
		dialog=new MyDialog(this,menuview,R.style.MyDialog,"��Ƭѡ��");
		dialog.setCanceledOnTouchOutside(true);
		final SimpleAdapter adapter = new SimpleAdapter(this, getPopMenuList(new Object[]{"�ֻ�����","�����ѡ��"}), R.layout.contentlist, new String[] { "contentname" },new int[] { R.id.contextz });
		menulist.setAdapter(adapter);
		menulist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(((HashMap<String,Object>)adapter.getItem(position)).get("contentname").toString().equals("�ֻ�����")){
					System.out.println("�ֻ�����");
					if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
						 File fos=null;  
						  fos=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"photo.jpg");  
						  Uri u=Uri.fromFile(fos);  
						    Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
						    i.putExtra(MediaStore.Images.Media.ORIENTATION, 0);  
						    i.putExtra(MediaStore.EXTRA_OUTPUT, u);  
						  
						    AddGs.this.startActivityForResult(i, 123);  
					 }else{
						 Toast.makeText(getApplicationContext(), "SDCARD������", 0).show();
						 
					 }
				}else{
				    Intent picture = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				    startActivityForResult(picture, 456);
				}
				dialog.dismiss();
			}
		});
		dialog.show();
		status=0;
	}
	
	public void xzphoto2(View view){
		View menuview=getLayoutInflater().inflate(R.layout.dialogmenu_layout, null);
		menulist=(ListView) menuview.findViewById(R.id.menulist);
		dialog=new MyDialog(this,menuview,R.style.MyDialog,"��Ƭѡ��");
		dialog.setCanceledOnTouchOutside(true);
		final SimpleAdapter adapter = new SimpleAdapter(this, getPopMenuList(new Object[]{"�ֻ�����","�����ѡ��"}), R.layout.contentlist, new String[] { "contentname" },new int[] { R.id.contextz });
		menulist.setAdapter(adapter);
		menulist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(((HashMap<String,Object>)adapter.getItem(position)).get("contentname").toString().equals("�ֻ�����")){
					System.out.println("�ֻ�����");
					if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
						 File fos=null;  
						  fos=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"photo2.jpg");  
						  Uri u=Uri.fromFile(fos);  
						    Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
						    i.putExtra(MediaStore.Images.Media.ORIENTATION, 0);  
						    i.putExtra(MediaStore.EXTRA_OUTPUT, u);  
						  
						    AddGs.this.startActivityForResult(i, 789);  
					 }else{
						 Toast.makeText(getApplicationContext(), "SDCARD������", 0).show();
						 
					 }
				}else{
				    Intent picture = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				    startActivityForResult(picture, 101);
				}
				dialog.dismiss();
			}
		});
		dialog.show();
		status=1;
	}
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		try {
			new Thread(new Runnable(){
			
			@Override
			public void run() {
				if(resultCode==RESULT_OK) {
					 Bitmap bb=null;
					   if(requestCode==123)  
			            { 
						   bb=imageUtil.compressImageFromFile(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"photo.jpg");
			            	
							//photoimage.setImageBitmap(bitmap);       
			            }else if (requestCode==456){
			            	Uri selectedImage = data.getData();
			            	   String[] filePathColumns={MediaStore.Images.Media.DATA};
			            	   if(cursor!=null){
			            		   if(cursor.isClosed()){
			            			   
			            		   }
			            	   }
			            	   cursor=getContentResolver().query(selectedImage, filePathColumns, null,null, null);
			            	   cursor.moveToFirst();
			            	   int columnIndex = cursor.getColumnIndex(filePathColumns[0]);
			            	   String picturePath= cursor.getString(columnIndex);
			            	   cursor.close();
			            	   bb=imageUtil.compressImageFromFile(picturePath);
			            } else if(requestCode==789)  
					            { 
								   bb=imageUtil.compressImageFromFile(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"photo2.jpg");
   
					            }else if (requestCode==101){
					            	Uri selectedImage = data.getData();
					            	   String[] filePathColumns={MediaStore.Images.Media.DATA};
					            	   if(cursor!=null){
					            		   if(cursor.isClosed()){
					            			   
					            		   }
					            	   }
					            	   cursor=getContentResolver().query(selectedImage, filePathColumns, null,null, null);
					            	   cursor.moveToFirst();
					            	   int columnIndex = cursor.getColumnIndex(filePathColumns[0]);
					            	   String picturePath= cursor.getString(columnIndex);
					            	   cursor.close();
					            	   bb=imageUtil.compressImageFromFile(picturePath);
					            }
					   if(status==0){
						   File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator,"photo.jpg");
				         	  if (f.exists()) {
				         	   f.delete();
				         	  }
				         	  
				         	  FileOutputStream out = null;
								try {
									out = new FileOutputStream(f);
									bb.compress(Bitmap.CompressFormat.JPEG, 100, out);
					            	  
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}finally{
									 try {
										out.flush();
										 out.close();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
					            	  
								}
								handler.sendEmptyMessage(UPDATEIMAGE);
							
								statusstr="yes";
					   }else{
						   
						   
						   File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator,"photo2.jpg");
				         	  if (f.exists()) {
				         	   f.delete();
				         	  }
				         	  
				         	  FileOutputStream out = null;
								try {
									out = new FileOutputStream(f);
									bb.compress(Bitmap.CompressFormat.JPEG, 100, out);
					            	  
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}finally{
									 try {
										out.flush();
										 out.close();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
					            	  
								}
								handler.sendEmptyMessage(UPDATEIMAGE);
							 
								statusstr2="yes";
					   }
					 
				 } 
			}
		}).start();
		}catch (Exception e) {
				// TODO: handle exception
			}
		
	
	}
	public List<Map<String, Object>> getPopMenuList(Object[] objects) {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = null;
		for (int i = 0; i < objects.length; i++) {
			map = new HashMap<String, Object>();
			map.put("contentname", objects[i]);
			data.add(map);
		}
		return data;
	}
	public void onCreateGps() {
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(myListener);
		mLocationClient.setAK(Prototypes.BaiduMapKey);
		clientOption = new LocationClientOption();
		clientOption.setOpenGps(true);
		clientOption.setAddrType("all");// ���صĶ�λ���������ַ��Ϣ
		clientOption.setCoorType("bd09ll");// ���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
		clientOption.setScanSpan(1000);// ���÷���λ����ļ��ʱ��Ϊ5000ms
		clientOption.disableCache(true);// ��ֹ���û��涨λ
		clientOption.setPoiNumber(5); // ��෵��POI����
		clientOption.setPoiDistance(1000); // poi��ѯ����
		clientOption.setPoiExtraInfo(true); // �Ƿ���ҪPOI�ĵ绰�͵�ַ����ϸ��Ϣ
		mLocationClient.setLocOption(clientOption);
		mLocationClient.start();
		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.requestLocation();
		}

	}
	class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				Toast.makeText(AddGs.this, "��ȡ����λ����Ϣ", 1);
			else {
				//run(location);
				mLocationClient.stop();
				AddGs.this.location=location;
				if(null!=location.getAddrStr()){
					gsdzed.setText(location.getAddrStr());
				}
			
			}
			sc.setVisibility(View.VISIBLE);
			loadll.setVisibility(View.GONE);
			
		}

		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;
			}
			StringBuffer sb = new StringBuffer(256);
			sb.append("Poi time : ");
			sb.append(poiLocation.getTime());
			sb.append("\nerror  code : ");
			sb.append(poiLocation.getLocType());
			sb.append("\nlatitude : ");
			sb.append(poiLocation.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(poiLocation.getLongitude());
			sb.append("\nradius : ");
			sb.append(poiLocation.getRadius());
			if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(poiLocation.getAddrStr());
			}
			if (poiLocation.hasPoi()) {
				sb.append("\nPoi:");
				sb.append(poiLocation.getPoi());
			} else {
				sb.append("noPoi information");
			}
		}

	}
	@Override
    public void onConfigurationChanged(Configuration newConfig)
    {
		try {
			super.onConfigurationChanged(newConfig);

			if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			  }
			} catch (Exception ex) {
				
			}
    }
}
