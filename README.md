# NBA MVP AI selector microservices
[![<PiotrMichalowski96>](https://circleci.com/gh/PiotrMichalowski96/nba-mvp-ai-selector.svg?style=svg)](https://circleci.com/gh/PiotrMichalowski96/nba-mvp-ai-selector)

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=PiotrMichalowski96_nba-mvp-ai-selector&metric=bugs)](https://sonarcloud.io/dashboard?id=PiotrMichalowski96_nba-mvp-ai-selector)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=PiotrMichalowski96_nba-mvp-ai-selector&metric=coverage)](https://sonarcloud.io/dashboard?id=PiotrMichalowski96_nba-mvp-ai-selector)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=PiotrMichalowski96_nba-mvp-ai-selector&metric=ncloc)](https://sonarcloud.io/dashboard?id=PiotrMichalowski96_nba-mvp-ai-selector)


Artificial Intelligence is selecting MVP (most-valuable-player) in NBA games. Two microservices that are used for fetching NBA matches statistics from external provider. Then ChatGPT is called to select MVP.

![alt text](https://github.com/PiotrMichalowski96/nba-mvp-ai-selector/blob/main/doc/nba-mvp-ai-selector.png?raw=true)

## Project Overview
Technology stack:
- Kotlin
- Spring Boot
- Apache Kafka
- Redis
- REST API (with HATEOAS support)

Testing:
- Embedded Kafka Tests (integration tests)
- Topology Test Driver (Kafka Stream unit tests)
- JUnit 5

## Setup
Start Redis and Kafka cluster using Docker (doc/docker-compose.yml):
```
$ docker compose up -d
```
Then start microservices: NbaApi and MvpSelector

You can check services using postman collection from doc directory.