package com.onspring.onspring_customer.domain.franchise.entity;

import com.onspring.onspring_customer.domain.common.entity.BaseEntity;
import com.onspring.onspring_customer.domain.common.entity.CustomerFranchise;
import com.onspring.onspring_customer.domain.common.entity.Transaction;
import com.onspring.onspring_customer.domain.franchise.dto.FranchiseDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "franchise")
public class Franchise extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String userName;

    @NotNull
    private String password;

    @NotNull
    private String name;

    @NotNull
    private String ownerName;

    @NotNull
    private String businessNumber;

    @NotNull
    @Column(unique = true)
    private String address;

    @Column
    private Double latitude;

    @Column
    private Double longitude;


    @NotNull
    @Column(unique = true)
    private String phone;

    @Column
    private String description;

    @NotNull
    private boolean isActivated;

    @OneToMany(mappedBy = "franchise")
    private Set<CustomerFranchise> customerFranchises = new HashSet<>();

    @OneToMany(mappedBy = "franchise")
    private Set<Transaction> transactions = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY) @BatchSize(size = 20)
    List<FranchiseMenuImages> menuImages = new ArrayList<>();

    public Franchise(Long id, String name, String address, String phone, String description) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.description = description;
    }

    public void addImage(FranchiseMenuImages image) {
        menuImages.add(image);
    }

    public void addImageString(String fileName) {
        FranchiseMenuImages image = new FranchiseMenuImages(fileName, this.menuImages.size());
        addImage(image);
    }

    public void clearList() {
        this.menuImages.clear();
    }

    public FranchiseDto entityToDto() {
        FranchiseDto franchiseDto = FranchiseDto.builder()
                .id(this.id)
                .name(this.name)
                .address(this.address)
                .phone(this.phone)
                .businessNumber(this.businessNumber)
                .ownerName(this.ownerName)
                .description(this.description)
                .build();

        // 이미지 파일 리스트 변환
        List<String> fileNames = new ArrayList<>();
        for (FranchiseMenuImages image : this.menuImages) {
            fileNames.add(image.getFileName());
        }
        franchiseDto.setUploadFileNames(fileNames);

        return franchiseDto;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeAddress(String address) {
        this.address = address;
    }

    public void changePhone(String phone) {
        this.phone = phone;
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void changeActivated(boolean isActivated) {
        this.isActivated = isActivated;
    }



}