package com.treesales.controller;

import com.treesales.entity.Tree.TreeStatus;
import com.treesales.entity.Scout.ScoutStatus;
import com.treesales.service.TreeService;
import com.treesales.service.ScoutService;
import com.treesales.service.TreeTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/")
public class DashboardController {
    
    @Autowired
    private TreeService treeService;
    
    @Autowired
    private ScoutService scoutService;
    
    @Autowired
    private TreeTypeService treeTypeService;
    
    // Main dashboard view
    @GetMapping
    public String dashboard(Model model) {
        try {
            // Get summary statistics
            long totalTrees = treeService.getAllTrees().size();
            long availableTrees = treeService.getCountByStatus(TreeStatus.Available);
            long soldTrees = treeService.getCountByStatus(TreeStatus.Sold);
            long damagedTrees = treeService.getCountByStatus(TreeStatus.Damaged);
            
            long totalScouts = scoutService.getAllScouts().size();
            long activeScouts = scoutService.getCountByStatus(ScoutStatus.Active);
            long inactiveScouts = scoutService.getCountByStatus(ScoutStatus.Inactive);
            
            long totalTreeTypes = treeTypeService.getAllTreeTypes().size();
            
            // Add to model
            model.addAttribute("totalTrees", totalTrees);
            model.addAttribute("availableTrees", availableTrees);
            model.addAttribute("soldTrees", soldTrees);
            model.addAttribute("damagedTrees", damagedTrees);
            model.addAttribute("totalScouts", totalScouts);
            model.addAttribute("activeScouts", activeScouts);
            model.addAttribute("inactiveScouts", inactiveScouts);
            model.addAttribute("totalTreeTypes", totalTreeTypes);
            
            return "dashboard";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading dashboard: " + e.getMessage());
            return "error";
        }
    }
    
    // API endpoint for dashboard statistics
    @GetMapping("/api/dashboard/stats")
    @ResponseBody
    public ResponseEntity<?> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Tree statistics
            Map<String, Long> treeStats = new HashMap<>();
            treeStats.put("total", (long) treeService.getAllTrees().size());
            treeStats.put("available", treeService.getCountByStatus(TreeStatus.Available));
            treeStats.put("sold", treeService.getCountByStatus(TreeStatus.Sold));
            treeStats.put("damaged", treeService.getCountByStatus(TreeStatus.Damaged));
            treeStats.put("inactive", treeService.getCountByStatus(TreeStatus.Inactive));
            
            // Scout statistics
            Map<String, Long> scoutStats = new HashMap<>();
            scoutStats.put("total", (long) scoutService.getAllScouts().size());
            scoutStats.put("active", scoutService.getCountByStatus(ScoutStatus.Active));
            scoutStats.put("inactive", scoutService.getCountByStatus(ScoutStatus.Inactive));
            
            // Tree type statistics
            Map<String, Long> treeTypeStats = new HashMap<>();
            treeTypeStats.put("total", (long) treeTypeService.getAllTreeTypes().size());
            
            stats.put("trees", treeStats);
            stats.put("scouts", scoutStats);
            stats.put("treeTypes", treeTypeStats);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving dashboard stats: " + e.getMessage());
        }
    }
    
    // Trees needing attention endpoint
    @GetMapping("/api/dashboard/attention")
    @ResponseBody
    public ResponseEntity<?> getItemsNeedingAttention() {
        try {
            Map<String, Object> attention = new HashMap<>();
            attention.put("trees", treeService.getTreesNeedingAttention());
            return ResponseEntity.ok(attention);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving items needing attention: " + e.getMessage());
        }
    }
}