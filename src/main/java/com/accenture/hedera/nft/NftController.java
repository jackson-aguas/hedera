package com.accenture.hedera.nft;

import com.accenture.hedera.models.token.*;

import com.hedera.hashgraph.sdk.NftId;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TokenInfo;
import com.hedera.hashgraph.sdk.PrecheckStatusException;
import com.hedera.hashgraph.sdk.ReceiptStatusException;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping(path = "/nft")
public class NftController {
	@Autowired
	NftService nftService;

	public NftController() {
		nftService = new NftService();
	}

	@PostMapping(consumes = "application/json")
	public TokenId createNft(@RequestBody Create create)
			throws TimeoutException, ReceiptStatusException, PrecheckStatusException {
		return nftService.createNft(create.getName(), create.getSymbol(), create.getSupply());
	}

	@GetMapping(value = "/{tokenId}")
	public TokenInfo getNft(@PathVariable(value = "tokenId") String tokenId)
			throws TimeoutException, ReceiptStatusException, PrecheckStatusException {
		return nftService.getNft(tokenId);
	}

	@PostMapping(value = "/{tokenId}/mint")
	public NftId transferNft(@PathVariable(name = "tokenId") String tokenId)
			throws TimeoutException, ReceiptStatusException, PrecheckStatusException {
		return nftService.mintNft(tokenId);
	}

	@PostMapping(value = "/{tokenId}/transfer", consumes = "application/json")
	public boolean transferNft(@PathVariable(name = "tokenId") String tokenId, @RequestBody Transfer transfer)
			throws TimeoutException, ReceiptStatusException, PrecheckStatusException {
		return nftService.transferNft(tokenId, transfer.getAccountId(), transfer.getPrivateKey());
	}

	@DeleteMapping("/{tokenId}")
	public boolean deleteToken(@PathVariable(value = "tokenId") String tokenId)
			throws TimeoutException, ReceiptStatusException, PrecheckStatusException {
		return nftService.deleteNft(tokenId);
	}

	@DeleteMapping(value = "/{tokenId}/wipe", consumes = "application/json")
	public boolean transferToken(@PathVariable(name = "tokenId") String tokenId, @RequestBody Wipe wipe)
			throws TimeoutException, ReceiptStatusException, PrecheckStatusException {
		return nftService.wipeNft(tokenId, wipe.getAccountId(), wipe.getAmount());
	}
}
