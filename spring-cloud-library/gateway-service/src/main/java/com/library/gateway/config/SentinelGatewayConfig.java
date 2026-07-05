package com.library.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPathPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiPredicateItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class SentinelGatewayConfig {

    @Bean
    public CommandLineRunner initGatewaySentinelRules() {
        return args -> {
            // Define API groups
            Set<ApiDefinition> apiDefinitions = new HashSet<>();

            ApiDefinition userApi = new ApiDefinition("user-api")
                    .setPredicateItems(new HashSet<ApiPredicateItem>() {{
                        add(new ApiPathPredicateItem()
                                .setPattern("/user/**")
                                .setMatchStrategy(SentinelGatewayConstants.URL_MATCH_STRATEGY_PREFIX));
                    }});
            apiDefinitions.add(userApi);

            GatewayApiDefinitionManager.loadApiDefinitions(apiDefinitions);

            // Define gateway flow rules
            Set<GatewayFlowRule> rules = new HashSet<>();

            // Limit /user/login to 10 QPS
            GatewayFlowRule loginRule = new GatewayFlowRule("user-api")
                    .setResourceMode(SentinelGatewayConstants.RESOURCE_MODE_CUSTOM_API_NAME)
                    .setCount(10)
                    .setIntervalSec(1);
            rules.add(loginRule);

            GatewayRuleManager.loadRules(rules);
        };
    }
}
