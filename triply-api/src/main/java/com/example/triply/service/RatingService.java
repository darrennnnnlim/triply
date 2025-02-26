package com.example.triply.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.triply.model.Rating;

@Service
public class RatingService {
    private List<Rating> ratingList = new ArrayList<>();

    public List<Rating> getAllRatings(){
        return ratingList;
    }
}
