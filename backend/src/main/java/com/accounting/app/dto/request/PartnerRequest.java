package com.accounting.app.dto.request;

import javax.validation.constraints.*;

public class PartnerRequest {
    @NotBlank(message = "コードは必須です")
    @Size(max = 50)
    private String code;

    @NotBlank(message = "名前は必須です")
    @Size(max = 255)
    private String name;

    @Size(max = 20)
    private String postalCode;

    @Size(max = 500)
    private String address;

    @Size(max = 50)
    private String phone;

    @Email(message = "有効なメールアドレスを入力してください")
    @Size(max = 255)
    private String email;

    @NotNull(message = "取引先タイプは必須です")
    private String partnerType; // CUSTOMER, VENDOR, BOTH

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPartnerType() { return partnerType; }
    public void setPartnerType(String partnerType) { this.partnerType = partnerType; }
}
