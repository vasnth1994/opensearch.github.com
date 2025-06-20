package com.example.opensearchdemo.controller;

import com.example.opensearchdemo.model.PropertyRequestModel;
import com.example.opensearchdemo.dto.PropertyRequestDTO;
import com.example.opensearchdemo.service.PropertyService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {
    private final PropertyService service;

    public PropertyController(PropertyService service) {
        this.service = service;
    }

    @PostMapping
    public Map<String, Object> insert(@RequestBody PropertyRequestModel request) throws Exception {
        return service.insertProperty(request);

    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable String id, @RequestBody PropertyRequestDTO request) throws Exception {
        return service.updateProperty(id, request);
    }


    @GetMapping("/search")
    public List<Map<String, Object>> search() throws Exception {

        if(service.getAllProperties()==null || service.getAllProperties().isEmpty()) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "No properties found");
            return Collections.singletonList(map);
        }
        return service.getAllProperties();
    }

    
    @GetMapping("/search/{id}")
    public List<Map<String, Object>> searchById(@PathVariable String id) throws Exception {
        return service.getPropertiesById(id);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteById(@PathVariable String id) throws Exception {
        service.deletePropertyById(id);
        return "Deleted!";
    }
}
