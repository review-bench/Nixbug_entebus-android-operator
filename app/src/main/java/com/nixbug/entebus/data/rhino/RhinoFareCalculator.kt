package com.nixbug.entebus.data.rhino

import com.nixbug.entebus.domain.repository.FareCalculatorRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.mozilla.javascript.Context
import org.mozilla.javascript.Function
import org.mozilla.javascript.Scriptable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RhinoFareCalculator @Inject constructor() : FareCalculatorRepository {

    private val mutex = Mutex()

    private var currentScript: String? = null

    private var scope: Scriptable? = null

    private var fareFunction: Function? = null


    override suspend fun initialize(
        fareFunction: String
    ) {

        mutex.withLock {

            if (fareFunction == currentScript) {
                return
            }

            val context = Context.enter()

            try {

                context.optimizationLevel = -1

                val newScope =
                    context.initStandardObjects()

                context.evaluateString(
                    newScope,
                    fareFunction,
                    "fare.js",
                    1,
                    null
                )

                val jsFunction =
                    newScope.get(
                        "getFare",
                        newScope
                    ) as? Function
                        ?: throw IllegalArgumentException(
                            "Invalid fare script"
                        )

                currentScript = fareFunction

                scope = newScope

                this.fareFunction = jsFunction

            } finally {
                Context.exit()
            }
        }
    }


    override suspend fun calculate(
        ticketType: String,
        distanceInMeters: Double,
        extras: Map<String, Any>
    ): Double {

        return mutex.withLock {

            val context = Context.enter()

            try {

                context.optimizationLevel = -1

                val result =
                    fareFunction?.call(
                        context,
                        scope,
                        scope,
                        arrayOf(
                            ticketType,
                            distanceInMeters,
                            extras
                        )
                    )

                (result as Number)
                    .toDouble()

            } finally {
                Context.exit()
            }
        }
    }
}