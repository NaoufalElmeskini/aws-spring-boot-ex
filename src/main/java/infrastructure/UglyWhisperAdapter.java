package infrastructure;

import domain.file.FileUtils;
import domain.file.WavFileSplitter;
import domain.port.TranscriberOutput;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
@Slf4j
public class UglyWhisperAdapter implements TranscriberOutput {
    public final static int MAX_ALLOWED_SIZE = 25 * 1024 * 1024;
    public final static int MAX_CHUNK_SIZE_BYTES = 20 * 1024 * 1024;

    public static final String WORD_LIST = String.join(", ",
            List.of("Kousen", "GPT-3", "GPT-4", "DALL-E",
                    "Midjourney", "AssertJ", "Mockito", "JUnit", "Java", "Kotlin", "Groovy", "Scala",
                    "IOException", "RuntimeException", "UncheckedIOException", "UnsupportedAudioFileException",
                    "assertThrows", "assertTrue", "assertEquals", "assertNull", "assertNotNull", "assertThat",
                    "Tales from the jar side", "Spring Boot", "Spring Framework", "Spring Data", "Spring Security"));


    private final WhisperClient client = new WhisperClient();

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
            processBigFile(file, prompt, transcriptions);
        }

        // Join the individual transcripts and write to a file
        String transcription = String.join(" ", transcriptions);
        String fileNameWithoutPath = fileName.substring(
                fileName.lastIndexOf("/") + 1);
        FileUtils.writeTextToFile(transcription,
                fileNameWithoutPath.replace(".wav", ".txt"));
        return transcription;
    }

    private void processBigFile(File file, String prompt, List<String> transcriptions) {
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

    @Override
    public String transcribe(InputStream contentStream) throws IOException {
        // Collect the transcriptions of each chunk
        System.out.println("UglyWhisperAdapter.transcribe");
        String transcription = "";

        // First prompt is the word list
        String prompt = WORD_LIST;

        if (contentStream.readAllBytes().length <= MAX_ALLOWED_SIZE) {
            transcription = client.transcribeChunk(prompt, contentStream);
        } else {
            System.out.println("too big, yo fail.");
        }

        return transcription;
    }
}