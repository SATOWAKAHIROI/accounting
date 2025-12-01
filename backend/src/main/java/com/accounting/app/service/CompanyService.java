package com.accounting.app.service;

import com.accounting.app.dto.request.CompanyRequest;
import com.accounting.app.dto.response.CompanyResponse;
import com.accounting.app.entity.Company;
import com.accounting.app.exception.BusinessException;
import com.accounting.app.exception.ResourceNotFoundException;
import com.accounting.app.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 会社管理サービス
 */
@Service
@Transactional
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserCompanyRoleRepository userCompanyRoleRepository;
    private final AccountRepository accountRepository;

    public CompanyService(
            CompanyRepository companyRepository,
            UserCompanyRoleRepository userCompanyRoleRepository,
            AccountRepository accountRepository) {
        this.companyRepository = companyRepository;
        this.userCompanyRoleRepository = userCompanyRoleRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * 全件取得
     */
    @Transactional(readOnly = true)
    public List<CompanyResponse> findAll() {
        List<Company> companies = companyRepository.findAll();
        return companies.stream()
                .map(CompanyResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * ID検索
     */
    @Transactional(readOnly = true)
    public CompanyResponse findById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));
        return CompanyResponse.from(company);
    }

    /**
     * 作成
     */
    public CompanyResponse create(CompanyRequest request) {
        Company company = new Company();
        company.setName(request.getName());
        company.setAddress(request.getAddress());
        company.setPhone(request.getPhone());
        company.setEmail(request.getEmail());
        company.setTaxId(request.getTaxId());

        Company savedCompany = companyRepository.save(company);
        return CompanyResponse.from(savedCompany);
    }

    /**
     * 更新
     */
    public CompanyResponse update(Long id, CompanyRequest request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));

        company.setName(request.getName());
        company.setAddress(request.getAddress());
        company.setPhone(request.getPhone());
        company.setEmail(request.getEmail());
        company.setTaxId(request.getTaxId());

        Company updatedCompany = companyRepository.save(company);
        return CompanyResponse.from(updatedCompany);
    }

    /**
     * 削除
     */
    public void delete(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", id));

        // 会計データが存在するかチェック
        long accountCount = accountRepository.countByCompanyId(id);
        if (accountCount > 0) {
            throw new BusinessException("COMPANY_HAS_DATA",
                    "会計データが存在する会社は削除できません");
        }

        // ユーザーとの紐付けを削除
        userCompanyRoleRepository.deleteByCompanyId(id);

        // 会社を削除
        companyRepository.delete(company);
    }
}
