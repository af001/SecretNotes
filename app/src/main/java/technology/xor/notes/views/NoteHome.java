package technology.xor.notes.views;

import java.util.ArrayList;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import net.sqlcipher.database.SQLiteDatabase;

import technology.xor.photolibrary.R;
import technology.xor.photolibrary.adapter.SlideMenuAdapter;
import technology.xor.photolibrary.model.SlideData;

public class NoteHome extends AppCompatActivity implements SlideMenuAdapter.SlideMenuAdapterInterface {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 30;

    private Context mContext;
    private Toolbar toolbar;
    private DrawerLayout Drawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;
    private Fragment currentFragment=null;
    private ListView slidingList;
    private SlideMenuAdapter mSlideMenuAdapter;
    private int currentPosition=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mContext = NoteHome.this;
        initializeActionBar();
        initialCalling();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                Log.d("Home", "Already granted access");
            }
        }

        InitializeSQLCipher();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("NotesMain", "Permission Granted");

                } else {
                    Log.d("NotesMain", "Permission Failed");
                    Toast.makeText(NoteHome.this, "You must allow permission to access your location.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            // Add additional cases for other permissions you may have asked for
        }
    }

    private void InitializeSQLCipher() {
        SQLiteDatabase.loadLibs(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if(Drawer.isDrawerOpen(Gravity.LEFT)){
            Drawer.closeDrawer(Gravity.LEFT);
        } else{
            super.onBackPressed();
        }
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
                currentFragment = new NoteView();
                break;
            case 1:
                currentFragment = new RecordingView();
                break;
            case 2:
                currentFragment = new NoteView();
                break;
            default:
                break;
        }
    }

    /**
     * Slide Menu List Array.
     */
    private String[] title={"Notes","Voice Memos","Photos"};
    private int[] titleLogo={R.drawable.selector_note,R.drawable.selector_camera,R.drawable.selector_video};
    private ArrayList<SlideData> getSlideList(){
        ArrayList<SlideData> arrayList=new ArrayList<SlideData>();
        for (int i = 0; i < title.length; i++) {
            SlideData mSlideData = new SlideData();
            mSlideData.setIcon(titleLogo[i]);
            mSlideData.setName(title[i]);
            mSlideData.setState((i==0) ? 1:0);
            arrayList.add(mSlideData);
        }
        return arrayList;
    }
}
