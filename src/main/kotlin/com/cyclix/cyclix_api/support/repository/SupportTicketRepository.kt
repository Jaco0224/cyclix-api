package com.cyclix.cyclix_api.support.repository

import com.cyclix.cyclix_api.support.entity.SupportTicket
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface SupportTicketRepository : JpaRepository<SupportTicket, Long> {
    fun findAllByUserIdOrderByCreatedAtDesc(userId: Long): List<SupportTicket>
    fun findByIdAndUserId(id: Long, userId: Long): Optional<SupportTicket>
    fun findAllByOrderByCreatedAtDesc(): List<SupportTicket>
}
