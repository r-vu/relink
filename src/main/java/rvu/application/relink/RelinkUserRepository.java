package rvu.application.relink;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface RelinkUserRepository extends JpaRepository<RelinkUser, Long> {

    @Deprecated
    RelinkUser findByName(String name);

    @Query("SELECT ru FROM RelinkUser ru WHERE ru.isOAuth IS FALSE " +
    "AND ru.name = :name")
    RelinkUser findByNameLocal(@Param("name") String name);

    @Query("SELECT ru FROM RelinkUser ru WHERE ru.isOAuth IS TRUE " +
    "AND ru.name = :name AND ru.oAuthProvider = :provider")
    RelinkUser findByNameOAuth(@Param("name") String name, @Param("provider") int provider);

}
