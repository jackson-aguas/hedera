package com.accenture.hedera.models.token;

import java.io.Serializable;

public class Transfer implements Serializable {

	private String accountId;
	private String privateKey;
	private String amount;
	private String memo;

	public Transfer() {
		super();
	}

	public String getAccountId() {
		return accountId;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public String getAmount() {
		return amount;
	}

	public String getMemo() {
		return memo;
	}

}
