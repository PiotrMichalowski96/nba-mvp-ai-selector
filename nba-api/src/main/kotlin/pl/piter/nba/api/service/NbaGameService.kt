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
import pl.piter.nba.api.mapper.toNbaGame
import pl.piter.nba.api.mapper.toNbaGames
import pl.piter.nba.api.stream.MvpTopology.Companion.MVP_STORE
import java.time.LocalDate

@Service
class NbaGameService(private val scoreApiService: ScoreApiService,
                     private val streamsBuilder: StreamsBuilderFactoryBean) {

    fun getGameById(id: String): NbaGame {
        val gameResponse: GameResponse = scoreApiService.callGame(id)
        return gameResponse.toNbaGame()
    }

    fun getGameListByTime(gameTime: LocalDate): List<NbaGame> {
        val gameListResponse: GameListResponse = scoreApiService.callGameList(gameTime)
        return gameListResponse.toNbaGames()
    }

    fun getMostValuablePlayer(id: String): NbaGameMvp? {
        val kafkaStreams: KafkaStreams = streamsBuilder.kafkaStreams ?: throw RuntimeException("Cannot retrieve Kafka Stream")
        val mvpStore: ReadOnlyKeyValueStore<String, NbaGameMvp> = kafkaStreams.store(
            StoreQueryParameters.fromNameAndType(MVP_STORE, QueryableStoreTypes.keyValueStore())
        )
        return mvpStore.get(id)
    }

    fun evaluateMostValuablePlayer(id: String) {

        // fetch data from kafka store

        // validate if MVP was already evaluated

        // fetch game from external API

        // validate GameResponse

        // produce NBA Game Event

        TODO("Not yet implemented")
    }
}