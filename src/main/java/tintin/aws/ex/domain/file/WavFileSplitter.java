package tintin.aws.ex.domain.file;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WavFileSplitter {
    public final static int MAX_CHUNK_SIZE_BYTES = 20 * 1024 * 1024;

    public List<File> splitWavFileIntoChunks(File sourceWavFile) {
        List<File> chunks = new ArrayList<>();
        int chunkCounter = 1;

        if (!sourceWavFile.exists()) {
            throw new IllegalArgumentException("Source file not found at: " + sourceWavFile.getAbsolutePath());
        }

        try (var inputStream = AudioSystem.getAudioInputStream(sourceWavFile)) {
            long totalFrames = inputStream.getFrameLength(); // Total frames in the source wav file
            AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(sourceWavFile);
            AudioFormat format = fileFormat.getFormat();

            // Calculate the maximum number of frames for each chunk
            int frameSize = format.getFrameSize(); // Number of bytes in each frame
            long framesPerChunk = MAX_CHUNK_SIZE_BYTES / frameSize;

            byte[] buffer = new byte[(int) (framesPerChunk * frameSize)];

            while (totalFrames > 0) {
                long framesInThisFile = Math.min(totalFrames, framesPerChunk);
                int bytesRead = inputStream.read(buffer, 0, (int) (framesInThisFile * frameSize));
                if (bytesRead > 0) {
                    File chunkFile = createChunk(sourceWavFile, chunkCounter, buffer, bytesRead, format, framesInThisFile);
                    chunks.add(chunkFile);
                    chunkCounter++;
                }
                totalFrames -= framesInThisFile;
            }
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new RuntimeException(e);
        }

        return chunks;
    }

    private static File createChunk(File sourceWavFile, int chunkCounter, byte[] buffer, int bytesRead, AudioFormat format,
            long framesInThisFile) throws IOException {
        File chunkFile = new File(sourceWavFile.getAbsolutePath().replace(
                ".wav", "-%d.wav".formatted(chunkCounter)));
        try (var partStream = new AudioInputStream(
                new ByteArrayInputStream(buffer, 0, bytesRead), format, framesInThisFile)) {
            AudioSystem.write(partStream, AudioFileFormat.Type.WAVE, chunkFile);
        }
        return chunkFile;
    }
}