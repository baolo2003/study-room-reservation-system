package com.baolo.study_room_rservation_system.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationDTO {
    private Long seatId;
    private Long classroomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}