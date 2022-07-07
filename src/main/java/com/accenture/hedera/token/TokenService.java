package com.accenture.hedera.token;

import com.accenture.hedera.utils.EnvUtils;
import com.accenture.hedera.client.HederaClient;
// import com.accenture.hedera.account.models.Account;

// import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TokenInfo;
import com.hedera.hashgraph.sdk.TokenInfoQuery;
import com.hedera.hashgraph.sdk.TransactionResponse;
import com.hedera.hashgraph.sdk.TokenCreateTransaction;
// import com.hedera.hashgraph.sdk.TokenDeleteTransaction;

import com.hedera.hashgraph.sdk.PrecheckStatusException;
import com.hedera.hashgraph.sdk.ReceiptStatusException;

import org.springframework.stereotype.Service;

import java.util.Objects;
//import java.util.Collections;
import java.util.concurrent.TimeoutException;

@Service
public class TokenService {
    
    //Grab the singleton hedera client
    public Client client = HederaClient.getHederaClient();
    AccountId OPERATOR_ID = EnvUtils.getOperatorId();
    PrivateKey OPERATOR_KEY = EnvUtils.getOperatorKey();

    //Create
    public TokenId createToken(String name, String symbol) throws TimeoutException, ReceiptStatusException, PrecheckStatusException { 
        TransactionResponse response = new TokenCreateTransaction()
            .setTokenName(name)
            .setTokenSymbol(symbol)
            .setDecimals(3)
            .setInitialSupply(100)
            .setTreasuryAccountId(OPERATOR_ID)
            .setAdminKey(OPERATOR_KEY.getPublicKey())
            .setFreezeKey(OPERATOR_KEY.getPublicKey())
            .setWipeKey(OPERATOR_KEY.getPublicKey())
            .setKycKey(OPERATOR_KEY.getPublicKey())
            .setSupplyKey(OPERATOR_KEY.getPublicKey())
            .setFreezeDefault(false)
            .execute(client);

        TokenId tokenId = Objects.requireNonNull(response.getReceipt(client).tokenId);
        System.out.println("token id: " + tokenId);

        return tokenId;

    }

    //Read
    public TokenInfo getToken(String tokenIdString) throws TimeoutException, ReceiptStatusException, PrecheckStatusException {
        TokenId tokenId = TokenId.fromString(tokenIdString);
        TokenInfo tokenInfo = new TokenInfoQuery().setTokenId(tokenId).execute(client);
        return tokenInfo;
    }
   
    //Update
   
    //Delete 
}
