package de.tud.feedback.plugin;

import de.tud.feedback.ContextImporter;
import de.tud.feedback.ContextUpdater;
import de.tud.feedback.CypherExecutor;
import de.tud.feedback.FeedbackPlugin;
import de.tud.feedback.loop.CommandExecutor;
import de.tud.feedback.loop.MismatchProvider;
import de.tud.feedback.loop.MonitorAgent;
import de.tud.feedback.loop.ObjectiveEvaluator;
import de.tud.feedback.plugin.factory.*;
import de.tud.feedback.repository.CompensationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Provider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Component
public class ProteusFeedbackPlugin implements FeedbackPlugin {

    @Autowired Provider<RdfContextImporter> importerProvider;

    @Autowired RdfContextImporterFactoryBean importerFactory;

    @Autowired Provider<DogOntContextUpdater> updaterProvider;

    @Autowired DogOntContextUpdaterFactoryBean updaterFactory;

    @Autowired Provider<OpenHabMonitorAgent> monitorAgentProvider;

    @Autowired Provider<ProteusMonitorAgent> proteusMonitorAgentProvider;

    @Autowired SpelObjectiveEvaluatorFactoryBean evaluatorFactory;

    @Autowired Provider<SpelObjectiveEvaluator> evaluatorProvider;

    @Autowired DogOntCompensationRepositoryFactoryBean dogontCompensationRepositoryFactory;

    @Autowired ProteusCompensationRepositoryFactoryBean proteusCompensationRepositoryFactory;

    @Autowired Provider<DogOntCompensationRepository> dogOntCompensationRepositoryProvider;

    @Autowired Provider<ProteusCompensationRepository> proteusCompensationRepositoryProvider;

    @Autowired Provider<OpenHabCommandExecutor> commandExecutorProvider;

    @Override
    public ContextImporter getContextImporter(CypherExecutor executor) {
        importerFactory.setExecutor(executor);
        return importerProvider.get();
    }

    @Override
    public ContextUpdater getContextUpdater(CypherExecutor executor) {
        updaterFactory.setExecutor(executor);
        return updaterProvider.get();
    }

    @Override
    public ObjectiveEvaluator getObjectiveEvaluator(CypherExecutor executor) {
        evaluatorFactory.setExecutor(executor);
        return evaluatorProvider.get();
    }

    @Override
    public MismatchProvider getMismatchProvider() {
        return new SpelMismatchProvider();
    }

    @Override
    public Collection<MonitorAgent> getMonitorAgents() {
        return newArrayList(monitorAgentProvider.get(), proteusMonitorAgentProvider.get());
    }

    @Override
    public CommandExecutor getExecutor() {
        return commandExecutorProvider.get();
    }

    @Override
    public List<CompensationRepository> getCompensationRepositories(CypherExecutor executor) {
        dogontCompensationRepositoryFactory.setExecutor(executor);
        proteusCompensationRepositoryFactory.setExecutor(executor);
        return Arrays.asList(dogOntCompensationRepositoryProvider.get(), proteusCompensationRepositoryProvider.get());
    }

    public ProteusMonitorAgent getProteusMonitorAgent(){
        return proteusMonitorAgentProvider.get();
    }


}
