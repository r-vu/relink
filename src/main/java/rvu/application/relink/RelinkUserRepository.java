package rvu.application.relink;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface RelinkUserRepository extends JpaRepository<RelinkUser, Long> {

    @Deprecated
    RelinkUser findByName(String name);

    @Query("SELECT ru FROM RelinkUser ru WHERE ru.is_oauth IS FALSE " +
    "AND ru.name = :name")
    RelinkUser findByNameLocal(@Param("name") String name);

    @Query("SELECT ru FROM RelinkUser ru WHERE ru.is_oauth IS TRUE " +
    "AND ru.name = :name AND ru.oauth_uuid = :uuid")
    RelinkUser findByNameOAuth(@Param("name") String name, @Param("uuid") int uuid);

}
