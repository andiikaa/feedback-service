package de.tud.feedback.service.impl;

import de.tud.feedback.annotation.GraphTransactional;
import de.tud.feedback.api.context.ContextImportStrategy;
import de.tud.feedback.api.context.impl.NodeCollectingCypherExecutor;
import de.tud.feedback.domain.Node;
import de.tud.feedback.domain.context.Context;
import de.tud.feedback.domain.context.ContextImport;
import de.tud.feedback.repository.ContextImportRepository;
import de.tud.feedback.repository.ContextRepository;
import de.tud.feedback.repository.PluginRepository;
import de.tud.feedback.service.ContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Provider;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Lists.partition;
import static com.google.common.collect.Sets.intersection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
public class PluginContextService implements ContextService {

    private static final Logger LOG = LoggerFactory.getLogger(PluginContextService.class);

    private ContextImportRepository imports;

    private ContextRepository contexts;

    private PluginRepository plugins;

    private Provider<NodeCollectingCypherExecutor> executorProvider;

    @PostConstruct
    public void initConstraints() {
        contexts.createUniqueConstraint();
    }

    @Async
    @Override
    @GraphTransactional
    public void importFrom(Context context) {
        context.getImports().forEach(contextImport -> {
            contextImport.setContext(context);
            importContextFrom(contextImport);
        });
    }

    @Override
    public void preProcess(Context context) {
        context.setUniqueId(context.getPlugin() + context.getName());
    }

    private void importContextFrom(ContextImport contextImport) {
        final String plugin = contextImport.getContext().getPlugin();
        final ContextImportStrategy strategy = plugins.findOne(plugin).contextImportStrategy();
        final NodeCollectingCypherExecutor executor = executorProvider.get();

        LOG.info("Import {} with {} ...", contextImport.getSource(), plugin);

        strategy.importContextWith(executor, contextImport.getSource(), contextImport.getMime());
        partition(entranceNodesFrom(executor.createdNodes()), 10).forEach(nodes -> {
            contextImport.getEntranceNodes().addAll(nodes);
            imports.save(contextImport); });

        LOG.info("Import {} done", contextImport.getSource());
    }

    private List<Node> entranceNodesFrom(Set<Long> createdNotes) {
        return intersection(createdNotes, orphanedNodes())
                .stream()
                .map(id -> {
                    Node node = new Node();
                    node.setId(id);
                    return node;
                }).collect(toList());
    }

    private Set<Long> orphanedNodes() {
        return contexts.findOrphanedNodeIds()
                .stream()
                .map(Integer::longValue)
                .collect(toSet());
    }

    @Autowired
    public void setContextImportRepository(ContextImportRepository repository) {
        imports = repository;
    }

    @Autowired
    public void setContextRepository(ContextRepository repository) {
        contexts = repository;
    }

    @Autowired
    public void setPluginRepository(PluginRepository repository) {
        plugins = repository;
    }

    @Autowired
    public void setCypherExecutorProvider(Provider<NodeCollectingCypherExecutor> provider) {
        executorProvider = provider;
    }

}