package tintin.aws.ex.domain.transcribe;

import lombok.extern.slf4j.Slf4j;
import tintin.aws.ex.domain.file.FileUtils;
import tintin.aws.ex.domain.port.TranscriberInput;
import tintin.aws.ex.domain.port.TranscriberOutput;
import tintin.aws.ex.infrastructure.WhisperAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static tintin.aws.ex.domain.file.FileUtils.createTranscriptFile;

/**
 * ugly = DI unavailable
 */
@Slf4j
public class TranscriberService implements TranscriberInput {

	private final TranscriberOutput output = new WhisperAdapter();

	@Override
	public String getTranscription(String fileName) {
		Path transcriptionFilePath = createTranscriptFile(fileName);
		Path audioFilePath = Paths.get(FileUtils.INPUT_RESOURCES_PATH, fileName + ".wav");

		if (Files.exists(transcriptionFilePath)) {
			try {
				return Files.readString(transcriptionFilePath);
			} catch (IOException e) {
				System.err.println("Error reading transcription file: " + e.getMessage());
			}
		} else {
			return output.transcribe(audioFilePath.toString());
		}
		return "";
	}

	@Override
	public String getTranscription(InputStream contentStream) {
		log.info("getTranscription...");
		try {
			return output.transcribe(contentStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
