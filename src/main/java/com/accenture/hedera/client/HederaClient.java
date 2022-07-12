package com.accenture.hedera.client;

import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.accenture.hedera.utils.EnvUtils;

import java.util.Collections;

public class HederaClient {
	/**
	 * Create singleton Hedera client
	 */
	private static Client client = createClient(EnvUtils.getOperatorId(), EnvUtils.getOperatorKey());

	private HederaClient() throws InterruptedException {
		setMirrorNetwork();
	}

	public static Client getHederaClient() {
		if (client == null) {
			try {
				new HederaClient();
			} catch (InterruptedException e) {
				System.out.println(e);
			}

		}
		return client;
	}

	public static Client createClient(AccountId opId, PrivateKey opKey) {
		// Create Hedera client
		client = (EnvUtils.getHederaEnvironment() == EnvUtils.HederaEnvironment.TESTNET) ? Client.forTestnet()
				: Client.forMainnet();
		client.setOperator(opId, opKey);
		return client;
	}

	// Set Hedera mirror node for development
	public static Client setMirrorNetwork() throws InterruptedException {
		if (client != null) {
			return client.setMirrorNetwork(Collections.singletonList(EnvUtils.getMirrorNodeAddress()));
		} else {
			new HederaClient();
			return client.setMirrorNetwork(Collections.singletonList(EnvUtils.getMirrorNodeAddress()));
		}
	}
}
