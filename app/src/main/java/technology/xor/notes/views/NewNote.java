package technology.xor.notes.views;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import technology.xor.notes.database.CipherDatabaseHelper;
import technology.xor.notes.database.CipherNotes;
import technology.xor.notes.support.LocationProvider;
import technology.xor.photolibrary.R;

public class NewNote extends Activity implements LocationProvider.LocationCallback {

    private LocationProvider mLocationProvider;
    private double latitude;
    private double longitude;
    private CipherDatabaseHelper cipherDatabaseHelper;
    private CipherNotes cipherNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_new_note);

        mLocationProvider = new LocationProvider(this, this);
        latitude = 0.0;
        longitude = 0.0;

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setTitle("Add Note");
        // builder.setIcon(R.id.xxxx)

        final LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(75, 75, 75, 25);

        TextView tv = new TextView(this);
        tv.setTextSize(14);
        tv.setText("Title");
        layout.addView(tv);

        final int maxLength2 = 15;
        InputFilter[] fArray2 = new InputFilter[1];
        fArray2[0] = new InputFilter.LengthFilter(maxLength2);

        final EditText title = new EditText(this);
        title.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_FLAG_CAP_WORDS |
                EditorInfo.TYPE_TEXT_FLAG_AUTO_CORRECT);
        title.setLines(1);
        title.setSingleLine(true);
        title.setFilters(fArray2);
        title.setHint("Title...");
        layout.addView(title);

        TextView tv2 = new TextView(this);
        tv2.setTextSize(14);
        tv2.setText("Message");
        layout.addView(tv2);

        final int maxLength = 200;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);

        final EditText message = new EditText(this);
        message.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES |
                EditorInfo.TYPE_TEXT_FLAG_AUTO_CORRECT);
        message.setLines(4);
        message.setSingleLine(false);
        message.setGravity(Gravity.TOP);
        message.setFilters(fArray);
        message.setHint("Note...");
        layout.addView(message);

        final Bundle bundle = getIntent().getBundleExtra("bundle");

        if (bundle != null) {
            int index = bundle.getInt("note_id");

            cipherDatabaseHelper = new CipherDatabaseHelper(this);
            cipherNotes = cipherDatabaseHelper.GetNote(index);
            message.setText(cipherNotes.GetNote());
            title.setText(cipherNotes.GetTitle());
        }

        builder.setView(layout);
        builder.setPositiveButton("Save",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (!message.getText().toString().equals("") && !title.getText().toString().equals("")) {

                            if (bundle != null) {
                                cipherNotes.SetTitle(title.getText().toString());
                                cipherNotes.SetNote(message.getText().toString());
                                cipherNotes.SetTime(GetTime());
                                cipherDatabaseHelper.UpdateNote(cipherNotes);
                                cipherDatabaseHelper.close();
                                Toast.makeText(getBaseContext(), "Note updated!", Toast.LENGTH_SHORT).show();
                            } else {
                                CipherNotes cNotes = new CipherNotes(title.getText().toString(),
                                        GetDate(), GetTime(), String.valueOf(latitude) + "," +
                                        String.valueOf(longitude), message.getText().toString());
                                CipherDatabaseHelper dbHelper = new CipherDatabaseHelper(NewNote.this);
                                dbHelper.AddNote(cNotes);
                                dbHelper.close();
                                Toast.makeText(getBaseContext(), "Note added!", Toast.LENGTH_SHORT).show();
                            }

                            Intent returnIntent = new Intent();
                            setResult(RESULT_OK,returnIntent);
                            finish();

                        } else {
                            Toast.makeText(getBaseContext(), "Empty Messages Not Allowed!", Toast.LENGTH_SHORT).show();
                            message.setText("");
                        }
                    }
                });

        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent returnIntent = new Intent();
                        setResult(RESULT_CANCELED,returnIntent);
                        finish();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private String GetDate() {
        Calendar cal = Calendar.getInstance();
        //SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        return sdf.format(cal.getTime());
    }

    private String GetTime() {
        Calendar cal = Calendar.getInstance();
        //SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("k:m zzz", Locale.US);
        return sdf.format(cal.getTime());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationProvider.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationProvider.connect();
    }

    @Override
    public void handleNewLocation(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }
}
