package example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3ObjectLambdaEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.WriteGetObjectResponseRequest;
import domain.port.TranscriberInput;
import domain.transcribe.UglyTranscriberService;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * reference : https://docs.aws.amazon.com/AmazonS3/latest/userguide/olap-writing-lambda.html
 */
public class Handler {
	private TranscriberInput transcriberInput = new UglyTranscriberService();

	public void handleRequest(S3ObjectLambdaEvent event, Context context) throws Exception {
		System.out.println("event: " + event.toString());
		System.out.println("context: " + context.toString());

		String bucketId = event.getConfiguration().getAccessPointArn();
		System.out.println("bucketId: " + bucketId);

		AmazonS3 s3Client = AmazonS3Client.builder().build();
		// Prepare the presigned URL for use and make the request to S3.
		HttpClient httpClient = HttpClient.newBuilder().build();

		HttpResponse<InputStream> presignedResponse = httpClient.send(
				HttpRequest.newBuilder(new URI(event.inputS3Url())).GET().build(),
				HttpResponse.BodyHandlers.ofInputStream());
		System.out.println("presignedResponse.full : "+ presignedResponse);

		// Stream the original bytes back to the caller.
		InputStream contentStream = presignedResponse.body();
		String transcript = transcriberInput.getTranscription(contentStream);
		System.out.println("success! transcript: " + transcript);

		s3Client.writeGetObjectResponse(new WriteGetObjectResponseRequest()
				.withRequestRoute(event.outputRoute())
				.withRequestToken(event.outputToken())
				.withInputStream(contentStream));
	}
}
