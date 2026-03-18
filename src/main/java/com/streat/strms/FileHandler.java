package com.streat.strms;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.function.Function;

public class FileHandler {
    public static <T> List<T> load(String path, Function<String, T> model) {
        InputStream is = FileHandler.class.getResourceAsStream("/data/" + path);

        if (is == null) {
            throw new RuntimeException("Can not find file: " + "/data/" + path);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            return reader.lines()
                    .filter(line -> !line.isBlank())
                    .map(model)
                    .toList();

        } catch (Exception e) {
            throw new RuntimeException("Error while reading file:", e);
        }
    }
}