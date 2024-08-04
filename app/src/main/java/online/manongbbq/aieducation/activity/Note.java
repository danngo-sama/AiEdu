package online.manongbbq.aieducation.model;

public class Note {
    private int id;
    private String title;
    private String content;

    public Note() {
        // 生成唯一ID，可以使用时间戳或其他方法
        this.id = (int) (System.currentTimeMillis() & 0xfffffff);
        this.title = "新笔记";
        this.content = "";
    }

    public Note(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}