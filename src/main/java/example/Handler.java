package example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3ObjectLambdaEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.WriteGetObjectResponseRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import tintin.aws.ex.domain.port.TranscriberInput;
import tintin.aws.ex.domain.transcribe.TranscriberService;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;

/**
 * reference : https://docs.aws.amazon.com/AmazonS3/latest/userguide/olap-writing-lambda.html
 */
@Slf4j
public class Handler {
	private TranscriberInput transcriberInput = new TranscriberService();

	public void handleRequest(S3ObjectLambdaEvent event, Context context) throws Exception {
		String traceId = System.getenv("_X_AMZN_TRACE_ID");
		log.info("Trace ID: {}", traceId);
		log.info("starting... ");
		log.info("event: " + event.toString());
		log.info("context: " + context.toString());

		String bucketId = event.getConfiguration().getAccessPointArn();
		log.info("bucketId: " + bucketId);

		// Prepare the presigned URL for use and make the request to S3.
		HttpClient httpClient = HttpClient.newBuilder().build();

		HttpResponse<InputStream> presignedResponse = httpClient.send(
				HttpRequest.newBuilder(new URI(event.inputS3Url())).GET().build(),
				HttpResponse.BodyHandlers.ofInputStream());
		log.info("presignedResponse.full : "+ presignedResponse);

		// Stream the original bytes back to the caller.
		//		fixme
		//	InputStream contentStream = presignedResponse.body();
		//	String transcript = transcriberInput.getTranscription(contentStream);
		String transcript = "dump transcript";
		log.info("success! transcript: " + transcript);

		AmazonS3 s3Client = AmazonS3Client.builder().build();
		s3Client.writeGetObjectResponse(new WriteGetObjectResponseRequest()
				.withRequestRoute(event.outputRoute())
				.withRequestToken(event.outputToken())
				.withInputStream(IOUtils.toInputStream(transcript, Charset.defaultCharset())));
	}
}
