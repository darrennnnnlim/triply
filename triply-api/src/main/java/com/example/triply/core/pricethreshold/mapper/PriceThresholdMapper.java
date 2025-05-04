package com.example.triply.core.pricethreshold.mapper;

import com.example.triply.core.pricethreshold.dto.PriceThresholdDTO;
import com.example.triply.core.pricethreshold.entity.PriceThreshold;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring") // Integrate with Spring DI
public interface PriceThresholdMapper {

    PriceThresholdMapper INSTANCE = Mappers.getMapper(PriceThresholdMapper.class);

    @Mapping(source = "user.id", target = "userId")
    //@Mapping(source = "flight.id", target = "flightId")
    //@Mapping(source = "hotel.id", target = "hotelId")
    PriceThresholdDTO toDto(PriceThreshold priceThreshold);

    // MapStruct automatically handles list mapping if the single element mapping is defined
    List<PriceThresholdDTO> toDtoList(List<PriceThreshold> priceThresholds);

    // We might not need a mapping from DTO back to entity for creation,
    // as the service handles building the entity from CreatePriceThresholdRequest.
    // If needed later, it can be added here.
    // @Mapping(target = "user", ignore = true) // Ignore complex objects during reverse mapping initially
    // @Mapping(target = "flight", ignore = true)
    // @Mapping(target = "hotel", ignore = true)
    // PriceThreshold toEntity(PriceThresholdDTO priceThresholdDTO);
}