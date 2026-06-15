package dev.gabrielsales.accountapi.repository;

import dev.gabrielsales.accountapi.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TransferRepository extends JpaRepository<Transfer, UUID> {
}