package dev.gabrielsales.accountapi.repository;

import dev.gabrielsales.accountapi.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
}
