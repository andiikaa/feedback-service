package de.tud.feedback;

import de.tud.feedback.loop.MismatchProvider;
import de.tud.feedback.loop.MonitorAgent;
import de.tud.feedback.loop.ObjectiveEvaluator;
import de.tud.feedback.repository.CompensationRepository;

import java.util.Collection;

public interface FeedbackPlugin {

    ContextImporter getContextImporter(CypherExecutor executor);

    ContextUpdater getContextUpdater(CypherExecutor executor);

    ObjectiveEvaluator getObjectiveEvaluator(CypherExecutor executor);

    MismatchProvider getMismatchProvider();

    Collection<MonitorAgent> getMonitorAgents();

    CompensationRepository getCompensationRepository(CypherExecutor executor);

}
