package org.openownership.form_builder.model.dao

import jakarta.persistence.Column
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import org.openownership.form_builder.model.dto.BaseDto
import java.io.Serializable
import java.time.Instant
import java.util.UUID

@MappedSuperclass
abstract class BaseEntity<out T : BaseDto> : Serializable {
    @Id
    open var id: UUID = UUID.randomUUID()

    @Column(nullable = false, updatable = false)
    open var createdAt: Instant = Instant.now()

    @Column(nullable = false, updatable = false)
    open var createdBy: String = "system"

    @Column(nullable = false)
    open var updatedAt: Instant = Instant.now()

    @Column(nullable = false)
    open var updatedBy: String = "system"

    open var deletedAt: Instant? = null

    open var deletedBy: String? = null

    @PrePersist
    fun onPrePersist() {
        val now = Instant.now()
        createdAt = now
        updatedAt = now
    }

    @PreUpdate
    fun onPreUpdate() {
        updatedAt = Instant.now()
    }

    abstract fun toDto(): T

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseEntity<*>) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "${this::class.simpleName}(id=$id)"
}
