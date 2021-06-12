package hu.balassa.debter.integration

import hu.balassa.debter.config.TestConfig
import hu.balassa.debter.model.Room
import hu.balassa.debter.repository.DebterRepository
import hu.balassa.debter.service.DebtArrangementService
import hu.balassa.debter.util.testRoom
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient

@Import(TestConfig::class)
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class PaymentIT {
    companion object {
        private const val ROOM_KEY = "ROOMID"
    }

    @Autowired
    private lateinit var repository: DebterRepository

    @Autowired
    private lateinit var web: WebTestClient

    @MockBean
    private lateinit var debtArrangementService: DebtArrangementService

    @Test
    fun addPayment() {
        whenever(repository.findByKey(ROOM_KEY)).thenReturn(testRoom(ROOM_KEY))

        web.post().uri("room/$ROOM_KEY/payment")
            .bodyValue(object {
                val value = 10.0
                val memberId = "member1"
                val currency = "HUF"
                val note = "test note"
                val date = "2020-09-12T12:30:00+02:00"
                val included = listOf("member1", "member2")
            })
            .exchange()
            .expectStatus().isCreated

        argumentCaptor<Room> {
            verify(repository).save(capture())
            assertThat(firstValue.key).isEqualTo(ROOM_KEY)
            assertThat(firstValue.members.find { it.id == "member1" }!!.payments).hasSize(3).anySatisfy {
                assertThat(it.value).isEqualTo(10.0)
                assertThat(it.convertedValue).isEqualTo(10.0)
                assertThat(it.active).isTrue
                assertThat(it.active).isTrue
                assertThat(it.active).isTrue
                assertThat(it.active).isTrue
            }
        }
        argumentCaptor<Room> {
            verify(debtArrangementService).arrangeDebts(capture())
            assertThat(firstValue.key).isEqualTo(ROOM_KEY)
        }
    }
}