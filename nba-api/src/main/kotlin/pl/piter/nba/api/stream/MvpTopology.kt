package pl.piter.nba.api.stream

import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier
import org.apache.kafka.streams.state.Stores
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.support.serializer.JsonSerde
import org.springframework.stereotype.Component
import pl.piter.commons.api.model.nba.NbaGameMvp
import pl.piter.commons.domain.MvpEvent
import pl.piter.nba.api.config.TopicProperties
import pl.piter.nba.api.mapper.toNbaGameMvp
import pl.piter.nba.api.validation.AnnotationValidator

@Component
class MvpTopology(private val topicProperties: TopicProperties,
                  private val validator: AnnotationValidator) {

    companion object {
        const val MVP_STORE = "mvp-store"
    }

    @Autowired
    fun buildPipeline(streamsBuilder: StreamsBuilder) {
        val nbaGameMvpSerde: Serde<NbaGameMvp> = JsonSerde(NbaGameMvp::class.java)
        val storeSupplier: KeyValueBytesStoreSupplier = Stores.persistentKeyValueStore(MVP_STORE)

        mvpEvents(streamsBuilder)
            .filter { _, mvpEvent -> validator.validate(mvpEvent) }
            .mapValues { v -> v.toNbaGameMvp() }
            .toTable(
                Materialized.`as`<String, NbaGameMvp>(storeSupplier)
                    .withKeySerde(Serdes.String())
                    .withValueSerde(nbaGameMvpSerde)
                    .withCachingEnabled()
            )

    }

    private fun mvpEvents(streamsBuilder: StreamsBuilder): KStream<String, MvpEvent> {
        val mvpEventSerde: Serde<MvpEvent> = JsonSerde(MvpEvent::class.java)
        val mvpEventsConsumed = Consumed.with(Serdes.String(), mvpEventSerde)
        return streamsBuilder.stream(topicProperties.mvp, mvpEventsConsumed)
    }
}