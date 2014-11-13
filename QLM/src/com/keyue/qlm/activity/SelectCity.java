package com.keyue.qlm.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import com.keyue.qlm.R;
import com.keyue.qlm.util.CharacterParser;
import com.keyue.qlm.util.ClearEditText;
import com.keyue.qlm.util.DBHelp;
import com.keyue.qlm.util.PinyinComparator;
import com.keyue.qlm.util.SideBar;
import com.keyue.qlm.util.SortAdapter;
import com.keyue.qlm.util.SortModel;
import com.keyue.qlm.util.SideBar.OnTouchingLetterChangedListener;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SelectCity extends Activity {
	private ArrayList<String> allcitys;
	private final int FINDCITYSUCCESS=1;
	private final int FINDCITYERROR=2;
	private LinearLayout loadll;
	private FrameLayout selectcitymain;
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private SortAdapter adapter;
	/**
	 * ����ת����ƴ������
	 */
	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;
	private ClearEditText mClearEditText;
	/**
	 * ����ƴ��������ListView�����������
	 */
	private PinyinComparator pinyinComparator;
 	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.selectcity_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebyselectcity_layout);
		loadll=(LinearLayout) findViewById(R.id.loadll);
		selectcitymain=(FrameLayout) findViewById(R.id.selectcitymain);
this.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SelectCity.this.finish();
			}
		});
	
		Runnable updatetest = new Runnable() {

			@Override
			public void run() {
				// �õ�activity�еĸ�Ԫ��
				View view = findViewById(R.id.selectcity);
				// ��θ�Ԫ�ص�width��height����0˵��activity�Ѿ���ʼ�����
				if (view != null && view.getWidth() > 0 && view.getHeight() > 0) {
					//new UpdateApp(NearByJob.this).isUpdate();
					//onCreateGps();
					initdata();
					handler.removeCallbacks(this);
				} else {
					// ���activityû�г�ʼ�������ȴ�5�����ٴμ��
					handler.postDelayed(this, 100);
				}
			}
		};

		handler.post(updatetest);
	}

	private void initdata(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Message message = new Message();
				List<Object[]> objects = DBHelp.selsql("select dqmc from cityadress where dqjb=2 or dqjb=1 and dqmc like '%��%'");
				if(null!=objects){
					allcitys=new ArrayList<String>();
					for (int i = 0;i<objects.size();i++) {
						allcitys.add(objects.get(i)[0].toString());
					}
					message.what=FINDCITYSUCCESS;
				}else{
					message.what=FINDCITYERROR;
				}
				handler.sendMessage(message);
			}
		}).start();
		
	}
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case FINDCITYERROR:
				Toast.makeText(SelectCity.this, "���ӳ�ʱ", 0).show();
				break;
			case FINDCITYSUCCESS:
				initViews();
				loadll.setVisibility(View.GONE);
				selectcitymain.setVisibility(View.VISIBLE);
				mClearEditText.setVisibility(View.VISIBLE);
				break;
			}

		}
	};
	private void initViews() {
		//ʵ��������תƴ����
		characterParser = CharacterParser.getInstance();
		
		pinyinComparator = new PinyinComparator();
		
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);
		
		//�����Ҳഥ������

		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			
			@Override
			public void onTouchingLetterChanged(String s) {
				//����ĸ�״γ��ֵ�λ��
				int position = adapter.getPositionForSection(s.charAt(0));
				if(position != -1){
					sortListView.setSelection(position);
				}
				
			}
		});
		
		sortListView = (ListView) findViewById(R.id.country_lvcountry);
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//����Ҫ����adapter.getItem(position)����ȡ��ǰposition����Ӧ�Ķ���
				String city= ((SortModel)adapter.getItem(position)).getName().toString();
				Intent intent = new Intent(SelectCity.this,OtherCityByJob.class);
				intent.putExtra("city", city);
				SelectCity.this.setResult(RESULT_OK, intent);
				SelectCity.this.finish();
			}
		});
		
		SourceDateList = filledData(allcitys.toArray());
		
		// ����a-z��������Դ����
		Collections.sort(SourceDateList, pinyinComparator);
		adapter = new SortAdapter(this, SourceDateList);
		sortListView.setAdapter(adapter);
		
		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
		
		//�������������ֵ�ĸı�����������
		mClearEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//������������ֵΪ�գ�����Ϊԭ�����б�����Ϊ���������б�
				final String str=s.toString();
				handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						filterData(str);
					}
				}, 100);
			
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}
	
	private List<SortModel> filledData(Object[] date){
		List<SortModel> mSortList = new ArrayList<SortModel>();
		
		for(int i=0; i<date.length; i++){
			SortModel sortModel = new SortModel();
			sortModel.setName(date[i].toString());
			//����ת����ƴ��
			String pinyin = characterParser.getSelling(date[i].toString());
			String sortString = pinyin.substring(0, 1).toUpperCase();
			
			// ������ʽ���ж�����ĸ�Ƿ���Ӣ����ĸ
			if(sortString.matches("[A-Z]")){
				sortModel.setSortLetters(sortString.toUpperCase());
			}else{
				sortModel.setSortLetters("#");
			}
			
			mSortList.add(sortModel);
		}
		return mSortList;
		
	}
	/**
	 * ����������е�ֵ���������ݲ�����ListView
	 * @param filterStr
	 */
	private void filterData(String filterStr){
		List<SortModel> filterDateList = new ArrayList<SortModel>();
		
		if(TextUtils.isEmpty(filterStr)){
			filterDateList = SourceDateList;
		}else{
			filterDateList.clear();
			for(SortModel sortModel : SourceDateList){
				String name = sortModel.getName();
				if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
					filterDateList.add(sortModel);
				}
			}
		}
		
		// ����a-z��������
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
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
