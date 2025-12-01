package com.accounting.app.service;

import com.accounting.app.dto.request.FiscalPeriodRequest;
import com.accounting.app.dto.response.FiscalPeriodResponse;
import com.accounting.app.entity.Company;
import com.accounting.app.entity.FiscalPeriod;
import com.accounting.app.exception.BadRequestException;
import com.accounting.app.exception.BusinessException;
import com.accounting.app.exception.ResourceNotFoundException;
import com.accounting.app.repository.CompanyRepository;
import com.accounting.app.repository.FiscalPeriodRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 会計期間管理サービス
 */
@Service
@Transactional
public class FiscalPeriodService {

    private final FiscalPeriodRepository fiscalPeriodRepository;
    private final CompanyRepository companyRepository;

    public FiscalPeriodService(
            FiscalPeriodRepository fiscalPeriodRepository,
            CompanyRepository companyRepository) {
        this.fiscalPeriodRepository = fiscalPeriodRepository;
        this.companyRepository = companyRepository;
    }

    /**
     * 全件取得
     */
    @Transactional(readOnly = true)
    public List<FiscalPeriodResponse> findAll(Long companyId) {
        List<FiscalPeriod> periods = fiscalPeriodRepository.findByCompanyIdOrderByStartDate(companyId);
        return periods.stream()
                .map(FiscalPeriodResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * ID検索
     */
    @Transactional(readOnly = true)
    public FiscalPeriodResponse findById(Long id) {
        FiscalPeriod period = fiscalPeriodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FiscalPeriod", "id", id));
        return FiscalPeriodResponse.from(period);
    }

    /**
     * 作成
     */
    public FiscalPeriodResponse create(Long companyId, FiscalPeriodRequest request) {
        // 会社の取得
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", companyId));

        // 日付の妥当性チェック
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("終了日は開始日以降である必要があります");
        }

        // 期間の重複チェック
        List<FiscalPeriod> overlappingPeriods = fiscalPeriodRepository
                .findByCompanyIdAndDateRangeOverlapping(
                        companyId,
                        request.getStartDate(),
                        request.getEndDate()
                );

        if (!overlappingPeriods.isEmpty()) {
            throw new BusinessException("PERIOD_OVERLAP",
                    "指定された期間は既存の会計期間と重複しています");
        }

        // エンティティの作成
        FiscalPeriod period = new FiscalPeriod();
        period.setCompany(company);
        period.setPeriodYear(request.getPeriodYear());
        period.setPeriodNumber(request.getPeriodNumber());
        period.setPeriodName(request.getPeriodName());
        period.setStartDate(request.getStartDate());
        period.setEndDate(request.getEndDate());
        period.setIsClosed(false);

        // 保存
        FiscalPeriod savedPeriod = fiscalPeriodRepository.save(period);
        return FiscalPeriodResponse.from(savedPeriod);
    }

    /**
     * 更新
     */
    public FiscalPeriodResponse update(Long id, FiscalPeriodRequest request) {
        FiscalPeriod period = fiscalPeriodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FiscalPeriod", "id", id));

        // 締められた期間は更新不可
        if (period.getIsClosed()) {
            throw new BusinessException("PERIOD_CLOSED",
                    "締められた会計期間は更新できません");
        }

        // 日付の妥当性チェック
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("終了日は開始日以降である必要があります");
        }

        // 期間の重複チェック（自身を除く）
        List<FiscalPeriod> overlappingPeriods = fiscalPeriodRepository
                .findByCompanyIdAndDateRangeOverlapping(
                        period.getCompany().getId(),
                        request.getStartDate(),
                        request.getEndDate()
                );

        overlappingPeriods = overlappingPeriods.stream()
                .filter(p -> !p.getId().equals(id))
                .collect(Collectors.toList());

        if (!overlappingPeriods.isEmpty()) {
            throw new BusinessException("PERIOD_OVERLAP",
                    "指定された期間は既存の会計期間と重複しています");
        }

        // 更新
        period.setPeriodYear(request.getPeriodYear());
        period.setPeriodNumber(request.getPeriodNumber());
        period.setPeriodName(request.getPeriodName());
        period.setStartDate(request.getStartDate());
        period.setEndDate(request.getEndDate());

        FiscalPeriod updatedPeriod = fiscalPeriodRepository.save(period);
        return FiscalPeriodResponse.from(updatedPeriod);
    }

    /**
     * 期間締め
     */
    public FiscalPeriodResponse closePeriod(Long id) {
        FiscalPeriod period = fiscalPeriodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FiscalPeriod", "id", id));

        // 既に締められている場合
        if (period.getIsClosed()) {
            throw new BusinessException("PERIOD_ALREADY_CLOSED",
                    "この会計期間は既に締められています");
        }

        // 期間を締める
        period.setIsClosed(true);
        FiscalPeriod closedPeriod = fiscalPeriodRepository.save(period);
        return FiscalPeriodResponse.from(closedPeriod);
    }

    /**
     * 期間再開
     */
    public FiscalPeriodResponse reopenPeriod(Long id) {
        FiscalPeriod period = fiscalPeriodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FiscalPeriod", "id", id));

        // 締められていない場合
        if (!period.getIsClosed()) {
            throw new BusinessException("PERIOD_NOT_CLOSED",
                    "この会計期間は締められていません");
        }

        // 期間を再開
        period.setIsClosed(false);
        FiscalPeriod reopenedPeriod = fiscalPeriodRepository.save(period);
        return FiscalPeriodResponse.from(reopenedPeriod);
    }
}
