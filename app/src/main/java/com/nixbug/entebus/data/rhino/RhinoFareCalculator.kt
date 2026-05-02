package com.nixbug.entebus.data.rhino

import com.nixbug.entebus.domain.repository.FareCalculatorRepository
import org.mozilla.javascript.Context
import org.mozilla.javascript.Function
import org.mozilla.javascript.Scriptable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RhinoFareCalculator @Inject constructor() : FareCalculatorRepository {

    private var currentScript: String? = null

    private var scope: Scriptable? = null

    private var fareFunction: Function? = null


    override fun initialize(
        fareFunction: String
    ) {

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
                        "Invalid fare script: expected 'getFare' to be defined as a function."
                    )

            currentScript = fareFunction

            scope = newScope

            this.fareFunction = jsFunction

        } finally {
            Context.exit()
        }
    }


    override fun calculate(
        ticketType: String,
        distanceInMeters: Double,
        extras: Map<String, Any>
    ): Double {

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

            return (result as Number)
                .toDouble()

        } finally {
            Context.exit()
        }
    }
}
