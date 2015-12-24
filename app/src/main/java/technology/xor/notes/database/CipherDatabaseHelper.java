package technology.xor.notes.database;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.util.ArrayList;

public class CipherDatabaseHelper extends SQLiteOpenHelper {

    // DATABASE INFORMATION
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "notes.db";

    // TABLE FOR NOTES
    private static final String TABLE_NAME = "notes";

    // TABLE FOR RECORDINGS
    private static final String TABLE_RECORDING = "recording";

    // COLUMN INFORMATION - NOTES
    private static final String NOTE_ID = "id";
    private static final String NOTE_TITLE = "title";
    private static final String NOTE_DATE = "date";
    private static final String NOTE_TIME = "time";
    private static final String NOTE_LOCATION = "location";
    private static final String NOTE_NOTE = "note";

    // COLUMN INFORMATION - RECORDINGS
    private static final String RECORDING_ID = "id";
    private static final String RECORDING_TITLE = "title";
    private static final String RECORDING_DATE = "date";
    private static final String RECORDING_TIME = "time";
    private static final String RECORDING_LOCATION = "location";
    private static final String RECORDING_DATA = "recording";
    private static final String RECORDING_LENGTH = "length";

    public static final String TAG = "Notes DB";

    private final ArrayList<CipherNotes> note_list = new ArrayList<CipherNotes>();
    private final ArrayList<CipherRecording> recording_list = new ArrayList<CipherRecording>();

    private Context context;

    public CipherDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // CREATE A TABLE FOR NOTES
        String CREATE_NOTE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                NOTE_TITLE + " TEXT," +
                NOTE_DATE + " TEXT," +
                NOTE_TIME + " TEXT," +
                NOTE_LOCATION + " TEXT," +
                NOTE_NOTE + " TEXT" +
                ")";
        db.execSQL(CREATE_NOTE_TABLE);

        // CREATE A TABLE FOR RECORDINGS
        String CREATE_RECORDING_TABLE = "CREATE TABLE " + TABLE_RECORDING + "(" +
                RECORDING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                RECORDING_TITLE + " TEXT," +
                RECORDING_DATE + " TEXT," +
                RECORDING_TIME + " TEXT," +
                RECORDING_LOCATION + " TEXT," +
                RECORDING_DATA + " BLOB," +
                RECORDING_LENGTH + " TEXT" +
                ")";
        db.execSQL(CREATE_RECORDING_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // START FRESH
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDING);
        onCreate(db);
    }

    public void DeleteDatabase() {
        context.deleteDatabase(DATABASE_NAME);
    }

    //----> DATABASE FUNCTIONS FOR: NOTES <-------//

    public void AddNote(CipherNotes newNote) {
        SQLiteDatabase db = this.getWritableDatabase("user_pass");
        ContentValues values = new ContentValues();
        values.put(NOTE_TITLE, newNote.GetTitle());
        values.put(NOTE_DATE, newNote.GetDate());
        values.put(NOTE_TIME, newNote.GetTime());
        values.put(NOTE_LOCATION, newNote.GetLocation());
        values.put(NOTE_NOTE, newNote.GetNote());
        // INSERT ROW
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public CipherNotes GetNote(int id) {
        SQLiteDatabase db = this.getReadableDatabase("user_pass");

        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + NOTE_ID + " = " + id;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            CipherNotes cNotes = new CipherNotes(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    cursor.getString(4), cursor.getString(5));

            cursor.close();
            db.close();

            return cNotes;
        }
        return null;
    }

    public ArrayList<CipherNotes> GetNotes() {
        try {
            note_list.clear();
            SQLiteDatabase db = this.getWritableDatabase("user_pass");

            String selectQuery = "SELECT * FROM " + TABLE_NAME;
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    CipherNotes cNotes = new CipherNotes();
                    cNotes.SetId(Integer.parseInt(cursor.getString(0)));
                    cNotes.SetTitle(cursor.getString(1));
                    cNotes.SetDate(cursor.getString(2));
                    cNotes.SetTime(cursor.getString(3));
                    cNotes.SetLocation(cursor.getString(4));
                    cNotes.SetNote(cursor.getString(5));

                    // ADD THE NOTE TO THE LIST
                    note_list.add(cNotes);
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e);
        }

        return note_list;
    }

    public void UpdateNote(CipherNotes note) {
        SQLiteDatabase db = this.getWritableDatabase("user_pass");

        ContentValues values = new ContentValues();
        values.put(NOTE_TITLE, note.GetTitle());
        values.put(NOTE_DATE, note.GetDate());
        values.put(NOTE_TIME, note.GetTime());
        values.put(NOTE_LOCATION, note.GetLocation());
        values.put(NOTE_NOTE, note.GetNote());
        db.update(TABLE_NAME, values, NOTE_ID + " = ?", new String[]{String.valueOf(note.GetId())});
        db.close();
    }

    public void DeleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase("user_pass");
        db.delete(TABLE_NAME, NOTE_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    //----> DATABASE FUNCTIONS FOR: RECORDINGS <-------//
    public void AddRecording(CipherRecording newRecording) {
        SQLiteDatabase db = this.getWritableDatabase("user_pass");
        ContentValues values = new ContentValues();
        values.put(RECORDING_TITLE, newRecording.GetTitle());
        values.put(RECORDING_DATE, newRecording.GetDate());
        values.put(RECORDING_TIME, newRecording.GetTime());
        values.put(RECORDING_LOCATION, newRecording.GetLocation());
        values.put(RECORDING_DATA, newRecording.GetRecording());
        values.put(RECORDING_LENGTH, newRecording.GetLength());
        // INSERT ROW
        db.insert(TABLE_RECORDING, null, values);
        db.close();
    }

    public ArrayList<CipherRecording> GetRecordings() {
        try {
            recording_list.clear();
            SQLiteDatabase db = this.getWritableDatabase("user_pass");

            String selectQuery = "SELECT * FROM " + TABLE_RECORDING;
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    CipherRecording cRecording = new CipherRecording();
                    cRecording.SetId(Integer.parseInt(cursor.getString(0)));
                    cRecording.SetTitle(cursor.getString(1));
                    cRecording.SetDate(cursor.getString(2));
                    cRecording.SetTime(cursor.getString(3));
                    cRecording.SetLocation(cursor.getString(4));
                    cRecording.SetRecording(cursor.getBlob(5));
                    cRecording.SetLength(cursor.getString(6));

                    // ADD THE NOTE TO THE LIST
                    recording_list.add(cRecording);
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e);
        }

        return recording_list;
    }
}