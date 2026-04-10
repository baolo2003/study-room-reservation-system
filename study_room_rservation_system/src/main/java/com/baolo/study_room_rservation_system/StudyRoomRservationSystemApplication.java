package com.baolo.study_room_rservation_system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("com.baolo.study_room_rservation_system.Mapper")
@SpringBootApplication
public class StudyRoomRservationSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyRoomRservationSystemApplication.class, args);
    }

}
