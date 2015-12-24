package technology.xor.notes.support;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.github.clans.fab.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import technology.xor.notes.database.CipherRecording;
import technology.xor.photolibrary.R;

public class RecordingRecyclerViewAdapter extends RecyclerSwipeAdapter<RecordingRecyclerViewAdapter.SimpleViewHolder> {

    private ArrayList<CipherRecording> recordings;
    private Context mContext;

    public RecordingRecyclerViewAdapter(Context context, ArrayList<CipherRecording> recordings) {
        this.recordings = recordings;
        this.mContext = context;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_recordings, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        final CipherRecording recording = recordings.get(position);
        viewHolder.title.setText(recording.GetTitle());
        viewHolder.location.setText(recording.GetLocation());
        viewHolder.dateTime.setText(recording.GetTime());
        viewHolder.length.setText(recording.GetLength());

        viewHolder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);

        // Drag From Right
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper));

        // Handling different events when swiping
        viewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                viewHolder.tvDelete.setVisibility(View.GONE);
                viewHolder.tvEdit.setVisibility(View.GONE);
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                //you are swiping.
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {
                viewHolder.tvDelete.setVisibility(View.VISIBLE);
                viewHolder.tvEdit.setVisibility(View.VISIBLE);
            }

            @Override
            public void onOpen(SwipeLayout layout) {
                //when the BottomView totally show.
            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                //when user's hand released.
            }
        });

        viewHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, " onClick : " + recording.GetTime(), Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                byte[] soundBytes=recording.GetRecording();

                String outputFile= Environment.getExternalStorageDirectory().getAbsolutePath() + "/output.3gp";
                File path = new File(outputFile);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(path);
                    fos.write(soundBytes);
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MediaPlayer mediaPlayer = new MediaPlayer();

                try {
                    mediaPlayer.setDataSource(outputFile);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.release();
            }
        });

        viewHolder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                recordings.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, recordings.size());
                mItemManger.closeAllItems();

                /*)
                CipherDatabaseHelper databaseHelper = new CipherDatabaseHelper(mContext);
                databaseHelper.DeleteNote(recording.GetId());
                databaseHelper.close(); */
            }
        });

        // mItemManger is member in RecyclerSwipeAdapter Class
        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return recordings == null ? 0 : recordings.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    //  ViewHolder Class
    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        private SwipeLayout swipeLayout;
        private FloatingActionButton btnPlay;
        private TextView title;
        private TextView dateTime;
        private TextView location;
        private TextView recording;
        private TextView length;
        private TextView tvDelete;
        private TextView tvEdit;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            btnPlay = (FloatingActionButton) itemView.findViewById(R.id.ib_rec_play);
            title = (TextView) itemView.findViewById(R.id.tv_rec_title);
            dateTime = (TextView) itemView.findViewById(R.id.tv_rec_time);
            location = (TextView) itemView.findViewById(R.id.tv_rec_loc);
            length = (TextView) itemView.findViewById(R.id.tv_rec_length);
            tvDelete = (TextView) itemView.findViewById(R.id.tvDelete);
            tvEdit = (TextView) itemView.findViewById(R.id.tvEdit);

            tvDelete.setVisibility(View.GONE);
            tvEdit.setVisibility(View.GONE);
        }
    }
}
