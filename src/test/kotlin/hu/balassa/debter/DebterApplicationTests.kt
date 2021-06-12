package hu.balassa.debter

import hu.balassa.debter.config.TestConfig
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT
import org.springframework.context.annotation.Import

@SpringBootTest( webEnvironment = DEFINED_PORT)
@Import(TestConfig::class)
class DebterApplicationTests {

    @Test
    fun contextLoads() {
    }

}
