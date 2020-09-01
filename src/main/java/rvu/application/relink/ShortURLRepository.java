package rvu.application.relink;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShortURLRepository extends JpaRepository<ShortURL, Long> {
    
    ShortURL findByName(String name);
}