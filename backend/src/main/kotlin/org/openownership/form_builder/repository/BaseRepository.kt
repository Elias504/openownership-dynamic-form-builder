package org.openownership.form_builder.repository

import org.openownership.form_builder.model.dao.BaseEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import java.util.UUID

@NoRepositoryBean
interface BaseRepository<E : BaseEntity<*>> : JpaRepository<E, UUID> {
    fun findAllByDeletedAtIsNull(): List<E>

    fun findByIdAndDeletedAtIsNull(id: UUID): E?
}
