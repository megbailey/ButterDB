package com.github.megbailey.butter.db;

import com.github.megbailey.google.exception.SheetCreationException;
import com.github.megbailey.google.exception.SheetNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class ButterDBService {
    private final ButterRepository butterRepository;

    @Autowired
    public ButterDBService(ButterRepository butterRepository) {
        this.butterRepository = butterRepository;
    }

    public void create(String tableName) throws SheetCreationException {
        try {
            this.butterRepository.createGSheet(tableName);
        } catch (IOException e) {
            e.printStackTrace();
            throw new SheetCreationException();
        }
    }

    public void delete(String tableName) throws SheetNotFoundException {
        try {
            this.butterRepository.deleteGSheet( tableName );
        } catch (IOException e) {
            e.printStackTrace();
            throw new SheetNotFoundException();
        }
    }

}
