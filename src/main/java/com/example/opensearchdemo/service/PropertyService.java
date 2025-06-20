package com.example.opensearchdemo.service;

import org.opensearch.action.delete.DeleteRequest;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.GetIndexRequest;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.search.SearchHit;
import org.springframework.stereotype.Service;

import com.example.opensearchdemo.dto.PropertyRequestDTO;
import com.example.opensearchdemo.model.PropertyRequestModel;

import java.io.IOException;
import java.util.*;

@Service
public class PropertyService {
    private final RestHighLevelClient client;

    public PropertyService(RestHighLevelClient client) {
        this.client = client;
    }

    public Map<String, Object> insertProperty(PropertyRequestModel req) throws IOException {
    if (req.id == null || req.title == null || req.city == null) {
        throw new IllegalArgumentException("All fields are required.");
    }

    ensureIndexExists("properties");

    Map<String, Object> doc = new HashMap<>();
    doc.put("title", req.title);
    doc.put("city", req.city);
    doc.put("price", req.price);

    // Check if doc exists
    SearchRequest docExistsRequest = new SearchRequest("properties");
    docExistsRequest.source(new SearchSourceBuilder()
        .query(QueryBuilders.idsQuery().addIds(req.id)));

    SearchResponse response = client.search(docExistsRequest, RequestOptions.DEFAULT);
    Map<String, Object> result = new HashMap<>();

    if (response.getHits().getTotalHits().value > 0) {
        result.put("message", "Document with ID " + req.id + " already exists.");
    } else {
        IndexRequest request = new IndexRequest("properties").id(req.id).source(doc);
        client.index(request, RequestOptions.DEFAULT);
        result.put("message", "Document indexed successfully with ID: " + req.id);
        result.put("document", doc);
    }

    return result;
}


    public List<Map<String, Object>> getAllProperties() throws IOException {

        SearchRequest request = new SearchRequest("properties");
        request.source(new SearchSourceBuilder()
            .query(QueryBuilders.matchAllQuery()));

        List<Map<String, Object>> doc = new ArrayList<>();
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        for (SearchHit hit : response.getHits()) {
            Map<String, Object> result = new HashMap<>();
            result.put("id", hit.getId());
            result.put("title", hit.getSourceAsMap().get("title"));
            result.put("city", hit.getSourceAsMap().get("city"));
            result.put("price", hit.getSourceAsMap().get("price"));
            doc.add(result);

        }
        return doc;
    }

    public Map<String, Object> updateProperty(String id, PropertyRequestDTO req) throws IOException {
        ensureIndexExists("properties");

        Map<String, Object> doc = new HashMap<>();
        doc.put("title", req.title);
        doc.put("city", req.city);
        doc.put("price", req.price);

        // Check if document exists
        SearchRequest docExistsRequest = new SearchRequest("properties");
        docExistsRequest.source(new SearchSourceBuilder()
            .query(QueryBuilders.idsQuery().addIds(id)));

        SearchResponse response = client.search(docExistsRequest, RequestOptions.DEFAULT);
        Map<String, Object> result = new HashMap<>();

        if (response.getHits().getTotalHits().value > 0) {
            IndexRequest request = new IndexRequest("properties").id(id).source(doc);
            client.index(request, RequestOptions.DEFAULT);
            result.put("message", "Document updated successfully with ID: " + id);
            result.put("document", doc);
        } else {
            result.put("message", "Document with ID " + id + " does not exist.");
        }

        return result;
    }


    public List<Map<String, Object>> getPropertiesById(String id) throws IOException {
        SearchRequest request = new SearchRequest("properties");
        request.source(new SearchSourceBuilder()
            .query(QueryBuilders.idsQuery().addIds(id)));

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        List<Map<String, Object>> result = new ArrayList<>();
        for (SearchHit hit : response.getHits()) {
            Map<String, Object> map =null; 
            map.put("_id", hit.getId());
            map.putAll(hit.getSourceAsMap());  

            result.add(map);
        }
        return result;
    }

    public void deletePropertyById(String id) throws IOException {
        DeleteRequest request = new org.opensearch.action.delete.DeleteRequest("properties", id);
        client.delete(request, RequestOptions.DEFAULT);
    }


    public void ensureIndexExists(String indexName) throws IOException {
    GetIndexRequest request = new GetIndexRequest(indexName);
    boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
    if (!exists) {
        CreateIndexRequest createRequest = new CreateIndexRequest(indexName);
        createRequest.mapping(Map.of(
            "properties", Map.of(
                "title", Map.of("type", "text"),
                "city", Map.of("type", "keyword"),
                "price", Map.of("type", "double")
            )
        ));
        client.indices().create(createRequest, RequestOptions.DEFAULT);
    }
}



}
