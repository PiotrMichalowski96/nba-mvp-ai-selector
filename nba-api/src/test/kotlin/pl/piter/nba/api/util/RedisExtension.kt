package pl.piter.nba.api.util

import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import redis.embedded.RedisServer
import redis.embedded.RedisServerBuilder

class RedisExtension : BeforeAllCallback, AfterAllCallback {

    private val redisServer: RedisServer = RedisServerBuilder()
        .port(6379)
        .build()

    override fun beforeAll(context: ExtensionContext?) = redisServer.start()

    override fun afterAll(context: ExtensionContext?) = redisServer.stop()
}