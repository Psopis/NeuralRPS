package com.example.plugins

import com.example.Lobbyfounds
import com.example.objects.UserConnections
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import io.ktor.server.application.*
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
            if(Lobbyfounds() != 0){
        webSocket("/userconnect/${Lobbyfounds()}") {
            val idlobby= call.parameters["idlobby"].toString()
            if (idlobby == null) close()

            launch {
                var lobby = mutableSet.find { idlobby == it.id } ?: UserConnections()


                println("lobby++ $lobby")
                lobby?.let {
                    it.channel.consumeEach { frame ->
                        send(frame.copy())
                        println("frame.copy()  ${frame.copy()}")
                    }
                }

            }.join()

        }}
        else{
        webSocket("/lobby/{idlobby}") {
            val idLobby = call.parameters["idlobby"]
            if (idLobby == null) close(CloseReason(CloseReason.Codes.NORMAL, "lobby is null"))
            else {
//            val broadcastChannel = Channel<Frame>()
//            listsOfChannels.put(idDriver, broadcastChannel.broadcast())

                var lobby = mutableSet.find { idLobby == it.id }
                if (lobby != null) {
                    lobby.connection = this
                } else {
                    lobby = UserConnections(idLobby, this)
                    lobby.connection = this
                    mutableSet.add(lobby)
                }
                lobby.startBroadcast()
            }
        }
        }
    }
}
