package io.plugwerk.server.config

import io.plugwerk.descriptor.DescriptorResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DescriptorConfiguration {

    @Bean
    fun descriptorResolver(): DescriptorResolver = DescriptorResolver()
}
