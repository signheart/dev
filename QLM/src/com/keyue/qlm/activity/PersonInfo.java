package com.keyue.qlm.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.keyue.qlm.R;
import com.keyue.qlm.util.DBHelp;
import com.keyue.qlm.util.DBManager;
import com.keyue.qlm.util.ImageUtil;
import com.keyue.qlm.util.MyDialog;
import com.keyue.qlm.util.Prototypes;
import com.tencent.connect.auth.QQAuth;
import com.tencent.connect.share.QQShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PersonInfo extends  Activity {
	private String profile_id;
	private LinearLayout loadll;
	private final int FINDPERSONINFOSUCCESS=1;
	private final int FINDPERSONINFOERROR=2;
	private final int FINDPRORECORDNORECORD=3;
	private final int FINDPRORECORDSUCCESS=4;
	private final int FINDPRORECORDERROR=5;
	private final int NOTHAVEUSERCB=6;
	private HashMap<String,Object> hashMap;
	private ImageView proimage;
	private TextView name;
	private TextView xwgw;
	private TextView xwxz;
	private TextView xzgw;
	private TextView nlms;
	TextView baochi;
	TextView baozhu;
	TextView shuangxiu;
	TextView wxyj;
	TextView xwgzdd;
	TextView phonetext;
	private String status;
	private DBManager dbManager;
	List<Object[]> objects=null;
	ScrollView sc;
	private MyDialog dialog;
	Builder builder;
	private TextView tj;
	QQShare qqShare;
	QQAuth qqAuth;
	private IWXAPI api;
	private ListView menulist;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personinfo_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebypersoninfo_layout);
		qqAuth= QQAuth.createInstance(Prototypes.AppID, PersonInfo.this);
		qqShare=new QQShare(this, qqAuth.getQQToken());
		api=WXAPIFactory.createWXAPI(this, Prototypes.WXAppID,true);
		api.registerApp(Prototypes.WXAppID);
		dbManager= new DBManager(this);
		dbManager.opendb();
    	objects= dbManager.sel("select * from user", new Object[]{"userid","username","userimage"});
    	dbManager.closedb();
		profile_id=getIntent().getStringExtra("profile_id");
		loadll=(LinearLayout) findViewById(R.id.loadll);
		sc=(ScrollView) findViewById(R.id.personinfomainsc);
		proimage=(ImageView) findViewById(R.id.proimage);
		name=(TextView) findViewById(R.id.name);
		xwgw=(TextView) findViewById(R.id.xwgw);
		xwxz=(TextView) findViewById(R.id.xwxz);
		xzgw=(TextView) findViewById(R.id.xzgw);
		nlms=(TextView) findViewById(R.id.nlms);
		baochi=(TextView) findViewById(R.id.baochi);
		baozhu=(TextView) findViewById(R.id.baozhu);
		shuangxiu=(TextView) findViewById(R.id.shuangxiu);
		wxyj=(TextView) findViewById(R.id.wxyj);
		xwgzdd=(TextView) findViewById(R.id.xwgzdd);
		phonetext=(TextView) this.findViewById(R.id.phonetext);
		status=getIntent().getStringExtra("status");
		tj=(TextView) this.findViewById(R.id.tj);
