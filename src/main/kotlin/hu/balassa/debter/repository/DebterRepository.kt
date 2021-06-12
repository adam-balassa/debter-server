package hu.balassa.debter.repository

import hu.balassa.debter.config.DynamoDbConfig
import hu.balassa.debter.model.Room
import hu.balassa.debter.service.RoomService
import hu.balassa.debter.util.generateRoomKey
import hu.balassa.debter.util.generateUUID
import hu.balassa.debter.util.logger
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema

interface DebterRepository {
    fun save(room: Room): Room

    fun deleteByKey(key: String)

    fun findByKey(key: String): Room?
}


@Repository
class RecipeRepositoryImpl(
    db: DynamoDbEnhancedClient
): DebterRepository {
    companion object {
        private val tableSchema = TableSchema.fromBean(Room::class.java)
    }

    private val table by lazy {
        db.table(DynamoDbConfig.tableName, tableSchema)
    }

    private fun findAll(): Set<Room> = table.scan().items().toSet()

    override fun save(room: Room): Room {
        when (room.key) {
            null -> {
                val usedRoomKeys = findAll().map { it.key!! }
                room.key = generateRoomKey(usedRoomKeys)
                table.putItem(room)
            }
            else -> { table.updateItem(room) }
        }
        return room
    }

    override fun deleteByKey(key: String) {
        table.deleteItem(
            Key.builder().partitionValue(key).build()
        )
    }

    override fun findByKey(key: String): Room? =
        table.getItem(
            Key.builder().partitionValue(key).build()
        )
}