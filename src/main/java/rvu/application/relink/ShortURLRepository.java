package rvu.application.relink;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ShortURLRepository extends PagingAndSortingRepository<ShortURL, Long> {

}