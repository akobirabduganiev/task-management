package tech.nuqta.taskmanagement.role;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.nuqta.taskmanagement.enums.RoleName;


import java.util.Optional;

/**
 * The RoleRepository interface is responsible for querying the "role" table in the database and providing
 * CRUD operations for the Role entity.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
