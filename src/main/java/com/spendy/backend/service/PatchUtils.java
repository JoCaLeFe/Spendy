package com.spendy.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PatchUtils {

    private final ObjectMapper mapper;

    // Si Spring proporciona un ObjectMapper, se usa.
    // Si no, se crea manualmente con soporte para fechas.
    public PatchUtils(@Autowired(required = false) ObjectMapper injected) {
        if (injected != null) {
            this.mapper = injected;
        } else {
            ObjectMapper m = new ObjectMapper();
            m.registerModule(new JavaTimeModule());
            m.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            this.mapper = m;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T applyPatch(T data, List<Map<String, Object>> updates) throws JsonPatchException {
        // 1️⃣ Convierte la lista del body JSON en un objeto JSON Patch estándar
        JsonPatch patch = mapper.convertValue(updates, JsonPatch.class);

        // 2️⃣ Convierte el objeto Java original a JSON genérico (JsonNode)
        JsonNode json = mapper.convertValue(data, JsonNode.class);

        // 3️⃣ Aplica las operaciones del JSON Patch sobre el JSON original
        JsonNode updated = patch.apply(json);

        // 4️⃣ Convierte el JSON modificado de nuevo al tipo original del objeto
        return (T) mapper.convertValue(updated, data.getClass());
    }
}