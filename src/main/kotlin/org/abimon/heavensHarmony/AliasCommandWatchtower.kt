package org.abimon.heavensHarmony

import org.abimon.dArmada.MessageOrder
import org.abimon.dArmada.ServerMessageOrder
import org.abimon.imperator.handle.Order
import org.abimon.imperator.handle.Watchtower
import org.json.JSONException
import java.util.*

open class AliasCommandWatchtower(val command: String, val checkOutputs: (Map<String, String>) -> Boolean = { true }): Watchtower {
    override fun allow(order: Order): Boolean {
        if (order !is MessageOrder)
            return false
        val outputs = getOutputs(order)
        if (outputs != null)
            return checkOutputs.invoke(outputs)
        return false
    }

    fun getOutputs(order: MessageOrder): Map<String, String>? {
        if(order is ServerMessageOrder) {
            order.prefix = order.server.aliasPrefix
            order.params = order.getParams()
        }

        try {
            val msg = order.msg.content ?: ""
            if (msg.startsWith("${order.prefix}$command")) {
                val hash = HashMap<String, String>()
                val params = order.params
                for (i in 0 until params.size)
                    hash["#param$i"] = params[i]
                return hash
            } else if (msg.startsWith("${order.client.ourUser.mention(true)} $command")) {
                val hash = HashMap<String, String>()
                val params = order.params
                for (i in 0 until params.size - 1)
                    hash["#param$i"] = params[i + 1]
                return hash
            } else if (msg.startsWith("${order.client.ourUser.mention(false)} $command")) {
                val hash = HashMap<String, String>()
                val params = order.params
                for (i in 0 until params.size - 1)
                    hash["#param$i"] = params[i + 1]
                return hash
            }

            if(order is ServerMessageOrder)
                paths@ for (alias in order.server.getAliasPaths(command)) {
                    val path = alias["alias"] as? String ?: "%prefix$command"
                    for (aliasPath in arrayOf(path.replace("%prefix", order.prefix),
                            path.replace("%prefix", order.client.ourUser.mention(true)),
                            path.replace("%prefix", order.client.ourUser.mention(false)))) {
                        var aliasIndex = 0
                        var msgIndex = 0

                        var output = ""
                        var msgOutput = ""
                        var inGroup = false
                        val outputs = HashMap<String, String>()

                        for (dataKey in arrayOf("data", "params", "values")) { //Few options
                            if (dataKey in alias) {
                                val data = alias[dataKey]
                                when (data) {
                                    is Map<*, *> -> data.keys.filter { it is String }.map { key -> outputs[key as String] = data[key].toString() }
                                    is Array<*> -> data.forEachIndexed { index, value -> outputs["#param${index + 1}"] = value.toString() }
                                    else -> outputs["#param1"] = data.toString()
                                }
                            }
                        }

                        for (i in 0 until aliasPath.length) {
                            if (output.isNotBlank()) {
                                val aliasChar = aliasPath[aliasIndex]
                                if (aliasChar == '#') { //End the search
                                    if (aliasPath.length - 1 == aliasIndex) {
                                        while (msgIndex < msg.length) {
                                            val msgChar = msg[msgIndex++]
                                            if (msgChar == '"')
                                                inGroup = !inGroup
                                            else if (msgChar == '\\' && msgIndex + 1 < msg.length)
                                                msgOutput += msg[++msgIndex]
                                            else
                                                msgOutput += msgChar
                                        }

                                        break
                                    } else {
                                        val aliasBreak = aliasPath[++aliasIndex]
                                        var msgChar = msg[msgIndex]
                                        while ((msgChar != aliasBreak || inGroup) && msgIndex + 1 < msg.length) {
                                            if (msgChar == '"')
                                                inGroup = !inGroup
                                            else if (msgChar == '\\' && msgIndex + 1 < msg.length)
                                                msgOutput += msg[++msgIndex]
                                            else
                                                msgOutput += msgChar
                                            msgChar = msg[++msgIndex]
                                        }
                                        outputs[output] = msgOutput

                                        output = ""
                                        msgOutput = ""
                                        msgIndex++
                                    }
                                } else {
                                    output += aliasChar
                                }

                                aliasIndex++
                            } else if (msgIndex < msg.length && aliasIndex < aliasPath.length) {
                                val aliasChar = aliasPath[aliasIndex]
                                val msgChar = msg[msgIndex]

                                //println("[$aliasChar|$msgChar]")

                                if (aliasChar == '\\') {
                                    if (aliasPath[++aliasIndex] != msg[++msgIndex])
                                        continue@paths
                                } else if (aliasChar == '#') {
                                    output += aliasChar
                                    if (msgChar == '"')
                                        inGroup = !inGroup
                                    else if (msgChar == '\\' && msgIndex + 1 < msg.length)
                                        msgOutput += msg[++msgIndex]
                                    else
                                        msgOutput += msgChar
                                } else if (aliasChar != msgChar)
                                    continue@paths

                                aliasIndex++
                                msgIndex++
                            } else if (aliasIndex >= aliasPath.length && msgIndex >= msg.length)
                                break
                            else
                                continue@paths
                        }

                        if (output.isNotBlank() && msgOutput.isNotBlank())
                            outputs[output] = msgOutput

                        //                    val msgParams = order.params
                        //                    for (i in 0 until msgParams.size)
                        //                        outputs.putIfAbsent("#param$i", msgParams[i])

                        return outputs.mapValues { (_, value) ->
                            value.replace("%prefix", order.server.aliasPrefix)
                                    .replace("%author_name", order.author.name)
                                    .replace("%author_id", order.author.stringID)
                                    .replace("%bot_name", order.client.ourUser.name)
                                    .replace("%bot_id", order.client.ourUser.stringID)
                        }
                    }
                }
        } catch(cast: ClassCastException) {
        } //User error
        catch(json: JSONException) { //Likely user error, just in case
            json.printStackTrace()
        }

        return null
    }

    override fun getName(): String = command
}