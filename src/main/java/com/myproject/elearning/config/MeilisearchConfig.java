package com.myproject.elearning.config;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Config;
import com.meilisearch.sdk.Index;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeilisearchConfig {
    @Value("${meilisearch.host}")
    private String host;

    @Value("${meilisearch.master-key}")
    private String masterKey;

    private static final String COURSE_INDEX = "courses";

    @Bean
    public Client client() {
        Config config = new Config(host, masterKey);
        return new Client(config);
    }

    /**
     * Note that Meilisearch will rebuild your index whenever you update filterableAttributes
     */
    @Bean
    public Index courseIndex(Client client) {
        Index index = client.index(COURSE_INDEX);
        index.updateSearchableAttributesSettings(new String[] {"title", "instructorName"});
        index.updateFilterableAttributesSettings(new String[] {"category", "level", "duration", "price"});
        return index;
    }
}
