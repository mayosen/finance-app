package com.mayosen.financeapp.snapshot.jdbc

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountSnapshotEntityRepository : CrudRepository<AccountSnapshotEntity, String>
