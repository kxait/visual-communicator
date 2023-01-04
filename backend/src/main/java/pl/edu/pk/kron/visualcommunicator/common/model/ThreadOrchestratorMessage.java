package pl.edu.pk.kron.visualcommunicator.common.model;

import java.util.UUID;

public record ThreadOrchestratorMessage(ThreadOrchestratorMessageType type, UUID clientId){}
