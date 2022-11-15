package core.framework.utils;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SlackClient {
    private static final Log LOGGER = LogFactory.getLog(SlackClient.class);
    public static OkHttpClient client = new OkHttpClient.Builder().build();

    public static void send(String slackUrl, String content) {
        Request.Builder builder = new Request.Builder();
        builder.url(slackUrl);
        byte[] body = content.getBytes(UTF_8);
        builder.method("POST", RequestBody.create(body, MediaType.get("application/json")));
        try {
            Response execute = client.newCall(builder.build()).execute();
            Optional.ofNullable(execute.body()).ifPresent(responseBody -> {
                try {
                    LOGGER.info(responseBody.string());
                } catch (IOException e) {
                    LOGGER.error(e.getMessage());
                }
            });
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
