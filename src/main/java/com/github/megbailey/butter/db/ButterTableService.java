package com.github.megbailey.butter.db;

import com.github.megbailey.butter.google.exception.GAccessException;
import com.github.megbailey.butter.google.exception.BadRequestException;
import com.github.megbailey.butter.google.exception.ResourceNotFoundException;
import com.github.megbailey.butter.domain.ObjectModel;
import com.google.gson.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


@Service
public class ButterTableService {
    private final ButterTableRepository butterTableRepository;

    @Autowired
    public ButterTableService(ButterTableRepository butterTableRepository) {
        this.butterTableRepository = butterTableRepository;
    }

    public JsonArray all(String tableName) throws GAccessException, ResourceNotFoundException, ClassNotFoundException {
        return this.butterTableRepository.all(tableName);
    }

    public JsonArray query(String tableName, String query)
            throws GAccessException, BadRequestException, ResourceNotFoundException, ClassNotFoundException {
        return this.butterTableRepository.query(tableName, query);
    }

    public List<ObjectModel> create(String tableName, List<ObjectModel> objects)
            throws BadRequestException, ResourceNotFoundException {
        return this.butterTableRepository.append(tableName, objects);
    }

    public boolean delete(String tableName, String query)
            throws ResourceNotFoundException, GAccessException, IOException, ClassNotFoundException {
        return this.butterTableRepository.delete(tableName, query);
    }

}
