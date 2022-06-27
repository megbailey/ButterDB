package com.github.megbailey.butter.table;

import com.github.megbailey.google.exception.AccessException;
import com.github.megbailey.google.exception.EmptyContentException;
import com.github.megbailey.butter.ObjectModel;
import com.github.megbailey.google.exception.InvalidInsertionException;
import com.github.megbailey.google.exception.SheetNotFoundException;
import com.google.gson.JsonArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ButterTableService {
    private final ButterTableRepository butterTableRepository;

    @Autowired
    public ButterTableService(ButterTableRepository butterTableRepository) {
        this.butterTableRepository = butterTableRepository;
    }

    public JsonArray all(String tableName)
            throws EmptyContentException, AccessException, SheetNotFoundException {
        return this.butterTableRepository.all(tableName);
    }

    public JsonArray query(String tableName, String constraints)
            throws EmptyContentException, AccessException, SheetNotFoundException {
        return this.butterTableRepository.query(tableName, constraints);
    }

    public ObjectModel create(String tableName, ObjectModel object) throws InvalidInsertionException {
        return this.butterTableRepository.append(tableName, object);
    }
}
