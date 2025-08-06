package com.mayosen.financeapp.command.api.admin

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@RequestMapping("/admin-api/v1")
interface AdminApi {
    @PostMapping("/replay")
    fun replayEvents(
        @RequestBody request: ReplayEventsRequest,
    ): ResponseEntity<AdminCommandResponse>
}
