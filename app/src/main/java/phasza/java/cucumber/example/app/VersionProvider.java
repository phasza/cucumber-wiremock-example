package phasza.java.cucumber.example.app;

import lombok.NoArgsConstructor;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Reads the version string from the resource file to supply for the --version option
 */
@NoArgsConstructor
public class VersionProvider implements CommandLine.IVersionProvider {

    @Override
    public String[] getVersion() throws Exception {
        try (var inputStream = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("app.version")) {
            if (inputStream == null) {
                throw new IOException("Could not read app.version resource file!");
            }
            return new String[]{String.format("%s", readStream(inputStream))};
        }
    }

    private String readStream(final InputStream inputStream) throws IOException {
        try (var result = new ByteArrayOutputStream()) {
            final var buffer = new byte[8 * 1_024]; //NOPMD
            while (true) {
                final var length = inputStream.read(buffer);
                if (length < 0) {
                    break;
                }
                result.write(buffer, 0, length);
            }
            return result.toString(StandardCharsets.UTF_8.name());
        }
    }
}
