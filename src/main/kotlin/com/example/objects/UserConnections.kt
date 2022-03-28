package com.example.objects

import io.ktor.websocket.*
import kotlinx.coroutines.channels.BroadcastChannel

class UserConnections(
    var id:String = "",
    var connection: DefaultWebSocketSession? = null,
    val channel : BroadcastChannel<Frame> = BroadcastChannel<Frame>(1)//Channel<Frame>().broadcast()
){
    suspend fun startBroadcast(){
        connection?.let { connect ->
            for (frame in connect.incoming) {
                println("rhis $this")
                channel.send(frame.copy())

            }
        }
    }
}