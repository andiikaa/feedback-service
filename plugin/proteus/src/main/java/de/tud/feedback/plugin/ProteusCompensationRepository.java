package de.tud.feedback.plugin;


import de.tud.feedback.CypherExecutor;
import de.tud.feedback.domain.Command;
import de.tud.feedback.plugin.domain.ProteusCommand;
import de.tud.feedback.repository.CompensationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static de.tud.feedback.Utils.params;
import static java.util.stream.Collectors.toSet;

public class ProteusCompensationRepository implements CompensationRepository {
    private final CypherExecutor executor;
    private final String query;

    public ProteusCompensationRepository(CypherExecutor executor, String query) {
        this.executor = executor;
        this.query = query;
    }

    @Override
    public Set<Command> findCommandsManipulating(Long testNodeId) {
        return executor.execute(query, params()
                .put("processNodeId", testNodeId)
                .build())
                .stream()
                .map(this::toCommand)
                .collect(toSet());
    }

    private Command toCommand(Map<String, Object> attributes){
        Map<String, Object> original = (Map<String, Object>)attributes.get("original");
        Map<String, Object> newPeer = (Map<String, Object>)attributes.get("newPeer");

        String orgProcessId = (String)original.get("processId");
        String orgProcessName = (String)original.get("name");
        String processModelId = (String)original.get("processModelId");
        String ip = (String)newPeer.get("ip");
        String peerId = (String)newPeer.get("peerId");

        return new ProteusCommand()
                .setOriginalInstanceId(orgProcessId)
                .setProcessModelId(processModelId)
                .setNewIp(ip)
                .setNewPeerId(peerId)
                .setTargetTo(peerId + "_" + ip)
                .setNameTo("Compensate_" + orgProcessName + "_" + orgProcessId + "_OnNew_" + peerId)
                .setTypeTo(Command.Type.ASSIGN)
                .setRepeatable(false);
    }
}
