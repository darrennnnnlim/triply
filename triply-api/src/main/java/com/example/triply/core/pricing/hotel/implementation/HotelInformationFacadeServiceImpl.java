package com.example.triply.core.pricing.hotel.implementation;

import com.example.triply.core.hotel.model.entity.Hotel;
import com.example.triply.core.hotel.model.entity.HotelRoomPrice;
import com.example.triply.core.hotel.model.entity.HotelRoomType;
import com.example.triply.core.hotel.repository.HotelRepository;
import com.example.triply.core.hotel.repository.HotelRoomPriceRepository;
import com.example.triply.core.hotel.repository.HotelRoomTypeRepository;
import com.example.triply.core.pricing.hotel.model.dto.HotelOfferDTO;
import com.example.triply.core.search.hotel.model.dto.HotelSearchRequestDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class HotelInformationFacadeServiceImpl {

    private HotelRepository hotelRepository;

    private HotelRoomTypeRepository hotelRoomTypeRepository;

    private HotelRoomPriceRepository hotelRoomPriceRepository;

    public HotelInformationFacadeServiceImpl(HotelRepository hotelRepository, HotelRoomTypeRepository hotelRoomTypeRepository, HotelRoomPriceRepository hotelRoomPriceRepository) {
        this.hotelRepository = hotelRepository;
        this.hotelRoomTypeRepository = hotelRoomTypeRepository;
        this.hotelRoomPriceRepository = hotelRoomPriceRepository;
    }

    public List<HotelOfferDTO> getHotelPrices(HotelSearchRequestDTO hotelSearchRequest) {
        List<HotelOfferDTO> hotelOfferDTOS = new ArrayList<>();

        List<Hotel> hotels =  hotelRepository.findAllByLocation(hotelSearchRequest.getLocation());

        for (Hotel hotel : hotels) {
            List<HotelRoomType> hotelRoomTypes = hotelRoomTypeRepository.findAllByHotelId(hotel.getId());

            for (HotelRoomType hotelRoomType : hotelRoomTypes) {
                List<HotelRoomPrice> hotelRoomPrices = hotelRoomPriceRepository.findPricesWithOverlappingDates(hotelRoomType.getId(), hotelSearchRequest.getCheckInDate().atStartOfDay(), hotelSearchRequest.getCheckOutDate().atStartOfDay());

                LocalDate checkInDate = hotelSearchRequest.getCheckInDate();
                LocalDate checkOutDate = hotelSearchRequest.getCheckOutDate();
                BigDecimal basePrice = hotelRoomType.getBasePrice();

                HotelOfferDTO hotelOfferDTO = new HotelOfferDTO();

                hotelOfferDTO.setHotelId(hotel.getId());
                hotelOfferDTO.setHotelName(hotel.getName());
                hotelOfferDTO.setLocation(hotel.getLocation());

                hotelOfferDTO.setHotelRoomTypeId(hotelRoomType.getId());
                hotelOfferDTO.setCapacity(hotelRoomType.getCapacity());
                hotelOfferDTO.setHotelRoomTypeName(hotelRoomType.getName());

                hotelOfferDTO.setCheckInDate(hotelSearchRequest.getCheckInDate());
                hotelOfferDTO.setCheckOutDate(hotelSearchRequest.getCheckOutDate());

                // Calculate total price for this hotelOffer
                BigDecimal totalAdditionalPrice = calculateTotalAdditionalPrice(checkInDate, checkOutDate, hotelRoomPrices);
                Long noOfNights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
                BigDecimal totalBasePrice = basePrice.multiply(BigDecimal.valueOf(noOfNights));
                BigDecimal totalPrice = totalBasePrice.add(totalAdditionalPrice);

                hotelOfferDTO.setTotalPrice(totalPrice);

                hotelOfferDTOS.add(hotelOfferDTO);
            }
        }
        hotelOfferDTOS.sort(Comparator.comparing(HotelOfferDTO::getTotalPrice));
        return hotelOfferDTOS;
    }

    private BigDecimal calculateTotalAdditionalPrice(LocalDate checkInDate, LocalDate checkOutDate, List<HotelRoomPrice> hotelRoomPrices) {
        BigDecimal totalAdditionalPrice = BigDecimal.ZERO;
        for (HotelRoomPrice hotelRoomPrice : hotelRoomPrices) {
            LocalDate hotelRoomPriceStart = hotelRoomPrice.getStartDate().toLocalDate();
            LocalDate hotelRoomPriceEnd = hotelRoomPrice.getEndDate().toLocalDate();

            LocalDate overlapStart = checkInDate.isAfter(hotelRoomPriceStart) ? checkInDate : hotelRoomPriceStart;
            LocalDate overlapEnd = checkOutDate.isBefore(hotelRoomPriceEnd) ? checkOutDate : hotelRoomPriceEnd;

            Long overlappingNights = ChronoUnit.DAYS.between(overlapStart, overlapEnd);
            totalAdditionalPrice = totalAdditionalPrice.add(hotelRoomPrice.getPrice().multiply(BigDecimal.valueOf(overlappingNights)));
        }
        return totalAdditionalPrice;
    }
}
