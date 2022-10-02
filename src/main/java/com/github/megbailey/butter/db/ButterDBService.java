package com.github.megbailey.butter.db;

import com.github.megbailey.butter.google.exception.ResourceAlreadyExistsException;
import com.github.megbailey.butter.google.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class ButterDBService {
    private final ButterDBRepository butterRepository;

    @Autowired
    public ButterDBService(ButterDBRepository butterRepository) {
        this.butterRepository = butterRepository;
    }

    /*
        Create a sheet
        @thrown SheetCreationException -> sheet by that name already exists
    */
    public void create(String tableName) throws ResourceAlreadyExistsException {
        try {
            this.butterRepository.createGSheet(tableName);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResourceAlreadyExistsException();
        }
    }

    /*
        Delete a sheet
        @thrown SheetNotFoundException -> sheet DNE
    */
    public void delete(String tableName) throws ResourceNotFoundException {
        try {
            this.butterRepository.deleteGSheet( tableName );
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResourceNotFoundException();
        }
    }

}
