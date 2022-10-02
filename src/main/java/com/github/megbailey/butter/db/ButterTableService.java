package com.github.megbailey.butter.db;

import com.github.megbailey.butter.google.exception.GAccessException;
import com.github.megbailey.butter.google.exception.InvalidInsertionException;
import com.github.megbailey.butter.google.exception.InvalidQueryException;
import com.github.megbailey.butter.google.exception.ResourceNotFoundException;
import com.github.megbailey.butter.domain.ObjectModel;
import com.google.gson.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ButterTableService {
    private final ButterTableRepository butterTableRepository;

    @Autowired
    public ButterTableService(ButterTableRepository butterTableRepository) {
        this.butterTableRepository = butterTableRepository;
    }

    public JsonArray all(String tableName)
            throws GAccessException, ResourceNotFoundException, NullPointerException {
        return this.butterTableRepository.all(tableName);
    }

    public JsonArray query(String tableName, String constraints)
            throws GAccessException, ResourceNotFoundException, InvalidQueryException, NullPointerException {
        return this.butterTableRepository.query(tableName, constraints);
    }

    public List<ObjectModel> create(String tableName, List<ObjectModel> objects)
            throws InvalidInsertionException, ResourceNotFoundException {
        return this.butterTableRepository.append(tableName, objects);
    }
}
