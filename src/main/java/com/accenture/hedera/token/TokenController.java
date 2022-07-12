package com.accenture.hedera.token;

import com.accenture.hedera.token.models.*;

import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TokenInfo;
import com.hedera.hashgraph.sdk.PrecheckStatusException;
import com.hedera.hashgraph.sdk.ReceiptStatusException;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping(path = "/token")
public class TokenController {
	@Autowired
	TokenService tokenService;

	public TokenController() {
		tokenService = new TokenService();
	}

	@PostMapping(consumes = "application/json")
	public TokenId createToken(@RequestBody Create create)
			throws TimeoutException, ReceiptStatusException, PrecheckStatusException {
		return tokenService.createToken(create.getName(), create.getSymbol(), create.getSupply());
	}

	@GetMapping("/{tokenId}")
	public TokenInfo getToken(@PathVariable(value = "tokenId") String tokenId)
			throws TimeoutException, ReceiptStatusException, PrecheckStatusException {
		return tokenService.getToken(tokenId);
	}

	@PostMapping(value = "/{tokenId}/transfer", consumes = "application/json")
	public boolean transferToken(@PathVariable(name = "tokenId") String tokenId, @RequestBody Transfer transfer)
			throws TimeoutException, ReceiptStatusException, PrecheckStatusException {
		return tokenService.transferToken(tokenId, transfer.getAccountId(), transfer.getPrivateKey(),
				transfer.getAmount());
	}

	@DeleteMapping("/{tokenId}")
	public boolean deleteToken(@PathVariable(value = "tokenId") String tokenId)
			throws TimeoutException, ReceiptStatusException, PrecheckStatusException {
		return tokenService.deleteToken(tokenId);
	}
}
