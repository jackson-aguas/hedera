package com.accenture.hedera.models.token;

public class Mint {

	private String tokenId;
	private long amount;
	private String memo;

	public Mint() {
		super();
	}

	public String getTokenId() {
		return tokenId;
	}

	public long getAmount() {
		return amount;
	}

	public String getMemo() {
		return memo;
	}
}
