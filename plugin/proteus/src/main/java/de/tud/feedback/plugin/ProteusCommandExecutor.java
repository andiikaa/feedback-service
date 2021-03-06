package de.tud.feedback.plugin;


import de.tud.feedback.domain.Command;
import de.tud.feedback.loop.CommandExecutor;
import de.tud.feedback.plugin.domain.ProteusCommand;
import eu.vicci.process.client.ProcessEngineClientBuilder;
import eu.vicci.process.client.core.IProcessEngineClient;
import eu.vicci.process.model.util.messages.core.CompensationRequest;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.joda.time.DateTime.now;

public class ProteusCommandExecutor implements CommandExecutor{
    private static final Logger LOG = LoggerFactory.getLogger(ProteusCommandExecutor.class);

    private IProcessEngineClient client;
    private ProteusFeedbackPlugin plugin;

    public ProteusCommandExecutor(ProteusFeedbackPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Command command) {
        LOG.debug("Execute Proteus Compensation Command");
        if(!(command instanceof ProteusCommand)){
            String name = command == null ? "NULL" : command.getClass().getSimpleName();
            LOG.error("Cant execute command from type '{}'", name);
            return;
        }

        connectClient();
        command.setLastSendAt(now());
        client.publishCompensationRequest(createCompensationRequest((ProteusCommand)command));
        disconnectClient();
    }

    @Override
    public boolean supportsCommand(Command command) {
        return command instanceof ProteusCommand;
    }

    /**
     * Attention: We do not track disconnections. If we are not connected, we make a new connection.
     */
    private void connectClient(){
        if(client != null && client.isConnected())
            return;
        disconnectClient();

        ProteusMonitorAgent.ConnectSettings settings = plugin.getProteusMonitorAgent().getCurrentConnectionSettings();
        if(settings == null)
            throw new RuntimeException("No connection settings for proteus found. Cant compensate.");

        client = new ProcessEngineClientBuilder()
                .withIp(settings.ip)
                .withPort(settings.port)
                .withRealmName(settings.realm)
                .withNamespace(settings.namespace)
                .withName(ProteusCommandExecutor.class.getSimpleName())
                .build();
        client.connect();
    }

    private void disconnectClient(){
        if(client != null) client.close();
        client = null;
    }

    private CompensationRequest createCompensationRequest(ProteusCommand command){
        CompensationRequest r = new CompensationRequest();
        r.ip = command.getNewIp();
        r.newPeerId = command.getNewPeerId();
        r.processId = command.getProcessModelId();
        r.originalInstanceId = command.getOriginalInstanceId();
        return r;
    }


}
