package com.example.DepartmentalStoreCrud.integration.controller;

import com.example.DepartmentalStoreCrud.bean.Backorder;
import com.example.DepartmentalStoreCrud.repository.BackorderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class BackorderControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BackorderRepository backorderRepository;

    @Test
    void testGetAllBackorders() throws Exception {
        List<Backorder> backorderList = new ArrayList<>();
        backorderList.add(createBackorder(1L));
        backorderList.add(createBackorder(2L));
        backorderRepository.saveAll(backorderList);
        mockMvc.perform(MockMvcRequestBuilders.get("/backorders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(backorderList.size())));
    }

    @Test
    void testGetBackorderById_BackorderFound() throws Exception {
        Backorder backorder = createBackorder(1L);
        backorderRepository.save(backorder);
        mockMvc.perform(MockMvcRequestBuilders.get("/backorders/{backorderID}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    //negative case invalid backorder id
    @Test
    void testGetBackorderById_BackorderNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/backorders/{backorderID}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteBackorderById_Successful() throws Exception {
        Backorder backorder = createBackorder(1L);
        backorderRepository.save(backorder);
        mockMvc.perform(MockMvcRequestBuilders.delete("/backorders/{backorderID}", 1L))
                .andExpect(status().isOk());
    }

    //negative backorder not found
    @Test
    void testDeleteBackorder_BackorderNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/backorders/{backorderID}", 1L))
                .andExpect(status().isNotFound());
    }

    private Backorder createBackorder(Long id) {
        Backorder backorder = new Backorder();
        backorder.setBackorderID(id);
        backorder.setOrder(null);
        return backorder;
    }
}
