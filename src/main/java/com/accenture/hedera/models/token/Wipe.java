package com.accenture.hedera.models.token;

public class Wipe {

	private String tokenId;
	private String accountId;
	private long amount;
	private String memo;

	public Wipe() {
		super();
	}

	public String getTokenId() {
		return tokenId;
	}

	public String getAccountId() {
		return accountId;
	}

	public long getAmount() {
		return amount;
	}

	public String getMemo() {
		return memo;
	}
}
