package com.library.user.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SentinelConfig {

    @Bean
    public CommandLineRunner initSentinelRules() {
        return args -> {
            List<FlowRule> rules = new ArrayList<>();

            // Login rate limit: 10 QPS
            FlowRule loginRule = new FlowRule();
            loginRule.setResource("login");
            loginRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
            loginRule.setCount(10);
            rules.add(loginRule);

            // Register rate limit: 5 QPS
            FlowRule registerRule = new FlowRule();
            registerRule.setResource("register");
            registerRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
            registerRule.setCount(5);
            rules.add(registerRule);

            FlowRuleManager.loadRules(rules);
        };
    }
}
