package technology.xor.photolibrary.activity;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import technology.xor.notes.nfc.record.TextRecord;
import technology.xor.notes.views.NoteHome;
import technology.xor.photolibrary.R;
import technology.xor.photolibrary.adapter.SlideMenuAdapter;
import technology.xor.photolibrary.model.SlideData;

public class ActivityHome extends AppCompatActivity implements SlideMenuAdapter.SlideMenuAdapterInterface {

	private Context mContext;
	private Toolbar toolbar;
	private DrawerLayout Drawer;
	private ActionBarDrawerToggle mDrawerToggle;
	private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;
	private Fragment currentFragment=null;
	private NfcAdapter mAdapter;
	private PendingIntent mPendingIntent;
	private NdefMessage mNdefPushMessage;
	private ListView slidingList;
	private SlideMenuAdapter mSlideMenuAdapter;
	private int currentPosition=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		mContext = ActivityHome.this;
		initializeActionBar();
		initialCalling();

		mAdapter = NfcAdapter.getDefaultAdapter(this);

		mPendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		mNdefPushMessage = new NdefMessage(new NdefRecord[] { newTextRecord(
				"Message from NFC Reader :-)", Locale.ENGLISH, true) });
	}

	private NdefRecord newTextRecord(String text, Locale locale, boolean encodeInUtf8) {
		byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

		Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
		byte[] textBytes = text.getBytes(utfEncoding);

		int utfBit = encodeInUtf8 ? 0 : (1 << 7);
		char status = (char) (utfBit + langBytes.length);

		byte[] data = new byte[1 + langBytes.length + textBytes.length];
		data[0] = (byte) status;
		System.arraycopy(langBytes, 0, data, 1, langBytes.length);
		System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

		return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mAdapter != null) {
			if (mAdapter.isEnabled()) {
				mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
				mAdapter.enableForegroundNdefPush(this, mNdefPushMessage);
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mAdapter != null) {
			mAdapter.disableForegroundDispatch(this);
			mAdapter.disableForegroundNdefPush(this);
		}
	}

	@Override
	public void onBackPressed() {
		if(Drawer.isDrawerOpen(Gravity.LEFT)){
			Drawer.closeDrawer(Gravity.LEFT);
	    }else{
	        super.onBackPressed();
	    }
	}

	private void resolveIntent(Intent intent) {
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			NdefMessage[] msgs = new NdefMessage[0];
			if (rawMsgs != null) {
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
				}
			}

			NdefRecord[] records = msgs[0].getRecords();

			if (TextRecord.isText(records[0])) {
				String result = TextRecord.parse(records[0]);
				System.out.println("Result [NFC]: " + result);

				Intent notes = new Intent(ActivityHome.this, NoteHome.class);
				startActivity(notes);
			}
		}
	}

	@Override
	public void onNewIntent(Intent intent) {
		setIntent(intent);
		resolveIntent(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void slideRowClickEvent(int postion) {
		if (currentPosition== postion) {
			closeDrware();
			return;
		}
		currentPosition= postion;
		getFragment(postion);
		attachedFragment();
	}
	
	private void initializeActionBar() {
		toolbar = (Toolbar) findViewById(R.id.tool_bar);
		toolbar.setTitle("");
		setSupportActionBar(toolbar);
		
		slidingList=(ListView)findViewById(R.id.sliding_listView);
		mSlideMenuAdapter=new SlideMenuAdapter(mContext, getSlideList());
		mSlideMenuAdapter.setSlidemenuadapterinterface(this);
		slidingList.setAdapter(mSlideMenuAdapter);

		Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);
		mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar,
				R.string.openDrawer, R.string.closeDrawer) {

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);

			}

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
			}

		};
		Drawer.setDrawerListener(mDrawerToggle);
		mDrawerToggle.syncState();
		

	}
	
	private void closeDrware(){
		if(Drawer.isDrawerOpen(Gravity.LEFT)){
			Drawer.closeDrawer(Gravity.LEFT);
	    }
	}
	
	private void initialCalling(){
		fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

		getFragment(0);
		attachedFragment();
	}
	
	
	
	private void attachedFragment(){
		try {
			if (currentFragment != null) {
				if (fragmentTransaction.isEmpty()) {
					fragmentTransaction.add(R.id.fragment_container, currentFragment,"" + currentFragment.toString());
					fragmentTransaction.commit();
					toolbar.setTitle(title[currentPosition]);
				}else {
					fragmentTransaction = fragmentManager.beginTransaction();
					fragmentTransaction.replace(R.id.fragment_container, currentFragment,"" + currentFragment.toString());
					fragmentTransaction.commit();
					toolbar.setTitle(title[currentPosition]);
				}
				
			}
			closeDrware();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void getFragment(int postion){
		switch (postion) { 
		case 0:
			currentFragment = new GalleryFragment();
			break;
		case 1:
			currentFragment = new CameraFragment();
			break;
		case 2:
			currentFragment = new VideoFragment();
			break;

		default:
			break;
		}
	}

	/**
	 * Slide Menu List Array.
	 */
	private String[] title={"All Images","Camera","Video"};
	private int[] titleLogo={R.drawable.selector_allpic,R.drawable.selector_camera,R.drawable.selector_video};
	private ArrayList<SlideData> getSlideList(){
		ArrayList<SlideData> arrayList=new ArrayList<SlideData>();
		for (int i = 0; i < title.length; i++) {
			SlideData mSlideData=new SlideData();
			mSlideData.setIcon(titleLogo[i]);
			mSlideData.setName(title[i]);
			mSlideData.setState((i==0)?1:0);
			arrayList.add(mSlideData);
		}
		return arrayList;
	}
}