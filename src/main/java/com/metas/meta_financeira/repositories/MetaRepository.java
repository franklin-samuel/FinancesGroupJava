package com.metas.meta_financeira.repositories;

import com.metas.meta_financeira.models.Meta;
import com.metas.meta_financeira.models.StatusMeta;
import com.metas.meta_financeira.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface MetaRepository extends JpaRepository<Meta, Long> {
    List<Meta> findByOwner(User owner);
    List<Meta> findByOwnerAndStatus(User owner, StatusMeta status);
    List<Meta> findByOwnerAndStatusIn(User owner, Collection<StatusMeta> statuses);
}
