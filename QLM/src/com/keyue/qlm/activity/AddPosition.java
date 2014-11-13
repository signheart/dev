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

import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;

import com.keyue.qlm.R;
import com.keyue.qlm.activity.AddProfile.MyLocationListener;
import com.keyue.qlm.bean.Position;
import com.keyue.qlm.util.DBHelp;
import com.keyue.qlm.util.DBManager;
import com.keyue.qlm.util.ImageUtil;
import com.keyue.qlm.util.MyDialog;
import com.keyue.qlm.util.NumberUtil;

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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AddPosition extends Activity{
	private Button add;
	private TextView gsmced;
	private EditText zwmced;
	private EditText zprsed;
	private EditText zwxzed;
//	private EditText gsdzed;
	private EditText zwyqed;
	//private EditText gsjjed;
	private EditText lxdhed;
	//private ImageView photoimage;
	private CheckBox baochick;
	private CheckBox baozhuck;
	private CheckBox shuangxiuck;
	private CheckBox wxyjck;
	private ListView menulist;
	private ArrayList<String> allgw;
	private ArrayList<String> allxz;
	private ArrayList<String> allzprs;
	private ArrayList<String> allgs;
	private LinearLayout loadll;
	private ScrollView sc;
	//private LocationClient mLocationClient = null;//��λ����
	//private BDLocationListener myListener = new MyLocationListener();//��λ����
	//private LocationClientOption clientOption = null;//��λ����
	private BDLocation location = null;//��ǰλ����Ϣ
	private TextView loadtext;
	private MyDialog dialog;
//	private Cursor cursor;
//	private String statusstr="";
//	private String statusstr2="";
	private String user_id="2";
//	BMapManager mBMapMan = null;
	//MKSearch mkSearch=null;
	private ImageView photoimage2;
	private int status=0;
	private final int ADDPOSITIONSUCCESS=5;
	private final int ADDPOSITIONSERVERERROR=7;
	private DBManager dbManager;
	private Position position;
	private String position_id;
	private TextView titletext;
	SimpleAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		dbManager= new DBManager(this);
		dbManager.opendb();
    	List<Object[]> objects= dbManager.sel("select * from user", new Object[]{"userid","username","userimage"});
    	dbManager.closedb();
    	user_id=objects.get(0)[0].toString();
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.addposition_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebyaddposition_layout);
		add=(Button) this.findViewById(R.id.addposition);
		gsmced =(TextView) this.findViewById(R.id.gsmc);
		zwmced= (EditText) this.findViewById(R.id.zwmc);
		zprsed = (EditText) this.findViewById(R.id.zprs);
		zwxzed = (EditText) this.findViewById(R.id.zwxz);
		//gsdzed = (EditText) this.findViewById(R.id.gsdz);
		zwyqed = (EditText) this.findViewById(R.id.zwyq);
		//gsjjed=(EditText) this.findViewById(R.id.gsjj);
		lxdhed=(EditText) this.findViewById(R.id.lxdh);
		//photoimage=(ImageView) this.findViewById(R.id.photoimage);
		baochick=(CheckBox) this.findViewById(R.id.baochick);
		baozhuck=(CheckBox) this.findViewById(R.id.baozhuck);
		shuangxiuck=(CheckBox) this.findViewById(R.id.shuangxiuck);
		wxyjck=(CheckBox) this.findViewById(R.id.wxyjck);
		sc=(ScrollView) this.findViewById(R.id.mainsc);
		loadll=(LinearLayout) this.findViewById(R.id.loadll);
		loadtext=(TextView) this.findViewById(R.id.loadtext);
		titletext=(TextView) this.findViewById(R.id.titletext);
		//photoimage2=(ImageView) this.findViewById(R.id.photoimage2);
		position_id=getIntent().getStringExtra("position_id");
		if(!position_id.equals("-1")){
			position=(Position) getIntent().getSerializableExtra("position");
			add.setText("�޸�");
			titletext.setText("�޸�ְλ");
			gsmced.setText(position.getGsmc());
			zprsed.setText(position.getZprs());
			zwmced.setText(position.getZwmc());
			zwxzed.setText(position.getZwxz());
			if(Double.parseDouble(position.getBaochi())==1){
				baochick.setChecked(true);
			}
			if((Double.parseDouble(position.getBaozhu())==1)){
				baozhuck.setChecked(true);
			}
			if((Double.parseDouble(position.getShuangxiu())==1)){
				shuangxiuck.setChecked(true);
			}
			if((Double.parseDouble(position.getWxyj())==1)){
				wxyjck.setChecked(true);
			}
			zwyqed.setText(position.getZwyq());
			lxdhed.setText(position.getLxdh());
		}
		this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AddPosition.this.finish();
			}
		});
		allxz=new ArrayList<String>();
		allxz.add("����");
		allxz.add("����");
		allxz.add("2000-3000");
		allxz.add("3000-5000");
		allxz.add("5000-8000");
		allxz.add("8000-12000");
		allxz.add("12000-20000");
		allxz.add("20000����");
		allzprs=new ArrayList<String>();
		allzprs.add("50������");
		allzprs.add("50-100��");
		allzprs.add("100-200��");
		allzprs.add("200-500��");
		allzprs.add("500-1000��");
		allzprs.add("1000������");
		add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*if(gsdzed.getText().toString().trim().equals("")){
					Toast.makeText(AddPosition.this, "�����빫˾��ַ",0).show();
					return;
				}*/
				//mkSearch.geocode(gsdzed.getText().toString().trim(), null);
				
				if(gsmced.getText().toString().trim().equals("��ѡ��˾")){
					Toast.makeText(AddPosition.this, "��ѡ��˾",0).show();
					return;
				}
				if(zprsed.getText().toString().trim().equals("")){
					Toast.makeText(AddPosition.this, "��������Ƹ����",0).show();
					return;
				}
				/*if(gsdzed.getText().toString().trim().equals("")){
					Toast.makeText(AddPosition.this, "�����빫˾��ַ",0).show();
					return;
				}*/
				if(lxdhed.getText().toString().trim().equals("")){
					Toast.makeText(AddPosition.this, "��������ϵ��ʽ",0).show();
					return;
				}
				if(!NumberUtil.isNumeric(lxdhed.getText().toString().trim())){
					Toast.makeText(AddPosition.this, "��ϵ��ʽ����������",0).show();
					return;
				}
			/*	if(statusstr.equals("")){
					Toast.makeText(AddPosition.this, "��ѡ��һ�Ź�˾��Ƭ",0).show();
					return;
				}
				if(statusstr2.equals("")){
					Toast.makeText(AddPosition.this, "��ѡ��һ�Ź�˾Ӫҵִ����Ƭ",0).show();
					return;
				}
				*/
				
				
				View popprobar=getLayoutInflater().inflate(R.layout.popprobar, null);
				dialog=new MyDialog(AddPosition.this,popprobar,R.style.MyDialog,"������...");
				dialog.show();
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						String zwmc="����";
						String zwxz="����";
						String zwyq="��";
					//	String gsjj="��";
						int baochi=0;
						int baozhu=0;
						int shuangxiu=0;
						int wxyj=0;
						if(!zwmced.getText().toString().trim().equals("")){
							zwmc=zwmced.getText().toString();
						}
						if(!zwxzed.getText().toString().trim().equals("")){
							zwxz=zwxzed.getText().toString();
						}
						if(!zwyqed.getText().toString().trim().equals("")){
							zwyq=zwyqed.getText().toString();
						}
				/*		if(!gsjjed.getText().toString().trim().equals("")){
							gsjj=gsjjed.getText().toString();
						}*/
						if(baochick.isChecked()){
							baochi=1;
						}
						if(baozhuck.isChecked()){
							baozhu=1;
						}
						if(shuangxiuck.isChecked()){
							shuangxiu=1;
						}
						if(wxyjck.isChecked()){
							wxyj=1;
						}
						Message message = new Message();
						
								if(position_id.equals("-1")){
									int result1=DBHelp.savesql("insert into position (gs_id,zwmc,zprs,zwxz,fbrq,ckcs,lxdh,zwyq,wxyj,baochi,baozhu,shuangxiu) " +
											"values ((select gs_id from gs where gsmc='"+gsmced.getText().toString().trim()+"'),'"+zwmc+"','"+zprsed.getText().toString()+"','"+zwxz+"',NOW(),1,'"+lxdhed.getText().toString()+"','"+zwyq+"',"+wxyj+","+baochi+","+baozhu+","+shuangxiu+")");
									if(result1>0){
										message.what=ADDPOSITIONSUCCESS;
									}else{
										message.what=ADDPOSITIONSERVERERROR;
									}
								}else{
									int result1=DBHelp.savesql("update position set gs_id=(select gs_id from gs where gsmc ='"+gsmced.getText().toString()+"'),zwmc='"+zwmc+"',zprs='"+zprsed.getText().toString()+"',zwxz='"+zwxz+"',lxdh='"+lxdhed.getText().toString()+"',zwyq='"+zwyq+"',wxyj="+wxyj+",baochi="+baochi+",baozhu="+baozhu+",shuangxiu="+shuangxiu+" where position_id="+position_id);
									if(result1>0){
										message.what=ADDPOSITIONSUCCESS;
									}else{
										message.what=ADDPOSITIONSERVERERROR;
									}
								}
									
									//String result= ImageUtil.uploadFile("UploadFileServlet", Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"photo.jpg");
								/*	if(null!=result){
										String result2=ImageUtil.uploadFile("UploadFileServlet", Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"photo2.jpg");
										if(null!=result2){
									}else{
										message.what=ADDPOSITIONSERVERERROR;
									}
								}else{
									message.what=ADDPOSITIONSERVERERROR;
								}*/
						
						handler.sendMessage(message);
					}
				}).start();
			}
		});
		Runnable updatetest = new Runnable() {

			@Override
			public void run() {
				// �õ�activity�еĸ�Ԫ��
				View view = findViewById(R.id.addpositionrev);
				// ��θ�Ԫ�ص�width��height����0˵��activity�Ѿ���ʼ�����
				if (view != null && view.getWidth() > 0 && view.getHeight() > 0) {
					//new UpdateApp(NearByJob.this).isUpdate();
				//	onCreateGps();
					initdialoginfo();
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
			case 1:
				Toast.makeText(AddPosition.this, "��������ʧ��", 0).show();
				break;
			case 2:
				break;
			case ADDPOSITIONSUCCESS:
				dialog.dismiss();
				Intent intent = new Intent(AddPosition.this,MainActivity.class);
				AddPosition.this.setResult(RESULT_OK, intent);
				AddPosition.this.finish();
				break;
			case ADDPOSITIONSERVERERROR:
				Toast.makeText(AddPosition.this, "���ӳ�ʱ", 1).show();
				dialog.dismiss();
			}
			sc.setVisibility(View.VISIBLE);
			loadll.setVisibility(View.GONE);
		}
	};
	
	public void xzzwmc(View view){
		View menuview=getLayoutInflater().inflate(R.layout.dialogmenu_layout, null);
		menulist=(ListView) menuview.findViewById(R.id.menulist);
		dialog=new MyDialog(this,menuview,R.style.MyDialog,"��Ƹְλ");
		dialog.setCanceledOnTouchOutside(true);
		SimpleAdapter adapter = new SimpleAdapter(this, getPopMenuList(allgw.toArray()), R.layout.contentlist, new String[] { "contentname" },new int[] { R.id.contextz });
		menulist.setAdapter(adapter);
		menulist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				zwmced.setText(allgw.get(position).toString());
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	
	public void xzzprs(View view){
		View menuview=getLayoutInflater().inflate(R.layout.dialogmenu_layout, null);
		menulist=(ListView) menuview.findViewById(R.id.menulist);
		dialog=new MyDialog(this,menuview,R.style.MyDialog,"��Ƹ����");
		dialog.setCanceledOnTouchOutside(true);
		SimpleAdapter adapter = new SimpleAdapter(this, getPopMenuList(allzprs.toArray()), R.layout.contentlist, new String[] { "contentname" },new int[] { R.id.contextz });
		menulist.setAdapter(adapter);
		menulist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				zprsed.setText(allzprs.get(position).toString());
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	public void xzzwxz(View view){
		View menuview=getLayoutInflater().inflate(R.layout.dialogmenu_layout, null);
		menulist=(ListView) menuview.findViewById(R.id.menulist);
		dialog=new MyDialog(this,menuview,R.style.MyDialog,"ְλн��");
		dialog.setCanceledOnTouchOutside(true);
		SimpleAdapter adapter = new SimpleAdapter(this, getPopMenuList(allxz.toArray()), R.layout.contentlist, new String[] { "contentname" },new int[] { R.id.contextz });
		menulist.setAdapter(adapter);
		menulist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				zwxzed.setText(allxz.get(position).toString());
				dialog.dismiss();
			}
		});
		dialog.show();
		
	}
	public void xzgs(View view){
		handler.post(new Runnable() {
			
			@Override
			public void run() {
			 List<Object[]>	objects = DBHelp.selsql("select gsmc from gs where user_id="+user_id+" group by gsmc order by gsmc");
				allgs = new ArrayList<String>();	
			 if(null!=objects){
					for(int i=0;i<objects.size();i++){
						allgs.add(objects.get(i)[0].toString());
					}
					View menuview=getLayoutInflater().inflate(R.layout.dialogmenu_layout, null);
					menulist=(ListView) menuview.findViewById(R.id.menulist);
					dialog=new MyDialog(AddPosition.this,menuview,R.style.MyDialog,"��˾");
					dialog.setCanceledOnTouchOutside(true);
					if(allgs.size()<=0){
						allgs.add("��ӹ�˾");
					}
					 adapter = new SimpleAdapter(AddPosition.this, getPopMenuList(allgs.toArray()), R.layout.contentlist, new String[] { "contentname" },new int[] { R.id.contextz });
					menulist.setAdapter(adapter);
					menulist.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
							if(((HashMap<String,Object>)adapter.getItem(position)).get("contentname").toString().equals("��ӹ�˾")){
								Intent intent = new Intent(AddPosition.this,AddGs.class);
								intent.putExtra("gs_id", "-1");
								startActivity(intent);
							}else{
								gsmced.setText(allgs.get(position).toString());
								dialog.dismiss();
							}
							
						}
					});
					dialog.show();
			}else{
				Toast.makeText(AddPosition.this, "���ӳ�ʱ",0).show();
			}
			 
			}
			});
	
	}
