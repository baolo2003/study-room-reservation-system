package com.baolo.study_room_rservation_system;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;

@SpringBootTest
class StudyRoomRservationSystemApplicationTests {

    @Test
    void contextLoads() {
    }

    @PostMapping("/test")
    public String test() {
        return "test ok";
    }


}
