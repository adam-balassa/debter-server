package hu.balassa.debter

import hu.balassa.debter.integration.BaseIT
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.context.annotation.Import

@SpringBootTest( webEnvironment = RANDOM_PORT)
@Import(BaseIT::class)
class DebterApplicationTests {

    @Test
    fun contextLoads() {
    }

}
