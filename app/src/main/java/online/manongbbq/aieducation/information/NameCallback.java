package online.manongbbq.aieducation.information;

public interface NameCallback {
    void onNameFound(String name);
    void onError(Exception e);
}
