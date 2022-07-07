package com.accenture.hedera.token;

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

    @PostMapping("")
    public TokenId createToken(@RequestParam(defaultValue = "XXXXX") String name, @RequestParam(defaultValue = "X") String symbol) throws TimeoutException, ReceiptStatusException, PrecheckStatusException {
        return tokenService.createToken(name, symbol);
    }

    @GetMapping("/{tokenId}")
    public TokenInfo getToken(@PathVariable(value = "tokenId") String tokenId) throws TimeoutException, ReceiptStatusException, PrecheckStatusException {
        return tokenService.getToken(tokenId);
    }
}
