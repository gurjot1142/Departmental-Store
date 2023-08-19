package com.example.DepartmentalStoreCrud.unit.controller;

import com.example.DepartmentalStoreCrud.bean.Backorder;
import com.example.DepartmentalStoreCrud.controller.BackorderController;
import com.example.DepartmentalStoreCrud.repository.BackorderRepository;
import com.example.DepartmentalStoreCrud.service.BackorderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration(classes = AutoConfigureMockMvc.class)
@WebMvcTest(BackorderController.class)
public class BackorderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private BackorderService backorderService;

    @Mock
    private BackorderRepository backorderRepository;

    @InjectMocks
    private BackorderController backorderController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(backorderController).build();
    }

    @Test
    void createBackorderTest() throws Exception {
        Backorder backorder = createBackorder();

        mockMvc.perform(post("/backorders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(backorder)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Backorder created successfully."));

        verify(backorderService, never()).createBackorder(backorder);
    }

    @Test
    void getAllBackordersTest() throws Exception {
        List<Backorder> backorders = new ArrayList<>();
        backorders.add(createBackorder());

        when(backorderService.getAllBackorders()).thenReturn(backorders);

        mockMvc.perform(get("/backorders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", is(backorders.size())));

        verify(backorderService, times(1)).getAllBackorders();
    }

    //positive case - valid backorder id
    @Test
    void getBackorderByIdTest_backorderFound() throws Exception {
        Long backorderId = 1L;
        Backorder backorder = createBackorder();
        when(backorderService.getBackorderById(backorderId)).thenReturn(backorder);

        mockMvc.perform(get("/backorders/{backorderId}", backorderId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(backorderService, times(1)).getBackorderById(backorderId);
    }

    //negative case - invalid backorder id
    @Test
    void getBackorderByIdTest_BackorderNotFound() throws Exception {
        Long backorderID = 1L;
        when(backorderService.getBackorderById(backorderID)).thenReturn(null);

        mockMvc.perform(get("/{backorderId}", backorderID))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBackorderTest() throws Exception {
        Long backorderId = 1L;
        doNothing().when(backorderService).deleteBackorder(backorderId);

        mockMvc.perform(delete("/backorders/{backorderId}", backorderId))
                .andExpect(status().isOk());

        verify(backorderService, times(1)).deleteBackorder(backorderId);
    }

    @Test
    void deleteBackorderTest_backorderNotFound() throws Exception {
        Long backorderId = 1L;
        doNothing().when(backorderService).deleteBackorder(backorderId);

        mockMvc.perform(delete("/{backorderId}", backorderId))
                .andExpect(status().isNotFound());

        verify(backorderService, never()).deleteBackorder(backorderId);
    }

    private Backorder createBackorder() {
        Backorder backorder = new Backorder();
        backorder.setBackorderID(1L);
        backorder.setOrder(null);
        return backorder;
    }
}
