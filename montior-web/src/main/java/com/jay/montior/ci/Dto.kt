package com.jay.montior.ci

enum class Status {success, failed, error, unknown }
enum class State {running, finished, unknown}

data class BuildTypeStatus(
        val buildType: BuildType,
        val latestFinished: Build?,
        val latestRunning: Build?,
        val lastTwenty: List<Pair<String, Status>>
)

data class Build(
        val id: String,
        val buildType: BuildType?,
        val status: Status,
        val text: String?,
        val percentageComplete: Int,
        val state: State,
        val webUrl: String?
)

data class BuildType(
        val id: String,
        val name: String,
        val projectId: String,
        val projectName: String,
        val webUrl: String
)