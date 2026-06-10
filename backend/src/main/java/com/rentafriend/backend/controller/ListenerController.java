package com.rentafriend.backend.controller;

import com.rentafriend.backend.dto.ListenerCardDto;
import com.rentafriend.backend.dto.ListenerDetailDto;
import com.rentafriend.backend.service.ListenerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listeners")
public class ListenerController {

    private final ListenerService listenerService;

    public ListenerController(ListenerService listenerService) {
        this.listenerService = listenerService;
    }

    @GetMapping
    public List<ListenerCardDto> getAllListeners() {
        return listenerService.getAllListeners();
    }

    @GetMapping("/{id}")
    public ListenerDetailDto getListener(@PathVariable Long id) {
        return listenerService.getListener(id);
    }
}

