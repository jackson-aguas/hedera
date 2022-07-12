package com.accenture.hedera.token;

import com.accenture.hedera.utils.EnvUtils;
import com.accenture.hedera.client.HederaClient;
import com.accenture.hedera.account.AccountService;

import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.AccountBalance;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.Status;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TokenInfo;
import com.hedera.hashgraph.sdk.TokenInfoQuery;
import com.hedera.hashgraph.sdk.TokenSupplyType;
import com.hedera.hashgraph.sdk.TokenType;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransactionResponse;
import com.hedera.hashgraph.sdk.TransferTransaction;
import com.hedera.hashgraph.sdk.TokenCreateTransaction;
import com.hedera.hashgraph.sdk.TokenDeleteTransaction;
import com.hedera.hashgraph.sdk.TokenGrantKycTransaction;
import com.hedera.hashgraph.sdk.TokenAssociateTransaction;

import com.hedera.hashgraph.sdk.PrecheckStatusException;
import com.hedera.hashgraph.sdk.ReceiptStatusException;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

@Service
public class TokenService {

	// Grab the singleton hedera client
	private Client client = HederaClient.getHederaClient();
	private AccountId operatorId = EnvUtils.getOperatorId();
	private PrivateKey operatorKey = EnvUtils.getOperatorKey();

	// Create
	public TokenId createToken(String name, String symbol, String supply)
			throws TimeoutException, ReceiptStatusException, PrecheckStatusException {

		long initialSupply = Long.parseLong(supply);
		TransactionResponse response = new TokenCreateTransaction().setTokenName(name).setTokenSymbol(symbol)
				.setTokenType(TokenType.FUNGIBLE_COMMON).setDecimals(3).setInitialSupply(initialSupply)
				.setTreasuryAccountId(operatorId).setAdminKey(operatorKey.getPublicKey())
				.setFreezeKey(operatorKey.getPublicKey()).setWipeKey(operatorKey.getPublicKey())
				.setKycKey(operatorKey.getPublicKey()).setSupplyKey(operatorKey.getPublicKey()).setFreezeDefault(false)
				.setSupplyType(TokenSupplyType.INFINITE).execute(client);
		return Objects.requireNonNull(response.getReceipt(client).tokenId);
	}

	// Read
	public TokenInfo getToken(String tokenIdString)
			throws TimeoutException, ReceiptStatusException, PrecheckStatusException {

		TokenId tokenId = TokenId.fromString(tokenIdString);
		return Objects.requireNonNull(new TokenInfoQuery().setTokenId(tokenId).execute(client));
	}

	// Transfer
	public boolean transferToken(String tokenIdString, String toAccountIdString, String toPrivateKeyString,
			String amountString) throws TimeoutException, ReceiptStatusException, PrecheckStatusException {

		TokenId tokenId = TokenId.fromString(tokenIdString);
		AccountId toAccountId = AccountId.fromString(toAccountIdString);
		PrivateKey toPrivateKey = PrivateKey.fromString(toPrivateKeyString);
		long amount = Long.parseLong(amountString);

		if (!checkAssociationStatus(toAccountId, tokenId)) {
			new TokenAssociateTransaction().setAccountId(toAccountId).setTokenIds(Collections.singletonList(tokenId))
					.freezeWith(client).sign(operatorKey).sign(toPrivateKey).execute(client).getReceipt(client);

			new TokenGrantKycTransaction().setAccountId(toAccountId).setTokenId(tokenId).execute(client)
					.getReceipt(client);
		}

		TransactionReceipt receipt = new TransferTransaction().addTokenTransfer(tokenId, operatorId, -1 * amount)
				.addTokenTransfer(tokenId, toAccountId, amount).execute(client).getReceipt(client);

		return receipt.status == Status.SUCCESS;
	}

	// Delete
	public boolean deleteToken(String tokenIdString)
			throws TimeoutException, ReceiptStatusException, PrecheckStatusException {

		TokenId tokenId = TokenId.fromString(tokenIdString);

		TransactionReceipt receipt = new TokenDeleteTransaction().setTokenId(tokenId).execute(client)
				.getReceipt(client);

		return receipt.status == Status.SUCCESS;
	}

	// Checks association status of account for a token
	private boolean checkAssociationStatus(AccountId accountId, TokenId tokenId)
			throws TimeoutException, ReceiptStatusException, PrecheckStatusException {

		AccountService accountService = new AccountService();
		AccountBalance balance = accountService.getAccountBalance(accountId.toString());

		return balance.tokens.get(tokenId) != null;
	}
}
