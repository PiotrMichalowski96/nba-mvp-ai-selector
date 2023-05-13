package pl.piter.nba.api.config

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.kafka.support.JacksonUtils
import pl.piter.commons.api.model.scores.GameListResponse
import pl.piter.commons.api.model.scores.GameResponse
import java.time.Duration

@EnableCaching
@Configuration
@EnableConfigurationProperties(value = [CacheProperties::class])
class CacheConfig(private val cacheProperties: CacheProperties) {

    @Bean
    fun redisCacheManagerBuilderCustomizer() = RedisCacheManagerBuilderCustomizer {
        it.withCacheConfiguration(cacheProperties.gameName, cacheConfiguration(GameResponse::class.java))
            .withCacheConfiguration(cacheProperties.gameListName, cacheConfiguration(GameListResponse::class.java))
    }

    private fun <T> cacheConfiguration(clazz: Class<T>) = RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofMinutes(cacheProperties.timeoutInMinutes))
        .disableCachingNullValues()
        .serializeKeysWith(SerializationPair.fromSerializer(StringRedisSerializer()))
        .serializeValuesWith(SerializationPair.fromSerializer(jsonRedisSerializer(clazz)))

    private fun <T> jsonRedisSerializer(clazz: Class<T>) =
        Jackson2JsonRedisSerializer(JacksonUtils.enhancedObjectMapper(), clazz)
}