package rvu.application.relink;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;

public interface ShortURLRepository extends JpaRepository<ShortURL, Long> {

    static final String findAllWithNullAndAdminOverride = 
        "SELECT s from ShortURL s " +
        "LEFT JOIN s.owner owner WHERE (1=?#{hasRole('ROLE_ADMIN') ? 1 : " +
        "0} OR (owner IS NOT NULL AND owner.name = ?#{authentication?.name}))";

    @Override
    @Query(findAllWithNullAndAdminOverride)
    Page<ShortURL> findAll(Pageable pageable);

    ShortURL findByName(String name);

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or @shortURLRepository.findById(#id)?.owner?.name == authentication?.name")
    void deleteById(@Param("id") Long id);

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN') or #shortURL?.owner?.name == authentication?.name")
    void delete(@Param("shortURL") ShortURL shortURL);
}