/*	public void xzphoto(View view){
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
						  
						    AddPosition.this.startActivityForResult(i, 123);  
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
						  
						    AddPosition.this.startActivityForResult(i, 789);  
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
	}*/
	/*@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if(resultCode==RESULT_OK) {
					 Bitmap bb=null;
					   if(requestCode==123)  
			            { 
						   bb=ImageUtil.compressImageFromFile(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"photo.jpg");
			            	
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
			            	   bb=ImageUtil.compressImageFromFile(picturePath);
			            } else if(requestCode==789)  
					            { 
								   bb=ImageUtil.compressImageFromFile(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"photo2.jpg");
   
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
					            	   bb=ImageUtil.compressImageFromFile(picturePath);
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
								
							   Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"photo.jpg");
								Drawable drawable = new BitmapDrawable(bitmap);
								int height = (int) ((float) photoimage.getWidth()/drawable.getMinimumWidth() * drawable.getMinimumHeight());
								photoimage.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height));
								photoimage.setImageBitmap(bitmap);
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
								
							   Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"photo2.jpg");
								Drawable drawable = new BitmapDrawable(bitmap);
								int height = (int) ((float) photoimage.getWidth()/drawable.getMinimumWidth() * drawable.getMinimumHeight());
								photoimage2.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height));
								photoimage2.setImageBitmap(bitmap);
								statusstr2="yes";
					   }
					 
				 } 
			}
		}, 100);
		
	
	}*/
	private void initdialoginfo(){
		Message message = new Message();
		List<Object[]> objects = DBHelp.selsql("select zwmc from position group by zwmc order by zwmc");
		allgw= new ArrayList<String>();
		allgw.add("����");

		if(null!=objects){
			for(int i=0;i<objects.size();i++){
				allgw.add(objects.get(i)[0].toString());
			}
			
				message.what=2;
		}else{
			message.what=1;
		}
		handler.sendMessage(message);
		
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
/*	public void onCreateGps() {
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(myListener);
		mLocationClient.setAK("TBnS7qVFleYpGA9cNqOhLp46");
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

	}*/
/*	class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				Toast.makeText(AddPosition.this, "��ȡ����λ����Ϣ", 1);
			else {
				//run(location);
				mLocationClient.stop();
				AddPosition.this.location=location;
				if(null!=location.getAddrStr()){
					gsdzed.setText(location.getAddrStr());
				}
				
				loadtext.setText("������...");
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						initdialoginfo();
					}
				}).start();
				
			}

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
	*/
	
	
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
