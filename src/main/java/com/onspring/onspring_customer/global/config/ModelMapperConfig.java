package com.onspring.onspring_customer.global.config;

import com.onspring.onspring_customer.domain.common.dto.TransactionDto;
import com.onspring.onspring_customer.domain.common.entity.Transaction;
import com.onspring.onspring_customer.domain.franchise.dto.FranchiseDto;
import com.onspring.onspring_customer.domain.user.dto.EndUserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Transaction -> TransactionDto 매핑 규칙 정의
        modelMapper.addMappings(new PropertyMap<Transaction, TransactionDto>() {
            @Override
            protected void configure() {
                map(source.getId(), destination.getId());
                map(source.getTransactionTime(), destination.getTransactionTime());
                map(source.getAmount(), destination.getAmount());
                map(source.isAccepted(), destination.isAccepted());
                map(source.isClosed(), destination.isClosed());

                // franchise와 endUser를 각각 DTO로 매핑
                using(ctx -> modelMapper.map(ctx.getSource(), FranchiseDto.class))
                        .map(source.getFranchise(), destination.getFranchiseDto());
                using(ctx -> modelMapper.map(ctx.getSource(), EndUserDto.class))
                        .map(source.getEndUser(), destination.getEndUserDto());
            }
        });

        return modelMapper;
    }
}
