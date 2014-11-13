package com.keyue.qlm.activity;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.keyue.qlm.R;

import com.keyue.qlm.util.AccessTokenKeeper;
import com.keyue.qlm.util.DBHelp;
import com.keyue.qlm.util.DBManager;
import com.keyue.qlm.util.ExitApplication;
import com.keyue.qlm.util.ImageUtil;
import com.keyue.qlm.util.Prototypes;

import com.keyue.qlm.util.MyDialog;
import com.keyue.qlm.util.PageUtil;
import com.keyue.qlm.util.UpdateApp;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.tauth.Tencent;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity implements OnTouchListener,
		GestureDetector.OnGestureListener {
	private static final int FINDZPINFOSUCCESS = 1;
	private static final int FINDZPINFOERROR = 2;
	private LinearLayout zp1;
	private LinearLayout zp2;
	private PageUtil pageUtil;
	private int totalindex = 0;
	private ProgressBar loadprobypage;
	private ProgressBar loadpro;
	private GestureDetector detector;
	private LinearLayout loadll;
	ScrollView sc;
	private int index = 0;
	private RelativeLayout bottomenu;
	private RelativeLayout titlerev;
	TextView myjob;
	TextView myzp;
	TextView fjob;
	TextView fjrc;
	TextView othercity;
	TextView space;
	private Dialog dialog;
	ListView menulist;
	private LinearLayout zpll;
	MyAdapter adapter;
	private boolean hasMeasured = false;// �Ƿ�Measured.
	private LinearLayout layout_left;// ��߲���
	private RelativeLayout layout_right;// �ұ߲���
	private ListView lv_set;// ���ò˵�
	private Button menu_set;

	/** ÿ���Զ�չ��/�����ķ�Χ */
	private int MAX_WIDTH = 0;
	/** ÿ���Զ�չ��/�������ٶ� */
	private final static int SPEED = 10;

	private final static int sleep_time = 1;

	private GestureDetector mGestureDetector;// ����
	private boolean isScrolling = false;
	private float mScrollX; // ���黬������
	private int window_width;// ��Ļ�Ŀ��

	private String TAG = "jj";

	private View view = null;// �����view

	private List<HashMap<String, Object>> data;

	private LinearLayout mylaout;
	private Button search;
	private DBManager dbManager;
	List<Object[]> objects;
	private final int FINDINFOPAGESUCCESS=3;
	private final int NOTIFYSUCCESS=4;
	private Tencent tencent;
	private IWXAPI api;
	private IWeiboShareAPI mWeiboShareAPI;
	private WeiboAuth weiboAuth;
	private Oauth2AccessToken oauth2AccessToken;
	private SsoHandler ssoHandler;
	ImageUtil imageUtil;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imageUtil=ImageUtil.getDefaultUtil(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		tencent=Tencent.createInstance(Prototypes.AppID, this);
		api=WXAPIFactory.createWXAPI(this, Prototypes.WXAppID,true);
		api.registerApp(Prototypes.WXAppID);
		weiboAuth= new WeiboAuth(this, Prototypes.SinaAppID,Prototypes.REDIRECT_URL, Prototypes.SCOPE);
		ssoHandler = new SsoHandler(MainActivity.this, weiboAuth);
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Prototypes.SinaAppID);
		mWeiboShareAPI.registerApp(); 
		zp1 = (LinearLayout) findViewById(R.id.zp1);
		dbManager = new DBManager(this);
		zp2 = (LinearLayout) findViewById(R.id.zp2);
		loadprobypage = (ProgressBar) findViewById(R.id.loadprobypage);
		loadpro = (ProgressBar) findViewById(R.id.loadpro);
		sc = (ScrollView) findViewById(R.id.mainsc);
		loadll = (LinearLayout) findViewById(R.id.loadll);
		// detector = new GestureDetector((OnGestureListener) this);
		ExitApplication.getInstance().addActivity(this);
		bottomenu = (RelativeLayout) this.findViewById(R.id.bottomenu);
		titlerev = (RelativeLayout) this.findViewById(R.id.titlerev);
		myjob = (TextView) findViewById(R.id.myjob);
		myzp = (TextView) findViewById(R.id.myzp);
		fjob = (TextView) findViewById(R.id.fjob);
		fjrc = (TextView) findViewById(R.id.fjrc);
		othercity = (TextView) findViewById(R.id.othercity);
		space = (TextView) findViewById(R.id.space);
		InitView();
		search = (Button) findViewById(R.id.search);
		search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, SearchJob.class);
				intent.putExtra("city", "1");
				startActivity(intent);
			}
		});
		myjob.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dbManager.opendb();
				List<Object[]> objects = dbManager.sel("select * from user",
						new Object[] { "userid", "username", "userimage" });
				dbManager.closedb();
				if (objects.size() > 0) {
					Intent intent = new Intent(MainActivity.this, MyYpZw.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(MainActivity.this,
							LoginReg.class);
					intent.putExtra("act", "main");
					startActivityForResult(intent, 3333);
				}

			}
		});
		myzp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dbManager.opendb();
				List<Object[]> objects = dbManager.sel("select * from user",
						new Object[] { "userid", "username", "userimage" });
				dbManager.closedb();
				if (objects.size() > 0) {
					Intent intent = new Intent(MainActivity.this, MyFbZw.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(MainActivity.this,
							LoginReg.class);
					intent.putExtra("act", "main");
					startActivityForResult(intent, 4444);
				}

			}
		});
		fjob.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(MainActivity.this, NearByJob.class);
				startActivity(intent);
			}
		});
		fjrc.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						NearByPerson.class);
				startActivity(intent);
			}
		});
		othercity.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				View menuview = getLayoutInflater().inflate(
						R.layout.dialogmenu_layout, null);
				menulist = (ListView) menuview.findViewById(R.id.menulist);
				dialog = new MyDialog(MainActivity.this, menuview,
						R.style.MyDialog, "��������");
				dialog.setCanceledOnTouchOutside(true);
				final SimpleAdapter adapter = new SimpleAdapter(
						MainActivity.this, getPopMenuList(new Object[] { "�ҹ���",
								"���˲�" }), R.layout.contentlist,
						new String[] { "contentname" },
						new int[] { R.id.contextz });
				menulist.setAdapter(adapter);
				menulist.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						if (((HashMap<String, Object>) adapter
								.getItem(position)).get("contentname")
								.toString().equals("�ҹ���")) {
							Intent intent = new Intent(MainActivity.this,
									OtherCityByJob.class);
							startActivity(intent);
							dialog.dismiss();
						} else {
							Intent intent = new Intent(MainActivity.this,
									OtherCityByPerson.class);
							startActivity(intent);
							dialog.dismiss();
						}
					}

				});
				dialog.show();
			}
		});
		space.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				View menuview = getLayoutInflater().inflate(
						R.layout.dialogmenu_layout, null);
				menulist = (ListView) menuview.findViewById(R.id.menulist);
				dialog = new MyDialog(MainActivity.this, menuview,
						R.style.MyDialog, "������Ϣ");
				dialog.setCanceledOnTouchOutside(true);
				final SimpleAdapter adapter = new SimpleAdapter(
						MainActivity.this, getPopMenuList(new Object[] {
								"��Ӽ���", "����ְλ" }), R.layout.contentlist,
						new String[] { "contentname" },
						new int[] { R.id.contextz });
				menulist.setAdapter(adapter);
				menulist.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						dbManager.opendb();
						List<Object[]> objects = dbManager.sel(
								"select * from user", new Object[] { "userid",
										"username", "userimage" });
						dbManager.closedb();
						if (((HashMap<String, Object>) adapter
								.getItem(position)).get("contentname")
								.toString().equals("��Ӽ���")) {
							if (objects.size() > 0) {
								Intent intent = new Intent(MainActivity.this,
										AddProfile.class);
								intent.putExtra("profile_id", "-1");
								startActivityForResult(intent, 111);
							} else {
								Intent intent = new Intent(MainActivity.this,
										LoginReg.class);
								intent.putExtra("act", "main");
								startActivityForResult(intent, 1111);
							}
							dialog.dismiss();
						} else {
							if (objects.size() > 0) {
								Intent intent = new Intent(MainActivity.this,
										AddPosition.class);
								intent.putExtra("position_id", "-1");
								startActivityForResult(intent, 222);
							} else {
								Intent intent = new Intent(MainActivity.this,
										LoginReg.class);
								intent.putExtra("act", "main");
								startActivityForResult(intent, 2222);
							}
							dialog.dismiss();
						}
					}

				});
				dialog.show();

			}
		});

		Runnable updatetest = new Runnable() {

			@Override
			public void run() {
				// �õ�activity�еĸ�Ԫ��
				View view = findViewById(R.id.mainrev);
				// ��θ�Ԫ�ص�width��height����0˵��activity�Ѿ���ʼ�����
				if (view != null && view.getWidth() > 0 && view.getHeight() > 0) {

					handler.removeCallbacks(this);
							initzpinfo();

					// new UpdateApp(MainActivity.this).isUpdate();
					handler.removeCallbacks(this);
					/*handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							new UpdateApp(MainActivity.this).isUpdate();
						}
					}, 5000);*/

				} else {
					// ���activityû�г�ʼ�������ȴ�5�����ٴμ��
					handler.postDelayed(this, 100);
				}
			}
		};

		handler.post(updatetest);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
			}
		if (resultCode == RESULT_OK) {
			if (requestCode == 111) {
				Toast.makeText(this, "��Ӽ����ɹ�", 0).show();
			} else if (requestCode == 222) {
				sc.setVisibility(View.GONE);
				loadll.setVisibility(View.VISIBLE);
				initzpinfo();
				Toast.makeText(this, "����ְλ�ɹ�", 0).show();
			} else if (requestCode == 123) {
				sc.setVisibility(View.GONE);
				loadll.setVisibility(View.VISIBLE);
				initzpinfo();

			} else if (requestCode == 1111) {
				Intent intent = new Intent(MainActivity.this, AddProfile.class);
				intent.putExtra("profile_id", "-1");
				startActivityForResult(intent, 111);
			} else if (requestCode == 2222) {
				Intent intent = new Intent(MainActivity.this, AddPosition.class);
				intent.putExtra("position_id", "-1");
				startActivityForResult(intent, 222);
			} else if (requestCode == 3333) {
				Intent intent = new Intent(MainActivity.this, MyYpZw.class);
				startActivity(intent);
			} else if (requestCode == 4444) {
				Intent intent = new Intent(MainActivity.this, MyFbZw.class);
				startActivity(intent);
			}else if(requestCode == 888){
				sc.setVisibility(View.GONE);
				loadll.setVisibility(View.VISIBLE);
				initzpinfo();
			}else if(requestCode == 919){
				Toast.makeText(this, "������Ϣ�ɹ�����л����֧��", 0).show();
			}

		}
	}

	private void initlistnotfly() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Message message = new Message();
				dbManager.opendb();
				 objects = dbManager.sel("select * from user",
						new Object[] { "userid", "username", "userimage" });
				dbManager.closedb();
				data.clear();
				if (objects.size() > 0) {
					HashMap<String, Object> hashMap = new HashMap<String, Object>();
					hashMap.put("tv_item", "������ҳ");
					hashMap.put("itemimage", R.drawable.menu_home);
					data.add(hashMap);
					hashMap = new HashMap<String, Object>();
					hashMap.put("tv_item", objects.get(0)[1].toString());
					hashMap.put("itemimage", objects.get(0)[2].toString());
					hashMap.put("userid", objects.get(0)[0].toString());
					data.add(hashMap);
					hashMap = new HashMap<String, Object>();
					hashMap.put("tv_item", "����ű�");
					hashMap.put("itemimage", R.drawable.menu_star);
					data.add(hashMap);
					hashMap = new HashMap<String, Object>();
					hashMap.put("tv_item", "�ҵļ���");
					hashMap.put("itemimage", R.drawable.menu_feed);
					data.add(hashMap);
					hashMap = new HashMap<String, Object>();
					hashMap.put("tv_item", "�ҵĹ�˾");
					hashMap.put("itemimage", R.drawable.menu_feed);
					data.add(hashMap);
					hashMap = new HashMap<String, Object>();
					hashMap.put("tv_item", "���������");
					hashMap.put("itemimage", R.drawable.menu_message);
					data.add(hashMap);
					hashMap = new HashMap<String, Object>();
					hashMap.put("tv_item", "������");
					hashMap.put("itemimage", R.drawable.menu_city);
					data.add(hashMap);
					hashMap = new HashMap<String, Object>();
					hashMap.put("tv_item", "��Ҫ����");
					hashMap.put("itemimage", R.drawable.menu_chart);
					data.add(hashMap);
					hashMap = new HashMap<String, Object>();
					hashMap.put("tv_item", "ע����¼");
					hashMap.put("itemimage", R.drawable.menu_power);
					data.add(hashMap);
				} else {

					HashMap<String, Object> hashMap = new HashMap<String, Object>();
					hashMap.put("tv_item", "������ҳ");
					hashMap.put("itemimage", R.drawable.menu_home);
					data.add(hashMap);
					hashMap = new HashMap<String, Object>();
					hashMap.put("tv_item", "��¼");
					hashMap.put("itemimage", R.drawable.menu_login);
					data.add(hashMap);
					hashMap = new HashMap<String, Object>();
					hashMap.put("tv_item", "���������");
					hashMap.put("itemimage", R.drawable.menu_message);
					data.add(hashMap);
					hashMap = new HashMap<String, Object>();
					hashMap.put("tv_item", "������");
					hashMap.put("itemimage", R.drawable.menu_city);
					data.add(hashMap);
					hashMap = new HashMap<String, Object>();
					hashMap.put("tv_item", "��Ҫ����");
					hashMap.put("itemimage", R.drawable.menu_chart);
					data.add(hashMap);
				}
			message.what=NOTIFYSUCCESS;
			handler.sendMessage(message);
			}
		}).start();
		
	}

	private void initzpinfo() {
		zp1.removeAllViews();
		zp2.removeAllViews();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				totalindex = 0;
				Message message = new Message();
				List<Object[]> count = DBHelp
						.selsql("select count(*) from position");
				pageUtil = new PageUtil();
				pageUtil.setPagesize(20);
				if (null != count) {
					if (count.size() > 0) {
						pageUtil.setTotalcount((int) (Math.rint((Double) count
								.get(0)[0])));
						List<Object[]> objects = DBHelp
								.selsql("select position_id,zwmc,zprs,zpimage,zwxz,gsmc,fbrq,gsdz,ckcs,wxyj,baochi,baozhu,shuangxiu from position inner join gs on gs.gs_id=position.gs_id order by fbrq desc limit "
										+ (pageUtil.getPageindex() - 1)
										* pageUtil.getPagesize()
										+ ","
										+ pageUtil.getPagesize());

						if (null != objects) {
							SimpleDateFormat dateFormat = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm");
							List<HashMap<String, Object>> hashMaps = new ArrayList<HashMap<String, Object>>();
							for (int i = 0; i < objects.size(); i++) {
								HashMap<String, Object> hashMap = new HashMap<String, Object>();
								hashMap.put("position_id", objects.get(i)[0]);
								hashMap.put("zwmc", objects.get(i)[1]);
								hashMap.put("zprs", objects.get(i)[2]);
								hashMap.put("zpimage", objects.get(i)[3]);
								hashMap.put("zwxz", objects.get(i)[4]);
								hashMap.put("gsmc",
										objects.get(i)[5].toString());
								hashMap.put("fbrq", dateFormat.format(new Date(
										objects.get(i)[6].toString())));
								hashMap.put("gsdz", objects.get(i)[7]);
								hashMap.put("ckcs", objects.get(i)[8]);
								hashMap.put("wxyj", objects.get(i)[9]);
								hashMap.put("baochi", objects.get(i)[10]);
								hashMap.put("baozhu", objects.get(i)[11]);
								hashMap.put("shuangxiu", objects.get(i)[12]);
								hashMaps.add(hashMap);

							}
							pageUtil.setHashMaps(hashMaps);
							message.what = FINDZPINFOSUCCESS;

						} else {
							message.what = FINDZPINFOERROR;

						}
					} else {
						message.what = FINDZPINFOERROR;
					}
				} else {
					message.what = FINDZPINFOERROR;
				}
				handler.sendMessage(message);
			}
		}).start();
				

	}

	private void getzpinfobypage() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message message = new Message();
				List<Object[]> objects = DBHelp
						.selsql("select position_id,zwmc,zprs,zpimage,zwxz,gsmc,fbrq,gsdz,ckcs,wxyj,baochi,baozhu,shuangxiu from position inner join gs on gs.gs_id=position.gs_id order by fbrq desc limit "
								+ (pageUtil.getPageindex() - 1)
								* pageUtil.getPagesize()
								+ ","
								+ pageUtil.getPagesize());
				if (null != objects) {
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm");
					List<HashMap<String, Object>> hashMaps = new ArrayList<HashMap<String, Object>>();
					for (int i = 0; i < objects.size(); i++) {
						HashMap<String, Object> hashMap = new HashMap<String, Object>();
						hashMap.put("position_id", objects.get(i)[0]);
						hashMap.put("zwmc", objects.get(i)[1]);
						hashMap.put("zprs", objects.get(i)[2]);
						hashMap.put("zpimage", objects.get(i)[3]);
						hashMap.put("zwxz", objects.get(i)[4]);
						hashMap.put("gsmc", objects.get(i)[5].toString());
						hashMap.put("fbrq", dateFormat.format(new Date(objects
								.get(i)[6].toString())));
						hashMap.put("gsdz", objects.get(i)[7]);
						hashMap.put("ckcs", objects.get(i)[8]);
						hashMap.put("wxyj", objects.get(i)[9]);
						hashMap.put("baochi", objects.get(i)[10]);
						hashMap.put("baozhu", objects.get(i)[11]);
						hashMap.put("shuangxiu", objects.get(i)[12]);
						hashMaps.add(hashMap);

					}
					pageUtil.setHashMaps(hashMaps);
				
					message.what=FINDINFOPAGESUCCESS;
				}else{
					message.what=FINDZPINFOERROR;
				}
				handler.sendMessage(message);

			}
		}).start();

	}

	public void bulidView(int status, final List<HashMap<String, Object>> data) {
		RelativeLayout.LayoutParams layoutst = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutst.setMargins(2, 3, 2, 3);
		View view;
		ImageView zpimage;
		TextView ckcs;
		TextView zpcreatedate;
		TextView gsmc;
		TextView zwmc;
		TextView zpdy;
		TextView zprs;
		TextView gsdz;
		TextView baochi;
		TextView baozhu;
		TextView shuangxiu;
		TextView wxyj;
		if (status == 1) {
			for (int i = 0; i < data.size(); i++) {
				totalindex++;
				view = getLayoutInflater().inflate(R.layout.diyrev, null);
				zpimage = (ImageView) view.findViewById(R.id.zpimage);
				ckcs = (TextView) view.findViewById(R.id.ckcs);
				zpcreatedate = (TextView) view.findViewById(R.id.zpcreatedate);
				gsmc = (TextView) view.findViewById(R.id.gsmc);
				zwmc = (TextView) view.findViewById(R.id.zwmc);
				zpdy = (TextView) view.findViewById(R.id.zpdy);
				zprs = (TextView) view.findViewById(R.id.zprs);
				gsdz = (TextView) view.findViewById(R.id.gsdz);
				baochi = (TextView) view.findViewById(R.id.baochi);
				wxyj = (TextView) view.findViewById(R.id.wxyj);
				baozhu = (TextView) view.findViewById(R.id.baozhu);
				shuangxiu = (TextView) view.findViewById(R.id.shuangxiu);
				if (((int) Math.rint((Double) data.get(i).get("baochi")) == 0)) {
					baochi.setVisibility(View.GONE);
				}
				if (((int) Math.rint((Double) data.get(i).get("wxyj")) == 0)) {
					wxyj.setVisibility(View.GONE);
				}
				if (((int) Math.rint((Double) data.get(i).get("baozhu")) == 0)) {
					baozhu.setVisibility(View.GONE);
				}
				if (((int) Math.rint((Double) data.get(i).get("shuangxiu")) == 0)) {
					shuangxiu.setVisibility(View.GONE);
				}
				imageUtil.loadImagebydra(zpimage, data.get(i).get("zpimage").toString());
				ckcs.setText((int) Math.rint((Double) data.get(i).get("ckcs"))
						+ "");
				zpcreatedate.setText(data.get(i).get("fbrq").toString());
				gsmc.setText(data.get(i).get("gsmc").toString());
				zwmc.setText(data.get(i).get("zwmc").toString());
				zpdy.setText("��" + data.get(i).get("zwxz").toString());
				zprs.setText(data.get(i).get("zprs").toString());
				gsdz.setText(data.get(i).get("gsdz").toString());
				view.setLayoutParams(layoutst);
				final int j = i;

				if (i % 2 == 0) {
					zp1.addView(view);
				} else {
					zp2.addView(view);
				}

				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MainActivity.this,
								ZpInfoDetail.class);
						intent.putExtra("position_id",
								data.get(j).get("position_id").toString());
						startActivity(intent);
					}
				});

			}

		} else {
			for (int i = 0; i < data.size(); i++) {
				totalindex++;
				view = getLayoutInflater().inflate(R.layout.diyrev, null);
				zpimage = (ImageView) view.findViewById(R.id.zpimage);
				ckcs = (TextView) view.findViewById(R.id.ckcs);
				zpcreatedate = (TextView) view.findViewById(R.id.zpcreatedate);
				gsmc = (TextView) view.findViewById(R.id.gsmc);
				zwmc = (TextView) view.findViewById(R.id.zwmc);
				zpdy = (TextView) view.findViewById(R.id.zpdy);
				zprs = (TextView) view.findViewById(R.id.zprs);
				gsdz = (TextView) view.findViewById(R.id.gsdz);
				baochi = (TextView) view.findViewById(R.id.baochi);
				wxyj = (TextView) view.findViewById(R.id.wxyj);
				baozhu = (TextView) view.findViewById(R.id.baozhu);
				shuangxiu = (TextView) view.findViewById(R.id.shuangxiu);
				if (((int) Math.rint((Double) data.get(i).get("baochi")) == 0)) {
					baochi.setVisibility(View.GONE);
				}
				if (((int) Math.rint((Double) data.get(i).get("wxyj")) == 0)) {
					wxyj.setVisibility(View.GONE);
				}
				if (((int) Math.rint((Double) data.get(i).get("baozhu")) == 0)) {
					baozhu.setVisibility(View.GONE);
				}
				if (((int) Math.rint((Double) data.get(i).get("shuangxiu")) == 0)) {
					shuangxiu.setVisibility(View.GONE);
				}
				imageUtil.loadImagebydra(zpimage, data.get(i).get("zpimage").toString());
				ckcs.setText((int) Math.rint((Double) data.get(i).get("ckcs"))
						+ "");
				zpcreatedate.setText(data.get(i).get("fbrq").toString());
				gsmc.setText(data.get(i).get("gsmc").toString());
				zwmc.setText(data.get(i).get("zwmc").toString());
				zpdy.setText("��" + data.get(i).get("zwxz").toString());
				zprs.setText(data.get(i).get("zprs").toString());
				gsdz.setText(data.get(i).get("gsdz").toString());
				view.setLayoutParams(layoutst);
				final int j = i;

				if (i % 2 == 0) {
					zp2.addView(view);
				} else {
					zp1.addView(view);
				}

				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MainActivity.this,
								ZpInfoDetail.class);
						intent.putExtra("position_id",
								data.get(j).get("position_id").toString());
						startActivity(intent);
					}
				});

			}

		}

	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case FINDZPINFOERROR:
				Toast.makeText(MainActivity.this, "���ӳ�ʱ", 0).show();
				loadprobypage.setVisibility(View.GONE);
				break;
			case FINDZPINFOSUCCESS:
				bulidView(1, pageUtil.getHashMaps());
				sc.setVisibility(View.VISIBLE);
				loadll.setVisibility(View.GONE);
				break;
			case FINDINFOPAGESUCCESS:
				if (zp1.getHeight() > zp2.getHeight()) {
					bulidView(2, pageUtil.getHashMaps());
				} else {
					bulidView(1, pageUtil.getHashMaps());
				}
				loadprobypage.setVisibility(View.GONE);
				break;
			case NOTIFYSUCCESS:
				adapter.notifyDataSetChanged();
				new AsynMove().execute(-SPEED);
				disableSubControls(layout_right);
				break;
			}

		}

	};

	/***
	 * ��ʼ��view
	 */
	void InitView() {
		dbManager.opendb();
		List<Object[]> objects = dbManager.sel("select * from user",
				new Object[] { "userid", "username", "userimage" });
		dbManager.closedb();

		data = new ArrayList<HashMap<String, Object>>();
		if (objects.size() > 0) {

			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put("tv_item", "������ҳ");
			hashMap.put("itemimage", R.drawable.menu_home);
			this.data.add(hashMap);
			hashMap = new HashMap<String, Object>();
			hashMap.put("tv_item", objects.get(0)[1].toString());
			hashMap.put("itemimage", objects.get(0)[2].toString());
			hashMap.put("userid", objects.get(0)[0].toString());
			this.data.add(hashMap);
			hashMap = new HashMap<String, Object>();
			hashMap.put("tv_item", "����ű�");
			hashMap.put("itemimage", R.drawable.menu_star);
			this.data.add(hashMap);
			hashMap = new HashMap<String, Object>();
			hashMap.put("tv_item", "�ҵļ���");
			hashMap.put("itemimage", R.drawable.menu_feed);
			this.data.add(hashMap);
			hashMap = new HashMap<String, Object>();
			hashMap.put("tv_item", "�ҵĹ�˾");
			hashMap.put("itemimage", R.drawable.menu_feed);
			this.data.add(hashMap);
			hashMap = new HashMap<String, Object>();
			hashMap.put("tv_item", "���������");
			hashMap.put("itemimage", R.drawable.menu_message);
			this.data.add(hashMap);
			hashMap = new HashMap<String, Object>();
			hashMap.put("tv_item", "������");
			hashMap.put("itemimage", R.drawable.menu_city);
			this.data.add(hashMap);
			hashMap = new HashMap<String, Object>();
			hashMap.put("tv_item", "��Ҫ����");
			hashMap.put("itemimage", R.drawable.menu_chart);
			this.data.add(hashMap);
			hashMap = new HashMap<String, Object>();
			hashMap.put("tv_item", "ע����¼");
			hashMap.put("itemimage", R.drawable.menu_power);
			this.data.add(hashMap);
		} else {

			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put("tv_item", "������ҳ");
			hashMap.put("itemimage", R.drawable.menu_home);
			data.add(hashMap);
			hashMap = new HashMap<String, Object>();
			hashMap.put("tv_item", "��¼");
			hashMap.put("itemimage", R.drawable.menu_login);
			data.add(hashMap);
			hashMap = new HashMap<String, Object>();
			hashMap.put("tv_item", "���������");
			hashMap.put("itemimage", R.drawable.menu_message);
			this.data.add(hashMap);
			hashMap = new HashMap<String, Object>();
			hashMap.put("tv_item", "������");
			hashMap.put("itemimage", R.drawable.menu_city);
			data.add(hashMap);
			hashMap = new HashMap<String, Object>();
			hashMap.put("tv_item", "��Ҫ����");
			hashMap.put("itemimage", R.drawable.menu_chart);
			data.add(hashMap);
		}
		adapter = new MyAdapter(data);
		layout_left = (LinearLayout) findViewById(R.id.layout_left);
		layout_right = (RelativeLayout) findViewById(R.id.layout_right);
		menu_set = (Button) findViewById(R.id.menuset);
		lv_set = (ListView) findViewById(R.id.lv_set);
		mylaout = (LinearLayout) findViewById(R.id.mylaout);
		lv_set.setAdapter(adapter);
		zpll = (LinearLayout) findViewById(R.id.zpll);
		lv_set.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (data.get(position).get("tv_item").toString().equals("������")) {
					handler.postDelayed(new Runnable() {
						public void run() {
							new UpdateApp(MainActivity.this).isUpdate();
						}
					}, 500);
				} else if (data.get(position).get("tv_item").toString()
						.equals("������ҳ")) {
					new AsynMove().execute(SPEED);
					startSubControls(layout_right);
				} else if (data.get(position).get("tv_item").toString()
						.equals("��¼")) {
					Intent intent = new Intent(MainActivity.this,
							LoginReg.class);
					intent.putExtra("act", "main");
					startActivityForResult(intent, 123);
					new AsynMove().execute(SPEED);
					startSubControls(layout_right);

				} else if (data.get(position).get("tv_item").toString()
						.equals("ע����¼")) {
					new AsynMove().execute(SPEED);
					startSubControls(layout_right);
					sc.setVisibility(View.GONE);
					loadll.setVisibility(View.VISIBLE);
					dbManager.opendb();
					dbManager.saveorupdate("delete from main.user");
					dbManager.closedb();
					initzpinfo();

				} else if (data.get(position).get("tv_item").toString()
						.equals("�ҵļ���")) {
					Intent intent = new Intent(MainActivity.this,
							MyProFiles.class);
					startActivity(intent);
				} else if (data.get(position).get("tv_item").toString()
						.equals("�ҵĹ�˾")) {
					Intent intent = new Intent(MainActivity.this, MyGs.class);
					startActivity(intent);
				} else if (data.get(position).get("tv_item").toString()
						.equals("����ű�")) {
					Intent intent = new Intent(MainActivity.this, Pay.class);
					startActivity(intent);
				}else if (data.get(position).get("tv_item").toString()
						.equals("��Ҫ����")) {
					Intent intent = new Intent(MainActivity.this, FeedBack.class);
					startActivityForResult(intent, 919);
					new AsynMove().execute(SPEED);
					startSubControls(layout_right);
				}else if (data.get(position).get("tv_item").toString()
						.equals("���������")) {
					View menuview = getLayoutInflater().inflate(
							R.layout.dialogfxurl_layout, null);
					menulist = (ListView) menuview.findViewById(R.id.menulist);
					dialog = new MyDialog(MainActivity.this, menuview,
							R.style.MyDialog, "������Ϣ");
					dialog.setCanceledOnTouchOutside(true);
					Window window = dialog.getWindow();
					window.setGravity(Gravity.BOTTOM);
					dialog.show();
				}else if (data.get(position).get("tv_item").toString()
						.equals(MainActivity.this.objects.get(0)[1].toString())) {
					Intent intent = new Intent(MainActivity.this, UserInfo.class);
					startActivityForResult(intent, 888);
					new AsynMove().execute(SPEED);
					startSubControls(layout_right);
				}

			}
		});

		layout_right.setOnTouchListener(this);
		menu_set.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layout_right
						.getLayoutParams();
				if (layoutParams.rightMargin >= 0) {
							initlistnotfly();
				} else {
					new AsynMove().execute(SPEED);
					startSubControls(layout_right);
				}
			}
		});
		mGestureDetector = new GestureDetector(this);
		zpll.setOnTouchListener(this);
		sc.setOnTouchListener(this);
		// ���ó�������
		mGestureDetector.setIsLongpressEnabled(false);
		getMAX_WIDTH();
	}

	void getMAX_WIDTH() {
		ViewTreeObserver viewTreeObserver = layout_right.getViewTreeObserver();
		// ��ȡ�ؼ����
		viewTreeObserver.addOnPreDrawListener(new OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				if (!hasMeasured) {
					window_width = getWindowManager().getDefaultDisplay()
							.getWidth();
					MAX_WIDTH = layout_left.getWidth();
					RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layout_right
							.getLayoutParams();
					RelativeLayout.LayoutParams layoutParams_1 = (RelativeLayout.LayoutParams) layout_left
							.getLayoutParams();
					ViewGroup.LayoutParams layoutParams_2 = mylaout
							.getLayoutParams();
					// ע�⣺ ����layout_left�Ŀ�ȡ���ֹ�����ƶ���ʱ��ؼ�����ѹ
					layoutParams.width = window_width;
					layout_right.setLayoutParams(layoutParams);

					// ����layout_right�ĳ�ʼλ��.
					layoutParams_1.rightMargin = window_width;
					layout_left.setLayoutParams(layoutParams_1);
					// ע�⣺����lv_set�Ŀ�ȷ�ֹ�����ƶ���ʱ��ؼ�����ѹ
					layoutParams_2.width = MAX_WIDTH;
					mylaout.setLayoutParams(layoutParams_2);
					hasMeasured = true;
				}
				return true;
			}
		});

	}

	private int verticalMinDistance = 50;
	private int minVelocity = 0;

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (view == sc) {

			if (e1.getY() - e2.getY() > verticalMinDistance
					&& Math.abs(velocityY) > minVelocity) {
				bottomenu.setVisibility(View.GONE);
				titlerev.setVisibility(View.GONE);

			} else if (e2.getY() - e1.getY() > verticalMinDistance
					&& Math.abs(velocityY) > minVelocity) {
				bottomenu.setVisibility(View.VISIBLE);
				titlerev.setVisibility(View.VISIBLE);

			}

		}

		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		view = v;// ��¼����Ŀؼ�

		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:

			if (view != null && view == layout_right || view == zpll
					|| view == sc) {
				RelativeLayout.LayoutParams layoutParams = (android.widget.RelativeLayout.LayoutParams) layout_right
						.getLayoutParams();
				if (layoutParams.rightMargin < 0) {
					// ˵��layout_left�����ƶ������״̬�����ʱ��������layout_leftӦ��ֱ������ԭ��״̬.(�����Ի�)
					// ���ƶ�
					new AsynMove().execute(SPEED);
					startSubControls(layout_right);
				}
			}
			view = null;
			break;

		}

		if (v == sc) {

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
				break;
			case MotionEvent.ACTION_MOVE:
				index++;
				break;
			default:
				break;
			}
			if (event.getAction() == MotionEvent.ACTION_UP && index > 0) {
				index = 0;
				View view = ((ScrollView) v).getChildAt(0);
				if (view.getMeasuredHeight() <= v.getScrollY() + v.getHeight()) {
					if (totalindex < pageUtil.getTotalcount()
							&& loadprobypage.getVisibility() != View.VISIBLE) {
						pageUtil.setPageindex(pageUtil.getPageindex() + 1);
						loadprobypage.setVisibility(View.VISIBLE);
						getzpinfobypage();
					} else if (totalindex >= pageUtil.getTotalcount()) {
						Toast.makeText(MainActivity.this, "û�и���������", 0).show();
					}
				}
			}

		}
		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {

		// ��֮��Ϊtrue���Żᴫ�ݸ�onSingleTapUp,��Ȼ�¼��������´���.
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return true;
	}

	class AsynMove extends AsyncTask<Integer, Integer, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			int times = 0;
			if (MAX_WIDTH % Math.abs(params[0]) == 0)// ����
				times = MAX_WIDTH / Math.abs(params[0]);
			else
				times = MAX_WIDTH / Math.abs(params[0]) + 1;// ������

			for (int i = 0; i < times; i++) {
				publishProgress(params[0]);
				try {
					Thread.sleep(sleep_time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			return null;
		}

		/**
		 * update UI
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layout_right
					.getLayoutParams();
			RelativeLayout.LayoutParams layoutParams_1 = (RelativeLayout.LayoutParams) layout_left
					.getLayoutParams();
			// ���ƶ�
			if (values[0] > 0) {
				layoutParams.rightMargin = Math.min(layoutParams.rightMargin
						+ values[0], 0);

				layoutParams_1.rightMargin = Math.min(
						layoutParams_1.rightMargin + values[0], window_width);
				layoutParams.leftMargin = -layoutParams.rightMargin;
			} else {
				// ���ƶ�
				layoutParams.rightMargin = Math.max(layoutParams.rightMargin
						+ values[0], -MAX_WIDTH);

				layoutParams_1.rightMargin = Math.max(
						layoutParams_1.rightMargin + values[0], window_width
								- MAX_WIDTH);

				layoutParams.leftMargin = -layoutParams.rightMargin;

			}
			layout_right.setLayoutParams(layoutParams);
			// layout_left.bringToFront();
			layout_left.setLayoutParams(layoutParams_1);

		}

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {

		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		mGestureDetector.onTouchEvent(ev);
		// scroll.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * �˵������ؼ���Ӧ
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) layout_right
					.getLayoutParams();
			if (layoutParams.rightMargin >= 0) {
						initlistnotfly();

			} else {
				exitBy2Click(); // ����˫���˳�����
			}

		}
		return false;
	}

	/**
	 * ˫���˳�����
	 */
	private static Boolean isExit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // ׼���˳�
			Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // ȡ���˳�
				}
			}, 2000); // ���2������û�а��·��ؼ�����������ʱ��ȡ�����ղ�ִ�е�����

		} else {
			ExitApplication.getInstance().exit();
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

	public static void disableSubControls(ViewGroup viewGroup) {
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			View v = viewGroup.getChildAt(i);
			if (v instanceof RelativeLayout) {
				v.setClickable(false);
			} else if (v instanceof TextView) {

				v.setClickable(false);
			}

			if (v instanceof ViewGroup) {
				disableSubControls((ViewGroup) v);
			}
		}
	}

	public static void startSubControls(ViewGroup viewGroup) {
		for (int i = 0; i < viewGroup.getChildCount(); i++) {
			View v = viewGroup.getChildAt(i);
			if (v instanceof RelativeLayout) {
				v.setClickable(true);
			} else if (v instanceof TextView) {

				v.setClickable(true);
			}
			if (v instanceof ViewGroup) {
				startSubControls((ViewGroup) v);
			}
		}
	}

	class MyAdapter extends BaseAdapter {
		private List<HashMap<String, Object>> data;

		public MyAdapter(List<HashMap<String, Object>> data) {
			this.data = data;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			HashMap<String, Object> obj = (HashMap<String, Object>) getItem(position);
			if (null != obj.get("userid")) {

				convertView = LayoutInflater.from(getBaseContext()).inflate(
						R.layout.item2byheadpic, null);
			} else {
				convertView = LayoutInflater.from(getBaseContext()).inflate(
						R.layout.item2, null);
			}

			if (convertView == null || convertView.getTag() == null) {

				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (null != obj.get("userid")) {
				ImageUtil.getDefaultUtil(MainActivity.this).loadImage(holder.itemiamge,
						obj.get("itemimage").toString());
			} else {
				holder.itemiamge.setImageResource(Integer.parseInt(obj.get(
						"itemimage").toString()));
			}

			holder.tv_item.setText(obj.get("tv_item").toString());

			return convertView;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	class ViewHolder {
		ImageView itemiamge;
		TextView tv_item;

		public ViewHolder(View view) {
			this.itemiamge = (ImageView) view.findViewById(R.id.itemiamge);
			this.tv_item = (TextView) view.findViewById(R.id.tv_item);

		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		imageUtil.clearimage();
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig)
    {	try {
		super.onConfigurationChanged(newConfig);

		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
		  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		  }
		} catch (Exception ex) {
			
		}
    }
	
	public void sendQQZONE(View view){
		
		dialog.dismiss();
		dbManager.opendb();
		final List<Object[]> objects = dbManager.sel("select * from user",
				new Object[] { "userid", "username", "userimage" });
		dbManager.closedb();

		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Bundle params = new Bundle();
		        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_NO_TYPE);
		        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, Prototypes.ShareTitle);
		        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY,Prototypes.ShareContent+(objects.size()>0?"?token_id="+objects.get(0)[0].toString():""));
		        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL,Prototypes.ShareUrl+(objects.size()>0?"?token_id="+objects.get(0)[0].toString():""));
		        ArrayList<String> arrayList  = new ArrayList<String>();
		        arrayList.add(Prototypes.ServerUrl+"upload/logo.png");
		        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL,arrayList);
		        tencent.shareToQzone(MainActivity.this, params, new com.tencent.tauth.IUiListener() {
					
					@Override
					public void onError(com.tencent.tauth.UiError arg0) {
					}
					
					@Override
					public void onComplete(Object arg0) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onCancel() {
						// TODO Auto-generated method stub
						
					}
				});
			}
		}).start();
		
		
	}
	
	public void sendWXFriends(View view){
		dialog.dismiss();
		dbManager.opendb();
		final List<Object[]> objects = dbManager.sel("select * from user",
				new Object[] { "userid", "username", "userimage" });
		dbManager.closedb();

		if(!api.isWXAppInstalled()){
			Toast.makeText(MainActivity.this, "�밲װ΢��",0).show();
			return;
		}
		try {
		
		// ��ʼ��һ��WXTextObject����
		WXTextObject wxTextObject = new WXTextObject();
		wxTextObject.text=Prototypes.ShareContent +(objects.size()>0?"?token_id="+objects.get(0)[0].toString():"");
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = wxTextObject;
		msg.title=Prototypes.ShareContent +(objects.size()>0?"?token_id="+objects.get(0)[0].toString():"");
		msg.description=Prototypes.ShareContent +(objects.size()>0?"?token_id="+objects.get(0)[0].toString():"");
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction =String.valueOf(System.currentTimeMillis())+"text";
		req.message = msg;
		req.scene =  SendMessageToWX.Req.WXSceneTimeline;
		// ����api�ӿڷ������ݵ�΢��
		api.sendReq(req);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	
    public void sendSMS(View view){
    	Intent intent = new Intent(Intent.ACTION_VIEW); 
    	intent.putExtra("address", ""); 
    	intent.putExtra("sms_body", Prototypes.ShareContent +(objects.size()>0?"?token_id="+objects.get(0)[0].toString():"")); 
    	intent.setType("vnd.android-dir/mms-sms"); 
    	startActivity(intent);
    }
    
    public void sendsinawb(View view){
    	dialog.dismiss();
    	ssoHandler.authorize(new WeiboAuthListener() {
			
			@Override
			public void onWeiboException(WeiboException arg0) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onComplete(Bundle arg0) {
				oauth2AccessToken = Oauth2AccessToken.parseAccessToken(arg0); // �� Bundle �н��� Token
		    	if (oauth2AccessToken.isSessionValid()) {
		    	AccessTokenKeeper.writeAccessToken(MainActivity.this, oauth2AccessToken); //����Token
		    	}
		    	dialog.dismiss();
				dbManager.opendb();
				final List<Object[]> objects = dbManager.sel("select * from user",
						new Object[] { "userid", "username", "userimage" });
				dbManager.closedb();
		    	TextObject object =new TextObject();
		    	object.text=Prototypes.ShareContent +(objects.size()>0?"?token_id="+objects.get(0)[0].toString():"");
		    	WeiboMultiMessage weiboMessage = new WeiboMultiMessage();//��ʼ��΢���ķ�����Ϣ
		    	weiboMessage.textObject = object;
		    	SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		    	request.transaction = String.valueOf(System.currentTimeMillis());
		    	request.multiMessage = weiboMessage;
		    	mWeiboShareAPI.sendRequest(request);
			}
		});
    	
    }
}
