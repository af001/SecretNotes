package technology.xor.notes.database;

public class CipherRecording {

    private int id;
    private String title;
    private String date;
    private String location;
    private String time;
    private byte[] recording;
    private String length;

    public CipherRecording() { }

    public CipherRecording(int id, String date, String time, String location, byte[] recording, String length, String title) {
        this.id = id;
        this.date = date;
        this.location = location;
        this.time = time;
        this.recording = recording;
        this.length = length;
        this.title = title;
    }

    public CipherRecording(String date,String time, String location, byte[] recording, String length, String title) {
        this.date = date;
        this.location = location;
        this.time = time;
        this.recording = recording;
        this.length = length;
        this.title = title;
    }

    public int GetId() {
        return this.id;
    }

    public void SetId(int id) {
        this.id = id;
    }

    public byte[] GetRecording() {
        return this.recording;
    }

    public void SetRecording(byte[] recording) {
        this.recording = recording;
    }

    public String GetDate() {
        return this.date;
    }

    public void SetDate(String date) {
        this.date = date;
    }

    public String GetTime() {
        return this.time;
    }

    public void SetTime(String time) {
        this.time = time;
    }

    public String GetLocation() {
        return this.location;
    }

    public void SetLocation(String location) {
        this.location = location;
    }

    public String GetLength() {
        return this.length;
    }

    public void SetLength(String length) {
        this.length = length;
    }

    public String GetTitle() {
        return this.title;
    }

    public void SetTitle(String title) {
        this.title = title;
    }
}
