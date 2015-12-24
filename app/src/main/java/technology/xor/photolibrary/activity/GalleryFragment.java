package technology.xor.photolibrary.activity;

import java.util.ArrayList;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import technology.xor.photolibrary.ApplicationOwnGallery;
import technology.xor.photolibrary.R;
import technology.xor.photolibrary.adapter.BaseFragmentAdapter;
import technology.xor.photolibrary.component.PhoneMediaControl;

public class GalleryFragment extends Fragment {

	private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 29;
	public static final String PACKAGE = "technology.xor.photolibrary";
    private TextView emptyView;
	private GridView mView;
	private Context mContext;
	
	
	public static ArrayList<PhoneMediaControl.AlbumEntry> albumsSorted = null;
	private Integer cameraAlbumId = null;
	private PhoneMediaControl.AlbumEntry selectedAlbum = null;
	private int itemWidth = 100;
	private ListAdapter listAdapter;
	private View v;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		/** Inflating the layout for this fragment **/
		mContext = this.getActivity();
		v = inflater.inflate(R.layout.fragment_gallery, null);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (mContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
						MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
			} else {
				Log.d("Home", "Already granted access");
				initializeView(v);
			}
		}

		return v;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					Log.d("Home", "Permission Granted");
					initializeView(v);
				} else {
					Log.d("Home", "Permission Failed");
					Toast.makeText(getActivity().getBaseContext(), "You must allow permission to access your photos.", Toast.LENGTH_SHORT).show();
					getActivity().finish();
				}
			}
			// Add additional cases for other permissions you may have asked for
		}
	}
	
	private void initializeView(View v){ 
		mView=(GridView)v.findViewById(R.id.grid_view);
        emptyView = (TextView)v.findViewById(R.id.searchEmptyView);
        emptyView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        emptyView.setText("NoPhotos");
		mView.setAdapter(listAdapter = new ListAdapter(mContext));

        int position = mView.getFirstVisiblePosition();
        int columnsCount = 2;
        mView.setNumColumns(columnsCount);
        itemWidth = (ApplicationOwnGallery.displaySize.x - ((columnsCount + 1) * ApplicationOwnGallery.dp(4))) / columnsCount;
        mView.setColumnWidth(itemWidth);

        listAdapter.notifyDataSetChanged();
        mView.setSelection(position);
        mView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            	Intent mIntent=new Intent(mContext,AlbumActivity.class);
            	Bundle mBundle=new Bundle();
            	mBundle.putString("Key_ID", position+"");
            	mBundle.putString("Key_Name", albumsSorted.get(position).bucketName+"");
            	mIntent.putExtras(mBundle);
            	mContext.startActivity(mIntent);
            }
        });
        
		LoadAllAlbum();
	}
	
	
	private void LoadAllAlbum(){
		PhoneMediaControl mediaControl=new PhoneMediaControl();
		mediaControl.setLoadalbumphoto(new PhoneMediaControl.loadAlbumPhoto() {
			
			@Override
			public void loadPhoto(ArrayList<PhoneMediaControl.AlbumEntry> albumsSorted_) {
				albumsSorted =new ArrayList<PhoneMediaControl.AlbumEntry>(albumsSorted_);
				if (mView != null && mView.getEmptyView() == null) {
					mView.setEmptyView(null);
		        }
		        if (listAdapter != null) {
		            listAdapter.notifyDataSetChanged();
		        }
			}
		});
		mediaControl.loadGalleryPhotosAlbums(mContext,0);
	}
	
	

	private class ListAdapter extends BaseFragmentAdapter {
		private Context mContext;
		private DisplayImageOptions options;
		private ImageLoader imageLoader = ImageLoader.getInstance();

		public ListAdapter(Context context) {
			mContext = context;
			options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.nophotos)
					.showImageForEmptyUri(R.drawable.nophotos)
					.showImageOnFail(R.drawable.nophotos).cacheInMemory(true)
					.cacheOnDisc(true).considerExifParams(true).build();
			imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		}

		@Override
		public boolean areAllItemsEnabled() {
			return true;
		}

		@Override
		public boolean isEnabled(int i) {
			return true;
		}

		@Override
		public int getCount() {
			if (selectedAlbum != null) {
				return selectedAlbum.photos.size();
			}
			return albumsSorted != null ? albumsSorted.size() : 0;
		}

		@Override
		public Object getItem(int i) {
			return null;
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewGroup) {
			if (view == null) {
				LayoutInflater li = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = li.inflate(R.layout.photo_picker_album_layout,
						viewGroup, false);
			}
			ViewGroup.LayoutParams params = view.getLayoutParams();
			params.width = itemWidth;
			params.height = itemWidth;
			view.setLayoutParams(params);

			PhoneMediaControl.AlbumEntry albumEntry = albumsSorted.get(i);
			final ImageView imageView = (ImageView) view
					.findViewById(R.id.media_photo_image);
			if (albumEntry.coverPhoto != null
					&& albumEntry.coverPhoto.path != null) {
				imageLoader.displayImage(
						"file://" + albumEntry.coverPhoto.path, imageView,
						options);
			} else {
				imageView.setImageResource(R.drawable.nophotos);
			}
			TextView textView = (TextView) view.findViewById(R.id.album_name);
			textView.setText(albumEntry.bucketName);
			if (cameraAlbumId != null && albumEntry.bucketId == cameraAlbumId) {

			} else {

			}
			textView = (TextView) view.findViewById(R.id.album_count);
			textView.setText("" + albumEntry.photos.size());

			return view;
		}

		@Override
		public int getItemViewType(int i) {
			if (selectedAlbum != null) {
				return 1;
			}
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public boolean isEmpty() {
			if (selectedAlbum != null) {
				return selectedAlbum.photos.isEmpty();
			}
			return albumsSorted == null || albumsSorted.isEmpty();
		}
	}
}
