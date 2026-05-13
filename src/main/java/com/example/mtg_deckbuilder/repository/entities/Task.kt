package com.example.mtg_deckbuilder.repository.entities
// ...
import com.example.mtg_deckbuilder.repository.tables.Tasks
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.dao.java.UUIDEntity
import org.jetbrains.exposed.v1.dao.java.UUIDEntityClass
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi


// ...

class Task(id: EntityID<UUID>) : UUIDEntity(id) {
   companion object : UUIDEntityClass<Task>(Tasks)

    @OptIn(ExperimentalUuidApi::class)
    var title by Tasks.title
    var description by Tasks.description
    var isCompleted by Tasks.isCompleted

    @OptIn(ExperimentalUuidApi::class)
    override fun toString(): String {
        return "Task(id=$id, title=$title, completed=$isCompleted)"
    }
}