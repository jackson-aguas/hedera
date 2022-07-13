package com.accenture.hedera.utils;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.PrivateKey;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Objects;

public class EnvUtils {

	static Dotenv getEnv() {
		return Dotenv.load();
	}

	public static HederaEnvironment getHederaEnvironment() {
		String _env = Objects.requireNonNull(getEnv().get("HEDERA_ENVIRONMENT"));
		return HederaEnvironment.valueOf(_env);
	}

	public static String getMirrorNodeAddress() {
		return Objects.requireNonNull(getEnv().get("MIRROR_NODE_ADDRESS"));
	}

	public static AccountId getOperatorId() {
		return AccountId.fromString(Objects.requireNonNull(getEnv().get("OPERATOR_ID")));
	}

	public static PrivateKey getOperatorKey() {
		return PrivateKey.fromString(Objects.requireNonNull(getEnv().get("OPERATOR_KEY")));
	}

	public enum HederaEnvironment {
		TESTNET, MAINNET
	}
}
