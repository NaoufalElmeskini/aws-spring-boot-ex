package tintin.aws.ex.domain.port;

import java.io.InputStream;

public interface TranscriberInput {
	String getTranscription(String fileName);
	String getTranscription(InputStream contentStream);
}
