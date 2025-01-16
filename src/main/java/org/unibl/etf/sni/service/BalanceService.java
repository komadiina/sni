package org.unibl.etf.sni.service;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.unibl.etf.sni.db.BalanceRepository;
import org.unibl.etf.sni.model.Balance;

@Service
public class BalanceService {
    private BalanceRepository balanceRepository;

    public Balance addBalance(Balance balance) {
        return balanceRepository.save(balance);
    }

    public Balance increase(@NotNull String username, @NotNull Double amount) {
        Balance balance = getBalance(username);
        balance.setAmount(balance.getAmount() + amount);
        balanceRepository.save(balance);
        return balance;
    }

    public boolean updateBalance(String username, Balance balance) {
        Balance b = balanceRepository.findByUsername(username).orElse(null);

        if (b != null) {
            b.setAmount(balance.getAmount());
            balanceRepository.save(b);
            return true;
        }

        return false;
    }

    public Balance getBalance(String username) {
        return balanceRepository.findByUsername(username).orElse(null);
    }

    public boolean deleteBalance(String username) {
        Balance balance = balanceRepository.findByUsername(username).orElse(null);

        if (balance == null) {
            return false;
        }

        balanceRepository.delete(balance);
        return true;
    }
}
