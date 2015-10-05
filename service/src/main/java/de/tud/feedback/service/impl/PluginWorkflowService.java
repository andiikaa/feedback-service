package de.tud.feedback.service.impl;

import de.tud.feedback.api.CypherExecutor;
import de.tud.feedback.domain.WorkflowInstance;
import de.tud.feedback.repository.WorkflowRepository;
import de.tud.feedback.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PluginWorkflowService implements WorkflowService {

    private WorkflowRepository workflows;

    private CypherExecutor executor;

    @Override
    public void attendExecutionOf(WorkflowInstance instance) {
        // TODO
    }

    @Autowired
    public void setWorkflowRepository(WorkflowRepository workflows) {
        this.workflows = workflows;
    }

    @Autowired
    public void setExecutorProvider(CypherExecutor executor) {
        this.executor = executor;
    }

}
