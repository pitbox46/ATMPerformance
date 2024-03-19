package github.pitbox46.atmperformance.benchmark;

import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

public class Benchmark {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String[] HEADER = {"start", "finish"};
    private final File log;
    private final Executor executor;

    public Benchmark(String name) {
        this.log = createLogFile(name);
        this.executor = Executors.newSingleThreadExecutor(r -> new Thread(r, "Benchmark-" + name));
        addHeader();
    }

    public static <T> Data<T> benchmark(Supplier<T> target) {
        long start = System.nanoTime();
        T value = target.get();
        long finish = System.nanoTime();
        return new Data<>(value, new String[]{String.valueOf(start), String.valueOf(finish)});
    }

    public <T> Data<T> benchmarkAndLog(Supplier<T> target) {
        Data<T> data = benchmark(target);
        executor.execute(() -> {
            StringBuilder builder = new StringBuilder("\n");
            for (String value : data.data()) {
                builder.append(value).append(',');
            }
            try (FileWriter writer = new FileWriter(log, true)) {
                writer.write(builder.toString());
            } catch (IOException e) {
                LOGGER.warn(e);
            }
        });
        return data;
    }

    private void addHeader() {
        StringBuilder builder = new StringBuilder();
        for (String f : HEADER) {
            builder.append(f).append(',');
        }
        try (FileWriter writer = new FileWriter(log, true)) {
            writer.write(builder.toString());
        } catch (IOException e) {
            LOGGER.warn(e);
        }
    }

    private static File createLogFile(String name) {
        try {
            File logDirFile = new File(Minecraft.getInstance().gameDirectory.getAbsolutePath(), "logs/atmperformance");
            Path logDirPath = logDirFile.toPath();
            if (!logDirFile.isDirectory()) {
                Files.createDirectory(logDirPath);
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            Path logPath = logDirPath.resolve(String.format("%s-%s.csv", name, dateFormat.format(new Date())));
            File logFile = logPath.toFile();
            if (!logFile.exists()) {
                Files.createFile(logPath);
            }
            return logFile;
        } catch (IOException e) {
            LOGGER.warn(e);
            return null;
        }
    }

    public record Data<T> (T value, String[] data) {}
}
