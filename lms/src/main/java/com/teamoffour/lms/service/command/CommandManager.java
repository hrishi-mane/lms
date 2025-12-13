package com.teamoffour.lms.service.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Stack;

@Service
public class CommandManager {
    private static final Logger log = LoggerFactory.getLogger(CommandManager.class);

    private final Stack<ICommand> commandHistory = new Stack<>();
    private static final int MAX_HISTORY_SIZE = 50;

    public String executeCommand(ICommand command) {
        log.info("→ Executing command: {}", command.getDescription());

        String result = command.execute();

        commandHistory.push(command);

        if (commandHistory.size() > MAX_HISTORY_SIZE) {
            commandHistory.remove(0);
            log.debug("Command history trimmed to {} entries", MAX_HISTORY_SIZE);
        }

        log.info("✓ Command executed successfully. History size: {}",
                commandHistory.size());

        return result;
    }

    public String undoLastCommand() {
        if (commandHistory.isEmpty()) {
            log.warn("✗ Cannot undo: Command history is empty");
            return "Cannot undo: No commands to undo";
        }

        ICommand lastCommand = commandHistory.pop();

        log.info("↶ Undoing command: {}", lastCommand.getDescription());

        String result = lastCommand.undo();

        log.info("✓ Command undone successfully. Remaining in history: {}",
                commandHistory.size());

        return result;
    }

    public int getHistorySize() {
        return commandHistory.size();
    }

    public void clearHistory() {
        int size = commandHistory.size();
        commandHistory.clear();
        log.info("Command history cleared ({} commands removed)", size);
    }

    public boolean canUndo() {
        return !commandHistory.isEmpty();
    }

    public String getLastCommandDescription() {
        if (commandHistory.isEmpty()) {
            return null;
        }
        return commandHistory.peek().getDescription();
    }

    public String[] getCommandHistory() {
        return commandHistory.stream()
                .map(ICommand::getDescription)
                .toArray(String[]::new);
    }
}