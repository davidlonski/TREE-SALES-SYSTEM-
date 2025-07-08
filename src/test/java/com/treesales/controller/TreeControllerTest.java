package com.treesales.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.treesales.entity.Tree;
import com.treesales.entity.Tree.TreeStatus;
import com.treesales.service.TreeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TreeController.class)
public class TreeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TreeService treeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllTrees() throws Exception {
        // Given
        Tree tree1 = new Tree("NF001", "Noble Fir", "Premium quality", TreeStatus.Available);
        Tree tree2 = new Tree("DF001", "Douglas Fir", "Classic choice", TreeStatus.Available);
        
        when(treeService.getAllTrees()).thenReturn(Arrays.asList(tree1, tree2));

        // When & Then
        mockMvc.perform(get("/api/trees"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].barcode").value("NF001"))
                .andExpect(jsonPath("$[0].treeType").value("Noble Fir"))
                .andExpect(jsonPath("$[1].barcode").value("DF001"));
    }

    @Test
    public void testGetTreeByBarcode() throws Exception {
        // Given
        Tree tree = new Tree("NF001", "Noble Fir", "Premium quality", TreeStatus.Available);
        when(treeService.getTreeByBarcode("NF001")).thenReturn(Optional.of(tree));

        // When & Then
        mockMvc.perform(get("/api/trees/NF001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.barcode").value("NF001"))
                .andExpect(jsonPath("$.treeType").value("Noble Fir"))
                .andExpect(jsonPath("$.status").value("Available"));
    }

    @Test
    public void testGetTreeByBarcode_NotFound() throws Exception {
        // Given
        when(treeService.getTreeByBarcode("INVALID")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/trees/INVALID"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateTree() throws Exception {
        // Given
        Tree newTree = new Tree("NF002", "Noble Fir", "Premium quality", TreeStatus.Available);
        Tree savedTree = new Tree("NF002", "Noble Fir", "Premium quality", TreeStatus.Available);
        savedTree.setDateStatusUpdated(LocalDate.now());

        when(treeService.createTree(any(Tree.class))).thenReturn(savedTree);

        // When & Then
        mockMvc.perform(post("/api/trees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTree)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.barcode").value("NF002"))
                .andExpect(jsonPath("$.treeType").value("Noble Fir"));
    }

    @Test
    public void testSellTree() throws Exception {
        // Given
        Tree tree = new Tree("NF001", "Noble Fir", "Premium quality", TreeStatus.Sold);
        tree.setDateStatusUpdated(LocalDate.now());
        
        when(treeService.sellTree("NF001")).thenReturn(tree);

        // When & Then
        mockMvc.perform(post("/api/trees/NF001/sell"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.barcode").value("NF001"))
                .andExpect(jsonPath("$.status").value("Sold"));
    }

    @Test
    public void testDeleteTree() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/trees/NF001"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetAvailableTrees() throws Exception {
        // Given
        Tree tree1 = new Tree("NF001", "Noble Fir", "Premium quality", TreeStatus.Available);
        Tree tree2 = new Tree("DF001", "Douglas Fir", "Classic choice", TreeStatus.Available);
        
        when(treeService.getAvailableTrees()).thenReturn(Arrays.asList(tree1, tree2));

        // When & Then
        mockMvc.perform(get("/api/trees/available"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }
}