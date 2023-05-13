package pl.piter.nba.api.controller

import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.Link
import org.springframework.hateoas.config.EnableHypermediaSupport
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pl.piter.commons.api.model.nba.NbaGame
import pl.piter.commons.api.model.nba.NbaGameMvp
import pl.piter.nba.api.service.NbaGameService
import java.time.LocalDate

@RestController
@RequestMapping("/nba-game")
@EnableHypermediaSupport(type = [HypermediaType.HAL])
class NbaGameController(private val nbaGameService: NbaGameService) {

    @GetMapping("/{id}")
    fun getGame(@PathVariable id: String): ResponseEntity<NbaGame> {
        require(id.isNotBlank())
        val nbaGame: NbaGame = nbaGameService.getGameById(id)
        addLinksForNbaGame(nbaGame)
        return ResponseEntity.ok(nbaGame)
    }

    @GetMapping
    fun getGameList(
        @RequestParam(required = false, name = "game-time") gameTime: LocalDate?
    ): ResponseEntity<CollectionModel<NbaGame>> {
        val nbaGames: List<NbaGame> = nbaGameService.getGameListByTime(gameTime ?: LocalDate.now())
        val nbaGamesCollectionModel = addLinksForNbaGameList(nbaGames, gameTime)
        return ResponseEntity.ok(nbaGamesCollectionModel)
    }

    @GetMapping("/{id}/mvp")
    fun getMostValuablePlayer(@PathVariable id: String): ResponseEntity<NbaGameMvp> {
        val mvp: NbaGameMvp = nbaGameService.getMostValuablePlayer(id)
            ?: return ResponseEntity.notFound().build()
        addLinkForMvp(id, mvp)
        return ResponseEntity.ok(mvp)
    }

    @PostMapping("/{id}/mvp")
    fun createMVP(@PathVariable id: String): ResponseEntity<Unit> {
        nbaGameService.evaluateMostValuablePlayer(id)
        return ResponseEntity.ok().build()
    }

    private fun addLinksForNbaGame(nbaGame: NbaGame) {
        val id: String = nbaGame.id
        val selfLink: Link = linkTo<NbaGameController> { getGame(id) }.withSelfRel()
        val mvpLink: Link = linkTo<NbaGameController> { getMostValuablePlayer(id) }.withRel("mvp")
        nbaGame.add(selfLink, mvpLink)
    }

    private fun addLinksForNbaGameList(
        nbaGames: List<NbaGame>,
        gameTime: LocalDate?
    ): CollectionModel<NbaGame> {
        nbaGames.forEach { addLinksForNbaGame(it) }
        val time: LocalDate = gameTime ?: LocalDate.now()
        val gamesLink: Link = linkTo<NbaGameController> { getGameList(time) }.withSelfRel()
        return CollectionModel.of(nbaGames, gamesLink)
    }

    private fun addLinkForMvp(id: String, mvp: NbaGameMvp) {
        val selfLink: Link = linkTo<NbaGameController> { getMostValuablePlayer(id) }.withSelfRel()
        mvp.add(selfLink)
    }
}