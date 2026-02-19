package com.artnest.service;

import com.artnest.dto.ArtistSearchPageResponse;
import org.springframework.stereotype.Service;

@Service
public interface ArtistService {
    ArtistSearchPageResponse searchArtists(String artType, Integer page, Integer size, String sortBy, String sortDir);
}
