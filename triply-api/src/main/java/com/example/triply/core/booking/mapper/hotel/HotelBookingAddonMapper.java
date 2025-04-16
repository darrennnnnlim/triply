package com.example.triply.core.booking.mapper.hotel;

import com.example.triply.common.mapper.BaseMapper;
import com.example.triply.core.booking.dto.HotelBookingAddonDTO;
import com.example.triply.core.hotel.model.entity.HotelAddon;
import com.example.triply.core.booking.entity.hotel.HotelBooking;
import com.example.triply.core.booking.entity.hotel.HotelBookingAddon;
import com.example.triply.core.hotel.repository.HotelAddonRepository;
import com.example.triply.core.booking.repository.hotel.HotelBookingRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class HotelBookingAddonMapper implements BaseMapper<HotelBookingAddon, HotelBookingAddonDTO> {

    private final HotelBookingRepository hotelBookingRepository;

    private final HotelAddonRepository hotelAddonRepository;

    public HotelBookingAddonMapper(HotelBookingRepository hotelBookingRepository, HotelAddonRepository hotelAddonRepository) {
        this.hotelBookingRepository = hotelBookingRepository;
        this.hotelAddonRepository = hotelAddonRepository;
    }

    @Override
    public HotelBookingAddonDTO toDto(HotelBookingAddon entity) {
        if (entity == null) {
            return null;
        }

        HotelBookingAddonDTO dto = new HotelBookingAddonDTO();
        dto.setId(entity.getId());
        dto.setHotelBookingId(entity.getHotelBooking().getId());
        dto.setHotelAddonId(entity.getHotelAddon().getId());
        dto.setQuantity(entity.getQuantity());
        dto.setTotalPrice(entity.getTotalPrice());

        mapAuditFieldsToDto(entity, dto);

        return dto;
    }

    @Override
    public HotelBookingAddon toEntity(HotelBookingAddonDTO dto) {
        if (dto == null) {
            return null;
        }

        HotelBookingAddon entity = new HotelBookingAddon();
        entity.setId(dto.getId());

        Optional<HotelBooking> hotelBookingOptional = hotelBookingRepository.findById(dto.getHotelBookingId());
        if (hotelBookingOptional.isPresent()) {
            entity.setHotelBooking(hotelBookingOptional.get());
        } else {
            entity.setHotelBooking(null);
        }

        Optional<HotelAddon> hotelAddonOptional = hotelAddonRepository.findById(dto.getHotelAddonId());
        if (hotelAddonOptional.isPresent()) {
            entity.setHotelAddon(hotelAddonOptional.get());
        } else {
            entity.setHotelAddon(null);
        }

        entity.setQuantity(dto.getQuantity());
        entity.setTotalPrice(dto.getTotalPrice());

        mapAuditFieldsToEntity(dto, entity);

        return entity;
    }
}
