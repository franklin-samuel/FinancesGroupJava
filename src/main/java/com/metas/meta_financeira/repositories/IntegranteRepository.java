package com.metas.meta_financeira.repositories;

import com.metas.meta_financeira.models.Integrante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntegranteRepository extends JpaRepository<Integrante, Long> {
}