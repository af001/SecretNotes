package technology.xor.notes.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.daimajia.swipe.util.Attributes;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;

import technology.xor.notes.database.CipherDatabaseHelper;
import technology.xor.notes.database.CipherRecording;
import technology.xor.notes.support.DividerItemDecoration;
import technology.xor.notes.support.RecordingRecyclerViewAdapter;
import technology.xor.photolibrary.R;

public class RecordingView extends Fragment {

    private RecyclerView mRecyclerView;
    private RecordingRecyclerViewAdapter mAdapter;
    private ArrayList<CipherRecording> cRecording;
    FloatingActionButton addNoteBtn;
    FloatingActionButton addPhotoBtn;
    FloatingActionButton addVoiceBtn;
    FloatingActionMenu actionMenu;

    public static NoteView newInstance() {
        return new NoteView();
    }

    public RecordingView() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_note_list, container, false);
        setHasOptionsMenu(true);


        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.noteList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.addItemDecoration(new DividerItemDecoration(ContextCompat.getDrawable(getContext(), R.drawable.divider)));

        addNoteBtn = (FloatingActionButton) rootView.findViewById(R.id.fb_add_note);
        addPhotoBtn = (FloatingActionButton) rootView.findViewById(R.id.fb_add_photo);
        addVoiceBtn = (FloatingActionButton) rootView.findViewById(R.id.fb_add_voice);
        actionMenu = (FloatingActionMenu) rootView.findViewById(R.id.fm_notes);

        SetLayout();
        ShowCards();
        return rootView;
    }

    private void SetLayout() {

        addNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMenu.close(false);
                Intent addNote = new Intent(getActivity(), NewNote.class);
                // TODO: Create a constant for 20010
                startActivityForResult(addNote, 20010);
            }
        });

        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getBaseContext(), "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        addVoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionMenu.close(false);
                Intent addRecording = new Intent(getActivity(), NewRecording.class);
                startActivityForResult(addRecording, 20011);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        cRecording.clear();
        ShowCards();
    }

    private void ShowCards() {

        CipherDatabaseHelper dbHelper = new CipherDatabaseHelper(getActivity());
        cRecording = dbHelper.GetRecordings();

        // Creating Adapter object
        mAdapter = new RecordingRecyclerViewAdapter(getActivity(), cRecording);

        // Setting Mode to Single to reveal bottom View for one item in List
        // Setting Mode to Mutliple to reveal bottom Views for multile items in List
        mAdapter.setMode(Attributes.Mode.Single);

        mRecyclerView.setAdapter(mAdapter);

        /* Scroll Listeners */
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.e("RecyclerView", "onScrollStateChanged");
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.system_settings:
                return true;
            case R.id.system_exit:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 20009) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                mAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == 20010) {
            if (resultCode == Activity.RESULT_OK) {
                mAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == 20011) {
            if (resultCode == Activity.RESULT_OK) {

            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.noteview, menu);
    }
}
