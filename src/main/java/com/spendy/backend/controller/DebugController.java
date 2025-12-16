package com.spendy.backend.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.spendy.backend.configuration.ApiPaths;
import java.util.Map;

@RestController
@RequestMapping(ApiPaths.V1 + "/debug")
public class DebugController {
    private final MongoTemplate mongo;
    public DebugController(MongoTemplate mongo) { this.mongo = mongo; }

    @GetMapping("/db")
    public Map<String,String> db() {
        return Map.of("db", mongo.getDb().getName());
    }
}
