package com.example.DepartmentalStoreCrud.integration.service;

import com.example.DepartmentalStoreCrud.bean.Backorder;
import com.example.DepartmentalStoreCrud.repository.BackorderRepository;
import com.example.DepartmentalStoreCrud.service.BackorderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class BackorderServiceIntegrationTests {

    @Autowired
    private BackorderRepository backorderRepository;

    @Autowired
    private BackorderService backorderService;

    @Test
    void testGetAllBackorders() {
        List<Backorder> backorderList = new ArrayList<>();
        backorderList.add(createBackorder(1L));
        backorderList.add(createBackorder(2L));
        backorderRepository.saveAll(backorderList);
        assertEquals(2, backorderService.getAllBackorders().size());
    }

    @Test
    void testGetBackorderById_BackorderFound() {
        Backorder backorder = createBackorder(1L);
        backorderRepository.save(backorder);
        Backorder findBackorder = backorderService.getBackorderById(1L);
        assertNotNull(findBackorder);
    }

    //negative backorder not found
    @Test
    void testGetBackorderById_BackorderNotFound() {
        assertThatThrownBy(() -> backorderService.getBackorderById(1L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void testDeleteBackorderById_Successful() {
        Backorder backorder = createBackorder(1L);
        backorderRepository.save(backorder);
        backorderRepository.deleteById(1L);
        assertThat(backorderRepository.findAll().isEmpty());
    }

    //negative backorder not found
    @Test
    void testDeleteBackorderById_BackorderNotFound() {
        assertThatThrownBy(() -> backorderService.deleteBackorder(1L))
                .isInstanceOf(NoSuchElementException.class);
    }

    private Backorder createBackorder(Long id) {
        Backorder backorder = new Backorder();
        backorder.setBackorderID(id);
        backorder.setOrder(null);
        return backorder;
    }
}
