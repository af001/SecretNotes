package technology.xor.notes.database;

public class CipherNotes {
    private int id;
    private String title;
    private String date;
    private String location;
    private String note;
    private String time;

    public CipherNotes() { }

    public CipherNotes(int id, String title, String date, String time, String location, String note) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.location = location;
        this.note = note;
    }

    public CipherNotes(String title, String date, String time, String location, String note) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.location = location;
        this.note = note;
    }

    public int GetId() {
        return this.id;
    }

    public void SetId(int id) {
        this.id = id;
    }

    public String GetTitle() {
        return this.title;
    }

    public void SetTitle(String title) {
        this.title = title;
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

    public String GetNote() {
        return this.note;
    }

    public void SetNote(String note) {
        this.note = note;
    }
}
