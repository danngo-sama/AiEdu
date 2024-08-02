package online.manongbbq.aieducation.ai.VoiceToText;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class InputStreamRequestBody extends RequestBody {
    private final InputStream inputStream;

    public InputStreamRequestBody(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse("application/octet-stream");
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        byte[] buffer = new byte[2048];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            sink.write(buffer, 0, bytesRead);
        }
        inputStream.close();
    }
}