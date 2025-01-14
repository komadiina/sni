package org.unibl.etf.sni.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan(basePackages = "org.unibl.etf.sni.model")
public class JpaConfig {
}
