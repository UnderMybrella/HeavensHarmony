package org.abimon.heavensHarmony

import org.abimon.dArmada.ServerMessageOrder
import org.abimon.dArmada.serverData
import org.abimon.heavensHarmony.permissions.IHeavenPermission
import org.abimon.imperator.handle.Order
import org.abimon.imperator.handle.Watchtower
import org.abimon.imperator.impl.InstanceSoldier
import java.util.*

open class HeavenServerCommand(name: String, command: String, val permission: IHeavenPermission, watchtowers: ArrayList<Watchtower> = arrayListOf(), val aliasCommandWatchtower: AliasCommandWatchtower = AliasCommandWatchtower(command), val commandAction: ServerMessageOrder.(Map<String, String>) -> Unit) :
        InstanceSoldier<ServerMessageOrder>(ServerMessageOrder::class.java, name, watchtowers.apply { add(aliasCommandWatchtower) }, { order -> order.commandAction(mapOf()) }) {

    override fun command(order: Order) {
        if (order !is ServerMessageOrder)
            return

        if (!permission.allow(order)) {
            permission.onPermissionDenied(order.author, order.channel, order.server.serverData)
            return
        }

        aliasCommandWatchtower.getOutputs(order)?.also { outputs ->
            val params = outputs.filter { output -> output.key.startsWith("#param") }.mapKeys { it.key.substring(6).toInt() }
            if (params.isNotEmpty()) {
                val maxParam = params.keys.max()!! + 1

                order.params = Array(maxParam, { "" })
                params.forEach { order.params[it.key] = it.value }
            }
            else
                order.params = arrayOf(order.content)

            try {
                order.commandAction(outputs)
            } catch(userError: UserInputError) {
                buffer { order.channel.sendMessage(userError.content, userError.embed) }
            } catch(th: Throwable) {
                th.printStackTrace()
            }
        }
    }
}
