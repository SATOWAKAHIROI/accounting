package com.accounting.app.service;

import com.accounting.app.dto.request.PartnerRequest;
import com.accounting.app.dto.response.PartnerResponse;
import com.accounting.app.entity.Company;
import com.accounting.app.entity.Partner;
import com.accounting.app.entity.Partner.PartnerType;
import com.accounting.app.exception.BadRequestException;
import com.accounting.app.exception.BusinessException;
import com.accounting.app.exception.ResourceNotFoundException;
import com.accounting.app.repository.PartnerRepository;
import com.accounting.app.repository.JournalDetailRepository;
import com.accounting.app.repository.InvoiceRepository;
import com.accounting.app.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PartnerService {

    private final PartnerRepository partnerRepository;
    private final JournalDetailRepository journalDetailRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    public PartnerService(PartnerRepository partnerRepository,
                         JournalDetailRepository journalDetailRepository,
                         InvoiceRepository invoiceRepository,
                         PaymentRepository paymentRepository) {
        this.partnerRepository = partnerRepository;
        this.journalDetailRepository = journalDetailRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true)
    public List<PartnerResponse> findAll(Long companyId) {
        return partnerRepository.findByCompanyIdOrderByCode(companyId).stream()
                .map(PartnerResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PartnerResponse findById(Long id) {
        Partner partner = partnerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partner", "id", id));
        return PartnerResponse.from(partner);
    }

    public PartnerResponse create(Long companyId, PartnerRequest request) {
        if (partnerRepository.existsByCompanyIdAndCode(companyId, request.getCode())) {
            throw new BadRequestException("取引先コード '" + request.getCode() + "' は既に使用されています");
        }

        Partner partner = new Partner();
        partner.setCode(request.getCode());
        partner.setName(request.getName());
        partner.setPostalCode(request.getPostalCode());
        partner.setAddress(request.getAddress());
        partner.setPhone(request.getPhone());
        partner.setEmail(request.getEmail());
        partner.setPartnerType(PartnerType.valueOf(request.getPartnerType()));

        Company company = new Company();
        company.setId(companyId);
        partner.setCompany(company);

        return PartnerResponse.from(partnerRepository.save(partner));
    }

    public PartnerResponse update(Long id, PartnerRequest request) {
        Partner partner = partnerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partner", "id", id));

        if (!partner.getCode().equals(request.getCode())) {
            if (partnerRepository.existsByCompanyIdAndCode(
                    partner.getCompany().getId(), request.getCode())) {
                throw new BadRequestException("取引先コード '" + request.getCode() + "' は既に使用されています");
            }
        }

        partner.setCode(request.getCode());
        partner.setName(request.getName());
        partner.setPostalCode(request.getPostalCode());
        partner.setAddress(request.getAddress());
        partner.setPhone(request.getPhone());
        partner.setEmail(request.getEmail());
        partner.setPartnerType(PartnerType.valueOf(request.getPartnerType()));

        return PartnerResponse.from(partnerRepository.save(partner));
    }

    public void delete(Long id) {
        Partner partner = partnerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Partner", "id", id));

        if (!journalDetailRepository.findByAccountId(id).isEmpty() ||
            !invoiceRepository.findByCompanyIdAndPartnerIdOrderByInvoiceDateDesc(
                    partner.getCompany().getId(), id).isEmpty() ||
            !paymentRepository.findByCompanyIdAndPartnerIdOrderByPaymentDateDesc(
                    partner.getCompany().getId(), id).isEmpty()) {
            throw new BusinessException("PARTNER_IN_USE",
                "この取引先は使用されているため削除できません");
        }

        partnerRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<PartnerResponse> findByPartnerType(Long companyId, PartnerType type) {
        return partnerRepository.findByCompanyIdAndPartnerTypeOrderByCode(companyId, type).stream()
                .map(PartnerResponse::from)
                .collect(Collectors.toList());
    }
}
