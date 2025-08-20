package com.morago.backend.service;

import com.morago.backend.dto.RatingDto;
import com.morago.backend.entity.Rating;
import com.morago.backend.exception.ResourceNotFoundException;
import com.morago.backend.mapper.RatingMapper;
import com.morago.backend.repository.RatingRepository;
import com.morago.backend.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;

    @Override
    public RatingDto create(RatingDto dto) {
        Rating rating = ratingMapper.toEntity(dto);
        return ratingMapper.toDto(ratingRepository.save(rating));
    }

    @Override
    public RatingDto getById(Long id) {
        return ratingMapper.toDto(findRatingOrThrow(id));
    }

    @Override
    public List<RatingDto> getAll() {
        return ratingRepository.findAll()
                .stream()
                .map(ratingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public RatingDto update(Long id, RatingDto dto) {
        Rating rating = findRatingOrThrow(id);
        rating.setScore(dto.getScore());
        rating.setComment(dto.getComment());
        return ratingMapper.toDto(ratingRepository.save(rating));
    }

    @Override
    public void delete(Long id) {
        Rating rating = findRatingOrThrow(id);
        ratingRepository.delete(rating);
    }

    private Rating findRatingOrThrow(Long id) {
        return ratingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found with id: " + id));
    }
}
