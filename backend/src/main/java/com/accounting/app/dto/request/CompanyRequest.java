package com.accounting.app.dto.request;

import javax.validation.constraints.*;

public class CompanyRequest {
    @NotBlank @Size(max = 200)
    private String name;
    @Size(max = 500)
    private String address;
    @Size(max = 50)
    private String phone;
    @Size(max = 100)
    private String email;
    @Size(max = 50)
    private String taxId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }
}
