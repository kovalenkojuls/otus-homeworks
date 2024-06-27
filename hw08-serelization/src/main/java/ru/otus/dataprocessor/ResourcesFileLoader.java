package ru.otus.dataprocessor;

import java.io.*;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import ru.otus.model.Measurement;

public class ResourcesFileLoader implements Loader {
    private final String fileName;
    private final ObjectMapper mapper;

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
        this.mapper = JsonMapper.builder().build();
    }

    @Override
    public List<Measurement> load() throws IOException {
        var file = new File(ClassLoader.getSystemResource(fileName).getFile());
        return mapper.readValue(file, new TypeReference<List<Measurement>>(){});
    }
}
