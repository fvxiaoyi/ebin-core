package core.framework.utils.slack;

import core.framework.alerting.domain.Alert;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SlackClient {
    public static OkHttpClient client = new OkHttpClient.Builder().build();

    public static void send(String content) {
        Request.Builder builder = new Request.Builder();
        builder.url("https://hooks.slack.com/services/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");
        byte[] body = content.getBytes(UTF_8);
        builder.method("POST", RequestBody.create(body, MediaType.get("application/json")));
        try {
            client.newCall(builder.build()).execute();
        } catch (IOException e) {

        }
    }

    public static void main(String[] args) {
        String content = "{\"text\":\"Hello, World!\"}";
        Request.Builder builder = new Request.Builder();
        builder.url("https://hooks.slack.com/services/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");
        byte[] body = content.getBytes(UTF_8);
        builder.method("POST", RequestBody.create(body, MediaType.get("application/json")));
        try {
            Response execute = client.newCall(builder.build()).execute();
            System.out.println(execute.body().string());
        } catch (IOException e) {

        }
    }
}
