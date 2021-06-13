package hu.balassa.debter.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.cache.support.NoOpCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Scheduled


@Configuration
@EnableCaching
open class CacheConfig {
    @Autowired
    private lateinit var cacheManager: CacheManager

    @Bean
    @Profile("!test")
    open fun cacheManager(): CacheManager {
        return ConcurrentMapCacheManager("exchangeRates")
    }

    @Bean
    @Profile("test")
    open fun testCacheManager(): CacheManager {
        return NoOpCacheManager()
    }

    @Scheduled(fixedRate = 24*60*60*1000)
    open fun evictAllCaches() {
        cacheManager.getCache("exchangeRates")?.clear()
    }
}