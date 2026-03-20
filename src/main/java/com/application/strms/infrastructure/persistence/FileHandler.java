package com.application.strms.infrastructure.persistence;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;

public class FileHandler {
    public <T> List<T> load(String name, Function<String, T> mapper) throws IOException {
        String path = "/com/application/strms/data/" + name;
        InputStream is = FileHandler.class.getResourceAsStream(path);

        if (is == null) {
            throw new FileNotFoundException("Data file not found: " + path);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {

            return reader.lines()
                    .filter(line -> !line.isBlank())
                    .map(mapper)
                    .toList();

        } catch (IOException e) {
            throw new IOException("Error while reading file: " + path, e);
        }
    }

    public <T> void save(String name, List<T> data, Function<T, String> mapper) throws IOException {
        String path = "src/main/resources/com/application/strms/data/" + name;

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(path, true), StandardCharsets.UTF_8))) {

            for (T element : data) {
                writer.write(mapper.apply(element));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new IOException("Error while writing file: " + path, e);
        }
    }

    public <T> void replaceAll(String name, List<T> data, Function<T, String> mapper) throws IOException {
        String path = "src/main/resources/com/application/strms/data/" + name;

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(path, false), StandardCharsets.UTF_8))) {

            for (T element : data) {
                writer.write(mapper.apply(element));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new IOException("Error while replacing file content: " + path, e);
        }
    }
}