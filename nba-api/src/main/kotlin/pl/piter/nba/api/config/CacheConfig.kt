package pl.piter.nba.api.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.kafka.support.JacksonUtils
import pl.piter.commons.api.model.scores.GameResponse
import java.time.Duration

@EnableCaching
@Configuration
class CacheConfig(@Value("\${cache.timeoutInMinutes:180}") private val cacheTimeoutInMinutes: Long) {

    @Bean
    fun cacheConfiguration(): RedisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofMinutes(cacheTimeoutInMinutes))
        .disableCachingNullValues()
        .serializeKeysWith(SerializationPair.fromSerializer(StringRedisSerializer()))
        .serializeValuesWith(SerializationPair.fromSerializer(gameResponseSerializer()))

    private fun gameResponseSerializer() =
        Jackson2JsonRedisSerializer(JacksonUtils.enhancedObjectMapper(), GameResponse::class.java)
}