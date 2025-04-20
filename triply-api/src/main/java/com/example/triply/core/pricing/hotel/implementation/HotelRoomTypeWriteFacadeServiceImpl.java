package com.example.triply.core.pricing.hotel.implementation;

import com.example.triply.core.hotel.mapper.HotelRoomTypeMapper;
import com.example.triply.core.hotel.model.dto.HotelRoomTypeDTO;
import com.example.triply.core.hotel.model.entity.HotelRoomType;
import com.example.triply.core.hotel.repository.HotelRoomTypeRepository;
import com.example.triply.core.pricing.hotel.notification.HotelRoomTypeListener;
import com.example.triply.core.pricing.hotel.notification.HotelRoomTypeWritePublisherImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class HotelRoomTypeWriteFacadeServiceImpl {

    private final HotelRoomTypeRepository hotelRoomTypePriceRepository;
    private final HotelRoomTypeMapper hotelRoomTypePriceMapper;
    private final HotelRoomTypeWritePublisherImpl publisher;

    public HotelRoomTypeWriteFacadeServiceImpl(HotelRoomTypeRepository hotelRoomTypePriceRepository, HotelRoomTypeMapper hotelRoomTypePriceMapper, HotelRoomTypeWritePublisherImpl publisher) {
        this.hotelRoomTypePriceRepository = hotelRoomTypePriceRepository;
        this.hotelRoomTypePriceMapper = hotelRoomTypePriceMapper;
        this.publisher = publisher;
    }

    @Transactional
    public List<HotelRoomTypeDTO> updateExistingHotelRoomType(Long hotelRoomTypeId, BigDecimal newBasePrice) {
        //TODO: (Bryan) Swap repository calls to service calls
        Optional<HotelRoomType> hotelRoomTypeOptional = hotelRoomTypePriceRepository.findById(hotelRoomTypeId);
        List<HotelRoomTypeDTO> oldHotelRoomTypes = new ArrayList<>();
        List<HotelRoomTypeDTO> newHotelRoomTypes = new ArrayList<>();

        if (hotelRoomTypeOptional.isPresent()) {
            HotelRoomType oldHotelRoomType = hotelRoomTypeOptional.get();
            oldHotelRoomTypes.add(hotelRoomTypePriceMapper.toDto(oldHotelRoomType));

            HotelRoomType newHotelRoomType = new HotelRoomType(oldHotelRoomType);
            newHotelRoomType.setBasePrice(newBasePrice.setScale(2, RoundingMode.HALF_EVEN));
            newHotelRoomTypes.add(hotelRoomTypePriceMapper.toDto(newHotelRoomType));
            hotelRoomTypePriceRepository.save(newHotelRoomType);

            publisher.publish(oldHotelRoomTypes, newHotelRoomTypes);
            return newHotelRoomTypes;
        }
        return Collections.emptyList();
    }

    @Transactional
    public List<HotelRoomTypeDTO> insertNewHotelRoomType(Long hotelId,
                                                  BigDecimal newBasePrice, BigDecimal discount,
                                                  BigDecimal surgeMultiplier) {
        //TODO: Validate hotel and hotel class exists in their repos


        //TODO: Validate no existing HotelRoomType for this hotelId and hotelClassId

        //TODO: Infer departureDate from Hotel

        //TODO:  Build new Hotelprice with null id
        HotelRoomType newHotelRoomType = new HotelRoomType();

        // Insert new HotelRoomType into Repo
        hotelRoomTypePriceRepository.save(newHotelRoomType);

        //Notify
        List<HotelRoomTypeDTO> oldHotelRoomTypes = new ArrayList<>();
        List<HotelRoomTypeDTO> newHotelRoomTypes = new ArrayList<>();
        newHotelRoomTypes.add(hotelRoomTypePriceMapper.toDto(newHotelRoomType));
        publisher.publish(oldHotelRoomTypes, newHotelRoomTypes);
        return newHotelRoomTypes;
    }
        public void addPriceListener(HotelRoomTypeListener listener) {
        publisher.addListener(listener);
    }

    public void removePriceListener(HotelRoomTypeListener listener) {
        publisher.removeListener(listener);
    }

}
