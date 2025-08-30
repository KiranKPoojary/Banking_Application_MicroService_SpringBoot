package com.example.accountservice.repository;

import com.example.accountservice.dto.LedgerDto;
import com.example.accountservice.entity.Ledger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {
    List<Ledger>  findByAccount_IdOrderByEntryDateDesc(Long accountID);

}
