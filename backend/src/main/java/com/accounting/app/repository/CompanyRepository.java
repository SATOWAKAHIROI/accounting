package com.accounting.app.repository;

import com.accounting.app.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 会社リポジトリ
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
}
