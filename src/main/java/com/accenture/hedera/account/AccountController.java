package com.accenture.hedera.account;

import com.accenture.hedera.account.models.Account;
import com.hedera.hashgraph.sdk.AccountBalance;
import com.hedera.hashgraph.sdk.AccountInfo;
import com.hedera.hashgraph.sdk.TransactionRecord;
import com.hedera.hashgraph.sdk.PrecheckStatusException;
import com.hedera.hashgraph.sdk.ReceiptStatusException;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.concurrent.TimeoutException;



@RestController
@RequestMapping(path = "/account")
public class AccountController {
    @Autowired
    AccountService accountService;

    public AccountController() {
        accountService = new AccountService();
    }

    //  /**
    //  * Account Create
    //  * Account Read Info
    //  * Account Read Balance
    //  * Account Update
    //  * Account Delete
    //  * Hbar Transfer
    //  */

    @PostMapping("")
    public Account createAccount(@RequestParam(defaultValue = "0") long initialBalance) throws TimeoutException, ReceiptStatusException, PrecheckStatusException {
        return accountService.createAccount(initialBalance);
    }

    @GetMapping("/{accountId}")
    public AccountInfo getAccount(@PathVariable(value = "accountId") String accountId) throws TimeoutException, ReceiptStatusException, PrecheckStatusException {
        AccountInfo accountInfo = accountService.getAccount(accountId);
        return accountInfo;
    }

    @GetMapping("/{accountId}/info")
    public AccountInfo getAccountInfo(@PathVariable(value = "accountId") String accountId) throws TimeoutException, ReceiptStatusException, PrecheckStatusException {
        AccountInfo accountInfo = accountService.getAccount(accountId);
        return accountInfo;
    }

    @GetMapping("/{accountId}/balance")
    public AccountBalance getAccountBalance(@PathVariable(value = "accountId") String accountId) throws TimeoutException, ReceiptStatusException, PrecheckStatusException {
        AccountBalance accountBalance = accountService.getAccountBalance(accountId);
        return accountBalance;
    }

    @DeleteMapping("/{accountId}")
    public boolean deleteAccount(@PathVariable(value = "accountId") String accountId, @RequestParam(value = "privateKey") String privateKey) throws TimeoutException, ReceiptStatusException, PrecheckStatusException {
        return accountService.deleteAccount(accountId, privateKey);
    }

    @PostMapping("/{accountId}")
    public TransactionRecord transferHbar(@PathVariable(value = "accountId") String accountId, @RequestParam(value = "amount") long hbar) throws TimeoutException, ReceiptStatusException, PrecheckStatusException {
        return accountService.transferHbar(accountId, hbar);
    }
}
