package com.jay.montior.common

import com.jay.montior.ci.BuildTypeStatus
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

public abstract class View(val fullWidth: Boolean = false) {
    val now = ZonedDateTime.now()
    val nowFormat = now.format(DateTimeFormatter.ISO_INSTANT)
    abstract val envLocation: String
}

public class ExceptionView(
        override val envLocation: String,
        val message: String,
        val stacktrace: List<String>
) : View()

public class OverviewView(
        override val envLocation: String,
        val builds: List<BuildTypeStatus>
) : View()
