package com.accenture.hedera.models.token;

import java.io.Serializable;

public class Create implements Serializable {

	private String name;
	private String symbol;
	private long supply;
	private String memo;

	public Create() {
		super();
	}

	public String getName() {
		return name;
	}

	public String getSymbol() {
		return symbol;
	}

	public long getSupply() {
		return supply;
	}

	public String getMemo() {
		return memo;
	}

}