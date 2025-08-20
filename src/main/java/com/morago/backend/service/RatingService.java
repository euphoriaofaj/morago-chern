package com.morago.backend.service;

import com.morago.backend.dto.RatingDto;
import java.util.List;

public interface RatingService {
    RatingDto create(RatingDto dto);
    RatingDto getById(Long id);
    List<RatingDto> getAll();
    RatingDto update(Long id, RatingDto dto);
    void delete(Long id);
}
