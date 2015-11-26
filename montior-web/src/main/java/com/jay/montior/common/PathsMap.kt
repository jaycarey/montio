package com.jay.montior.common

import java.util.*
import kotlin.reflect.memberProperties

class PathsMap : HashMap<String, Any?>(Paths::class.memberProperties.map { it.name to it.get(Paths) }.toMap())