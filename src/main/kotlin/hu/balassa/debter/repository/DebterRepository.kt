package hu.balassa.debter.repository

import hu.balassa.debter.config.DynamoDbConfig
import hu.balassa.debter.model.Room
import hu.balassa.debter.util.generateUUID
import org.springframework.stereotype.Repository
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema

interface DebterRepository {
    fun findAll(): Set<Room>

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

    override fun findAll(): Set<Room> = table.scan().items().toSet()


    override fun save(room: Room): Room {
        when (room.id) {
            null -> {
                room.id = generateUUID()
                table.putItem(room)
            }
            else -> { table.updateItem(room) }
        }
        return room
    }

    override fun deleteByKey(key: String) {
        table.deleteItem(
            Key.builder().sortValue(key).build()
        )
    }

    override fun findByKey(key: String): Room? =
        table.getItem(
            Key.builder().sortValue(key).build()
        )
}