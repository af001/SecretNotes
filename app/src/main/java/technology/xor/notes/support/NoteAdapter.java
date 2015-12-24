package technology.xor.notes.support;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import technology.xor.notes.database.CipherNotes;
import technology.xor.photolibrary.R;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private ArrayList<CipherNotes> notes;
    private int rowLayout;
    private Context mContext;

    public NoteAdapter(ArrayList<CipherNotes> notes, int rowLayout, Context context) {
        this.notes = notes;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final CipherNotes note = notes.get(i);
        viewHolder.title.setText(note.GetTitle());
        String msg = "<font color='#03A9F4'>" + note.GetDate() + "</font>" + " " + note.GetNote();
        viewHolder.location.setText(note.GetLocation());
        viewHolder.note.setText(Html.fromHtml(msg), TextView.BufferType.SPANNABLE);
        viewHolder.time.setText(note.GetTime());

        /*
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(30);
                Toast.makeText(mContext, "Clicked", Toast.LENGTH_SHORT).show();
            }
        }); */
    }

    @Override
    public int getItemCount() {
        return notes == null ? 0 : notes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView time;
        private TextView location;
        private TextView note;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.noteTitle);
            note = (TextView) itemView.findViewById(R.id.basicNote);
            time = (TextView) itemView.findViewById(R.id.noteTime);
            location = (TextView) itemView.findViewById(R.id.noteLocation);
        }

    }
}
