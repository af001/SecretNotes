package technology.xor.notes.database;

public class CipherPhotos {
    private int id;
    private String note;
    private String location;
    private byte[] photo;

    public CipherPhotos() { }

    public CipherPhotos(int id, String note, String location, byte[] photo) {
        this.id = id;
        this.note = note;
        this.location = location;
        this.photo = photo;
    }

    public CipherPhotos(String note, String location, byte[] photo) {
        this.note = note;
        this.location = location;
        this.photo = photo;
    }

    public int GetId() {
        return this.id;
    }

    public void SetId(int id) {
        this.id = id;
    }

    public String GetNote() {
        return this.note;
    }

    public void SetNote(String note) {
        this.note = note;
    }

    public String GetLocation() {
        return this.location;
    }

    public void SetLocation(String location) {
        this.location = location;
    }

    public byte[] GetPhoto() {
        return this.photo;
    }

    public void SetPhoto(byte[] photo) {
        this.photo = photo;
    }
}
