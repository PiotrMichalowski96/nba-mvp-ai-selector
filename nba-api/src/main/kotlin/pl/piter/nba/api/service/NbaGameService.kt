package pl.piter.nba.api.service

import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StoreQueryParameters
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore
import org.springframework.kafka.config.StreamsBuilderFactoryBean
import org.springframework.stereotype.Service
import pl.piter.commons.api.model.nba.NbaGame
import pl.piter.commons.api.model.nba.NbaGameMvp
import pl.piter.commons.api.model.scores.GameListResponse
import pl.piter.commons.api.model.scores.GameResponse
import pl.piter.commons.domain.NbaGameEvent
import pl.piter.nba.api.mapper.toNbaGame
import pl.piter.nba.api.mapper.toNbaGameEvent
import pl.piter.nba.api.mapper.toNbaGames
import pl.piter.nba.api.producer.NbaGameProducer
import pl.piter.nba.api.stream.MvpTopology.Companion.MVP_STORE
import pl.piter.nba.api.validation.AnnotationValidator
import java.time.LocalDate

@Service
class NbaGameService(
    private val scoreApiService: ScoreApiService,
    private val streamsBuilder: StreamsBuilderFactoryBean,
    private val validator: AnnotationValidator,
    private val nbaGameProducer: NbaGameProducer
) {

    fun getGameById(id: String): NbaGame {
        val gameResponse: GameResponse = scoreApiService.callGame(id)
        return gameResponse.toNbaGame()
    }

    fun getGameListByTime(gameTime: LocalDate): List<NbaGame> {
        val gameListResponse: GameListResponse = scoreApiService.callGameList(gameTime)
        return gameListResponse.toNbaGames()
    }

    fun getMostValuablePlayer(id: String): NbaGameMvp? {
        return fetchFromStoreBy(id)
    }

    fun evaluateMostValuablePlayer(id: String) {
        if (checkIfMvpExists(id)) return
        val gameResponse: GameResponse = scoreApiService.callGame(id)
        val nbaGameEvent: NbaGameEvent = gameResponse.toNbaGameEvent()

        validator.validate(nbaGameEvent)
        nbaGameProducer.send(nbaGameEvent)
    }

    private fun fetchFromStoreBy(id: String): NbaGameMvp? {
        val kafkaStreams: KafkaStreams = streamsBuilder.kafkaStreams ?: throw RuntimeException("Cannot retrieve Kafka Stream")
        val mvpStore: ReadOnlyKeyValueStore<String, NbaGameMvp> = kafkaStreams.store(
            StoreQueryParameters.fromNameAndType(MVP_STORE, QueryableStoreTypes.keyValueStore())
        )
        return mvpStore[id]
    }

    private fun checkIfMvpExists(id: String) = fetchFromStoreBy(id) != null
}