package com.accounting.app.service;

import com.accounting.app.dto.request.TaxTypeRequest;
import com.accounting.app.dto.response.TaxTypeResponse;
import com.accounting.app.entity.Company;
import com.accounting.app.entity.TaxType;
import com.accounting.app.exception.BadRequestException;
import com.accounting.app.exception.BusinessException;
import com.accounting.app.exception.ResourceNotFoundException;
import com.accounting.app.repository.TaxTypeRepository;
import com.accounting.app.repository.JournalDetailRepository;
import com.accounting.app.repository.InvoiceDetailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaxTypeService {

    private final TaxTypeRepository taxTypeRepository;
    private final JournalDetailRepository journalDetailRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;

    public TaxTypeService(TaxTypeRepository taxTypeRepository,
                         JournalDetailRepository journalDetailRepository,
                         InvoiceDetailRepository invoiceDetailRepository) {
        this.taxTypeRepository = taxTypeRepository;
        this.journalDetailRepository = journalDetailRepository;
        this.invoiceDetailRepository = invoiceDetailRepository;
    }

    @Transactional(readOnly = true)
    public List<TaxTypeResponse> findAll(Long companyId) {
        return taxTypeRepository.findByCompanyIdOrderByCode(companyId).stream()
                .map(TaxTypeResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaxTypeResponse findById(Long id) {
        TaxType taxType = taxTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaxType", "id", id));
        return TaxTypeResponse.from(taxType);
    }

    public TaxTypeResponse create(Long companyId, TaxTypeRequest request) {
        if (taxTypeRepository.existsByCompanyIdAndCode(companyId, request.getCode())) {
            throw new BadRequestException("税区分コード '" + request.getCode() + "' は既に使用されています");
        }

        if (request.getEffectiveTo() != null &&
            request.getEffectiveFrom().isAfter(request.getEffectiveTo())) {
            throw new BadRequestException("有効開始日は有効終了日より前である必要があります");
        }

        TaxType taxType = new TaxType();
        taxType.setCode(request.getCode());
        taxType.setName(request.getName());
        taxType.setTaxRate(request.getTaxRate());
        taxType.setEffectiveFrom(request.getEffectiveFrom());
        taxType.setEffectiveTo(request.getEffectiveTo());

        Company company = new Company();
        company.setId(companyId);
        taxType.setCompany(company);

        return TaxTypeResponse.from(taxTypeRepository.save(taxType));
    }

    public TaxTypeResponse update(Long id, TaxTypeRequest request) {
        TaxType taxType = taxTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaxType", "id", id));

        if (!taxType.getCode().equals(request.getCode())) {
            if (taxTypeRepository.existsByCompanyIdAndCode(
                    taxType.getCompany().getId(), request.getCode())) {
                throw new BadRequestException("税区分コード '" + request.getCode() + "' は既に使用されています");
            }
        }

        if (request.getEffectiveTo() != null &&
            request.getEffectiveFrom().isAfter(request.getEffectiveTo())) {
            throw new BadRequestException("有効開始日は有効終了日より前である必要があります");
        }

        taxType.setCode(request.getCode());
        taxType.setName(request.getName());
        taxType.setTaxRate(request.getTaxRate());
        taxType.setEffectiveFrom(request.getEffectiveFrom());
        taxType.setEffectiveTo(request.getEffectiveTo());

        return TaxTypeResponse.from(taxTypeRepository.save(taxType));
    }

    public void delete(Long id) {
        TaxType taxType = taxTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaxType", "id", id));

        if (!journalDetailRepository.findByAccountId(id).isEmpty() ||
            !invoiceDetailRepository.findByItemId(id).isEmpty()) {
            throw new BusinessException("TAXTYPE_IN_USE",
                "この税区分は使用されているため削除できません");
        }

        taxTypeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<TaxTypeResponse> findEffectiveTaxTypes(Long companyId, LocalDate date) {
        return taxTypeRepository.findEffectiveTaxTypes(companyId, date).stream()
                .map(TaxTypeResponse::from)
                .collect(Collectors.toList());
    }
}
