package com.accenture.hedera.token.models;

import java.io.Serializable;

public class Create implements Serializable {

	private String name;
	private String symbol;
	private String supply;
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

	public String getSupply() {
		return supply;
	}

	public String getMemo() {
		return memo;
	}

}