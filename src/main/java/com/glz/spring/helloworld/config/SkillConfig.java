package com.glz.spring.helloworld.config;

import com.alibaba.cloud.ai.graph.skills.registry.SkillRegistry;
import com.alibaba.cloud.ai.graph.skills.registry.classpath.ClasspathSkillRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.alibaba.cloud.ai.graph.agent.hook.skills.SkillsAgentHook;


@Configuration
public class SkillConfig {

    // 1. 注册中心：自动扫描 resources/skills 下的所有 SKILL.md
    @Bean
    public SkillRegistry skillRegistry() {
        return ClasspathSkillRegistry.builder()
                .classpathPath("skills")  // 指向 src/main/resources/skills
                .build();
    }

    // 2. 技能钩子：负责把技能列表注入给模型，并注册 read_skill 工具
    @Bean
    public SkillsAgentHook skillsAgentHook(SkillRegistry skillRegistry) {
        return SkillsAgentHook.builder()
                .skillRegistry(skillRegistry)
                .build();
    }
}
