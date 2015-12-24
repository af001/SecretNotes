package technology.xor.notes.support;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;

import java.util.ArrayList;

import technology.xor.notes.database.CipherDatabaseHelper;
import technology.xor.notes.database.CipherNotes;
import technology.xor.notes.views.NewNote;
import technology.xor.photolibrary.R;

public class NoteRecyclerViewAdapter extends RecyclerSwipeAdapter<NoteRecyclerViewAdapter.SimpleViewHolder> {

    private ArrayList<CipherNotes> notes;
    private Context mContext;

    public NoteRecyclerViewAdapter(Context context, ArrayList<CipherNotes> notes) {
        this.notes = notes;
        this.mContext = context;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_notes, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder viewHolder, final int position) {
        final CipherNotes note = notes.get(position);
        viewHolder.title.setText(note.GetTitle());
        String msg = "<font color='#03A9F4'>" + note.GetDate() + "</font>" + " " + note.GetNote();
        viewHolder.location.setText(note.GetLocation());
        viewHolder.note.setText(Html.fromHtml(msg), TextView.BufferType.SPANNABLE);
        viewHolder.time.setText(note.GetTime());

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

        /*viewHolder.swipeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if ((((SwipeLayout) v).getOpenStatus() == SwipeLayout.Status.Close)) {
                    //Start your activity

                    Toast.makeText(mContext, " onClick : " + item.getName() + " \n" + item.getEmailId(), Toast.LENGTH_SHORT).show();
                }

            }
        });*/

        viewHolder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, " onClick : " + note.GetTime(), Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, NewNote.class);
                Bundle args = new Bundle();
                args.putInt("note_id", note.GetId());
                intent.putExtra("bundle", args);
                ((Activity) mContext).startActivityForResult(intent, 20009);
            }
        });

        viewHolder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemManger.removeShownLayouts(viewHolder.swipeLayout);
                notes.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, notes.size());
                mItemManger.closeAllItems();

                CipherDatabaseHelper databaseHelper = new CipherDatabaseHelper(mContext);
                databaseHelper.DeleteNote(note.GetId());
                databaseHelper.close();

                Toast.makeText(view.getContext(), "Deleted " + viewHolder.title.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        // mItemManger is member in RecyclerSwipeAdapter Class
        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return notes == null ? 0 : notes.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    //  ViewHolder Class
    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        private SwipeLayout swipeLayout;
        private TextView title;
        private TextView time;
        private TextView location;
        private TextView note;
        private TextView tvDelete;
        private TextView tvEdit;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            title = (TextView) itemView.findViewById(R.id.noteTitle);
            note = (TextView) itemView.findViewById(R.id.basicNote);
            time = (TextView) itemView.findViewById(R.id.noteTime);
            location = (TextView) itemView.findViewById(R.id.noteLocation);
            tvDelete = (TextView) itemView.findViewById(R.id.tvDelete);
            tvEdit = (TextView) itemView.findViewById(R.id.tvEdit);

            tvDelete.setVisibility(View.GONE);
            tvEdit.setVisibility(View.GONE);
        }
    }
}