/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.logging

class PrintlnLogger(val name: String) : Logger {
    override fun error(e: Throwable, message: () -> String) {
        println("ERR [$name] : ${message()}")
    }

    override fun info(message: () -> String) {
        println("INFO [$name] : ${message()}")
    }
}