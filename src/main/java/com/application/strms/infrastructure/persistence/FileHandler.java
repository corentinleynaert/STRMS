package com.application.strms.infrastructure.persistence;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;

public class FileHandler {
    public <T> List<T> load(String name, Function<String, T> mapper) {
        String path = "/com/application/strms/data/" + name; 
        InputStream is = FileHandler.class.getResourceAsStream(path);

        if (is == null) {
            throw new RuntimeException("Can't find data file: " + path);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {

            return reader.lines()
                    .filter(line -> !line.isBlank())
                    .map(mapper)
                    .toList();

        } catch (Exception e) {
            throw new RuntimeException("Error while reading file: " + path, e);
        }
    }

    public <T> void save(String name, List<T> data, Function<T, String> mapper) {
        String path = "src/main/resources/com/application/strms/data/" + name;

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path, true), StandardCharsets.UTF_8))) {

            for (T element : data) {
                writer.write(mapper.apply(element));
                writer.newLine();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error while writing file: " + path, e);
        }
    }
}