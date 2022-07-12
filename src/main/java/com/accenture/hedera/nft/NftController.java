package com.accenture.hedera.nft;

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

	@PostMapping("")
	public TokenId createNft(@RequestParam(defaultValue = "XXXXX") String name,
			@RequestParam(defaultValue = "X") String symbol)
			throws TimeoutException, ReceiptStatusException, PrecheckStatusException {
		return nftService.createNft(name, symbol);
	}

	@GetMapping("/{tokenId}")
	public TokenInfo getNft(@PathVariable(value = "tokenId") String tokenId)
			throws TimeoutException, ReceiptStatusException, PrecheckStatusException {
		return nftService.getNft(tokenId);
	}
}
