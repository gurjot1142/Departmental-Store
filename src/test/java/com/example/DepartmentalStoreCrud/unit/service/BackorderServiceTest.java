package com.example.DepartmentalStoreCrud.unit.service;

import com.example.DepartmentalStoreCrud.bean.Backorder;
import com.example.DepartmentalStoreCrud.repository.BackorderRepository;
import com.example.DepartmentalStoreCrud.service.BackorderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BackorderServiceTest {

    @Mock
    private BackorderRepository backorderRepository;

    @InjectMocks
    private BackorderService backorderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllBackorders() {
        List<Backorder> backorders = new ArrayList<>();
        backorders.add(new Backorder());
        backorders.add(new Backorder());
        when(backorderRepository.findAll()).thenReturn(backorders);
        assertNotNull(backorders);
        List<Backorder> result = backorderService.getAllBackorders();
        assertEquals(backorders, result);
        assertEquals(2, result.size());
        verify(backorderRepository, times(1)).findAll();
    }

    @Test
    public void testGetBackorderById() {
        // Arrange
        Long backorderId = 1L;
        Backorder backorder = new Backorder();
        backorder.setBackorderID(backorderId);
        when(backorderRepository.findById(backorderId)).thenReturn(Optional.of(backorder));

        //Argument captor
        ArgumentCaptor<Long> backorderIdCaptor = ArgumentCaptor.forClass(Long.class);

        // Act
        Backorder result = backorderService.getBackorderById(backorderId);

        // Assert
        assertEquals(backorder, result);
        verify(backorderRepository, times(1)).findById(backorderIdCaptor.capture());
        assertEquals(backorderId, backorderIdCaptor.getValue());
    }

    @Test
    public void testGetBackorderById_NonexistentId() {
        // Arrange
        Long backorderId = 1L;
        when(backorderRepository.findById(backorderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> backorderService.getBackorderById(backorderId));
        verify(backorderRepository, times(1)).findById(backorderId);
    }

    @Test
    public void testCreateBackorder() {
        Backorder backorder = createBackorder(1L);

        // Argument captor
        ArgumentCaptor<Backorder> backorderCaptor = ArgumentCaptor.forClass(Backorder.class);

        Backorder newBackorder = backorderService.createBackorder(backorder);
        verify(backorderRepository, times(1)).save(backorderCaptor.capture());
        assertEquals(backorder, backorderCaptor.getValue());
    }

    @Test
    public void testDeleteBackorder() {
        // Arrange
        Long backorderId = 1L;
        Backorder backorder = createBackorder(backorderId);
        when(backorderRepository.findById(backorderId)).thenReturn(Optional.of(backorder));

        //Argument captor
        ArgumentCaptor<Long> backorderIdCaptor = ArgumentCaptor.forClass(Long.class);

        // Act
        backorderService.deleteBackorder(backorderId);

        // Assert
        verify(backorderRepository, times(1)).deleteById(backorderIdCaptor.capture());
        assertEquals(backorderId, backorderIdCaptor.getValue());
    }

    @Test
    public void testDeleteBackorder_NonExisting() {
        Long backorderId = 3L;
        when(backorderRepository.findById(backorderId)).thenReturn(Optional.empty());

        // Act
        assertThrows(NoSuchElementException.class, () -> backorderService.deleteBackorder(backorderId));

        // Assert
        verify(backorderRepository, times(1)).findById(backorderId);
        verify(backorderRepository, never()).delete(any());
    }

    private Backorder createBackorder(Long backorderId) {
        Backorder backorder = new Backorder();
        backorder.setBackorderID(1L);
        backorder.setOrder(null);
        return backorder;
    }
}
