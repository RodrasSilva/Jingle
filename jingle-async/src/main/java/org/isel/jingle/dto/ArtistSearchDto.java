package org.isel.jingle.dto;

public class ArtistSearchDto {

    ResultsDto results;

    public ArtistSearchDto(ResultsDto results) {
        this.results = results;
    }

    public ResultsDto getResults() {
        return results;
    }
}
