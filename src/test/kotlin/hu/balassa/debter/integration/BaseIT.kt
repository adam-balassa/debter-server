package hu.balassa.debter.integration

import hu.balassa.debter.repository.DebterRepository
import hu.balassa.debter.util.WebTestClient
import org.mockito.Mockito.mock

open class BaseIT {
    protected val repository = mock(DebterRepository::class.java)
    protected val web = WebTestClient()
}