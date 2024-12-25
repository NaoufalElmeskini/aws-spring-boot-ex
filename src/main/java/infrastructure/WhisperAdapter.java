package infrastructure;

import domain.file.FileUtils;
import domain.file.WavFileSplitter;
import domain.port.TranscriberOutput;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// See docs at https://platform.openai.com/docs/api-reference/audio/createTranscription

// response_format: json (default), text, srt, verbose_json, vtt
//      "text" is used here, as it returns the transcript directly
// language: ISO-639-1 code (optional)
//
// Rather than use multipart form data, add the file as a binary body directly
// Optional "prompt" used to give standard word spellings whisper might miss
//      If there are multiple chunks, the prompt for subsequent chunks should be the
//      transcription of the previous one (244 tokens max)

// file must be mp3, mp4, mpeg, mpga, m4a, wav, or webm
// NOTE: only wav files are supported here (mp3 apparently is proprietary)

// max size is 25MB; otherwise need to break the file into chunks
// See the WavFileSplitter class for that

@Service
@AllArgsConstructor
@Slf4j
public class WhisperAdapter implements TranscriberOutput {
    public final static int MAX_ALLOWED_SIZE = 25 * 1024 * 1024;
    public final static int MAX_CHUNK_SIZE_BYTES = 20 * 1024 * 1024;

    public static final String WORD_LIST = String.join(", ",
            List.of("Kousen", "GPT-3", "GPT-4", "DALL-E",
                    "Midjourney", "AssertJ", "Mockito", "JUnit", "Java", "Kotlin", "Groovy", "Scala",
                    "IOException", "RuntimeException", "UncheckedIOException", "UnsupportedAudioFileException",
                    "assertThrows", "assertTrue", "assertEquals", "assertNull", "assertNotNull", "assertThat",
                    "Tales from the jar side", "Spring Boot", "Spring Framework", "Spring Data", "Spring Security"));


    private final WhisperClient client;

    @Override
    public String transcribe(String fileName) {
        log.info("Transcribing {}", fileName);
        File file = new File(fileName);

        // Collect the transcriptions of each chunk
        List<String> transcriptions = new ArrayList<>();

        // First prompt is the word list
        String prompt = WORD_LIST;

        if (file.length() <= MAX_ALLOWED_SIZE) {
            String transcription = client.transcribeChunk(prompt, file);
            transcriptions = List.of(transcription);
        } else {
            var splitter = new WavFileSplitter();
            List<File> chunks = splitter.splitWavFileIntoChunks(file);
            for (File chunk : chunks) {
                // Subsequent prompts are the previous transcriptions
                String transcription = client.transcribeChunk(prompt, chunk);
                transcriptions.add(transcription);
                prompt = transcription;

                // After transcribing, no longer need the chunk
                if (!chunk.delete()) {
                    log.info("Failed to delete {}", chunk.getName());
                }
            }
        }

        // Join the individual transcripts and write to a file
        String transcription = String.join(" ", transcriptions);
        String fileNameWithoutPath = fileName.substring(
                fileName.lastIndexOf("/") + 1);
        FileUtils.writeTextToFile(transcription,
                fileNameWithoutPath.replace(".wav", ".txt"));
        return transcription;
    }

    @Override
    public String transcribe(File file) {
        String fileName = file.getName();
//        FileUtils.createTranscriptFile(fileName);

        // Collect the transcriptions of each chunk
        List<String> transcriptions = new ArrayList<>();

        // First prompt is the word list
        String prompt = WORD_LIST;

        if (file.length() <= MAX_ALLOWED_SIZE) {
            String transcription = client.transcribeChunk(prompt, file);
            transcriptions = List.of(transcription);
        } else {
            var splitter = new WavFileSplitter();
            List<File> chunks = splitter.splitWavFileIntoChunks(file);
            for (File chunk : chunks) {
                // Subsequent prompts are the previous transcriptions
                String transcription = client.transcribeChunk(prompt, chunk);
                transcriptions.add(transcription);
                prompt = transcription;
                // After transcribing, no longer need the chunk
                if (!chunk.delete()) {
                    log.error("Failed to delete {}", chunk.getName());
                }
            }
        }

        // Join the individual transcripts and write to a file
        String transcription = String.join(" ", transcriptions);
        String fileNameWithoutPath = fileName.substring(
                fileName.lastIndexOf("/") + 1);
        FileUtils.writeTextToFile(transcription,
                fileNameWithoutPath.replace(".wav", ".txt"));
        return transcription;
    }
}