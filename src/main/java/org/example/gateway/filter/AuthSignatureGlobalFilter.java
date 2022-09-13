package org.example.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author JQ
 * @說明：全局過濾器，進行統一的URL過濾及權限認證
 */
@Configuration
public class AuthSignatureGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestPath = exchange.getRequest().getPath().value();
        //判斷接口情況地址如果爲內部服務定義,則攔截
        if (requestPath.contains("internal")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        //應用accessToken有效性（這裏只是簡單判空，可以根據實際業務場景進行擴展）
        String accessToken = exchange.getRequest().getHeaders().getFirst("access_token");
        if (accessToken == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        //正常進行返回
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
