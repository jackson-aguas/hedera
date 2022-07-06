package com.accenture.hedera.client;

import java.util.Collections;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.Hbar;
import com.accenture.hedera.utils.EnvUtils;

public class HederaClient {
    /**
     * Create Singleton client instead of recreating everywhere
     */
    private static Client client = null;

    private HederaClient() throws InterruptedException {
        client = createClient(EnvUtils.getOperatorId(), EnvUtils.getOperatorKey());
        setMirrorNetwork();
    }

    public static Client getHederaClient() {
        if(client == null) {
            try {
                new HederaClient();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
        }
        return client;
    }

    public static Client createClient(AccountId opId, PrivateKey opKey) {
        //Create Hedera client
        Client client = (EnvUtils.getHederaEnvironment() == EnvUtils.HederaEnvironment.TESTNET)
            ? Client.forTestnet() 
            : Client.forMainnet();
        client.setOperator(opId, opKey);
        client.setDefaultMaxTransactionFee(new Hbar(6));
        client.setDefaultMaxQueryPayment(new Hbar(3));
        return client;
    }

    //Set Hedera mirror node for developments 
    public static Client setMirrorNetwork() throws InterruptedException {
        if (client != null) {
            return client.setMirrorNetwork(Collections.singletonList(EnvUtils.getMirrorNodeAddress()));
        } else {
            new HederaClient();
            return client.setMirrorNetwork(Collections.singletonList(EnvUtils.getMirrorNodeAddress()));
        }
    }
    
}
