package de.tud.feedback.configuration;

import com.google.common.base.Strings;
import de.tud.feedback.domain.Workflow;
import de.tud.feedback.domain.WorkflowInstance;
import de.tud.feedback.repository.WorkflowRepository;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.conversion.MetaDataDrivenConversionService;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.server.Neo4jServer;
import org.springframework.data.neo4j.server.RemoteServer;

import java.io.IOException;

@Configuration
@EnableNeo4jRepositories(basePackageClasses = {WorkflowRepository.class})
@EnableConfigurationProperties(KnowledgeConfiguration.ServerProperties.class)
class KnowledgeConfiguration extends Neo4jConfiguration {

    @Autowired
    ServerProperties server;

    @Override
    public Neo4jServer neo4jServer() {
        if (server.hasAuthentication()) {
            return new RemoteServer(server.url(), server.username(), server.password());
        } else {
            return new RemoteServer(server.url());
        }
    }

    @Override
    public SessionFactory getSessionFactory() {
        return new SessionFactory(
                Workflow.class.getPackage().getName(),
                WorkflowInstance.class.getPackage().getName());
    }

    @Bean
    public ConversionService conversionService(final ResourceLoader loader) {
        MetaDataDrivenConversionService conversionService
                = new MetaDataDrivenConversionService(getSessionFactory().metaData());

        conversionService.addConverter(new Converter<String, Resource>() {
            @Override
            public Resource convert(String source) {
                return loader.getResource(source);
            }
        });

        conversionService.addConverter(new Converter<Resource, String>() {
            @Override
            public String convert(Resource source) {
                try {
                    return source.getURI().toString();
                } catch (IOException e) {
                    return null;
                }
            }
        });

        return conversionService;
    }

    @ConfigurationProperties(prefix = "service.knowledge")
    public static final class ServerProperties {

        @URL
        @NotBlank
        private String url;

        private String username;

        private String password;

        public String url() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String username() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String password() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public boolean hasAuthentication() {
            return !Strings.isNullOrEmpty(username);
        }

    }

}
