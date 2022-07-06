package com.accenture.hedera.account.models;

import java.io.Serializable;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.PublicKey;

public class Account implements Serializable {
    //  /**
    //  * @param accountId
    //  * @param privateKey
    //  * @param publicKey
    //  * @param solidityAddress
    //  * @param accountIdString
    //  * @param privateKeyString
    //  * @param publicKeyString
    //  */

    private AccountId accountId;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private String accountIdString;
    private String privateKeyString;
    private String publicKeyString;
    private String solidityAddress;

    public Account() {
        super();
    }

    public Account(AccountId accountId, PrivateKey privateKey) {
        this.accountId = accountId;
        this.privateKey = privateKey;
        this.publicKey = privateKey.getPublicKey();
        this.accountIdString = accountId.toString();
        this.privateKeyString = privateKey.toString();
        this.publicKeyString = publicKey.toString();
        this.solidityAddress = accountId.toSolidityAddress();
    }

    public AccountId getAccountId() {
        return accountId;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public String getSolidityAddress() {
        return solidityAddress;
    }

    public String getAccountIdString() {
        return accountIdString;
    }

    public String getPrivateKeyString() {
        return privateKeyString;
    }

    public String getPublicKeyString() {
        return publicKeyString;
    }

}
