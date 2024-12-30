package com.journal.crawler.controller;


import com.journal.crawler.entity.JournalBasic;
import com.journal.crawler.service.JournalBasicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/journal")
public class JournalController {

    @Autowired
    private JournalBasicService journalBasicService;

    @GetMapping("/list")
    public List<JournalBasic> list() {
        return journalBasicService.list();
    }

    @PostMapping("/add")
    public boolean add(@RequestBody JournalBasic journalBasic) {
        return journalBasicService.save(journalBasic);
    }

    @PutMapping("/update")
    public boolean update(@RequestBody JournalBasic journalBasic) {
        return journalBasicService.updateById(journalBasic);
    }

    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable Long id) {
        return journalBasicService.removeById(id);
    }
}