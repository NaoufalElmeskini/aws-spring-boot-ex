package domain.port;

import java.io.File;

public interface TranscriberOutput {
	String transcribe(String fileName);

	String transcribe(File file);
}
