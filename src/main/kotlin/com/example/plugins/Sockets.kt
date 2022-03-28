package com.example.plugins

import com.example.objects.UserConnections
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import java.util.*

var listsOfChannels = Collections.synchronizedMap<Int, BroadcastChannel<Frame>>(mutableMapOf())
var mutableSet = Collections.synchronizedList<UserConnections>(mutableListOf())
fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {

        webSocket("/passenger/{idpassenger}") {
            val idpassenger = call.parameters["idpassenger"].toString()
            if (idpassenger == null) close()

            launch {
                var driver = mutableSet.find { idpassenger == it.id } ?: UserConnections()


                println("driver $driver")
                driver?.let {
                    it.channel.consumeEach { frame ->
                        send(frame.copy())
                        println("frame.copy()  ${frame.copy()}")
                    }
                }

            }.join()

        }
        webSocket("/driver/{idDriver}") {
            val idDriver = call.parameters["idDriver"]
            if (idDriver == null) close(CloseReason(CloseReason.Codes.NORMAL, "Driver is null"))
            else {
//            val broadcastChannel = Channel<Frame>()
//            listsOfChannels.put(idDriver, broadcastChannel.broadcast())
                var driver = mutableSet.find { idDriver == it.id }
                if (driver != null) {
                    driver.connection = this
                } else {
                    driver = UserConnections(idDriver, this)
                    driver.connection = this
                    mutableSet.add(driver)
                }
                driver.startBroadcast()
            }
        }
    }
}
