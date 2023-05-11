package pl.piter.mvp.selector.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "topic")
data class TopicProperties(
    val match: String,
    val mvp: String,
)
