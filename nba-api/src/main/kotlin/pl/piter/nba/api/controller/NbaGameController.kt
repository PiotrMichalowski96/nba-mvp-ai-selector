package pl.piter.nba.api.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pl.piter.commons.api.model.nba.NbaGame
import pl.piter.nba.api.service.NbaGameService
import java.time.LocalDate

@RestController
class NbaGameController(private val nbaGameService: NbaGameService) {

    @GetMapping("/nba-game/{id}")
    fun getGame(@PathVariable id: String): NbaGame {
        require(id.isNotBlank())
        return nbaGameService.getGameById(id)
    }

    @GetMapping("/nba-game")
    fun getGameList(@RequestParam(required = false, name = "game-time") gameTime: LocalDate?): List<NbaGame> {
        return nbaGameService.getGameListByTime(gameTime ?: LocalDate.now())
    }

    @GetMapping("/nba-game/{id}/mvp")
    fun getMVP() {
        TODO("to implement later")
    }
}