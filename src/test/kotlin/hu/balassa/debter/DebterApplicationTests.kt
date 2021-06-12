package hu.balassa.debter

import hu.balassa.debter.config.TestConfig
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(TestConfig::class)
class DebterApplicationTests {

    @Test
    fun contextLoads() {
    }

}
