package com.example.DepartmentalStoreCrud.service;

import com.example.DepartmentalStoreCrud.bean.Backorder;
import com.example.DepartmentalStoreCrud.repository.BackorderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
public class BackorderService {

    @Autowired
    private BackorderRepository backorderRepository;

    /**
     * To get List of all Backorders.
     *
     * @return List of Backorders
     */
    public List<Backorder> getAllBackorders() {
        return backorderRepository.findAll();
    }

    /**
     * To get Details of Backorder with Backorder's ID.
     *
     * @param backorderId - Backorder's ID
     * @return Backorder Details
     */
    public Backorder getBackorderById(final Long backorderId) {
        Optional<Backorder> backorder = backorderRepository.findById(backorderId);
        if (backorder.isEmpty()) {
            log.info("Invalid backorder id");
            throw new NoSuchElementException("No backorder exists with ID: " + backorderId);
        }
        return backorder.get();
    }

    /**
     * To create a new backorder.
     *
     * @param backorder - backorder's details
     * @return backorder's details
     */
    public Backorder createBackorder(final Backorder backorder) {
        log.info("Backorder created");
        return backorderRepository.save(backorder);
    }

    /**
     * To Delete a Backorder.
     *
     * @param backorderId - Backorder's ID
     */
    public void deleteBackorder(final Long backorderId) {
        Optional<Backorder> backorder = backorderRepository.findById(backorderId);
        if (backorder.isEmpty()) {
            log.info("Invalid backorder id");
            throw new NoSuchElementException("No backorder exists with ID: " + backorderId);
        }
        log.info("Backorder deleted with id-" + backorderId);
        backorderRepository.deleteById(backorderId);
    }
}
