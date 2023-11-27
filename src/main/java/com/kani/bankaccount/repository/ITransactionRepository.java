package com.kani.bankaccount.repository;

import com.kani.bankaccount.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ITransactionRepository extends JpaRepository<Transaction,Long> {

}
