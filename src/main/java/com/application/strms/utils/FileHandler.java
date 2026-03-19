package com.application.strms.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;

public class FileHandler {
    public static <T> List<T> load(String name, Function<String, T> mapper) {
        String path = "/data/" + name;
        InputStream is = FileHandler.class.getResourceAsStream(path);

        if (is == null) {
            throw new RuntimeException("Can't find data file: " + path);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))
        ) {
            return reader.lines()
                    .filter(line -> !line.isBlank())
                    .map(mapper)
                    .toList();

        } catch (Exception e) {
            throw new RuntimeException("Error while reading file: " + path, e);
        }
    }
}