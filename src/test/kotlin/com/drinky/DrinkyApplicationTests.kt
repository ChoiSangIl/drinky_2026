package com.drinky

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class DrinkyApplicationTests {

    @Test
    fun contextLoads() {
        // Context loading test - will fail until DB is configured
    }
}
