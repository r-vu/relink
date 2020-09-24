package rvu.application.relink;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ShortURLRepository extends JpaRepository<ShortURL, Long> {

    ShortURL findByName(String name);

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or @shortURLRepository.findById(#id)?.owner?.name == authentication?.name")
    void deleteById(@Param("id") Long id);

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or #shortURL?.owner?.name == authentication?.name")
    void delete(@Param("shortURL") ShortURL shortURL);
}
