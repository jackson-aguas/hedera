package com.accenture.hedera.nft;

import com.accenture.hedera.utils.EnvUtils;
import com.accenture.hedera.client.HederaClient;
import com.accenture.hedera.account.AccountService;

import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.Status;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.AccountBalance;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.NftId;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TokenType;
import com.hedera.hashgraph.sdk.TokenInfo;
import com.hedera.hashgraph.sdk.TokenInfoQuery;
import com.hedera.hashgraph.sdk.TokenMintTransaction;
import com.hedera.hashgraph.sdk.TokenSupplyType;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransactionResponse;
import com.hedera.hashgraph.sdk.TransferTransaction;
import com.hedera.hashgraph.sdk.TokenWipeTransaction;
import com.hedera.hashgraph.sdk.TokenCreateTransaction;
import com.hedera.hashgraph.sdk.TokenDeleteTransaction;
import com.hedera.hashgraph.sdk.TokenGrantKycTransaction;
import com.hedera.hashgraph.sdk.TokenAssociateTransaction;

import com.hedera.hashgraph.sdk.PrecheckStatusException;
import com.hedera.hashgraph.sdk.ReceiptStatusException;

import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Collections;
import java.nio.charset.Charset;
import java.util.concurrent.TimeoutException;

@Service
public class NftService {

	// Grab the singleton hedera client
	private Client client = HederaClient.getHederaClient();
	private AccountId operatorId = EnvUtils.getOperatorId();
	private PrivateKey operatorKey = EnvUtils.getOperatorKey();
	AccountService accountService = new AccountService();

	// Create NFT
	public TokenId createNft(String name, String symbol, long supply)
			throws TimeoutException, ReceiptStatusException, PrecheckStatusException {

		TransactionResponse response = new TokenCreateTransaction().setTokenName(name).setTokenSymbol(symbol)
				.setTokenType(TokenType.NON_FUNGIBLE_UNIQUE).setDecimals(0).setInitialSupply(0)
				.setSupplyType(TokenSupplyType.FINITE).setMaxSupply(supply).setTreasuryAccountId(operatorId)
				.setAdminKey(operatorKey.getPublicKey()).setFreezeKey(operatorKey.getPublicKey())
				.setWipeKey(operatorKey.getPublicKey()).setKycKey(operatorKey.getPublicKey())
				.setSupplyKey(operatorKey.getPublicKey()).setFreezeDefault(false).execute(client);

		return Objects.requireNonNull(response.getReceipt(client).tokenId);

	}

	// Read NFT token info
	public TokenInfo getNft(String tokenIdString) throws TimeoutException, PrecheckStatusException {

		TokenId tokenId = TokenId.fromString(tokenIdString);

		return Objects.requireNonNull(new TokenInfoQuery().setTokenId(tokenId).execute(client));
	}

	// Mint an NFT
	// Operator key is also the supply key (txn must be signed by supplyKey)
	public NftId mintNft(String tokenIdString)
			throws TimeoutException, ReceiptStatusException, PrecheckStatusException {

		TokenId tokenId = TokenId.fromString(tokenIdString);
		String metadata = "This is immutable metadata";

		TransactionReceipt receipt = new TokenMintTransaction().setTokenId(tokenId)
				.addMetadata(metadata.getBytes(Charset.defaultCharset())).freezeWith(client).sign(operatorKey)
				.execute(client).getReceipt(client);

		return new NftId(tokenId, receipt.serials.get(0));
	}

	// Transfer tokens
	public boolean transferNft(String nftIdString, String toAccountIdString, String toPrivateKeyString)
			throws TimeoutException, ReceiptStatusException, PrecheckStatusException {

		NftId nftId = NftId.fromString(nftIdString);
		AccountId toAccountId = AccountId.fromString(toAccountIdString);
		PrivateKey toPrivateKey = PrivateKey.fromString(toPrivateKeyString);

		if (!checkAssociationStatus(toAccountId, nftId.tokenId)) {
			new TokenAssociateTransaction().setAccountId(toAccountId)
					.setTokenIds(Collections.singletonList(nftId.tokenId)).freezeWith(client).sign(operatorKey)
					.sign(toPrivateKey).execute(client).getReceipt(client);

			new TokenGrantKycTransaction().setAccountId(toAccountId).setTokenId(nftId.tokenId).execute(client)
					.getReceipt(client);
		}

		TransactionReceipt receipt = new TransferTransaction().addNftTransfer(nftId, operatorId, toAccountId)
				.execute(client).getReceipt(client);

		return receipt.status == Status.SUCCESS;
	}

	// Delete token
	public boolean deleteNft(String tokenIdString)
			throws TimeoutException, ReceiptStatusException, PrecheckStatusException {

		TokenId tokenId = TokenId.fromString(tokenIdString);

		TransactionReceipt receipt = new TokenDeleteTransaction().setTokenId(tokenId).execute(client)
				.getReceipt(client);

		return receipt.status == Status.SUCCESS;
	}

	// Wipe tokens from an account
	// Operator key is also the wipe key (txn must be signed by both)
	public boolean wipeNft(String tokenIdString, String accountIdString, long amount)
			throws TimeoutException, ReceiptStatusException, PrecheckStatusException {

		TokenId tokenId = TokenId.fromString(tokenIdString);
		AccountId accountId = AccountId.fromString(accountIdString);

		TransactionReceipt receipt = new TokenWipeTransaction().setTokenId(tokenId).setAccountId(accountId)
				.setAmount(amount).freezeWith(client).sign(operatorKey).sign(operatorKey).execute(client)
				.getReceipt(client);

		return receipt.status == Status.SUCCESS;
	}

	// Checks association status of account for a token
	private boolean checkAssociationStatus(AccountId accountId, TokenId tokenId)
			throws TimeoutException, ReceiptStatusException, PrecheckStatusException {

		AccountBalance balance = accountService.getAccountBalance(accountId.toString());

		return balance.tokens.get(tokenId) != null;
	}

}
