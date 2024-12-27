package tintin.aws.ex.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.InputStreamBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.StatusLine;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

@Component
@Slf4j
public class WhisperClient {
	private final static String URL = "https://api.openai.com/v1/audio/transcriptions";
	private final static String KEY = System.getenv("OPENAI_API_KEY");
	private final static String MODEL = "whisper-1";

	public String transcribeChunk(String prompt, File chunkFile) {
		log.info("Transcribing {}", chunkFile.getName());

		try (CloseableHttpClient client = HttpClients.createDefault()) {
			HttpPost httpPost = new HttpPost(URL);
			httpPost.setHeader("Authorization", "Bearer %s".formatted(KEY));

			HttpEntity entity = MultipartEntityBuilder.create().setContentType(ContentType.MULTIPART_FORM_DATA)
					.addPart("file", new FileBody(chunkFile, ContentType.DEFAULT_BINARY))
					.addPart("model", new StringBody(MODEL, ContentType.DEFAULT_TEXT))
					.addPart("response_format", new StringBody("text", ContentType.DEFAULT_TEXT))
					.addPart("prompt", new StringBody(prompt, ContentType.DEFAULT_TEXT)).build();
			httpPost.setEntity(entity);
			return client.execute(httpPost, WhisperClient::handleResponse);
		} catch (IOException e) {
			log.error("error while sending request to Wisper API", e);
			throw new UncheckedIOException(e);
		}
	}

	public String transcribeChunk(String prompt, InputStream chunkStream) {
		log.info("transcribeChunk {}", chunkStream.toString());

		try (CloseableHttpClient client = HttpClients.createDefault()) {
			HttpPost httpPost = new HttpPost(URL);
			httpPost.setHeader("Authorization", "Bearer %s".formatted(KEY));

			HttpEntity entity = MultipartEntityBuilder.create().setContentType(ContentType.MULTIPART_FORM_DATA)
					.addPart("file", new InputStreamBody(chunkStream, ContentType.DEFAULT_BINARY, "file.wav"))
					.addPart("model", new StringBody(MODEL, ContentType.DEFAULT_TEXT))
					.addPart("response_format", new StringBody("text", ContentType.DEFAULT_TEXT))
					.addPart("prompt", new StringBody(prompt, ContentType.DEFAULT_TEXT)).build();
			httpPost.setEntity(entity);
			return client.execute(httpPost, WhisperClient::handleResponse);
		} catch (IOException e) {
			log.error("error while sending request to Wisper API", e);
			throw new UncheckedIOException(e);
		}
	}

	private static String handleResponse(ClassicHttpResponse response) throws HttpException, IOException {
		StatusLine status = new StatusLine(response);
		String responseMessage = EntityUtils.toString(response.getEntity());
		if (status.isError()) {
			log.error("error: " + responseMessage);
			throw new HttpException(responseMessage);
		}
		log.info("Response: {}", responseMessage);
		return responseMessage;
	}
}
