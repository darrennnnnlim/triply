package com.example.triply.core.pricethreshold.mapper;

import com.example.triply.core.auth.entity.User;
import com.example.triply.core.pricethreshold.dto.PriceThresholdDTO;
import com.example.triply.core.pricethreshold.entity.PriceThreshold;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-01T23:49:11+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class PriceThresholdMapperImpl implements PriceThresholdMapper {

    @Override
    public PriceThresholdDTO toDto(PriceThreshold priceThreshold) {
        if ( priceThreshold == null ) {
            return null;
        }

        PriceThresholdDTO priceThresholdDTO = new PriceThresholdDTO();

        priceThresholdDTO.setUserId( priceThresholdUserId( priceThreshold ) );
        priceThresholdDTO.setId( priceThreshold.getId() );
        priceThresholdDTO.setThresholdPrice( priceThreshold.getThresholdPrice() );

        return priceThresholdDTO;
    }

    @Override
    public List<PriceThresholdDTO> toDtoList(List<PriceThreshold> priceThresholds) {
        if ( priceThresholds == null ) {
            return null;
        }

        List<PriceThresholdDTO> list = new ArrayList<PriceThresholdDTO>( priceThresholds.size() );
        for ( PriceThreshold priceThreshold : priceThresholds ) {
            list.add( toDto( priceThreshold ) );
        }

        return list;
    }

    private Long priceThresholdUserId(PriceThreshold priceThreshold) {
        if ( priceThreshold == null ) {
            return null;
        }
        User user = priceThreshold.getUser();
        if ( user == null ) {
            return null;
        }
        Long id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
