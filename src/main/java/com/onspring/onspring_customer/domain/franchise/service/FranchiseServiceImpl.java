package com.onspring.onspring_customer.domain.franchise.service;

import com.onspring.onspring_customer.domain.franchise.dto.FranchiseDto;
import com.onspring.onspring_customer.domain.franchise.entity.Franchise;
import com.onspring.onspring_customer.domain.franchise.repository.FranchiseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class FranchiseServiceImpl implements FranchiseService {
    private final FranchiseRepository franchiseRepository;
    private final ModelMapper modelMapper;

    @Override
    public Long saveFranchise(FranchiseDto franchiseDto) {
        return 0L;
    }

    /**
     * 특정 ID에 해당하는 프랜차이즈 정보를 조회
     *
     * @param id 프랜차이즈의 ID
     * @return 해당 ID의 프랜차이즈 정보를 담은 FranchiseDto 객체 반환
     */
    @Override
    public FranchiseDto findFranchiseById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("프랜차이즈 ID는 null일 수 없습니다.");
        }

        Franchise franchise = franchiseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ID " + id + "에 해당하는 프랜차이즈를 찾을 수 없습니다."));

        log.info(franchise.getId());

        FranchiseDto franchiseDto = franchise.entityToDto();

        return franchiseDto;
    }

    @Override
    public List<FranchiseDto> findAllFranchise() {
        return List.of();
    }

    // 엔티티의 필드를 업데이트하는 메소드
    public void updateFranchiseFields(Franchise franchise, FranchiseDto franchiseDto) {
        if (franchiseDto.getName() != null) {
            franchise.setName(franchiseDto.getName());
        }
        if (franchiseDto.getAddress() != null) {
            franchise.setAddress(franchiseDto.getAddress());
        }
        if (franchiseDto.getPhone() != null) {
            franchise.setPhone(franchiseDto.getPhone());
        }
    }

    /**
     * 프랜차이즈의 정보를 수정
     *
     * @param id 프랜차이즈의 로그인에 사용할 Id 후에 수정 필요
     * @return 업데이트 성공 여부를 담은 boolean 객체
     */
    @Override
    public boolean updateFranchise(Long id, FranchiseDto franchiseDto) {
        Franchise franchise = franchiseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ID " + id + "에 해당하는 프랜차이즈를 찾을 수 없습니다."));

        updateFranchiseFields(franchise, franchiseDto);

        franchiseRepository.save(franchise);

        return true;
    }

    /**
     * 프랜차이즈의 메뉴 이미지를 수정
     *
     * @param franchiseDto 프랜차이즈의 메뉴 이미지를 담은 dto객체
     * @return 업데이트 성공 여부를 담은 boolean 객체
     */
    @Override
    public boolean updateMenuImage(FranchiseDto franchiseDto) {
        Franchise franchise = franchiseRepository.findById(franchiseDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("ID" + franchiseDto.getId() + "에 해당하는 프랜차이즈를 찾을 수 없습니다."));

        //업로드 된 파일을 우선 지움
        franchise.clearList();

        List<String> uploadFileNames = franchiseDto.getUploadFileNames();

        if (uploadFileNames != null && !uploadFileNames.isEmpty()) {
            uploadFileNames.forEach(franchise::addImageString);
        }
        franchiseRepository.save(franchise);

        return true;
    }


    @Override
    public boolean deleteFranchiseById(Long id) {
        return false;
    }
}
