package com.accounting.app.service;

import com.accounting.app.dto.request.ItemRequest;
import com.accounting.app.dto.response.ItemResponse;
import com.accounting.app.entity.Company;
import com.accounting.app.entity.Item;
import com.accounting.app.exception.BadRequestException;
import com.accounting.app.exception.BusinessException;
import com.accounting.app.exception.ResourceNotFoundException;
import com.accounting.app.repository.ItemRepository;
import com.accounting.app.repository.InvoiceDetailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;

    public ItemService(ItemRepository itemRepository,
                      InvoiceDetailRepository invoiceDetailRepository) {
        this.itemRepository = itemRepository;
        this.invoiceDetailRepository = invoiceDetailRepository;
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> findAll(Long companyId) {
        return itemRepository.findByCompanyIdOrderByCode(companyId).stream()
                .map(ItemResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ItemResponse findById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", "id", id));
        return ItemResponse.from(item);
    }

    public ItemResponse create(Long companyId, ItemRequest request) {
        if (itemRepository.existsByCompanyIdAndCode(companyId, request.getCode())) {
            throw new BadRequestException("品目コード '" + request.getCode() + "' は既に使用されています");
        }

        Item item = new Item();
        item.setCode(request.getCode());
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setUnitPrice(request.getUnitPrice());
        item.setUnit(request.getUnit());

        Company company = new Company();
        company.setId(companyId);
        item.setCompany(company);

        return ItemResponse.from(itemRepository.save(item));
    }

    public ItemResponse update(Long id, ItemRequest request) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", "id", id));

        if (!item.getCode().equals(request.getCode())) {
            if (itemRepository.existsByCompanyIdAndCode(
                    item.getCompany().getId(), request.getCode())) {
                throw new BadRequestException("品目コード '" + request.getCode() + "' は既に使用されています");
            }
        }

        item.setCode(request.getCode());
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setUnitPrice(request.getUnitPrice());
        item.setUnit(request.getUnit());

        return ItemResponse.from(itemRepository.save(item));
    }

    public void delete(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", "id", id));

        if (!invoiceDetailRepository.findByItemId(id).isEmpty()) {
            throw new BusinessException("ITEM_IN_USE",
                "この品目は請求書で使用されているため削除できません");
        }

        itemRepository.deleteById(id);
    }
}
