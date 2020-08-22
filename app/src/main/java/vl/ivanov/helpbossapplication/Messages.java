package vl.ivanov.helpbossapplication;

public class Messages {
    private String msg;
    private int sender = 0;
    private int id = 0;

    public Messages(String msg, int sender, int id) {
        this.msg = msg;
        this.sender = sender;
        this.id = id;
    }

    public String getMsg() {
        return this.msg;
    }

    public int getSender() {
        return this.sender;
    }

    public int getId() { return this.id; }
}
