package com.example

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.booleanLiteral
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
object User: Table("table_name"){
    val name = varchar("name",20)
    val lobbyid = integer("id")
    val lobbystate = bool("statelobby")
}
@Serializable
class UserWrap(val name : String = "", val lobbyid : Int = 0, val lobbstate : Boolean = false)
fun Lobbyfounds(): Int{

    var idcorrectlobby = 0
    var t = mutableListOf<Int>()

    transaction{

         User.selectAll().forEach {
            if (it[User.lobbystate] == false) {

            }
            else{
                t.add(it[User.lobbyid])
                idcorrectlobby = t.first()
            }
        }

//            User.selectAll().forEach {
//                if (it[User.lobbystate] == true) {
//                    idcorrectlobby = UserWrap(lobbyid = it[User.lobbyid])

//
//                }
//
//            }


    }
    return idcorrectlobby
}
