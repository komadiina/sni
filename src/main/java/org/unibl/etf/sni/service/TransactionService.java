package org.unibl.etf.sni.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.unibl.etf.sni.db.TransactionRepository;
import org.unibl.etf.sni.model.Transaction;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction addTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(Integer id, Transaction transaction) {
        transaction.setId(id.longValue());
        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Integer id) {
        transactionRepository.deleteById(id.longValue());
    }

    public Transaction getTransaction(Integer id) {
        return transactionRepository.findById(id.longValue()).orElse(null);
    }
}
