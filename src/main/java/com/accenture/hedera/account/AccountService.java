package com.accenture.hedera.account;

import com.accenture.hedera.utils.EnvUtils;
import com.accenture.hedera.client.HederaClient;
import com.accenture.hedera.account.models.Account;

import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.PublicKey;
import com.hedera.hashgraph.sdk.AccountInfo;
import com.hedera.hashgraph.sdk.AccountInfoQuery;
import com.hedera.hashgraph.sdk.AccountBalance;
import com.hedera.hashgraph.sdk.AccountBalanceQuery;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransactionResponse;
import com.hedera.hashgraph.sdk.AccountCreateTransaction;
import com.hedera.hashgraph.sdk.AccountDeleteTransaction;

import com.hedera.hashgraph.sdk.PrecheckStatusException;
import com.hedera.hashgraph.sdk.ReceiptStatusException;

import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeoutException;

@Service
public class AccountService {

    //Grab the  singleton hedera client
    public Client client = HederaClient.getHederaClient();

    /**
     * Creates a Hedera Account
     *
     * @param initialBalance - initial Balance on hedera
     * @return hedera accountId, hedera public and private key, solidity address
     */
    public Account createAccount(long initialBalance) throws TimeoutException, ReceiptStatusException, PrecheckStatusException{
        // Generates new private key and dervives the public key
        PrivateKey newPrivateKey = PrivateKey.generateED25519();
        PublicKey newPublicKey = newPrivateKey.getPublicKey();
        System.out.println("private key: " + newPrivateKey.toString());
        System.out.println("public key: " + newPublicKey.toString());

        // Creates new hedera account for generated keys and transfers initial balance from operator
        AccountCreateTransaction txn = new AccountCreateTransaction()
            .setKey(newPublicKey)
            .setInitialBalance(new Hbar(initialBalance));
        TransactionResponse txnResponse = txn.execute(client);

        // Gets reciept from completed transaction
        TransactionReceipt receipt = txnResponse.getReceipt(client);
        AccountId newAccountId = receipt.accountId;
        System.out.println("account id: " + newAccountId.toString());

        // Applys custom account class
        Account account = new Account(newAccountId, newPrivateKey);
        return account;
    }

    /**
     * Gets a Hedera Account by ID
     *
     * @param accountIdString - stringified account id
     * @return hedera account
     */
    public AccountInfo getAccount(String accountIdString) throws TimeoutException, ReceiptStatusException, PrecheckStatusException {
        AccountId accountId = AccountId.fromString(accountIdString);
        AccountInfoQuery accountQuery = new AccountInfoQuery()
            .setAccountId(accountId);
        AccountInfo accountInfo = accountQuery.execute(client);
        System.out.println(accountInfo.toString());
        return accountInfo;
    }

    /**
     * Gets a Hedera account balance
     * 
     * @param accountIdString - account to get balance
     * @return balance
     */
    public AccountBalance getAccountBalance(String accountIdString)  throws TimeoutException, ReceiptStatusException, PrecheckStatusException {

        AccountId accountId = AccountId.fromString(Objects.requireNonNull(accountIdString));

        // Because AccountBalanceQuery is a free query, we can make it without setting an operator on the client.
        AccountBalance balance = new AccountBalanceQuery()
            .setAccountId(accountId)
            .execute(client);

        System.out.println("hbar balance: " + balance.hbars);
        System.out.println("token balances: " + balance.tokens);
        return balance;
    }

    /**
     * Deletes a Hedera account
     * 
     * @param accountIdString - account to be deleted
     * @param privateKeyString - of the account deleted
     * @return boolean
     */
    public boolean deleteAccount(String accountIdString, String privateKeyString) throws TimeoutException, ReceiptStatusException, PrecheckStatusException {

        AccountId accountId = AccountId.fromString(Objects.requireNonNull(accountIdString));
        PrivateKey privateKey = PrivateKey.fromString(Objects.requireNonNull(privateKeyString));

        TransactionReceipt txn = new AccountDeleteTransaction()
            // note the transaction ID has to use the ID of the account being deleted
            .setTransactionId(TransactionId.generate(accountId))
            .setAccountId(accountId)
            .setTransferAccountId(EnvUtils.getOperatorId())
            .freezeWith(client)
            .sign(privateKey)
            .execute(client)
            .getReceipt(client);

        System.out.println("deleted " + txn.accountId);
        return true;
    }
}