this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PersonInfo.this.finish();
			}
		});
		this.findViewById(R.id.home).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PersonInfo.this,MainActivity.class);
				startActivity(intent);
			}
		});
		this.findViewById(R.id.pay).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(objects.size()>0){
					Intent intent = new Intent(PersonInfo.this, Pay.class);
					startActivity(intent);
				}
			}
		});
		Runnable updatetest = new Runnable() {

			@Override
			public void run() {
				// �õ�activity�еĸ�Ԫ��
				View view = findViewById(R.id.personinforev);
				// ��θ�Ԫ�ص�width��height����0˵��activity�Ѿ���ʼ�����
				if (view != null && view.getWidth() > 0 && view.getHeight() > 0) {
				//	new UpdateApp(ZpInfoDetail.this).isUpdate();
					handler.removeCallbacks(this);
					initpersoninfo();
				} else {
					// ���activityû�г�ʼ�������ȴ�5�����ٴμ��
					handler.postDelayed(this, 100);
				}
			}
		};

		handler.post(updatetest);
	}

	
	private void initpersoninfo(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Message message  =new Message();
				int result =DBHelp.savesql("update profile set ckcs=ckcs+1 where profile_id="+profile_id);
				if(result>0){
					List<Object[]>  objects = DBHelp.selsql("select profile_id,name,proimage,xwgw,xwxz,nlms,lxdh,xzgw,baochi,baozhu,shuangxiu,wxyj,xwgzdd from profile where profile_id="+profile_id);
					if(null!=objects){
						hashMap = new HashMap<String, Object>();
						hashMap.put("profile_id", objects.get(0)[0]);
						hashMap.put("name", objects.get(0)[1]);
						hashMap.put("proimage", objects.get(0)[2]);
						hashMap.put("xwgw", objects.get(0)[3]);
						hashMap.put("nlms", objects.get(0)[5]);
						hashMap.put("lxdh", objects.get(0)[6]);
						hashMap.put("xzgw", objects.get(0)[7]);
						hashMap.put("xwxz", "��"+ objects.get(0)[4].toString()+"/��");
						hashMap.put("baochi", objects.get(0)[8]);
						hashMap.put("baozhu", objects.get(0)[9]);
						hashMap.put("shuangxiu", objects.get(0)[10]);
						hashMap.put("wxyj", objects.get(0)[11]);
						hashMap.put("xwgzdd", objects.get(0)[12]);
						message.what=FINDPERSONINFOSUCCESS;
					}else{
						message.what=FINDPERSONINFOERROR;
					}
				}else{
					message.what=FINDPERSONINFOERROR;
				}
				handler.sendMessage(message);
			}
		}).start();
	
	}
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case FINDPERSONINFOERROR:
				Toast.makeText(PersonInfo.this, "���ӳ�ʱ", 0).show();
				if(null!=dialog){
					PersonInfo.this.dialog.dismiss();
				}
				break;

			case FINDPERSONINFOSUCCESS:
				ImageUtil.getDefaultUtil().loadImagebynoSize(proimage, hashMap.get("proimage").toString());
				name.setText(hashMap.get("name").toString());
				xwgw.setText(hashMap.get("xwgw").toString());
				nlms.setText(hashMap.get("nlms").toString());
				xzgw.setText(hashMap.get("xzgw").toString());
				xwxz.setText(hashMap.get("xwxz").toString());
				xwgzdd.setText(hashMap.get("xwgzdd").toString());
				if(((int) Math.rint((Double) hashMap.get("baochi"))==0)){
		        	baochi.setVisibility(View.GONE);
				}
				if(((int) Math.rint((Double) hashMap.get("wxyj"))==0)){
					wxyj.setVisibility(View.GONE);
				}
				if(((int) Math.rint((Double) hashMap.get("baozhu"))==0)){
					baozhu.setVisibility(View.GONE);
				}
				if(((int) Math.rint((Double) hashMap.get("shuangxiu"))==0)){
					shuangxiu.setVisibility(View.GONE);
				}
				if(!status.equals("-1")){
					phonetext.setText(hashMap.get("lxdh").toString());
				}
				sc.setVisibility(View.VISIBLE);
				loadll.setVisibility(View.GONE);
				tj.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						View menuview=getLayoutInflater().inflate(R.layout.dialogmenu_layout, null);
						menulist=(ListView) menuview.findViewById(R.id.menulist);
						dialog=new MyDialog(PersonInfo.this,menuview,R.style.MyDialog,"�Ƽ�������");
						dialog.setCanceledOnTouchOutside(true);
						final SimpleAdapter adapter = new SimpleAdapter(PersonInfo.this,getPopMenuList(new Object[]{"�Ƽ���QQ����","�Ƽ���΢�ź���"}), R.layout.contentlist, new String[] { "contentname" },new int[] { R.id.contextz });
						menulist.setAdapter(adapter);
						menulist.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0, View arg1,
									final int arg2, long arg3) {
								dialog.dismiss();
								new Thread(new Runnable() {
									
									@Override
									public void run() {
										if(((HashMap<String,Object>)adapter.getItem(arg2)).get("contentname").toString().equals("�Ƽ���QQ����")){
											Bundle bundle = new Bundle();
											bundle.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
											bundle.putString(QQShare.SHARE_TO_QQ_TITLE, "�ף��Ƽ���ӦƸ�߸���Ӵ");
											bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY,"���ھ�Ƹ���������ӦƸ���ʺ�����"+hashMap.get("xwgw").toString()+",�뾡����ϵӴ");
											bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "http://www.qq.com");
											bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, Prototypes.ServerUrl+hashMap.get("proimage").toString());
											qqShare.shareToQQ(PersonInfo.this, bundle, new IUiListener() {
												
												@Override
												public void onError(UiError arg0) {
													System.out.println("error"+arg0.errorMessage);
												}
												
												@Override
												public void onComplete(Object arg0) {
													System.out.println("complete");
												}
												
												@Override
												public void onCancel() {
													System.out.println("cancel");
												}
											});
											
										}else{
											if(!api.isWXAppInstalled()){
												Toast.makeText(PersonInfo.this, "�밲װ΢��",0).show();
												return;
											}

											
											try {
											
											// ��ʼ��һ��WXTextObject����
											WXWebpageObject webpageObject = new WXWebpageObject();
											webpageObject.webpageUrl="http://211.149.204.5:20148/Recruitment/selProDetail?profileId="+hashMap.get("profile_id").toString();
											WXMediaMessage msg = new WXMediaMessage();
											msg.mediaObject = webpageObject;
											msg.title="�ף��Ƽ���ӦƸ�߸���Ӵ";
											msg.description="���ھ�Ƹ���������ӦƸ���ʺ�����"+hashMap.get("xwgw").toString()+",�뾡����ϵӴ";
											Bitmap bmp = BitmapFactory.decodeStream(ImageUtil.getDefaultUtil().getStreamFromURL(hashMap.get("proimage").toString()));
											Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 120, 120, true);
											msg.thumbData = ImageUtil.getDefaultUtil().bmpToByteArray(thumbBmp, true);
											bmp.recycle();
											SendMessageToWX.Req req = new SendMessageToWX.Req();
											req.transaction =String.valueOf(System.currentTimeMillis())+"text";
											req.message = msg;
											req.scene =  SendMessageToWX.Req.WXSceneSession;
											// ����api�ӿڷ������ݵ�΢��
											api.sendReq(req);
											} catch (Exception e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											} 
										}
									}
								}).start();
								
							}
						});
						dialog.show();
					}
				});
				break;
			case FINDPRORECORDERROR:
				Toast.makeText(PersonInfo.this, "���ӳ�ʱ", 0).show();
				dialog.dismiss();
				break;
			case FINDPRORECORDNORECORD:
				builder=new AlertDialog.Builder(PersonInfo.this);
				builder.setTitle("��������");
				builder.setMessage("�鿴��ϵ��ʽ��������30���ű�(��¼���ᱣ��7��))");
				builder.setPositiveButton("�ǣ�ȷ�ϲ鿴", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(final DialogInterface dialog, int which) {
						dialog.dismiss();
						View popprobar=getLayoutInflater().inflate(R.layout.popprobar, null);
						PersonInfo.this.dialog=new MyDialog(PersonInfo.this,popprobar,R.style.MyDialog,"������...");
						PersonInfo.this.dialog.show();
						new Thread(new Runnable() {
							@Override
							public void run() {
								Message message = new Message();
								List<Object[]> objects = DBHelp.selsql("select usercb from user where user_id="+PersonInfo.this.objects.get(0)[0].toString());
								if(null!=objects){
									Double usercb=(Double) objects.get(0)[0];
									if(usercb<30){
										message.what=NOTHAVEUSERCB;
									}else{
										int result= DBHelp.savesql("{Call selphone("+PersonInfo.this.objects.get(0)[0].toString()+","+profile_id+",\""+System.currentTimeMillis()+"\",1)}");
										if(result>0){
											phonetext.post(new Runnable() {
												
												@Override
												public void run() {
													phonetext.setText(hashMap.get("lxdh").toString());
													PersonInfo.this.dialog.dismiss();
												}
											});
											
										}else{
										message.what=FINDPERSONINFOERROR;
										}	
									}
								}else{
									message.what=FINDPERSONINFOERROR;
								}
								handler.sendMessage(message);
							}
						}).start();
					}
				});
				builder.setNegativeButton("ȡ��",null);
				builder.show();
				dialog.dismiss();
				break;
			case FINDPRORECORDSUCCESS:
				long time= Long.parseLong(msg.obj.toString());
				if(((System.currentTimeMillis()-time)/1000/60/24)<7){
					phonetext.setText(hashMap.get("lxdh").toString());
				}else{
					builder=new AlertDialog.Builder(PersonInfo.this);
					builder.setTitle("��������");
					builder.setMessage("�鿴��ϵ��ʽ��������30���ű�(��¼���ᱣ��7��))");
					builder.setPositiveButton("�ǣ�ȷ�ϲ鿴", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(final DialogInterface dialog, int which) {
							dialog.dismiss();
							View popprobar=getLayoutInflater().inflate(R.layout.popprobar, null);
							PersonInfo.this.dialog=new MyDialog(PersonInfo.this,popprobar,R.style.MyDialog,"������...");
							PersonInfo.this.dialog.show();
							new Thread(new Runnable() {
								@Override
								public void run() {
									Message message = new Message();
									List<Object[]> objects = DBHelp.selsql("select usercb from user where user_id="+PersonInfo.this.objects.get(0)[0].toString());
									if(null!=objects){
										Double usercb=(Double) objects.get(0)[0];
										if(usercb<30){
											message.what=NOTHAVEUSERCB;
										}else{
											int result= DBHelp.savesql("{Call selphone("+PersonInfo.this.objects.get(0)[0].toString()+","+profile_id+",\""+System.currentTimeMillis()+"\",2)}");
											if(result>0){
												phonetext.post(new Runnable() {
													
													@Override
													public void run() {
														phonetext.setText(hashMap.get("lxdh").toString());
														PersonInfo.this.dialog.dismiss();
													}
												});
											}else{
												message.what=FINDPERSONINFOERROR;
											}	
										}
									}else{
										message.what=FINDPERSONINFOERROR;
									}
									handler.sendMessage(message);
								}
							}).start();
						}
					});
					builder.setNegativeButton("ȡ��",null);
					builder.show();
					dialog.dismiss();
				}
				dialog.dismiss();
				break;
			case NOTHAVEUSERCB:
				Toast.makeText(PersonInfo.this, "���Ĳű����㣬���ȹ���ű�",0).show();
				PersonInfo.this.dialog.dismiss();
				break;
			}
		}
		
		
	};
	
	
	
	
	public void telphone(View view){
		
		if(!phonetext.getText().toString().equals("��������")){
			Intent intent = new Intent();
			intent.setAction("android.intent.action.CALL");
			intent.setData(Uri.parse("tel:"
					+ phonetext.getText().toString().trim()));
			startActivity(intent);
		}else{
			if(objects.size()>0){
				selprorecord();
			}else{
				Intent intent = new Intent(PersonInfo.this, LoginReg.class);
				intent.putExtra("act", "main");
				startActivityForResult(intent, 123);
			}
		}
	}
	
	private void selprorecord(){
		View popprobar=getLayoutInflater().inflate(R.layout.popprobar, null);
		dialog=new MyDialog(PersonInfo.this,popprobar,R.style.MyDialog,"������...");
		dialog.show();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Message message = new Message();
				List<Object[]> objects = DBHelp.selsql("select time from  prorecord where user_id="+PersonInfo.this.objects.get(0)[0].toString()+" and profile_id="+profile_id);
				if(null!=objects){
					if(objects.size()>0){
						message.obj=objects.get(0)[0].toString();
						message.what=FINDPRORECORDSUCCESS;
					}else{
						message.what=FINDPRORECORDNORECORD;
					}
				}else{
					message.what=FINDPRORECORDERROR;
				}
				handler.sendMessage(message);
			}
		}).start();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK){
			if(requestCode==123){
				dbManager.opendb();
		    	objects= dbManager.sel("select * from user", new Object[]{"userid","username","userimage"});
		    	dbManager.closedb();
		    	selprorecord();
			}
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
