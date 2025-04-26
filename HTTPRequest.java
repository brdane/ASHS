import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class HTTPRequest
{

    // Functional interface for the callback
    public interface ResponseCallback {
        void onResponse(String response) throws Exception;
    }

    // Constructor that accepts a URL and timeouts as well as a callback
    public HTTPRequest(String theURL, int connectTimeout, int readTimeout, ResponseCallback callback)
    {
        // Create an HttpClient with custom timeouts
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(connectTimeout))
                .build();

        // Build the HttpRequest
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(theURL))
                .timeout(java.time.Duration.ofSeconds(readTimeout)) // Set a read timeout
                .build();

        // Send the request asynchronously
        CompletableFuture<HttpResponse<String>> futureResponse = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        // Process the response
        futureResponse.thenApply(HttpResponse::body)
                .thenAccept(responseData -> {
                    try {
                        callback.onResponse(responseData);
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .exceptionally(e -> {
                    // Call the provided callback method, but return "NORESPONSE"
                    try {
                        callback.onResponse("NORESPONSE");
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                    return null; // Returning null as we are using exceptionally
                });
    }
}