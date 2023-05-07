package pl.piter.nba.api.mapper

import pl.piter.commons.api.model.nba.NbaGame
import pl.piter.commons.api.model.scores.*
import pl.piter.commons.domain.GameResult
import pl.piter.commons.domain.NbaGameEvent
import pl.piter.commons.domain.Player
import pl.piter.commons.domain.Stats

fun GameResponse.toNbaGame() = NbaGame(
    id = id,
    homeTeam = "${home.market} ${home.name}",
    awayTeam = "${away.market} ${away.name}",
    startTime = scheduled,
    gameResult = GameResult(home.points, away.points)
)

fun Game.toNbaGame() = NbaGame(
    id = id,
    homeTeam = home.name,
    awayTeam = away.name,
    startTime = scheduled,
    gameResult = GameResult(homePoints, awayPoints)
)

fun GameListResponse.toNbaGames(): List<NbaGame> = games
    .map { it.toNbaGame() }
    .toList()

fun GameResponse.toNbaGameEvent() = NbaGameEvent(
    gameId = id,
    homeTeam = "${home.market} ${home.name}",
    awayTeam = "${away.market} ${away.name}",
    startTime = scheduled,
    gameResult = GameResult(home.points, away.points),
    bestPlayers = mapPlayers(home) + mapPlayers(away)
)

private fun mapPlayers(team: Team): Set<Player> =
    mapPlayers(team.name, team.leaders.points, team.leaders.assists, team.leaders.rebounds)

private fun mapPlayers(team: String, vararg players: List<PlayerGame>): Set<Player> =
    players.reduce { list1, list2 -> list1 + list2 }
        .map { p -> p.toPlayer(team) }
        .toSet()

private fun PlayerGame.toPlayer(team: String) = Player(
    name = fullName,
    team = team,
    stats = statistics.toStats()
)

private fun Statistics.toStats() = Stats(
    points = points,
    assists = assists,
    rebounds = rebounds,
    steals = steals,
    blocks = blocks
)
