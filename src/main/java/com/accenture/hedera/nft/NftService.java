package com.accenture.hedera.nft;

import com.accenture.hedera.utils.EnvUtils;
import com.accenture.hedera.client.HederaClient;

import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TokenInfo;
import com.hedera.hashgraph.sdk.TokenInfoQuery;
import com.hedera.hashgraph.sdk.TokenNftInfo;
import com.hedera.hashgraph.sdk.TokenNftInfoQuery;
import com.hedera.hashgraph.sdk.TokenType;
import com.hedera.hashgraph.sdk.TokenSupplyType;
import com.hedera.hashgraph.sdk.TokenCreateTransaction;
import com.hedera.hashgraph.sdk.TransactionResponse;

import com.hedera.hashgraph.sdk.PrecheckStatusException;
import com.hedera.hashgraph.sdk.ReceiptStatusException;

import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeoutException;

@Service
public class NftService {

	// Grab the singleton hedera client
	private Client client = HederaClient.getHederaClient();
	private AccountId operatorId = EnvUtils.getOperatorId();
	private PrivateKey operatorKey = EnvUtils.getOperatorKey();

	// Create
	public TokenId createNft(String name, String symbol)
			throws TimeoutException, ReceiptStatusException, PrecheckStatusException {
		TransactionResponse response = new TokenCreateTransaction().setTokenName(name).setTokenSymbol(symbol)
				.setDecimals(3).setInitialSupply(100).setTreasuryAccountId(operatorId)
				.setAdminKey(operatorKey.getPublicKey()).setFreezeKey(operatorKey.getPublicKey())
				.setWipeKey(operatorKey.getPublicKey()).setKycKey(operatorKey.getPublicKey())
				.setSupplyKey(operatorKey.getPublicKey()).setTokenType(TokenType.NON_FUNGIBLE_UNIQUE)
				.setSupplyType(TokenSupplyType.FINITE).setFreezeDefault(false).execute(client);

		TokenId tokenId = Objects.requireNonNull(response.getReceipt(client).tokenId);
		System.out.println("token id: " + tokenId);
		return tokenId;

	}

	// Read
	public TokenInfo getNft(String tokenIdString)
			throws TimeoutException, ReceiptStatusException, PrecheckStatusException {
		TokenId tokenId = TokenId.fromString(tokenIdString);
		return new TokenInfoQuery().setTokenId(tokenId).execute(client);
	}

	// Update

	// Delete
}
