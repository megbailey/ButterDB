package com.github.megbailey.butter.db;

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

    public boolean create(String tableName) {
        try {
            return this.butterRepository.createGSheet(tableName);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cannot create GSheet");
            return false;
        }
    }

    public boolean delete(String tableName) {
        try {
            return this.butterRepository.deleteGSheet( tableName );
        } catch (IOException e) {
            System.out.println("Cannot delete GSheet");
            e.printStackTrace();
            return false;
        }
    }

}
