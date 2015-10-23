package de.tud.feedback.repository;

import de.tud.feedback.domain.Command;

import java.util.Collection;

public interface CommandRepository {

    Collection<Command> findCommandsManipulating(Long testNodeId);

}
