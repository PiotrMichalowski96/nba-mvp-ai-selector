package pl.piter.nba.api.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pl.piter.commons.api.model.nba.NbaGame
import pl.piter.commons.api.model.nba.NbaGameMvp
import pl.piter.nba.api.service.NbaGameService
import java.time.LocalDate

@RestController
class NbaGameController(private val nbaGameService: NbaGameService) {

    @GetMapping("/nba-game/{id}")
    fun getGame(@PathVariable id: String): ResponseEntity<NbaGame> {
        require(id.isNotBlank())
        val nbaGame: NbaGame = nbaGameService.getGameById(id)
        return ResponseEntity.ok(nbaGame)
    }

    @GetMapping("/nba-game")
    fun getGameList(
        @RequestParam(required = false, name = "game-time") gameTime: LocalDate?
    ): ResponseEntity<List<NbaGame>> {
        val nbaGames: List<NbaGame> = nbaGameService.getGameListByTime(gameTime ?: LocalDate.now())
        return ResponseEntity.ok(nbaGames)
    }

    @GetMapping("/nba-game/{id}/mvp")
    fun getMostValuablePlayer(@PathVariable id: String): ResponseEntity<NbaGameMvp> {
        val mvp: NbaGameMvp =
            nbaGameService.getMostValuablePlayer(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(mvp)
    }

    @PostMapping("/nba-game/{id}/mvp")
    fun createMVP(@PathVariable id: String): ResponseEntity<Unit> {
        nbaGameService.evaluateMostValuablePlayer(id)
        return ResponseEntity.ok().build()
    }
}