package rvu.application.relink;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface RelinkUserRepository extends JpaRepository<RelinkUser, Long> {

    RelinkUser findByName(String name);

}
